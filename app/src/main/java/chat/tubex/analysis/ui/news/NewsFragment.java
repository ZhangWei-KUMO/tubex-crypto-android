package chat.tubex.analysis.ui.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import chat.tubex.analysis.databinding.FragmentNewsListBinding;
import chat.tubex.analysis.model.NewsResponse;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, NewsViewModel.DataFetchCallback {
    private FragmentNewsListBinding binding;
    private NewsAdapter adapter;
    private NewsViewModel newsViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView newsRecyclerView;
    private TextView loadingStatusTextView;
    private Button retryButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        binding = FragmentNewsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        newsRecyclerView = binding.newsRecyclerView;
        adapter = new NewsAdapter(new ArrayList<>());
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsRecyclerView.setAdapter(adapter);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setOnRefreshListener(this);

        loadingStatusTextView = binding.loadingStatusTextView;
        retryButton = binding.retryButton;
        retryButton.setOnClickListener(v -> onRefresh());


        observeData();
        return root;
    }

    private void observeData() {
        // 观察加载状态
        newsViewModel.isRefreshing().observe(getViewLifecycleOwner(), this::setRefreshing);

        newsViewModel.getNewsList().observe(getViewLifecycleOwner(), items -> {

            if (items == null) {
                showErrorUI("加载失败, 请重试");
                return;
            }

            if (items.isEmpty()) {
                showEmptyDataUI();
            } else {
                adapter.setItems(items);
                showDataUI();
            }

        });
    }
    private void showEmptyDataUI() {
        loadingStatusTextView.setVisibility(View.VISIBLE);
        loadingStatusTextView.setText("暂无数据");
        retryButton.setVisibility(View.GONE);
        newsRecyclerView.setVisibility(View.GONE);
    }
    private void showDataUI() {
        loadingStatusTextView.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        newsRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorUI(String message) {
        loadingStatusTextView.setVisibility(View.VISIBLE);
        loadingStatusTextView.setText(message);
        retryButton.setVisibility(View.VISIBLE);
        newsRecyclerView.setVisibility(View.GONE);
    }
    private void setRefreshing(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
        if (isRefreshing) {
            loadingStatusTextView.setVisibility(View.VISIBLE);
            loadingStatusTextView.setText("加载中...");
            retryButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        newsViewModel.fetchData(this);
    }

    @Override
    public void onSuccess() {
        setRefreshing(false);
    }

    @Override
    public void onFailure(String message) {
        showErrorUI("加载失败,请重试: " + message);
        Log.e("NewsFragment", "Data fetch failed: " + message);
        setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}