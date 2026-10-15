package com.example.campusfix.ui.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.util.DateUtils;

import java.text.DateFormat;

/**
 * Report list adapter built on ListAdapter + DiffUtil so the RecyclerView
 * receives efficient, animated updates instead of notifyDataSetChanged()
 * (Learning Lab topic 04.5).
 */
public class ReportAdapter
        extends ListAdapter<ReportEntity, ReportAdapter.ReportViewHolder> {

    public interface OnReportClickListener {
        void onReportClick(ReportEntity report);
    }

    private final OnReportClickListener listener;
    private final DateFormat dateFormat =
            DateFormat.getDateInstance(DateFormat.MEDIUM);

    public ReportAdapter(OnReportClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<ReportEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ReportEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ReportEntity oldItem,
                        @NonNull ReportEntity newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ReportEntity oldItem,
                        @NonNull ReportEntity newItem) {
                    return oldItem.title.equals(newItem.title)
                            && oldItem.status == newItem.status
                            && oldItem.priority == newItem.priority
                            && oldItem.category == newItem.category
                            && oldItem.location.equals(newItem.location)
                            && oldItem.description.equals(newItem.description)
                            && oldItem.updatedAt == newItem.updatedAt
                            && oldItem.isSample == newItem.isSample;
                }
            };

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ReportViewHolder extends RecyclerView.ViewHolder {

        private final TextView badgeSample;
        private final TextView badgeYours;
        private final TextView status;
        private final TextView title;
        private final TextView categoryPriority;
        private final TextView location;
        private final TextView date;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeSample = itemView.findViewById(R.id.tvBadgeSample);
            badgeYours = itemView.findViewById(R.id.tvBadgeYours);
            status = itemView.findViewById(R.id.tvStatus);
            title = itemView.findViewById(R.id.tvTitle);
            categoryPriority = itemView.findViewById(R.id.tvCategoryPriority);
            location = itemView.findViewById(R.id.tvLocation);
            date = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReportClick(getItem(pos));
                }
            });
        }

        void bind(ReportEntity report) {
            android.content.Context context = itemView.getContext();

            title.setText(report.title);

            status.setText(context.getString(report.status.labelRes));
            int statusColor =
                    ContextCompat.getColor(context, report.status.colorRes);
            status.setTextColor(Color.WHITE);
            tint(status, statusColor);

            categoryPriority.setText(
                    context.getString(report.category.labelRes)
                            + "  •  "
                            + context.getString(report.priority.labelRes));

            location.setText(report.location);
            date.setText(DateUtils.formatDate(dateFormat, report.createdAt));

            // Honest labelling of where the row came from.
            if (report.isSample) {
                badgeSample.setVisibility(View.VISIBLE);
                badgeYours.setVisibility(View.GONE);
            } else {
                badgeSample.setVisibility(View.GONE);
                badgeYours.setVisibility(View.VISIBLE);
                tint(badgeYours,
                        ContextCompat.getColor(context, R.color.secondary));
            }
        }

        private void tint(TextView view, int color) {
            GradientDrawable background =
                    (GradientDrawable) view.getBackground().mutate();
            background.setColor(color);
            view.setBackground(background);
        }
    }
}
