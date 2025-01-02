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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        helpCenterContentTextView = findViewById(R.id.help_center_content);
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
                }
            });
        });
    }


    private String readMarkdownFile() {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.help_content); // R.raw.help_content
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