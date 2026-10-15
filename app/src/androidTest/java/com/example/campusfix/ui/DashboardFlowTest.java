package com.example.campusfix.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.campusfix.R;
import com.example.campusfix.ui.dashboard.DashboardActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests for the navigation hub (topic 06.1 — UI testing, and
 * 04.4 — user navigation).
 *
 * The first test is the 06.1 lesson exercise verbatim: open the dashboard,
 * assert the "All Reports" button is displayed, click it, and assert the
 * list's create FAB is there.
 *
 * Runs on a device or emulator: gradlew.bat connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4.class)
public class DashboardFlowTest {

    @Rule
    public ActivityScenarioRule<DashboardActivity> dashboardRule =
            new ActivityScenarioRule<>(DashboardActivity.class);

    @Test
    public void dashboard_showsEveryEntryPoint() {
        onView(withId(R.id.btnCreateReport)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAllReports)).check(matches(isDisplayed()));
        onView(withId(R.id.btnStatistics)).check(matches(isDisplayed()));
        onView(withId(R.id.btnLearningLab)).check(matches(isDisplayed()));
        onView(withId(R.id.btnBookmarks)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSettings)).check(matches(isDisplayed()));
        onView(withId(R.id.btnAbout)).check(matches(isDisplayed()));
    }

    /** Lesson 06.1 exercise: dashboard → report list → create FAB. */
    @Test
    public void allReports_opensTheListWithItsCreateFab() {
        onView(withId(R.id.btnAllReports)).perform(click());

        onView(withId(R.id.fabCreateReport)).check(matches(isDisplayed()));
        onView(withId(R.id.tilSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.tvResultCount)).check(matches(isDisplayed()));
    }

    @Test
    public void learningLab_opensTheLessonList() {
        onView(withId(R.id.btnLearningLab)).perform(click());

        onView(withId(R.id.recyclerLessons)).check(matches(isDisplayed()));
        onView(withId(R.id.tvLearningProgress)).check(matches(isDisplayed()));
        onView(withId(R.id.chipGroupLearning)).check(matches(isDisplayed()));
    }

    @Test
    public void createReport_opensAnEmptyForm() {
        onView(withId(R.id.btnCreateReport)).perform(click());

        onView(withId(R.id.tvFormTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.etTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.actvCategory)).check(matches(isDisplayed()));
        onView(withId(R.id.chipGroupPriority)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()));
    }
}
