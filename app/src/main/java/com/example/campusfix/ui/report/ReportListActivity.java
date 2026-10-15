package com.example.campusfix.ui.report;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.R;
import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportFilter;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.data.model.ReportStatus;
import com.example.campusfix.ui.ViewModelFactory;
import com.example.campusfix.ui.adapter.ReportAdapter;
import com.example.campusfix.ui.viewmodel.ReportListViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Searchable, filterable report list backed by Room LiveData.
 *
 * Course topics demonstrated here: 02.1 (activity + explicit intent to the
 * detail/editor screens), 02.2 (filter state survives rotation via the
 * ViewModel, plus onSaveInstanceState for process death), 04.3 (popup menu
 * pickers), 04.4 (navigation), 04.5 (RecyclerView + DiffUtil).
 */
public class ReportListActivity extends AppCompatActivity {

    private static final String STATE_SEARCH = "state_search";

    private ReportListViewModel viewModel;
    private ReportAdapter adapter;

    private TextInputEditText etSearch;
    private ChipGroup chipGroupStatus;
    private MaterialButton btnFilterCategory;
    private MaterialButton btnFilterPriority;
    private MaterialButton btnSort;
    private TextView tvResultCount;
    private TextView tvEmptyTitle;
    private TextView tvEmptyBody;
    private View emptyState;
    private MaterialButton btnEmptyAction;
    private ProgressBar progressList;

    public static void launch(@NonNull Context context) {
        context.startActivity(new Intent(context, ReportListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        viewModel = new ViewModelProvider(this,
                ViewModelFactory.getInstance(
                        (CampusFixApplication) getApplication()))
                .get(ReportListViewModel.class);

        bindViews();
        setupRecyclerView();
        setupSearch();
        setupStatusChips();
        setupFilterButtons();
        setupFabAndEmptyAction();
        restoreState(savedInstanceState);
        observe();
    }

    private void bindViews() {
        etSearch = findViewById(R.id.etSearch);
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        btnFilterCategory = findViewById(R.id.btnFilterCategory);
        btnFilterPriority = findViewById(R.id.btnFilterPriority);
        btnSort = findViewById(R.id.btnSort);
        tvResultCount = findViewById(R.id.tvResultCount);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyBody = findViewById(R.id.tvEmptyBody);
        emptyState = findViewById(R.id.emptyState);
        btnEmptyAction = findViewById(R.id.btnEmptyAction);
        progressList = findViewById(R.id.progressList);
    }

    private void setupRecyclerView() {
        adapter = new ReportAdapter(report ->
                ReportDetailActivity.launch(this, report.id));
        RecyclerView recycler = findViewById(R.id.recyclerReports);
        recycler.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setFilter(viewModel.getFilter().withQuery(s.toString()));
                progressList.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupStatusChips() {
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // selectionRequired=true normally prevents this; recover safely.
                group.check(R.id.chipStatusAll);
                return;
            }
            viewModel.setFilter(viewModel.getFilter()
                    .withStatus(statusForChip(checkedIds.get(0))));
            progressList.setVisibility(View.VISIBLE);
        });
    }

    private ReportStatus statusForChip(int chipId) {
        if (chipId == R.id.chipStatusSubmitted) return ReportStatus.SUBMITTED;
        if (chipId == R.id.chipStatusInProgress) return ReportStatus.IN_PROGRESS;
        if (chipId == R.id.chipStatusResolved) return ReportStatus.RESOLVED;
        if (chipId == R.id.chipStatusRejected) return ReportStatus.REJECTED;
        return null; // chipStatusAll
    }

    private int chipForStatus(ReportStatus status) {
        if (status == ReportStatus.SUBMITTED) return R.id.chipStatusSubmitted;
        if (status == ReportStatus.IN_PROGRESS) return R.id.chipStatusInProgress;
        if (status == ReportStatus.RESOLVED) return R.id.chipStatusResolved;
        if (status == ReportStatus.REJECTED) return R.id.chipStatusRejected;
        return R.id.chipStatusAll;
    }

    private void setupFilterButtons() {
        btnFilterCategory.setOnClickListener(v -> showCategoryMenu(v));
        btnFilterPriority.setOnClickListener(v -> showPriorityMenu(v));
        btnSort.setOnClickListener(v -> showSortMenu(v));
    }

