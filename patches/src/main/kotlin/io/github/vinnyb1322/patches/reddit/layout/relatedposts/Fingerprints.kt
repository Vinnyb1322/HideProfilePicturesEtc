package io.github.vinnyb1322.patches.reddit.layout.relatedposts

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string

/**
 * toString() of the GraphQL model holding the related posts carousel shown below the comments.
 * More than one query has this model, so all matches are patched.
 */
internal object BelowCommentRecommendationsToStringFingerprint : Fingerprint(
    name = "toString",
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("BelowCommentRecommendations(edges=")
    )
)
