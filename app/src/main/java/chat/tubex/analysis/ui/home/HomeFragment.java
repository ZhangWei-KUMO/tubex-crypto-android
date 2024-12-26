package chat.tubex.analysis.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import chat.tubex.analysis.R;
import chat.tubex.analysis.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import chat.tubex.analysis.ui.coin.CoinFragment;

public class HomeFragment extends Fragment implements TopSearchAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private TopSearchAdapter adapter;
    private HomeViewModel homeViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViewModel();
        initRecyclerView();
        observeData();

        return root;
    }

    private void initViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void initRecyclerView() {
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopSearchAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void observeData() {
        homeViewModel.getTopSearchList().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.setData(items);
            }
        });
    }
    @Override
    public void onItemClick(TopSearchItem item) {
        if (item != null) {
            navigateToCoinFragment(item);
        }
    }

    private void navigateToCoinFragment(TopSearchItem item) {
        CoinFragment coinFragment = new CoinFragment();
        Bundle bundle = new Bundle();
        bundle.putString("coinSymbol", item.getSymbol());
        coinFragment.setArguments(bundle);


        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, coinFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}