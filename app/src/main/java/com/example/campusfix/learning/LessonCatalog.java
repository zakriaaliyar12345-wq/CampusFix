package com.example.campusfix.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The complete Android course: all 32 topics from the course slides
 * (01.0 Introduction … 10.1 Room, LiveData, and ViewModel), each turned
 * into a lesson with explanation, code example, exercise and quiz.
 *
 * Immutable after class initialization; verified exhaustively by
 * LessonCatalogTest so a topic can never be silently dropped.
 */
public final class LessonCatalog {

    /** The canonical 32 course ids — must match the course slide files. */
    public static final String[] REQUIRED_IDS = {
            "01.0", "01.1", "01.2", "01.3", "01.4",
            "02.1", "02.2", "02.3",
            "03.1", "03.2", "03.3",
            "04.1", "04.2", "04.3", "04.4", "04.5",
            "05.1", "05.2", "05.3",
            "06.1",
            "07.1", "07.2", "07.3", "07.4",
            "08.1", "08.2", "08.3",
            "09.0", "09.1", "09.2",
            "10.0", "10.1"
    };

    private static final List<Lesson> ALL;
    private static final Map<String, Lesson> BY_ID;
    private static final Map<String, List<Lesson>> BY_UNIT;

    static {
        List<Lesson> lessons = new ArrayList<>();
        lessons.addAll(Unit1Lessons.create());
        lessons.addAll(Unit2Lessons.create());
        lessons.addAll(Unit3Lessons.create());
        lessons.addAll(Unit4Lessons.create());
        lessons.addAll(Unit5Lessons.create());
        lessons.addAll(Unit6Lessons.create());
        lessons.addAll(Unit7Lessons.create());
        lessons.addAll(Unit8Lessons.create());
        lessons.addAll(Unit9Lessons.create());
        lessons.addAll(Unit10Lessons.create());
        ALL = Collections.unmodifiableList(lessons);

        Map<String, Lesson> byId = new LinkedHashMap<>();
        Map<String, List<Lesson>> byUnit = new LinkedHashMap<>();
        for (Lesson lesson : ALL) {
            if (byId.put(lesson.id, lesson) != null) {
                throw new IllegalStateException(
                        "Duplicate lesson id: " + lesson.id);
            }
            List<Lesson> unit = byUnit.get(lesson.unit);
            if (unit == null) {
                unit = new ArrayList<>();
                byUnit.put(lesson.unit, unit);
            }
            unit.add(lesson);
        }
        BY_ID = Collections.unmodifiableMap(byId);

        Map<String, List<Lesson>> frozenUnits = new LinkedHashMap<>();
        for (Map.Entry<String, List<Lesson>> e : byUnit.entrySet()) {
            frozenUnits.put(e.getKey(), Collections.unmodifiableList(e.getValue()));
        }
        BY_UNIT = Collections.unmodifiableMap(frozenUnits);
    }

    private LessonCatalog() {
    }

    /** All lessons in course order. */
    public static List<Lesson> all() {
        return ALL;
    }

    /** @return the lesson with this course id, or null when unknown. */
    public static Lesson byId(String id) {
        return id == null ? null : BY_ID.get(id);
    }

    /** Unit titles in course order. */
    public static List<String> unitTitles() {
        return new ArrayList<>(BY_UNIT.keySet());
    }

    /** Lessons of one unit, in course order. */
    public static List<Lesson> inUnit(String unitTitle) {
        List<Lesson> lessons = BY_UNIT.get(unitTitle);
        return lessons == null
                ? Collections.emptyList() : lessons;
    }

    /** Number of lessons (32). */
    public static int size() {
        return ALL.size();
    }
}
