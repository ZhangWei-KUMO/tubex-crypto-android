package chat.tubex.analysis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.widget.ProgressBar;

public class ShareVideoActivity extends AppCompatActivity {
    private ProgressBar subtitleLoading;

    private RecyclerView subtitleRecyclerView;
    private SubtitleAdapter subtitleAdapter;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
//    private WebView videoWebView;
    private static final String TAG = "ShareVideoActivity";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_video);

        subtitleRecyclerView = findViewById(R.id.subtitleRecyclerView);
        subtitleRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取 WebView 实例
//        if(videoWebView == null){
//            Log.e(TAG,"WebView 为null, 请检查布局文件");
//            return;
//        }

        // WebView设置
//        WebSettings webSettings = videoWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true); // 启用 JavaScript
//        webSettings.setDomStorageEnabled(true); // 启用 DOM storage
//        videoWebView.setWebChromeClient(new WebChromeClient()); // 处理弹窗等
        subtitleLoading = findViewById(R.id.subtitleLoading);

        Log.d(TAG,"onCreate: WebView 初始化完成");

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
            handleSendText(intent);
        }
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            String videoId = extractVideoId(sharedText);
            if (videoId != null) {
//                loadVideo(videoId);
                loadSubtitle(videoId);
            }
        }
    }

    private String extractVideoId(String url) {
        Uri uri = Uri.parse(url);
        String videoId = uri.getQueryParameter("v");
        if(videoId == null){ // 尝试解析短链接
            String path = uri.getPath();
            if(path != null && path.startsWith("/")){
                videoId = path.substring(1);
            }
        }
        Log.d(TAG, "extractVideoId: 提取的videoId: " + videoId);
        return videoId;
    }


    // 加载视频的方法
//    private void loadVideo(String videoId) {
//        String videoUrl = "https://www.youtube.com/embed/" + videoId;
//        Log.d(TAG, "loadVideo: 加载的 videoUrl: " + videoUrl);
//        try {
//            videoWebView.loadUrl(videoUrl);
//        }catch (Exception e){
//            Log.e(TAG,"loadVideo: 加载视频出错",e);
//        }
//
//    }

    private void loadSubtitle(String videoId) {
        subtitleLoading.setVisibility(View.VISIBLE);

        SubtitleFetcher fetcher = new SubtitleFetcher();
        executorService.submit(() -> {
            try {
                fetcher.fetchSubtitles(videoId).thenAccept(subtitleItems -> {
                    runOnUiThread(() -> {
                        subtitleLoading.setVisibility(View.GONE); //隐藏
                        if (subtitleItems != null) {
                            Log.d(TAG,"loadSubtitle: 加载到字幕");
                            subtitleAdapter = new SubtitleAdapter(subtitleItems);
                            subtitleRecyclerView.setAdapter(subtitleAdapter);
                            subtitleRecyclerView.setVisibility(View.VISIBLE); //显示

                        }else{
                            Log.d(TAG,"loadSubtitle: 获取到的字幕为空");
                        }
                    });
                });
            }catch (Exception e){
                Log.e(TAG,"loadSubtitle: 加载字幕出错",e);
                runOnUiThread(() -> {
                    subtitleLoading.setVisibility(View.GONE); //隐藏
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        executorService.shutdown();
    }
}