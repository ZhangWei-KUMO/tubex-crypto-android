package chat.tubex.analysis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import chat.tubex.analysis.R;  // 假设你项目中的包名
import chat.tubex.analysis.WelcomeActivity; // 你的欢迎界面

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 延迟 2 秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //  启动欢迎界面
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();

        }, SPLASH_DISPLAY_LENGTH);
    }
}