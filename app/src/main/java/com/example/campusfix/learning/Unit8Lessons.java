package com.example.campusfix.learning;

import java.util.List;

/** Unit 8 — course files 08.1 … 08.3 (notifications, alarms, JobScheduler/WorkManager). */
final class Unit8Lessons {

    private static final String UNIT = "Unit 8 — Notifications, Alarms & Scheduling";

    private Unit8Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "08.1",
                        "Notifications",
                        UNIT,
                        "Channels, building a notification, and the POST_NOTIFICATIONS runtime "
                                + "permission.",
                        "A notification is a message in the system tray, shown outside your app's "
                                + "UI. Building one involves a channel (Android 8+), a builder, and "
                                + "a PendingIntent:\n"
                                + "```java\n"
                                + "NotificationManager nm = ctx.getSystemService(NotificationManager.class);\n"
                                + "if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {\n"
                                + "    NotificationChannel ch = new NotificationChannel(\n"
                                + "            CHANNEL_ID, \"Report reminders\",\n"
                                + "            NotificationManager.IMPORTANCE_DEFAULT);\n"
                                + "    ch.setDescription(\"Reminders about unresolved reports\");\n"
                                + "    nm.createNotificationChannel(ch);\n"
                                + "}\n"
                                + "Notification n = new NotificationCompat.Builder(ctx, CHANNEL_ID)\n"
                                + "        .setSmallIcon(R.drawable.ic_notification)\n"
                                + "        .setContentTitle(\"Report updated\")\n"
                                + "        .setContentText(\"Wi-Fi issue is now In Progress\")\n"
                                + "        .setAutoCancel(true)\n"
                                + "        .setContentIntent(PendingIntent.getActivity(ctx, 0,\n"
                                + "                detailIntent, PendingIntent.FLAG_IMMUTABLE))\n"
                                + "        .build();\n"
                                + "nm.notify(reportId, n);   // tag-less id → updating same id replaces it\n"
                                + "```\n\n"
                                + "Key rules:\n"
                                + "* **Channels** are mandatory on API 26+: users see them in system "
                                + "settings and can mute them individually; the app cannot remove one "
                                + "after creation (deleteNotificationChannel exists but is for full "
                                + "migration). Group notifications by purpose (CampusFix: one channel "
                                + "for reminders).\n"
                                + "* **PendingIntent flags** — FLAG_IMMUTABLE (or FLAG_MUTABLE) is "
                                + "required on API 31+. A pending intent must wrap an Activity, "
                                + "Service or BroadcastReceiver Intent so the tap can run your code.\n"
                                + "* **POST_NOTIFICATIONS runtime permission** — on Android 13+ you "
                                + "must request it at runtime; on older versions install-time is "
                                + "enough. Declaring it in the manifest is the first step either way.\n"
                                + "* The notify id decides replacement: reusing a report's id updates "
                                + "the existing notification instead of stacking new ones — great "
                                + "for 'progress' style updates.\n"
                                + "* setAutoCancel(true) removes the notification on tap; ongoing "
                                + "notifications (setOngoing) cannot be swiped away — reserve them "
                                + "for foreground services.\n\n"
                                + "Good content: short title, one-line body, correct icon (a plain "
                                + "silhouette — coloured status bar icons are flattened by the "
                                + "system), and a content intent that deep-links straight to the "
                                + "relevant screen.",
                        "Add POST_NOTIFICATIONS to the manifest and request it at runtime with "
                                + "ActivityCompat.requestPermissions on a practice screen (guard the "
                                + "request to SDK 33+). Then build a notification for a fixed report "
                                + "id twice and confirm the second replaces the first.",
                        Quiz.list(
                                Quiz.of(
                                        "Why must notifications have a channel on Android 8+?",
                                        1,
                                        "Users manage channels individually in system settings; the "
                                                + "app cannot post to an id that has no channel.",
                                        "Channels store data",
                                        "User-visible categories for per-channel control",
                                        "To save memory",
                                        "Channels replace the manifest"),
                                Quiz.of(
                                        "What does FLAG_IMMUTABLE guarantee about a PendingIntent?",
                                        2,
                                        "The intent cannot be altered by another app before it fires — "
                                                + "a security requirement since API 31.",
                                        "The app cannot cancel it",
                                        "It survives reboot",
                                        "It cannot be modified by receiving apps",
                                        "It is faster"),
                                Quiz.of(
                                        "On Android 13+, what must happen before a notification "
                                                + "appears?",
                                        0,
                                        "POST_NOTIFICATIONS is a runtime permission — you must request "
                                                + "it and the user must grant it.",
                                        "The app must be in foreground",
                                        "User grants POST_NOTIFICATIONS at runtime",
                                        "Nothing — it always works",
                                        "A channel must be deleted")),
                        "W"),

                new Lesson(
                        "08.2",
                        "Alarms",
                        UNIT,
                        "AlarmManager for exact-time wakeups, Doze, and the permission rules.",
                        "AlarmManager schedules an Intent at a wall-clock time — the classic way to "
                                + "wake your app at a specific moment (a reminder at 09:00):\n"
                                + "```java\n"
                                + "AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);\n"
                                + "Intent i = new Intent(ctx, AlarmReceiver.class)\n"
                                + "        .putExtra(\"reportId\", 42);\n"
                                + "PendingIntent pi = PendingIntent.getBroadcast(ctx, 42, i,\n"
                                + "        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);\n"
                                + "am.setExactAndAllowWhileIdle(\n"
                                + "        AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);\n"
                                + "```\n\n"
                                + "Choosing the method matters:\n"
                                + "* set() — inexact, batched by the system: fine for 'roughly "
                                + "today', the most battery-friendly.\n"
                                + "* setExact() — precise, but subject to Doze restrictions.\n"
                                + "* setExactAndAllowWhileIdle() — precise *and* fires in Doze, but "
                                + "throttled to roughly once per app per 9 minutes while idle.\n"
                                + "* setWindow() — a grace window (e.g. within 15 minutes) so the "
                                + "system can batch near your time.\n\n"
                                + "Permissions changed over time: SCHEDULE_EXACT_ALARM is a "
                                + "special-access permission on API 31+ (granted by default for most "
                                + "apps, but check canScheduleExactAlarms() before setExact); "
                                + "USE_EXACT_ALARM is restricted to alarm/calendar apps on Play; "
                                + "SET_ALARM (for the clock's own alarm-clock intent) needs no "
                                + "permission.\n\n"
                                + "Doze (API 23+) suspends network and defers alarms when the device "
                                + "is unplugged, still and screen-off — which is exactly why "
                                + "WhileIdle variants exist. If your reminder tolerates a delay, use "
                                + "the batched API and save battery; if it must be exact (a class "
                                + "start time), use setExactAndAllowWhileIdle sparingly.\n\n"
                                + "Alarms do not survive reboot — re-register on "
                                + "ACTION_BOOT_COMPLETED (a manifest receiver), or use AlarmManager's "
                                + "or WorkManager's persisted scheduling. CampusFix demonstrates an "
                                + "in-app reminder that fires while the app runs; a background "
                                + "reminder would combine this receiver with POST_NOTIFICATIONS from "
                                + "topic 08.1.",
                        "Schedule an exact alarm that fires a broadcast 30 seconds from now, log "
                                + "its receipt, then cancel it with am.cancel(pi) and verify nothing "
                                + "arrives. List which alarm method you would pick for each of: a "
                        + "class start reminder, a daily sync, and a per-minute UI tick (explain each choice).",
                        Quiz.list(
                                Quiz.of(
                                        "Which alarm method fires precisely even in Doze?",
                                        1,
                                        "setExactAndAllowWhileIdle() is the only variant designed to "
                                                + "run during idle/Doze (throttled).",
                                        "set()",
                                        "setExactAndAllowWhileIdle()",
                                        "setInexactRepeating()",
                                        "Handler.postDelayed"),
                                Quiz.of(
                                        "What is Android's Doze?",
                                        0,
                                        "A deep-idle power mode that defers alarms, jobs and network "
                                                + "for stationary, unplugged devices.",
                                        "A memory optimizer",
                                        "Idle mode that defers background work",
                                        "A do-not-disturb mode",
                                        "A CPU governor"),
                                Quiz.of(
                                        "Why must alarms be re-registered after a device reboot?",
                                        2,
                                        "Alarms live in memory only — reboot clears them, so a "
                                                + "BOOT_COMPLETED receiver must reschedule.",
                                        "Apps are cleared on reboot",
                                        "The user resets them",
                                        "Alarms are stored in RAM, not persisted",
                                        "PendingIntents are invalid")),
                        "W"),

                new Lesson(
                        "08.3",
                        "Efficient data transfer and JobScheduler",
                        UNIT,
                        "Batching work with constraints — JobScheduler's model and WorkManager's "
                        + "modern wrapper.",
                        "Efficiency means not doing work at the wrong time: a job scheduler groups "
                                + "tasks and runs them when conditions (constraints) are met — "
                                + "charging, unmetered network, idle. Google's JobScheduler (API 21+) "
                                + "is the platform API underneath:\n"
                                + "```java\n"
                                + "JobInfo job = new JobInfo.Builder(JOB_ID, serviceComponent)\n"
                                + "        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)\n"
                                + "        .setRequiresCharging(true)\n"
                                + "        .setPeriodic(15 * 60 * 1000L)\n"
                                + "        .build();\n"
                                + "JobScheduler js = ctx.getSystemService(JobScheduler.class);\n"
                                + "js.schedule(job);\n"
                                + "```\n"
                                + "The system coalesces jobs across apps and runs them in batches — "
                                + "hundreds of identical wakeups become one radio-on period, which is "
                                + "where the battery savings come from.\n\n"
                                + "JobScheduler's callbacks are awkward (a JobService with "
                                + "jobFinished, API-level differences, no backoff tuning before "
                                + "API 26), so Google shipped **WorkManager** as the recommended "
                                + "high-level API — same engine, better ergonomics:\n"
                                + "```java\n"
                                + "Constraints c = new Constraints.Builder()\n"
                                + "        .setRequiredNetworkType(NetworkType.UNMETERED)\n"
                                + "        .setRequiresCharging(true)\n"
                                + "        .build();\n"
                                + "PeriodicWorkRequest w = new PeriodicWorkRequest.Builder(\n"
                                + "        SyncWorker.class, 6, TimeUnit.HOURS)\n"
                                + "        .setConstraints(c)\n"
                                + "        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)\n"
                                + "        .build();\n"
                                + "WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(\n"
                                + "        \"campusfix_sync\", ExistingPeriodicWorkPolicy.KEEP, w);\n"
                                + "```\n"
                                + "WorkManager persists across reboots, retries with backoff, and "
                                + "supports unique work so repeated calls cannot stack duplicate "
                                + "jobs.\n\n"
                                + "Also in 'efficient data transfer': batch your own requests, "
                                + "compress payloads, use one connection instead of ten, prefer "
                                + "pull-then-push ordering, and defer non-urgent uploads until "
                                + "unmetered. Combine with the Java Executor pattern from 07.1 for "
                                + "the actual execution.\n\n"
                                + "Note the deprecation rule from M4's checklist: JobScheduler itself "
                                + "is *not* deprecated, but WorkManager is the modern choice for "
                                + "most apps — and Android 15's JobService grounds-condition changes "
                                + "make the migrate-even-sooner argument stronger.",
                        "Enqueue a unique periodic WorkManager task (e.g. every 15 minutes) with an "
                                + "unmetered-network constraint, log its first run, then call "
                                + "WorkManager.getInstance(ctx).cancelUniqueWork(...) and confirm "
                                + "it stops. Explain why enqueueUniquePeriodicWork is safer than enqueue.",
                        Quiz.list(
                                Quiz.of(
                                        "What are 'constraints' in job scheduling?",
                                        0,
                                        "Conditions (network type, charging, idle) that must hold "
                                                + "before the job runs.",
                                        "Job priority levels",
                                        "Conditions gating execution (charging/network)",
                                        "Retry counts",
                                        "Thread limits"),
                                Quiz.of(
                                        "Why is WorkManager preferred over raw JobScheduler?",
                                        1,
                                        "It wraps JobScheduler/AlarmManager with persistence, backoff "
                                                + "and a cleaner API that works back to older devices.",
                                        "It is written in Kotlin only",
                                        "Better API, persistence, backoff; back-compat",
                                        "It runs on the main thread",
                                        "It needs no permissions"),
                                Quiz.of(
                                        "What does ExistingPeriodicWorkPolicy.KEEP do?",
                                        0,
                                        "Ignores the new request if a unique work chain with that name "
                                                + "already exists — preventing duplicates.",
                                        "Keeps the app awake",
                                        "Keeps work after reboot only",
                                        "Deletes old work and enqueues",
                                        "Pauses the work")),
                        "W"));
    }
}
