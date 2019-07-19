import { mount_diff_on_element } from "../../../../client/build/release";
import browser from "webextension-polyfill";
import { getFileContentFromDiff } from "../scms/bitbucket/fetch";
import {
  getCurrentFile,
  getBitbucketDiffElement,
  hideBitbucketDiff,
  showBitbucketDiff
} from "../scms/bitbucket/ui";
import { getMulePreviewElement } from "../ui";

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
      mount_diff_on_element(
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

export const stopDiff = () => {
  const element = getMulePreviewElement();
  if (element) {
    element.remove();
  }
  showBitbucketDiff();
};

export const toggleDiff = () => {
  const element = getMulePreviewElement();
  if (element === null) {
    console.log("[Mule Preview] No existing element. Starting diff");
    startDiff();
  } else {
    console.log("[Mule Preview] Existing element found. Stopping diff");
    stopDiff();
  }
};

export const isDiffMode = () => {
  return new URL(document.URL).pathname.endsWith("diff");
};
