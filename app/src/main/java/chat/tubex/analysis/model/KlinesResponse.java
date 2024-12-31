package chat.tubex.analysis.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KlinesResponse {
    private List<List<String>> klines; // 修改为 String 类型
    public List<List<String>> getKlines() {
        return klines;
    }

    public void setKlines(List<List<String>> klines) {
        this.klines = klines;
    }

    public static class NewsResponse {

        @SerializedName("code")
        private int code;

        @SerializedName("message")
        private String message;

        @SerializedName("data")
        private Data data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }


        public static class Data {
            @SerializedName("count")
            private int count;

            @SerializedName("items")
            private List<Item> items;


            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public List<Item> getItems() {
                return items;
            }

            public void setItems(List<Item> items) {
                this.items = items;
            }
        }

        public static class Item {

            @SerializedName("article")
            private Object article;

            @SerializedName("ban_comment")
            private boolean banComment;

            @SerializedName("channels")
            private List<String> channels;

            @SerializedName("content")
            private String content;


            @SerializedName("content_more")
            private String contentMore;

            @SerializedName("content_text")
            private String contentText;

            @SerializedName("cover_images")
            private List<String> coverImages;

            @SerializedName("display_time")
            private long displayTime;

            @SerializedName("global_channel_name")
            private String globalChannelName;

            @SerializedName("global_more_uri")
            private String globalMoreUri;

            @SerializedName("highlight_title")
            private String highlightTitle;

            @SerializedName("id")
            private int id;

            @SerializedName("images")
            private List<String> images;

            @SerializedName("is_calendar")
            private boolean isCalendar;

            @SerializedName("is_favourite")
            private boolean isFavourite;

            @SerializedName("is_priced")
            private boolean isPriced;

            @SerializedName("is_scaling")
            private boolean isScaling;

            @SerializedName("reference")
            private String reference;

            @SerializedName("related_themes")
            private Object relatedThemes;

            @SerializedName("score")
            private int score;

            @SerializedName("symbols")
            private List<Object> symbols;


            @SerializedName("tags")
            private List<String> tags;


            @SerializedName("title")
            private String title;

            @SerializedName("type")
            private String type;

            @SerializedName("uri")
            private String uri;

            public Object getArticle() {
                return article;
            }

            public void setArticle(Object article) {
                this.article = article;
            }

            public boolean isBanComment() {
                return banComment;
            }

            public void setBanComment(boolean banComment) {
                this.banComment = banComment;
            }

            public List<String> getChannels() {
                return channels;
            }

            public void setChannels(List<String> channels) {
                this.channels = channels;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getContentMore() {
                return contentMore;
            }

            public void setContentMore(String contentMore) {
                this.contentMore = contentMore;
            }

            public String getContentText() {
                return contentText;
            }

            public void setContentText(String contentText) {
                this.contentText = contentText;
            }

            public List<String> getCoverImages() {
                return coverImages;
            }

            public void setCoverImages(List<String> coverImages) {
                this.coverImages = coverImages;
            }

            public long getDisplayTime() {
                return displayTime;
            }

            public void setDisplayTime(long displayTime) {
                this.displayTime = displayTime;
            }

            public String getGlobalChannelName() {
                return globalChannelName;
            }

            public void setGlobalChannelName(String globalChannelName) {
                this.globalChannelName = globalChannelName;
            }

            public String getGlobalMoreUri() {
                return globalMoreUri;
            }

            public void setGlobalMoreUri(String globalMoreUri) {
                this.globalMoreUri = globalMoreUri;
            }

            public String getHighlightTitle() {
                return highlightTitle;
            }

            public void setHighlightTitle(String highlightTitle) {
                this.highlightTitle = highlightTitle;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public List<String> getImages() {
                return images;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }

            public boolean isCalendar() {
                return isCalendar;
            }

            public void setCalendar(boolean calendar) {
                isCalendar = calendar;
            }

            public boolean isFavourite() {
                return isFavourite;
            }

            public void setFavourite(boolean favourite) {
                isFavourite = favourite;
            }

            public boolean isPriced() {
                return isPriced;
            }

            public void setPriced(boolean priced) {
                isPriced = priced;
            }

            public boolean isScaling() {
                return isScaling;
            }

            public void setScaling(boolean scaling) {
                isScaling = scaling;
            }

            public String getReference() {
                return reference;
            }

            public void setReference(String reference) {
                this.reference = reference;
            }

            public Object getRelatedThemes() {
                return relatedThemes;
            }

            public void setRelatedThemes(Object relatedThemes) {
                this.relatedThemes = relatedThemes;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public List<Object> getSymbols() {
                return symbols;
            }

            public void setSymbols(List<Object> symbols) {
                this.symbols = symbols;
            }

            public List<String> getTags() {
                return tags;
            }

            public void setTags(List<String> tags) {
                this.tags = tags;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getUri() {
                return uri;
            }

            public void setUri(String uri) {
                this.uri = uri;
            }
        }
    }
}