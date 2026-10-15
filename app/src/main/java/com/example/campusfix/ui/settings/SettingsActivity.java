package com.example.campusfix.ui.settings;

import android.Manifest;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campusfix.R;
import com.example.campusfix.data.alarm.ReminderReceiver;
import com.example.campusfix.data.repository.SettingsRepository;
import com.example.campusfix.data.sync.DemoSyncService;
import com.example.campusfix.data.sync.SyncWorker;
import com.example.campusfix.util.ConnectivityHelper;
import com.example.campusfix.util.NotificationHelper;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * Working settings: the theme control changes the app immediately and the
 * choice is re-applied by CampusFixApplication after a full restart; the
 * notification switch gates NotificationHelper (topic 08.1).
 *
 * The BACKGROUND & SYSTEM DEMO section wires up the system-integration
 * topics: connectivity status (07.2), a started Service (07.4), WorkManager
 * jobs (08.3), exact/inexact AlarmManager reminders (08.2), and the
 * Android 13+ notification runtime permission (08.1).
 */
public class SettingsActivity extends AppCompatActivity {

    private SettingsRepository settings;
    private boolean binding = true;
    private TextView demoStatus;

    /**
     * Registered at construction time (the Activity Result API requires the
     * call before the activity is STARTED).
     */
    private final ActivityResultLauncher<String> notificationPermission =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            postTestNotification();
                        } else {
                            showDemoStatus(R.string.demo_permission_needed);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new SettingsRepository(this);

        RadioGroup groupTheme =
                findViewById(R.id.groupTheme);
        MaterialSwitch switchNotifications =
                findViewById(R.id.switchNotifications);
        TextView status =
                findViewById(R.id.tvSettingsStatus);

        // Restore persisted state (binding flag avoids firing saves while restoring).
        binding = true;
        int mode = settings.getThemeMode();
        if (mode == SettingsRepository.THEME_LIGHT) {
            groupTheme.check(R.id.radioThemeLight);
        } else if (mode == SettingsRepository.THEME_DARK) {
            groupTheme.check(R.id.radioThemeDark);
        } else {
            groupTheme.check(R.id.radioThemeSystem);
        }
        switchNotifications.setChecked(
                settings.areNotificationsEnabled());
        binding = false;

        groupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (binding) return;

            if (checkedId == R.id.radioThemeLight) {
                settings.setThemeMode(SettingsRepository.THEME_LIGHT);
            } else if (checkedId == R.id.radioThemeDark) {
                settings.setThemeMode(SettingsRepository.THEME_DARK);
            } else {
                settings.setThemeMode(SettingsRepository.THEME_SYSTEM);
            }
            showStatus(getString(R.string.settings_theme) + " ✓");
        });

        switchNotifications.setOnCheckedChangeListener((button, checked) -> {
            if (binding) return;

            settings.setNotificationsEnabled(checked);
            showStatus(getString(checked
                    ? R.string.settings_notifications_on
                    : R.string.settings_notifications_off));
        });

        setupDemoSection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView connectivity = findViewById(R.id.tvConnectivityStatus);
        connectivity.setText(ConnectivityHelper.describe(this));
    }

    private void setupDemoSection() {
        demoStatus = findViewById(R.id.tvDemoStatus);

        findViewById(R.id.btnDemoNotification).setOnClickListener(v ->
                tryPostTestNotification());

        findViewById(R.id.btnDemoServiceSync).setOnClickListener(v -> {
            DemoSyncService.start(this);
            showDemoStatus(R.string.demo_service_started);
        });

        findViewById(R.id.btnDemoWorkerSync).setOnClickListener(v -> {
            SyncWorker.enqueueOneTime(this);
            showDemoStatus(R.string.demo_worker_enqueued);
        });

        findViewById(R.id.btnDemoSchedulePeriodic).setOnClickListener(v -> {
            SyncWorker.schedulePeriodic(this);
            showDemoStatus(R.string.demo_periodic_scheduled);
        });

        findViewById(R.id.btnDemoCancelPeriodic).setOnClickListener(v -> {
            SyncWorker.cancelPeriodic(this);
            showDemoStatus(R.string.demo_periodic_cancelled);
        });

        findViewById(R.id.btnDemoAlarm).setOnClickListener(v ->
                showDemoStatusText(
                        ReminderReceiver.schedule(this, 10_000L)));

        findViewById(R.id.btnDemoAlarmCancel).setOnClickListener(v ->
                showDemoStatusText(ReminderReceiver.cancel(this)));
    }

    private void tryPostTestNotification() {
        if (NotificationHelper.needsPermissionRequest(this)) {
            notificationPermission.launch(
                    Manifest.permission.POST_NOTIFICATIONS);
            return;
        }
        postTestNotification();
    }

    private void postTestNotification() {
        if (!settings.areNotificationsEnabled()) {
            showDemoStatus(R.string.demo_notifications_off);
            return;
        }
        boolean posted = NotificationHelper.notify(this,
                NotificationHelper.ID_TEST,
                getString(R.string.demo_test_notification_title),
                getString(R.string.demo_test_notification_body));
        showDemoStatus(posted
                ? R.string.demo_test_notification_ok
                : R.string.demo_permission_needed);
    }

    private void showStatus(String message) {
        TextView status = findViewById(R.id.tvSettingsStatus);
        status.setText(message);
    }

    private void showDemoStatus(int messageRes) {
        if (demoStatus != null) demoStatus.setText(messageRes);
    }

    private void showDemoStatusText(String message) {
        if (demoStatus != null) demoStatus.setText(message);
    }
}
