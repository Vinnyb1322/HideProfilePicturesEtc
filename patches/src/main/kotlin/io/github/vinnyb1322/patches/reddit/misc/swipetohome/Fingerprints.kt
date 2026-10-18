package io.github.vinnyb1322.patches.reddit.misc.swipetohome

import app.morphe.patcher.Fingerprint

internal object MainActivityOnCreateFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/launch/main/MainActivity;",
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;")
)

/**
 * The extension finds these by signature with reflection. Matching them here makes
 * patching fail, instead of the swipe silently doing nothing, if they change.
 */
internal object MainActivityBottomNavScreenFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/launch/main/MainActivity;",
    returnType = "Lcom/reddit/launch/bottomnav/BottomNavScreen;",
    parameters = listOf()
)

internal object BottomNavScreenSelectTabFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/launch/bottomnav/BottomNavScreen;",
    returnType = "V",
    parameters = listOf("Lcom/reddit/launch/bottomnav/BottomNavTab;", "Z")
)

internal object BottomNavScreenGetCurrentScreenFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/launch/bottomnav/BottomNavScreen;",
    name = "getCurrentScreen",
    returnType = "Lcom/reddit/screen/BaseScreen;",
    parameters = listOf()
)
