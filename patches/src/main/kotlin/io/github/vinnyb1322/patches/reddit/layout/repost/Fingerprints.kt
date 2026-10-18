package io.github.vinnyb1322.patches.reddit.layout.repost

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Composable for the action bar below a post (vote, comment, repost, share, ...).
 * Signature: (state, voteAction, modifier, ..., repostAction, shareAction, modAction, composer, changed, default).
 */
internal object PostActionBarFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("L", "L", "L", "L", "L", "L", "L", "L", "I", "I"),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/reddit/ui/compose/ds/ActionBarRefreshVariant;",
            name = "isEnabled"
        )
    ),
    custom = { method, _ ->
        // The vote, repost, share and mod actions all share the same type.
        val types = method.parameterTypes
        types[1] == types[4] && types[4] == types[5] && types[5] == types[6]
    }
)
