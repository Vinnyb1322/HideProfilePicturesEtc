package io.github.vinnyb1322.patches.reddit.layout.sidebar

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import io.github.vinnyb1322.patches.reddit.shared.Constants.COMPATIBILITY_REDDIT
import io.github.vinnyb1322.patches.reddit.shared.Constants.EXTENSION_PACKAGE
import io.github.vinnyb1322.patches.reddit.shared.sharedExtensionPatch

private const val EXTENSION_CLASS = "$EXTENSION_PACKAGE/HideSidebarItemsPatch;"

@Suppress("unused")
val hideSidebarItemsPatch = bytecodePatch(
    name = "Hide sidebar items",
    description = "Hides Popular, News, Latest, Discover communities, Start a community, " +
            "Games on Reddit, Reddit Pro and Resources from the community sidebar."
) {
    compatibleWith(COMPATIBILITY_REDDIT)

    dependsOn(sharedExtensionPatch)

    execute {
        // Parameters are (List list, Collection items, HeaderItemUiModel header, ...).
        CommunityDrawerAddSectionFingerprint.method.addInstructions(
            0,
            """
                invoke-static { p1, p2 }, $EXTENSION_CLASS->filterSection(Ljava/util/Collection;Ljava/lang/Object;)Ljava/util/Collection;
                move-result-object p1
            """
        )
    }
}
