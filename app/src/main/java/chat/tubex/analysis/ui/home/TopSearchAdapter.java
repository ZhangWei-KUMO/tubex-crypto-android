package chat.tubex.analysis.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import chat.tubex.analysis.R;
import java.util.List;
import java.text.DecimalFormat;

public class TopSearchAdapter extends RecyclerView.Adapter<TopSearchAdapter.ViewHolder> {

    private List<TopSearchItem> data;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private OnItemClickListener onItemClickListener;

    public TopSearchAdapter(List<TopSearchItem> topSearchItemList, OnItemClickListener onItemClickListener) {
        this.data = topSearchItemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopSearchItem item = data.get(position);
        holder.symbolTextView.setText(item.getSymbol());
        holder.rankTextView.setText(String.valueOf(item.getRank()));

        String formattedRatio = decimalFormat.format(item.getRatio()*100)+"%";
        holder.ratioTextView.setText(formattedRatio);

        // 解析波动率数值并根据正负设置颜色
        try {
            double ratio = item.getRatio();
            int backgroundColor;
            if (ratio > 0) {
                backgroundColor = Color.parseColor("#4CAF50"); // 绿色，你可以根据需要修改
            } else {
                backgroundColor = Color.parseColor("#F44336"); // 红色，你可以根据需要修改
            }
            holder.ratioTextView.setBackgroundColor(backgroundColor);
            holder.ratioTextView.setTextColor(Color.WHITE); // 设置字体颜色为白色

        } catch (NumberFormatException e) {
            // 处理解析错误
            holder.ratioTextView.setBackgroundColor(Color.GRAY); // 默认灰色
            holder.ratioTextView.setTextColor(Color.WHITE); // 设置字体颜色为白色
        }


        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<TopSearchItem> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(TopSearchItem item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView;
        TextView rankTextView;
        TextView ratioTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            ratioTextView = itemView.findViewById(R.id.ratioTextView);
        }
    }
}