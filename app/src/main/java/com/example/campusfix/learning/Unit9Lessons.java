package com.example.campusfix.learning;

import java.util.List;

/** Unit 9 — course files 09.0 … 09.2 (data storage overview, shared preferences, settings). */
final class Unit9Lessons {

    private static final String UNIT = "Unit 9 — Data Storage";

    private Unit9Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "09.0",
                        "Data Storage",
                        UNIT,
                        "Choosing where data lives: memory, files, preferences, SQLite, and the "
                                + "cloud.",
                        "Android gives every app a sandboxed storage area with several options, "
                                + "each suited to different data:\n\n"
                                + "* **Internal storage** (context.getFilesDir(), getCacheDir()) — "
                                + "private to your app, always available, no permission. Files in "
                                + "cacheDir are deletable by the system under pressure. Use for "
                                + "photos you take in-app, exports, downloaded attachments. "
                                + "CampusFix stores report photos via PhotoUtils in a subfolder of "
                                + "the cache — private and zero-permission.\n"
                                + "* **SharedPreferences** — small key→value pairs typed as "
                                + "String/boolean/int/float: settings, flags, last-visit timestamps. "
                                + "Not for lists or large blobs (see 09.1).\n"
                                + "* **SQLite via Room** — relational structured data with queries, "
                                + "relations and transactions: the right home for reports, history, "
                                + "lessons (see unit 10).\n"
                                + "* **External storage / MediaStore / scoped storage** — shared "
                                + "files visible to other apps; since API 29 scoped storage removes "
                                + "raw file paths for other apps' media and needs runtime permissions "
                                + "on older devices. Prefer MediaStore or SAF (Storage Access "
                                + "Framework, ACTION_OPEN_DOCUMENT).\n"
                                + "* **Network/cloud** — a backend or Drive for sync; must be treated "
                                + "as optional because connectivity is not guaranteed (see 07.2).\n\n"
                                + "How to choose? Ask three questions: How big? Structured or blob? "
                                + "Who else needs it?\n"
                                + "- Tiny and simple → SharedPreferences.\n"
                                + "- Queryable structured data → Room/SQLite.\n"
                                + "- Binary blobs → a files directory.\n"
                                + "- Shared with other apps → MediaStore/SAF.\n"
                                + "- Needed everywhere → network, with a local cache as source of "
                                + "truth (offline-first).\n\n"
                                + "Hybrids are normal: CampusFix keeps *photos* in internal cache, "
                                + "*settings* in SharedPreferences, *reports and progress* in Room — "
                                + "one app, three storage engines, each picked deliberately:\n"
                                + "```java\n"
                                + "File photo = new File(getCacheDir(), \"reports/photo.jpg\");\n"
                                + "SharedPreferences sp = getSharedPreferences(\n"
                                + "        \"campusfix_settings\", Context.MODE_PRIVATE);\n"
                                + "boolean dark = sp.getBoolean(\"dark_mode\", false);\n"
                                + "List<ReportEntity> all = reportDao.getAllSync(); // Room\n"
                                + "```\n\n"
                                + "Two universal rules: never store secrets in plain files or prefs "
                                + "(use EncryptedSharedPreferences or a Keystore), and never block "
                                + "the main thread on disk I/O — Room and the executor patterns from "
                                + "07.1 exist for that reason.",
                        "List five pieces of data your own phone apps store (hint: chat drafts, "
                                + "settings, media, contacts) and match each to an Android storage "
                                + "mechanism from this lesson, justifying size, structure and sharing.",
                        Quiz.list(
                                Quiz.of(
                                        "Where does CampusFix keep report photos and why?",
                                        1,
                                        "Internal cache via PhotoUtils — private to the app, needs no "
                                                + "permission, and system can reclaim cache space.",
                                        "SD card root",
                                        "Internal cache — private, no permission",
                                        "SharedPreferences",
                                        "Raw external storage"),
                                Quiz.of(
                                        "Which storage fits hundreds of rows you need to search and "
                                                + "join?",
                                        0,
                                        "SQLite/Room is built for structured queries; prefs and flat "
                                                + "files do not query.",
                                        "SQLite/Room",
                                        "SharedPreferences",
                                        "Cache directory",
                                        "A JSON file"),
                                Quiz.of(
                                        "What replaced raw cross-app file paths on modern Android?",
                                        2,
                                        "Scoped storage: other apps see media only through "
                                                + "MediaStore or the Storage Access Framework.",
                                        "Root access",
                                        "Scoped storage with MediaStore/SAF",
                                        "FTP servers",
                                        "Bluetooth shares")),
                        "W"),

                new Lesson(
                        "09.1",
                        "Shared Preferences",
                        UNIT,
                        "Persisting small key–value pairs: editors, commits, and async writes.",
                        "SharedPreferences stores primitive types (String, int, boolean, float, "
                                + "long, StringSet) under string keys — ideal for settings:\n"
                                + "```java\n"
                                + "SharedPreferences sp = ctx.getSharedPreferences(\n"
                                + "        \"campusfix_settings\", Context.MODE_PRIVATE);\n"
                                + "sp.edit()\n"
                                + "        .putBoolean(\"dark_mode\", true)\n"
                                + "        .putString(\"display_name\", \"Zakria\")\n"
                                + "        .apply();          // async write to disk\n"
                                + "boolean dark = sp.getBoolean(\"dark_mode\", false);\n"
                                + "```\n"
                                + "MODE_PRIVATE is the only sensible mode (files are app-private). "
                                + "apply() writes asynchronously in memory first — the in-memory copy "
                                + "is always current, and reads never block. commit() writes "
                                + "synchronously and returns success — only for the rare case where "
                                + "you must know the write landed (avoid it on the main thread).\n\n"
                                + "APIs you will use:\n"
                                + "* contains(key) — does the key exist?\n"
                                + "* getAll() — dump everything (debugging).\n"
                                + "* registerOnSharedPreferenceChangeListener — observe changes; "
                                + "remember to unregister.\n"
                                + "* PreferenceManager.getDefaultSharedPreferences(ctx) — the "
                                + "conventional single default file (used by androidx.preference).\n\n"
                                + "CampusFix's SettingsRepository wraps all of this behind typed "
                                + "getters/setters so no other class touches raw keys:\n"
                                + "```java\n"
                                + "public boolean isDarkMode() { return sp.getBoolean(KEY_DARK, false); }\n"
                                + "public void setDarkMode(boolean v) { sp.edit().putBoolean(KEY_DARK, v).apply(); }\n"
                                + "```\n"
                                + "CampusFixApplication reads it at startup and applies the night "
                                + "mode before any activity inflates — order matters, otherwise users "
                                + "see a flash of the wrong theme.\n\n"
                                + "Limits to respect: no queries or relations (it is a flat map), no "
                                + "huge values (the whole file loads into memory — a multi-MB string "
                                + "will jank every read), and no secrets in plain text (use "
                                + "EncryptedSharedPreferences from androidx.security.crypto). If your "
                                + "data has a structure you query, it belongs in Room instead.",
                        "In a practice app add two booleans (notifications, dark_mode) and an int "
                                + "(launch count) to SharedPreferences; bump the count in "
                                + "onCreate and display it. Then wrap the access in a SettingsRepository "
                                + "class and confirm behaviour is identical after the refactor.",
                        Quiz.list(
                                Quiz.of(
                                        "Difference between apply() and commit()?",
                                        0,
                                        "apply() writes in memory immediately and flushes to disk "
                                                + "asynchronously; commit() blocks until written and returns "
                                                + "success.",
                                        "apply() is async (no result); commit() blocks and returns "
                                                + "success",
                                        "apply() deletes; commit() saves",
                                        "commit() is async",
                                        "No difference"),
                                Quiz.of(
                                        "Why wrap SharedPreferences in a repository class?",
                                        2,
                                        "It centralises key names, typing and defaults so keys never "
                                                + "get mistyped and prefs can be swapped for another store.",
                                        "Because prefs are deprecated",
                                        "To make reads faster",
                                        "Centralises keys/typing; hides the storage choice",
                                        "It is required by XML"),
                                Quiz.of(
                                        "What is wrong with storing a 5 MB JSON blob in prefs?",
                                        1,
                                        "The whole preference file is loaded into memory, so every "
                                                + "read pays for that blob — blobs belong in files or Room.",
                                        "Nothing",
                                        "Whole file loads into memory — jank",
                                        "It corrupts the key",
                                        "Prefs only store ints")),
                        "W"),

                new Lesson(
                        "09.2",
                        "App settings",
                        UNIT,
                        "Building a real settings screen with androidx.preference and reacting to "
                                + "changes.",
                        "Settings screens are mostly declarative: a PreferenceScreen XML + "
                                + "PreferenceFragmentCompat from androidx.preference does the "
                                + "persistence for you:\n"
                                + "```xml\n"
                                + "<PreferenceScreen xmlns:android=\"...\">\n"
                                + "    <SwitchPreferenceCompat\n"
                                + "        android:key=\"notifications_enabled\"\n"
                                + "        android:title=\"@string/settings_notifications\" />\n"
                                + "    <ListPreference\n"
                                + "        android:key=\"theme_mode\"\n"
                                + "        android:title=\"@string/settings_theme\"\n"
                                + "        android:entries=\"@array/theme_entries\"\n"
                                + "        android:entryValues=\"@array/theme_values\"\n"
                                + "        android:defaultValue=\"system\" />\n"
                                + "</PreferenceScreen>\n"
                                + "```\n"
                                + "Each key is read/written to the chosen SharedPreferences file "
                                + "automatically; types (switch, list, edit-text, seekbar) come from "
                                + "the tag. ActivityResult APIs cover 'special' rows like a folder "
                                + "picker.\n\n"
                                + "Reacting to changes is the part people miss. The fragment can "
                                + "listen itself:\n"
                                + "```java\n"
                                + "findPreference(\"notifications_enabled\")\n"
                                + "        .setOnPreferenceChangeListener((pref, newValue) -> {\n"
                                + "            applyNotifications((Boolean) newValue);\n"
                                + "            return true;   // false would veto the write\n"
                                + "        });\n"
                                + "```\n"
                                + "return true lets the new value persist; returning false cancels "
                                + "it — a cheap validation hook. For changes made elsewhere (a sync "
                                + "of defaults, or the theme applied at startup), use "
                                + "SharedPreferences.OnSharedPreferenceChangeListener.\n\n"
                                + "CampusFix's SettingsActivity follows a simpler hand-built layout "
                                + "bound to SettingsRepository, keeping exactly the same "
                                + "contract:\n"
                                + "1. Load current values into the controls in onCreate (no "
                                + "surprises).\n"
                                + "2. On toggle, write through the repository (apply() — async).\n"
                                + "3. React immediately: the dark-mode switch calls "
                                + "AppCompatDelegate.setDefaultNightMode so the whole UI repaints "
                                + "live, while the intro flag only affects the next launch.\n"
                                + "4. Theme choice persists across restarts because "
                                + "CampusFixApplication reads it before inflating anything.\n\n"
                                + "Design guidance: group related settings (Appearance, "
                                + "Notifications, Data), give every row a summary showing the current "
                                + "value (ListPreference.setSummaryProvider), and never require a "
                                + "restart for something you can apply live.",
                        "Add a ListPreference for 'sort default' to a practice preference screen "
                                + "with three entries, set its summary to show the selected value, and "
                                + "verify persistence by killing and relaunching the app. Then veto one "
                                + "change by returning false from the change listener and observe the "
                        + "switch snap back.",
                        Quiz.list(
                                Quiz.of(
                                        "What does a change listener returning false do?",
                                        2,
                                        "It vetoes the change — the new value is not persisted, which "
                                                + "is useful for validation.",
                                        "Saves the value",
                                        "Crashes the app",
                                        "Vetoes the write",
                                        "Restarts the activity"),
                                Quiz.of(
                                        "Which preference type lets users pick one value from a list "
                                                + "with persistence built in?",
                                        1,
                                        "ListPreference stores the chosen value under its key and "
                                                + "renders entries/entryValues from arrays.",
                                        "EditTextPreference",
                                        "ListPreference",
                                        "SwitchPreferenceCompat",
                                        "SeekBarPreference"),
                                Quiz.of(
                                        "Why must CampusFix apply the theme in Application.onCreate?",
                                        0,
                                        "The stored theme must take effect before any activity "
                                                + "inflates, otherwise the wrong theme flashes on launch.",
                                        "To save battery",
                                        "Before any activity inflates — avoids a wrong-theme flash",
                                        "Android requires it",
                                        "It registers the receiver")),
                        "W"));
    }
}
