package com.example.campusfix.util;

import androidx.annotation.Nullable;

/**
 * One-shot event wrapper for LiveData values that should be handled only
 * once (navigate, finish, show a toast) — plain LiveData would redeliver the
 * same value to a new observer after a configuration change.
 *
 * Pure Java with no Android dependencies so it is unit-testable.
 */
public class Event<T> {

    private final T content;
    private boolean handled;

    public Event(T content) {
        this.content = content;
    }

    /** Returns the content exactly once; null on subsequent calls. */
    @Nullable
    public T getContentIfNotHandled() {
        if (handled) return null;
        handled = true;
        return content;
    }

    /** Peek without consuming (for layout binding). */
    public T peek() {
        return content;
    }
}
