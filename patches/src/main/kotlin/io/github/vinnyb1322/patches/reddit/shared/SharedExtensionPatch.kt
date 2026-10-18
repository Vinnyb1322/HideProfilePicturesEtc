package io.github.vinnyb1322.patches.reddit.shared

import app.morphe.patcher.patch.bytecodePatch

/**
 * Merges the extension code into the app. Every patch that calls into
 * the extension depends on this.
 */
val sharedExtensionPatch = bytecodePatch {
    extendWith("extensions/extension.mpe")
}
