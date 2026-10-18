package io.github.vinnyb1322.patches.reddit.layout.skiptocomments

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall

/**
 * Opens a post from the comment button in the feed. Creates a scroll target that
 * jumps to the first comment; tapping the post itself passes no target.
 */
internal object OpenCommentsNavigationFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/feeds/impl/ui/actions/OnClickOpenCommentsEventHandler",
    name = "invokeSuspend",
    filters = listOf(
        methodCall(
            definingClass = "Lcom/reddit/postdetail/model/PostDetailScrollTarget;",
            name = "<init>"
        )
    )
)
