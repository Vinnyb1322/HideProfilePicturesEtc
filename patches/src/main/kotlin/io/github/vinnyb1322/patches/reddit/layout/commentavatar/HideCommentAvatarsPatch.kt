package io.github.vinnyb1322.patches.reddit.layout.commentavatar

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT

@Suppress("unused")
val hideCommentAvatarsPatch = bytecodePatch(
    name = "Hide comment avatars",
    description = "Hides profile pictures in comment threads."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    execute {
        // Returning before the composable starts its group makes it draw nothing,
        // the same as if it was never called.
        CommentHeaderAvatarFingerprint.method.addInstruction(0, "return-void")
    }
}
