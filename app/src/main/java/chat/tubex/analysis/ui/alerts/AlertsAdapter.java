package chat.tubex.analysis.ui.alerts;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import chat.tubex.analysis.R;
import chat.tubex.analysis.model.AlertsResponse;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.ViewHolder> {

    private List<AlertsResponse.Article> items;

    public AlertsAdapter(List<AlertsResponse.Article> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertsResponse.Article item = items.get(position);
        if (item != null) {
            if (!TextUtils.isEmpty(item.getTitle())) {
                holder.titleTextView.setText(item.getTitle());
                holder.titleTextView.setVisibility(View.VISIBLE); // 确保 titleTextView 是可见的
            } else {
                holder.titleTextView.setVisibility(View.GONE);    // 如果title为空，隐藏 titleTextView
            }
            long timestampInMilliseconds = item.getReleaseDate();
            Date date = new Date(timestampInMilliseconds);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(date);
            holder.timeTextView.setText(formattedDate);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    // 修改这里，添加 setArticles 方法
    public void setArticles(List<AlertsResponse.Article> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}