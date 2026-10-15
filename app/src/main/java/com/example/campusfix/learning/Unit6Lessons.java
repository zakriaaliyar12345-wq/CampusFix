package com.example.campusfix.learning;

import java.util.List;

/** Unit 6 — course file 06.1 (UI testing). */
final class Unit6Lessons {

    private static final String UNIT = "Unit 6 — UI Testing";

    private Unit6Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "06.1",
                        "UI testing",
                        UNIT,
                        "Espresso: driving real screens with onView, matchers, and actions.",
                        "UI tests are instrumented tests that run on a device/emulator and operate "
                                + "the app exactly as a user would: find a view, perform an action, "
                                + "assert a result. Espresso is the standard library ("
                                + "androidx.test.espresso).\n\n"
                                + "Its API has three verbs:\n"
                                + "```java\n"
                                + "@Test\n"
                                + "public void dashboard_showsReportButton() {\n"
                                + "    onView(withId(R.id.btnAllReports))    // matcher\n"
                                + "            .check(matches(isDisplayed())); // assertion\n"
                                + "}\n\n"
                                + "@Test\n"
                                + "public void createReport_rejectsShortTitle() {\n"
                                + "    onView(withId(R.id.etTitle)).perform(typeText(\"abc\"));\n"
                                + "    onView(withId(R.id.btnSave)).perform(click());\n"
                                + "    onView(withId(R.id.tilTitle))\n"
                                + "            .check(matches(hasErrorText(\n"
                                + "                    getTargetContext().getString(R.string.error_title_too_short))));\n"
                                + "}\n"
                                + "```\n"
                                + "* onView(Matcher) — find a view via withId, withText, isDisplayed…\n"
                                + "* perform(...) — typeText, click, scrollTo, closeSoftKeyboard.\n"
                                + "* check(...) — matches(matcher), doesNotExist(), hasErrorText().\n\n"
                                + "Espresso's killer feature is automatic synchronisation: it idles "
                                + "the main looper and waits for your injected AsyncTasks before "
                                + "acting, so no Thread.sleep(). The rule: if background work runs "
                                + "outside the main looper (Room executors, WorkManager), you must "
                                + "register an idling resource or, as CampusFix tests do, prefer "
                                + "synchronous test seams.\n\n"
                                + "Match precisely: withId alone can match several views (duplicate "
                                + "ids in include tags); add .inRoot(withDecorView(not(...))) or "
                                + "withText to disambiguate. For RecyclerView items there is no "
                                + "built-in scrollTo for free — the "
                                + "espresso-contrib RecyclerViewActions.actionOnItemAtPosition() "
                                + "helper handles it.\n\n"
                                + "What deserves a UI test? Critical user journeys only — create a "
                                + "report, change a status, filter the list, open a lesson. Business "
                                + "logic belongs in fast local unit tests; using Espresso for "
                                + "validators wastes minutes per run. Keep one happy path and one "
                                + "error path per screen, and test the actual strings from resources "
                                + "rather than hard-coded literals so translations stay in sync.",
                        "CampusFix implements this exact exercise in "
                                + "DashboardFlowTest.allReports_opensTheListWithItsCreateFab "
                                + "(app/src/androidTest): it opens DashboardActivity, asserts "
                                + "R.id.btnAllReports is displayed, clicks it, and asserts the "
                                + "FAB (R.id.fabCreateReport) is there. Re-derive it yourself, "
                                + "then run gradlew :app:connectedDebugAndroidTest against an "
                                + "emulator — or run only the local unit tests if no device is "
                                + "available.",
                        Quiz.list(
                                Quiz.of(
                                        "What is Espresso's main advantage over manual UI checks?",
                                        1,
                                        "It synchronises with the app's idling state so assertions are "
                                                + "deterministic — no sleeps, fewer flakes.",
                                        "It runs without a device",
                                        "Automatic idling/synchronisation",
                                        "It tests the network",
                                        "It needs no code"),
                                Quiz.of(
                                        "onView(withId(...)).perform(click()) does what?",
                                        0,
                                        "Finds the view with that id and injects a click — the three-step "
                                                + "matcher/perform/check pattern's action step.",
                                        "Finds the view, then taps it",
                                        "Types text",
                                        "Starts an activity",
                                        "Screenshots the view"),
                                Quiz.of(
                                        "Where should validation logic be tested instead of with "
                                                + "Espresso?",
                                        2,
                                        "Local unit tests in src/test run in seconds against plain "
                                                + "classes — UI tests are reserved for real user journeys.",
                                        "In Logcat",
                                        "Manual testing only",
                                        "Local unit tests",
                                        "In the manifest")),
                        "W"));
    }
}
