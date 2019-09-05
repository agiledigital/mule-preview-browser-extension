## Mule Preview Javascript Module

### Summary

This is the core module of the Mule Preview project.

It is a self contained bundle that can be included in other projects
to render Mule XML files.

See the [Mule Preview Browser Plugin](../browser-plugin) for example usage.

### Instructions

The module exposes four functions that can be used in other projects:

- `mount-url-diff-on-element(mulePreviewElement, fileAUrl, fileBUrl, contentRoot)`
- `mount-url-preview-on-element(mulePreviewElement, fileUrl, contentRoot)`
- `mount-diff-on-element(mulePreviewElement, fileAContent, fileBContent, contentRoot)`
- `mount-preview-on-element(mulePreviewElement, fileContent, contentRoot)`

where:

- `mulePreviewElement` is an element somewhere in the DOM to mount the Mule Preview React renderer on
- `fileUrl`, `fileAUrl` and `fileBUrl` are URLs to XML files to render or diff
- `fileAContent`, `fileBContent` and `fileContent` are strings containing XML data to render or diff
- `contentRoot` is the a prefix to prepend to any requests for the Mule component image files.

        import {
            mount-url-diff-on-element,
        } from "mule-preview";

        mount_diff_on_element(
            document.getElementById('root-node'),
            "https://example.com/muleA.xml",
            "https://example.com/muleB.xml",
            "."
        );

### Building

Simply run these command to produce a production build

    $ npx shadow-cljs release plugin

The release bundle will be placed at "build/release.js"

### Developing

To work on this module, the following command will mount Mule Preview in a test environment
with hot reloading.

    $ npx shadow-cljs watch frontend

Simply navigate to http://localhost:8080 in a browser to view the test environment

### FAQ

#### What is the "hack-remove-bad-source-map" script in package.json?

To make packaging easier the mapping metadata extracted from Anypoint Studio
(mappings.json) is embedded into the output files.

Since it is such a large blob of data (~128 kB), it causes most source map processors
to crash with OOM errors. This includes running tests with Jest.

We don't really need that source map anyway, so I've added a temporary build step to
remove it before running tests so that Jest doesn't crash.

When consuming the output module with other tools such as Webpack (see browser-extension)
you will need to exclude the mapping file from being processed.

The long term solution is to find a better way to bundle the JSON blob with
the module but there are other bugs to fix that are higher priority right now.

### Acknowledgements

Math icons made by [Freepik](https://www.freepik.com/home) from www.flaticon.com
