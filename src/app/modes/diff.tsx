import { MulePreviewDiffContent } from "@agiledigital/mule-preview";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { browser } from "webextension-polyfill-ts";
import { DiffContent } from "~app/scms/bitbucket/types";
import { ScraperResponse } from "~app/types/scraper";
import { getFileContentFromDiff } from "../scms/bitbucket/fetch";
import { getBitbucketData } from "../scms/bitbucket/ui";
import { createContainerElement, getMulePreviewElement } from "../ui";

const handleBitbucketData = (bitbucketData: ScraperResponse) => {
  if (!bitbucketData.valid) {
    throw new Error("Could not fetch Bitbucket data");
  }
  return getFileContentFromDiff(bitbucketData);
};

const handleFileContent = ({ fileA, fileB }: DiffContent) => {
  const element = document.querySelector("body");

  if (element === null) {
    throw new Error("Could not find body element");
  }

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
    .then(content => {
      if (content !== undefined) {
        handleFileContent(content);
      }
    });
};

export const stopDiff = () => {
  const element = getMulePreviewElement();
  if (element !== null) {
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
