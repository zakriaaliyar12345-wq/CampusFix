# CampusFix

An Android app for reporting campus maintenance problems — broken projectors,
flickering lights, leaking taps — built as the practical companion to the
*Android Programming* course (32 topics, from 01.0 Introduction to 10.1 Room,
LiveData, and ViewModel).

Every one of the 32 course files is covered either by a **working feature (W)**
in the production code or by a **Learning Lab demonstration (D)** — an in-app
lesson with explanation, Java/XML example, exercise and graded quiz.
[COURSE_TOPICS.md](COURSE_TOPICS.md) is the authoritative topic-by-topic
checklist.

## Features

**Reporting**

- Create, edit, delete and browse campus reports (title, category, location,
  description, priority, optional photo).
- Live filtering: free-text search plus status / category / priority chips and
  menus; sort newest-first or most-urgent-first.
- Status workflow with a per-report history timeline
  (`SUBMITTED → IN_PROGRESS → RESOLVED / REJECTED`).
- Statistics screen with status / category / priority breakdowns.
- Photos via camera (ACTION_IMAGE_CAPTURE + FileProvider) or gallery
  (ACTION_GET_CONTENT); share a report with ACTION_SEND; open its location in
  a maps app via a geo: intent.
- Offline-first: Room (SQLite) is the single source of truth; sample data is
  seeded on first run.

**Learning Lab**

- All 32 course topics as in-app lessons grouped into 10 units, each with a
  fenced Java/XML/SQL example, an exercise and a graded quiz.
- Persisted completion and bookmark progress (Room), filterable by
  all / bookmarks / completed.

**Background & system demos** (driven from the Settings screen)

- Connectivity status (07.2), status-change broadcast → notification (07.3),
  started `Service` (07.4), notification channel + runtime `POST_NOTIFICATIONS`
  permission (08.1), `AlarmManager` reminder with exact-to-inexact fallback
  (08.2), and WorkManager one-time + periodic sync (08.3).
- Deprecated technologies (AsyncTask, AsyncTaskLoader, Support Library,
  JobScheduler) are taught accurately *as deprecated*, with the modern
  replacement used in production code.

## Architecture

Single-module app, classic **MVVM** on Java + AndroidX:

```
app/src/main/java/com/example/campusfix/
├── CampusFixApplication.java   # theme + notification channel bootstrap
├── data/
│   ├── local/                  # Room: CampusDatabase, entities, DAOs, converters
│   ├── repository/             # ReportRepository, LessonProgressRepository, SettingsRepository
│   ├── model/                  # enums + immutable ReportFilter
│   ├── alarm/ broadcast/ sync/ # ReminderReceiver, ReportStatusReceiver, DemoSyncService, SyncWorker
│   └── ...
├── learning/                   # LessonCatalog + Unit*Lessons (lesson content)
├── ui/                         # activities, adapters, ViewModels, ViewModelFactory
└── util/                       # AppExecutors, ConnectivityHelper, NotificationHelper, PhotoUtils, Validator
```

- **Room** (`data/local`) owns persistence; DAOs expose `LiveData` reads and
  synchronous queries for background flows.
- **ViewModels** hold screen state; `ReportFilter` lives in the list ViewModel
  so rotation never loses a filter (02.2).
- **Repository** is the single source of truth; `ReportRepository.buildQuery()`
  builds one parameterized SELECT per filter combination (10.0).
- **Validator** is pure JVM logic so it is unit-testable without a device.
- Tablet layouts in `layout-sw600dp/` reuse the same view IDs for a two-column
  dashboard (05.3).

## Build & run

Requires the Android SDK (see `local.properties`). AGP 9.3.3, minSdk 24,
targetSdk 37, Java 11.

```bash
gradlew.bat :app:assembleDebug          # build the APK
gradlew.bat :app:installDebug           # install on a connected device/emulator
```

## Tests

Three layers, matching course topic 03.2:

| Layer | Location | Command |
|---|---|---|
| Local unit tests (JVM, no device) | `app/src/test` | `gradlew.bat :app:testDebugUnitTest` |
| Instrumented tests (device/emulator) | `app/src/androidTest` | `gradlew.bat :app:connectedDebugAndroidTest` |
| UI tests with Espresso (device/emulator) | `app/src/androidTest` | same command as above |

What each suite covers:

- `LessonCatalogTest` — all 32 lessons exist exactly once, in course order,
  with usable content, a balanced code example and a graded quiz; coverage
  flags match COURSE_TOPICS.md.
- `ValidatorTest` — title/description/location boundary cases and the
  message-key → string-resource map (03.2 exercise: 4-char accept, 81-char
  reject).
- `ReportFilterTest` — immutable filter copy semantics behind the list screen.
- `ReportQueryTest` — the dynamic SQL builder: WHERE/AND composition, LIKE
  wildcards, bind order, urgency `CASE` sort, and that values are bound as
  arguments, never concatenated.
- `BackgroundDemoContractTest` — manifest permissions/components and the
  broadcast action constant for the 07.x/08.x demos.
- `ReportDaoTest` (instrumented) — Room insert/read/update/delete against a
  real in-memory database, including the `@Transaction` report+history delete.
- `DashboardFlowTest` / `ReportListFlowTest` (Espresso) — the 06.1 lesson
  exercise (dashboard → list → create FAB) plus the create form's inline
  validation error path and back navigation.

## Course coverage

32 topics, all accounted for. **W** = working implementation, **D** =
educational demonstration. Full evidence table in
[COURSE_TOPICS.md](COURSE_TOPICS.md).

| Unit | Topics |
|---|---|
| 1 — Intro & UI foundations | 01.0 D · 01.1 D · 01.2 W · 01.3 W · 01.4 D |
| 2 — Activities & Intents | 02.1 W · 02.2 W · 02.3 W |
| 3 — Debugging, Testing & Libraries | 03.1 D · 03.2 W · 03.3 D |
| 4 — UI Controls & Navigation | 04.1 W · 04.2 W · 04.3 W · 04.4 W · 04.5 W |
| 5 — Styles, Themes & Material | 05.1 W · 05.2 W · 05.3 W |
| 6 — UI Testing | 06.1 W |
| 7 — Background & System | 07.1 D · 07.2 W · 07.3 W · 07.4 W |
| 8 — Scheduled Work | 08.1 W · 08.2 W · 08.3 W |
| 9 — Persistence | 09.0 W · 09.1 W · 09.2 W |
| 10 — Local Databases | 10.0 W · 10.1 W |

## Milestones

| Milestone | Delivered |
|---|---|
| M0 | Baseline app + this checklist |
| M1 | Report CRUD, ViewModels, filters, statistics, adaptive tablet layout |
| M2 | Learning Lab: all 32 lessons, reader, quizzes, progress |
| M3 | Background/system demos: connectivity, broadcast, service, notifications, alarms, WorkManager |
| M4 | Tests (unit + instrumented + Espresso), README, finalized topic map |
