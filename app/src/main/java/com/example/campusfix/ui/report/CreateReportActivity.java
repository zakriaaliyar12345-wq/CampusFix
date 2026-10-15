package com.example.campusfix.ui.report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.campusfix.CampusFixApplication;
import com.example.campusfix.R;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.ui.ViewModelFactory;
import com.example.campusfix.ui.viewmodel.ReportEditorViewModel;
import com.example.campusfix.util.AppExecutors;
import com.example.campusfix.util.PhotoUtils;
import com.example.campusfix.util.Validator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Create and edit screen for a campus report.
 *
 * Course topics demonstrated: 01.3 (long text in a scrolling view), 02.1
 * (started for result from the list), 02.3 (implicit intents: camera via
 * ACTION_IMAGE_CAPTURE + FileProvider, gallery via ACTION_GET_CONTENT),
 * 04.1/04.2 (buttons, text fields, exposed dropdown, choice chips), 09.1
 * (validation state kept in the ViewModel, not SharedPreferences — see
 * SettingsRepository for the SharedPreferences side).
 */
public class CreateReportActivity extends AppCompatActivity {

    public static final String EXTRA_REPORT_ID = "extra_report_id";
    public static final long NEW_REPORT = -1L;

    private static final int REQ_TAKE_PHOTO = 101;
    private static final int REQ_PICK_PHOTO = 102;

    private ReportEditorViewModel viewModel;

    private TextInputLayout tilTitle;
    private TextInputLayout tilLocation;
    private TextInputLayout tilDescription;
    private TextInputEditText etTitle;
    private TextInputEditText etLocation;
    private TextInputEditText etDescription;
    private AutoCompleteTextView actvCategory;
    private ChipGroup chipGroupPriority;
    private ImageView ivPhotoPreview;
    private MaterialButton btnTakePhoto;
    private MaterialButton btnPickPhoto;
    private MaterialButton btnRemovePhoto;
    private MaterialButton btnSave;

    private ReportCategory selectedCategory = ReportCategory.OTHER;
    private String photoUri;
    private String pendingCaptureUri;
    private boolean dirty;
    private boolean prefillDone;

