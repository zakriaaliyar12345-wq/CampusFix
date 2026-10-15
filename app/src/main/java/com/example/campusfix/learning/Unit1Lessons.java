package com.example.campusfix.learning;

import java.util.List;

/** Unit 1 — course files 01.0 … 01.4 (introduction & UI foundations). */
final class Unit1Lessons {

    private static final String UNIT = "Unit 1 — Introduction & UI Foundations";

    private Unit1Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "01.0",
                        "Introduction to Android",
                        UNIT,
                        "What Android is, how the platform is layered, and how an app fits into it.",
                        "Android is an open-source operating system built on a Linux kernel. From the "
                                + "bottom up it is layered: the Linux kernel (drivers, memory, processes), "
                                + "the Hardware Abstraction Layer (HAL), the Android runtime (ART, which "
                                + "executes your compiled DEX bytecode and manages garbage collection), the "
                                + "native libraries (OpenGL, SQLite, WebKit), the Java framework classes "
                                + "(ActivityManager, WindowManager, PackageManager, resources), and finally "
                                + "your applications at the top.\n\n"
                                + "An installed app is packaged as an APK (or an Android App Bundle for "
                                + "Play delivery). Every app runs in its own process with its own Linux "
                                + "user id, which is the basis of Android's sandbox: one app cannot read "
                                + "another app's files or data unless the user grants an explicit "
                                + "permission.\n\n"
                                + "Four core components are declared in AndroidManifest.xml: the Activity "
                                + "(one screen), the Service (work in the background), the Broadcast "
                                + "Receiver (reactions to system-wide events), and the Content Provider "
                                + "(shared structured data). The system, not your code, creates these "
                                + "objects — they are instantiated by the framework, a style called "
                                + "inversion of control.\n\n"
                                + "CampusFix declares its components like this:\n"
                                + "```xml\n"
                                + "<application\n"
                                + "    android:name=\".CampusFixApplication\"\n"
                                + "    android:label=\"@string/app_name\"\n"
                                + "    android:theme=\"@style/Theme.CampusFix\">\n"
                                + "    <activity android:name=\".ui.dashboard.DashboardActivity\"\n"
                                + "        android:exported=\"true\">\n"
                                + "        <intent-filter>\n"
                                + "            <action android:name=\"android.intent.action.MAIN\" />\n"
                                + "            <category android:name=\"android.intent.category.LAUNCHER\" />\n"
                                + "        </intent-filter>\n"
                                + "    </activity>\n"
                                + "</application>\n"
                                + "```\n\n"
                                + "Notice android:exported: since Android 12 any component with an "
                                + "intent-filter must state explicitly whether other apps may start it. "
                                + "The MAIN/LAUNCHER filter is what puts the icon on the home screen.\n\n"
                                + "Android versions matter for compatibility: minSdk decides the oldest "
                                + "supported device, targetSdk declares which behaviour rules you have "
                                + "adopted. CampusFix uses minSdk 24 (Android 7.0) and targets the "
                                + "current release.",
                        "Open CampusFix in Android Studio, find AndroidManifest.xml, and identify the "
                                + "launcher activity. Then open build.gradle and locate minSdk, "
                                + "targetSdk and applicationId. Write down which Android version a "
                                + "minSdk 24 device runs.",
                        Quiz.list(
                                Quiz.of(
                                        "Which layer of Android executes your compiled app bytecode?",
                                        1,
                                        "ART (Android Runtime) compiles DEX bytecode ahead of time or "
                                                + "just in time and runs it; the kernel and HAL sit below it, "
                                                + "and the framework sits above it.",
                                        "The Linux kernel",
                                        "ART (Android Runtime)",
                                        "The Hardware Abstraction Layer",
                                        "Java framework"),
                                Quiz.of(
                                        "What does android:exported=\"true\" mean for a component?",
                                        0,
                                        "exported=true allows other apps to start the component; it is "
                                                + "mandatory for any activity that declares an intent-filter.",
                                        "Other apps may start this component",
                                        "The component is written in Java",
                                        "The component survives rotation",
                                        "The component runs as root"),
                                Quiz.of(
                                        "Why does Android run each app in a separate process?",
                                        1,
                                        "Separate processes with distinct Linux uids create the app "
                                                + "sandbox: one app cannot access another app's memory or "
                                                + "files without a granted permission.",
                                        "To make apps start faster",
                                        "For security isolation between apps",
                                        "Because Java requires it",
                                        "To save battery only")),
                        "D"),

                new Lesson(
                        "01.1",
                        "Your first Android app",
                        UNIT,
                        "Project structure, the new-project wizard, and what happens when you press Run.",
                        "Android Studio's New Project wizard generates a Gradle project with one app "
                                + "module. The important folders are: app/src/main/java (your Java "
                                + "sources), app/src/main/res (layouts, strings, drawables, menus, "
                                + "values), app/src/main/AndroidManifest.xml, and app/build.gradle.kts "
                                + "which declares the SDK levels and dependencies.\n\n"
                                + "Gradle builds the project in stages: it processes resources into the R "
                                + "class, compiles Java into .class files, converts them to DEX, packages "
                                + "everything into an APK, and signs it with your debug key. The Run "
                                + "button then installs the APK and starts your launcher activity.\n\n"
                                + "The anatomy of the generated code is tiny:\n"
                                + "```java\n"
                                + "public class MainActivity extends AppCompatActivity {\n"
                                + "    @Override\n"
                                + "    protected void onCreate(Bundle savedInstanceState) {\n"
                                + "        super.onCreate(savedInstanceState);\n"
                                + "        setContentView(R.layout.activity_main);\n"
                                + "    }\n"
                                + "}\n"
                                + "```\n"
                                + "The class extends AppCompatActivity so that modern back "
                                + "navigation, Material theming and the support-library features work on "
                                + "every API level. setContentView inflates the XML layout and attaches "
                                + "it to the window.\n\n"
                                + "The paired layout looks like this:\n"
                                + "```xml\n"
                                + "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                                + "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                                + "    android:layout_width=\"match_parent\"\n"
                                + "    android:layout_height=\"match_parent\"\n"
                                + "    android:gravity=\"center\"\n"
                                + "    android:orientation=\"vertical\">\n"
                                + "    <TextView\n"
                                + "        android:layout_width=\"wrap_content\"\n"
                                + "        android:layout_height=\"wrap_content\"\n"
                                + "        android:text=\"@string/hello\" />\n"
                                + "</LinearLayout>\n"
                                + "```\n\n"
                                + "Notice the text is not hard-coded: it points at @string/hello so it "
                                + "can be translated. CampusFix follows the same rule everywhere — every "
                                + "user-visible string lives in res/values/strings.xml.\n\n"
                                + "Build variants matter: the debug build is signed with a development "
                                + "key, keeps debugging enabled and is what you install daily; the "
                                + "release build is minified and needs a release keystore.",
                        "Create a fresh Empty Views Activity project, run it on an emulator or device, "
                                + "then change the hard-coded text into a string resource and rerun. "
                                + "Finally, open CampusFix and confirm it follows the same folder "
                                + "structure you just built.",
                        Quiz.list(
                                Quiz.of(
                                        "Where do you define a string that your layout references with "
                                                + "@string/...?",
                                        2,
                                        "@string references resolve against res/values/strings.xml "
                                                + "(and locale-specific variants); code and Gradle files are "
                                                + "never looked up for string resources.",
                                        "In build.gradle",
                                        "In the AndroidManifest only",
                                        "In res/values/strings.xml",
                                        "Directly as android:text in every layout"),
                                Quiz.of(
                                        "What does setContentView(R.layout.activity_main) do?",
                                        1,
                                        "It inflates the XML layout hierarchy and attaches it to this "
                                                + "activity's window; R.layout.activity_main is the compiled "
                                                + "resource id.",
                                        "Saves the layout to disk",
                                        "Inflates the layout and shows it in the activity window",
                                        "Converts XML to Java",
                                        "Starts a new activity"),
                                Quiz.of(
                                        "Which build variant do you normally install while developing?",
                                        0,
                                        "The debug variant is signed with a debug key and keeps "
                                                + "debugging tools enabled; release is prepared for "
                                                + "distribution.",
                                        "debug",
                                        "release",
                                        "benchmark",
                                        "lint-only")),
                        "D"),

                new Lesson(
                        "01.2",
                        "Layouts and resources for the UI",
                        UNIT,
                        "XML layouts, common layout containers, the R class, and why strings live in resources.",
                        "An Android screen is a tree of Views described in XML. The root layout you "
                                + "choose decides how children are positioned:\n\n"
                                + "* LinearLayout arranges children in a single row or column with "
                                + "layout_weight to split space.\n"
                                + "* FrameLayout stacks children on top of each other (overlays).\n"
                                + "* ConstraintLayout positions children relative to each other or the "
                                + "parent with flat, flexible constraints — ideal for complex screens.\n"
                                + "* ScrollView makes its single child scrollable when content exceeds "
                                + "the screen.\n"
                                + "* CoordinatorLayout coordinates sliding behaviours (FAB, app bar).\n\n"
                                + "Layout attributes are LayoutParams: match_parent fills the parent, "
                                + "wrap_content hugs the content, and margins/padding add space outside "
                                + "and inside a view.\n\n"
                                + "Resources are the second half of the topic. Anything you might "
                                + "translate, restyle or reuse belongs in res/: strings in strings.xml, "
                                + "colors in colors.xml, sizes in dimens.xml, pictures and shapes in "
                                + "drawable/, screens in layout/, menu definitions in menu/. aapt "
                                + "compiles them and generates the R class so Java can reference them "
                                + "as R.string.app_name or R.layout.activity_dashboard.\n\n"
                                + "A typical CampusFix layout node:\n"
                                + "```xml\n"
                                + "<com.google.android.material.button.MaterialButton\n"
                                + "    android:id=\"@+id/btnAllReports\"\n"
                                + "    android:layout_width=\"match_parent\"\n"
                                + "    android:layout_height=\"wrap_content\"\n"
                                + "    android:layout_marginTop=\"@dimen/spacing_small\"\n"
                                + "    android:text=\"@string/nav_all_reports\"\n"
                                + "    app:icon=\"@drawable/ic_list\" />\n"
                                + "```\n\n"
                                + "id creates a field in R.id so findViewById() can find it later; "
                                + "@dimen and @string keep spacing and wording consistent across the "
                                + "whole app. Resource qualifiers (values-night, layout-sw600dp, "
                                + "values-fr) let one project serve dark mode, tablets and other "
                                + "languages without code changes.",
                        "In CampusFix, open res/values/strings.xml and add a new string named "
                                + "my_exercise_label. Reference it from a TextView in any layout, run "
                                + "the app, then change the value and confirm the UI updates without "
                                + "touching Java code.",
                        Quiz.list(
                                Quiz.of(
                                        "Which layout stacks children on top of each other?",
                                        2,
                                        "FrameLayout overlays children; LinearLayout lines them up in a "
                                                + "row/column and ConstraintLayout positions them with "
                                                + "constraints.",
                                        "LinearLayout",
                                        "ScrollView",
                                        "FrameLayout",
                                        "HorizontalScrollView"),
                                Quiz.of(
                                        "What does android:layout_width=\"match_parent\" do?",
                                        0,
                                        "match_parent makes the view as wide as its parent; "
                                                + "wrap_content sizes the view to fit its content.",
                                        "Fills the parent's width",
                                        "Wraps to text content",
                                        "Matches the screen diagonal",
                                        "Centres the view"),
                                Quiz.of(
                                        "Why put text in strings.xml instead of android:text directly?",
                                        1,
                                        "Externalized strings can be translated per language, "
                                                + "reviewed in one place and reused; hard-coded text cannot. "
                                                + "Android lint flags hard-coded text as an error-prone "
                                                + "practice.",
                                        "XML is faster than Java",
                                        "So the text can be translated and managed centrally",
                                        "Because hard-coded text crashes",
                                        "To reduce APK size by half")),
                        "W"),

                new Lesson(
                        "01.3",
                        "Text and scrolling views",
                        UNIT,
                        "TextView, EditText, input types, and making long content scrollable.",
                        "TextView displays text; its siblings cover every input need. EditText is the "
                                + "editable version, and Material's TextInputLayout wraps it to add "
                                + "floating hints, error text and character counters.\n\n"
                                + "inputType tells the system which keyboard to show and how to treat "
                                + "the text: textCapSentences capitalises sentences, textMultiLine "
                                + "allows returns, number gives a numeric keypad, textPassword masks "
                                + "input. Combine flags with the | operator. imeOptions controls the "
                                + "keyboard's action key — actionSearch swaps Enter for a search "
                                + "button.\n\n"
                                + "CampusFix's search box is a real example:\n"
                                + "```xml\n"
                                + "<com.google.android.material.textfield.TextInputLayout\n"
                                + "    android:id=\"@+id/tilSearch\"\n"
                                + "    style=\"@style/Widget.Material3.TextInputLayout.OutlinedBox\"\n"
                                + "    android:layout_width=\"match_parent\"\n"
                                + "    android:layout_height=\"wrap_content\"\n"
                                + "    app:startIconDrawable=\"@drawable/ic_search\">\n"
                                + "    <com.google.android.material.textfield.TextInputEditText\n"
                                + "        android:id=\"@+id/etSearch\"\n"
                                + "        android:layout_width=\"match_parent\"\n"
                                + "        android:layout_height=\"wrap_content\"\n"
                                + "        android:imeOptions=\"actionSearch\"\n"
                                + "        android:inputType=\"text\"\n"
                                + "        android:maxLines=\"1\" />\n"
                                + "</com.google.android.material.textfield.TextInputLayout>\n"
                                + "```\n\n"
                                + "Scrolling: a screen whose content can exceed the display must be "
                                + "wrapped in a ScrollView (which may have only one direct child) or a "
                                + "NestedScrollView when it contains a scrolling child like "
                                + "RecyclerView. fillViewport=\"true\" lets the child grow to at least "
                                + "the viewport height even when its content is short.\n\n"
                                + "Text styling options you will use constantly: textSize in sp (scales "
                                + "with user font size), textColor, textStyle=\"bold\", "
                                + "lineSpacingExtra for readability, and textAppearance styles for "
                                + "reusable typography.",
                        "Add a second multi-line EditText to a practice layout with "
                                + "inputType=\"textCapSentences|textMultiLine\" and minLines=\"4\", "
                                + "wrap it in a ScrollView, and confirm the keyboard shows capitals and "
                                + "the screen scrolls when the keyboard opens.",
                        Quiz.list(
                                Quiz.of(
                                        "Which inputType combination gives a multi-line, sentence-"
                                                + "capitalised field?",
                                        0,
                                        "textCapSentences|textMultiLine combines two flags with |; each "
                                                + "flag contributes one behaviour.",
                                        "textCapSentences|textMultiLine",
                                        "textMultiLine|number",
                                        "textPassword|text",
                                        "textVisiblePassword|textCapWords"),
                                Quiz.of(
                                        "Why does content that may exceed the screen need a ScrollView?",
                                        1,
                                        "Android does not auto-scroll static screens; without a "
                                                + "scrolling container the content below the fold would be "
                                                + "unreachable.",
                                        "To make text selectable",
                                        "Because views are not scrollable by default",
                                        "To improve performance",
                                        "Because EditText requires it"),
                                Quiz.of(
                                        "What unit should textSize use so it respects the user's "
                                                + "font-size setting?",
                                        1,
                                        "sp (scale-independent pixels) scales with the system font "
                                                + "scale; dp does not.",
                                        "dp",
                                        "sp",
                                        "px",
                                        "pt")),
                        "W"),

                new Lesson(
                        "01.4",
                        "Resources to help you learn",
                        UNIT,
                        "Where to find answers: official docs, codelabs, samples, and release notes.",
                        "The official home for Android knowledge is developer.android.com. Its "
                                + "structure worth knowing: the Guides section explains concepts "
                                + "(app architecture, UI, background work), the Reference section is "
                                + "the API documentation for every class, and the Codelabs are "
                                + "step-by-step hands-on tutorials that build a small working app from "
                                + "scratch.\n\n"
                                + "When you hit an error message, search it in quotes plus the word "
                                + "Android — most Gradle and resource-linking problems are answered "
                                + "on Stack Overflow or in the Android issue tracker. For deeper "
                                + "understanding, the official samples repository on GitHub ("
                                + "github.com/android) contains curated, current examples of CameraX, "
                                + "Room, WorkManager and more.\n\n"
                                + "Release notes matter more than people expect: each Android Studio "
                                + "and each androidx library release lists deprecations. Today's "
                                + "deprecated API (AsyncTask, the old Support Library) is tomorrow's "
                                + "removed API, so reading release notes is how you stay compatible. "
                                + "The Learning Lab inside CampusFix is itself a curated resource: its "
                                + "lessons map exactly onto this course's slide deck.\n\n"
                                + "A practical research loop:\n"
                                + "1. Reproduce the problem in a small sample.\n"
                                + "2. Read the error, then the official guide page for that API.\n"
                                + "3. Check the deprecation list for the API you are using.\n"
                                + "4. Apply the smallest fix and rerun.\n\n"
                                + "Two commands are worth memorising first — run them from the "
                                + "project folder (or Android Studio's terminal) whenever you want "
                                + "to verify your own work:\n"
                                + "```bash\n"
                                + "gradlew :app:assembleDebug --console=plain      # compile and package\n"
                                + "gradlew :app:testDebugUnitTest --console=plain # run the unit tests\n"
                                + "```\n\n"
                                + "Offline-friendly resources are worth noting too: the API "
                                + "documentation can be downloaded in Android Studio (Help > "
                                + "Check for Updates / SDK Docs), so you can keep working without a "
                                + "connection — the same offline-first principle CampusFix applies to "
                                + "its features.",
                        "Open this app's Learning Lab and read one lesson from a unit you have not "
                                + "studied yet. Then, in a browser, find the Android developer guide "
                                + "page for Room and compare its recommendations with what this app "
                                + "actually does in data/local.",
                        Quiz.list(
                                Quiz.of(
                                        "Which official site hosts Android's API reference and guides?",
                                        1,
                                        "developer.android.com is the official portal for guides, "
                                                + "API references, codelabs and samples.",
                                        "android.com",
                                        "developer.android.com",
                                        "google.com/android",
                                        "play.google.com"),
                                Quiz.of(
                                        "Why should you read androidx release notes regularly?",
                                        0,
                                        "Release notes announce deprecations and behaviour changes; "
                                                + "catching them early lets you migrate before APIs are "
                                                + "removed.",
                                        "They list app sales figures",
                                        "They contain free fonts",
                                        "They are required by the Play Store",
                                        "They replace the compiler"),
                                Quiz.of(
                                        "What is the recommended first step when an error message "
                                                + "appears?",
                                        2,
                                        "Search the exact error text — most Android build and runtime "
                                                + "errors are well documented; then confirm against the "
                                                + "official guide for the API involved.",
                                        "Rewrite the whole app",
                                        "Post on social media",
                                        "Search the exact error and read the relevant guide",
                                        "Delete your code")),
                        "D"));
    }
}
