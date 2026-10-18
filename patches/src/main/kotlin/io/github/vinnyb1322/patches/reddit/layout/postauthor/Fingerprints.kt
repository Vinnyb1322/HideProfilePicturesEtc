package io.github.vinnyb1322.patches.reddit.layout.postauthor

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

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
