#!/usr/bin/env bash
set -euo pipefail
: "${TAG:?Release tag required}"
: "${GH_REPO:?Repository required}"
if ! gh release view "$TAG" >/dev/null 2>&1; then
  gh release create "$TAG" --prerelease --title "Hyper Essentials $TAG (preview)" \
    --notes 'Debug-signed preview. Physical-device testing pending. See README for requirements.' \
    || gh release view "$TAG" >/dev/null
fi
if [[ "$(gh release view "$TAG" --json assets --jq '[.assets[].name] | index("app-debug.apk") != null')" == true ]]; then
  echo 'APK already published; preserving its signature, checksum and download URL.'
else
  gh release upload "$TAG" "$@"
fi
