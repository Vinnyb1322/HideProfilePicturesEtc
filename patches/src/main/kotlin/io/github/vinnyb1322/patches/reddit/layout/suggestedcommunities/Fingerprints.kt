package io.github.vinnyb1322.patches.reddit.layout.suggestedcommunities

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.StringComparisonType
import app.morphe.patcher.string

/**
 * Query variables that make the server include community recommendation units in a feed.
 */
internal val COMMUNITY_RECOMMENDATION_VARIABLES = setOf(
    "includeCarouselRecommendations",
    "includeListStyleRecommendations",
    "includeCompactPostStyleRecommendations",
    "includeContentForwardRecommendations",
    "includeCardPostStyleRecommendations"
)

private object SubredditFeedQueryDocumentFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("query SubredditFeedSdui(", StringComparisonType.STARTS_WITH)
    )
)

/**
 * Writes the variables of the subreddit feed query.
 */
internal object SubredditFeedQueryVariablesFingerprint : Fingerprint(
    classFingerprint = SubredditFeedQueryDocumentFingerprint,
    returnType = "V",
    filters = listOf(
        string("includeCarouselRecommendations")
    )
)
