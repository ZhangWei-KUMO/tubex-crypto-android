package com.example.tubex.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tubex.R;
import java.util.List;
import java.text.DecimalFormat;

public class TopSearchAdapter extends RecyclerView.Adapter<TopSearchAdapter.ViewHolder> {

    private List<TopSearchItem> data;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##"); // 格式化为保留小数点后9位的小数
    public TopSearchAdapter(List<TopSearchItem> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_search, parent, false); // 注意这里使用了新的item_top_search布局
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopSearchItem item = data.get(position);
        holder.symbolTextView.setText(item.getSymbol());
        holder.rankTextView.setText(String.valueOf(item.getRank()));
        holder.ratioTextView.setText(decimalFormat.format(item.getRatio())); // 正确的 TextView
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<TopSearchItem> newData){
        this.data = newData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView;
        TextView rankTextView;
        TextView ratioTextView; // 修改为 ratioTextView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            ratioTextView = itemView.findViewById(R.id.ratioTextView); // 修改为 ratioTextView
        }
    }
}