package com.example.campusfix.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.campusfix.data.local.CampusDatabase;
import com.example.campusfix.data.local.dao.ReportDao;
import com.example.campusfix.data.local.dao.StatusHistoryDao;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;
import com.example.campusfix.data.model.ReportFilter;
import com.example.campusfix.data.model.ReportStatus;
import com.example.campusfix.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

/**
 * Single source of truth for report data. Activities/ViewModels never talk
 * to the DAOs directly, so switching storage later would only touch this
 * layer.
 *
 * All writes go through {@link AppExecutors#diskIO()} — never the main
 * thread. LiveData results are delivered on the main thread automatically.
 */
public class ReportRepository {

    public interface InsertCallback {
        /** Called on the main thread with the generated row id. */
        void onInserted(long newId);
    }

    public interface SimpleCallback {
        /** Called on the main thread after the operation finished. */
        void onDone();
    }

    public interface ErrorCallback {
        void onError(Exception error);
    }

    private final ReportDao reportDao;
    private final StatusHistoryDao historyDao;

    public ReportRepository(CampusDatabase db) {
        this.reportDao = db.reportDao();
        this.historyDao = db.statusHistoryDao();
    }

    // ---------------------------------------------------------------- reads

    public LiveData<List<ReportEntity>> findReports(ReportFilter filter) {
        return reportDao.findReports(buildQuery(filter));
    }

    public LiveData<ReportEntity> getReport(long id) {
        return reportDao.getById(id);
    }

    public LiveData<List<StatusHistoryEntity>> getHistory(long reportId) {
        return historyDao.getForReport(reportId);
    }

    public LiveData<Integer> getTotalCount() {
        return reportDao.getTotalCount();
    }

    public LiveData<List<ReportDao.StatusCount>> getCountByStatus() {
        return reportDao.getCountByStatus();
    }

    public LiveData<List<ReportDao.CategoryCount>> getCountByCategory() {
        return reportDao.getCountByCategory();
    }

    public LiveData<List<ReportDao.PriorityCount>> getCountByPriority() {
        return reportDao.getCountByPriority();
    }

    public LiveData<Integer> getSampleCount() {
        return reportDao.getSampleCount();
    }

    // --------------------------------------------------------------- writes

    public void insert(ReportEntity report,
                       @Nullable InsertCallback callback,
                       @Nullable ErrorCallback errorCallback) {
        AppExecutors.diskIO().execute(() -> {
            try {
                long id = reportDao.insert(report);
                if (callback != null) {
                    AppExecutors.mainThread(() -> callback.onInserted(id));
                }
            } catch (Exception e) {
                deliverError(errorCallback, e);
            }
        });
    }

    /** Adds the initial history entry right after a report is created. */
    public void recordInitialStatus(long reportId, ReportStatus status) {
        AppExecutors.diskIO().execute(() -> {
            StatusHistoryEntity entry = new StatusHistoryEntity();
            entry.reportId = reportId;
            entry.status = status;
            entry.timestamp = System.currentTimeMillis();
            entry.note = null;
            historyDao.insert(entry);
        });
    }

    public void updateReport(ReportEntity report,
                             @Nullable SimpleCallback callback,
                             @Nullable ErrorCallback errorCallback) {
        AppExecutors.diskIO().execute(() -> {
            try {
                report.updatedAt = System.currentTimeMillis();
                reportDao.updateReport(
                        report.id,
                        report.title,
                        report.category,
                        report.description,
                        report.location,
                        report.priority,
                        report.photoUri,
                        report.updatedAt);
                if (callback != null) {
                    AppExecutors.mainThread(callback::onDone);
                }
            } catch (Exception e) {
                deliverError(errorCallback, e);
            }
        });
    }

    /**
     * Changes the status and appends a history entry in the background.
     * The demo flag marks simulated changes honestly in the UI.
     */
    public void changeStatus(long reportId,
                             ReportStatus newStatus,
                             @Nullable SimpleCallback callback,
                             @Nullable ErrorCallback errorCallback) {
        AppExecutors.diskIO().execute(() -> {
            try {
                long now = System.currentTimeMillis();
                reportDao.updateStatus(reportId, newStatus, now);

                StatusHistoryEntity entry = new StatusHistoryEntity();
                entry.reportId = reportId;
                entry.status = newStatus;
                entry.timestamp = now;
                entry.note = "Simulated status change (demo)";
                historyDao.insert(entry);

                if (callback != null) {
                    AppExecutors.mainThread(callback::onDone);
                }
            } catch (Exception e) {
                deliverError(errorCallback, e);
            }
        });
    }

    public void deleteReport(long reportId,
                             @Nullable SimpleCallback callback,
                             @Nullable ErrorCallback errorCallback) {
        AppExecutors.diskIO().execute(() -> {
            try {
                reportDao.deleteReportWithHistory(reportId);
                if (callback != null) {
                    AppExecutors.mainThread(callback::onDone);
                }
            } catch (Exception e) {
                deliverError(errorCallback, e);
            }
        });
    }

    /** Synchronous read for background flows (never call on main thread). */
    public ReportEntity getReportSync(long id) {
        return reportDao.getByIdSync(id);
    }

    private void deliverError(@Nullable ErrorCallback callback, Exception e) {
        if (callback != null) {
            AppExecutors.mainThread(() -> callback.onError(e));
        }
    }

    // ------------------------------------------------------------- filtering

    /**
     * Builds one dynamic SELECT covering the current search text, filters
     * and sort order. Arguments are bound positionally in the same order
     * the placeholders appear.
     */
    static SupportSQLiteQuery buildQuery(ReportFilter filter) {
        StringBuilder sql = new StringBuilder("SELECT * FROM reports");
        List<Object> args = new ArrayList<>();
        boolean firstWhere = true;

        if (filter.status != null) {
            sql.append(firstWhere ? " WHERE " : " AND ")
                    .append("status = ?");
            args.add(filter.status.name());
            firstWhere = false;
        }

        if (filter.category != null) {
            sql.append(firstWhere ? " WHERE " : " AND ")
                    .append("category = ?");
            args.add(filter.category.name());
            firstWhere = false;
        }

        if (filter.priority != null) {
            sql.append(firstWhere ? " WHERE " : " AND ")
                    .append("priority = ?");
            args.add(filter.priority.name());
            firstWhere = false;
        }

        if (!filter.query.isEmpty()) {
            sql.append(firstWhere ? " WHERE " : " AND ")
                    .append("(title LIKE ? OR description LIKE ? OR location LIKE ?)");
            String like = "%" + filter.query + "%";
            args.add(like);
            args.add(like);
            args.add(like);
        }

        if (filter.newestFirst) {
            sql.append(" ORDER BY createdAt DESC");
        } else {
            // Most urgent first, then newest.
            sql.append(" ORDER BY CASE priority ")
                    .append("WHEN 'CRITICAL' THEN 0 ")
                    .append("WHEN 'HIGH' THEN 1 ")
                    .append("WHEN 'MEDIUM' THEN 2 ")
                    .append("WHEN 'LOW' THEN 3 ELSE 4 END, ")
                    .append("createdAt DESC");
        }

        return new SimpleSQLiteQuery(sql.toString(), args.toArray());
    }
}
