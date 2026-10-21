package io.github.vinnyb1322.patches.reddit.misc.swipetohome

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import io.github.vinnyb1322.patches.reddit.shared.Constants.EXTENSION_PACKAGE
import io.github.vinnyb1322.patches.reddit.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/SwipeToHomePatch;"

@Suppress("unused")
val swipeToHomePatch = bytecodePatch(
    name = "Swipe to home feed",
    description = "Swiping right from the left edge of a post's comments or the full screen media " +
            "viewer goes back to the previous post if you came from one, or else to the home feed."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(sharedExtensionPatch)

    execute {
        // The extension calls these with reflection; there must be exactly one tab selector.
        MainActivityBottomNavScreenFingerprint.method
        val tabSelectors = BottomNavScreenSelectTabFingerprint.matchAll()
        if (tabSelectors.size != 1) {
            throw PatchException("Expected one BottomNavScreen tab selector, found ${tabSelectors.size}")
        }

        // region add BottomNavScreen.patch_getScreenBelow()

        // getCurrentScreen() returns entries.get(size - 1) of the navigation stack. Build a copy that
        // returns the entry below it, reusing the obfuscated calls it makes.
        BottomNavScreenGetCurrentScreenFingerprint.let { fingerprint ->
            val calls = fingerprint.method.instructions.mapNotNull { instruction ->
                if (instruction.opcode != Opcode.INVOKE_VIRTUAL && instruction.opcode != Opcode.INVOKE_INTERFACE) {
                    return@mapNotNull null
                }
                (instruction as ReferenceInstruction).reference as MethodReference
            }
            // Router getter, hasEntries(), entries(), size(), ArrayList.get(), entry.screen()
            if (calls.size != 6) throw PatchException("Unexpected getCurrentScreen() structure")
            val (getRouter, hasEntries, getEntries, getSize) = calls
            val getEntryScreen = calls[5]
            val entryType = fingerprint.method.instructions.filter { it.opcode == Opcode.CHECK_CAST }
                .map { ((it as ReferenceInstruction).reference as TypeReference).type }
                .first { it != "Ljava/util/ArrayList;" }

            fingerprint.classDef.methods.add(
                ImmutableMethod(
                    fingerprint.classDef.type,
                    "patch_getScreenBelow",
                    listOf(),
                    "Lcom/reddit/screen/BaseScreen;",
                    AccessFlags.PUBLIC.value or AccessFlags.FINAL.value,
                    null,
                    null,
                    MutableMethodImplementation(2),
                ).toMutable().apply {
                    addInstructionsWithLabels(
                        0,
                        """
                            invoke-virtual { p0 }, $getRouter
                            move-result-object p0
                            if-eqz p0, :none
                            invoke-virtual { p0 }, $hasEntries
                            move-result v0
                            if-eqz v0, :none
                            invoke-virtual { p0 }, $getEntries
                            move-result-object v0
                            invoke-virtual { p0 }, $getSize
                            move-result p0
                            add-int/lit8 p0, p0, -0x2
                            if-ltz p0, :none
                            check-cast v0, Ljava/util/ArrayList;
                            invoke-virtual { v0, p0 }, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;
                            move-result-object p0
                            check-cast p0, $entryType
                            invoke-interface { p0 }, $getEntryScreen
                            move-result-object p0
                            check-cast p0, Lcom/reddit/screen/BaseScreen;
                            return-object p0
                            :none
                            const/4 p0, 0x0
                            return-object p0
                        """
                    )
                }
            )
        }

        // endregion

        MainActivityOnCreateFingerprint.method.addInstruction(
            0,
            "invoke-static/range { p0 .. p0 }, $EXTENSION_CLASS->init(Landroid/app/Activity;)V"
        )
    }
}
