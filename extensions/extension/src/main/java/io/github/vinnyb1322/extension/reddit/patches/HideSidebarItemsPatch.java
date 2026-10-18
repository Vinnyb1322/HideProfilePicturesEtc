package io.github.vinnyb1322.extension.reddit.patches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.vinnyb1322.extension.reddit.Utils;

@SuppressWarnings("unused")
public final class HideSidebarItemsPatch {

    /**
     * Sections hidden together with their header, by HeaderItem enum name.
     */
    private static final String[] HIDDEN_SECTIONS = {
            "GAMES_ON_REDDIT",
            "REDDIT_PRO",
            "RESOURCES"
    };

    /**
     * Single items hidden by the string resource of their title.
     */
    private static final String[] HIDDEN_ITEM_TITLES = {
            "popular_feed_label",       // Popular
            "news_feed_label",          // News
            "latest_feed_label",        // Latest
            "communities_cta_title",    // Discover communities
            "label_start_a_community",  // Start a community
            "games_feed_item_title"     // Games, shown instead of the Games on Reddit section
    };

    private static Set<String> hiddenTitleIds;

    /**
     * Injection point. Called for every section added to the community drawer.
     *
     * @param items  The items of the section.
     * @param header The section header, or null. Its toString() looks like
     *               {@code HeaderItemUiModel(uniqueId=-5, type=RESOURCES, isCollapsed=false, ...)}.
     * @return The items to show.
     */
    public static Collection<?> filterSection(Collection<?> items, Object header) {
        try {
            if (items == null || items.isEmpty()) return items;

            if (header != null) {
                String type = Utils.getToStringValue(header.toString(), "type");
                for (String hiddenSection : HIDDEN_SECTIONS) {
                    if (hiddenSection.equals(type)) {
                        return Collections.emptyList();
                    }
                }
            }

            Set<String> hiddenIds = getHiddenTitleIds();
            if (hiddenIds.isEmpty()) return items;

            List<Object> filtered = null;
            int index = 0;
            for (Object item : items) {
                String titleResId = item == null
                        ? null
                        : Utils.getToStringValue(item.toString(), "titleResId");
                boolean hide = titleResId != null && hiddenIds.contains(titleResId);

                if (hide && filtered == null) {
                    // Copy everything before the first hidden item. The original may be immutable.
                    filtered = new ArrayList<>(items.size());
                    int i = 0;
                    for (Object previous : items) {
                        if (i++ == index) break;
                        filtered.add(previous);
                    }
                } else if (!hide && filtered != null) {
                    filtered.add(item);
                }
                index++;
            }

            return filtered == null ? items : filtered;
        } catch (Exception ex) {
            Utils.logException("filterSection failure", ex);
            return items;
        }
    }

    private static Set<String> getHiddenTitleIds() {
        if (hiddenTitleIds == null) {
            Set<String> ids = new HashSet<>();
            for (String name : HIDDEN_ITEM_TITLES) {
                int id = Utils.getStringId(name);
                if (id != 0) ids.add(String.valueOf(id));
            }
            // Only cache once the context was available.
            if (Utils.getContext() == null) return ids;
            hiddenTitleIds = ids;
        }
        return hiddenTitleIds;
    }
}
