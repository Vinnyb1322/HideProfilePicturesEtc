package io.github.vinnyb1322.patches.reddit.misc.homesort

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterImmediately
import app.morphe.patcher.StringComparisonType
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.opcode
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.Opcode

/**
 * GraphQL queries that load the home feed. Each declares a `$sort: PostFeedSort` variable,
 * which the app leaves unset so the server picks its personalized ranking.
 * The "Latest" feed is the same `homeV3` query with the sort set to NEW.
 */
internal const val MAIN_HOME_FEED_QUERY = "HomeFeedSdui"

/**
 * Other queries that load the home feed. Not every app version has all of them.
 */
internal val OTHER_HOME_FEED_QUERIES = listOf(
    "HomeFeedSduiBg",
    "HomeFeedSduiPrewarm", // Removed in 2026.42.0.
    "HomeFeedWithDefer"
)

internal fun homeFeedQueryDocumentFingerprint(queryName: String) = Fingerprint(
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("query $queryName(", StringComparisonType.STARTS_WITH)
    )
)

/**
 * Writes the variables of a home feed query. Matches the read of the sort field,
 * the check that it is set, and the "sort" key that follows.
 */
internal fun homeFeedQuerySortFingerprint(documentFingerprint: Fingerprint) = Fingerprint(
    classFingerprint = documentFingerprint,
    returnType = "V",
    filters = listOf(
        fieldAccess(opcode = Opcode.IGET_OBJECT, definingClass = "this"),
        opcode(Opcode.INSTANCE_OF, MatchAfterImmediately()),
        opcode(Opcode.IF_EQZ, MatchAfterImmediately()),
        string("sort", location = MatchAfterImmediately())
    )
)
