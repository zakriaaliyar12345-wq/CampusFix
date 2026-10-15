package com.example.campusfix.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Central place for threading.
 *
 * <ul>
 *   <li>{@link #diskIO()} — a single background thread for Room reads and
 *       writes (a serial executor also keeps write ordering predictable).</li>
 *   <li>{@link #mainThread(Runnable)} — posts back to the UI thread for
 *       view updates that are not already delivered by LiveData.</li>
 * </ul>
 *
 * This is the modern replacement for the deprecated AsyncTask (Lab topic
 * 07.1): plain executors have no lifecycle pitfalls and compose well with
 * LiveData/ViewModel.
 */
public final class AppExecutors {

    private static final Executor DISK_IO =
            Executors.newSingleThreadExecutor();

    private static final Handler MAIN =
            new Handler(Looper.getMainLooper());

    private AppExecutors() {
        // no instances
    }

    public static Executor diskIO() {
        return DISK_IO;
    }

    public static void mainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            MAIN.post(runnable);
        }
    }
}
