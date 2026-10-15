package com.example.campusfix.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.data.local.CampusDatabase;
import com.example.campusfix.data.repository.LessonProgressRepository;
import com.example.campusfix.data.repository.ReportRepository;
import com.example.campusfix.data.repository.SettingsRepository;
import com.example.campusfix.ui.viewmodel.LearningViewModel;
import com.example.campusfix.ui.viewmodel.LessonDetailViewModel;
import com.example.campusfix.ui.viewmodel.QuizViewModel;
import com.example.campusfix.ui.viewmodel.ReportDetailViewModel;
import com.example.campusfix.ui.viewmodel.ReportEditorViewModel;
import com.example.campusfix.ui.viewmodel.ReportListViewModel;
import com.example.campusfix.ui.viewmodel.StatisticsViewModel;

/**
 * Hand-written ViewModel factory (no lifecycle-viewmodel-compose or KAPT
 * compiler needed): constructs each ViewModel with the single Room database,
 * the repository layer, and — where relevant — SharedPreferences.
 *
 * The factory keeps Activities free of database wiring and demonstrates the
 * standard {@link ViewModelProvider.Factory} pattern (course topic 10.1).
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private static volatile ViewModelFactory instance;

    private final CampusFixApplication application;

    private ViewModelFactory(CampusFixApplication application) {
        this.application = application;
    }

    @NonNull
    public static ViewModelFactory getInstance(@NonNull CampusFixApplication application) {
        if (instance == null) {
            synchronized (ViewModelFactory.class) {
                if (instance == null) {
                    instance = new ViewModelFactory(application);
                }
            }
        }
        return instance;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        CampusDatabase db = CampusDatabase.getInstance(application);

        if (modelClass.isAssignableFrom(ReportListViewModel.class)) {
            return (T) new ReportListViewModel(new ReportRepository(db));
        }
        if (modelClass.isAssignableFrom(ReportEditorViewModel.class)) {
            return (T) new ReportEditorViewModel(new ReportRepository(db));
        }
        if (modelClass.isAssignableFrom(ReportDetailViewModel.class)) {
            return (T) new ReportDetailViewModel(new ReportRepository(db));
        }
        if (modelClass.isAssignableFrom(StatisticsViewModel.class)) {
            return (T) new StatisticsViewModel(new ReportRepository(db));
        }
        if (modelClass.isAssignableFrom(LearningViewModel.class)) {
            return (T) new LearningViewModel(
                    new LessonProgressRepository(db.lessonProgressDao()));
        }
        if (modelClass.isAssignableFrom(LessonDetailViewModel.class)) {
            return (T) new LessonDetailViewModel(
                    new LessonProgressRepository(db.lessonProgressDao()));
        }
        if (modelClass.isAssignableFrom(QuizViewModel.class)) {
            return (T) new QuizViewModel(
                    new LessonProgressRepository(db.lessonProgressDao()));
        }

        throw new IllegalArgumentException(
                "Unknown ViewModel class: " + modelClass.getName());
    }

    /** Convenience for screens that only read settings. */
    public SettingsRepository settingsRepository() {
        return new SettingsRepository(application);
    }
}
