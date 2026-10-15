package com.example.campusfix.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.campusfix.data.local.dao.ReportDao;
import com.example.campusfix.data.repository.ReportRepository;

import java.util.List;

/**
 * Statistics screen state. Every value is a Room aggregate evaluated inside
 * the database (never by looping rows on the UI thread) and observed
 * reactively — adding or changing a report updates the screen automatically.
 */
public class StatisticsViewModel extends ViewModel {

    private final LiveData<Integer> totalCount;
    private final LiveData<Integer> sampleCount;
    private final LiveData<List<ReportDao.StatusCount>> statusCounts;
    private final LiveData<List<ReportDao.CategoryCount>> categoryCounts;
    private final LiveData<List<ReportDao.PriorityCount>> priorityCounts;

    public StatisticsViewModel(@NonNull ReportRepository repository) {
        totalCount = repository.getTotalCount();
        sampleCount = repository.getSampleCount();
        statusCounts = repository.getCountByStatus();
        categoryCounts = repository.getCountByCategory();
        priorityCounts = repository.getCountByPriority();
    }

    public LiveData<Integer> getTotalCount() {
        return totalCount;
    }

    public LiveData<Integer> getSampleCount() {
        return sampleCount;
    }

    public LiveData<List<ReportDao.StatusCount>> getStatusCounts() {
        return statusCounts;
    }

    public LiveData<List<ReportDao.CategoryCount>> getCategoryCounts() {
        return categoryCounts;
    }

    public LiveData<List<ReportDao.PriorityCount>> getPriorityCounts() {
        return priorityCounts;
    }
}
