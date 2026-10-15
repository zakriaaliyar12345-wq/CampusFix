package com.example.campusfix.ui.report;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.R;
import com.example.campusfix.data.local.dao.ReportDao;
import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.data.model.ReportStatus;
import com.example.campusfix.ui.ViewModelFactory;
import com.example.campusfix.ui.viewmodel.StatisticsViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report statistics computed entirely by Room aggregate queries (topic 10.1)
 * and rendered as label + count + percentage bar rows.
 *
 * Every breakdown iterates the fixed enum order so rows never jump around,
 * and all three sections hide together when there is no data yet.
 */
public class StatisticsActivity extends AppCompatActivity {

    private StatisticsViewModel viewModel;

    private TextView tvStatsTotal;
    private TextView tvStatsSampleNote;
    private TextView tvStatsEmpty;
    private LinearLayout layoutStatusBreakdown;
    private LinearLayout layoutCategoryBreakdown;
    private LinearLayout layoutPriorityBreakdown;
    private View tvSectionStatus;
    private View tvSectionCategory;
    private View tvSectionPriority;

    private int totalCount;

    public static void launch(@NonNull Context context) {
        context.startActivity(new Intent(context, StatisticsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        viewModel = new ViewModelProvider(this,
                ViewModelFactory.getInstance(
                        (CampusFixApplication) getApplication()))
                .get(StatisticsViewModel.class);

        tvStatsTotal = findViewById(R.id.tvStatsTotal);
        tvStatsSampleNote = findViewById(R.id.tvStatsSampleNote);
        tvStatsEmpty = findViewById(R.id.tvStatsEmpty);
        layoutStatusBreakdown = findViewById(R.id.layoutStatusBreakdown);
        layoutCategoryBreakdown = findViewById(R.id.layoutCategoryBreakdown);
        layoutPriorityBreakdown = findViewById(R.id.layoutPriorityBreakdown);
        tvSectionStatus = findViewById(R.id.tvSectionStatus);
        tvSectionCategory = findViewById(R.id.tvSectionCategory);
        tvSectionPriority = findViewById(R.id.tvSectionPriority);

        observe();
    }

    private void observe() {
        viewModel.getTotalCount().observe(this, total -> {
            totalCount = total == null ? 0 : total;
            tvStatsTotal.setText(String.valueOf(totalCount));
            boolean empty = totalCount == 0;
            tvStatsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            int sectionVisibility = empty ? View.GONE : View.VISIBLE;
            tvSectionStatus.setVisibility(sectionVisibility);
            tvSectionCategory.setVisibility(sectionVisibility);
            tvSectionPriority.setVisibility(sectionVisibility);
            layoutStatusBreakdown.setVisibility(sectionVisibility);
            layoutCategoryBreakdown.setVisibility(sectionVisibility);
            layoutPriorityBreakdown.setVisibility(sectionVisibility);
        });

        viewModel.getSampleCount().observe(this, sample -> {
            int sampleCount = sample == null ? 0 : sample;
            if (sampleCount > 0 && totalCount > 0) {
                tvStatsSampleNote.setVisibility(View.VISIBLE);
                tvStatsSampleNote.setText(getString(R.string.stats_sample_note,
                        sampleCount, totalCount));
            } else {
                tvStatsSampleNote.setVisibility(View.GONE);
            }
        });

        viewModel.getStatusCounts()
                .observe(this, counts -> renderStatusRows(counts));
        viewModel.getCategoryCounts()
                .observe(this, counts -> renderCategoryRows(counts));
        viewModel.getPriorityCounts()
                .observe(this, counts -> renderPriorityRows(counts));
    }

    // -------------------------------------------------------------- rows

    private void renderStatusRows(@Nullable List<ReportDao.StatusCount> counts) {
        if (counts == null) return;
        Map<ReportStatus, Integer> map = new HashMap<>();
        for (ReportDao.StatusCount row : counts) map.put(row.status, row.count);

        layoutStatusBreakdown.removeAllViews();
        for (ReportStatus status : ReportStatus.values()) {
            int count = map.containsKey(status) ? map.get(status) : 0;
            addRow(layoutStatusBreakdown,
                    getString(status.labelRes),
                    ContextCompat.getColor(this, status.colorRes),
                    count);
        }
    }

    private void renderCategoryRows(@Nullable List<ReportDao.CategoryCount> counts) {
        if (counts == null) return;
        Map<ReportCategory, Integer> map = new HashMap<>();
        for (ReportDao.CategoryCount row : counts) map.put(row.category, row.count);

        layoutCategoryBreakdown.removeAllViews();
        int categoryColor = ContextCompat.getColor(this, R.color.secondary);
        for (ReportCategory category : ReportCategory.values()) {
            int count = map.containsKey(category) ? map.get(category) : 0;
            addRow(layoutCategoryBreakdown,
                    getString(category.labelRes),
                    categoryColor,
                    count);
        }
    }

    private void renderPriorityRows(@Nullable List<ReportDao.PriorityCount> counts) {
        if (counts == null) return;
        Map<ReportPriority, Integer> map = new HashMap<>();
        for (ReportDao.PriorityCount row : counts) map.put(row.priority, row.count);

        layoutPriorityBreakdown.removeAllViews();
        for (ReportPriority priority : ReportPriority.values()) {
            int count = map.containsKey(priority) ? map.get(priority) : 0;
            addRow(layoutPriorityBreakdown,
                    getString(priority.labelRes),
                    ContextCompat.getColor(this, priority.colorRes),
                    count);
        }
    }

    private void addRow(LinearLayout container, String label, int color, int count) {
        View row = LayoutInflater.from(this)
                .inflate(R.layout.item_stat_row, container, false);

        TextView tvLabel = row.findViewById(R.id.tvStatLabel);
        TextView tvCount = row.findViewById(R.id.tvStatCount);
        View dot = row.findViewById(R.id.statDot);
        ProgressBar progress = row.findViewById(R.id.statProgress);

        tvLabel.setText(label);
        tvCount.setText(String.valueOf(count));

        dot.getBackground().setTint(color);
        progress.setProgress(totalCount > 0
                ? (int) (count * 100f / totalCount) : 0);
        progress.setProgressTintList(
                android.content.res.ColorStateList.valueOf(color));

        container.addView(row);
    }
}
