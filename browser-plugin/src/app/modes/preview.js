import { mount_preview_on_element } from "../../../../client/build/release";
import browser from "webextension-polyfill";
import {
  getFileRawUrlFromContentView,
  getBitbucketFilePreviewElement,
  hideBitbucketFilePreview,
  showBitbucketFilePreview
} from "../scms/bitbucket/ui";
import { getMulePreviewElement } from "../ui";

const startPreview = () => {
  if (getMulePreviewElement() !== null) {
    console.log("[Mule Preview] Already loaded. Will not load again.");
    return;
  }

  console.log(
    "[Mule Preview] Bitbucket detected. Will attempt to load overlay."
  );
  const element = getBitbucketFilePreviewElement();
  const url = getFileRawUrlFromContentView();
  return fetch(url)
    .then(response => response.text())
    .then(content => {
      hideBitbucketFilePreview();
      const mulePreviewElement = document.createElement("div");
      element.insertAdjacentElement("afterend", mulePreviewElement);
      mount_preview_on_element(
        mulePreviewElement,
        content,
        browser.runtime.getURL("public/")
      );
    })
    .catch(err => {
      console.error(err);
    });
};

export const stopPreview = () => {
  const element = getMulePreviewElement();
  if (element) {
    element.remove();
  }
  showBitbucketFilePreview();
};

export const togglePreview = () => {
  const element = getMulePreviewElement();
  if (element === null) {
    console.log("[Mule Preview] No existing element. Starting preview");
    startPreview();
  } else {
    console.log("[Mule Preview] Existing element found. Stopping preview");
    stopPreview();
  }
};

export const isPreviewMode = () => {
  return new URL(document.URL).pathname.endsWith(".xml");
};
