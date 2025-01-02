package chat.tubex.analysis;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;


public class ResearchListActivity extends AppCompatActivity {

    private AppCompatImageButton backButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseach); // 确认你的布局文件名称

        // 设置状态栏和导航栏为黑色
        setSystemBarsColor();

        backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                onBackPressed();
            });
        } else {
            //handle exception
        }

    }

    private void setSystemBarsColor() {
        Window window = getWindow();
        //如果系统版本高于 Android L (API 21)，使用以下方式设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // 设置状态栏为黑色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

            //设置导航栏为黑色
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

            //设置状态栏字体为白色(api>=23,  API<23 需要调整布局)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~uiOptions);

                //设置导航栏字体为白色 (API>=26, API < 26需要调整布局)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                }
            }
        }
    }
}