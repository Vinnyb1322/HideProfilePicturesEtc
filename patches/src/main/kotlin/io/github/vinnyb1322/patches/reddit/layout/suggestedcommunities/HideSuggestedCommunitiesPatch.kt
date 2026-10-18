package io.github.vinnyb1322.patches.reddit.layout.suggestedcommunities

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT

@Suppress("unused")
val hideSuggestedCommunitiesPatch = bytecodePatch(
    name = "Hide suggested communities",
    description = "Hides the suggested communities shown in the feed of a subreddit."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    execute {
        SubredditFeedQueryVariablesFingerprint.method.apply {
            // Each variable is written as its name, then the value is read from its wrapper:
            //   const-string vX, "includeCarouselRecommendations"
            //   ...
            //   iget-object vY, vY, Wrapper;->b:Ljava/lang/Object;
            // Replacing the value with false makes the server leave out that kind of unit.
            val valueReads = instructions.withIndex().filter { (_, instruction) ->
                instruction.opcode == Opcode.CONST_STRING &&
                        ((instruction as ReferenceInstruction).reference as StringReference).string in
                        COMMUNITY_RECOMMENDATION_VARIABLES
            }.map { (nameIndex, _) ->
                instructions.withIndex().drop(nameIndex + 1).firstOrNull { (_, instruction) ->
                    instruction.opcode == Opcode.IGET_OBJECT &&
                            ((instruction as ReferenceInstruction).reference as FieldReference).type ==
                            "Ljava/lang/Object;"
                }?.index ?: throw PatchException("Value of recommendation variable not found")
            }
            if (valueReads.isEmpty()) throw PatchException("No recommendation variables found")

            // Reversed so earlier indices stay valid.
            valueReads.reversed().forEach { index ->
                val register = (instructions[index] as TwoRegisterInstruction).registerA
                addInstruction(index + 1, "sget-object v$register, Ljava/lang/Boolean;->FALSE:Ljava/lang/Boolean;")
            }
        }
    }
}
