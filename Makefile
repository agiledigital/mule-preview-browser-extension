
ANYPOINT_STUDIO_ARCHIVE=AnypointStudio-for-linux-64bit-6.6.1-201906072050.tar.gz
ANYPOINT_6_URL=https://mule-studio.s3.amazonaws.com/6.6.1-U1/AnypointStudio-for-linux-64bit-6.6.1-201906072050.tar.gz
ANYPOINT_7_URL=https://mule-studio.s3.amazonaws.com/7.3.5-U5/AnypointStudio-for-linux-64bit-7.3.5-201909031749.tar.gz
ANYPOINT_STUDIO_INSTALLATION=dependencies/AnypointStudio
METADATA_EXTRACTOR_URL=https://github.com/agiledigital/mule-metadata-extractor/releases/download/v1.0.14/mule-metadata-extractor-1.0.14-standalone.jar
METADATA_EXTRACTOR_JAR=mule-metadata-extractor-1.0.14-standalone.jar

BROWSER_PLUGIN_FILES := $(shell find src -type f -iname '*.js')

all: build/package-unsigned.zip
.PHONY: all

build/package-unsigned.zip: extension/dist
	@echo ">>> Packaging Browser Extension (Release)"
	mkdir -p build
	cd extension && zip -r ../build/package-unsigned.zip *

extension/dist: node_modules/.installed extension/public/mappings.json extension/public/img/icons/.timestamp $(BROWSER_PLUGIN_FILES)
	@echo ">>> Building Browser Extension (Release)"
	npm run build

node_modules/.installed: package.json
	@echo ">>> Installing dependencies for Browser Extension"
	npm install && touch node_modules/.installed
	# TODO: Work out how to bundle or supply these assets in a idiomatic way
	mkdir -p extension/public/css
	mkdir -p extension/public/img
	rm -f extension/public/css/* extension/public/img/*.{png,svg}
	cp -r node_modules/@agiledigital/mule-preview/public/css/ extension/public/css
	cp node_modules/@agiledigital/mule-preview/public/img/* extension/public/img/

extension/public/mappings.json: $(ANYPOINT_STUDIO_INSTALLATION)/.timestamp dependencies/$(METADATA_EXTRACTOR_JAR)
	@echo ">>> Generating mappings metadata from Anypoint Installation for Client Module"
	java -jar dependencies/$(METADATA_EXTRACTOR_JAR) -d "$(ANYPOINT_STUDIO_INSTALLATION)" -o extension/public/ generate-mappings

extension/public/img/icons/.timestamp: $(ANYPOINT_STUDIO_INSTALLATION)/.timestamp dependencies/$(METADATA_EXTRACTOR_JAR)
	@echo ">>> Extracting icon assets from Anypoint Installation for Client Module"
	mkdir -p extension/public/img/icons
	java -jar dependencies/$(METADATA_EXTRACTOR_JAR) -d "$(ANYPOINT_STUDIO_INSTALLATION)" -o extension/public/img/icons extract-images
	java -jar dependencies/$(METADATA_EXTRACTOR_JAR) -d "$(ANYPOINT_STUDIO_INSTALLATION)" -o extension/public/img/icons apply-light-theme
	touch $@

$(ANYPOINT_STUDIO_INSTALLATION)/.timestamp: dependencies/$(ANYPOINT_STUDIO_ARCHIVE)
	@echo ">>> Extracting Anypoint Studio dependency"
	cd dependencies && tar -xzf $(ANYPOINT_STUDIO_ARCHIVE)
	touch $@

dependencies/$(METADATA_EXTRACTOR_JAR):
	@echo ">>> Downloading mule-metadata-extractor binary"
	mkdir -p dependencies
	curl -L --show-error --fail -o dependencies/$(METADATA_EXTRACTOR_JAR) $(METADATA_EXTRACTOR_URL)

dependencies/$(ANYPOINT_STUDIO_ARCHIVE):
	@echo ">>> Downloading Anypoint Studio dependency"
	mkdir -p dependencies
	cd dependencies && curl --show-error --fail -o $(ANYPOINT_STUDIO_ARCHIVE) $(ANYPOINT_6_URL)
	touch $@

clean:
	rm -rf \
	.cache \
	build \
	extension/public \
	extension/dist \
	node_modules \
	web-ext-artifacts \
	libs/reagent/target \
	dependencies

.PHONY: clean
