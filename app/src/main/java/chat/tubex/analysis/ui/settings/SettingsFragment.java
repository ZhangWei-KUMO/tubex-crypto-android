package chat.tubex.analysis.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import chat.tubex.analysis.R;
import chat.tubex.analysis.ShareVideoActivity;
import chat.tubex.analysis.databinding.FragmentSettingsBinding;
import chat.tubex.analysis.AboutUsActivity;
import chat.tubex.analysis.HelpCenterActivity;
import chat.tubex.analysis.ShareActivity;
import chat.tubex.analysis.LoginActivity;
import chat.tubex.analysis.SubscribeActivity;
import chat.tubex.analysis.ResearchListActivity;
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        setupClickListeners();

        return root;
    }

    private void setupClickListeners() {
        // 获取关于按钮的LinearLayout
//        LinearLayout aboutLayout = binding.getRoot().findViewById(R.id.aboutLayout);
//        if(aboutLayout != null){
//            aboutLayout.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), AboutUsActivity.class);
//                startActivity(intent);
//            });
//        }

        // 获取帮助中心的LinearLayout
        LinearLayout helpCenterLayout = binding.getRoot().findViewById(R.id.helpCenterLayout);
        if(helpCenterLayout != null) {
            helpCenterLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), HelpCenterActivity.class);
                intent.putExtra("actionType", "helpCenter");
                startActivity(intent);
            });
        }
        // 运行环境
        LinearLayout runTimeLayout = binding.getRoot().findViewById(R.id.runTimeLayout);
        if(runTimeLayout != null) {
            runTimeLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), HelpCenterActivity.class);
                intent.putExtra("actionType", "runTime");
                startActivity(intent);
            });
        }
        // 隐私政策
        LinearLayout privacyPoliceLayout = binding.getRoot().findViewById(R.id.privacyPoliceLayout);
        if(privacyPoliceLayout != null) {
            privacyPoliceLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), HelpCenterActivity.class);
                intent.putExtra("actionType", "privacyPolicy");
                startActivity(intent);
            });
        }
        // 使用条款
        LinearLayout termsOfUseLayout = binding.getRoot().findViewById(R.id.termsOfUseLayout);
        if(termsOfUseLayout != null) {
            termsOfUseLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), HelpCenterActivity.class);
                intent.putExtra("actionType", "termsOfUse");
                startActivity(intent);
            });
        }

        // 获取推荐朋友的LinearLayout
//        LinearLayout shareLayout = binding.getRoot().findViewById(R.id.shareLayout);
//        if(shareLayout != null) {
//            shareLayout.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), ShareActivity.class);
//                startActivity(intent);
//            });
//        }

        // 获取订阅的LinearLayout
//        LinearLayout subscribeLayout = binding.getRoot().findViewById(R.id.subscribeLayout);
//        if(subscribeLayout != null) {
//            subscribeLayout.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), SubscribeActivity.class);
//                startActivity(intent);
//            });
//        }

        // 获取市场研报的LinearLayout
//        LinearLayout researchLayout = binding.getRoot().findViewById(R.id.researchLayout);
//        if(researchLayout != null) {
//            researchLayout.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), ResearchListActivity.class);
//                startActivity(intent);
//            });
//        }

        // 获取开发者模式的LinearLayout
//        LinearLayout developerLayout = binding.getRoot().findViewById(R.id.developerLayout);
//        if(developerLayout != null) {
//            developerLayout.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), ShareVideoActivity.class);
//                startActivity(intent);
//            });
//        }

        // 获取登录的LinearLayout
//        LinearLayout loginLayout = binding.getRoot().findViewById(R.id.loginLayout);
//        if(loginLayout != null) {
//            loginLayout.setOnClickListener(v -> {
//                Intent intent = new Intent(getContext(), LoginActivity.class);
//                startActivity(intent);
//            });
//        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}