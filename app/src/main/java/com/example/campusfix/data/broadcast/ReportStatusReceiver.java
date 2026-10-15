package com.example.campusfix.data.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.campusfix.R;
import com.example.campusfix.util.NotificationHelper;

/**
 * Reacts to the in-app report-status broadcast (course topic 07.3) by
 * posting a notification (topic 08.1).
 *
 * The sender restricts the Intent with setPackage(getPackageName()), which
 * keeps the broadcast inside this app AND lets this manifest-declared
 * receiver wake up even when the app process was idle (an unrestricted
 * implicit broadcast would not, since Android 8). The receiver verifies the
 * action defensively — anyone can send an Intent with a matching action.
 */
public class ReportStatusReceiver extends BroadcastReceiver {

    public static final String ACTION_REPORT_STATUS_CHANGED =
            "com.example.campusfix.ACTION_REPORT_STATUS_CHANGED";
    public static final String EXTRA_REPORT_ID = "extra_report_id";
    public static final String EXTRA_STATUS_LABEL = "extra_status_label";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null
                || !ACTION_REPORT_STATUS_CHANGED.equals(intent.getAction())) {
            return;
        }

        long reportId = intent.getLongExtra(EXTRA_REPORT_ID, -1L);
        String statusLabel = intent.getStringExtra(EXTRA_STATUS_LABEL);
        if (reportId < 0) return;

        NotificationHelper.notify(
                context,
                NotificationHelper.ID_STATUS_BASE + (int) (reportId % 1000),
                context.getString(R.string.demo_broadcast_title),
                context.getString(R.string.demo_broadcast_body,
                        reportId,
                        statusLabel == null ? "" : statusLabel));
    }

    /** Builds the package-restricted broadcast sent by the detail screen. */
    public static Intent newStatusChangedIntent(Context context,
                                                long reportId,
                                                String statusLabel) {
        return new Intent(ACTION_REPORT_STATUS_CHANGED)
                .setPackage(context.getPackageName())
                .putExtra(EXTRA_REPORT_ID, reportId)
                .putExtra(EXTRA_STATUS_LABEL, statusLabel);
    }
}
