## Mule Preview Tools

Command line tools to extract metadata from Anypoint Studio installations.

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
