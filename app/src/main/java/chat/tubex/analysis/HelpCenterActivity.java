package chat.tubex.analysis;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import chat.tubex.analysis.R;

import io.noties.markwon.Markwon;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelpCenterActivity extends AppCompatActivity {

    private TextView helpCenterContentTextView;
    private Markwon markwon;
    private ExecutorService executorService;
    private TextView backButtonText;
    private String actionType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);
        actionType = getIntent().getStringExtra("actionType");

        helpCenterContentTextView = findViewById(R.id.help_center_content);
        backButtonText = findViewById(R.id.backButtonText);
        markwon = Markwon.builder(this).build();
        // 初始化线程池
        executorService = Executors.newFixedThreadPool(2);
        loadAndRenderMarkdown();

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadAndRenderMarkdown() {
        executorService.execute(() -> {
            String markdownText = readMarkdownFile();
            runOnUiThread(() -> {
                if (markdownText != null) {
                    markwon.setMarkdown(helpCenterContentTextView, markdownText);
                    setBackButtonText();
                } else {
                    runOnUiThread(() -> {
                        helpCenterContentTextView.setText("加载内容失败");
                        setBackButtonText();
                    });
                }

            });
        });
    }

    private void setBackButtonText(){
        String title;
        if ("helpCenter".equals(actionType)) {
            title = "帮助中心"; // 根据 actionType 设置标题
        } else if ("runTime".equals(actionType)) {
            title = "运行环境";
        }
        else if ("privacyPolicy".equals(actionType)) {
            title = "隐私政策";
        }
        else if ("termsOfUse".equals(actionType)) {
            title = "使用条款";
        }
        else {
            title = "未知选项";
        }
        backButtonText.setText(title);
    }
    private String readMarkdownFile() {
        int rawResId;
        if ("helpCenter".equals(actionType)) {
            rawResId = R.raw.help_center;
        } else if ("runTime".equals(actionType)) {
            rawResId = R.raw.run_time;
        }
        else if ("privacyPolicy".equals(actionType)) {
            rawResId = R.raw.privacy_policy;
        }
        else if("termsOfUse".equals(actionType)) {
            rawResId = R.raw.terms_of_use;
        }
        else {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        try {
            InputStream inputStream = getResources().openRawResource(rawResId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        return builder.toString();
    }
}