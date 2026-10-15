package com.example.campusfix.ui.report;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.R;
import com.example.campusfix.data.broadcast.ReportStatusReceiver;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;
import com.example.campusfix.data.model.ReportStatus;
import com.example.campusfix.ui.ViewModelFactory;
import com.example.campusfix.ui.adapter.StatusHistoryAdapter;
import com.example.campusfix.ui.viewmodel.ReportDetailViewModel;
import com.example.campusfix.util.AppExecutors;
import com.example.campusfix.util.DateUtils;
import com.example.campusfix.util.PhotoUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Full view of one report with its status timeline and actions.
 *
 * Course topics demonstrated: 02.1 (explicit intent to the editor),
 * 02.3 (implicit intents: ACTION_SEND share, ACTION_VIEW geo map), 04.1
 * (clickable buttons), 04.5 (RecyclerView timeline), 10.1 (LiveData observed
 * across the report, history and one-shot events).
 */
public class ReportDetailActivity extends AppCompatActivity {

    public static final String EXTRA_REPORT_ID = "extra_report_id";

    private static final int REQ_EDIT = 201;

    private ReportDetailViewModel viewModel;
    private StatusHistoryAdapter historyAdapter;

    private TextView tvDetailStatus;
    private TextView tvDetailSampleBadge;
    private TextView tvDetailTitle;
    private TextView tvDetailCategoryPriority;
    private TextView tvDetailCreated;
    private TextView tvDetailUpdated;
    private TextView tvDetailLocation;
    private TextView tvDetailDescription;
    private ImageView ivDetailPhoto;
    private TextView tvDetailNoPhoto;
    private TextView tvHistoryEmpty;
    private MaterialButton btnEditReport;

    private boolean bound;
    private String loadedPhotoUri;

