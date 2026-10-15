package com.example.campusfix.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.lifecycle.LiveData;

import com.example.campusfix.data.local.entity.StatusHistoryEntity;

import java.util.List;

@Dao
public interface StatusHistoryDao {

    @Insert
    long insert(StatusHistoryEntity entry);

    @androidx.room.Query("SELECT * FROM status_history WHERE reportId = :reportId ORDER BY timestamp ASC")
    LiveData<List<StatusHistoryEntity>> getForReport(long reportId);

    @androidx.room.Query("SELECT * FROM status_history WHERE reportId = :reportId ORDER BY timestamp ASC")
    List<StatusHistoryEntity> getForReportSync(long reportId);
}
