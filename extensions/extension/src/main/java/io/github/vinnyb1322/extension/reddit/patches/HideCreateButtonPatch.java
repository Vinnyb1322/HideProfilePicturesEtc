package io.github.vinnyb1322.extension.reddit.patches;

import java.util.List;

@SuppressWarnings("unused")
public final class HideCreateButtonPatch {

    /**
     * Injection point. Replaces the call that adds a tab to the bottom navigation bar.
     *
     * @param tab A tab ui model, whose toString() looks like
     *            {@code BottomNavTabUiModel(tab=Post, labelResId=...)}.
     */
    public static void addTab(List<Object> tabs, Object tab) {
        if (tab != null && tab.toString().contains("(tab=Post,")) {
            return;
        }
        tabs.add(tab);
    }
}
