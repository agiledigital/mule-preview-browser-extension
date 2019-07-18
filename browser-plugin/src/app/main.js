import mule_preview from "../../../client/build/npm/mule_preview.client.core";
import browser from "webextension-polyfill";
import fetch from "cross-fetch";
// import fetch from "node-fetch";

console.log("[Mule Preview] Plugin Initialising");

const fetchRawFileFromHash = (filePath, hash) => {
  const fetchUrl = new URL(`../../raw/${filePath}?at=${hash}`, document.URL);
  console.log(`fetchUrl: [${fetchUrl}]`);
  return fetch(fetchUrl).then(response => response.text());
};

const fetchRawFilesFromHashes = (filePath, fromHash, toHash) =>
  Promise.all([
    fetchRawFileFromHash(filePath, fromHash),
    fetchRawFileFromHash(filePath, toHash)
  ]).then(([fileA, fileB]) => ({
    fileA,
    fileB
  }));

const main = () => {
  console.log(
    "[Mule Preview] Bitbucket detected. Will attempt to load overlay."
  );
  const element = document.querySelector(".file-content");

  const currentUrl = new URL(document.URL);

  if (!element || !currentUrl.pathname.endsWith("diff")) {
    console.log("[Mule Preview] Not a diff view. Will not load.");
    return;
  }

  console.log(document.URL);

  // Bitbucket has its own require function ¯\_(ツ)_/¯
  const filePathObj = window.wrappedJSObject
    .require("bitbucket/internal/model/page-state")
    .getFilePath();
  const filePath = filePathObj.attributes.components.join("/");
  console.log(`filePath: ${filePath}`);

  fetch(document.URL, {
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json"
    }
  })
    .then(response => {
      console.log("Response received. Streaming JSON");
      return response.json();
    })
    .then(({ fromHash, toHash }) =>
      fetchRawFilesFromHashes(filePath, fromHash, toHash)
    )
    .then(({ fileA, fileB }) => {
      console.dir(fileA);
      console.dir(fileB);
      mule_preview.mount_diff_on_element(
        element,
        fileA,
        fileB,
        browser.runtime.getURL("public/")
      );
    })
    .catch(err => console.error(err));
};

setTimeout(() => {
  if (typeof window.wrappedJSObject.bitbucket === "object") {
    main();
  } else {
    console.log("[Mule Preview] Bitbucket not detected. Execution disabled.");
  }
}, 5000);
