package com.example.campusfix;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test — runs on a real device or emulator.
 * Execute with: gradlew.bat connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        Context appContext =
                InstrumentationRegistry.getInstrumentation()
                        .getTargetContext();

        assertEquals("com.example.campusfix",
                appContext.getPackageName());
    }
}
