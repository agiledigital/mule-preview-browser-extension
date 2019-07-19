/**
 * Functions to get the state of the Bitbucket UI
 */

export const isRunningInBitbucket = () => {
  const metaTag = document.querySelector("meta[name=application-name]");
  return metaTag === null
    ? false
    : metaTag.getAttribute("content") === "Bitbucket";
};

export const getBitbucketDiffElement = () =>
  document.querySelector(".diff-view");

export const hideBitbucketDiff = () => {
  const element = getBitbucketDiffElement();
  if (element) {
    element.classList.add("mp-hidden");
  }
};
export const showBitbucketDiff = () => {
  const element = getBitbucketDiffElement();
  if (element) {
    element.classList.remove("mp-hidden");
  }
};

export const getCurrentFile = () => {
  const filePathObj = window.wrappedJSObject
    .require("bitbucket/internal/model/page-state")
    .getFilePath();
  return filePathObj.attributes.components.join("/");
};
