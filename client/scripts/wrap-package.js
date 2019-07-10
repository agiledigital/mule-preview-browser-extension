#!/usr/bin/env node

const { readFileSync, writeFileSync } = require("fs");
const { join } = require("path");

const unwrappedBundle = readFileSync(join(__dirname, '../public/js/release.js'));
const wrappedBundle = `
(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define([], factory);
    } else if (typeof module === 'object' && module.exports) {
        // Node. Does not work with strict CommonJS, but
        // only CommonJS-like environments that support module.exports,
        // like Node.
        module.exports = factory();
    } else {
        // Browser globals (root is window)
        root.returnExports = factory();
  }
}(typeof self !== 'undefined' ? self : this, function () {

    ${unwrappedBundle}

    // Just return a value to define the module export.
    // This example returns an object, but the module
    // can return a function as the exported value.
    return mule_preview.client.core;
}));
`;
writeFileSync(join(__dirname, '../public/js/umd.js'), wrappedBundle);