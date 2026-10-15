package com.example.campusfix.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.campusfix.data.local.entity.LessonProgressEntity;
import com.example.campusfix.data.repository.LessonProgressRepository;
import com.example.campusfix.learning.Lesson;
import com.example.campusfix.learning.LessonCatalog;

/**
 * Lesson reader state: the static lesson text (from the catalog) plus its
 * live Room progress row. Bookmark and completion changes go through the
 * repository on the disk executor; the UI only observes LiveData.
 */
public class LessonDetailViewModel extends ViewModel {

    private final LessonProgressRepository repository;

    private final MutableLiveData<String> lessonId = new MutableLiveData<>();

    private final LiveData<LessonProgressEntity> progress;

    public LessonDetailViewModel(@NonNull LessonProgressRepository repository) {
        this.repository = repository;
        this.progress = Transformations.switchMap(
                lessonId, repository::getProgress);
    }

    /**
     * Binds this reader to a lesson and records it as "last opened".
     * Idempotent: rotation re-delivers the same id without a second write.
     */
    public void load(@NonNull String id) {
        String current = lessonId.getValue();
        if (!id.equals(current)) {
            lessonId.setValue(id);
            repository.touch(id);
        }
    }

    @Nullable
    public String getLessonId() {
        return lessonId.getValue();
    }

    /** Static lesson content; null only for an unknown id. */
    @Nullable
    public Lesson getLesson() {
        return LessonCatalog.byId(lessonId.getValue());
    }

    public LiveData<LessonProgressEntity> getProgress() {
        return progress;
    }

    public void toggleBookmark() {
        String id = lessonId.getValue();
        if (id != null) repository.toggleBookmark(id);
    }

    public void setCompleted(boolean completed) {
        String id = lessonId.getValue();
        if (id != null) repository.setCompleted(id, completed);
    }
}
