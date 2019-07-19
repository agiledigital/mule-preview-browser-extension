import mule_preview from "../../../client/build/npm/mule_preview.client.core";
import browser from "webextension-polyfill";
import fetch from "cross-fetch";

const getCurrentUrl = () => new URL(document.URL);
const timeout = 10000;
const startTime = new Date().getTime();

console.log("[Mule Preview] Plugin Initialising");

const getRuntime = () => new Date().getTime() - startTime;
const isTimedOut = () => getRuntime() > timeout;
const isRunningInBitbucket = () => {
  const metaTag = document.querySelector("meta[name=application-name]");
  return metaTag === null
    ? false
    : metaTag.getAttribute("content") === "Bitbucket";
};

const fetchRawFileFromHash = (filePath, hash) => {
  const fetchUrl = new URL(`../../raw/${filePath}?at=${hash}`, document.URL);
  console.log(`fetchUrl: [${fetchUrl}]`);
  return fetch(fetchUrl).then(response => response.text());
};

const fetchRawFilesFromHashes = (fromFilePath, toFilePath, fromHash, toHash) =>
  Promise.all([
    fetchRawFileFromHash(fromFilePath, fromHash),
    fetchRawFileFromHash(toFilePath, toHash)
  ]).then(([fileA, fileB]) => ({
    fileA,
    fileB
  }));

const getBitbucketDiffElement = () => document.querySelector(".diff-view");

const getMulePreviewElement = () =>
  document.querySelector(".mp.root-component");

const hideBitbucketDiff = () => {
  const element = getBitbucketDiffElement();
  if (element) {
    element.classList.add("mp-hidden");
  }
};

const showBitbucketDiff = () => {
  const element = getBitbucketDiffElement();
  if (element) {
    element.classList.remove("mp-hidden");
  }
};

const findDiffFromFilePath = (diffs, filePath) =>
  diffs.find(
    diff =>
      diff.destination && diff.source && diff.destination.toString === filePath
  );

const extractPathsFromDiff = diff => ({
  fromFilePath: diff.source.toString,
  toFilePath: diff.destination.toString
});

const startDiff = () => {
  if (getMulePreviewElement() !== null) {
    console.log("[Mule Preview] Already loaded. Will not load again.");
    return;
  }

  console.log(
    "[Mule Preview] Bitbucket detected. Will attempt to load overlay."
  );
  const element = getBitbucketDiffElement();

  // Bitbucket has its own require function ¯\_(ツ)_/¯
  const filePathObj = window.wrappedJSObject
    .require("bitbucket/internal/model/page-state")
    .getFilePath();
  const filePath = filePathObj.attributes.components.join("/");

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
    .then(({ fromHash, toHash, diffs }) => {
      const diff = findDiffFromFilePath(diffs, filePath);
      if (diff) {
        const { fromFilePath, toFilePath } = extractPathsFromDiff(diff);
        return fetchRawFilesFromHashes(
          fromFilePath,
          toFilePath,
          fromHash,
          toHash
        );
      }
      throw new Error("Cannot diff files with only one state");
    })
    .then(({ fileA, fileB }) => {
      hideBitbucketDiff();
      const mulePreviewElement = document.createElement("div");
      element.insertAdjacentElement("afterend", mulePreviewElement);
      mule_preview.mount_diff_on_element(
        mulePreviewElement,
        fileA,
        fileB,
        browser.runtime.getURL("public/")
      );
    })
    .catch(err => {
      console.error(err);
    });
};

const stopDiff = () => {
  const element = getMulePreviewElement();
  if (element) {
    element.remove();
  }
  showBitbucketDiff();
};

const toggleDiff = () => {
  const element = getMulePreviewElement();
  if (element === null) {
    console.log("[Mule Preview] No existing element. Starting diff");
    startDiff();
  } else {
    console.log("[Mule Preview] Existing element found. Stopping diff");
    stopDiff();
  }
};

browser.runtime.onMessage.addListener(function(message, sender) {
  console.log(
    `[Mule Preview] Received message from [${sender}]: [${JSON.stringify(
      message
    )}]`
  );
  if (message.type === "toggle-diff") {
    toggleDiff();
  } else if (message.type === "reset") {
    reset();
  }
});

const onReady = () => {
  console.log("[Mule Preview] Bitbucket ready. Enabling button");
  browser.runtime.sendMessage({
    type: "supported",
    value: true
  });
};

const startReadyPolling = () => {
  const readyPoller = setInterval(() => {
    if (getBitbucketDiffElement() !== null) {
      console.log("[Mule Preview]  Bitbucket is now ready");
      clearInterval(readyPoller);
      onReady();
    }
    if (isTimedOut()) {
      console.log("[Mule Preview] Timed out waiting for bitbucket to be ready");
      clearInterval(readyPoller);
    }
  }, 1000);
};

const reset = () => {
  stopDiff();
  // Reset button
  browser.runtime.sendMessage({
    type: "supported",
    value: false
  });

  if (isRunningInBitbucket() && getCurrentUrl().pathname.endsWith("diff")) {
    console.log(
      "[Mule Preview] I'm pretty sure this is the right place but I have to wait for the element to be ready."
    );
    startReadyPolling();
  } else {
    console.log("[Mule Preview] Bitbucket not detected. Execution disabled.");
  }
};

reset();

window.addEventListener(
  "hashchange",
  () => {
    console.log("[Mule Preview] Hash change detected. Resetting...");
    reset();
  },
  false
);
