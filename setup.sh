#!/bin/bash
set -euo pipefail
IFS=$'\n\t'


function usage() {
    echo "Usage: $(basename "$0") <ANYPOINT_STUDIO_INSTALLATION>"
    exit 1
}


if [[ "$#" -ne 1 ]]; then
    usage
fi

ANYPOINT_STUDIO_INSTALLATION="$1"

if [ ! -d "${ANYPOINT_STUDIO_INSTALLATION}" ]; then
    usage
fi

pushd tools
echo "Leiningen takes a really long time to boot, so if the script looks like it's frozen, give it a few more minutes."
echo "In the future I will build these tools into jars or something to make it quicker"
echo ""
echo "Generating mappings..."
lein run -- -d "${ANYPOINT_STUDIO_INSTALLATION}/plugins/" -o ../client/src/mule_preview/client generate-mappings
echo "Extracting plugin images..."
lein run -- -d "${ANYPOINT_STUDIO_INSTALLATION}/plugins/" -o ../client/public/img/icons extract-images
echo "Applying light theme..."
lein run -- -d "${ANYPOINT_STUDIO_INSTALLATION}/plugins/" -o ../client/public/img/icons apply-light-theme
popd
