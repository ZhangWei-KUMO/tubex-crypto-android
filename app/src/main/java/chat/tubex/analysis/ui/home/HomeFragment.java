package chat.tubex.analysis.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import chat.tubex.analysis.CryptoFeatureActivity;
import chat.tubex.analysis.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements TopSearchAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private TopSearchAdapter adapter;
    private HomeViewModel homeViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private List<TopSearchItem> originalData = new ArrayList<>(); // 保存原始数据


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopSearchAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(this);
        searchView = binding.searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });
        //设置SearchView的点击事件监听器
//        searchView.setOnClickListener(v -> {
//            searchView.setIconified(false);//强制展开searchview
//        });


        observeData();
        return root;
    }

    private void observeData() {
        homeViewModel.getTopSearchList().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                originalData.clear();
                originalData.addAll(items);
                adapter.setData(items);
            }
        });
        homeViewModel.isRefreshing().observe(getViewLifecycleOwner(), isRefreshing -> {
            swipeRefreshLayout.setRefreshing(isRefreshing);
        });
    }

    private void filterData(String query) {
        List<TopSearchItem> filterData = originalData.stream().filter(item -> {
            return item.getSymbol().toLowerCase().contains(query.toLowerCase());
        }).collect(Collectors.toList());
        adapter.setData(filterData);
    }

    @Override
    public void onItemClick(TopSearchItem item) {
        String symbol = item.getSymbol();
        Intent intent = new Intent(getContext(), CryptoFeatureActivity.class);
        intent.putExtra("symbol", symbol);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        homeViewModel.fetchData(new HomeViewModel.DataFetchCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(String message) {
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