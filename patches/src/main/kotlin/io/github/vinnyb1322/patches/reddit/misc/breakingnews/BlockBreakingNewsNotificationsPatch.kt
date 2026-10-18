package io.github.vinnyb1322.patches.reddit.misc.breakingnews

import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.smali.ExternalLabel
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import io.github.vinnyb1322.patches.reddit.shared.Constants.EXTENSION_PACKAGE
import io.github.vinnyb1322.patches.reddit.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/BlockBreakingNewsNotificationsPatch;"

@Suppress("unused")
val blockBreakingNewsNotificationsPatch = bytecodePatch(
    name = "Block breaking news notifications",
    description = "Blocks breaking news push notifications, including breaking sports and entertainment news."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(sharedExtensionPatch)

    execute {
        OnMessageReceivedFingerprint.method.apply {
            // v0 is free here: the original code assigns it before reading it.
            addInstructionsWithLabels(
                0,
                """
                    invoke-static { p1 }, $EXTENSION_CLASS->blockNotification(Ljava/lang/Object;)Z
                    move-result v0
                    if-eqz v0, :show
                    return-void
                """,
                ExternalLabel("show", getInstruction(0))
            )
        }
    }
}
