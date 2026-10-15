package com.example.campusfix.learning;

import java.util.List;

/** Unit 7 — course files 07.1 … 07.4 (AsyncTask history, connectivity, broadcasts, services). */
final class Unit7Lessons {

    private static final String UNIT = "Unit 7 — Background Work & Connectivity";

    private Unit7Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "07.1",
                        "AsyncTask and AsyncTaskLoader",
                        UNIT,
                        "The historical background-work API — taught as deprecated, with its modern "
                                + "replacements and why the old one broke apps.",
                        "For years AsyncTask was the default way to leave the main thread: "
                                + "```java\n"
                                + "@Deprecated\n"
                                + "class SaveTask extends AsyncTask<ReportEntity, Void, Long> {\n"
                                + "    protected Long doInBackground(ReportEntity... rs) {\n"
                                + "        return db.reportDao().insert(rs[0]);   // background\n"
                                + "    }\n"
                                + "    protected void onPostExecute(Long id) {\n"
                                + "        Toast.makeText(ctx, \"Saved \" + id, Toast.LENGTH_SHORT).show();\n"
                                + "    }\n"
                                + "}\n"
                                + "new SaveTask().execute(report);\n"
                                + "```\n"
                                + "doInBackground runs on a worker thread; onPreExecute/"
                                + "onPostExecute run on the main thread. AsyncTaskLoader added "
                                + "lifecycle-aware reloading (onLoadFinished) for data that should "
                                + "survive rotation.\n\n"
                                + "Both are deprecated (API 30) for serious reasons you should "
                                + "understand:\n"
                                + "1. Orientation death — the task holds a reference to the Activity "
                                + "that started it; rotate mid-save and the callback touches a "
                                + "destroyed context: leaked Activity (memory), or "
                                + "IllegalStateException (crash) when the task completes.\n"
                                + "2. No cancellation guarantee — onCancelled may never run if the "
                                + "task already finished; parallel execution limits were confusing "
                                + "(SERIAL_EXECUTOR default).\n"
                                + "3. No retry, no constraint, no observability — no way to say 'only "
                                + "run on unmetered charging'.\n\n"
                                + "The modern replacements, all used or referenced in CampusFix:\n"
                                + "* **java.util.concurrent / ExecutorService + Handler** — what "
                                + "AppExecutors wraps: run on background, post result to the main "
                                + "looper via mainHandler.post(...). Explicit and lifecycle-safe if "
                                + "you guard the callback.\n"
                                + "* **Kotlin coroutines** (scope-based) — structured concurrency "
                                + "cancels work when the screen dies (the course is Java, so this "
                                + "is the natural next step when you learn Kotlin).\n"
                                + "* **WorkManager** — the recommended successor for deferrable, "
                                + "guaranteed work with constraints and retries (topic 08.3).\n"
                                + "* **LiveData/Room reactive queries** — data that updates itself "
                                + "instead of you pushing it into callbacks (topic 10.1).\n\n"
                                + "CampusFix therefore never calls AsyncTask: Room does its own "
                                + "background threading, AppExecutors handles one-shot tasks, and "
                                + "WorkManager schedules recurring syncs. Learn AsyncTask to read old "
                                + "code — not to write new code.",
                        "Rewrite this AsyncTask pattern in your notes using AppExecutors: move the "
                                + "database call to the executor and the Toast to mainHandler.post, "
                                + "then list two lifecycle bugs the rewrite eliminates.",
                        Quiz.list(
                                Quiz.of(
                                        "Why is AsyncTask deprecated for new code?",
                                        1,
                                        "It leaks Activities across rotation and offers no lifecycle "
                                                + "safety, retries, or constraints — modern APIs do it better.",
                                        "It is too slow",
                                        "It leaks activities and lacks lifecycle/constraint safety",
                                        "It only works in Kotlin",
                                        "It was never official"),
                                Quiz.of(
                                        "Which modern API replaces AsyncTask for guaranteed, "
                                                + "constrained background work?",
                                        2,
                                        "WorkManager runs deferrable work with constraints, retries "
                                                + "and persistence — the direct successor for most AsyncTask use.",
                                        "HandlerThread",
                                        "WorkManager",
                                        "SharedPreferences",
                                        "View.post"),
                                Quiz.of(
                                        "What does onPostExecute run on?",
                                        0,
                                        "The main (UI) thread — background results must be applied to "
                                                + "views only there.",
                                        "The main thread",
                                        "A background thread",
                                        "The binder thread",
                                        "Any thread")),
                        "D"),

                new Lesson(
                        "07.2",
                        "Internet connection",
                        UNIT,
                        "Permissions, checking connectivity, and calling web APIs safely.",
                        "Talking to the network needs three things: the permission, a connectivity "
                                + "check, and an off-main-thread HTTP client.\n\n"
                                + "1. Permission in AndroidManifest.xml:\n"
                                + "```xml\n"
                                + "<uses-permission android:name=\"android.permission.INTERNET\" />\n"
                                + "<uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />\n"
                                + "```\n"
                                + "INTERNET is install-time (no runtime prompt); ACCESS_NETWORK_STATE "
                                + "lets you query connectivity.\n\n"
                                + "2. Checking connectivity — modern and a classic API:\n"
                                + "```java\n"
                                + "ConnectivityManager cm = (ConnectivityManager)\n"
                                + "        ctx.getSystemService(Context.CONNECTIVITY_SERVICE);\n"
                                + "Network active = cm.getActiveNetwork();\n"
                                + "NetworkCapabilities caps = cm.getNetworkCapabilities(active);\n"
                                + "boolean online = caps != null\n"
                                + "        && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);\n"
                                + "```\n"
                                + "Treat 'connected' as a hint, not truth — captive portals lie. The "
                                + "only real proof is a successful request.\n\n"
                                + "3. Doing the request off the main thread. On modern APIs use "
                                + "HttpURLConnection or an OkHttp-style client inside an executor:\n"
                                + "```java\n"
                                + "AppExecutors.getInstance().diskIO().execute(() -> {\n"
                                + "    try {\n"
                                + "        String body = fetch(url);\n"
                                + "        mainHandler.post(() -> onSuccess(body));\n"
                                + "    } catch (IOException e) {\n"
                                + "        mainHandler.post(() -> onError(e));\n"
                                + "    }\n"
                                + "});\n"
                                + "```\n"
                                + "Calling the network on the main thread throws "
                                + "NetworkOnMainThreadException (since Honeycomb) — the framework "
                                + "refuses to let you freeze the UI that way.\n\n"
                                + "Design for flaky networks: timeouts, retries with backoff, caching, "
                                + "and clear empty/error states in the UI. Store responses when they "
                                + "are user data (offline-first), and always JSON-parse off the main "
                                + "thread too.\n\n"
                                + "CampusFix is offline-first by design: reports live in Room, so no "
                                + "screen requires the internet to function — a campus app for "
                                + "students should work in a basement lecture hall with no signal.",
                        "Add ACCESS_NETWORK_STATE to a practice project's manifest, write a "
                                + "isOnline() helper with getActiveNetworkCapabilities, and log its "
                                + "result while toggling airplane mode in the emulator.",
                        Quiz.list(
                                Quiz.of(
                                        "Which permission is required before any HTTP request?",
                                        1,
                                        "android.permission.INTERNET — install-time, declared in the "
                                                + "manifest; without it every connection fails.",
                                        "ACCESS_NETWORK_STATE",
                                        "INTERNET",
                                        "CAMERA",
                                        "WRITE_EXTERNAL_STORAGE"),
                                Quiz.of(
                                        "What happens if you open a URL on the main thread?",
                                        2,
                                        "NetworkOnMainThreadException — Android forbids blocking the UI "
                                                + "thread with network I/O.",
                                        "It silently fails",
                                        "It queues politely",
                                        "NetworkOnMainThreadException",
                                        "Nothing"),
                                Quiz.of(
                                        "Why is 'connected' not a guarantee the request will work?",
                                        0,
                                        "Captive portals and dead links report a connection yet fail at "
                                                + "the application layer — only a completed request proves "
                                                + "reachability.",
                                        "Connections are encrypted",
                                        "Captive portals / broken links can lie",
                                        "The emulator disables it",
                                        "ConnectivityManager is async")),
                        "W"),

                new Lesson(
                        "07.3",
                        "Broadcasts",
                        UNIT,
                        "Broadcast receivers: system-wide events, explicit vs implicit, and modern "
                                + "limits.",
                        "A BroadcastReceiver reacts to system-wide messages (broadcast Intents). The "
                                + "system or another app sends a message; any registered receiver "
                                + "wakes up and receives an Intent with an action string.\n\n"
                                + "Two ways to register:\n"
                                + "1. Manifest-declared (implicit) — the receiver exists without "
                                + "your app running; required for events like BOOT_COMPLETED:\n"
                                + "```xml\n"
                                + "<receiver android:name=\".BootReceiver\" android:exported=\"false\">\n"
                                + "    <intent-filter>\n"
                                + "        <action android:name=\"android.intent.action.BOOT_COMPLETED\" />\n"
                                + "    </intent-filter>\n"
                                + "</receiver>\n"
                                + "```\n"
                                + "2. Context-registered (explicit) — alive only while a component "
                                + "runs; use it for screen-scoped events:\n"
                                + "```java\n"
                                + "IntentFilter f = new IntentFilter(\"com.example.campusfix.REPORT_STATUS_CHANGED\");\n"
                                + "registerReceiver(statusReceiver, f, Context.RECEIVER_NOT_EXPORTED);\n"
                                + "// ... and in onDestroy:\n"
                                + "unregisterReceiver(statusReceiver);\n"
                                + "```\n"
                                + "Forgetting unregisterReceiver leaks the receiver and crashes the "
                                + "next registration — always pair them.\n\n"
                                + "Explicit vs implicit broadcasts: since Android 8, implicit "
                                + "manifest-declared broadcasts mostly do not fire (they woke every "
                                + "app and drained batteries). Your own custom actions between your "
                                + "own components are unaffected — send them with setPackage(getPackageName()) "
                                + "so they stay inside your app.\n\n"
                                + "CampusFix's status-change flow uses this pattern: when a report's "
                                + "status advances, an explicit in-app broadcast with action "
                                + "com.example.campusfix.ACTION_STATUS_CHANGED and extra "
                                + "com.example.campusfix.EXTRA_REPORT_ID notifies listeners; the "
                                + "receiver must check action.equals(intent.getAction()) before "
                                + "acting, and read extras defensively (getIntExtra(key, -1)) since "
                                + "any component could send a malformed intent. Send the broadcast "
                                + "with sendBroadcast(intent) and always package-restrict it.\n\n"
                                + "When a broadcast arrives you get a few seconds on the main thread "
                                + "— never do heavy work there; hand it to WorkManager or a service.",
                        "Register a context receiver for a custom action in a practice activity, "
                                + "send it from a button with sendBroadcast, verify the log, then "
                                + "deliberately skip unregisterReceiver and observe the leak warning "
                                + "on rotation (revert the change).",
                        Quiz.list(
                                Quiz.of(
                                        "When must unregisterReceiver be called?",
                                        1,
                                        "In the matching lifecycle teardown (e.g. onDestroy) — a "
                                                + "leaked receiver crashes on the next registration.",
                                        "Never",
                                        "When the receiver fires",
                                        "In onPause only",
                                        "Only for manifest receivers"),
                                Quiz.of(
                                        "Why do most implicit manifest broadcasts no longer wake apps "
                                                + "on Android 8+?",
                                        0,
                                        "They woke every installed app, killing batteries — the system "
                                                + "now restricts them to a whitelist.",
                                        "They are deprecated entirely",
                                        "Battery/broadcast-storm restrictions",
                                        "Google removed receivers",
                                        "Only because of permissions"),
                                Quiz.of(
                                        "How do you keep a custom broadcast inside your own app?",
                                        1,
                                        "setPackage(getPackageName()) package-restricts the Intent so "
                                                + "no other app receives it.",
                                        "android:exported=false on everything",
                                        "setPackage(getPackageName())",
                                        "Use a local variable",
                                        "Request a permission")),
                        "W"),

                new Lesson(
                        "07.4",
                        "Services",
                        UNIT,
                        "Background services, foreground services, and when WorkManager is better.",
                        "A Service is a component with no UI that performs long-running work in the "
                                + "background: the system creates it, and it runs on a worker thread "
                                + "of its own process unless you say otherwise.\n\n"
                                + "The lifecycle pairs with the four start modes:\n"
                                + "```java\n"
                                + "public class SyncService extends Service {\n"
                                + "    public int onStartCommand(Intent i, int flags, int startId) {\n"
                                + "        AppExecutors.getInstance().networkIO().execute(() -> {\n"
                                + "            doSync();\n"
                                + "            stopSelf(startId);   // done with this start request\n"
                                + "        });\n"
                                + "        return START_NOT_STICKY;\n"
                                + "    }\n"
                                + "    public IBinder onBind(Intent i) { return null; }  // unbound\n"
                                + "}\n"
                                + "```\n"
                                + "* startService()/startForegroundService() → onStartCommand per "
                                + "request; the service stops itself with stopSelf(startId) or is "
                                + "stopped with stopService().\n"
                                + "* bindService() gives clients an IBinder for request/response "
                                + "calls; when all clients unbind, onUnbind runs.\n"
                                + "* onCreate/onDestroy bookend the instance either way.\n\n"
                                + "Android 8+ background-execution limits mean a started service may "
                                + "be killed seconds after launch unless it becomes a **foreground "
                                + "service** with a persistent notification (topic 08.1). That is "
                                + "why music, navigation and fitness apps show a notification — it "
                                + "is the contract that keeps them alive.\n\n"
                                + "Choose the right tool:\n"
                                + "* Need a guaranteed result with constraints/retry and no user "
                                + "visibility → **WorkManager** (CampusFix's reminder sync).\n"
                                + "* Need continuous user-visible work (playing audio, live tracking) "
                                + "→ **foreground Service**.\n"
                                + "* Need a bound API to another process → **bound Service / AIDL**.\n"
                                + "* Fire-and-forget one-shot work → executor is enough.\n\n"
                                + "Classic bugs to avoid: doing network on the service's main thread "
                                + "(onStartCommand runs on main — post to an executor), forgetting "
                                + "to stopSelf so the service lingers, and updating views directly "
                                + "from a service (they are not bound to it).",
                        "Sketch CampusFix's demo sync service: list what runs in onStartCommand "
                                + "(post work + return quickly), what stopSelf(startId) guarantees, "
                                + "and why WorkManager would be the better choice for a nightly sync.",
                        Quiz.list(
                                Quiz.of(
                                        "Which lifecycle callback runs once per startService() "
                                                + "request?",
                                        1,
                                        "onStartCommand — one call per start request, carrying the "
                                                + "startId used by stopSelf(startId).",
                                        "onCreate",
                                        "onStartCommand",
                                        "onBind",
                                        "onDestroy"),
                                Quiz.of(
                                        "What does stopSelf(startId) guarantee?",
                                        0,
                                        "The service stops only if no newer start request arrived — "
                                                + "avoids killing work for a later request.",
                                        "Stops immediately",
                                        "Stops only if id matches last start",
                                        "Restarts the service",
                                        "Stops after one minute"),
                                Quiz.of(
                                        "When should you prefer WorkManager over a Service?",
                                        2,
                                        "For deferrable, guaranteed work with constraints and retries "
                                                + "that needs no user visibility — services are for live or "
                                                + "user-visible tasks.",
                                        "Never",
                                        "For real-time audio",
                                        "For background jobs needing guarantees + constraints",
                                        "When you need a notification")),
                        "W"));
    }
}
