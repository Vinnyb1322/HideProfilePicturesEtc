package io.github.vinnyb1322.patches.reddit.misc.homesort

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.stringOption
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT

private const val POST_FEED_SORT = "Lcom/reddit/type/PostFeedSort;"

@Suppress("unused")
val homeFeedSortPatch = bytecodePatch(
    name = "Sort home feed",
    description = "Sorts the home feed like old.reddit.com, by Hot by default, instead of the " +
            "personalized ranking that changes on every refresh."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    val sort by stringOption(
        key = "sort",
        default = "HOT",
        values = mapOf(
            "Hot" to "HOT",
            "New" to "NEW",
            "Rising" to "RISING",
            "Top" to "TOP"
        ),
        title = "Sort",
        description = "How to sort the home feed.",
        required = true
    )

    execute {
        HOME_FEED_QUERIES.forEach { queryName ->
            homeFeedQuerySortFingerprint(queryName).let {
                it.method.apply {
                    val readIndex = it.instructionMatches[0].index
                    val instanceOfIndex = it.instructionMatches[1].index
                    val read = getInstruction<TwoRegisterInstruction>(readIndex)
                    val instanceOf = getInstruction<TwoRegisterInstruction>(instanceOfIndex)
                    // The type of an optional variable that is set, which wraps its value.
                    val presentType = getInstruction<ReferenceInstruction>(instanceOfIndex).reference.toString()

                    val sortRegister = read.registerA
                    // Assigned by the instance-of right after, so free to use until then.
                    val freeRegister = instanceOf.registerA

                    addInstructions(
                        readIndex + 1,
                        """
                            sget-object v$freeRegister, $POST_FEED_SORT->$sort:$POST_FEED_SORT
                            new-instance v$sortRegister, $presentType
                            invoke-direct { v$sortRegister, v$freeRegister }, $presentType-><init>(Ljava/lang/Object;)V
                        """
                    )
                }
            }
        }
    }
}
