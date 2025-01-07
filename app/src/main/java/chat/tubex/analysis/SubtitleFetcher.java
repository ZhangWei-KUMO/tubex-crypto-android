package chat.tubex.analysis;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
public class SubtitleFetcher {

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final int MAX_CHUNK_LENGTH = 10000;

    public CompletableFuture<List<SubtitleItem>> fetchSubtitles(String videoId) {
        CompletableFuture<List<SubtitleItem>> future = new CompletableFuture<>();
        getVideoInfo(videoId).thenCompose(this::parseXml)
                .thenCompose(this::extractRawText)
                .thenCompose(this::translateText)
                .thenApply(rawAndTranslated -> {
                    List<SubtitleItem> items = formatSubtitles(rawAndTranslated.first, rawAndTranslated.second);
                    future.complete(items);
                    return null;
                })
                .exceptionally(e -> {
                    Log.e("SubtitleFetcher", "Error fetching subtitles", e);
                    future.complete(null);
                    return null;
                });

        return future;
    }

    // 默认videoId fq6jDxJk1LM
    private CompletableFuture<String> getVideoInfo(String videoId) {
        String defaultVideoId = "fq6jDxJk1LM"; // 设置默认的 videoId
        if (videoId == null || videoId.trim().isEmpty()) {
            videoId = defaultVideoId;
        }
        String videoPageUrl = "https://www.youtube.com/watch?v=" + videoId;
        return fetchContent(videoPageUrl)
                .thenApply(videoPageHtml -> {
                    if (videoPageHtml == null) {
                        Log.e("SubtitleFetcher", "Failed to fetch video page html.");
                        return null;
                    }

                    String[] splittedHtml = videoPageHtml.split("\"captions\":");
                    if (splittedHtml.length < 2) {
                        Log.e("SubtitleFetcher", "无法找到字幕信息");
                        return null;
                    }

                    String jsonPart = splittedHtml[1].split(",\"videoDetails")[0].replace("\n", "");
                    JsonObject playerCaptionsTracklistRenderer = gson.fromJson(jsonPart, JsonObject.class)
                            .getAsJsonObject("playerCaptionsTracklistRenderer");
                    if (playerCaptionsTracklistRenderer == null) {
                        Log.e("SubtitleFetcher", "playerCaptionsTracklistRenderer is null.");
                        return null;
                    }
                    com.google.gson.JsonArray captionTracks = playerCaptionsTracklistRenderer.getAsJsonArray("captionTracks");
                    if (captionTracks == null || captionTracks.size() == 0) {
                        Log.e("SubtitleFetcher", "没有找到可用的英文字幕");
                        return null;
                    }

                    String englishSubUrl = null;
                    for (int i = 0; i < captionTracks.size(); i++) {
                        JsonObject track = captionTracks.get(i).getAsJsonObject();
                        if (track.get("languageCode").getAsString().equals("en")) {
                            englishSubUrl = track.get("baseUrl").getAsString();
                            break;
                        }
                    }


                    if (englishSubUrl == null) {
                        Log.e("SubtitleFetcher", "没有找到可用的英文字幕");
                        return null;
                    }
                    return englishSubUrl;
                });
    }

    private CompletableFuture<List<SubtitleCue>> parseXml(String xmlUrl) {
        return fetchContent(xmlUrl).thenApply(xmlData -> {
            if (xmlData == null) {
                Log.e("SubtitleFetcher", "Failed to fetch xml data.");
                return null;
            }
            return parseXmlContent(xmlData);
        });
    }

