package com.example.campusfix.data.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.campusfix.R;
import com.example.campusfix.util.NotificationHelper;

/**
 * Fired by AlarmManager (course topic 08.2) when the demo reminder is due.
 *
 * The PendingIntent that starts this receiver names the component
 * explicitly, so no intent-filter or exported receiver is required — the
 * alarm delivery is already an explicit, app-internal broadcast.
 */
public class ReminderReceiver extends BroadcastReceiver {

    private static final int REQUEST_REMINDER = 42;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.notify(
                context,
                NotificationHelper.ID_REMINDER,
                context.getString(R.string.demo_alarm_title),
                context.getString(R.string.demo_alarm_body));
    }

    /**
     * Schedules the reminder. Tries the precise-and-Doze-safe API first and
     * falls back gracefully when the SCHEDULE_EXACT_ALARM permission was
     * revoked on Android 12+ (or when the platform rejects it), mirroring
     * the permission rules taught in topic 08.2.
     *
     * @return a short status string describing what actually happened.
     */
    public static String schedule(Context context, long delayMillis) {
        AlarmManager manager =
                context.getSystemService(AlarmManager.class);
        if (manager == null) {
            return context.getString(R.string.demo_alarm_failed);
        }

        long triggerAt = System.currentTimeMillis() + delayMillis;
        PendingIntent pending = pendingIntent(context);

        try {
            manager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAt, pending);
            return context.getString(R.string.demo_alarm_scheduled_exact);
        } catch (SecurityException exactDenied) {
            // Android 12+: SCHEDULE_EXACT_ALARM can be revoked per app.
        }

        try {
            manager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending);
            return context.getString(R.string.demo_alarm_scheduled_exact);
        } catch (SecurityException denied) {
            // Inexact alarm needs no special permission — still fires, just
            // batched by the system.
            manager.set(AlarmManager.RTC_WAKEUP, triggerAt, pending);
            return context.getString(R.string.demo_alarm_scheduled_inexact);
        }
    }

    /** Cancels a previously scheduled demo reminder. */
    public static String cancel(Context context) {
        AlarmManager manager =
                context.getSystemService(AlarmManager.class);
        if (manager == null) {
            return context.getString(R.string.demo_alarm_failed);
        }
        manager.cancel(pendingIntent(context));
        return context.getString(R.string.demo_alarm_cancelled);
    }

    private static PendingIntent pendingIntent(Context context) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        return PendingIntent.getBroadcast(context, REQUEST_REMINDER, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
