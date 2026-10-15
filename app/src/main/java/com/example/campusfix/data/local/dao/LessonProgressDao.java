package com.example.campusfix.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.lifecycle.LiveData;

import com.example.campusfix.data.local.entity.LessonProgressEntity;

import java.util.List;

@Dao
public interface LessonProgressDao {

    /** Upsert: inserting an existing lessonId replaces the row. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(LessonProgressEntity progress);

    @androidx.room.Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    LessonProgressEntity getByIdSync(String lessonId);

    @androidx.room.Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId")
    androidx.lifecycle.LiveData<LessonProgressEntity> getById(String lessonId);

    @androidx.room.Query("SELECT * FROM lesson_progress ORDER BY lessonId ASC")
    LiveData<List<LessonProgressEntity>> getAll();

    @androidx.room.Query("SELECT * FROM lesson_progress WHERE bookmarked = 1 ORDER BY lessonId ASC")
    LiveData<List<LessonProgressEntity>> getBookmarked();

    @androidx.room.Query("SELECT COUNT(*) FROM lesson_progress WHERE completed = 1")
    LiveData<Integer> getCompletedCount();

    @androidx.room.Query("SELECT COUNT(*) FROM lesson_progress WHERE bookmarked = 1")
    LiveData<Integer> getBookmarkedCount();
}
