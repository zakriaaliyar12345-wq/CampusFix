package com.example.campusfix;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.example.campusfix.data.broadcast.ReportStatusReceiver;
import com.example.campusfix.util.NotificationHelper;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Source-level contract tests for the background/system demonstrations
 * (topics 07.2, 07.3, 07.4, 08.1, 08.2, 08.3).
 *
 * Plain JVM tests cannot exercise AlarmManager, Service or WorkManager, so
 * these assert the declarations those demos depend on: the manifest must
 * request the permissions and register the components, and the broadcast
 * action wired in the manifest must equal the constant the sender uses —
 * a rename on either side would silently break the 07.3 chain.
 */
public class BackgroundDemoContractTest {

    private static String readManifest() {
        Path[] candidates = {
                Paths.get("src", "main", "AndroidManifest.xml"),
                Paths.get("app", "src", "main", "AndroidManifest.xml")
        };
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                try {
                    return new String(Files.readAllBytes(candidate),
                            StandardCharsets.UTF_8);
                } catch (Exception e) {
                    fail("Cannot read manifest: " + e);
                }
            }
        }
        fail("AndroidManifest.xml not found from "
                + Paths.get("").toAbsolutePath());
        return null;
    }

    @Test
    public void manifestDeclaresBackgroundComponents() {
        String manifest = readManifest();

        // 07.4 — started service must exist and stay private to the app.
        assertTrue(manifest.contains(".data.sync.DemoSyncService"));
        assertTrue(manifest.contains(".data.sync.DemoSyncService\"\n"
                + "            android:exported=\"false\""));

        // 08.2 — manifest receiver for AlarmManager delivery.
        assertTrue(manifest.contains(".data.alarm.ReminderReceiver"));
        assertTrue(manifest.contains(".data.alarm.ReminderReceiver\"\n"
                + "            android:exported=\"false\""));

        // 07.3 — manifest receiver for the in-app status broadcast.
        assertTrue(manifest.contains(".data.broadcast.ReportStatusReceiver"));
        assertTrue(manifest.contains(".data.broadcast.ReportStatusReceiver\"\n"
                + "            android:exported=\"false\""));
    }

    @Test
    public void manifestRequestsSystemPermissions() {
        String manifest = readManifest();

        assertTrue("07.2 connectivity needs ACCESS_NETWORK_STATE",
                manifest.contains("android.permission.ACCESS_NETWORK_STATE"));
        assertTrue("08.1 posting needs POST_NOTIFICATIONS on Android 13+",
                manifest.contains("android.permission.POST_NOTIFICATIONS"));
        assertTrue("08.2 exact alarms need SCHEDULE_EXACT_ALARM",
                manifest.contains("android.permission.SCHEDULE_EXACT_ALARM"));
    }

    @Test
    public void manifestActionMatchesBroadcastConstant() {
        // Sender (code) and receiver (manifest filter) must agree exactly.
        assertTrue(readManifest().contains(
                ReportStatusReceiver.ACTION_REPORT_STATUS_CHANGED));
    }

    @Test
    public void workManagerDependencyIsDeclared() {
        // 08.3: SyncWorker cannot compile/run without androidx.work —
        // guard the Gradle declaration so it is not dropped silently.
        Path[] candidates = {
                Paths.get("build.gradle.kts"),
                Paths.get("app", "build.gradle.kts")
        };
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                try {
                    String script = new String(
                            Files.readAllBytes(candidate),
                            StandardCharsets.UTF_8);
                    assertTrue("libs.work.runtime must stay declared",
                            script.contains("libs.work.runtime"));
                    return;
                } catch (Exception e) {
                    fail("Cannot read build script: " + e);
                }
            }
        }
        fail("build.gradle.kts not found from "
                + Paths.get("").toAbsolutePath());
    }

    @Test
    public void notificationIdsDoNotCollide() {
        // Distinct fixed ids so the test / sync / reminder notifications
        // replace themselves instead of overwriting each other, and the
        // report-status range starts above all fixed ids.
        assertNotEquals(NotificationHelper.ID_TEST,
                NotificationHelper.ID_SYNC);
        assertNotEquals(NotificationHelper.ID_TEST,
                NotificationHelper.ID_REMINDER);
        assertNotEquals(NotificationHelper.ID_SYNC,
                NotificationHelper.ID_REMINDER);
        assertNotEquals(NotificationHelper.ID_REMINDER,
                NotificationHelper.ID_STATUS_BASE);
        assertTrue(NotificationHelper.ID_STATUS_BASE
                > NotificationHelper.ID_REMINDER);
    }
}
