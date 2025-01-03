package chat.tubex.analysis.ui.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;

import java.util.List;

import chat.tubex.analysis.R;
import chat.tubex.analysis.model.NewsResponse;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<NewsResponse.Item> items;

    public NewsAdapter(List<NewsResponse.Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsResponse.Item item = items.get(position);
        if (!TextUtils.isEmpty(item.getTitle())) {
            String title = item.getTitle();
            if (title.contains("华尔街见闻")) {
                title = "币格私董会早报";
            }
            holder.titleTextView.setText(title);
            holder.titleTextView.setVisibility(View.VISIBLE); // 确保 titleTextView 是可见的
        } else {
            holder.titleTextView.setVisibility(View.GONE);    // 如果title为空，隐藏 titleTextView
        }
        holder.contentTextView.setText(item.getContentText());
        long timestampInSeconds = item.getDisplayTime();
        long timestampInMilliseconds = timestampInSeconds * 1000L; // 将秒转换为毫秒
        Date date = new Date(timestampInMilliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(date);
        holder.timeTextView.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<NewsResponse.Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}