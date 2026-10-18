package io.github.vinnyb1322.extension.reddit.patches;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.github.vinnyb1322.extension.reddit.Utils;

@SuppressWarnings("unused")
public final class HideSearchTrendingPatch {
    private static final int MAX_REMEMBERED_SUGGESTIONS = 1000;

    /**
     * Suggestion id to the id of the list it belongs to.
     */
    private static final Map<String, String> suggestionParents = new ConcurrentHashMap<>();

    /**
     * Ids of lists with a title starting with "Trending".
     */
    private static final Set<String> trendingListIds =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Injection point. Called when a search suggestion is parsed.
     */
    public static void registerSuggestion(String id, String parentId) {
        if (id == null || parentId == null) return;
        if (suggestionParents.size() > MAX_REMEMBERED_SUGGESTIONS) {
            suggestionParents.clear();
        }
        suggestionParents.put(id, parentId);
    }

    /**
     * Injection point.
     *
     * @param state toString() looks like
     *              {@code SearchTypeaheadListViewState(id=abc, title=Trending today, ...)}.
     * @return If the header should be hidden.
     */
    public static boolean hideSectionHeader(Object state) {
        try {
            if (state == null) return false;
            String stateString = state.toString();
            String title = Utils.getToStringValue(stateString, "title");
            if (title == null || !title.toLowerCase(Locale.ROOT).startsWith("trending")) {
                return false;
            }

            String id = Utils.getToStringValue(stateString, "id");
            if (id != null) trendingListIds.add(id);
            return true;
        } catch (Exception ex) {
            Utils.logException("hideSectionHeader failure", ex);
            return false;
        }
    }

    /**
     * Injection point.
     *
     * @param item toString() looks like
     *             {@code TypeaheadResultItemViewState(id=TypeaheadResultItemId(itemId=t5_abc, index=0), ...)}.
     * @return If the suggestion should be hidden.
     */
    public static boolean hideSuggestion(Object item) {
        try {
            if (item == null || trendingListIds.isEmpty()) return false;
            String itemId = Utils.getToStringValue(item.toString(), "itemId");
            if (itemId == null) return false;

            String parentId = suggestionParents.get(itemId);
            return parentId != null && trendingListIds.contains(parentId);
        } catch (Exception ex) {
            Utils.logException("hideSuggestion failure", ex);
            return false;
        }
    }
}
