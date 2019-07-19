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
