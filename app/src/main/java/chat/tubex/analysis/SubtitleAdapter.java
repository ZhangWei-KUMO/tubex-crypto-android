package chat.tubex.analysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.text.TextUtils;
import android.view.MotionEvent;
public class SubtitleAdapter extends RecyclerView.Adapter<SubtitleAdapter.ViewHolder> {

    private List<SubtitleItem> subtitles;

    public SubtitleAdapter(List<SubtitleItem> subtitles) {
        this.subtitles = subtitles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subtitle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.englishTextView.setLongClickable(true);
        holder.chineseTextView.setLongClickable(true);
        SubtitleItem subtitle = subtitles.get(position);
        holder.englishTextView.setText(subtitle.getEnglish());
        holder.chineseTextView.setText(subtitle.getChinese());
        // 添加以下代码
        holder.englishTextView.clearFocus();
        holder.chineseTextView.clearFocus();
        holder.englishTextView.setPressed(false);
        holder.chineseTextView.setPressed(false);

//        if (TextUtils.isEmpty(holder.englishTextView.getText())) {
//            holder.englishTextView.setEnabled(false);
//        }else {
//            holder.englishTextView.setEnabled(true);
//        }
//
//        if (TextUtils.isEmpty(holder.chineseTextView.getText())) {
//            holder.chineseTextView.setEnabled(false);
//        }else {
//            holder.chineseTextView.setEnabled(true);
//        }
    }

    @Override
    public int getItemCount() {
        return subtitles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView englishTextView;
        TextView chineseTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            englishTextView = itemView.findViewById(R.id.englishTextView);
            chineseTextView = itemView.findViewById(R.id.chineseTextView);
        }
    }
}