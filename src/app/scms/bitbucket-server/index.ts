import {
  DiffContent,
  PreviewContent,
  ScmMode,
  ScmModule
} from "~app/types/scms";
import { handleBitbucketData } from "./fetch";
import {
  getBitbucketData,
  getBitbucketDiffElement,
  getBitbucketFilePreviewElement,
  getFileRawUrlFromContentView
} from "./ui";

/**
 * Supports the self hosted version of Atlassian Bitbucket (Bitbucket Server)
 *
 * Does not support Bitbucket Cloud, this works very differently under the hood
 * and will need to be a separate module.
 *
 * Tested on Atlassian Bitbucket v6.1.1
 * It will probably work on any version of v6 and maybe even earlier versions
 * but has not be tested on those versions.
 */
export const bitbucketServerScmModule: ScmModule = {
  isSupported: (): boolean => {
    const metaTag = document.querySelector("meta[name=application-name]");
    return metaTag === null
      ? false
      : metaTag.getAttribute("content") === "Bitbucket";
  },
  isReady: (): boolean =>
    getBitbucketDiffElement() !== null ||
    getBitbucketFilePreviewElement() !== null,
  determineScmMode: async (): Promise<ScmMode> => {
    if (new URL(document.URL).pathname.endsWith("diff")) {
      return "Diff";
    }
    if (new URL(document.URL).pathname.endsWith(".xml")) {
      return "Preview";
    }
    return "None";
  },
  getDiffContent: async (): Promise<DiffContent> =>
    getBitbucketData().then(handleBitbucketData),
  getPreviewContent: async (): Promise<PreviewContent> => {
    const url = getFileRawUrlFromContentView();
    return fetch(url).then(response => response.text());
  }
};
