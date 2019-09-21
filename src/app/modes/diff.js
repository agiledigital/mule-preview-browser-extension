import React from "react";
import { MulePreviewDiffContent } from "@agiledigital/mule-preview";
import browser from "webextension-polyfill";
import { getFileContentFromDiff } from "../scms/bitbucket/fetch";
import { getBitbucketData } from "../scms/bitbucket/ui";
import { getMulePreviewElement, createContainerElement } from "../ui";
import ReactDOM from "react-dom";

const handleBitbucketData = bitbucketData => {
  if (!bitbucketData.valid) {
    throw new Error("Could not fetch Bitbucket data");
  }
  return getFileContentFromDiff(bitbucketData);
};

const handleFileContent = ({ fileA, fileB }) => {
  const element = document.querySelector("body");

  const mulePreviewElement = createContainerElement();
  element.appendChild(mulePreviewElement);
  ReactDOM.render(
    <MulePreviewDiffContent
      contentStrings={[fileA, fileB]}
      contentRoot={browser.runtime.getURL("public/")}
    />,
    mulePreviewElement
  );
};

const startDiff = () => {
  if (getMulePreviewElement() !== null) {
    console.log("[Mule Preview] Already loaded. Will not load again.");
    return;
  }

  console.log(
    "[Mule Preview] Bitbucket detected. Will attempt to load overlay."
  );
  getBitbucketData()
    .then(handleBitbucketData)
    .then(handleFileContent)
    .catch(err => {
      console.error(err);
    });
};

export const stopDiff = () => {
  const element = getMulePreviewElement();
  if (element) {
    element.remove();
  }
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
