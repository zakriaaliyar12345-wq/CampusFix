package com.example.campusfix.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfix.R;
import com.example.campusfix.learning.Lesson;
import com.example.campusfix.ui.model.LessonRow;

/**
 * Learning Lab list adapter: unit headers plus lesson cards in one
 * RecyclerView, with DiffUtil keyed on {@link LessonRow#key} so marking a
 * lesson complete rebinds exactly one row (topic 04.5).
 */
public class LessonAdapter
        extends ListAdapter<LessonRow, RecyclerView.ViewHolder> {

    public interface OnLessonClickListener {
        void onLessonClick(@NonNull Lesson lesson);
    }

    private final OnLessonClickListener listener;

    public LessonAdapter(OnLessonClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<LessonRow> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<LessonRow>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull LessonRow oldItem,
                        @NonNull LessonRow newItem) {
                    return oldItem.key.equals(newItem.key);
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull LessonRow oldItem,
                        @NonNull LessonRow newItem) {
                    return oldItem.sameContent(newItem);
                }
            };

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == LessonRow.TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(
                    R.layout.item_unit_header, parent, false));
        }
        return new LessonViewHolder(inflater.inflate(
                R.layout.item_lesson, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        LessonRow row = getItem(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(row);
        } else if (holder instanceof LessonViewHolder) {
            ((LessonViewHolder) holder).bind(row);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvUnitTitle);
        }

        void bind(LessonRow row) {
            title.setText(row.headerTitle);
        }
    }

    class LessonViewHolder extends RecyclerView.ViewHolder {

        private final TextView id;
        private final TextView title;
        private final TextView summary;
        private final TextView badgeCompleted;
        private final TextView badgeScore;
        private final TextView badgeBookmark;

        LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tvLessonId);
            title = itemView.findViewById(R.id.tvLessonTitle);
            summary = itemView.findViewById(R.id.tvLessonSummary);
            badgeCompleted = itemView.findViewById(R.id.tvLessonBadgeCompleted);
            badgeScore = itemView.findViewById(R.id.tvLessonBadgeScore);
            badgeBookmark = itemView.findViewById(R.id.tvLessonBadgeBookmark);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION
                        && listener != null) {
                    LessonRow row = getItem(position);
                    if (row.lesson != null) {
                        listener.onLessonClick(row.lesson);
                    }
                }
            });
        }

        void bind(LessonRow row) {
            Lesson lesson = row.lesson;
            if (lesson == null) return;

            android.content.Context context = itemView.getContext();

            id.setText(lesson.id);
            title.setText(lesson.title);
            summary.setText(lesson.summary);

            badgeCompleted.setVisibility(
                    row.completed ? View.VISIBLE : View.GONE);
            badgeBookmark.setVisibility(
                    row.bookmarked ? View.VISIBLE : View.GONE);

            if (row.bestQuizScore >= 0) {
                badgeScore.setVisibility(View.VISIBLE);
                badgeScore.setText(context.getString(
                        R.string.learning_best_score,
                        row.bestQuizScore, lesson.quiz.size()));
            } else {
                badgeScore.setVisibility(View.GONE);
            }

            int tint = ContextCompat.getColor(context,
                    row.completed ? R.color.status_resolved : R.color.primary);
            badgeCompleted.setTextColor(tint);
            badgeScore.setTextColor(ContextCompat.getColor(
                    context, R.color.secondary));
        }
    }
}
