import { MulePreviewDiffContent } from "@agiledigital/mule-preview";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { browser } from "webextension-polyfill-ts";
import { DiffContent, ScmModule } from "~app/types/scms";
import { createContainerElement, getMulePreviewElement } from "~app/ui";

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

const startDiff = async (scmModule: ScmModule) => {
  if (getMulePreviewElement() !== null) {
    console.log("[Mule Preview] Already loaded. Will not load again.");
    return;
  }

  console.log(
    "[Mule Preview] Bitbucket detected. Will attempt to load overlay."
  );
  return scmModule.getDiffContent().then(handleFileContent);
};

export const stopDiff = () => {
  const element = getMulePreviewElement();
  if (element !== null) {
    element.remove();
  }
};

export const toggleDiff = (scmModule: ScmModule) => {
  const element = getMulePreviewElement();
  if (element === null) {
    console.log("[Mule Preview] No existing element. Starting diff");
    startDiff(scmModule);
  } else {
    console.log("[Mule Preview] Existing element found. Stopping diff");
    stopDiff();
  }
};

export const isDiffMode = () => {
  return new URL(document.URL).pathname.endsWith("diff");
};
