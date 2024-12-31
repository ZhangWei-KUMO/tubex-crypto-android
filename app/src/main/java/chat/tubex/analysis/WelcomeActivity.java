package chat.tubex.analysis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private Button startButton;
    private int pageCount = 3;
    private boolean isFirstTime;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_FIRST_TIME = "isFirstTime";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        startButton = findViewById(R.id.startButton);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isFirstTime = prefs.getBoolean(KEY_FIRST_TIME, true); // 生产环境使用
//        isFirstTime = true; // 开发期间使用


        if (!isFirstTime) {
            startMainActivity();
            return;
        }


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(WelcomeFragment.newInstance(R.drawable.first,"专业","专业的机器学习模型和统计数据分析"));
        fragments.add(WelcomeFragment.newInstance(R.drawable.second,"及时","实时推送经济新闻"));
        fragments.add(WelcomeFragment.newInstance(R.drawable.third,"专注","市场热门的才是你需要的"));
        viewPager.setAdapter(new ViewPagerAdapter(this,fragments));
        createIndicators();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
                if(position == (pageCount -1 )){
                    startButton.setVisibility(View.VISIBLE);
                }else{
                    startButton.setVisibility(View.GONE);
                }
            }
        });

        startButton.setOnClickListener(v->{
            markFirstTime();
            startMainActivity();

        });


    }
    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    private void markFirstTime(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_TIME, false).apply();
    }



    private void createIndicators() {
        for (int i = 0; i < pageCount; i++) {
            TextView indicator = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    20, 5
            );
            params.setMargins(10, 0, 10, 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(R.drawable.indicator_inactive_dot);
            indicatorLayout.addView(indicator);
        }
        if (indicatorLayout.getChildCount() > 0) {
            updateIndicators(0);
        }
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            TextView indicator = (TextView) indicatorLayout.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundResource(R.drawable.indicator_active_dot);
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_inactive_dot);
            }
        }
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private  List<Fragment> fragments;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }


        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}