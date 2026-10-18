package io.github.vinnyb1322.extension.reddit.patches;

import io.github.vinnyb1322.extension.reddit.Utils;

@SuppressWarnings("unused")
public final class ShowPostAuthorPatch {
    private static final String SEPARATOR = " \u2022 ";

    /**
     * Injection point.
     *
     * @param domain   The link domain shown in the metadata row, or null for posts without a link.
     * @param metadata The post metadata, whose toString() looks like
     *                 {@code PostUnitMetadata(author=Author(id=t2_abc, username=foo, ...),
     *                 subreddit=Subreddit(id=t5_xyz, ..., subredditType=public, ...), ..., promoted=false, ...)}.
     * @return The text to show in place of the domain.
     */
    public static String appendAuthor(String domain, Object metadata) {
        try {
            if (metadata == null) return domain;
            String metadataString = metadata.toString();

            // Promoted posts and posts in user profiles already show the author instead of the subreddit.
            if ("true".equals(Utils.getToStringValue(metadataString, "promoted"))
                    || "user".equals(Utils.getToStringValue(metadataString, "subredditType"))) {
                return domain;
            }

            String username = Utils.getToStringValue(metadataString, "username");
            if (username == null || username.isEmpty() || username.equals("null")) return domain;

            String author = "u/" + username;
            return domain == null || domain.isEmpty() ? author : domain + SEPARATOR + author;
        } catch (Exception ex) {
            Utils.logException("appendAuthor failure", ex);
            return domain;
        }
    }
}
