# Mule Preview Browser Extension

[![GitHub License](https://img.shields.io/github/license/agiledigital/mule-preview-browser-extension.svg)](https://github.com/agiledigital/mule-preview-browser-extension/blob/master/LICENSE)
[![Build Status](https://travis-ci.com/agiledigital/mule-preview-browser-extension.svg?branch=master)](https://travis-ci.com/agiledigital/mule-preview-browser-extension)
[![Known Vulnerabilities](https://snyk.io//test/github/agiledigital/mule-preview-browser-extension/badge.svg)](https://snyk.io//test/github/agiledigital/mule-preview-browser-extension)
[![Maintainability](https://api.codeclimate.com/v1/badges/ce5e7ca1a6ef3cc5b6ce/maintainability)](https://codeclimate.com/github/agiledigital/mule-preview-browser-extension/maintainability)
![GitHub tag (latest SemVer)](https://img.shields.io/github/tag/agiledigital/mule-preview-browser-extension)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fagiledigital%2Fmule-preview-browser-extension.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fagiledigital%2Fmule-preview-browser-extension?ref=badge_shield)

### Summary

This project takes the [Mule Preview client](https://github.com/agiledigital/mule-preview)
and uses it to replace a diff of Mule XML in Bitbucket
with a graphical diff.

### Instructions

Simply open a PR in Bitbucket and select a Mule XML file.
The Mule Preview button in your browsers toolbar should enable.
Clicking it will toggle Mule Preview mode.

### Support

Only tested on Firefox but it should also be compatible with Chrome.

### Building

Simply run these command to produce a production build

    $ npm install
    $ npm run prebuild
    $ npm run build

Note: This is currently broken because UglifyJs doesn't like the output of Babel at the moment.

### Developing

This commands set you up for development:

    $ npm install
    $ npm prebuild

This command will build the plugin and rebuild if any files change:

    $ npm start

This command will hot reload the extension into Firefox

    $ npx web-ext run -s extension/

This command will ensure your code is up to scratch before comitting.

    $ npm run lint

### Acknowledgement

Thanks to https://github.com/williankeller/browser-extension-boilerplate for making this extension so easy to get running.


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fagiledigital%2Fmule-preview-browser-extension.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fagiledigital%2Fmule-preview-browser-extension?ref=badge_large)