    private void showSortMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_sort, popup.getMenu());
        ReportFilter filter = viewModel.getFilter();
        popup.getMenu()
                .findItem(filter.newestFirst
                        ? R.id.action_sort_newest
                        : R.id.action_sort_priority)
                .setChecked(true);
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_sort_newest) {
                viewModel.setFilter(filter.withNewestFirst(true));
                return true;
            }
            if (id == R.id.action_sort_priority) {
                viewModel.setFilter(filter.withNewestFirst(false));
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showCategoryMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_category, popup.getMenu());
        ReportFilter filter = viewModel.getFilter();
        popup.getMenu().findItem(categoryMenuId(filter.category)).setChecked(true);
        popup.setOnMenuItemClickListener(item -> {
            viewModel.setFilter(filter.withCategory(categoryForMenu(item.getItemId())));
            return true;
        });
        popup.show();
    }

    private int categoryMenuId(ReportCategory category) {
        if (category == null) return R.id.action_category_all;
        switch (category) {
            case CLASSROOM_EQUIPMENT:
                return R.id.action_category_classroom_equipment;
            case FURNITURE:
                return R.id.action_category_furniture;
            case WIFI_NETWORK:
                return R.id.action_category_wifi;
            case PROJECTOR_AV:
                return R.id.action_category_projector;
            case CLEANLINESS:
                return R.id.action_category_cleanliness;
            case ELECTRICAL:
                return R.id.action_category_electrical;
            default:
                return R.id.action_category_other;
        }
    }

    private ReportCategory categoryForMenu(int itemId) {
        if (itemId == R.id.action_category_classroom_equipment)
            return ReportCategory.CLASSROOM_EQUIPMENT;
        if (itemId == R.id.action_category_furniture)
            return ReportCategory.FURNITURE;
        if (itemId == R.id.action_category_wifi)
            return ReportCategory.WIFI_NETWORK;
        if (itemId == R.id.action_category_projector)
            return ReportCategory.PROJECTOR_AV;
        if (itemId == R.id.action_category_cleanliness)
            return ReportCategory.CLEANLINESS;
        if (itemId == R.id.action_category_electrical)
            return ReportCategory.ELECTRICAL;
        if (itemId == R.id.action_category_other)
            return ReportCategory.OTHER;
        return null; // action_category_all
    }

    private void showPriorityMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_priority, popup.getMenu());
        ReportFilter filter = viewModel.getFilter();
        popup.getMenu().findItem(priorityMenuId(filter.priority)).setChecked(true);
        popup.setOnMenuItemClickListener(item -> {
            viewModel.setFilter(filter.withPriority(priorityForMenu(item.getItemId())));
            return true;
        });
        popup.show();
    }

    private int priorityMenuId(ReportPriority priority) {
        if (priority == null) return R.id.action_priority_all;
        switch (priority) {
            case LOW:
                return R.id.action_priority_low;
            case HIGH:
                return R.id.action_priority_high;
            case CRITICAL:
                return R.id.action_priority_critical;
            default:
                return R.id.action_priority_medium;
        }
    }

    private ReportPriority priorityForMenu(int itemId) {
        if (itemId == R.id.action_priority_low) return ReportPriority.LOW;
        if (itemId == R.id.action_priority_medium) return ReportPriority.MEDIUM;
        if (itemId == R.id.action_priority_high) return ReportPriority.HIGH;
        if (itemId == R.id.action_priority_critical) return ReportPriority.CRITICAL;
        return null; // action_priority_all
    }

    private void setupFabAndEmptyAction() {
        FloatingActionButton fab = findViewById(R.id.fabCreateReport);
        fab.setOnClickListener(v ->
                CreateReportActivity.launch(this, CreateReportActivity.NEW_REPORT));

        btnEmptyAction.setOnClickListener(v -> {
            if (viewModel.getFilter().isActive()) {
                clearAllFilters();
            } else {
                CreateReportActivity.launch(this, CreateReportActivity.NEW_REPORT);
            }
        });
    }

    /** Restores UI from the ViewModel (rotation) or the bundle (process death). */
    private void restoreState(Bundle savedInstanceState) {
        ReportFilter filter = viewModel.getFilter();

        // Process death: the fresh ViewModel has an empty query but the
        // activity previously held search text — recover it (topic 02.2).
        if (savedInstanceState != null
                && filter.query.isEmpty()
                && savedInstanceState.containsKey(STATE_SEARCH)) {
            String savedQuery = savedInstanceState.getString(STATE_SEARCH, "");
            filter = filter.withQuery(savedQuery);
            viewModel.setFilter(filter);
        }

        // Apply the filter to the widgets without triggering change loops
        // (setFilter de-duplicates identical values anyway).
        String query = filter.query;
        if (!query.equals(String.valueOf(etSearch.getText()))) {
            etSearch.setText(query);
        }
        chipGroupStatus.check(chipForStatus(filter.status));
        updateFilterButtonLabels(filter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SEARCH,
                etSearch.getText() == null
                        ? "" : etSearch.getText().toString());
    }

    private void observe() {
        viewModel.getReports().observe(this, reports -> {
            progressList.setVisibility(View.GONE);
            adapter.submitList(reports);
            renderResult(reports == null ? 0 : reports.size());
        });
    }

    private void renderResult(int count) {
        ReportFilter filter = viewModel.getFilter();

        tvResultCount.setText(getString(R.string.list_total_count, count));
        updateFilterButtonLabels(filter);

        if (count > 0) {
            emptyState.setVisibility(View.GONE);
            return;
        }

        emptyState.setVisibility(View.VISIBLE);
        if (filter.isActive()) {
            tvEmptyTitle.setText(R.string.list_no_matches_title);
            tvEmptyBody.setText(R.string.list_no_matches_body);
            btnEmptyAction.setText(R.string.list_empty_clear_filters);
        } else {
            tvEmptyTitle.setText(R.string.list_empty_title);
            tvEmptyBody.setText(R.string.list_empty_body);
            btnEmptyAction.setText(R.string.nav_create_report);
        }
    }

    private void updateFilterButtonLabels(ReportFilter filter) {
        btnFilterCategory.setText(filter.category == null
                ? R.string.filter_category
                : filter.category.labelRes);
        btnFilterPriority.setText(filter.priority == null
                ? R.string.filter_priority
                : filter.priority.labelRes);
    }

    private void clearAllFilters() {
        etSearch.setText("");
        chipGroupStatus.check(R.id.chipStatusAll);
        viewModel.clearFilters();
        progressList.setVisibility(View.VISIBLE);
    }
}
