package com.example.campusfix.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Local unit tests for the immutable list-filter snapshot (topics 03.2 —
 * local unit tests, and 04.3 — filter/sort menus that drive it).
 *
 * The report list mutates a copy via the with* helpers, so the contract that
 * matters is: every helper returns a NEW instance, leaves the original
 * untouched, and only changes its own field.
 */
public class ReportFilterTest {

    @Test
    public void defaultFilterIsEmptyAndInactive() {
        ReportFilter filter = new ReportFilter();

        assertEquals("", filter.query);
        assertNull(filter.status);
        assertNull(filter.category);
        assertNull(filter.priority);
        assertTrue(filter.newestFirst);
        assertFalse(filter.isActive());
    }

    @Test
    public void nullQueryIsNormalisedToEmpty() {
        ReportFilter filter = new ReportFilter(null, null, null, null, true);
        assertEquals("", filter.query);
        assertFalse(filter.isActive());
    }

    @Test
    public void queryIsTrimmedOnConstruction() {
        ReportFilter filter = new ReportFilter("  wifi  ", null, null, null, true);
        assertEquals("wifi", filter.query);
        assertTrue(filter.isActive());
    }

    @Test
    public void anySingleFilterMakesTheFilterActive() {
        assertTrue(new ReportFilter("", ReportStatus.RESOLVED, null, null, true)
                .isActive());
        assertTrue(new ReportFilter("", null, ReportCategory.WIFI_NETWORK, null, true)
                .isActive());
        assertTrue(new ReportFilter("", null, null, ReportPriority.CRITICAL, true)
                .isActive());
        assertTrue(new ReportFilter("tap", null, null, null, true).isActive());
    }

    @Test
    public void withHelpersReturnNewInstancesAndPreserveTheOthers() {
        ReportFilter original = new ReportFilter();

        ReportFilter searched = original.withQuery("projector");
        assertEquals("projector", searched.query);
        assertEquals("", original.query);
        assertTrue(searched.isActive());

        ReportFilter statused = searched.withStatus(ReportStatus.IN_PROGRESS);
        assertEquals(ReportStatus.IN_PROGRESS, statused.status);
        assertEquals("projector", statused.query);
        assertNull(searched.status);

        ReportFilter categorized = statused.withCategory(ReportCategory.FURNITURE);
        assertEquals(ReportCategory.FURNITURE, categorized.category);
        assertEquals(ReportStatus.IN_PROGRESS, categorized.status);
        assertNull(statused.category);

        ReportFilter prioritized = categorized.withPriority(ReportPriority.HIGH);
        assertEquals(ReportPriority.HIGH, prioritized.priority);
        assertEquals(ReportCategory.FURNITURE, prioritized.category);
        assertNull(categorized.priority);

        ReportFilter sorted = prioritized.withNewestFirst(false);
        assertFalse(sorted.newestFirst);
        assertTrue(prioritized.newestFirst);
        assertEquals(ReportPriority.HIGH, sorted.priority);
    }

    @Test
    public void clearedReturnsAFreshInactiveFilter() {
        ReportFilter busy = new ReportFilter("wifi",
                ReportStatus.SUBMITTED, ReportCategory.WIFI_NETWORK,
                ReportPriority.LOW, false);

        ReportFilter cleared = busy.cleared();
        assertEquals("", cleared.query);
        assertNull(cleared.status);
        assertNull(cleared.category);
        assertNull(cleared.priority);
        assertTrue(cleared.newestFirst);
        assertFalse(cleared.isActive());

        // The original keeps its state.
        assertTrue(busy.isActive());
        assertFalse(busy.newestFirst);
    }
}
