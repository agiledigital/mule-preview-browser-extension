
ANYPOINT_STUDIO_ARCHIVE=AnypointStudio-for-linux-64bit-6.6.1-201906072050.tar.gz
ANYPOINT_6_URL=https://mule-studio.s3.amazonaws.com/6.6.1-U1/AnypointStudio-for-linux-64bit-6.6.1-201906072050.tar.gz
ANYPOINT_7_URL=https://mule-studio.s3.amazonaws.com/7.3.5-U5/AnypointStudio-for-linux-64bit-7.3.5-201909031749.tar.gz
ANYPOINT_STUDIO_INSTALLATION=dependencies/AnypointStudio
METADATA_EXTRACTOR_URL=https://github.com/agiledigital/mule-metadata-extractor/releases/download/v1.0.14/mule-metadata-extractor-1.0.14-standalone.jar
METADATA_EXTRACTOR_JAR=mule-metadata-extractor-1.0.14-standalone.jar

CLIENT_FILES := $(shell find client/src -type f -iname '*.cljs')
CLIENT_PUBLIC_FILES := $(shell find client/public -type f ! -path "client/public/img/icons/*")
BROWSER_PLUGIN_FILES := $(shell find browser-plugin/src -type f -iname '*.js')

all: browser-plugin/build/package-unsigned.zip
.PHONY: all

browser-plugin/build/package-unsigned.zip: browser-plugin/extension/dist
	@echo ">>> Packaging Browser Extension (Release)"
	mkdir -p browser-plugin/build
	cd browser-plugin/extension && zip -r ../build/package-unsigned.zip *

browser-plugin/extension/dist: browser-plugin/node_modules/.installed client/dist/.timestamp browser-plugin/extension/public $(BROWSER_PLUGIN_FILES)
	@echo ">>> Building Browser Extension (Release)"
	cd browser-plugin && npm run build

client/dist/.timestamp: client/node_modules/.installed client/public/mappings.json client/public/img/icons/.timestamp libs/reagent/target/reagent-0.8.1-BINDFIX.jar $(CLIENT_FILES)
	@echo ">>> Building Client Module (Release)"
	cd client && npm run build
	touch $@

browser-plugin/extension/public: client/public/mappings.json client/public/img/icons/.timestamp $(CLIENT_PUBLIC_FILES)
	@echo ">>> Copying required assets for Browser Extension"
	rm -rf browser-plugin/extension/public && mkdir -p browser-plugin/extension/public && cp -rv client/public/css client/public/img client/public/mappings.json browser-plugin/extension/public

browser-plugin/node_modules/.installed: browser-plugin/package.json
	@echo ">>> Installing dependencies for Browser Extension"
	cd browser-plugin && npm install && touch node_modules/.installed

client/node_modules/.installed: client/package.json
	@echo ">>> Installing dependencies for Client Module"
	cd client && npm install && touch node_modules/.installed

client/public/mappings.json: $(ANYPOINT_STUDIO_INSTALLATION)/.timestamp dependencies/$(METADATA_EXTRACTOR_JAR)
	@echo ">>> Generating mappings metadata from Anypoint Installation for Client Module"
	java -jar dependencies/$(METADATA_EXTRACTOR_JAR) -d "$(ANYPOINT_STUDIO_INSTALLATION)" -o client/public/ generate-mappings

client/public/img/icons/.timestamp: $(ANYPOINT_STUDIO_INSTALLATION)/.timestamp dependencies/$(METADATA_EXTRACTOR_JAR)
	@echo ">>> Extracting icon assets from Anypoint Installation for Client Module"
	mkdir -p client/public/img/icons
	java -jar dependencies/$(METADATA_EXTRACTOR_JAR) -d "$(ANYPOINT_STUDIO_INSTALLATION)" -o client/public/img/icons extract-images
	java -jar dependencies/$(METADATA_EXTRACTOR_JAR) -d "$(ANYPOINT_STUDIO_INSTALLATION)" -o client/public/img/icons apply-light-theme
	touch $@

libs/reagent/target/reagent-0.8.1-BINDFIX.jar: libs/reagent/project.clj
	@echo ">>> Installing forked version of Reagent into local repo"
	cd libs/reagent && lein install

libs/reagent/.timestamp: .gitmodules
	@echo ">>> Updating submodule"
	git submodule update --init --recursive --remote
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
	browser-plugin/.cache \
	browser-plugin/build \
	browser-plugin/extension/public \
	browser-plugin/extension/dist \
	browser-plugin/node_modules \
	browser-plugin/web-ext-artifacts \
	client/public/js \
	client/dist \
	client/build \
	client/.shadow-cljs \
	client/node_modules \
	client/public/mappings.json \
	client/public/img/icons \
	libs/reagent/target #\
	#dependencies

.PHONY: clean
