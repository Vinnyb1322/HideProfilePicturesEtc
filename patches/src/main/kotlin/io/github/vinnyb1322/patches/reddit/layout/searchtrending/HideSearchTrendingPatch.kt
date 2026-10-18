package io.github.vinnyb1322.patches.reddit.layout.searchtrending

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import app.morphe.patcher.util.smali.ExternalLabel
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import io.github.vinnyb1322.patches.reddit.shared.Constants.EXTENSION_PACKAGE
import io.github.vinnyb1322.patches.reddit.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/HideSearchTrendingPatch;"

@Suppress("unused")
val hideSearchTrendingPatch = bytecodePatch(
    name = "Hide search trending",
    description = "Hides the trending searches and trending communities in search."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(sharedExtensionPatch)

    execute {
        /**
         * Returns early from a composable if the extension method returns true.
         * Locals are unassigned at the start of a method, so v0 is free.
         * The range form is used because parameter registers can be above v15.
         */
        fun MutableMethod.hideIf(extensionMethod: String, register: String) {
            addInstructionsWithLabels(
                0,
                """
                    invoke-static/range { $register .. $register }, $EXTENSION_CLASS->$extensionMethod(Ljava/lang/Object;)Z
                    move-result v0
                    if-eqz v0, :show
                    return-void
                """,
                ExternalLabel("show", getInstruction(0))
            )
        }

        // Remember which suggestion belongs to which list.
        SuggestionFeedElementConstructorFingerprint.method.addInstructions(
            0,
            "invoke-static { p2, p3 }, $EXTENSION_CLASS->registerSuggestion(Ljava/lang/String;Ljava/lang/String;)V"
        )

        SearchSectionHeaderFingerprint.method.hideIf("hideSectionHeader", "p0")

        TypeaheadSuggestionItemFingerprint.method.hideIf("hideSuggestion", "p0")

        TrendingQueryItemFingerprint.method.addInstruction(0, "return-void")
    }
}
