import { messages } from "./constants";
import browser from "webextension-polyfill";

const sendMessageRobust = async (currentTabId, message) => {
  try {
    await browser.tabs.sendMessage(currentTabId, message);
  } catch (error) {
    console.warn(`Could not send message: [${error}]. Ignoring...`);
  }
};

export const setTabSupportsMulePreview = async supported => {
  await browser.runtime.sendMessage({
    type: messages.Supported,
    value: supported
  });
};

export const resetTab = async tabId =>
  await sendMessageRobust(tabId, {
    type: messages.Reset
  });

export const toggleDiffOnTab = async tabId =>
  await sendMessageRobust(tabId, {
    type: messages.ToggleDiff
  });
