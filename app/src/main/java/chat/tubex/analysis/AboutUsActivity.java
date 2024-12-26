package chat.tubex.analysis;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import chat.tubex.analysis.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // 关闭 Activity
        });
    }
}