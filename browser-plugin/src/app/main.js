import mule_preview from "../../../client/build/npm/mule_preview.client.core";
import browser from "webextension-polyfill";
import { getFileContentFromDiff } from "./scms/bitbucket/fetch";
import {
  getCurrentFile,
  getBitbucketDiffElement,
  hideBitbucketDiff,
  showBitbucketDiff,
  isRunningInBitbucket
} from "./scms/bitbucket/ui";
import { messages } from "./constants";

const getCurrentUrl = () => new URL(document.URL);
const timeout = 10000;
const startTime = new Date().getTime();

console.log("[Mule Preview] Plugin Initialising");

const getRuntime = () => new Date().getTime() - startTime;
const isTimedOut = () => getRuntime() > timeout;
const getMulePreviewElement = () =>
  document.querySelector(".mp.root-component");

const startDiff = () => {
  if (getMulePreviewElement() !== null) {
    console.log("[Mule Preview] Already loaded. Will not load again.");
    return;
  }

  console.log(
    "[Mule Preview] Bitbucket detected. Will attempt to load overlay."
  );
  const element = getBitbucketDiffElement();
  const filePath = getCurrentFile();

  getFileContentFromDiff(filePath)
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
  if (message.type === messages.ToggleDiff) {
    toggleDiff();
  } else if (message.type === messages.Reset) {
    reset();
  }
});

const onReady = () => {
  console.log("[Mule Preview] Bitbucket ready. Enabling button");
  browser.runtime.sendMessage({
    type: messages.Supported,
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
    type: messages.Supported,
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
