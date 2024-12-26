package com.example.tubex.ui.home;

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
import com.example.tubex.databinding.FragmentHomeBinding;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements TopSearchAdapter.OnItemClickListener {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private TopSearchAdapter adapter;
    private HomeViewModel homeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // find recyclerview
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TopSearchAdapter(new ArrayList<>(), this); // 注意这里改为传入 this
        recyclerView.setAdapter(adapter);


        homeViewModel.getTopSearchList().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.setData(items);
            }
        });
        return root;
    }

    @Override
    public void onItemClick(TopSearchItem item) {
        // 处理点击事件
        if (item != null) {
            Toast.makeText(getContext(), "Clicked: " + item.getSymbol(), Toast.LENGTH_SHORT).show();
            // 在这里可以根据你的业务逻辑处理点击事件，例如启动另一个活动或者更新UI等
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}