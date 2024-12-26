package com.example.tubex.ui.coin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tubex.R;

public class CoinFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin, container, false);

        Bundle args = getArguments();
        if (args != null) {
            String symbol = args.getString("symbol");

            TextView symbolTextView = view.findViewById(R.id.text_coin); // 获取布局中的TextView

            if (symbolTextView != null) {
                symbolTextView.setText(symbol);  // 将 symbol 显示到TextView中
            }

        }
        return view;
    }
}