package com.example.campusfix.util;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.campusfix.R;
import com.example.campusfix.data.repository.SettingsRepository;
import com.example.campusfix.ui.dashboard.DashboardActivity;

/**
 * Notification channel + builder plumbing (course topic 08.1).
 *
 * Posting respects three gates, in order: the user's notification switch in
 * Settings (SharedPreferences), the POST_NOTIFICATIONS runtime permission on
 * Android 13+, and the channel existing (mandatory since Android 8).
 * {@link #notify} returns false when any gate blocks, so callers can explain
 * exactly why nothing appeared instead of failing silently.
 */
public final class NotificationHelper {

    public static final String CHANNEL_ID = "campusfix_updates";

    /** Notification ids: stable so a repeat replaces instead of stacking. */
    public static final int ID_TEST = 1001;
    public static final int ID_SYNC = 1002;
    public static final int ID_REMINDER = 1003;
    public static final int ID_STATUS_BASE = 2000;

    private NotificationHelper() {
    }

    /** True when the app may post notifications (permission + settings). */
    public static boolean canPost(Context context) {
        if (!new SettingsRepository(context).areNotificationsEnabled()) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /** True when the Android 13+ runtime permission is still missing. */
    public static boolean needsPermissionRequest(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED;
    }

    /** Creates the channel (Android 8+) and posts; false when blocked. */
    public static boolean notify(Context context, int id,
                                 String title, String body) {
        if (!canPost(context)) return false;

        NotificationManager manager =
                context.getSystemService(NotificationManager.class);
        if (manager == null) return false;
        ensureChannel(manager, context);

        // The tap opens the dashboard — PendingIntent.FLAG_IMMUTABLE is
        // required since API 31 (topic 08.1).
        Intent tap = new Intent(context, DashboardActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pending = PendingIntent.getActivity(context, id, tap,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Small icon must be a silhouette: the system flattens and tints it.
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setAutoCancel(true)
                        .setContentIntent(pending)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(id, builder.build());
        return true;
    }

    private static void ensureChannel(NotificationManager manager,
                                      Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notif_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(
                context.getString(R.string.notif_channel_description));
        manager.createNotificationChannel(channel);
    }
}
