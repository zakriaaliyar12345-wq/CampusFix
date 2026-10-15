package com.example.campusfix.data.repository;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteProgram;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportFilter;
import com.example.campusfix.data.model.ReportPriority;
import com.example.campusfix.data.model.ReportStatus;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Local unit tests for the dynamic SQL builder (topics 10.0 — SQLite primer:
 * one real parameterized SELECT per filter combination, and 10.1 — the
 * repository as the single source of truth that owns the query).
 *
 * Everything here runs on the JVM in milliseconds: no emulator and no
 * database. Bound values are captured by handing {@code bindTo} a recording
 * statement, which is exactly how Room will bind them later — the instrumented
 * {@code ReportDaoTest} then proves the same statements run against real
 * SQLite.
 */
public class ReportQueryTest {

    /**
     * Captures the 1-based bind calls the query makes, so tests can assert
     * argument values and their order without a real SQLite statement.
     */
    private static final class RecordingStatement implements SupportSQLiteProgram {
        private final Map<Integer, Object> bindings = new HashMap<>();

        @Override
        public void bindNull(int index) {
            bindings.put(index, null);
        }

        @Override
        public void bindLong(int index, long value) {
            bindings.put(index, value);
        }

        @Override
        public void bindDouble(int index, double value) {
            bindings.put(index, value);
        }

        @Override
        public void bindString(int index, String value) {
            bindings.put(index, value);
        }

        @Override
        public void bindBlob(int index, byte[] value) {
            bindings.put(index, value);
        }

        @Override
        public void clearBindings() {
            bindings.clear();
        }

        @Override
        public void close() {
        }

        Object valueAt(int index) {
            return bindings.get(index);
        }
    }

    private static String sqlOf(SupportSQLiteQuery query) {
        return query.getSql();
    }

    /** Bound arguments in placeholder order, exactly as Room receives them. */
    private static Object[] argsOf(SupportSQLiteQuery query) {
        RecordingStatement statement = new RecordingStatement();
        query.bindTo(statement);

        Object[] args = new Object[query.getArgCount()];
        for (int i = 0; i < args.length; i++) {
            args[i] = statement.valueAt(i + 1);
        }
        return args;
    }

    @Test
    public void defaultFilterSelectsEverythingNewestFirst() {
        SupportSQLiteQuery query =
                ReportRepository.buildQuery(new ReportFilter());

        assertEquals("SELECT * FROM reports ORDER BY createdAt DESC",
                sqlOf(query));
        assertEquals(0, query.getArgCount());
    }

    @Test
    public void statusFilterAddsOneBoundArgument() {
        SupportSQLiteQuery query = ReportRepository.buildQuery(
                new ReportFilter("", ReportStatus.IN_PROGRESS, null, null, true));

        assertEquals("SELECT * FROM reports WHERE status = ?"
                + " ORDER BY createdAt DESC", sqlOf(query));
        assertArrayEquals(new Object[]{"IN_PROGRESS"}, argsOf(query));
    }

    @Test
    public void everyFilterIsAndJoinedInDeclarationOrder() {
        SupportSQLiteQuery query = ReportRepository.buildQuery(
                new ReportFilter("wifi", ReportStatus.SUBMITTED,
                        ReportCategory.WIFI_NETWORK, ReportPriority.HIGH, true));

        assertEquals("SELECT * FROM reports"
                        + " WHERE status = ?"
                        + " AND category = ?"
                        + " AND priority = ?"
                        + " AND (title LIKE ? OR description LIKE ? OR location LIKE ?)"
                        + " ORDER BY createdAt DESC",
                sqlOf(query));

        // Bind order must match placeholder order, and LIKE wraps both sides.
        String like = "%wifi%";
        assertArrayEquals(new Object[]{
                "SUBMITTED",
                "WIFI_NETWORK",
                "HIGH",
                like, like, like
        }, argsOf(query));
    }

    @Test
    public void searchQueryIsTrimmedBeforeBinding() {
        SupportSQLiteQuery query = ReportRepository.buildQuery(
                new ReportFilter("  tap  ", null, null, null, true));

        assertEquals("SELECT * FROM reports"
                        + " WHERE (title LIKE ? OR description LIKE ? OR location LIKE ?)"
                        + " ORDER BY createdAt DESC",
                sqlOf(query));
        assertArrayEquals(new Object[]{"%tap%", "%tap%", "%tap%"},
                argsOf(query));
    }

    @Test
    public void urgentSortUsesACaseExpressionThenNewest() {
        SupportSQLiteQuery query = ReportRepository.buildQuery(
                new ReportFilter().withNewestFirst(false));

        String sql = sqlOf(query);
        assertTrue(sql.startsWith("SELECT * FROM reports ORDER BY CASE priority"));
        assertTrue(sql.contains("WHEN 'CRITICAL' THEN 0"));
        assertTrue(sql.contains("WHEN 'HIGH' THEN 1"));
        assertTrue(sql.contains("WHEN 'MEDIUM' THEN 2"));
        assertTrue(sql.contains("WHEN 'LOW' THEN 3"));
        assertTrue(sql.endsWith("createdAt DESC"));
        assertEquals(0, query.getArgCount());
    }

    @Test
    public void filterValuesAreBoundAsArgumentsNeverConcatenated() {
        // A hostile/odd search string must stay a bound value — the SQL text
        // itself never contains it (topic 10.0: parameterized queries).
        String hostile = "wifi'; DROP TABLE reports;--";
        SupportSQLiteQuery query = ReportRepository.buildQuery(
                new ReportFilter(hostile, null, null, null, true));

        assertFalse(sqlOf(query).contains("DROP TABLE"));
        assertArrayEquals(new Object[]{
                "%" + hostile + "%", "%" + hostile + "%", "%" + hostile + "%"
        }, argsOf(query));
    }

    @Test
    public void resultIsASimpleSQLiteQuerySoRoomCanExecuteIt() {
        // Room's @RawQuery accepts SupportSQLiteQuery; keep the concrete type
        // stable so the DAO signature keeps compiling.
        assertTrue(ReportRepository.buildQuery(new ReportFilter())
                instanceof SimpleSQLiteQuery);
    }
}
