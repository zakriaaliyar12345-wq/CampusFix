package com.example.campusfix.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusfix.R;
import com.example.campusfix.ui.about.AboutActivity;
import com.example.campusfix.ui.learning.LearningActivity;
import com.example.campusfix.ui.report.CreateReportActivity;
import com.example.campusfix.ui.report.ReportListActivity;
import com.example.campusfix.ui.report.StatisticsActivity;
import com.example.campusfix.ui.settings.SettingsActivity;

/**
 * Launcher screen and navigation hub (topics 01.2 layouts, 02.1 activities,
 * 04.4 navigation). The Learning Lab section was added in milestone M2.
 */
public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Reporting
        findViewById(R.id.btnCreateReport).setOnClickListener(v ->
                CreateReportActivity.launch(
                        this, CreateReportActivity.NEW_REPORT));

        findViewById(R.id.btnAllReports).setOnClickListener(v ->
                ReportListActivity.launch(this));

        findViewById(R.id.btnStatistics).setOnClickListener(v ->
                StatisticsActivity.launch(this));

        // Learning
        findViewById(R.id.btnLearningLab).setOnClickListener(v ->
                LearningActivity.launch(this));

        findViewById(R.id.btnBookmarks).setOnClickListener(v ->
                openBookmarks());

        // App
        findViewById(R.id.btnSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        findViewById(R.id.btnAbout).setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
    }

    /** Jumps straight into the Learning Lab with the Bookmarked filter on. */
    private void openBookmarks() {
        startActivity(new Intent(this, LearningActivity.class)
                .putExtra(LearningActivity.EXTRA_SHOW_BOOKMARKS, true));
    }
}
