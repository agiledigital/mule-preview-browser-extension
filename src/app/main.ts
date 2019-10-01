import { browser, Runtime } from "webextension-polyfill-ts";
import { setTabSupportsMulePreview } from "~app/messenging";
import { stopDiff, toggleDiff } from "~app/modes/diff";
import { togglePreview } from "~app/modes/preview";
import { bitbucketServerScmModule } from "~app/scms/bitbucket-server";
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
    const mode = await bitbucketServerScmModule.determineScmMode();
    if (message.type === "ToggleDiff") {
      if (mode === "Diff") {
        toggleDiff(bitbucketServerScmModule);
      }
      if (mode === "Preview") {
        togglePreview(bitbucketServerScmModule);
      }
    } else if (message.type === "Reset") {
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
    if (bitbucketServerScmModule.isReady()) {
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

const reset = async () => {
  stopDiff();
  // Reset button
  await setTabSupportsMulePreview(false);

  const supported = await bitbucketServerScmModule.isSupported();
  if (supported) {
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
