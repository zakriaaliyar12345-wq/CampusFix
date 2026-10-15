# CampusFix — Android Course Topic Checklist

Source of truth: the 32 course PPTX files in `C:\Users\Zakria\Desktop\Adroid Programming`
(01.0 Introduction … 10.1 Room, LiveData, and ViewModel).

Coverage types:

- **W** = working implementation in the production app code
- **D** = educational demonstration (lesson content: explanation, Java/XML example, exercise)
- **T** = covered by an automated test

States: `Not started` → `In progress` → `Implemented` → `Build verified` → `Test verified`
(or `Blocked by an external prerequisite`).

A topic may only be marked **Test verified** after its lesson content and required
demonstration exist, the project builds, and any associated test passes.

---

| # | Topic (course file name) | Type | State | Evidence in CampusFix |
|---|---|---|---|---|
| 01.0 | Introduction to Android | D | Test verified | Learning Lab lesson `01.0` (content + fenced example + quiz), covered by `LessonCatalogTest` |
| 01.1 | Your first Android app | D | Test verified | Learning Lab lesson `01.1`, covered by `LessonCatalogTest` |
| 01.2 | Layouts and resources for the UI | W | Build verified | `res/layout/*`, `values/{strings,dimens,colors,themes}.xml` |
| 01.3 | Text and scrolling views | W | Build verified | `activity_dashboard.xml` (ScrollView), TextViews, TextInputLayout, multi-line editor |
| 01.4 | Resources to help you learn | D | Test verified | Learning Lab lesson `01.4` + About screen resources; covered by `LessonCatalogTest` |
| 02.1 | Activities and Intents | W | Build verified | Dashboard → list → detail → editor; 10 activities in manifest with parentActivityName |
| 02.2 | Activity lifecycle and state | W | Build verified | Filter in ViewModel (rotation), `onSaveInstanceState` search text, discard-changes back guard |
| 02.3 | Implicit Intents | W | Build verified | `ReportDetailActivity` share (ACTION_SEND) + maps (geo), `CreateReportActivity` camera/gallery |
| 03.1 | The Android Studio debugger | D | Test verified | Lesson `03.1` (breakpoints, Evaluate Expression, guarded logging); covered by `LessonCatalogTest` |
| 03.2 | App testing | W | Test verified | Three test layers in place: `ValidatorTest`/`ReportFilterTest`/`ReportQueryTest` (local), `ReportDaoTest` (instrumented Room), `DashboardFlowTest`/`ReportListFlowTest` (Espresso); lesson `03.2` |
| 03.3 | The Android Support Library | D | Test verified | Lesson `03.3` (support-lib → AndroidX history, gradle.properties flags); covered by `LessonCatalogTest` |
| 04.1 | Buttons and clickable images | W | Build verified | MaterialButtons across dashboard, forms, detail actions |
| 04.2 | Input controls | W | Build verified | `activity_create_report.xml` (EditText, exposed dropdown AutoComplete, Chips), switches in settings |
| 04.3 | Menus and pickers | W | Build verified | `res/menu/{menu_sort,menu_category,menu_priority}.xml` via PopupMenu; status single-choice dialog |
| 04.4 | User navigation | W | Build verified | Dashboard hub → list → detail → editor; Up navigation via parentActivityName; FAB/empty-state entry points |
| 04.5 | RecyclerView | W | Build verified | `ReportAdapter` (ListAdapter + DiffUtil), `StatusHistoryAdapter`, `LessonAdapter`, `item_stat_row` |
| 05.1 | Drawables, styles, and themes | W | Build verified | `res/drawable/*` vectors + shapes, `values/themes.xml`, day/night |
| 05.2 | Material Design | W | Build verified | MaterialCardView, Chips, FAB, TextInputLayout, MaterialAlertDialogBuilder |
| 05.3 | Resources for adaptive layouts | W | Build verified | `layout-sw600dp/activity_dashboard.xml` (two-column tablet dashboard, same IDs) |
| 06.1 | UI testing | W | Test verified | Lesson `06.1`; `DashboardFlowTest` implements the lesson exercise (dashboard → list → FAB), `ReportListFlowTest` adds the inline-validation error path; run via `connectedDebugAndroidTest` |
| 07.1 | AsyncTask and AsyncTaskLoader | D | Test verified | Lesson `07.1` teaches both as deprecated with executor/WorkManager replacements; `AppExecutors` javadoc; covered by `LessonCatalogTest` |
| 07.2 | Internet connection | W | Test verified | `ConnectivityHelper` + Settings demo status; `ACCESS_NETWORK_STATE`; lesson `07.2`; `BackgroundDemoContractTest` |
| 07.3 | Broadcasts | W | Test verified | Status change sends package-restricted broadcast to `ReportStatusReceiver` (notification); lesson `07.3`; contract test (action match) |
| 07.4 | Services | W | Test verified | `DemoSyncService` (executor steps + `stopSelf`) launched from Settings; lesson `07.4`; contract test (manifest component) |
| 08.1 | Notifications | W | Test verified | `NotificationHelper` (channel, builder, switch + `POST_NOTIFICATIONS` runtime request); lesson `08.1`; contract test (permission + unique ids) |
| 08.2 | Alarms | W | Test verified | `ReminderReceiver` with exact-to-inexact fallback, 10 s demo reminder + cancel; lesson `08.2`; contract test (permission + component) |
| 08.3 | Efficient data transfer and JobScheduler | W | Test verified | `SyncWorker` one-time (CONNECTED) + unique 15-min periodic (unmetered, charging) via WorkManager; lesson `08.3`; contract test (dependency) |
| 09.0 | Data Storage | W | Build verified | Photos in internal cache (`PhotoUtils`), prefs (`SettingsRepository`), Room DB; lesson `09.0` |
| 09.1 | Shared Preferences | W | Build verified | `SettingsRepository` (theme, notifications, intro flag) used by `CampusFixApplication` + Settings |
| 09.2 | App settings | W | Build verified | `SettingsActivity` + `activity_settings.xml` |
| 10.0 | SQLite Primer | W | Test verified | `ReportRepository.buildQuery()` — real parameterized SQL; lesson `10.0`; `ReportQueryTest` (bind order, LIKE wildcards, `CASE` sort, no string concatenation) |
| 10.1 | Room, LiveData, and ViewModel | W | Test verified | Entities/DAOs/DB/repository + LiveData + 4 ViewModels + `ViewModelFactory`; `ReportDaoTest` (instrumented Room, incl. `@Transaction` delete) |

