package com.example.campusfix.learning;

import java.util.List;

/** Unit 2 — course files 02.1 … 02.3 (activities, lifecycle, implicit intents). */
final class Unit2Lessons {

    private static final String UNIT = "Unit 2 — Activities & Intents";

    private Unit2Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "02.1",
                        "Activities and Intents",
                        UNIT,
                        "Starting screens with explicit intents and passing data between them.",
                        "An Activity is one screen with a lifecycle the framework drives. You launch "
                                + "activities with Intent objects — bundles of request data.\n\n"
                                + "An explicit intent names its target component directly, which is what "
                                + "you use inside your own app:\n"
                                + "```java\n"
                                + "Intent intent = new Intent(this, ReportDetailActivity.class);\n"
                                + "intent.putExtra(\"extra_report_id\", report.id);\n"
                                + "startActivity(intent);\n"
                                + "```\n\n"
                                + "putExtra carries typed values (long, String, Parcelable…) under "
                                + "string keys; the receiving activity reads them in onCreate:\n"
                                + "```java\n"
                                + "long id = getIntent().getLongExtra(\"extra_report_id\", -1L);\n"
                                + "```\n\n"
                                + "CampusFix centralises this so no key is ever mistyped:\n"
                                + "```java\n"
                                + "public static void launch(Context context, long reportId) {\n"
                                + "    context.startActivity(new Intent(context, ReportDetailActivity.class)\n"
                                + "            .putExtra(EXTRA_REPORT_ID, reportId));\n"
                                + "}\n"
                                + "```\n\n"
                                + "If you need a result back (the editor telling the list 'I saved'), "
                                + "use startActivityForResult or the newer ActivityResultLauncher. "
                                + "CampusFix mostly relies on Room's observable LiveData instead: when "
                                + "the editor writes a new report, the list's LiveData fires by itself "
                                + "and no result plumbing is needed — a deliberate architectural "
                                + "choice worth comparing in your own apps.\n\n"
                                + "Every activity MUST be declared in AndroidManifest.xml (except "
                                + "activities in library modules are declared there instead). "
                                + "Forgetting the declaration throws ActivityNotFoundException the "
                                + "moment you start it. android:exported controls whether other apps "
                                + "can start the activity; internal screens use false.\n\n"
                                + "Data passed between activities should be small and parcelable; for "
                                + "big structured data pass just an id and let the next screen load "
                                + "it — exactly what CampusFix does with report ids.",
                        "Trace the navigation path in CampusFix: dashboard → All Reports → a report "
                                + "card → detail. Open the three Java files and write down the intent "
                                + "extra key used between list and detail, and where the id is read. "
                                + "Then add a temporary log line in ReportDetailActivity.onCreate to "
                                + "print the received id.",
                        Quiz.list(
                                Quiz.of(
                                        "Which intent targets a component by class name?",
                                        2,
                                        "An explicit intent specifies the target class directly (new "
                                                + "Intent(context, Target.class)); implicit intents declare "
                                                + "an action and let matching apps respond.",
                                        "An implicit intent",
                                        "A broadcast intent",
                                        "An explicit intent",
                                        "A pending intent"),
                                Quiz.of(
                                        "What happens if an activity is not declared in "
                                                + "AndroidManifest.xml?",
                                        1,
                                        "The framework cannot resolve it and startActivity throws "
                                                + "ActivityNotFoundException at runtime.",
                                        "It still runs",
                                        "ActivityNotFoundException is thrown",
                                        "Gradle fails to build",
                                        "It runs in safe mode"),
                                Quiz.of(
                                        "Why does CampusFix pass a report id instead of the whole "
                                                + "report object between screens?",
                                        0,
                                        "Intents should carry small payloads; an id keeps the parcel "
                                                + "small and lets the detail screen observe fresh LiveData "
                                                + "from Room instead of a stale copy.",
                                        "Objects cannot be passed at all",
                                        "To keep the payload small and load fresh data",
                                        "Because Parcelable is broken",
                                        "To force a database write")),
                        "W"),

                new Lesson(
                        "02.2",
                        "Activity lifecycle and state",
                        UNIT,
                        "The creation/destruction dance of activities and how to survive rotation.",
                        "Android can destroy and recreate activities at any time — most commonly on "
                                + "rotation — because a configuration change rebuilds the whole view "
                                + "hierarchy. The lifecycle callbacks are:\n\n"
                                + "onCreate (build the UI), onStart (visible), onResume (interactive), "
                                + "onPause (partially obscured), onStop (not visible), onDestroy "
                                + "(final). A new activity instance is created for every rotation, so "
                                + "any field you set up in onCreate is lost.\n\n"
                                + "The classic bug: type text into a field, rotate, and the text is "
                                + "gone. The framework saves view state automatically for views with "
                                + "an android:id (via onSaveInstanceState/onRestoreInstanceState), "
                                + "but your own fields and any loaded data are NOT saved.\n\n"
                                + "Two complementary techniques solve this:\n\n"
                                + "1. ViewModel — survives rotation because it is retained by the "
                                + "ViewModelStore until the activity is finished for good:\n"
                                + "```java\n"
                                + "viewModel = new ViewModelProvider(this)\n"
                                + "        .get(ReportListViewModel.class);\n"
                                + "// filter state lives in the ViewModel and outlives rotation\n"
                                + "viewModel.setFilter(viewModel.getFilter().withQuery(text));\n"
                                + "```\n\n"
                                + "2. onSaveInstanceState — for small pieces of UI state and for "
                                + "process death (where the whole process, including ViewModels, is "
                                + "killed):\n"
                                + "```java\n"
                                + "@Override\n"
                                + "protected void onSaveInstanceState(@NonNull Bundle outState) {\n"
                                + "    super.onSaveInstanceState(outState);\n"
                                + "    outState.putString(\"state_search\", search);\n"
                                + "}\n"
                                + "```\n\n"
                                + "CampusFix's ReportListActivity deliberately uses both: the ViewModel "
                                + "holds the filter across rotation, and the Bundle restores search "
                                + "text if the process was killed and the ViewModel is brand new.\n\n"
                                + "Also remember: never do slow work in onCreate — it blocks the frame; "
                                + "and always pair every addOnBackPressedCallback or registered "
                                + "listener with removal where needed to avoid leaks. Leaks happen "
                                + "when a long-lived object (static field, handler) references a "
                                + "destroyed activity.",
                        "In Android Studio enable Don't keep activities (Developer options), open "
                                + "CampusFix's report list, type a search, press Home and return — "
                                + "observe what survives. Then rotate the device and verify the filter "
                                + "chips and search text are still applied.",
                        Quiz.list(
                                Quiz.of(
                                        "What happens to an Activity instance during a rotation?",
                                        1,
                                        "It is destroyed and a new instance is created; onCreate runs "
                                                + "again with a fresh view hierarchy.",
                                        "Nothing changes",
                                        "It is destroyed and recreated",
                                        "Only onPause is called",
                                        "It moves to the background"),
                                Quiz.of(
                                        "Which component survives configuration changes?",
                                        0,
                                        "ViewModel objects live in the ViewModelStore, which is not "
                                                + "cleared on rotation — only when the activity finishes.",
                                        "ViewModel",
                                        "Activity fields",
                                        "Local variables",
                                        "EditText listeners"),
                                Quiz.of(
                                        "What does onSaveInstanceState protect against?",
                                        2,
                                        "It captures small UI state into a Bundle that the system "
                                                + "retains; it is used both for recreation and for full "
                                                + "process death, where ViewModels are also lost.",
                                        "Database corruption",
                                        "Process death and recreation",
                                        "Network errors",
                                        "Slow onCreate")),
                        "W"),

                new Lesson(
                        "02.3",
                        "Implicit Intents",
                        UNIT,
                        "Letting other apps do the work: sharing, maps, camera and photo picking.",
                        "An implicit intent describes an ACTION plus (usually) data and type, and "
                                + "the system finds components able to handle it. Sharing text looks "
                                + "like this:\n"
                                + "```java\n"
                                + "Intent send = new Intent(Intent.ACTION_SEND);\n"
                                + "send.setType(\"text/plain\");\n"
                                + "send.putExtra(Intent.EXTRA_TEXT, report.title + \"\\n\" + location);\n"
                                + "startActivity(Intent.createChooser(send, \"Share\"));\n"
                                + "```\n\n"
                                + "ACTION_SEND with type text/plain matches every messaging and "
                                + "email app; createChooser shows the system picker so the user "
                                + "picks which one. Wrap startActivity in try/catch for "
                                + "ActivityNotFoundException — a device with no matching app would "
                                + "otherwise crash.\n\n"
                                + "Opening a location uses a geo URI:\n"
                                + "```java\n"
                                + "Uri geo = Uri.parse(\"geo:0,0?q=\" + Uri.encode(location));\n"
                                + "startActivity(new Intent(Intent.ACTION_VIEW, geo));\n"
                                + "```\n\n"
                                + "Camera capture needs special care on API 24+: passing a raw file:// "
                                + "URI to another app throws FileUriExposedException, so apps use a "
                                + "FileProvider that mints a content:// URI protected by a temporary "
                                + "grant:\n"
                                + "```java\n"
                                + "Uri output = FileProvider.getUriForFile(context,\n"
                                + "        context.getPackageName() + \".fileprovider\", file);\n"
                                + "Intent snap = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);\n"
                                + "snap.putExtra(MediaStore.EXTRA_OUTPUT, output);\n"
                                + "snap.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);\n"
                                + "```\n\n"
                                + "Choosing an existing photo uses the Storage Access Framework:\n"
                                + "```java\n"
                                + "Intent pick = new Intent(Intent.ACTION_GET_CONTENT);\n"
                                + "pick.addCategory(Intent.CATEGORY_OPENABLE);\n"
                                + "pick.setType(\"image/*\");\n"
                                + "```\n"
                                + "SAF grants a read permission for the returned content:// URI — no "
                                + "storage runtime permission is required at all.\n\n"
                                + "All four patterns are live in CampusFix: share and maps in "
                                + "ReportDetailActivity, camera and gallery in CreateReportActivity.",
                        "In CampusFix open a report and tap Share, then Open in maps (emulators "
                                + "usually include a maps app; a physical device may too). In the "
                                + "editor, attach a photo with both Take photo and Choose from "
                                + "gallery, and note that no permission dialog appears.",
                        Quiz.list(
                                Quiz.of(
                                        "What makes an intent 'implicit'?",
                                        1,
                                        "It declares an action/data/type instead of naming the target "
                                                + "class, so any registered component can respond.",
                                        "It names the target class",
                                        "It declares an action and lets matching apps respond",
                                        "It runs without permissions",
                                        "It starts a service"),
                                Quiz.of(
                                        "Why can't you pass a file:// URI to the camera on API 24+?",
                                        0,
                                        "The system throws FileUriExposedException; FileProvider "
                                                + "content:// URIs with temporary grants replace them.",
                                        "file:// URIs expose files to other apps (FileUriExposedException)",
                                        "Cameras only read content text",
                                        "file:// is invalid XML",
                                        "It corrupts the JPEG"),
                                Quiz.of(
                                        "What advantage does ACTION_GET_CONTENT have over reading "
                                                + "external storage directly?",
                                        2,
                                        "SAF returns a scoped, revocable content:// grant for the "
                                                + "chosen file — no READ_EXTERNAL_STORAGE permission needed.",
                                        "It is faster always",
                                        "It writes the file for you",
                                        "It requires no permission and grants scoped access",
                                        "It supports only images")),
                        "W"));
    }
}
