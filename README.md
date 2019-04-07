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

```
lein figwheel
```

To start with nREPL (if you are connecting in with CIDER or Calva for VS Code) run the following command in the terminal:

```
lein repl
```

Then in the repl run the following command

```
(use 'figwheel-sidecar.repl-api) (start-figwheel!) (cljs-repl)
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `http://localhost:3449` page in a browser.

### Building for production

```
lein clean
lein package
```

### Extracting Mule Stuff Notes

#### Mapping extraction tool

I have written a command line tool to extract a map of widget element names, to widget icons/categories.
It outputs JSON which can be read in by the client to render widgets correctly.

    $ lein run -m mule-preview.tools.mapping-generator.main -- -h

    This is a tool for extracting information and icons for Mule widgets.

    Usage: mapping-generator [options]

    Options:
    -d, --anypoint-dir DIR                        Anypoint Studio Directory
    -o, --output FILE       public/mappings.json  Path where the generated mapping file will be written to
    -v                                            Verbosity level
    -h, --help

For example:

    lein run -m mule-preview.tools.mapping-generator.main -- -d /mnt/c/Tools/AnypointStudio/plugins/ -o public/mappings.cli.json

#### Image extraction tool

I have written a command line to extract all the images (icons) associated with widgets
and dump the into a directory

    $ lein run -m mule-preview.tools.image-extractor.main -- -h

    This is a tool for extracting icons for Mule widgets.

    Usage: image-extractor [options]

    Options:
    -d, --anypoint-dir DIR              Anypoint Studio Directory
    -o, --output FOLDER     public/img  Path where the images will be copied to
    -v                                  Verbosity level
    -h, --help

For example:

    lein run -m mule-preview.tools.image-extractor.main -- -d /mnt/c/Tools/AnypointStudio/plugins/ -o public/img/icons/

#### Getting list of possible widget types

Examine the "org/mule/tooling/ui/modules/core/widgets/attributes.xsd" file
which is stored in the "org.mule.tooling.ui.modules.core\_\*.jar" plugin.

You can see the possible element types for a widget:

- connector
- endpoint
- multi-source
- wizard
- global
- pattern
- scope
- global-filter
- global-transformer
- global-cloud-connector
- global-endpoint
- filter
- transformer
- component
- flow
- router
- cloud-connector
- nested

There are some widget element types that I can't find in that schema file
but I have worked them out manually:

- container

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
