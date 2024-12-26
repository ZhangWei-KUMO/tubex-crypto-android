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
import chat.tubex.analysis.databinding.FragmentSettingsBinding;
import chat.tubex.analysis.AboutUsActivity;
import chat.tubex.analysis.HelpCenterActivity;

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
        LinearLayout aboutLayout = binding.getRoot().findViewById(R.id.aboutLayout);
        if(aboutLayout != null){
            aboutLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AboutUsActivity.class);
                startActivity(intent);
            });
        }

        // 获取帮助中心的LinearLayout
        LinearLayout helpCenterLayout = binding.getRoot().findViewById(R.id.helpCenterLayout);
        if(helpCenterLayout != null) {
            helpCenterLayout.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), HelpCenterActivity.class);
                startActivity(intent);
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}