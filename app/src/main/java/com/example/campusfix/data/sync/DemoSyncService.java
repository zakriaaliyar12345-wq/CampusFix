package com.example.campusfix.data.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.campusfix.R;
import com.example.campusfix.util.AppExecutors;
import com.example.campusfix.util.NotificationHelper;

/**
 * Demo background sync Service (course topic 07.4).
 *
 * Started with startService() from a foreground screen, it performs three
 * short steps on an executor (never on the main thread — onStartCommand
 * itself runs there), posts a completion notification, and then calls
 * stopSelf(startId) so the service never lingers.
 *
 * This is the "user-visible, short-lived" shape of background work. For
 * deferrable, guaranteed work with constraints, see {@link SyncWorker}.
 */
public class DemoSyncService extends Service {

    public static final String EXTRA_LOG = "extra_log";

    public static void start(Context context) {
        context.startService(new Intent(context, DemoSyncService.class));
    }

    public static void cancel(Context context) {
        context.stopService(new Intent(context, DemoSyncService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Heavy work belongs on a worker thread; post it there.
        AppExecutors.diskIO().execute(() -> {
            runSteps();
            AppExecutors.mainThread(() -> {
                NotificationHelper.notify(
                        this,
                        NotificationHelper.ID_SYNC,
                        getString(R.string.demo_service_title),
                        getString(R.string.demo_service_body));
                // Stop only this start request — a newer one keeps running.
                stopSelf(startId);
            });
        });

        // Not sticky: a demo sync is not worth recreating after a kill.
        return START_NOT_STICKY;
    }

    private void runSteps() {
        for (int step = 1; step <= 3; step++) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // started, unbound service
    }
}
