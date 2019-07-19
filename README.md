## Mule Preview

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
