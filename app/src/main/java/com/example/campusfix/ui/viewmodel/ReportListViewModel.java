package com.example.campusfix.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.model.ReportFilter;
import com.example.campusfix.data.repository.ReportRepository;

import java.util.List;

/**
 * Holds the report-list filter state so it survives rotation and
 * process-driven recreation (course topic 02.2 + 10.1).
 *
 * The screen never queries the database itself: it pushes a new immutable
 * {@link ReportFilter} in, and observes the mapped LiveData out.
 */
public class ReportListViewModel extends ViewModel {

    private final ReportRepository repository;

    private final MutableLiveData<ReportFilter> filter =
            new MutableLiveData<>(new ReportFilter());

    /** Derived stream: re-subscribed whenever the filter changes. */
    private final LiveData<List<ReportEntity>> reports;

    public ReportListViewModel(@NonNull ReportRepository repository) {
        this.repository = repository;
        this.reports = Transformations.switchMap(filter, repository::findReports);
    }

    public LiveData<List<ReportEntity>> getReports() {
        return reports;
    }

    @NonNull
    public ReportFilter getFilter() {
        ReportFilter current = filter.getValue();
        return current == null ? new ReportFilter() : current;
    }

    public void setFilter(@NonNull ReportFilter newFilter) {
        // Avoid re-running the query when nothing actually changed.
        ReportFilter current = filter.getValue();
        if (current != null
                && current.query.equals(newFilter.query)
                && current.status == newFilter.status
                && current.category == newFilter.category
                && current.priority == newFilter.priority
                && current.newestFirst == newFilter.newestFirst) {
            return;
        }
        filter.setValue(newFilter);
    }

    public void clearFilters() {
        setFilter(new ReportFilter());
    }
}
