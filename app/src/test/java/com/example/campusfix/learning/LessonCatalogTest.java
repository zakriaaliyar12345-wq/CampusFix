package com.example.campusfix.learning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exhaustive contract for the Learning Lab catalog: all 32 course topics
 * exist exactly once, in course order, with usable content, a fenced code
 * example, an exercise and a graded quiz — and each lesson's coverage flag
 * matches COURSE_TOPICS.md (W = working feature, D = demonstration).
 */
public class LessonCatalogTest {

    /** Expected coverage per course file, copied from COURSE_TOPICS.md. */
    private static final Map<String, String> EXPECTED_COVERAGE =
            new HashMap<String, String>() {{
                put("01.0", "D");
                put("01.1", "D");
                put("01.2", "W");
                put("01.3", "W");
                put("01.4", "D");
                put("02.1", "W");
                put("02.2", "W");
                put("02.3", "W");
                put("03.1", "D");
                put("03.2", "W");
                put("03.3", "D");
                put("04.1", "W");
                put("04.2", "W");
                put("04.3", "W");
                put("04.4", "W");
                put("04.5", "W");
                put("05.1", "W");
                put("05.2", "W");
                put("05.3", "W");
                put("06.1", "W");
                put("07.1", "D");
                put("07.2", "W");
                put("07.3", "W");
                put("07.4", "W");
                put("08.1", "W");
                put("08.2", "W");
                put("08.3", "W");
                put("09.0", "W");
                put("09.1", "W");
                put("09.2", "W");
                put("10.0", "W");
                put("10.1", "W");
            }};

    @Test
    public void catalogHasExactlyThe32CourseTopics() {
        assertEquals(LessonCatalog.REQUIRED_IDS.length, 32);
        assertEquals(32, LessonCatalog.size());
        assertEquals(32, LessonCatalog.all().size());

        Set<String> seen = new HashSet<>();
        for (Lesson lesson : LessonCatalog.all()) {
            assertTrue("duplicate id: " + lesson.id,
                    seen.add(lesson.id));
        }
        for (String required : LessonCatalog.REQUIRED_IDS) {
            assertTrue("missing topic: " + required,
                    seen.contains(required));
        }
    }

    @Test
    public void lessonsAreListedInCourseOrder() {
        List<String> ids = new ArrayList<>();
        for (Lesson lesson : LessonCatalog.all()) {
            ids.add(lesson.id);
        }
        assertEquals(Arrays.asList(LessonCatalog.REQUIRED_IDS), ids);
    }

    @Test
    public void lessonsGroupIntoTenUnitsInOrder() {
        List<String> units = LessonCatalog.unitTitles();
        assertEquals(10, units.size());
        assertEquals("Unit 1 — Introduction & UI Foundations", units.get(0));
        assertEquals("Unit 10 — Local Databases", units.get(9));

        int total = 0;
        for (String unit : units) {
            List<Lesson> lessons = LessonCatalog.inUnit(unit);
            assertFalse("unit has no lessons: " + unit, lessons.isEmpty());
            total += lessons.size();
        }
        assertEquals(32, total);
    }

    @Test
    public void everyLessonHasUsableContent() {
        for (Lesson lesson : LessonCatalog.all()) {
            String where = "lesson " + lesson.id;
            assertFalse(where + ": empty title",
                    lesson.title.trim().isEmpty());
            assertFalse(where + ": empty unit",
                    lesson.unit.trim().isEmpty());
            assertFalse(where + ": empty summary",
                    lesson.summary.trim().isEmpty());
            assertTrue(where + ": content too short",
                    lesson.content.length() > 400);
            assertFalse(where + ": empty exercise",
                    lesson.exercise.trim().isEmpty());
            assertTrue("lesson " + lesson.id + ": unknown coverage "
                            + lesson.coverage,
                    "W".equals(lesson.coverage)
                            || "D".equals(lesson.coverage));
        }
    }

    @Test
    public void everyLessonShowsABalancedCodeExample() {
        for (Lesson lesson : LessonCatalog.all()) {
            int fences = countOccurrences(lesson.content, "```");
            assertTrue("lesson " + lesson.id + ": unbalanced code fences",
                    fences >= 2 && fences % 2 == 0);
            assertTrue("lesson " + lesson.id + ": missing java/xml example",
                    lesson.content.contains("```java")
                            || lesson.content.contains("```xml")
                            || lesson.content.contains("```sql")
                            || lesson.content.contains("```bash"));
        }
    }

    @Test
    public void everyLessonHasAGradedQuiz() {
        for (Lesson lesson : LessonCatalog.all()) {
            assertTrue("lesson " + lesson.id + ": quiz too small",
                    lesson.quiz.size() >= 3);
            for (QuizQuestion question : lesson.quiz) {
                assertFalse("blank question in " + lesson.id,
                        question.question.trim().isEmpty());
                assertTrue("too few options in " + lesson.id,
                        question.options.size() >= 2);
                assertTrue("too many options in " + lesson.id,
                        question.options.size() <= 4);
                assertTrue("correctIndex out of range in " + lesson.id,
                        question.correctIndex >= 0
                                && question.correctIndex
                                < question.options.size());
                assertFalse("missing explanation in " + lesson.id,
                        question.explanation.trim().isEmpty());
                Set<String> distinct = new HashSet<>(question.options);
                assertEquals("duplicate options in " + lesson.id,
                        question.options.size(), distinct.size());
            }
        }
    }

    @Test
    public void coverageMatchesTheCourseChecklist() {
        for (Lesson lesson : LessonCatalog.all()) {
            assertEquals("coverage mismatch for " + lesson.id,
                    EXPECTED_COVERAGE.get(lesson.id), lesson.coverage);
        }
        assertEquals(EXPECTED_COVERAGE.size(), LessonCatalog.size());
    }

    @Test
    public void byIdReturnsLessonsAndNullForUnknown() {
        Lesson lesson = LessonCatalog.byId("04.5");
        assertNotNull(lesson);
        assertEquals("RecyclerView", lesson.title);

        assertNull(LessonCatalog.byId("99.9"));
        assertNull(LessonCatalog.byId(null));
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        int index = haystack.indexOf(needle);
        while (index >= 0) {
            count++;
            index = haystack.indexOf(needle, index + needle.length());
        }
        return count;
    }
}
