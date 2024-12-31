package chat.tubex.analysis.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import chat.tubex.analysis.CryptoFeatureActivity;
import chat.tubex.analysis.databinding.FragmentHomeBinding;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements TopSearchAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private TopSearchAdapter adapter;
    private HomeViewModel homeViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // find recyclerview
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopSearchAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(this);

        observeData();
        return root;
    }
    private void observeData(){
        homeViewModel.getTopSearchList().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.setData(items);
            }
        });
        homeViewModel.isRefreshing().observe(getViewLifecycleOwner(), isRefreshing -> {
            swipeRefreshLayout.setRefreshing(isRefreshing);
        });

    }

    @Override
    public void onItemClick(TopSearchItem item) {
        // 创建隐式 Intent
        String symbol = item.getSymbol();
        Intent intent = new Intent(getContext(), CryptoFeatureActivity.class);
        intent.putExtra("symbol", symbol); // 添加额外数据
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        homeViewModel.fetchData(new HomeViewModel.DataFetchCallback() {
            @Override
            public void onSuccess() {
                // Data fetch successful
                Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String message) {
                // Handle data fetch failure
                Toast.makeText(getContext(), "刷新失败: " + message, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

