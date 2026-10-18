package io.github.vinnyb1322.patches.reddit.layout.repost

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT

@Suppress("unused")
val hideRepostButtonPatch = bytecodePatch(
    name = "Hide repost button",
    description = "Hides the repost button from the action bar of posts."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    execute {
        // The repost action is nullable; the action bar leaves it out when it is null.
        // const/16 because the parameter registers of this large method are above v15.
        PostActionBarFingerprint.method.addInstruction(0, "const/16 p4, 0x0")
    }
}
