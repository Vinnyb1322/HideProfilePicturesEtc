package io.github.vinnyb1322.patches.reddit.layout.createbutton

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.Opcode

private const val BOTTOM_NAV_TAB = "Lcom/reddit/launch/bottomnav/BottomNavTab;"

/**
 * Constructor of the class that holds one ui model per bottom navigation tab.
 */
private object BottomNavTabsConstructorFingerprint : Fingerprint(
    name = "<init>",
    filters = listOf(
        fieldAccess(opcode = Opcode.SGET_OBJECT, definingClass = BOTTOM_NAV_TAB, name = "Home"),
        fieldAccess(opcode = Opcode.SGET_OBJECT, definingClass = BOTTOM_NAV_TAB, name = "Post"),
        fieldAccess(opcode = Opcode.SGET_OBJECT, definingClass = BOTTOM_NAV_TAB, name = "UnifiedInbox")
    )
)

/**
 * Builds the list of tabs shown in the bottom navigation bar.
 */
internal object BottomNavTabsListFingerprint : Fingerprint(
    classFingerprint = BottomNavTabsConstructorFingerprint,
    returnType = "L",
    parameters = listOf(),
    filters = listOf(
        methodCall(smali = "Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;"),
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            smali = "Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z"
        )
    )
)
