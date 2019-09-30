import { browser } from "webextension-polyfill-ts";
import { messages } from "./constants";
import { Message } from "./types/messenging";

const sendMessageRobust = async (currentTabId: number, message: Message) =>
  browser.tabs.sendMessage(currentTabId, message).catch((error: unknown) => {
    console.warn(`Could not send message: [${error}]. Ignoring...`);
  });

export const setTabSupportsMulePreview = async (supported: boolean) => {
  await browser.runtime.sendMessage({
    type: messages.Supported,
    value: supported
  });
};

export const resetTab = async (tabId: number) =>
  sendMessageRobust(tabId, {
    type: messages.Reset
  });

export const toggleDiffOnTab = async (tabId: number) =>
  sendMessageRobust(tabId, {
    type: messages.ToggleDiff
  });
