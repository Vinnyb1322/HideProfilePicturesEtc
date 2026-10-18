package io.github.vinnyb1322.patches.reddit.layout.searchtrending

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Header of a list in the search screen, such as "Trending today".
 * The first parameter is the list state, whose toString() has the list id and title.
 */
internal object SearchSectionHeaderFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    filters = listOf(
        string("search_section_header"),
        string("search_section_title")
    )
)

/**
 * A trending search query.
 */
internal object TrendingQueryItemFingerprint : Fingerprint(
    returnType = "V",
    filters = listOf(
        string("search_trending_item")
    )
)

/**
 * A community or user suggestion, used both in trending lists and while typing.
 */
internal object TypeaheadSuggestionItemFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf(
        "L",
        "Lkotlin/jvm/functions/Function0;",
        "Lkotlin/jvm/functions/Function0;",
        "L",
        "L",
        "I"
    ),
    filters = listOf(
        string("typeahead_suggestion_item")
    )
)

private object SuggestionFeedElementToStringFingerprint : Fingerprint(
    name = "toString",
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("SearchTypeaheadSuggestionFeedElement(identifier=")
    )
)

/**
 * Parsed suggestion. Signature: (String identifier, String id, String parentId, ...).
 */
internal object SuggestionFeedElementConstructorFingerprint : Fingerprint(
    classFingerprint = SuggestionFeedElementToStringFingerprint,
    name = "<init>",
    parameters = listOf(
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Ljava/util/List;",
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "L",
        "L",
        "Ljava/lang/String;",
        "Z"
    )
)
