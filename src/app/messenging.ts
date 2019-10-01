import { browser } from "webextension-polyfill-ts";
import { Message } from "~app/types/messenging";

const sendMessageRobust = async (currentTabId: number, message: Message) =>
  browser.tabs.sendMessage(currentTabId, message).catch((error: unknown) => {
    console.warn(`Could not send message: [${error}]. Ignoring...`);
  });

export const setTabSupportsMulePreview = async (supported: boolean) =>
  browser.runtime.sendMessage({
    type: "Supported",
    value: supported
  });

export const resetTab = async (tabId: number) =>
  sendMessageRobust(tabId, {
    type: "Reset"
  });

export const toggleDiffOnTab = async (tabId: number) =>
  sendMessageRobust(tabId, {
    type: "ToggleDiff"
  });
