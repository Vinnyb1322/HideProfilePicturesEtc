package io.github.vinnyb1322.patches.reddit.layout.mediaswipe

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Composable for the pager of the full screen media viewer, which chains
 * additional posts vertically or horizontally.
 * Signature: (ChainingMode chainingMode, boolean userScrollEnabled, PagerState state, ...).
 */
internal object FullBleedPagerFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/fullbleedplayer/ui/composables/",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    custom = { method, _ ->
        // Later versions add parameters after the first two.
        val types = method.parameterTypes.map { it.toString() }
        types.size > 2 &&
                types[0] == "Lcom/reddit/fullbleedplayer/ui/ChainingMode;" &&
                types[1] == "Z"
    }
)
