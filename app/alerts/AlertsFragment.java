package chat.tubex.analysis.ui.alerts.alerts;

import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

import chat.tubex.analysis.databinding.FragmentNewsListBinding;

public class AlertsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener , AlertsViewModel.DataFetchCallback{
    private FragmentNewsListBinding binding; // 绑定XML静态资源
    private AlertsAdapter adapter;
    private AlertsViewModel newsViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView newsRecyclerView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        newsViewModel = new ViewModelProvider(this).get(AlertsViewModel.class);
        binding = FragmentNewsListBinding.inflate(inflater, container, false); // 绑定 fragment_news_list.xml
        View root = binding.getRoot();

        // 初始化 RecyclerView
        newsRecyclerView = binding.newsRecyclerView;
        adapter = new AlertsAdapter(new ArrayList<>()); // 初始化 adapter
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsRecyclerView.setAdapter(adapter);

        // 监听下拉刷新事件
        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(this);
        observeData();
        return root;
    }

    private void observeData() {
        // 观察新闻列表数据变化
        newsViewModel.getNewsList().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.setItems(items); // 更新 adapter 数据
            }
        });

        // 观察刷新状态
        newsViewModel.isRefreshing().observe(getViewLifecycleOwner(), this::setRefreshing);
    }

    private void setRefreshing(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }


    @Override
    public void onRefresh() {
        newsViewModel.fetchData(this);
    }
    @Override
    public void onSuccess() {
        // Data fetch successful
        Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
        setRefreshing(false);
    }

    @Override
    public void onFailure(String message) {
        // Handle data fetch failure
        Toast.makeText(getContext(), "刷新失败: " + message, Toast.LENGTH_SHORT).show();
        Log.e("NewsFragment", "Data fetch failed: " + message); // Log 错误信息
        setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}