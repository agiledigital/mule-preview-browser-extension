# Mule Preview

A project to take a Mule configuration XML file and render the flows in HTML.

It uses Clojurescript to transform the XML into a React virtual DOM
using the Reagent bindings library.

This was the result of a single [Fedex day](http://www.theenterprisearchitect.eu/blog/2013/07/23/10-reasons-organize-fedex-day/) at [Agile Digital](https://agiledigital.com.au/).
I will hopefully work on it to improve it.

The eventual goal is to embed this in a Bitbucket Server plugin so we can preview
Mule files directly in the browser. A visual diff would also be helpful.

![Example screenshot showing rendered flows](https://raw.githubusercontent.com/NoxHarmonium/mule-preview/master/doc/example.PNG "Example screenshot showing rendered flows")

As you can see, there are still a lot of styling issues but the basic concept is there.

### Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```bash
lein figwheel
```

To start with nREPL (if you are connecting in with CIDER or Calva for VS Code) run the following command in the terminal:

```bash
lein repl
```

Then in the repl run the following command

```bash
(use 'figwheel-sidecar.repl-api) (start-figwheel!) (cljs-repl)
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `http://localhost:3449` page in a browser.

### Building for production

```bash
lein clean
lein package
```

### Extracting the Anypoint Studio Components

#### Setup script

The easiest way to get started is to use `setup.sh`.

    ./setup.sh <Anypoint Studio Plugins Directory>

This will call all the below tools with the options required to setup
the project properly.

#### Mapping extraction tool

I have written a command line tool to extract a map of widget element names, to widget icons/categories.
It outputs JSON which can be read in by the client to render widgets correctly.

For example (in the tools directory):

    lein run -d /mnt/c/Tools/AnypointStudio/plugins/ -o ../client/src/mule_preview/client generate-mappings

#### Image extraction tool

I have written a command line to extract all the images (icons) associated with widgets
and dump the into a directory

For example (in the tools directory):

    lein run -d /mnt/c/Tools/AnypointStudio/plugins/ -o ../client/public/img/icons/ extract-images

#### Light theme

The light theme plugin is a jar full of images with the same name as plugin images.
They are meant to overwrite the plugins image file with the same name.

For example (in the tools directory):

    lein run -d /mnt/c/Tools/AnypointStudio/plugins/ -o ../client/public/img/icons apply-light-theme

#### Getting list of possible widget types

Examine the "org/mule/tooling/ui/modules/core/widgets/attributes.xsd" file
which is stored in the "org.mule.tooling.ui.modules.core\_\*.jar" plugin.

You can see the possible element types for a widget:

- cloud-connector
- component
- connector
- endpoint
- filter
- flow
- global
- global-cloud-connector
- global-endpoint
- global-filter
- global-transformer
- multi-source
- nested
- pattern
- router
- scope
- transformer
- wizard

Nested are a special case, they usually duplicate existing components by name
but define a version of it that can take children.
For example, most if not all of the filter components have a "filter" entry
and a nested entry. The images for the nested entry are mapped to the "nested-image"
key so that they don't overwrite the normal component's images.

##### Additional possible widget types

There are some widget element types that are not in the schema.
You can find them in the plugin.xml files under the
"org.mule.tooling.core.contributionhandler" extension point.

To extract custom defined widget types from plugins that are
not defined in the "http://www.mulesoft.org/schema/mule/tooling.attributes") namespace,
you can use this tool.

Currently there isn't many so you can manually examine the output
and update "mule-widget-tags" in "shared.clj" manually.

For example (in the tools directory):

    lein run -d /mnt/c/Tools/AnypointStudio/plugins/ -o /tmp/ extract-widget-types

I have extracted the following additional types using the widget type
extractor:

- cloud-connector-message-source
- composite
- container
- graphical-container
- nested-container

#### Extracting widget information

Plugins are stored in the "plugins" directory underneath the
Anypoint Studio installation directory.

Some plugins are in jar files, and others are extracted already.

Search each "plugin.xml" file for an element that looks like the following:

    <extension point="org.mule.tooling.core.contribution">

For each of the contribution/externalContribution elements
examine the XML file specified by the `path` attribute.

In this XML Under the root element look for one more more of the
possible widget element types (see above).

For each of those elements:

- The 'localId' attriubute is the xml element name
- 'image' is the widget image

#### New style widget icons and frames

The jar file "org.mule.tooling.ui.theme.light\_\*.jar" under the plugins
directory contains new style icons for all the widgets in plugins.

It also contains frames to go around the new style icons which are located at
"icons/categories/\*.png". The name of these categories match the above widget types
(e.g. filter).

You can simply overwrite the images associated with the above widgets
with the ones from the light theme. The filenames should be the same.
