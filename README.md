## Mule Preview

[![GitHub License](https://img.shields.io/github/license/agiledigital/mule-preview.svg)](https://github.com/agiledigital/mule-preview/blob/master/LICENSE)
[![Build Status](https://travis-ci.com/agiledigital/mule-preview.svg?branch=master)](https://travis-ci.com/agiledigital/mule-preview)
[![Known Vulnerabilities](https://snyk.io//test/github/agiledigital/mule-preview/badge.svg?targetFile=browser-plugin/package.json)](https://snyk.io//test/github/agiledigital/mule-preview?targetFile=browser-plugin/package.json)
[![Known Vulnerabilities](https://snyk.io//test/github/agiledigital/mule-preview/badge.svg?targetFile=client/package.json)](https://snyk.io//test/github/agiledigital/mule-preview?targetFile=client/package.json)
[![Maintainability](https://api.codeclimate.com/v1/badges/958029813bd4b7f26dca/maintainability)](https://codeclimate.com/github/agiledigital/mule-preview/maintainability)

A project to take Mule configuration XML files and render the flows in HTML
and also display visual diffs for things like Pull Requests.

![Example screenshot showing rendered flows](https://raw.githubusercontent.com/NoxHarmonium/mule-preview/master/doc/example.PNG "Example screenshot showing rendered flows")

### Structure

It is currently made up of three modules placed in subdirectories:

#### Browser Extension

A browser extension written in Javascript that can display visual diffs of Mule files in Bitbucket.

#### Client

The self contained module that can be used by other modules such as the browser extension.

It uses Clojurescript to transform the XML into a React virtual DOM
using the Reagent bindings library.

#### Tools

Some command line tools written in Clojure to extract metadata from a Mule installation
for use in the Client module.

### Building Everything

There is a Makefile that will build everything for you. Once you have the required dependencies you will simply have to run

    $ make -j2

### Dependencies

You will need the following things:

- make (should be on most \*nix like environments)
- A JDK >= 1.8 (E.g. https://adoptopenjdk.net/)
- Leiningen (https://leiningen.org/#install)
- node >= 10.0 (See .nvmrc for exact version)
