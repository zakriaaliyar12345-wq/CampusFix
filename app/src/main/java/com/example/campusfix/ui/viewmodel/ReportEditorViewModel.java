package com.example.campusfix.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.campusfix.R;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.data.model.ReportStatus;
import com.example.campusfix.data.repository.ReportRepository;
import com.example.campusfix.util.Event;
import com.example.campusfix.util.Validator;

/**
 * Create/edit screen state: loads the report being edited, validates input,
 * and performs the insert or update through the repository.
 *
 * Validation lives here (pure logic via {@link Validator}) so it survives
 * rotation and is unit-testable; the Activity only renders errors.
 */
public class ReportEditorViewModel extends ViewModel {

    /** Extra value meaning "create a brand-new report". */
    public static final long NEW_REPORT = -1L;

    private final ReportRepository repository;

    private final MutableLiveData<Long> editingId = new MutableLiveData<>(NEW_REPORT);

    /** The report being edited, or null in create mode / while loading. */
    private final LiveData<ReportEntity> report;

    private final MutableLiveData<Boolean> saving = new MutableLiveData<>(false);

    private final MutableLiveData<Event<Long>> savedEvent = new MutableLiveData<>();

    private final MutableLiveData<Event<Integer>> errorMessageEvent =
            new MutableLiveData<>();

    public ReportEditorViewModel(@NonNull ReportRepository repository) {
        this.repository = repository;
        this.report = Transformations.switchMap(editingId, id -> {
            if (id == null || id <= 0) {
                MutableLiveData<ReportEntity> empty = new MutableLiveData<>(null);
                return empty;
            }
            return repository.getReport(id);
        });
    }

    /** Call once from onCreate with the incoming intent extra. */
    public void start(long id) {
        Long current = editingId.getValue();
        if (current == null || current != id) {
            editingId.setValue(id);
        }
    }

    public boolean isEditMode() {
        Long id = editingId.getValue();
        return id != null && id > 0;
    }

    public LiveData<ReportEntity> getReport() {
        return report;
    }

    public LiveData<Boolean> isSaving() {
        return saving;
    }

    public LiveData<Event<Long>> getSavedEvent() {
        return savedEvent;
    }

    public LiveData<Event<Integer>> getErrorMessageEvent() {
        return errorMessageEvent;
    }

    /**
     * Validates then persists. Creates a new row (with its initial history
     * entry) or updates the existing one.
     *
     * @return null when local validation passed (save proceeds), otherwise
     *         the error message for the failing field — the Activity shows it
     *         inline before any database work happens.
     */
    @Nullable
    public String save(@NonNull String title,
                       @NonNull ReportCategory category,
                       @NonNull String description,
                       @NonNull String location,
                       @NonNull ReportPriority priority,
                       @Nullable String photoUri) {
        String errorKey = Validator.validateReport(title, description, location);
        if (errorKey != null) {
            return errorKey;
        }

        saving.setValue(true);

        if (isEditMode()) {
            // Only the columns ReportRepository.updateReport() writes are set
            // here; createdAt/status/history rows are left untouched.
            ReportEntity entity = new ReportEntity();
            entity.id = editingId.getValue();
            entity.title = title.trim();
            entity.category = category;
            entity.description = description.trim();
            entity.location = location.trim();
            entity.priority = priority;
            entity.photoUri = photoUri;
            repository.updateReport(entity,
                    () -> {
                        saving.setValue(false);
                        savedEvent.setValue(new Event<>(entity.id));
                    },
                    error -> {
                        saving.setValue(false);
                        errorMessageEvent.setValue(new Event<>(R.string.state_error));
                    });
        } else {
            ReportEntity entity = new ReportEntity();
            entity.title = title.trim();
            entity.category = category;
            entity.description = description.trim();
            entity.location = location.trim();
            entity.priority = priority;
            entity.status = ReportStatus.SUBMITTED;
            entity.photoUri = photoUri;
            long now = System.currentTimeMillis();
            entity.createdAt = now;
            entity.updatedAt = now;
            entity.isSample = false;
            repository.insert(entity,
                    newId -> {
                        repository.recordInitialStatus(newId, ReportStatus.SUBMITTED);
                        saving.setValue(false);
                        savedEvent.setValue(new Event<>(newId));
                    },
                    error -> {
                        saving.setValue(false);
                        errorMessageEvent.setValue(new Event<>(R.string.state_error));
                    });
        }
        return null;
    }
}
