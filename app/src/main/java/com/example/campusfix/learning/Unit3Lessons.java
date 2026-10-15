package com.example.campusfix.learning;

import java.util.List;

/** Unit 3 — course files 03.1 … 03.3 (debugger, testing, support library). */
final class Unit3Lessons {

    private static final String UNIT = "Unit 3 — Debugging, Testing & Libraries";

    private Unit3Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "03.1",
                        "The Android Studio debugger",
                        UNIT,
                        "Breakpoints, evaluate expression, and reading logs to find the real cause.",
                        "The debugger lets you pause execution and inspect the exact state of your "
                                + "program. Click the gutter next to a line number to add a red "
                                + "breakpoint, then run the app with the Debug icon (bug) instead of "
                                + "Run. When execution hits the breakpoint the process pauses and "
                                + "Android Studio opens the Debug window with:\n\n"
                                + "* Frames — the call stack, so you can see how you got here.\n"
                                + "* Variables — every local and field in scope; expand objects to "
                                + "inspect nested values.\n"
                                + "* Evaluate Expression (Alt+F8) — run a snippet against the paused "
                                + "state: call a method, check a calculation, or test a fix before "
                                + "writing it into the file.\n\n"
                                + "Use conditional breakpoints for loops: right-click the breakpoint "
                                + "and type a condition such as i == 42. Use exception breakpoints "
                                + "(View Breakpoints > + > Java Exception) to pause on every "
                                + "NullPointerException — the fastest way to find a crash's origin.\n\n"
                                + "Not everything needs a debugger. Logcat plus Log lines are often "
                                + "quicker:\n"
                                + "```java\n"
                                + "if (BuildConfig.DEBUG) {\n"
                                + "    Log.d(\"CampusFix\", \"saved report id=\" + id);\n"
                                + "}\n"
                                + "```\n"
                                + "Gate debug logs with BuildConfig.DEBUG so release builds never "
                                + "print them (they cost CPU and can leak data). Android Studio's "
                                + "Logcat in recent versions supports structured queries like "
                                + "package:mine level:error.\n\n"
                                + "Common diagnostic order: 1) read the stack trace in Logcat from the "
                                + "bottom-most frame in your code, 2) reproduce with a breakpoint at "
                                + "that frame, 3) inspect the variables the message mentions, 4) "
                                + "Evaluate Expression to confirm your hypothesis, 5) fix and rerun "
                                + "without the breakpoint.\n\n"
                                + "Two CampusFix examples worth debugging: break in "
                                + "ReportRepository.buildQuery() to watch the SQL string assemble, or "
                                + "in ReportAdapter.areContentsTheSame() to see why a row did or did "
                                + "not rebind.",
                        "Set a conditional breakpoint inside ReportRepository.buildQuery() with the "
                                + "condition filter.query.equals(\"wifi\"), search for 'wifi' in the "
                                + "app, and use Evaluate Expression to print sql.toString(). Then "
                                + "add a temporary Log.d guarded by BuildConfig.DEBUG and find it in "
                                + "Logcat.",
                        Quiz.list(
                                Quiz.of(
                                        "How do you inspect a value while execution is paused at a "
                                                + "breakpoint?",
                                        2,
                                        "The Variables pane shows in-scope values, and Evaluate "
                                                + "Expression (Alt+F8) runs arbitrary snippets against the "
                                                + "paused state.",
                                        "Recompile with prints",
                                        "Read Logcat only",
                                        "Use the Variables pane and Evaluate Expression",
                                        "Restart the app"),
                                Quiz.of(
                                        "Why wrap Log.d calls in BuildConfig.DEBUG?",
                                        1,
                                        "So release builds skip logging entirely — avoids wasted work "
                                                + "and prevents leaking internal data to Logcat.",
                                        "Because Log.d crashes in release",
                                        "To keep logs out of release builds",
                                        "Because Logcat is debug-only",
                                        "To make logs faster"),
                                Quiz.of(
                                        "What is the benefit of a conditional breakpoint?",
                                        0,
                                        "It pauses only when the condition holds, which is essential "
                                                + "inside loops or high-frequency code paths.",
                                        "Pauses only when a chosen condition is true",
                                        "Runs the condition as a unit test",
                                        "Breaks in Logcat",
                                        "Skips compilation")),
                        "D"),

                new Lesson(
                        "03.2",
                        "App testing",
                        UNIT,
                        "The three test levels: local unit tests, instrumented tests, and manual checks.",
                        "Android testing has two automated levels (plus manual QA):\n\n"
                                + "* Local unit tests (app/src/test) run on the JVM in seconds. They "
                                + "are perfect for pure logic — validators, query builders, formatters, "
                                + "ViewModels with fakes. JUnit4 + Mockito/livedata-testing live here.\n"
                                + "* Instrumented tests (app/src/androidTest) run on a device or "
                                + "emulator because they need the Android framework: Room DAOs "
                                + "against a real SQLite file, Espresso UI flows, component tests. "
                                + "androidx.test.ext.junit and Espresso are the standard libraries.\n\n"
                                + "A local test example of exactly the style CampusFix uses:\n"
                                + "```java\n"
                                + "@Test\n"
                                + "public void validateTitle_rejectsTooShort() {\n"
                                + "    assertEquals(\"error_title_too_short\",\n"
                                + "            Validator.validateTitle(\"abc\"));\n"
                                + "}\n"
                                + "```\n\n"
                                + "Run them with Gradle:\n"
                                + "```bash\n"
                                + "gradlew :app:testDebugUnitTest\n"
                                + "gradlew :app:connectedDebugAndroidTest   # device required\n"
                                + "```\n\n"
                                + "Design for testability: keep logic out of Activities (they are hard "
                                + "to instantiate), push it into plain classes (Validator, "
                                + "ReportRepository.buildQuery) or ViewModels. Room makes database "
                                + "tests safe with an in-memory database:\n"
                                + "```java\n"
                                + "Room.inMemoryDatabaseBuilder(context, CampusDatabase.class)\n"
                                + "        .allowMainThreadQueries().build();\n"
                                + "```\n\n"
                                + "What should you test? Boundary cases (the exact title length "
                                + "limits), failure paths (empty input), and invariants (deleting a "
                                + "report also deletes its history — Room @Transaction). Skip "
                                + "trivial getters. Aim for tests that fail loudly when someone "
                                + "breaks the behaviour.",
                        "CampusFix already covers this in ValidatorTest "
                                + "(app/src/test): title_acceptsExactlyFourCharacters and "
                                + "title_rejectsEightyOneCharacters are the boundary cases. Add "
                                + "your own alongside them — e.g. a description of exactly 10 "
                                + "characters — and run ./gradlew :app:testDebugUnitTest to "
                                + "confirm it passes.",
                        Quiz.list(
                                Quiz.of(
                                        "Which tests run on the JVM without a device?",
                                        1,
                                        "Local unit tests under src/test compile against the JDK and "
                                                + "run in Gradle in seconds.",
                                        "Espresso tests",
                                        "Local unit tests in src/test",
                                        "Room DAO tests",
                                        "Connected tests"),
                                Quiz.of(
                                        "Why are Android components like Activity awkward to unit-"
                                                + "test?",
                                        0,
                                        "They are instantiated and driven by the framework, so "
                                                + "constructing one manually requires Robolectric or a "
                                                + "device; logic should be extracted to plain classes.",
                                        "They are always null",
                                        "They need the Android framework to instantiate them",
                                        "They have no methods",
                                        "They run only in Kotlin"),
                                Quiz.of(
                                        "Which Room feature guarantees report + history rows delete "
                                                + "together?",
                                        2,
                                        "@Transaction makes the annotated DAO method commit as one "
                                                + "atomic unit — either both statements run or neither does.",
                                        "@Insert",
                                        "@ForeignKey only",
                                        "@Transaction",
                                        "@Query")),
                        "W"),

                new Lesson(
                        "03.3",
                        "The Android Support Library",
                        UNIT,
                        "Where the support library came from, why it became AndroidX, and what changed.",
                        "Before 2018, new platform features shipped in the 'Android Support Library' "
                                + "(packages android.support.v4, android.support.v7…) so that apps "
                                + "could use them on older devices: the framework updated yearly, the "
                                + "support library updated monthly via the Android Support Repository "
                                + "in the SDK.\n\n"
                                + "Famous examples: android.support.v7.app.AppCompatActivity, "
                                + "android.support.v7.widget.RecyclerView, android.support.v4.app."
                                + "FragmentManager. v4 and v7 named the minimum API they supported "
                                + "(API 4, API 7).\n\n"
                                + "In 2018 Google rebranded and restructured it as AndroidX: identical "
                                + "functionality, but with cleaned-up package names, independent "
                                + "versioning per artifact, and automatic migration tools. The mapping "
                                + "is direct:\n\n"
                                + "* android.support.v7.app.AppCompatActivity → androidx.appcompat.app."
                                + "AppCompatActivity\n"
                                + "* android.support.v7.widget.RecyclerView → androidx.recyclerview."
                                + "widget.RecyclerView\n"
                                + "* android.support.v4.content.FileProvider → androidx.core.content."
                                + "FileProvider\n\n"
                                + "Projects must never mix support and AndroidX libraries — the "
                                + "compiler forbids it. The flag android.useAndroidX=true in "
                                + "gradle.properties (present in CampusFix) tells the build to map "
                                + "dependencies to AndroidX, and jetifier (android.enableJetifier) "
                                + "can rewrite old support-based third-party libraries on the fly:\n"
                                + "```bash\n"
                                + "# gradle.properties\n"
                                + "android.useAndroidX=true\n"
                                + "android.enableJetifier=true\n"
                                + "```\n\n"
                                + "Practical consequences for you today:\n"
                                + "1. Any tutorial using android.support.* is outdated — substitute "
                                + "the androidx package of the same name.\n"
                                + "2. Gradle coordinates changed group: android.support.v7:recyclerview"
                                + " → androidx.recyclerview:recyclerview.\n"
                                + "3. AndroidX artifacts version independently, so read each library's "
                                + "own release notes (this course's 01.4 lesson).\n\n"
                                + "CampusFix is 100% AndroidX — every import in its source starts with "
                                + "androidx.*, which you can verify with a quick search.",
                        "Search the CampusFix sources for \"android.support\" and confirm there are "
                                + "zero matches; then list five androidx imports actually used. In an "
                                + "old online tutorial, spot one android.support.* reference and "
                                + "write its androidx replacement.",
                        Quiz.list(
                                Quiz.of(
                                        "What replaced the Android Support Library?",
                                        1,
                                        "AndroidX is the successor: same features, cleaned package "
                                                + "names (androidx.*), independent versioning.",
                                        "Kotlin extensions",
                                        "AndroidX",
                                        "Google Play services",
                                        "The Jetpack CLI"),
                                Quiz.of(
                                        "Which import is valid in a modern AndroidX project?",
                                        0,
                                        "androidx.appcompat.app.AppCompatActivity is the AndroidX "
                                                + "successor of android.support.v7.app.AppCompatActivity; the "
                                                + "android.support.* packages no longer exist in AndroidX "
                                                + "projects.",
                                        "android.support.v7.app.AppCompatActivity",
                                        "androidx.support.app.AppCompatActivity",
                                        "android.support.appcompat.AppCompat",
                                        "com.google.android.app.AppCompatActivity"),
                                Quiz.of(
                                        "Why can a project not mix support libraries and AndroidX?",
                                        1,
                                        "The two package trees define overlapping classes and "
                                                + "resources; mixing causes duplicate classes and conflicting "
                                                + "behaviour, so builds forbid it.",
                                        "They are the same size",
                                        "Duplicate/conflicting classes result",
                                        "AndroidX is paid",
                                        "It only affects Kotlin")),
                        "D"));
    }
}
