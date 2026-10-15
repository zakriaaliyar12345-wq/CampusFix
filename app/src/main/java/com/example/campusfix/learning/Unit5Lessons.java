package com.example.campusfix.learning;

import java.util.List;

/** Unit 5 — course files 05.1 … 05.3 (drawables/styles, Material Design, adaptive layouts). */
final class Unit5Lessons {

    private static final String UNIT = "Unit 5 — Styling & Material Design";

    private Unit5Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "05.1",
                        "Drawables, styles, and themes",
                        UNIT,
                        "Vector drawables, shape drawables, style chains, and day/night theming.",
                        "Drawables are drawable resources: bitmaps, vector graphics, or shape "
                                + "definitions stored under res/drawable and loaded with "
                                + "R.drawable.name.\n\n"
                                + "Vector drawables are the modern default — one XML file scales "
                                + "crisply at any density:\n"
                                + "```xml\n"
                                + "<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"
                                + "    android:width=\"24dp\"\n"
                                + "    android:height=\"24dp\"\n"
                                + "    android:viewportWidth=\"24\"\n"
                                + "    android:viewportHeight=\"24\">\n"
                                + "    <path android:fillColor=\"@color/ic_launcher_background\"\n"
                                + "        android:pathData=\"M12,2L2,22h20L12,2z\" />\n"
                                + "</vector>\n"
                                + "```\n"
                                + "Shape drawables define gradients, borders and corners in code-free "
                                + "XML — CampusFix uses them for badges and dividers:\n"
                                + "```xml\n"
                                + "<shape xmlns:android=\"...\" android:shape=\"rectangle\">\n"
                                + "    <solid android:color=\"@color/surface_variant\" />\n"
                                + "    <corners android:radius=\"8dp\" />\n"
                                + "    <stroke android:width=\"1dp\" android:color=\"@color/outline\" />\n"
                                + "</shape>\n"
                                + "```\n\n"
                                + "Themes and styles both package attributes, but differ in reach. A "
                                + "*style* is applied to one view (Widget.CampusFix.Button), while a "
                                + "*theme* is applied to an activity or application and its "
                                + "attributes flow down to every inflated child — that is how a "
                                + "single colorPrimary repaints all buttons on a screen:\n"
                                + "```xml\n"
                                + "<style name=\"Theme.CampusFix\" parent=\"Theme.Material3.DayNight\">\n"
                                + "    <item name=\"colorPrimary\">@color/primary</item>\n"
                                + "    <item name=\"android:windowBackground\">@color/surface</item>\n"
                                + "</style>\n"
                                + "```\n"
                                + "Styles inherit hierarchically: Widget.CampusFix.Button can extend "
                                + "Widget.Material3.Button and override only the height.\n\n"
                                + "Day/night is the same mechanism with resource qualifiers: define "
                                + "colors in values/ (light) and values-night/ (dark), keep the "
                                + "attribute names identical, and the system switch does the rest. "
                                + "CampusFix's Theme.CampusFix follows this; the Settings screen can "
                                + "also force a mode at runtime via AppCompatDelegate.setDefaultNightMode.",
                        "Open values/themes.xml and trace one attribute (colorPrimary) from the theme "
                                + "down to an actual MaterialButton in a layout. Then add a shape "
                                + "drawable with an 8dp corner radius and set it as a card's "
                                + "android:background.",
                        Quiz.list(
                                Quiz.of(
                                        "Theme vs style: what is the key difference?",
                                        2,
                                        "A theme applies its attributes to an entire activity/app and "
                                                + "inflated views inherit them; a style targets a single view.",
                                        "They are synonyms",
                                        "Styles are only for XML",
                                        "A theme applies broadly; a style applies to one view",
                                        "Themes require Java"),
                                Quiz.of(
                                        "Why prefer a vector drawable over a PNG?",
                                        0,
                                        "Vectors scale losslessly to any density and screen size from "
                                                + "one small file, replacing whole PNG density buckets.",
                                        "They animate automatically",
                                        "One file scales at any density",
                                        "They are required by Material",
                                        "PNGs are not supported"),
                                Quiz.of(
                                        "How does day/night theming work?",
                                        1,
                                        "Same attribute names are defined in values/ and values-night/ "
                                                + "resource sets; the system picks the set at runtime.",
                                        "An if statement in Java",
                                        "Duplicate resources in values-night/ with same names",
                                        "A third-party library",
                                        "You ship two apps")),
                        "W"),

                new Lesson(
                        "05.2",
                        "Material Design",
                        UNIT,
                        "The Material 3 component set: cards, chips, FABs, fields, and dialogs.",
                        "Material Design (Material 3) is Google's design system: colour roles, "
                                + "typography scales, elevation, and a component vocabulary. The "
                                + "Material Components library (com.google.android.material) gives "
                                + "you Java/XML classes for all of it — CampusFix uses many:\n\n"
                                + "* MaterialCardView — a raised surface for grouping (report rows, "
                                + "dashboard stats). app:cardElevation controls shadow, "
                                + "app:cardCornerRadius the rounding.\n"
                                + "* Chip + ChipGroup — compact choices/filters with singleSelection.\n"
                                + "* FloatingActionButton (extended variant) — the primary action; "
                                + "CampusFix uses it as 'create report' on the list.\n"
                                + "* TextInputLayout/TextInputEditText — fields with floating labels "
                                + "and errors.\n"
                                + "* MaterialAlertDialogBuilder — themed modals with list choices.\n"
                                + "* Snackbar — transient feedback with an optional action.\n\n"
                        + "A card row example:\n"
                                + "```xml\n"
                                + "<com.google.android.material.card.MaterialCardView\n"
                                + "    android:layout_width=\"match_parent\"\n"
                                + "    android:layout_height=\"wrap_content\"\n"
                                + "    app:cardCornerRadius=\"12dp\"\n"
                                + "    app:cardElevation=\"2dp\"\n"
                                + "    android:clickable=\"true\"\n"
                                + "    android:focusable=\"true\">\n"
                                + "    <LinearLayout ... />\n"
                                + "</com.google.android.material.card.MaterialCardView>\n"
                                + "```\n\n"
                                + "Colour roles replace raw colours: colorPrimary for key actions, "
                                + "colorSurface for backgrounds, colorOnPrimary for content on the "
                                + "primary, colorError for failures. Components read these roles from "
                                + "the theme, which is why switching one theme colour restyles the "
                                + "whole app.\n\n"
                                + "Touch targets: Material mandates a 48dp minimum. Buttons handle "
                                + "this themselves; custom clickable views need at least 48dp of "
                                + "slop or use android:minHeight=\"48dp\". Ripple feedback tells the "
                                + "user the tap registered.\n\n"
                                + "Elevation communicates hierarchy — the FAB floats above cards, "
                                + "cards above the background. Do not overuse it; two or three levels "
                                + "is typical. Motion: components ship with default durations; add "
                                + "coordinated transitions rather than ad-hoc animations.",
                        "Pick one CampusFix screen and annotate which Material component and colour "
                                + "role every element uses (for example: FAB = colorPrimary, card = "
                                + "colorSurface). Then set the card's cardElevation from 2dp to 8dp "
                                + "and observe the hierarchy shift at runtime.",
                        Quiz.list(
                                Quiz.of(
                                        "Which Material role do components read for their main "
                                                + "action colour?",
                                        1,
                                        "colorPrimary is the key action role; components reference it "
                                                + "so one theme change restyles every button.",
                                        "android:colorBackground",
                                        "colorPrimary",
                                        "hard-coded #FF0000",
                                        "textColor"),
                                Quiz.of(
                                        "What is the minimum touch target size Material specifies?",
                                        0,
                                        "48dp in each dimension (at least 44dp is acceptable, but "
                                                + "48dp is the documented Material minimum for accessibility).",
                                        "48dp",
                                        "12dp",
                                        "8dp",
                                        "100px"),
                                Quiz.of(
                                        "What does a FloatingActionButton usually represent?",
                                        2,
                                        "The screen's primary creation action — it floats above the "
                                                + "content so it is always reachable (CampusFix: new report).",
                                        "A delete action",
                                        "An overflow menu",
                                        "The primary action",
                                        "A loading spinner")),
                        "W"),

                new Lesson(
                        "05.3",
                        "Resources for adaptive layouts",
                        UNIT,
                        "One design, many screens: qualifiers, layout-sw600dp, and same-ID merging.",
                        "Adaptive layout means your XML adapts instead of being rewritten for each "
                                + "device. The mechanism is resource *qualifiers* — folders and "
                                + "attribute rules that Android picks by environment:\n\n"
                                + "* res/layout-sw600dp/ — smallest-width qualifier: activated when "
                                + "the screen's smallest dimension is ≥600dp (typical 7\" tablet).\n"
                                + "* res/layout-land/ — orientation; res/values-night/ — dark theme; "
                                + "res/values-fr/ — locale; drawable-xhdpi — density.\n"
                                + "The system resolves the best-matching resource at runtime; "
                                + "missing qualifiers fall back to the default res/layout.\n\n"
                                + "CampusFix's tablet dashboard demonstrates the pattern:\n"
                                + "```java\n"
                                + "setContentView(R.layout.activity_dashboard);\n"
                                + "```\n"
                                + "On a phone this inflates res/layout/activity_dashboard.xml "
                                + "(single column inside a ScrollView); on a 600dp+ tablet it "
                                + "inflates res/layout-sw600dp/activity_dashboard.xml (two columns). "
                                + "The Java never changes because both variants keep identical "
                                + "android:id values — R.id.nav_report_list resolves correctly in "
                                + "whichever file was inflated. That same-ID contract is the rule of "
                                + "adaptive layouts: variants may differ structurally, never in "
                                + "identity.\n\n"
                                + "Inside a layout, three tools handle size changes without new "
                                + "folders:\n"
                                + "1. Flexible containers — ConstraintLayout chains, LinearLayout "
                                + "layout_weight, and GridLayout let content share space.\n"
                                + "2. Breakpoints in code — view binding plus WindowManager metrics "
                                + "can switch multi-pane behaviour at runtime.\n"
                                + "3. Insets — WindowInsetsCompat (fitSystemWindows) keeps content "
                                + "clear of cutouts and gesture bars.\n\n"
                                + "Guidance: start with one responsive layout using constraints/weights; "
                                + "add a -sw600dp variant only when the phone layout would be a "
                                + "waste of space (list-detail is the classic case). Always test "
                                + "both orientations — rotation is the cheapest adaptive-layout test "
                                + "there is.",
                        "Open both activity_dashboard.xml files side by side and verify every "
                                + "@+id matches across them. Then create res/layout-land/ with a "
                                + "landscape variant of a simple layout (same ids), rotate the "
                                + "emulator, and confirm Android picks the -land folder.",
                        Quiz.list(
                                Quiz.of(
                                        "What does the layout-sw600dp qualifier mean?",
                                        1,
                                        "smallest-width ≥ 600dp — the layout is used on large screens "
                                                + "such as 7-inch tablets.",
                                        "600 dp font size",
                                        "Smallest width of at least 600dp (tablets)",
                                        "600 pixels wide",
                                        "A version code"),
                                Quiz.of(
                                        "Why must tablet variants keep the same view ids?",
                                        0,
                                        "findViewById and R.id references resolve at compile time "
                                                + "against ids, so identical ids let one Java file serve both "
                                                + "layouts.",
                                        "For the compiler to accept R.id",
                                        "Same ids cost less memory",
                                        "Ids cannot be changed in XML",
                                        "They are auto-generated"),
                                Quiz.of(
                                        "Which is the FIRST thing to try before creating a whole new "
                                                + "layout folder?",
                                        0,
                                        "Make the existing layout flexible with constraints or weights — "
                                                + "a new qualifier folder only earns its keep when the design "
                                                + "genuinely differs.",
                                        "Rewrite everything in Kotlin",
                                        "Use flexible containers (weight/Constraints) first",
                                        "Add more ImagesViews",
                                        "Disable orientation changes")),
                        "W"));
    }
}
