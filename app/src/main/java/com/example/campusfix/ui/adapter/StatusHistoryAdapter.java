package com.example.campusfix.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfix.R;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Status-timeline adapter. History is append-only and small, so a simple
 * submitList-style setter is enough (no DiffUtil needed here).
 */
public class StatusHistoryAdapter
        extends RecyclerView.Adapter<StatusHistoryAdapter.HistoryViewHolder> {

    private final List<StatusHistoryEntity> items = new ArrayList<>();
    private final DateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public void submitList(List<StatusHistoryEntity> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_status_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final View dot;
        private final TextView status;
        private final TextView note;
        private final TextView time;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dot = itemView.findViewById(R.id.tvDot);
            status = itemView.findViewById(R.id.tvHistoryStatus);
            note = itemView.findViewById(R.id.tvHistoryNote);
            time = itemView.findViewById(R.id.tvHistoryTime);
        }

        void bind(StatusHistoryEntity entry) {
            Context context = itemView.getContext();

            status.setText(context.getString(entry.status.labelRes));
            status.setTextColor(
                    ContextCompat.getColor(context, entry.status.colorRes));

            GradientDrawable background =
                    (GradientDrawable) dot.getBackground().mutate();
            background.setColor(
                    ContextCompat.getColor(context, entry.status.colorRes));
            dot.setBackground(background);

            if (entry.note == null || entry.note.isEmpty()) {
                note.setVisibility(View.GONE);
            } else {
                note.setVisibility(View.VISIBLE);
                note.setText(entry.note);
            }

            time.setText(dateFormat.format(entry.timestamp));
        }
    }
}
