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

echo "Leiningen takes a really long time to boot, so if the script looks like it's frozen, give it a few more minutes."
echo "In the future I will build these tools into jars or something to make it quicker"
echo ""
echo "Generating mappings..."
lein run -m mule-preview.tools.mapping-generator.main -- -d /mnt/c/Tools/AnypointStudio/plugins/ -o src/mule_preview/client/mappings.json
echo "Extracting plugin images..."
lein run -m mule-preview.tools.image-extractor.main -- -d /mnt/c/Tools/AnypointStudio/plugins/ -o public/img/icons
echo "Applying light theme..."
lein run -m mule-preview.tools.light-theme-applier.main -- -d /mnt/c/Tools/AnypointStudio/plugins/ -o public/img/icons
