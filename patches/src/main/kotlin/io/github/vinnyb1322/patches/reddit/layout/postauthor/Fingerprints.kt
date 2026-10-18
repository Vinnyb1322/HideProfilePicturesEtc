package io.github.vinnyb1322.patches.reddit.layout.postauthor

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

/**
 * toString() of the header model of feed posts. The first String field read after
 * the ", mediaPath=" constant is the link domain.
 */
internal object MetadataHeaderElementToStringFingerprint : Fingerprint(
    name = "toString",
    returnType = "Ljava/lang/String;",
    parameters = listOf(),
    filters = listOf(
        string("MetadataHeaderElement(linkId="),
        string(", mediaDomain="),
        string(", mediaPath="),
        fieldAccess(opcode = Opcode.IGET_OBJECT, definingClass = "this", type = "Ljava/lang/String;")
    )
)

/**
 * Element that draws the header of feed posts: "r/subreddit • time • domain".
 */
internal const val FEED_POST_HEADER_CLASS = "Lcom/reddit/feeds/impl/ui/composables/a;"

/**
 * Composable for the metadata line at the top of a post in the feed:
 * subreddit, then a row with the time posted and the link domain.
 * Signature: (PostUnitMetadata metadata, ..., String domain, composer, changed).
 */
internal object PostMetadataFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("L", "L", "Lkotlin/jvm/functions/Function1;", "L", "Ljava/lang/String;", "L", "I"),
    filters = listOf(
        string("post_metadata")
    )
)
