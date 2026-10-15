package com.example.campusfix;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.campusfix.data.repository.SettingsRepository;

/**
 * Applies the user's persisted theme choice before any activity is drawn,
 * so a dark/light preference survives process restarts (not just rotation).
 */
public class CampusFixApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        int themeMode =
                new SettingsRepository(this).getThemeMode();

        AppCompatDelegate.setDefaultNightMode(themeMode);

        // First-launch sample data (background thread, runs once).
        com.example.campusfix.util.SampleDataSeeder.seedIfNeeded(this);
    }
}