---

## Milestone map

| Milestone | Topics advanced |
|---|---|
| M0 (baseline + this checklist) | 01.2, 01.3, 02.1, 04.1, 04.2, 04.5, 05.1, 05.2, 09.1, 09.2 already in place; checklist created |
| M1 (report CRUD + ViewModels) | 02.2, 02.3, 04.3, 04.4, 05.3, 10.1 → Build verified |
| M2 (Learning Lab) | All 32 lessons authored + Learning/Lesson/Quiz screens built; 01.0, 01.1, 01.4, 03.1, 03.3, 07.1 → Test verified (lesson + `LessonCatalogTest`); 09.0, 10.0 → Build verified |
| M3 (background/system) | 07.2, 07.3, 07.4, 08.1, 08.2, 08.3 → Test verified (lesson + Settings-screen demos + build + `BackgroundDemoContractTest`) |
| M4 (tests + docs) | 03.2, 06.1, 10.0, 10.1 → Test verified; `ValidatorTest`, `ReportFilterTest`, `ReportQueryTest`, `DashboardFlowTest`, extended `ReportListFlowTest`; README; topic map finalized |

## Rules

1. A topic is marked complete only after its lesson content/demonstration is implemented **and** verified by a successful build (and test, where one exists).
2. Deprecated technologies (AsyncTask, AsyncTaskLoader, Support Library, JobScheduler) are taught accurately as deprecated, with the modern replacement used in production code.
3. No topic is silently omitted, merged away, or replaced with generic content.
4. Final verification compares this table row-by-row against the 32 course file names.

## Final verification (M4)

- All 32 rows present, one per course file, in course order — enforced by `LessonCatalogTest` (`catalogHasExactlyThe32CourseTopics`, `lessonsAreListedInCourseOrder`), which also pins every row's W/D flag (`coverageMatchesTheCourseChecklist`).
- `gradlew :app:testDebugUnitTest` — 40 tests, 0 failures.
- `gradlew :app:assembleDebug` — APK builds.
- `gradlew :app:connectedDebugAndroidTest` — instrumented + Espresso suites compile and are ready to run on a device/emulator.

