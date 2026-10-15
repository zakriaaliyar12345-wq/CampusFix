package com.example.campusfix.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.lifecycle.LiveData;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;
import com.example.campusfix.data.model.ReportStatus;

import java.util.List;

/**
 * Data-access object for reports plus their status history.
 *
 * Every method returns either LiveData (observed reactively by ViewModels)
 * or is executed by the caller on a background executor â€” Room forbids
 * main-thread database work by default.
 */
@Dao
public interface ReportDao {

    /**
     * Dynamic list query: the repository builds WHERE/ORDER BY clauses at
     * runtime with SimpleSQLiteQuery (search + status/category/priority
     * filters + sort). Room re-runs the query automatically when the
     * underlying tables change because observedEntities is declared.
     */
    @RawQuery(observedEntities = ReportEntity.class)
    LiveData<List<ReportEntity>> findReports(SupportSQLiteQuery query);

    @Query("SELECT * FROM reports WHERE id = :id")
    LiveData<ReportEntity> getById(long id);

    /** Synchronous variant used by background edit/delete flows. */
    @Query("SELECT * FROM reports WHERE id = :id")
    ReportEntity getByIdSync(long id);

    /** @return the generated row id, usable on background threads. */
    @Insert
    long insert(ReportEntity report);

    @Query("DELETE FROM reports WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM status_history WHERE reportId = :reportId")
    void deleteHistoryForReport(long reportId);

    /**
     * Atomic delete: report + its history in one transaction. Declared here
     * (not split across DAOs) so Room can guarantee both statements commit
     * together.
     */
    @Transaction
    default void deleteReportWithHistory(long reportId) {
        deleteHistoryForReport(reportId);
        deleteById(reportId);
    }

    @Query("UPDATE reports SET status = :status, updatedAt = :timestamp WHERE id = :id")
    void updateStatus(long id, ReportStatus status, long timestamp);

    @Query("UPDATE reports SET title = :title, category = :category, "
            + "description = :description, location = :location, "
            + "priority = :priority, photoUri = :photoUri, updatedAt = :timestamp "
            + "WHERE id = :id")
    void updateReport(long id, String title,
                      com.example.campusfix.data.model.ReportCategory category,
                      String description, String location,
                      com.example.campusfix.data.model.ReportPriority priority,
                      String photoUri, long timestamp);

    // ---- Statistics aggregates (all run inside Room, off the main thread) ----

    @Query("SELECT COUNT(*) FROM reports")
    LiveData<Integer> getTotalCount();

    /** Synchronous count for background seeding (never on the main thread). */
    @Query("SELECT COUNT(*) FROM reports")
    int getTotalCountSync();

    @Query("SELECT status, COUNT(*) AS count FROM reports GROUP BY status")
    LiveData<List<StatusCount>> getCountByStatus();

    @Query("SELECT category, COUNT(*) AS count FROM reports GROUP BY category")
    LiveData<List<CategoryCount>> getCountByCategory();

    @Query("SELECT priority, COUNT(*) AS count FROM reports GROUP BY priority")
    LiveData<List<PriorityCount>> getCountByPriority();

    @Query("SELECT COUNT(*) FROM reports WHERE isSample = 1")
    LiveData<Integer> getSampleCount();

    /** Plain projection classes Room maps from the SELECT aliases. */
    class StatusCount {
        public ReportStatus status;
        public int count;
    }

    class CategoryCount {
        public com.example.campusfix.data.model.ReportCategory category;
        public int count;
    }

    class PriorityCount {
        public com.example.campusfix.data.model.ReportPriority priority;
        public int count;
    }
}
