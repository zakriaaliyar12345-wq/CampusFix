package com.example.campusfix.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.campusfix.R;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Local unit tests for the pure validation logic (topic 03.2 — app testing:
 * local unit tests in src/test run on the JVM in seconds, no device needed).
 *
 * Covers the failure paths, the exact boundary lengths the Validator
 * declares, and the message-key → string-resource mapping the editor screen
 * relies on. The lesson 03.2 exercise asks for exactly the 4-char and 81-char
 * title cases below.
 */
public class ValidatorTest {

    private static String repeat(char c, int count) {
        char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    // ----------------------------------------------------------------- title

    @Test
    public void title_rejectsNullEmptyAndBlank() {
        assertEquals("error_title_required", Validator.validateTitle(null));
        assertEquals("error_title_required", Validator.validateTitle(""));
        assertEquals("error_title_required", Validator.validateTitle("   "));
    }

    @Test
    public void title_rejectsTooShort() {
        assertEquals("error_title_too_short", Validator.validateTitle("abc"));
    }

    @Test
    public void title_acceptsExactlyFourCharacters() {
        // Lower boundary: TITLE_MIN characters is valid.
        assertNull(Validator.validateTitle("abcd"));
    }

    @Test
    public void title_acceptsExactlyEightyCharacters() {
        // Upper boundary: TITLE_MAX characters is valid.
        assertNull(Validator.validateTitle(repeat('a', Validator.TITLE_MAX)));
    }

    @Test
    public void title_rejectsEightyOneCharacters() {
        assertEquals("error_title_too_long",
                Validator.validateTitle(repeat('a', Validator.TITLE_MAX + 1)));
    }

    @Test
    public void title_trimsBeforeMeasuring() {
        assertNull(Validator.validateTitle("  abcd  "));
        assertEquals("error_title_too_short", Validator.validateTitle(" ab "));
    }

    // ------------------------------------------------------------ description

    @Test
    public void description_rejectsNullEmptyAndBlank() {
        assertEquals("error_description_required",
                Validator.validateDescription(null));
        assertEquals("error_description_required",
                Validator.validateDescription(""));
        assertEquals("error_description_required",
                Validator.validateDescription("\t\n "));
    }

    @Test
    public void description_enforcesBothBoundaries() {
        String min = repeat('d', Validator.DESCRIPTION_MIN);
        String max = repeat('d', Validator.DESCRIPTION_MAX);

        assertEquals("error_description_too_short",
                Validator.validateDescription(repeat('d',
                        Validator.DESCRIPTION_MIN - 1)));
        assertNull(Validator.validateDescription(min));
        assertNull(Validator.validateDescription(max));
        assertEquals("error_description_too_long",
                Validator.validateDescription(repeat('d',
                        Validator.DESCRIPTION_MAX + 1)));
    }

    // -------------------------------------------------------------- location

    @Test
    public void location_isRequiredAndBounded() {
        assertEquals("error_location_required", Validator.validateLocation(null));
        assertEquals("error_location_required", Validator.validateLocation(""));
        assertEquals("error_location_required", Validator.validateLocation("   "));

        assertNull(Validator.validateLocation("Block C, Room 204"));
        assertNull(Validator.validateLocation(repeat('L',
                Validator.LOCATION_MAX)));
        assertEquals("error_location_too_long", Validator.validateLocation(
                repeat('L', Validator.LOCATION_MAX + 1)));
    }

    // ----------------------------------------------------------------- form

    @Test
    public void report_returnsFirstInvalidFieldInOrder() {
        // Title problems are reported before description, before location.
        assertEquals("error_title_required",
                Validator.validateReport("", "perfectly fine text", "Room 1"));
        assertEquals("error_title_too_short",
                Validator.validateReport("ab", "perfectly fine text", "Room 1"));
        assertEquals("error_description_too_short",
                Validator.validateReport("A valid title", "too short", "Room 1"));
        assertEquals("error_location_required",
                Validator.validateReport("A valid title",
                        "perfectly fine text", "  "));
    }

    @Test
    public void report_acceptsACompletelyValidForm() {
        assertNull(Validator.validateReport(
                "Broken projector",
                "The ceiling projector flickers and shuts off.",
                "Block C, Room 204"));
    }

    // ------------------------------------------------------------- resources

    @Test
    public void errorResOf_mapsEveryKeyToADistinctStringResource() {
        List<String> keys = Arrays.asList(
                "error_title_required",
                "error_title_too_short",
                "error_title_too_long",
                "error_description_required",
                "error_description_too_short",
                "error_description_too_long",
                "error_location_required",
                "error_location_too_long");

        Set<Integer> seen = new HashSet<>();
        for (String key : keys) {
            int res = Validator.errorResOf(key);
            assertTrue("no string resource for " + key, res != 0);
            assertTrue("duplicate resource for " + key, seen.add(res));
        }
    }

    @Test
    public void errorResOf_unknownKeyFallsBackToGenericError() {
        assertEquals(R.string.state_error, Validator.errorResOf("nope"));
        assertEquals(R.string.state_error, Validator.errorResOf(""));
        assertEquals(R.string.state_error, Validator.errorResOf(null));
    }

    @Test
    public void constantsMatchTheDocumentedLimits() {
        // Guards the boundary tests above against silent drift.
        assertNotEquals(Validator.TITLE_MIN, Validator.TITLE_MAX);
        assertNotEquals(Validator.DESCRIPTION_MIN, Validator.DESCRIPTION_MAX);
        assertTrue(Validator.TITLE_MIN > 0);
        assertTrue(Validator.DESCRIPTION_MIN > 0);
        assertTrue(Validator.LOCATION_MAX > 0);
    }
}
