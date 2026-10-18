package io.github.vinnyb1322.patches.reddit.layout.mediaswipe

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT

@Suppress("unused")
val disableMediaViewerSwipePatch = bytecodePatch(
    name = "Disable swiping to next post",
    description = "Disables swiping up in the full screen media viewer to go through more posts."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    execute {
        // Turn off user scrolling of the pager that chains posts.
        // Swiping down to close the viewer is a separate gesture.
        // const/16 because the parameter registers of this large method are above v15.
        FullBleedPagerFingerprint.method.addInstruction(0, "const/16 p1, 0x0")
    }
}