    private List<SubtitleCue> parseXmlContent(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));
            int eventType = parser.getEventType();
            List<SubtitleCue> cues = new ArrayList<>();
            String currentText = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("text")) {
                        currentText = parser.nextText().replaceAll("[\\n\\r]+", " ");
                        cues.add(new SubtitleCue(currentText)); // 创建 SubtitleCue 对象
                    }
                }
                eventType = parser.next();
            }
            return cues;
        } catch (Exception e) {
            Log.e("SubtitleFetcher", "XML parsing error: " + e.getMessage());
            return null;
        }
    }


    private CompletableFuture<String> extractRawText(List<SubtitleCue> cues) {
        return CompletableFuture.completedFuture(cues == null ? "" : cues.stream()
                .map(SubtitleCue::getText)
                .reduce("", (a, b) -> a + " " + b).trim());

    }


    private CompletableFuture<Pair<String, String>> translateText(String rawContent) {
        List<String> chunks = chunkString(rawContent, MAX_CHUNK_LENGTH);
        CompletableFuture<Pair<String,String>> future = new CompletableFuture<>();
        StringBuilder translatedText = new StringBuilder();
        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);


        for (String chunk : chunks) {
            chain = chain.thenCompose(v -> requestGoogle(chunk))
                    .thenAccept(result -> {
                        if(result.startsWith("error:")) {
                            future.complete(new Pair<>("","error: translation error"));
                            return;
                        }
                        translatedText.append(result);
                    });
        }
        chain.thenRun(() -> future.complete(new Pair<>(rawContent,translatedText.toString())));
        return future;
    }


    private List<String> chunkString(String str, int size) {
        List<String> chunks = new ArrayList<>();
        int numChunks = (int) Math.ceil((double) str.length() / size);
        for (int i = 0; i < numChunks; i++) {
            int start = i * size;
            int end = Math.min(start + size, str.length());
            chunks.add(str.substring(start, end));
        }
        return chunks;
    }

    private CompletableFuture<String> requestGoogle(String rawContent) {
        String from = "en";
        String lang = "zh-CN";
        if (from.equals(lang)) {
            return CompletableFuture.completedFuture("");
        }
        String googleTranslate = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + from + "&tl=" + lang + "&hl=" + lang + "&dt=t&dj=1&source=icon&tk=946553.946553&q=";
        try {
            return fetchContent(googleTranslate + URLEncoder.encode(rawContent, "UTF-8"))
                    .thenApply(it -> {
                        if(it == null) {
                            return "error:Google Translate request failed";
                        }
                        try{
                            JsonObject data = gson.fromJson(it, JsonObject.class);
                            StringBuilder str = new StringBuilder();
                            data.getAsJsonArray("sentences").forEach(element -> {
                                str.append(element.getAsJsonObject().get("trans").getAsString());
                            });
                            return str.toString();
                        }catch (Exception e){
                            Log.e("SubtitleFetcher", "Google translation json parse error: " + e.getMessage());
                            return "error:Google Translate json parse error";
                        }
                    });
        } catch (Exception e) {
            Log.e("SubtitleFetcher", "Google translation error:" + e.getMessage());
            return CompletableFuture.completedFuture("error:" + e.getMessage());
        }
    }


    private CompletableFuture<String> fetchContent(String url) {
        CompletableFuture<String> future = new CompletableFuture<>();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SubtitleFetcher", "Failed to fetch content from " + url + ": " + e.getMessage());
                future.complete(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String body = response.body().string();
                    future.complete(body);
                } else {
                    Log.e("SubtitleFetcher", "Failed to fetch content from " + url + ": " + response.code());
                    future.complete(null);
                }
            }
        });
        return future;
    }

    private  List<SubtitleItem> formatSubtitles(String rawContent, String translatedText) {
        String[] englishSentences = rawContent.split("(?<!\\b\\w\\.)(?<=[.?!])\\s+");
        String[] chineseSentences = translatedText.split("(?<=[。？！])\\s*");
        List<SubtitleItem> filteredSentences = new ArrayList<>();
        for (int i = 0; i < englishSentences.length; i++) {
            String englishSentence = englishSentences[i].trim();
            if(englishSentence.isEmpty()) continue;

            Spanned spannedText;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // API Level 24 及以上使用新模式
                spannedText = Html.fromHtml(englishSentence, Html.FROM_HTML_MODE_LEGACY);
            } else {
                // API Level 24 之前使用旧模式
                spannedText = Html.fromHtml(englishSentence);
            }
            englishSentence = spannedText.toString();

            String chineseSentence = i < chineseSentences.length ? chineseSentences[i].trim() : "";
            if (chineseSentence.length() > 10) {
                filteredSentences.add(new SubtitleItem(englishSentence, chineseSentence));
            }
        }
        return filteredSentences;
    }
}

