// 声明一个coin包，方便外界引用
package chat.tubex.analysis.ui.coin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import chat.tubex.analysis.R;


public class CoinFragment extends Fragment {

    public CoinFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin, container, false);
        // 获取传递的数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            String coinSymbol = bundle.getString("coinSymbol");
            TextView coinSymbolTextView = view.findViewById(R.id.coinSymbolTextView);
            if (coinSymbolTextView != null && coinSymbol != null) {
                coinSymbolTextView.setText("Coin Symbol: " + coinSymbol);
            }
        }
        return view;
    }
}