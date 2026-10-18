# 🧩 Vince's Patches

Unofficial Reddit patches for use with [Morphe](https://github.com/MorpheApp).
This project is not affiliated with or endorsed by the Morphe project.

## ❓ About

Patches that tweak the Reddit app, such as hiding profile pictures and other clutter.

### How to use these patches

Click here to add these patches to Morphe: https://morphe.software/add-source?github=Vinnyb1322/HideProfilePicturesEtc

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.0.0-dev.4](https://github.com/Vinnyb1322/HideProfilePicturesEtc/releases/tag/v1.0.0-dev.4)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;11 patches total
<details open>
<summary>📦 Reddit&nbsp;&nbsp;•&nbsp;&nbsp;11 patches</summary>
<br>

**🎯 Supported versions:**

| 2026.38.0 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Block breaking news notifications](#block-breaking-news-notifications) | Blocks breaking news push notifications, including breaking sports and entertainment news. |  |
| [Disable swiping to next post](#disable-swiping-to-next-post) | Disables swiping up in the full screen media viewer to go through more posts. |  |
| [Don't skip to comments](#don-t-skip-to-comments) | Opens posts at the top from the comment button in the feed, instead of jumping past the post to the comments. |  |
| [Hide Create button](#hide-create-button) | Hides the Create button from the bottom navigation bar. |  |
| [Hide comment avatars](#hide-comment-avatars) | Hides profile pictures in comment threads. |  |
| [Hide related posts](#hide-related-posts) | Hides the related posts shown after the end of a comment section. |  |
| [Hide repost button](#hide-repost-button) | Hides the repost button from the action bar of posts. |  |
| [Hide search trending](#hide-search-trending) | Hides the trending searches and trending communities in search. |  |
| [Hide sidebar items](#hide-sidebar-items) | Hides Popular, News, Latest, Discover communities, Start a community, Games on Reddit, Reddit Pro and Resources from the community sidebar. |  |
| [Show post author](#show-post-author) | Shows the username of the poster after the subreddit, time posted and link of posts in the feed. |  |
| [Swipe to home feed](#swipe-to-home-feed) | Swiping right on a post's comments or in the full screen media viewer goes straight back to the home feed. |  |

</details>

<!-- PATCHES_END -->

### 🛠️ Building locally

- Run `./gradlew buildAndroid`
- The built patches .mpp file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe-Desktop](https://github.com/MorpheApp/morphe-desktop)
  like any other patch bundle.

See the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation) for more information.

## 📜 License

Vince's Patches are licensed under the [GNU General Public License v3.0](LICENSE)
