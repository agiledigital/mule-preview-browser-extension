
ANYPOINT_STUDIO_ARCHIVE=AnypointStudio-for-linux-64bit-6.6.1-201906072050.tar.gz
ANYPOINT_URL=https://mule-studio.s3.amazonaws.com/6.6.1-U1/AnypointStudio-for-linux-64bit-6.6.1-201906072050.tar.gz
ANYPOINT_STUDIO_INSTALLATION=dependencies/AnypointStudio

CLIENT_FILES := $(shell find client/src -iname '*.cljs')
BROWSER_PLUGIN_FILES := $(shell find browser-plugin/src -iname '*.js')

all: browser-plugin/build/package.zip
.PHONY: all

browser-plugin/build/package.zip: browser-plugin/extension/dist
	@echo ">>> Packaging Browser Extension (Release)"
	mkdir -p browser-plugin/build
	zip -r browser-plugin/build/package.zip browser-plugin/extension/*

browser-plugin/extension/dist: browser-plugin/node_modules/.installed client/build/release.js browser-plugin/extension/public $(BROWSER_PLUGIN_FILES)
	@echo ">>> Building Browser Extension (Release)"
	cd browser-plugin && npm run build

client/build/release.js: client/node_modules/.installed client/src/main/mule_preview/client/mappings.json client/public/img/icons/.timestamp $(CLIENT_FILES)
	@echo ">>> Building Client Module (Release)"
	cd client && npm test && npx shadow-cljs release plugin --source-maps

browser-plugin/extension/public: client/src/main/mule_preview/client/mappings.json client/public/img/icons/.timestamp
	@echo ">>> Copying required assets for Browser Extension"
	rm -rf browser-plugin/extension/public && cp -rv client/public browser-plugin/extension/public

browser-plugin/node_modules/.installed: browser-plugin/package.json
	@echo ">>> Installing dependencies for Browser Extension"
	cd browser-plugin && npm install && touch node_modules/.installed

client/node_modules/.installed: client/package.json
	@echo ">>> Installing dependencies for Client Module"
	cd client && npm install && touch node_modules/.installed

client/src/main/mule_preview/client/mappings.json: $(ANYPOINT_STUDIO_INSTALLATION)/.timestamp
	@echo ">>> Generating mappings metadata from Anypoint Installation for Client Module"
	cd tools && lein run -- -d "../$(ANYPOINT_STUDIO_INSTALLATION)/plugins/" -o ../client/src/main/mule_preview/client generate-mappings

client/public/img/icons/.timestamp: $(ANYPOINT_STUDIO_INSTALLATION)/.timestamp
	@echo ">>> Extracting icon assets from Anypoint Installation for Client Module"
	mkdir -p client/public/img/icons
	cd tools && lein run -- -d "../$(ANYPOINT_STUDIO_INSTALLATION)/plugins/" -o ../client/public/img/icons extract-images
	cd tools && lein run -- -d "../$(ANYPOINT_STUDIO_INSTALLATION)/plugins/" -o ../client/public/img/icons apply-light-theme
	touch $@

$(ANYPOINT_STUDIO_INSTALLATION)/.timestamp: dependencies/$(ANYPOINT_STUDIO_ARCHIVE)
	@echo ">>> Extracting Anypoint Studio dependency"
	cd dependencies && tar -xzf $(ANYPOINT_STUDIO_ARCHIVE)
	touch $@

dependencies/$(ANYPOINT_STUDIO_ARCHIVE):
	@echo ">>> Downloading Anypoint Studio dependency"
	mkdir -p dependencies
	cd dependencies && curl --show-error --fail -o $(ANYPOINT_STUDIO_ARCHIVE) $(ANYPOINT_URL)
	touch $@

clean:
	rm -rf \
	browser-plugin/extension/public \
	browser-plugin/extension/dist \
	client/build/release.js \
	browser-plugin/node_modules \
	client/node_modules \
	client/src/main/mule_preview/client/mappings.json \
	client/public/img/icons \
	dependencies \
	tools/target \
	client/build \
	client/.shadow-cljs \
	browser-plugin/build
.PHONY: clean