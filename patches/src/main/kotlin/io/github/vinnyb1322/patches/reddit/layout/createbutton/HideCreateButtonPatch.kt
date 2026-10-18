package io.github.vinnyb1322.patches.reddit.layout.createbutton

import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import io.github.vinnyb1322.patches.reddit.shared.Constants.EXTENSION_PACKAGE
import io.github.vinnyb1322.patches.reddit.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/HideCreateButtonPatch;"

@Suppress("unused")
val hideCreateButtonPatch = bytecodePatch(
    name = "Hide Create button",
    description = "Hides the Create button from the bottom navigation bar."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(sharedExtensionPatch)

    execute {
        BottomNavTabsListFingerprint.let {
            it.method.apply {
                val addIndex = it.instructionMatches.last().index
                val addInstruction = getInstruction<FiveRegisterInstruction>(addIndex)

                // The result of ArrayList.add() is unused, so it can be swapped for a void call.
                replaceInstruction(
                    addIndex,
                    "invoke-static { v${addInstruction.registerC}, v${addInstruction.registerD} }, " +
                            "$EXTENSION_CLASS->addTab(Ljava/util/List;Ljava/lang/Object;)V"
                )
            }
        }
    }
}
