package io.github.vinnyb1322.patches.reddit.misc.breakingnews

import app.morphe.patcher.Fingerprint

internal object OnMessageReceivedFingerprint : Fingerprint(
    definingClass = "Lcom/reddit/notification/impl/fcm/RedditMessagingService;",
    name = "onMessageReceived",
    returnType = "V",
    parameters = listOf("Lcom/google/firebase/messaging/RemoteMessage;")
)
