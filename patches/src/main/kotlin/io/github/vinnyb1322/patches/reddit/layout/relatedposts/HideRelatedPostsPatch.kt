package io.github.vinnyb1322.patches.reddit.layout.relatedposts

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT

@Suppress("unused")
val hideRelatedPostsPatch = bytecodePatch(
    name = "Hide related posts",
    description = "Hides the related posts shown after the end of a comment section."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    execute {
        val matches = BelowCommentRecommendationsToStringFingerprint.matchAll()
        if (matches.isEmpty()) throw BelowCommentRecommendationsToStringFingerprint.patchException()

        matches.forEach { match ->
            val constructor = match.classDef.methods.firstOrNull { method ->
                method.name == "<init>" &&
                        method.parameterTypes.map { it.toString() } == listOf("Ljava/util/ArrayList;")
            } ?: throw PatchException("Constructor not found in ${match.classDef.type}")

            // Replace the parsed edges with an empty list, so there are no posts to show.
            constructor.addInstructions(
                0,
                """
                    new-instance p1, Ljava/util/ArrayList;
                    invoke-direct { p1 }, Ljava/util/ArrayList;-><init>()V
                """
            )
        }
    }
}
