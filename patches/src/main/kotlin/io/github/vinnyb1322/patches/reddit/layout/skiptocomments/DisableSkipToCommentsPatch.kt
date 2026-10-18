package io.github.vinnyb1322.patches.reddit.layout.skiptocomments

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT

@Suppress("unused")
val disableSkipToCommentsPatch = bytecodePatch(
    name = "Don't skip to comments",
    description = "Opens posts at the top from the comment button in the feed, " +
            "instead of jumping past the post to the comments."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    execute {
        OpenCommentsNavigationFingerprint.let {
            it.method.apply {
                val constructorIndex = it.instructionMatches.first().index
                val targetRegister = getInstruction<FiveRegisterInstruction>(constructorIndex).registerC

                // Replace the new scroll target with null, the same as tapping the post itself.
                addInstruction(constructorIndex + 1, "const/16 v$targetRegister, 0x0")
            }
        }
    }
}
