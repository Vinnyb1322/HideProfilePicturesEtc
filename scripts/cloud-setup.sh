#!/usr/bin/env bash
# Setup script for Claude Code cloud environments.
# Paste this into: environment settings > Setup script.
#
# What it does:
#   1. Installs the Android SDK pieces the Morphe Gradle plugin needs
#      (platform 36, build-tools 36.0.0) into ~/Android/Sdk.
#   2. Makes every new shell export ANDROID_HOME.
#   3. If MORPHE_GPR_USER and MORPHE_GPR_KEY are set as environment variables,
#      writes them to ~/.gradle/gradle.properties as gpr.user / gpr.key,
#      which settings.gradle.kts uses to log in to GitHub Packages.
#
# Needs these hosts allowed in the environment's network settings:
#   dl.google.com, maven.pkg.github.com, jitpack.io
set -euo pipefail

ANDROID_SDK_DIR="$HOME/Android/Sdk"
CMDLINE_TOOLS_ZIP="commandlinetools-linux-13114758_latest.zip"

# Java 17+ is needed by sdkmanager and Gradle. Most images already have it.
if ! command -v java >/dev/null 2>&1; then
  apt-get update -y && apt-get install -y openjdk-21-jdk-headless
fi
command -v unzip >/dev/null 2>&1 || { apt-get update -y && apt-get install -y unzip; }

# 1. Android SDK
if [ ! -x "$ANDROID_SDK_DIR/cmdline-tools/latest/bin/sdkmanager" ]; then
  tmp="$(mktemp -d)"
  curl -fsSL -o "$tmp/tools.zip" "https://dl.google.com/android/repository/$CMDLINE_TOOLS_ZIP"
  unzip -q "$tmp/tools.zip" -d "$tmp"
  mkdir -p "$ANDROID_SDK_DIR/cmdline-tools"
  rm -rf "$ANDROID_SDK_DIR/cmdline-tools/latest"
  mv "$tmp/cmdline-tools" "$ANDROID_SDK_DIR/cmdline-tools/latest"
  rm -rf "$tmp"
fi

sdkmanager="$ANDROID_SDK_DIR/cmdline-tools/latest/bin/sdkmanager"
yes | "$sdkmanager" --sdk_root="$ANDROID_SDK_DIR" --licenses >/dev/null || true
"$sdkmanager" --sdk_root="$ANDROID_SDK_DIR" \
  "platform-tools" "platforms;android-36" "build-tools;36.0.0"

# 2 + 3. Shell startup: ANDROID_HOME, and GitHub Packages credentials.
# Credentials are written at shell start (not baked in here) so changing the
# environment variables takes effect without rerunning this script.
cat > /etc/profile.d/morphe-dev.sh <<'EOF'
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

if [ -n "${MORPHE_GPR_USER:-}" ] && [ -n "${MORPHE_GPR_KEY:-}" ]; then
  mkdir -p "$HOME/.gradle"
  props="$HOME/.gradle/gradle.properties"
  touch "$props" && chmod 600 "$props"
  sed -i '/^gpr\.user=/d;/^gpr\.key=/d' "$props"
  printf 'gpr.user=%s\ngpr.key=%s\n' "$MORPHE_GPR_USER" "$MORPHE_GPR_KEY" >> "$props"
fi
EOF

if ! grep -q 'morphe-dev.sh' "$HOME/.bashrc" 2>/dev/null; then
  echo '[ -f /etc/profile.d/morphe-dev.sh ] && . /etc/profile.d/morphe-dev.sh' >> "$HOME/.bashrc"
fi

echo "Morphe dev setup complete. Android SDK: $ANDROID_SDK_DIR"
