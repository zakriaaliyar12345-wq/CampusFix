package com.example.campusfix.learning;

import java.util.List;

/** Unit 4 — course files 04.1 … 04.5 (buttons, input, menus, navigation, RecyclerView). */
final class Unit4Lessons {

    private static final String UNIT = "Unit 4 — Input, Menus & Navigation";

    private Unit4Lessons() {
    }

    static List<Lesson> create() {
        return java.util.Arrays.asList(

                new Lesson(
                        "04.1",
                        "Buttons and clickable images",
                        UNIT,
                        "Click listeners, Material buttons with icons, and click handling on any view.",
                        "Every interactive element shares one mechanism: set an OnClickListener and "
                                + "the framework delivers tap events on the main thread:\n"
                                + "```java\n"
                                + "findViewById(R.id.btnSave).setOnClickListener(v -> {\n"
                                + "    doSave();\n"
                                + "});\n"
                                + "```\n\n"
                                + "findViewById returns the view for the id created by android:id in "
                                + "XML; the lambda's parameter is the clicked view. Since API 26 findViewById is generic, so no casts are needed.\n\n"
                                + "MaterialButton extends AppCompatButton and adds theming, shape and "
                                + "icon support:\n"
                                + "```xml\n"
                                + "<com.google.android.material.button.MaterialButton\n"
                                + "    android:id=\"@+id/btnStatistics\"\n"
                                + "    style=\"@style/Widget.Material3.Button.TonalButton\"\n"
                                + "    android:layout_width=\"match_parent\"\n"
                                + "    android:layout_height=\"wrap_content\"\n"
                                + "    android:text=\"@string/nav_statistics\"\n"
                                + "    app:icon=\"@drawable/ic_stats\"\n"
                                + "    app:iconGravity=\"textStart\" />\n"
                                + "```\n"
                                + "Styles decide the variant: filled (emphasised), tonal (secondary), "
                                + "outlined (quiet), text (lightest). CampusFix defines its own style "
                                + "aliases (Widget.CampusFix.Button) so every screen gets consistent "
                                + "heights — reuse styles rather than copying attributes.\n\n"
                                + "Any view can be clickable: images, cards, whole rows. In a list "
                                + "row you usually make the itemView clickable in the adapter:\n"
                                + "```java\n"
                                + "itemView.setOnClickListener(v -> {\n"
                                + "    int pos = getBindingAdapterPosition();\n"
                                + "    if (pos != RecyclerView.NO_POSITION) {\n"
                                + "        listener.onReportClick(getItem(pos));\n"
                                + "    }\n"
                                + "});\n"
                                + "```\n"
                                + "getBindingAdapterPosition() matters: by the time the click is "
                                + "delivered the data may have changed, and using a stale position "
                                + "shows the wrong item. NO_POSITION means the view is being removed "
                                + "— ignore those clicks.\n\n"
                                + "Touch feedback (ripple) comes from the theme; "
                                + "android:foreground/ripple attributes can customise it. For "
                                + "accessibility every clickable view needs android:contentDescription "
                                + "if it has no text.",
                        "In CampusFix's report detail, identify all five action buttons and their "
                                + "listener code. Then add android:contentDescription to any icon-only "
                                + "ImageView you find missing one (for example in the list empty "
                                + "state) and rerun.",
                        Quiz.list(
                                Quiz.of(
                                        "What should a click listener on a RecyclerView row use to "
                                                + "find its data position?",
                                        1,
                                        "getBindingAdapterPosition() returns the current position, "
                                                + "handling animations and removals; a cached position can be "
                                                + "stale.",
                                        "A stored field set at bind time",
                                        "getBindingAdapterPosition()",
                                        "getAdapterCount()",
                                        "position from onBindViewHolder"),
                                Quiz.of(
                                        "Which MaterialButton style is the quietest (least "
                                                + "emphasised)?",
                                        2,
                                        "The text style has no fill or outline — it is the lowest "
                                                + "emphasis; filled > tonal > outlined > text in prominence.",
                                        "Filled",
                                        "Tonal",
                                        "Text",
                                        "Outlined"),
                                Quiz.of(
                                        "Why give an icon-only view a contentDescription?",
                                        0,
                                        "TalkBack reads contentDescription aloud so blind users know "
                                                + "what the control does; without it the view is anonymous.",
                                        "It makes icons render",
                                        "Required for ripple effect",
                                        "Accessibility for TalkBack users",
                                        "Reduces memory")),
                        "W"),

                new Lesson(
                        "04.2",
                        "Input controls",
                        UNIT,
                        "Text fields, checkboxes, switches, chips, and reading user input safely.",
                        "Android ships a family of input controls; Material Components modernises "
                                + "them:\n\n"
                                + "* TextInputLayout + TextInputEditText — outlined/filled text fields "
                                + "with floating hints, error messages and counters.\n"
                                + "* AutoCompleteTextView inside a TextInputLayout with "
                                + "ExposedDropdownMenu style — the category picker pattern.\n"
                                + "* CheckBox / SwitchMaterial — binary choices.\n"
                                + "* RadioGroup + RadioButton — exclusive options.\n"
                                + "* ChipGroup + Chip — modern filter/choice pills, single- or "
                                + "multi-selection.\n\n"
                                + "CampusFix's create-report form combines three of them:\n"
                                + "```xml\n"
                                + "<com.google.android.material.chip.ChipGroup\n"
                                + "    android:id=\"@+id/chipGroupPriority\"\n"
                                + "    app:singleSelection=\"true\"\n"
                                + "    app:selectionRequired=\"true\">\n"
                                + "    <com.google.android.material.chip.Chip\n"
                                + "        android:id=\"@+id/chipPriorityHigh\"\n"
                                + "        android:text=\"@string/priority_high\" />\n"
                                + "</com.google.android.material.chip.ChipGroup>\n"
                                + "```\n\n"
                                + "Reading input is where bugs hide. Always null-check EditText "
                                + "content because getText() can return null in edge cases:\n"
                                + "```java\n"
                                + "String title = etTitle.getText() == null\n"
                                + "        ? \"\" : etTitle.getText().toString();\n"
                                + "```\n"
                                + "Then validate before acting — CampusFix's Validator enforces length "
                                + "limits (4–80 for titles) and displays errors through "
                                + "tilTitle.setError(), which shows the message inline under the "
                                + "field instead of a toast.\n\n"
                                + "Programmatic state has pitfalls too: ChipGroup.check(id) fires your "
                                + "checked-change listener, so initial setup must either set state "
                                + "before attaching the listener or tolerate the callback (CampusFix "
                                + "does the latter — re-selecting the same value is harmless because "
                                + "the filter setter de-duplicates).\n\n"
                                + "Watch out for watchers: TextWatcher fires on every keystroke; if "
                                + "you trigger a database query per character, debounce or keep the "
                                + "query cheap (Room on a background thread handles short lists "
                                + "easily).",
                        "Add a SwitchMaterial and an EditText to a practice layout, log the switch "
                                + "state and text length on every change, then move the logging into "
                                + "an onCheckedChanged listener and a TextWatcher respectively. "
                        + "Confirm during initial setup that the listener fires once with the default value.",
                        Quiz.list(
                                Quiz.of(
                                        "Which container shows inline validation errors under a text "
                                                + "field?",
                                        1,
                                        "TextInputLayout.setError(text) renders the message under the "
                                                + "field and colours it with the error attribute.",
                                        "The EditText directly",
                                        "TextInputLayout.setError(...)",
                                        "A Snackbar",
                                        "android:textError"),
                                Quiz.of(
                                        "What do singleSelection and selectionRequired do in a "
                                                + "ChipGroup?",
                                        0,
                                        "singleSelection allows only one checked chip at a time; "
                                                + "selectionRequired forbids unchecking the last one.",
                                        "Allow only one chip, and at least one must stay checked",
                                        "Sort chips alphabetically",
                                        "Make chips disabled",
                                        "Allow multi-select"),
                                Quiz.of(
                                        "Why null-check EditText.getText()?",
                                        2,
                                        "getText() may return null before the field is attached or in "
                                                + "some EditText variants; calling toString() on it would "
                                                + "crash.",
                                        "Because text is encrypted",
                                        "To improve performance",
                                        "getText() can be null — calling toString() would crash",
                                        "It is required by XML")),
                        "W"),

                new Lesson(
                        "04.3",
                        "Menus and pickers",
                        UNIT,
                        "Overflow menus, popup menus, and choosing values with dialogs.",
                        "Menus let you offer many actions without cluttering the screen. Three forms "
                                + "matter:\n\n"
                                + "1. Options menu — the ActionBar overflow (⋮). Inflate it in "
                                + "onCreateOptionsMenu and handle choices in "
                                + "onOptionsItemSelected.\n"
                                + "2. PopupMenu — a anchored dropdown, perfect for filter/sort "
                                + "controls. Define items in res/menu and inflate:\n"
                                + "```java\n"
                                + "PopupMenu popup = new PopupMenu(this, anchorView);\n"
                                + "popup.getMenuInflater().inflate(R.menu.menu_sort, popup.getMenu());\n"
                                + "popup.getMenu().findItem(R.id.action_sort_newest).setChecked(true);\n"
                                + "popup.setOnMenuItemClickListener(item -> {\n"
                                + "    if (item.getItemId() == R.id.action_sort_newest) {\n"
                                + "        // apply choice\n"
                                + "        return true;\n"
                                + "    }\n"
                                + "    return false;\n"
                                + "});\n"
                                + "popup.show();\n"
                                + "```\n"
                                + "The menu XML uses a group with checkableBehavior=\"single\" so one "
                                + "item shows a radio tick:\n"
                                + "```xml\n"
                                + "<menu xmlns:android=\"http://schemas.android.com/apk/res/android\">\n"
                                + "    <group android:checkableBehavior=\"single\">\n"
                                + "        <item android:id=\"@+id/action_sort_newest\"\n"
                                + "            android:checked=\"true\"\n"
                                + "            android:title=\"@string/sort_newest\" />\n"
                                + "        <item android:id=\"@+id/action_sort_priority\"\n"
                                + "            android:title=\"@string/sort_priority\" />\n"
                                + "    </group>\n"
                                + "</menu>\n"
                                + "```\n\n"
                                + "3. Dialog pickers — MaterialAlertDialogBuilder offers list and "
                                + "single-choice dialogs for values that don't fit a button label:\n"
                                + "```java\n"
                                + "new MaterialAlertDialogBuilder(context)\n"
                                + "        .setTitle(R.string.detail_simulate_status)\n"
                                + "        .setSingleChoiceItems(items, checked, (d, which) -> {\n"
                                + "            apply(items[which]);\n"
                                + "            d.dismiss();\n"
                                + "        })\n"
                                + "        .setNegativeButton(R.string.action_cancel, null)\n"
                                + "        .show();\n"
                                + "```\n\n"
                                + "CampusFix uses all of it: three popup menus in the report list "
                                + "(sort, category, priority) and a single-choice dialog for status "
                                + "changes. The AutoCompleteTextView exposed dropdown is the fourth "
                                + "picker style — a menu that behaves like a text field.",
                        "Open res/menu/menu_priority.xml in CampusFix, add a new checkable item for "
                                + "an order like 'Most recent priority change' (a dummy is fine), "
                                + "handle its id in showPriorityMenu, and verify the popup shows the "
                                + "new choice with a tick.",
                        Quiz.list(
                                Quiz.of(
                                        "Which menu appears as a dropdown anchored to a view?",
                                        1,
                                        "PopupMenu is anchored to any view and shows a vertical list "
                                                + "— ideal for sort/filter controls.",
                                        "The options menu",
                                        "PopupMenu",
                                        "The context menu only",
                                        "A dialog menu"),
                                Quiz.of(
                                        "What does checkableBehavior=\"single\" do in a menu group?",
                                        2,
                                        "Only one item in the group can be checked, and checking one "
                                                + "unchecks the others — radio behaviour.",
                                        "Checks every item",
                                        "Disables the group",
                                        "Allows one checked item (radio style)",
                                        "Makes items checkboxes"),
                                Quiz.of(
                                        "When is a single-choice dialog a better picker than a popup "
                                                + "menu?",
                                        1,
                                        "When the choice needs a title, explanatory text, or appears "
                                                + "in a flow where the user expects a modal decision (like "
                                                + "changing a status).",
                                        "Never",
                                        "When you want a modal decision with a title and buttons",
                                        "Only for numbers",
                                        "When items exceed five")),
                        "W"),

                new Lesson(
                        "04.4",
                        "User navigation",
                        UNIT,
                        "Building coherent flows: tabs, drawer, back stack, and the up button.",
                        "Navigation design answers: where am I, where can I go, how do I get back? "
                                + "Android offers several patterns:\n\n"
                                + "* Back stack — starting an activity pushes onto a stack; Back "
                                + "pops it. This is the default and what CampusFix uses.\n"
                                + "* Tabs (TabLayout / TabLayoutMediator with ViewPager2) — sibling "
                                + "screens at the same level.\n"
                                + "* Navigation drawer — a slide-in list of top-level destinations.\n"
                                + "* Bottom navigation — 3–5 primary destinations, thumb-reachable.\n"
                                + "* Jetpack Navigation component — a graph of fragments with a "
                                + "single Activity host; great for larger apps (not used here because "
                                + "this course teaches activity-based flows first).\n\n"
                                + "Two buttons often confuse learners: Back (system, pops the stack) "
                                + "vs Up (ActionBar arrow, navigates to the logical parent you define "
                                + "in the manifest):\n"
                                + "```xml\n"
                                + "<activity\n"
                                + "    android:name=\".ui.report.ReportDetailActivity\"\n"
                                + "    android:parentActivityName=\".ui.report.ReportListActivity\" />\n"
                                + "```\n"
                                + "With parentActivityName the ActionBar shows an up arrow that "
                                + "recreates the parent with a fresh state — different from Back, "
                                + "which returns to the exact previous instance.\n\n"
                                + "CampusFix's navigation graph is a hub-and-spoke design:\n"
                                + "```\n"
                                + "Dashboard → ReportList → ReportDetail → CreateReport(edit)\n"
                                + "        → CreateReport(new)\n"
                                + "        → Statistics\n"
                                + "        → LearningLab → LessonDetail → Quiz\n"
                                + "```\n"
                                + "A dashboard hub keeps the information architecture shallow: every "
                                + "main feature is exactly two taps away.\n\n"
                                + "Passing state along the flow: give each screen one stable extra "
                                + "(a report id, a lesson id) instead of chaining objects. And handle "
                                + "the empty states — a detail screen opened for a deleted report "
                                + "must exit gracefully, not crash.",
                        "Draw CampusFix's navigation graph on paper including every activity in the "
                                + "manifest. Mark where parentActivityName changes the Up target, and "
                                + "explain the difference between Up and Back to a classmate.",
                        Quiz.list(
                                Quiz.of(
                                        "Difference between the Back button and the Up arrow?",
                                        1,
                                        "Back pops the actual activity stack; Up navigates to the "
                                                + "declared parent and typically rebuilds it fresh.",
                                        "They are identical",
                                        "Back pops the stack; Up goes to the declared parent",
                                        "Up only works in fragments",
                                        "Back is disabled on phones"),
                                Quiz.of(
                                        "Which attribute declares the logical parent for Up "
                                                + "navigation?",
                                        0,
                                        "android:parentActivityName names the parent activity; the "
                                                + "ActionBar uses it for the up button.",
                                        "android:parentActivityName",
                                        "android:targetActivity",
                                        "android:upEnabled",
                                        "android:navigationParent"),
                                Quiz.of(
                                        "Which pattern suits 3–5 top-level destinations one thumb "
                                                + "tap away?",
                                        2,
                                        "Bottom navigation is designed for a small set of primary "
                                                + "destinations; drawers and tabs suit larger or sibling sets.",
                                        "Back stack only",
                                        "A dialog",
                                        "Bottom navigation",
                                        "Multiple windows")),
                        "W"),

                new Lesson(
                        "04.5",
                        "RecyclerView",
                        UNIT,
                        "The modern list: ViewHolder, adapter, layout manager, and DiffUtil.",
                        "RecyclerView replaced ListView because it recycles views aggressively and "
                                + "plugs into different layouts. Three collaborating pieces:\n\n"
                                + "1. LayoutManager decides positioning (LinearLayoutManager rows, "
                                + "GridLayoutManager columns, StaggeredGridLayoutManager masonry).\n"
                                + "2. Adapter creates ViewHolders and binds data:\n"
                                + "```java\n"
                                + "public class ReportAdapter\n"
                                + "        extends ListAdapter<ReportEntity, ReportAdapter.Holder> {\n"
                                + "    ReportAdapter(OnReportClickListener listener) {\n"
                                + "        super(DIFF_CALLBACK);\n"
                                + "    }\n"
                                + "    @NonNull @Override\n"
                                + "    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int vt) {\n"
                                + "        View v = LayoutInflater.from(parent.getContext())\n"
                                + "                .inflate(R.layout.item_report, parent, false);\n"
                                + "        return new Holder(v);\n"
                                + "    }\n"
                                + "    @Override\n"
                                + "    public void onBindViewHolder(@NonNull Holder h, int pos) {\n"
                                + "        h.bind(getItem(pos));\n"
                                + "    }\n"
                                + "}\n"
                                + "```\n"
                                + "3. ViewHolder caches findViewById results per row — never call "
                                + "findViewById inside onBindViewHolder, that is the classic "
                                + "performance bug RecyclerView was designed to eliminate.\n\n"
                                + "DiffUtil computes what changed between old and new lists and "
                                + "dispatches precise insert/remove/change animations with "
                                + "notifyItemChanged instead of notifyDataSetChanged():\n"
                                + "```java\n"
                                + "static final DiffUtil.ItemCallback<ReportEntity> DIFF =\n"
                                + "        new DiffUtil.ItemCallback<ReportEntity>() {\n"
                                + "    public boolean areItemsTheSame(ReportEntity a, ReportEntity b) {\n"
                                + "        return a.id == b.id;          // same row?\n"
                                + "    }\n"
                                + "    public boolean areContentsTheSame(ReportEntity a, ReportEntity b) {\n"
                                + "        return a.title.equals(b.title) && a.status == b.status;\n"
                                + "    }\n"
                                + "};\n"
                                + "```\n"
                                + "areItemsTheSame compares identity (the id), areContentsTheSame "
                                + "compares displayed content — mixing them up causes wrong "
                                + "animations or missed updates.\n\n"
                                + "ListAdapter wraps this pattern and exposes submitList(list): call it "
                                + "with the fresh LiveData list and RecyclerView updates itself. In "
                                + "CampusFix the report list, status history and statistics rows all "
                                + "follow these rules (history is small and append-only, so a simple "
                                + "notifyDataSetChanged() there is acceptable — know when simple is "
                                + "enough).\n\n"
                                + "DiffUtil runs on a background thread inside ListAdapter; do not "
                                + "compute diffs manually on the UI thread for large lists.",
                        "Open ReportAdapter and answer: what two conditions must be true for a row to "
                                + "rebind? Then deliberately change areContentsTheSame to always "
                                + "return false, run the app, and observe how every list change now "
                                + "animates a full rebind (revert the change afterwards).",
                        Quiz.list(
                                Quiz.of(
                                        "Why use a ViewHolder?",
                                        1,
                                        "It caches view references per row so binding never calls "
                                                + "findViewById — the main ListView performance fix.",
                                        "To hold activity references",
                                        "To cache view lookups per row",
                                        "Because XML requires it",
                                        "To store list data"),
                                Quiz.of(
                                        "What does areItemsTheSame compare?",
                                        0,
                                        "Identity — usually the stable primary key (report id) — so "
                                                + "DiffUtil knows two objects represent the same row.",
                                        "Displayed text equality",
                                        "Identity (primary key) of the row",
                                        "Object hash codes",
                                        "Timestamps"),
                                Quiz.of(
                                        "What does ListAdapter.submitList() replace?",
                                        2,
                                        "Full notifyDataSetChanged() — submitList diffs the new list "
                                                + "and dispatches minimal, animated changes.",
                                        "setItemAnimator",
                                        "RecyclerView.setLayoutManager",
                                        "notifyDataSetChanged()",
                                        "onCreateViewHolder")),
                        "W"));
    }
}