    private final DateFormat dateTimeFormat =
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    public static void launch(@NonNull Context context, long reportId) {
        context.startActivity(new Intent(context, ReportDetailActivity.class)
                .putExtra(EXTRA_REPORT_ID, reportId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        long reportId = getIntent().getLongExtra(EXTRA_REPORT_ID, -1L);

        viewModel = new ViewModelProvider(this,
                ViewModelFactory.getInstance(
                        (CampusFixApplication) getApplication()))
                .get(ReportDetailViewModel.class);

        bindViews();
        setupHistoryList();
        setupActions(reportId);
        observe();
        viewModel.load(reportId);
    }

    private void bindViews() {
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailSampleBadge = findViewById(R.id.tvDetailSampleBadge);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailCategoryPriority = findViewById(R.id.tvDetailCategoryPriority);
        tvDetailCreated = findViewById(R.id.tvDetailCreated);
        tvDetailUpdated = findViewById(R.id.tvDetailUpdated);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        ivDetailPhoto = findViewById(R.id.ivDetailPhoto);
        tvDetailNoPhoto = findViewById(R.id.tvDetailNoPhoto);
        tvHistoryEmpty = findViewById(R.id.tvHistoryEmpty);
        btnEditReport = findViewById(R.id.btnEditReport);
    }

    private void setupHistoryList() {
        historyAdapter = new StatusHistoryAdapter();
        RecyclerView recycler = findViewById(R.id.recyclerHistory);
        recycler.setAdapter(historyAdapter);
    }

    private void setupActions(long reportId) {
        btnEditReport.setOnClickListener(v ->
                CreateReportActivity.launch(this, reportId));

        findViewById(R.id.btnSimulateStatus).setOnClickListener(v ->
                showStatusPicker());

        findViewById(R.id.btnShareReport).setOnClickListener(v -> shareReport());

        findViewById(R.id.btnOpenMap).setOnClickListener(v -> openMap());

        findViewById(R.id.btnDeleteReport).setOnClickListener(v -> confirmDelete());
    }

    private void observe() {
        viewModel.getReport().observe(this, report -> {
            if (report == null) {
                // Transient null while Room loads is fine; a null AFTER a
                // successful bind means the row vanished (deleted elsewhere).
                if (bound) {
                    Toast.makeText(this, R.string.detail_not_found,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
            bound = true;
            render(report);
        });

        viewModel.getHistory().observe(this, history -> {
            historyAdapter.submitList(history == null
                    ? new ArrayList<>() : history);
            boolean empty = history == null || history.isEmpty();
            tvHistoryEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        });

        viewModel.getDeletedEvent().observe(this, event -> {
            Boolean deleted = event.getContentIfNotHandled();
            if (Boolean.TRUE.equals(deleted)) {
                Toast.makeText(this, R.string.report_deleted,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getMessageEvent().observe(this, event -> {
            Integer messageRes = event.getContentIfNotHandled();
            if (messageRes != null) {
                Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------------------------------------------------------------- render

    private void render(ReportEntity report) {
        tvDetailStatus.setText(report.status.labelRes);
        tintPill(tvDetailStatus, ContextCompat.getColor(this, report.status.colorRes));
        tvDetailSampleBadge.setVisibility(report.isSample ? View.VISIBLE : View.GONE);

        tvDetailTitle.setText(report.title);
        tvDetailCategoryPriority.setText(
                getString(report.category.labelRes)
                        + "  •  "
                        + getString(report.priority.labelRes));
        tvDetailCreated.setText(getString(R.string.detail_created,
                DateUtils.formatDateTime(dateTimeFormat, report.createdAt)));
        tvDetailUpdated.setText(getString(R.string.detail_updated,
                DateUtils.formatDateTime(dateTimeFormat, report.updatedAt)));
        tvDetailLocation.setText(report.location);
        tvDetailDescription.setText(report.description);

        renderPhoto(report.photoUri);
    }

    private void tintPill(TextView view, int color) {
        GradientDrawable background = (GradientDrawable) view.getBackground().mutate();
        background.setColor(color);
        view.setBackground(background);
    }

    private void renderPhoto(@Nullable String uri) {
        if (uri == null || uri.isEmpty()) {
            loadedPhotoUri = null;
            ivDetailPhoto.setVisibility(View.GONE);
            tvDetailNoPhoto.setVisibility(View.VISIBLE);
            return;
        }
        if (uri.equals(loadedPhotoUri) && ivDetailPhoto.getVisibility() == View.VISIBLE) {
            return; // already displayed
        }
        loadedPhotoUri = uri;
        ivDetailPhoto.setVisibility(View.VISIBLE);
        tvDetailNoPhoto.setVisibility(View.GONE);
        AppExecutors.diskIO().execute(() -> {
            Bitmap bitmap = PhotoUtils.decodeSampled(this, uri);
            AppExecutors.mainThread(() -> {
                if (bitmap != null && uri.equals(loadedPhotoUri)) {
                    ivDetailPhoto.setImageBitmap(bitmap);
                } else if (bitmap == null) {
                    loadedPhotoUri = null;
                    ivDetailPhoto.setVisibility(View.GONE);
                    tvDetailNoPhoto.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    // -------------------------------------------------------------- actions

    private void showStatusPicker() {
        ReportEntity current = viewModel.getReport().getValue();
        if (current == null) return;

        final List<ReportStatus> options = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        int checked = 0;
        for (ReportStatus status : ReportStatus.values()) {
            if (status == current.status) continue;
            if (options.isEmpty()) checked = 0;
            options.add(status);
            labels.add(getString(status.labelRes));
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.detail_simulate_status)
                .setSingleChoiceItems(
                        labels.toArray(new String[0]), checked,
                        (dialog, which) -> {
                            ReportStatus newStatus = options.get(which);
                            viewModel.changeStatus(newStatus);
                            // Topic 07.3: package-restricted in-app broadcast.
                            // ReportStatusReceiver (manifest-registered) turns
                            // it into a notification when the switch allows.
                            ReportEntity latest =
                                    viewModel.getReport().getValue();
                            if (latest != null) {
                                sendBroadcast(
                                        ReportStatusReceiver
                                                .newStatusChangedIntent(
                                                        this,
                                                        latest.id,
                                                        getString(newStatus
                                                                .labelRes)));
                            }
                            dialog.dismiss();
                        })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void shareReport() {
        ReportEntity report = viewModel.getReport().getValue();
        if (report == null) return;

        String text = report.title
                + "\n" + report.location
                + "\n\n" + report.description;
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(send,
                    getString(R.string.action_share)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.state_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openMap() {
        ReportEntity report = viewModel.getReport().getValue();
        if (report == null || report.location.isEmpty()) return;

        Uri geo = Uri.parse("geo:0,0?q=" + Uri.encode(report.location));
        Intent map = new Intent(Intent.ACTION_VIEW, geo);
        try {
            startActivity(map);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.state_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.detail_delete_title)
                .setMessage(R.string.detail_delete_body)
                .setPositiveButton(R.string.action_delete,
                        (dialog, which) -> viewModel.delete())
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
