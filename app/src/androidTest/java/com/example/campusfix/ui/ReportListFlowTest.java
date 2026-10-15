package com.example.campusfix.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.campusfix.R;
import com.example.campusfix.ui.report.ReportListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso UI tests (topic 06.1 — UI testing).
 *
 * Deliberately asserts on views that exist immediately in the layouts and
 * on navigation between activities — not on Room-backed list contents —
 * so the tests stay deterministic regardless of seeded sample data.
 * One happy path (navigation) and one error path (inline validation) for
 * the create form, as lesson 06.1 recommends.
 *
 * Runs on a device or emulator: gradlew.bat connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4.class)
public class ReportListFlowTest {

    @Rule
    public ActivityScenarioRule<ReportListActivity> listRule =
            new ActivityScenarioRule<>(ReportListActivity.class);

    @Test
    public void listScreenShowsSearchAndFab() {
        onView(withId(R.id.tilSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.fabCreateReport)).check(matches(isDisplayed()));
        onView(withId(R.id.tvResultCount)).check(matches(isDisplayed()));
    }

    @Test
    public void fabOpensCreateForm() {
        onView(withId(R.id.fabCreateReport)).perform(click());
        // CreateReportActivity is now in the foreground.
        onView(withId(R.id.etTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()));
    }

    /** Error path: the form refuses a short title with an inline field error. */
    @Test
    public void createForm_showsInlineErrorForShortTitle() {
        onView(withId(R.id.fabCreateReport)).perform(click());

        onView(withId(R.id.etTitle)).perform(
                scrollTo(), typeText("abc"), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(scrollTo(), click());

        onView(withId(R.id.tilTitle)).check(matches(hasErrorText(
                ApplicationProvider.getApplicationContext().getString(
                        R.string.error_title_too_short))));
    }

    /** Leaving a pristine form goes straight back — no discard dialog. */
    @Test
    public void backFromUntouchedFormReturnsToList() {
        onView(withId(R.id.fabCreateReport)).perform(click());
        onView(withId(R.id.etTitle)).check(matches(isDisplayed()));

        pressBack();

        onView(withId(R.id.tilSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.fabCreateReport)).check(matches(isDisplayed()));
    }
}

