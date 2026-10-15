package com.example.campusfix.learning;

import java.util.List;

/** Unit 10 — course files 10.0 … 10.1 (SQLite primer; Room, LiveData, ViewModel). */
final class Unit10Lessons {

    private static final String UNIT = "Unit 10 — Local Databases";

    private Unit10Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "10.0",
                        "SQLite Primer",
                        UNIT,
                        "Raw SQL: tables, parameterized queries, and how Room sits on top.",
                        "SQLite is Android's built-in relational engine — a C library with a file "
                                + "per database. A quick tour of the SQL your app would otherwise "
                                + "write by hand:\n\n"
                                + "```sql\n"
                                + "CREATE TABLE reports (\n"
                                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                                + "    title TEXT NOT NULL,\n"
                                + "    category TEXT,\n"
                                + "    status INTEGER NOT NULL DEFAULT 0,\n"
                                + "    created_at INTEGER NOT NULL\n"
                                + ");\n\n"
                                + "INSERT INTO reports (title, category, status, created_at)\n"
                                + "VALUES (?, ?, ?, ?);\n\n"
                                + "SELECT * FROM reports\n"
                                + "WHERE status = ? AND title LIKE ?\n"
                                + "ORDER BY created_at DESC;\n\n"
                                + "UPDATE reports SET status = ? WHERE id = ?;\n"
                                + "DELETE FROM reports WHERE id = ?;\n"
                                + "```\n\n"
                                + "CampusFix builds these parameterized statements in "
                                + "ReportRepository.buildQuery() — the filter screen assembles SQL:\n"
                                + "```java\n"
                                + "StringBuilder sql = new StringBuilder(\n"
                                + "        \"SELECT r.* FROM reports r WHERE 1=1\");\n"
                                + "if (status >= 0) { sql.append(\" AND r.status = \").append(status); }\n"
                                + "if (query != null && !query.isEmpty()) {\n"
                                + "    sql.append(\" AND r.title LIKE ?\");\n"
                                + "    args.add(\"%\" + query + \"%\");   // user text stays a bound arg\n"
                                + "}\n"
                                + "sql.append(\" ORDER BY r.created_at DESC\");\n"
                                + "```\n"
                                + "Note the discipline: user input never gets concatenated into SQL — "
                                + "it is bound as a ? argument, which is what prevents SQL injection "
                                + "on a phone.\n\n"
                                + "Core concepts: types are dynamic (INTEGER, TEXT, REAL, BLOB, "
                                + "NULL); PRIMARY KEY rows are addressed by id; foreign keys express "
                                + "relations (reports.id → status_history.report_id) but must be "
                                + "enabled per connection (PRAGMA foreign_keys=ON); transactions wrap "
                                + "several statements so they commit together or not at all.\n\n"
                                + "Using raw SQLite in Java means SQLiteOpenHelper, manual cursor "
                                + "loops, string-concatenation risk and no compile-time checks — "
                                + "which is exactly why the course's next topic, Room, exists. Learn "
                                + "this primer so you understand what Room is generating for you.",
                        "Using SQLite Studio, the emulator's app browser, or `adb shell` inside the "
                                + "app's databases folder, run the SELECT in this lesson against the "
                                + "CampusFix DB with a ? parameter for 'wifi', then attempt the same "
                        + "search with a string containing a single quote and explain why parameter binding survived it.",
                        Quiz.list(
                                Quiz.of(
                                        "Why bind user input as ? instead of concatenating?",
                                        1,
                                        "Bound parameters are escaped by SQLite — concatenated input "
                                                + "can break the statement or inject SQL (injection).",
                                        "It is faster",
                                        "Prevents SQL injection / broken SQL",
                                        "It is required by Room",
                                        "Saves memory"),
                                Quiz.of(
                                        "What does a SQL transaction guarantee?",
                                        2,
                                        "Atomicity: all statements commit together or none do — so "
                                                + "related rows never half-update.",
                                        "Encryption",
                                        "Atomic all-or-nothing commit",
                                        "Faster reads",
                                        "Automatic rollback on crash"),
                                Quiz.of(
                                        "What is the main drawback of raw SQLiteOpenHelper code?",
                                        0,
                                        "Boilerplate and no compile-time safety — typos in column "
                                                + "names fail at runtime; Room's codegen eliminates this.",
                                        "It is too slow",
                                        "Boilerplate with no compile-time checks",
                                        "It cannot store strings",
                                        "It requires root")),
                        "W"),

                new Lesson(
                        "10.1",
                        "Room, LiveData, and ViewModel",
                        UNIT,
                        "The modern persistence stack: annotated entities/DAOs, observable LiveData, "
                                + "and ViewModels that survive rotation.",
                        "Room is SQLite with compile-time verification: you write interfaces and "
                                + "annotations; the compiler generates the implementation and fails "
                                + "the build on a bad query.\n\n"
                                + "**Entity** — a table:\n"
                                + "```java\n"
                                + "@Entity(tableName = \"reports\",\n"
                                + "        indices = {@Index(\"status\")})\n"
                                + "public class ReportEntity {\n"
                                + "    @PrimaryKey(autoGenerate = true) public long id;\n"
                                + "    @NonNull public String title = \"\";\n"
                                + "    public int status;\n"
                                + "    public long createdAt;\n"
                                + "}\n"
                                + "```\n\n"
                                + "**DAO** — queries with compile-time checking:\n"
                                + "```java\n"
                                + "@Dao\n"
                                + "public interface ReportDao {\n"
                                + "    @Insert long insert(ReportEntity r);\n"
                                + "    @Update void update(ReportEntity r);\n"
                                + "    @Delete void delete(ReportEntity r);\n"
                                + "    @Query(\"SELECT * FROM reports WHERE status = :status ORDER BY createdAt DESC\")\n"
                                + "    LiveData<List<ReportEntity>> observeByStatus(int status);\n"
                                + "    @Query(\"DELETE FROM reports WHERE id = :id\") void deleteById(long id);\n"
                                + "}\n"
                                + "```\n\n"
                                + "**Database** — wires entities, version and migrations:\n"
                                + "```java\n"
                                + "@Database(entities = {ReportEntity.class}, version = 1, exportSchema = true)\n"
                                + "public abstract class CampusDatabase extends RoomDatabase { }\n"
                                + "```\n\n"
                                + "Three pieces make the stack observable and lifecycle-safe:\n"
                                + "* **LiveData** — a container the UI can observe; Room re-runs the "
                                + "query on a background thread and posts a fresh list whenever the "
                                + "table changes. The UI never polls.\n"
                                + "* **ViewModel** — survives configuration changes (rotation). The "
                                + "Activity asks for it via ViewModelProvider; on rotation the same "
                                + "instance is reused, so no flicker, no reload, no leaked context "
                                + "(never pass an Activity into a ViewModel). CampusFix's "
                                + "ViewModelFactory builds them with the repository they need.\n"
                                + "* **Repository** — the single source of truth mediating DAOs and "
                                + "the main thread. Room forbids main-thread queries by default "
                                + "(allowMainThreadQueries is a test-only escape hatch).\n\n"
                                + "Typical wiring in an Activity:\n"
                                + "```java\n"
                                + "viewModel = new ViewModelProvider(this, factory)\n"
                                + "        .get(ReportListViewModel.class);\n"
                                + "viewModel.getFiltered().observe(this, adapter::submitList);\n"
                                + "```\n"
                                + "LiveData delivers only while the owner is STARTED, so observers "
                                + "never update a paused UI. Because LiveData has no operator on the "
                                + "plain class, transformations use Transformations.switchMap — the "
                                + "filter LiveData in ReportListViewModel selects the inner query by "
                                + "key, exactly what switchMap exists for.\n\n"
                                + "Migrations must be written for schema changes (version + "
                                + "Migration objects) — never drop-and-recreate user data; and "
                                + "always ship entities to Room via the repository so one seam "
                                + "controls threading (Room's own executors, as in "
                                + "LessonProgressRepository.diskIO).",
                        "Trace one CampusFix feature end-to-end on paper: XML filter chip → "
                                + "ViewModel LiveData → switchMap → repository → DAO @Query → "
                                + "LiveData callback → DiffUtil submitList. Mark the thread on each "
                        + "arrow. Then find the @Database version number in CampusDatabase and explain what a migration would add.",
                        Quiz.list(
                                Quiz.of(
                                        "What does a ViewModel guarantee across screen rotation?",
                                        1,
                                        "The same instance survives, so loaded data and state persist "
                                                + "without reloading or leaking the old Activity.",
                                        "It reloads data",
                                        "It survives configuration changes",
                                        "It stores data on disk",
                                        "It kills the activity"),
                                Quiz.of(
                                        "What does switchMap contribute in ReportListViewModel?",
                                        0,
                                        "It maps one LiveData (the filter) into a different inner "
                                                + "LiveData (the matching query) and switches whenever the "
                                                + "source emits — LiveData has no built-in operators, so "
                                                + "Transformations.switchMap provides it.",
                                        "It merges two lists",
                                        "Transforms the filter LiveData into the inner query LiveData, "
                                                + "switching sources on each change",
                                        "Renames keys",
                                        "Switches threads"),
                                Quiz.of(
                                        "Why should an Activity never be passed into a ViewModel?",
                                        2,
                                        "The ViewModel outlives the Activity across rotation — a "
                                                + "reference would leak every destroyed instance.",
                                        "It crashes immediately",
                                        "ViewModels are static",
                                        "It would leak the destroyed Activity",
                                        "Intents cannot hold references")),
                        "W"));
    }
}
