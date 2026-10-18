package io.github.vinnyb1322.patches.reddit.misc.swipetohome

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import io.github.vinnyb1322.patches.reddit.shared.Constants.EXTENSION_PACKAGE
import io.github.vinnyb1322.patches.reddit.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/SwipeToHomePatch;"

@Suppress("unused")
val swipeToHomePatch = bytecodePatch(
    name = "Swipe to home feed",
    description = "Swiping right on a post's comments or in the full screen media viewer " +
            "goes straight back to the home feed."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(sharedExtensionPatch)

    execute {
        // The extension calls these with reflection; there must be exactly one tab selector.
        MainActivityBottomNavScreenFingerprint.method
        BottomNavScreenGetCurrentScreenFingerprint.method
        val tabSelectors = BottomNavScreenSelectTabFingerprint.matchAll()
        if (tabSelectors.size != 1) {
            throw PatchException("Expected one BottomNavScreen tab selector, found ${tabSelectors.size}")
        }

        MainActivityOnCreateFingerprint.method.addInstruction(
            0,
            "invoke-static/range { p0 .. p0 }, $EXTENSION_CLASS->init(Landroid/app/Activity;)V"
        )
    }
}
