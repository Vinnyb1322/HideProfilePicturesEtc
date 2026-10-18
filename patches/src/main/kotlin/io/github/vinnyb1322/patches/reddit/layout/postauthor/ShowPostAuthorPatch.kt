package io.github.vinnyb1322.patches.reddit.layout.postauthor

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
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
        // region home feed header: "r/subreddit • time • domain"

        val domainField = MetadataHeaderElementToStringFingerprint.let {
            it.method.getInstruction<ReferenceInstruction>(it.instructionMatches.last().index)
                .reference as FieldReference
        }

        var hooks = 0
        mutableClassDefBy(FEED_POST_HEADER_CLASS).methods.forEach { method ->
            val domainReads = method.instructions.withIndex().filter { (_, instruction) ->
                instruction.opcode == Opcode.IGET_OBJECT &&
                        (instruction as ReferenceInstruction).reference == domainField
            }.map { it.index }

            // Reversed so earlier indices stay valid.
            domainReads.reversed().forEach { index ->
                val read = method.getInstruction<TwoRegisterInstruction>(index)
                // The header object is still in its register after the read, so the extension
                // rebuilds the domain from it. The range form works with any register number.
                method.addInstructions(
                    index + 1,
                    """
                        invoke-static/range { v${read.registerB} .. v${read.registerB} }, $EXTENSION_CLASS->getDomainWithAuthor(Ljava/lang/Object;)Ljava/lang/String;
                        move-result-object v${read.registerA}
                    """
                )
                hooks++
            }
        }
        if (hooks == 0) throw PatchException("Feed post header does not read the link domain")

        // endregion

        // region post unit metadata row, used by other feed layouts

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

        // endregion
    }
}
