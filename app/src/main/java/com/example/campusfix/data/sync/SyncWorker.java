package com.example.campusfix.data.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.campusfix.R;
import com.example.campusfix.util.NotificationHelper;

import java.util.concurrent.TimeUnit;

/**
 * WorkManager job (course topic 08.3 — the modern successor to raw
 * JobScheduler). The work persists across reboots, respects constraints,
 * and is de-duplicated by name via enqueueUnique*Work.
 *
 * Both flavours used by the demo screen live here: a one-time request for
 * "run now", and a15-minute periodic request gated on unmetered network
 * and charging so it never burns mobile data.
 */
public class SyncWorker extends Worker {

    public static final String UNIQUE_PERIODIC = "campusfix_periodic_sync";

    public SyncWorker(@NonNull Context context,
                      @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper.notify(
                getApplicationContext(),
                NotificationHelper.ID_SYNC,
                getApplicationContext().getString(R.string.demo_worker_title),
                getApplicationContext().getString(R.string.demo_worker_body));
        // Success even when the notification was blocked: the job itself ran,
        // and the settings screen explains the notification gate.
        return Result.success();
    }

    /** Fire-and-forget job, runs as soon as the scheduler can. */
    public static void enqueueOneTime(@NonNull Context context) {
        OneTimeWorkRequest request =
                new OneTimeWorkRequest.Builder(SyncWorker.class)
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build())
                        .build();
        WorkManager.getInstance(context).enqueue(request);
    }

    /**
     * Unique periodic job: KEEP prevents duplicates if the button is tapped
     * twice; constraints keep it cheap (unmetered + charging).
     */
    public static void schedulePeriodic(@NonNull Context context) {
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(
                        SyncWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.UNMETERED)
                                .setRequiresCharging(true)
                                .build())
                        .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    public static void cancelPeriodic(@NonNull Context context) {
        WorkManager.getInstance(context)
                .cancelUniqueWork(UNIQUE_PERIODIC);
    }
}
