package com.example.campusfix.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.campusfix.R;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;
import com.example.campusfix.data.model.ReportStatus;
import com.example.campusfix.data.repository.ReportRepository;
import com.example.campusfix.util.Event;

import java.util.List;

/**
 * Detail screen state: one report plus its append-only status timeline.
 * Status changes and deletion go through the repository (background thread);
 * completion is signalled with one-shot events so a rotation never repeats
 * a toast or a finish().
 *
 * Repository callbacks are delivered on the main thread, so setValue() is
 * safe inside them.
 */
public class ReportDetailViewModel extends ViewModel {

    private final ReportRepository repository;

    private final MutableLiveData<Long> reportId = new MutableLiveData<>();

    private final LiveData<ReportEntity> report;

    private final LiveData<List<StatusHistoryEntity>> history;

    private final MutableLiveData<Event<Boolean>> deletedEvent = new MutableLiveData<>();

    private final MutableLiveData<Event<Integer>> messageEvent = new MutableLiveData<>();

    public ReportDetailViewModel(@NonNull ReportRepository repository) {
        this.repository = repository;
        this.report = Transformations.switchMap(reportId, repository::getReport);
        this.history = Transformations.switchMap(reportId, repository::getHistory);
    }

    public void load(long id) {
        Long current = reportId.getValue();
        if (current == null || current != id) {
            reportId.setValue(id);
        }
    }

    public LiveData<ReportEntity> getReport() {
        return report;
    }

    public LiveData<List<StatusHistoryEntity>> getHistory() {
        return history;
    }

    /** Fires once when the report was deleted — the Activity finishes. */
    public LiveData<Event<Boolean>> getDeletedEvent() {
        return deletedEvent;
    }

    /** One-shot toast message (success or failure, always a string res id). */
    public LiveData<Event<Integer>> getMessageEvent() {
        return messageEvent;
    }

    /** Simulated workflow transition (demo data — see strings detail_simulate_hint). */
    public void changeStatus(@NonNull ReportStatus newStatus) {
        Long id = reportId.getValue();
        if (id == null) return;
        repository.changeStatus(id, newStatus,
                () -> messageEvent.setValue(new Event<>(R.string.report_updated)),
                error -> messageEvent.setValue(new Event<>(R.string.state_error)));
    }

    public void delete() {
        Long id = reportId.getValue();
        if (id == null) return;
        repository.deleteReport(id,
                () -> deletedEvent.setValue(new Event<>(true)),
                error -> messageEvent.setValue(new Event<>(R.string.state_error)));
    }
}
