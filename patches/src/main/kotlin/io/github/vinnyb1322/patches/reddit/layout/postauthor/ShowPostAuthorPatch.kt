package io.github.vinnyb1322.patches.reddit.layout.postauthor

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import io.github.vinnyb1322.patches.reddit.shared.Constants.EXTENSION_PACKAGE
import io.github.vinnyb1322.patches.reddit.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/ShowPostAuthorPatch;"

@Suppress("unused")
val showPostAuthorPatch = bytecodePatch(
    name = "Show post author",
    description = "Shows the username of the poster after the subreddit, time posted and link " +
            "of posts in the feed."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(sharedExtensionPatch)

    execute {
        // The domain is the last item of the metadata row, so the author is appended to it.
        // The parameter registers are above v15, so copy them to the unassigned v0 and v1 first.
        PostMetadataFingerprint.method.addInstructions(
            0,
            """
                move-object/from16 v0, p4
                move-object/from16 v1, p0
                invoke-static { v0, v1 }, $EXTENSION_CLASS->appendAuthor(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;
                move-result-object v0
                move-object/from16 p4, v0
            """
        )
    }
}
