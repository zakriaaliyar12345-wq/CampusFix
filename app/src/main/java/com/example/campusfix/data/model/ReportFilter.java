package com.example.campusfix.data.model;

/**
 * Immutable snapshot of the current list filters. The report list screen
 * mutates a copy and pushes it through a ViewModel whenever the user
 * changes search text, chips, sort order or dialog selections.
 */
public class ReportFilter {

    /** Free-text matched against title and description. Empty = no search. */
    public final String query;

    /** Null = all statuses. */
    public final ReportStatus status;

    /** Null = all categories. */
    public final ReportCategory category;

    /** Null = all priorities. */
    public final ReportPriority priority;

    /** true = newest first, false = most urgent first. */
    public final boolean newestFirst;

    public ReportFilter(String query,
                        ReportStatus status,
                        ReportCategory category,
                        ReportPriority priority,
                        boolean newestFirst) {
        this.query = query == null ? "" : query.trim();
        this.status = status;
        this.category = category;
        this.priority = priority;
        this.newestFirst = newestFirst;
    }

    /** Convenience constructor: empty search, no filters, newest first. */
    public ReportFilter() {
        this("", null, null, null, true);
    }

    public boolean isActive() {
        return !query.isEmpty()
                || status != null
                || category != null
                || priority != null;
    }

    public ReportFilter withQuery(String newQuery) {
        return new ReportFilter(newQuery, status, category, priority, newestFirst);
    }

    public ReportFilter withStatus(ReportStatus newStatus) {
        return new ReportFilter(query, newStatus, category, priority, newestFirst);
    }

    public ReportFilter withCategory(ReportCategory newCategory) {
        return new ReportFilter(query, status, newCategory, priority, newestFirst);
    }

    public ReportFilter withPriority(ReportPriority newPriority) {
        return new ReportFilter(query, status, category, newPriority, newestFirst);
    }

    public ReportFilter withNewestFirst(boolean value) {
        return new ReportFilter(query, status, category, priority, value);
    }

    public ReportFilter cleared() {
        return new ReportFilter();
    }
}
