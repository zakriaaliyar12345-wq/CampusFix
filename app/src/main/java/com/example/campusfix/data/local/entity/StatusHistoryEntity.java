package com.example.campusfix.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.campusfix.data.model.ReportStatus;

/**
 * One entry of a report's status timeline. Rows are added whenever a status
 * changes; they are never edited afterwards (an append-only history).
 *
 * The foreign key cascades the delete so removing a report also removes its
 * history, and the index makes "history for report #id" queries fast.
 */
@Entity(
        tableName = "status_history",
        foreignKeys = @ForeignKey(
                entity = ReportEntity.class,
                parentColumns = {"id"},
                childColumns = {"reportId"},
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("reportId")}
)
public class StatusHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "reportId")
    public long reportId;

    @NonNull
    public ReportStatus status = ReportStatus.SUBMITTED;

    /** Short human note, e.g. "Submitted (demo)". Null is allowed. */
    @ColumnInfo(name = "note")
    public String note;

    public long timestamp;
}
