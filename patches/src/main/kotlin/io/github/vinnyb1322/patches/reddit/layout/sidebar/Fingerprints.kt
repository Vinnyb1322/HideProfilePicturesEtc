package io.github.vinnyb1322.patches.reddit.layout.sidebar

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

private object CommunityDrawerSectionFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = $$"Lcom/reddit/navdrawer/analytics/CommunityDrawerAnalytics$Section;",
    parameters = listOf("Lcom/reddit/screens/drawer/community/HeaderItem;"),
    filters = listOf(
        methodCall("Ljava/lang/Enum;->ordinal()I"),
        fieldAccess(
            $$"Lcom/reddit/navdrawer/analytics/CommunityDrawerAnalytics$Section;->" +
                    $$"ABOUT:Lcom/reddit/navdrawer/analytics/CommunityDrawerAnalytics$Section;"
        )
    )
)

/**
 * Adds a section (an optional header followed by its items) to the community drawer list.
 * Single items such as Popular are added through this too, with no header.
 */
internal object CommunityDrawerAddSectionFingerprint : Fingerprint(
    classFingerprint = CommunityDrawerSectionFingerprint,
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    parameters = listOf("Ljava/util/List;", "Ljava/util/Collection;", "L", "L", "Z", "I"),
    filters = listOf(
        methodCall("Ljava/util/Collection;->isEmpty()Z")
    )
)
