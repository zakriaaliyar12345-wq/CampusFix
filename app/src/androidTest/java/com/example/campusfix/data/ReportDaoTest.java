package com.example.campusfix.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.campusfix.data.local.CampusDatabase;
import com.example.campusfix.data.local.dao.ReportDao;
import com.example.campusfix.data.local.dao.StatusHistoryDao;
import com.example.campusfix.data.local.entity.ReportEntity;
import com.example.campusfix.data.local.entity.StatusHistoryEntity;
import com.example.campusfix.data.model.ReportCategory;
import com.example.campusfix.data.model.ReportStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented DAO tests (topic 03.2 — app testing: unit, integration and
 * instrumented layers). Uses an in-memory Room database through the same
 * {@link CampusDatabase#createInMemory} helper the app itself uses, so the
 * schema, converters and queries under test are the production ones.
 *
 * Runs on a device or emulator: gradlew.bat connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4.class)
public class ReportDaoTest {

    private CampusDatabase db;
    private ReportDao reports;
    private StatusHistoryDao history;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = CampusDatabase.createInMemory(context);
        reports = db.reportDao();
        history = db.statusHistoryDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void insertThenReadBack() {
        ReportEntity report = new ReportEntity();
        report.title = "Broken projector";
        report.category = ReportCategory.OTHER;
        report.location = "Block C, Room 204";
        report.description = "The ceiling projector flickers and shuts off.";
        report.status = ReportStatus.SUBMITTED;

        long id = reports.insert(report);
        assertTrue(id > 0);

        ReportEntity loaded = reports.getByIdSync(id);
        assertNotNull(loaded);
        assertEquals("Broken projector", loaded.title);
        assertEquals(ReportStatus.SUBMITTED, loaded.status);
        assertEquals(1, reports.getTotalCountSync());
    }

    @Test
    public void updateStatusPersists() {
        ReportEntity report = new ReportEntity();
        report.title = "Leaking tap";

        long id = reports.insert(report);
        reports.updateStatus(id, ReportStatus.IN_PROGRESS, 4242L);

        ReportEntity loaded = reports.getByIdSync(id);
        assertNotNull(loaded);
        assertEquals(ReportStatus.IN_PROGRESS, loaded.status);
        assertEquals(4242L, loaded.updatedAt);
    }

    @Test
    public void deleteRemovesReportAndItsHistory() {
        ReportEntity report = new ReportEntity();
        report.title = "Flickering light";
        long id = reports.insert(report);

        StatusHistoryEntity entry = new StatusHistoryEntity();
        entry.reportId = id;
        entry.status = ReportStatus.SUBMITTED;
        entry.note = "Reported";
        entry.timestamp = 1L;
        history.insert(entry);
        assertEquals(1, history.getForReportSync(id).size());

        // Atomic transaction declared on the DAO (report + history together).
        reports.deleteReportWithHistory(id);

        assertNull(reports.getByIdSync(id));
        assertTrue(history.getForReportSync(id).isEmpty());
        assertEquals(0, reports.getTotalCountSync());
    }
}
