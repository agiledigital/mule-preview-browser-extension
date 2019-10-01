import { MulePreviewContent } from "@agiledigital/mule-preview";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { browser } from "webextension-polyfill-ts";
import { ScmModule } from "~app/types/scms";
import { createContainerElement, getMulePreviewElement } from "~app/ui";

const startPreview = async (scmModule: ScmModule) => {
  if (getMulePreviewElement() !== null) {
    console.log("[Mule Preview] Already loaded. Will not load again.");
    return;
  }

  console.log(
    "[Mule Preview] Bitbucket detected. Will attempt to load overlay."
  );
  const element = document.querySelector("body");

  if (element === null) {
    throw new Error("Could not find body element");
  }

  return scmModule.getPreviewContent().then(content => {
    const mulePreviewElement = createContainerElement();
    element.appendChild(mulePreviewElement);
    ReactDOM.render(
      <MulePreviewContent
        contentString={content}
        contentRoot={browser.runtime.getURL("public/")}
      />,
      mulePreviewElement
    );
  });
};

export const stopPreview = () => {
  const element = getMulePreviewElement();
  if (element !== null) {
    element.remove();
  }
};

export const togglePreview = (scmModule: ScmModule) => {
  const element = getMulePreviewElement();
  if (element === null) {
    console.log("[Mule Preview] No existing element. Starting preview");
    startPreview(scmModule);
  } else {
    console.log("[Mule Preview] Existing element found. Stopping preview");
    stopPreview();
  }
};

export const isPreviewMode = () => {
  return new URL(document.URL).pathname.endsWith(".xml");
};
