package com.example.campusfix.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.campusfix.data.local.entity.LessonProgressEntity;
import com.example.campusfix.data.repository.LessonProgressRepository;
import com.example.campusfix.learning.Lesson;
import com.example.campusfix.learning.LessonCatalog;
import com.example.campusfix.ui.model.LessonRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Learning Lab list state: assembles the static 32-lesson catalog with the
 * per-lesson progress rows stored in Room, and applies the All / Bookmarked
 * / Completed filter. Because the assembly lives in the ViewModel, rotation
 * reuses the same rows instead of recomputing them (topic 02.2).
 */
public class LearningViewModel extends ViewModel {

    /** List filter — mirrors the three chips on the screen (topic 04.2). */
    public enum Mode { ALL, BOOKMARKS, COMPLETED }

    private final LessonProgressRepository repository;

    private final MutableLiveData<Mode> mode = new MutableLiveData<>(Mode.ALL);

    private final MediatorLiveData<List<LessonRow>> rows = new MediatorLiveData<>();

    private List<LessonProgressEntity> progress = Collections.emptyList();

    public LearningViewModel(@NonNull LessonProgressRepository repository) {
        this.repository = repository;

        rows.addSource(repository.getAll(), list -> {
            progress = list == null ? Collections.emptyList() : list;
            rebuild();
        });
        rows.addSource(mode, value -> rebuild());
    }

    public LiveData<List<LessonRow>> getRows() {
        return rows;
    }

    public LiveData<Mode> getMode() {
        return mode;
    }

    public void setMode(@NonNull Mode newMode) {
        Mode current = mode.getValue();
        if (current != newMode) {
            mode.setValue(newMode);
        }
    }

    public LiveData<Integer> getCompletedCount() {
        return repository.getCompletedCount();
    }

    public LiveData<Integer> getBookmarkedCount() {
        return repository.getBookmarkedCount();
    }

    /** Total lessons in the course — 32, verified by LessonCatalogTest. */
    public int getTotalLessons() {
        return LessonCatalog.size();
    }

    private void rebuild() {
        Mode current = mode.getValue();
        if (current == null) current = Mode.ALL;

        Map<String, LessonProgressEntity> byId = new HashMap<>();
        for (LessonProgressEntity entity : progress) {
            byId.put(entity.lessonId, entity);
        }

        List<LessonRow> built = new ArrayList<>();
        for (String unit : LessonCatalog.unitTitles()) {
            List<LessonRow> unitRows = new ArrayList<>();
            for (Lesson lesson : LessonCatalog.inUnit(unit)) {
                LessonProgressEntity entity = byId.get(lesson.id);
                boolean completed = entity != null && entity.completed;
                boolean bookmarked = entity != null && entity.bookmarked;
                if (current == Mode.BOOKMARKS && !bookmarked) continue;
                if (current == Mode.COMPLETED && !completed) continue;
                unitRows.add(LessonRow.lesson(lesson, entity));
            }
            if (!unitRows.isEmpty()) {
                built.add(LessonRow.header(unit));
                built.addAll(unitRows);
            }
        }
        rows.setValue(Collections.unmodifiableList(built));
    }
}