    public static void launch(@NonNull Context context, long reportId) {
        context.startActivity(new Intent(context, CreateReportActivity.class)
                .putExtra(EXTRA_REPORT_ID, reportId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        long reportId = getIntent().getLongExtra(EXTRA_REPORT_ID, NEW_REPORT);

        viewModel = new ViewModelProvider(this,
                ViewModelFactory.getInstance(
                        (CampusFixApplication) getApplication()))
                .get(ReportEditorViewModel.class);
        viewModel.start(reportId);

        bindViews();
        setupTitle(reportId);
        setupCategoryDropdown();
        setupPriorityChips();
        setupPhotoButtons();
        setupSaveButton();
        setupBackHandling();
        observe();
    }

    private void bindViews() {
        tilTitle = findViewById(R.id.tilTitle);
        tilLocation = findViewById(R.id.tilLocation);
        tilDescription = findViewById(R.id.tilDescription);
        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        actvCategory = findViewById(R.id.actvCategory);
        chipGroupPriority = findViewById(R.id.chipGroupPriority);
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnPickPhoto = findViewById(R.id.btnPickPhoto);
        btnRemovePhoto = findViewById(R.id.btnRemovePhoto);
        btnSave = findViewById(R.id.btnSave);

        TextWatcher dirtyWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dirty = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        etTitle.addTextChangedListener(dirtyWatcher);
        etLocation.addTextChangedListener(dirtyWatcher);
        etDescription.addTextChangedListener(dirtyWatcher);
    }

    private void setupTitle(long reportId) {
        boolean edit = reportId > 0;
        int titleRes = edit ? R.string.edit_title : R.string.create_title;
        ((android.widget.TextView) findViewById(R.id.tvFormTitle)).setText(titleRes);
        setTitle(titleRes);
    }

    private void setupCategoryDropdown() {
        ReportCategory[] values = ReportCategory.values();
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            labels[i] = getString(values[i].labelRes);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, labels);
        actvCategory.setAdapter(adapter);
        actvCategory.setText(labels[selectedCategory.ordinal()], false);
        actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = values[position];
            dirty = true;
        });
    }

    private void setupPriorityChips() {
        chipGroupPriority.check(R.id.chipPriorityMedium);
        chipGroupPriority.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                group.check(R.id.chipPriorityMedium);
                return;
            }
            dirty = true;
        });
    }

    private void setupPhotoButtons() {
        btnTakePhoto.setOnClickListener(v -> launchCamera());
        btnPickPhoto.setOnClickListener(v -> launchGallery());
        btnRemovePhoto.setOnClickListener(v -> clearPhoto());
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> doSave());
    }

    /** Discard-changes guard (topic 02.2: user leaves with unsaved state). */
    private void setupBackHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!dirty) {
                    finish();
                    return;
                }
                new MaterialAlertDialogBuilder(CreateReportActivity.this)
                        .setTitle(R.string.discard_changes_title)
                        .setMessage(R.string.discard_changes_body)
                        .setPositiveButton(R.string.discard_changes_confirm,
                                (dialog, which) -> finish())
                        .setNegativeButton(R.string.action_cancel, null)
                        .show();
            }
        });
    }

    private void observe() {
        viewModel.getReport().observe(this, report -> {
            if (viewModel.isEditMode() && report != null && !prefillDone) {
                prefill(report);
            }
        });

        viewModel.isSaving().observe(this, saving -> {
            boolean busy = Boolean.TRUE.equals(saving);
            btnSave.setEnabled(!busy);
            btnSave.setText(busy ? R.string.saving : R.string.save_report);
        });

        viewModel.getSavedEvent().observe(this, event -> {
            Long id = event.getContentIfNotHandled();
            if (id != null) {
                boolean edit = viewModel.isEditMode();
                setResult(Activity.RESULT_OK);
                showToast(edit ? R.string.report_updated : R.string.report_saved);
                finish();
            }
        });

        viewModel.getErrorMessageEvent().observe(this, event -> {
            Integer messageRes = event.getContentIfNotHandled();
            if (messageRes != null) showToast(messageRes);
        });
    }

    private void prefill(ReportEntity report) {
        prefillDone = true;

        etTitle.setText(report.title);
        etLocation.setText(report.location);
        etDescription.setText(report.description);

        selectedCategory = report.category;
        actvCategory.setText(getString(report.category.labelRes), false);

        switch (report.priority) {
            case LOW:
                chipGroupPriority.check(R.id.chipPriorityLow);
                break;
            case HIGH:
                chipGroupPriority.check(R.id.chipPriorityHigh);
                break;
            case CRITICAL:
                chipGroupPriority.check(R.id.chipPriorityCritical);
                break;
            default:
                chipGroupPriority.check(R.id.chipPriorityMedium);
                break;
        }

        if (report.photoUri != null && !report.photoUri.isEmpty()) {
            photoUri = report.photoUri;
            renderPhoto();
        }

        // Everything above was system-populated, not user input.
        dirty = false;
    }

    // ----------------------------------------------------------------- save

    private void doSave() {
        clearErrors();

        String title = textOf(etTitle);
        String description = textOf(etDescription);
        String location = textOf(etLocation);
        ReportPriority priority = selectedPriority();

        // Inline field validation first (fast feedback, no database work).
        String errorKey = Validator.validateReport(title, description, location);
        if (errorKey != null) {
            applyFieldError(errorKey);
            return;
        }

        String failure = viewModel.save(title, selectedCategory,
                description, location, priority, photoUri);
        if (failure != null) {
            applyFieldError(failure);
        }
    }

    private void clearErrors() {
        tilTitle.setError(null);
        tilLocation.setError(null);
        tilDescription.setError(null);
    }

    private void applyFieldError(String errorKey) {
        if (errorKey.startsWith("error_title")) {
            tilTitle.setError(getString(Validator.errorResOf(errorKey)));
            etTitle.requestFocus();
        } else if (errorKey.startsWith("error_description")) {
            tilDescription.setError(getString(Validator.errorResOf(errorKey)));
            etDescription.requestFocus();
        } else if (errorKey.startsWith("error_location")) {
            tilLocation.setError(getString(Validator.errorResOf(errorKey)));
            etLocation.requestFocus();
        } else {
            showToast(Validator.errorResOf(errorKey));
        }
    }

    private ReportPriority selectedPriority() {
        int checkedId = chipGroupPriority.getCheckedChipId();
        if (checkedId == R.id.chipPriorityLow) return ReportPriority.LOW;
        if (checkedId == R.id.chipPriorityHigh) return ReportPriority.HIGH;
        if (checkedId == R.id.chipPriorityCritical) return ReportPriority.CRITICAL;
        return ReportPriority.MEDIUM;
    }

    private String textOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString();
    }

    // ---------------------------------------------------------------- photos

    private void launchCamera() {
        pendingCaptureUri = PhotoUtils.newCaptureUri(this);
        if (pendingCaptureUri == null) {
            showToast(R.string.state_error);
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(pendingCaptureUri));
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Grant the permission to every app that can handle the capture.
        if (intent.resolveActivity(getPackageManager()) != null) {
            for (android.content.pm.ResolveInfo ri
                    : getPackageManager().queryIntentActivities(intent, 0)) {
                grantUriPermission(ri.activityInfo.packageName,
                        Uri.parse(pendingCaptureUri),
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(intent, REQ_TAKE_PHOTO);
        } else {
            showToast(R.string.state_error);
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.photo_pick)),
                    REQ_PICK_PHOTO);
        } catch (android.content.ActivityNotFoundException e) {
            showToast(R.string.state_error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQ_TAKE_PHOTO && pendingCaptureUri != null) {
            photoUri = pendingCaptureUri;
            pendingCaptureUri = null;
            onPhotoChosen();
        } else if (requestCode == REQ_PICK_PHOTO
                && data != null && data.getData() != null) {
            photoUri = data.getData().toString();
            onPhotoChosen();
        }
    }

    private void onPhotoChosen() {
        dirty = true;
        renderPhoto();
    }

    private void clearPhoto() {
        photoUri = null;
        pendingCaptureUri = null;
        dirty = true;
        ivPhotoPreview.setImageDrawable(null);
        ivPhotoPreview.setVisibility(View.GONE);
        btnRemovePhoto.setVisibility(View.GONE);
    }

    /** Decodes off the UI thread, then swaps the preview on the main thread. */
    private void renderPhoto() {
        if (photoUri == null) {
            clearPhoto();
            return;
        }
        final String uri = photoUri;
        ivPhotoPreview.setVisibility(View.VISIBLE);
        btnRemovePhoto.setVisibility(View.VISIBLE);
        AppExecutors.diskIO().execute(() -> {
            Bitmap bitmap = PhotoUtils.decodeSampled(this, uri);
            AppExecutors.mainThread(() -> {
                // Ignore stale results if the user replaced/removed the photo.
                if (bitmap != null && uri.equals(photoUri)) {
                    ivPhotoPreview.setImageBitmap(bitmap);
                } else if (bitmap == null) {
                    showToast(R.string.state_error);
                }
            });
        });
    }

    private void showToast(int messageRes) {
        android.widget.Toast.makeText(this, messageRes,
                android.widget.Toast.LENGTH_SHORT).show();
    }
}
