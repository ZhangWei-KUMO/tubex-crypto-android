package chat.tubex.analysis;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity implements AboutUsAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AboutUsAdapter adapter;
    private WebView webView; // WebView field

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        webView = findViewById(R.id.webView);  // Initialize WebView
        webView.setWebViewClient(new WebViewClient()); // ensure the pages load in the webview

        List<String> aboutItems = new ArrayList<>();
        aboutItems.add("运行环境");
        aboutItems.add("隐私政策");
        aboutItems.add("使用条款");
        aboutItems.add("Cookies政策");
        aboutItems.add("评论区规则");
        aboutItems.add("许可");

        adapter = new AboutUsAdapter(aboutItems, this);
        recyclerView.setAdapter(adapter);

        TextView versionTextView = findViewById(R.id.versionTextView);
        String versionName = "1.0.0.0.1001555 build 1001555";
        versionTextView.setText("版本号 " + versionName);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // Close the Activity
        });
    }


    @Override
    public void onItemClick(String item) {
        String url = null;
        switch (item) {
            case "运行环境":
                url = "https://tubex.chat";
                break;
            case "隐私政策":
                url = "https://tubex.chat";
                break;
            case "使用条款":
                url = "https://tubex.chat";
                break;
            case "Cookies政策":
                url = "https://tubex.chat";
                break;
            case "评论区规则":
                url = "https://tubex.chat";
                break;
            case "许可":
                url = "https://tubex.chat";
                break;
        }

        if (url != null) {
            recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            webView.setVisibility(View.VISIBLE);   // Show WebView
            webView.loadUrl(url);                 // Load URL into WebView
        }
    }
    @Override
    public void onBackPressed() {
        if(webView.getVisibility()==View.VISIBLE){
            webView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else{
            super.onBackPressed();
        }

    }
}