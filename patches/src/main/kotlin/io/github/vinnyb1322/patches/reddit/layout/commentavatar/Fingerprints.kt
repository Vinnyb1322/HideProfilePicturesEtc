package io.github.vinnyb1322.patches.reddit.layout.commentavatar

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Composable that draws the author avatar in a comment header.
 */
internal object CommentHeaderAvatarFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("L", "L", "Lkotlin/jvm/functions/Function1;", "L", "I"),
    filters = listOf(
        string("user_avatar_header")
    )
)
