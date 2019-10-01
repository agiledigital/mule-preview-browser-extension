import { browser, Runtime } from "webextension-polyfill-ts";
import { messages } from "~app/constants";
import { setTabSupportsMulePreview } from "~app/messenging";
import { isDiffMode, stopDiff, toggleDiff } from "~app/modes/diff";
import { isPreviewMode, togglePreview } from "~app/modes/preview";
import {
  getBitbucketDiffElement,
  getBitbucketFilePreviewElement,
  isRunningInBitbucket
} from "~app/scms/bitbucket/ui";
import { Message } from "~app/types/messenging";
import "../scss/extension.scss";

const bitbucketPollPeriod = 1000; // ms
const timeout = bitbucketPollPeriod * 10; // ms
const startTime = new Date().getTime();

console.log("[Mule Preview] Plugin Initialising");

const getRuntime = () => new Date().getTime() - startTime;
const isTimedOut = () => getRuntime() > timeout;

browser.runtime.onMessage.addListener(
  async (rawMessage: unknown, sender: Runtime.MessageSender) => {
    const message = rawMessage as Message;
    console.log(
      `[Mule Preview] Received message from [${sender}]: [${JSON.stringify(
        message
      )}]`
    );
    if (message.type === messages.ToggleDiff) {
      if (getBitbucketDiffElement() !== null) {
        toggleDiff();
      }
      if (getBitbucketFilePreviewElement() !== null) {
        togglePreview();
      }
    } else if (message.type === messages.Reset) {
      reset();
    }
    return true; // Enable async
  }
);

const onReady = () => {
  console.log("[Mule Preview] Bitbucket ready. Enabling button");
  setTabSupportsMulePreview(true);
};

const startReadyPolling = () => {
  const readyPoller = setInterval(() => {
    if (
      getBitbucketDiffElement() !== null ||
      getBitbucketFilePreviewElement() !== null
    ) {
      console.log("[Mule Preview] Ready!");
      clearInterval(readyPoller);
      onReady();
    }
    if (isTimedOut()) {
      console.log("[Mule Preview] Timed out waiting for bitbucket to be ready");
      clearInterval(readyPoller);
    }
  }, bitbucketPollPeriod);
};

const reset = () => {
  stopDiff();
  // Reset button
  setTabSupportsMulePreview(false);

  if (isRunningInBitbucket() && (isDiffMode() || isPreviewMode())) {
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
