package com.example.campusfix.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Thin wrapper around SharedPreferences for small, lightweight settings.
 *
 * SharedPreferences is the right tool here: a handful of booleans/integers
 * read at startup. Structured data (reports, lesson progress) lives in Room
 * instead — SharedPreferences is not a database and never stores credentials.
 */
public class SettingsRepository {

    private static final String PREF_NAME = "campusfix_settings";

    public static final int THEME_SYSTEM =
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int THEME_LIGHT =
            AppCompatDelegate.MODE_NIGHT_NO;
    public static final int THEME_DARK =
            AppCompatDelegate.MODE_NIGHT_YES;

    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_NOTIFICATIONS_ENABLED =
            "notifications_enabled";
    private static final String KEY_DISMISS_INTRO = "intro_dismissed";

    private final SharedPreferences preferences;

    public SettingsRepository(Context context) {
        preferences =
                context.getApplicationContext().getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );
    }

    public int getThemeMode() {
        return preferences.getInt(KEY_THEME_MODE, THEME_SYSTEM);
    }

    /** Persists the choice and applies it process-wide immediately. */
    public void setThemeMode(int mode) {
        preferences.edit().putInt(KEY_THEME_MODE, mode).apply();
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public boolean areNotificationsEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        preferences.edit()
                .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
                .apply();
    }

    public boolean isIntroDismissed() {
        return preferences.getBoolean(KEY_DISMISS_INTRO, false);
    }

    public void setIntroDismissed(boolean dismissed) {
        preferences.edit()
                .putBoolean(KEY_DISMISS_INTRO, dismissed)
                .apply();
    }
}
