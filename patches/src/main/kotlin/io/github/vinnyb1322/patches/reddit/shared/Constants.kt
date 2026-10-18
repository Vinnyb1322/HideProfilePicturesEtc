package io.github.vinnyb1322.patches.reddit.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

internal object Constants {
    /**
     * Matches the newest version supported by Adobo, so both patch bundles
     * can be applied to the same APK.
     */
    val COMPATIBILITY_REDDIT = Compatibility(
        name = "Reddit",
        packageName = "com.reddit.frontpage",
        apkFileType = ApkFileType.APKM,
        appIconColor = 0xFF4500,
        signatures = setOf(
            "970b91143813b4c9d5f3634f672c9fcaa5621b4efaaedafd6c235cbbb869736f"
        ),
        targets = listOf(
            AppTarget(
                version = "2026.42.0",
                minSdk = 29
            ),
            // Last version supported by Adobo 1.5.0.
            AppTarget(
                version = "2026.38.0",
                minSdk = 29
            )
        )
    )

    const val EXTENSION_PACKAGE = "Lio/github/vinnyb1322/extension/reddit/patches"
}
