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
    private static final String TRANSLATION_FAILED = "翻译失败";

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
                String rawTitle = item.getTitle();
                holder.titleTextView.setText(rawTitle);
                holder.titleTextView.setVisibility(View.VISIBLE);
            } else {
                holder.titleTextView.setVisibility(View.GONE);
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