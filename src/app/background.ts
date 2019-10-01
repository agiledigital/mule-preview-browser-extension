import { browser, Runtime } from "webextension-polyfill-ts";
import { resetTab, toggleDiffOnTab } from "~app/messenging";
import { Message } from "~app/types/messenging";

const tabEnabledSet = new Set();

browser.tabs.onUpdated.addListener(async () => {
  console.log("[Mule Preview] change detected. Resetting...");
  const tabArray = await browser.tabs.query({
    currentWindow: true,
    active: true
  });
  const currentTabId = tabArray[0].id;
  if (currentTabId !== undefined) {
    await resetTab(currentTabId);
  }
});

const startDiff = async () => {
  const tabArray = await browser.tabs.query({
    currentWindow: true,
    active: true
  });
  const currentTabId = tabArray[0].id;
  if (currentTabId !== undefined) {
    await toggleDiffOnTab(currentTabId);
  }
};

browser.browserAction.disable();

const updateButtonState = async () => {
  const tabArray = await browser.tabs.query({
    currentWindow: true,
    active: true
  });
  const currentTab = tabArray[0];
  if (currentTab === undefined) {
    return;
  }
  const currentTabId = tabArray[0].id;
  console.log(`Switched to tab [${currentTabId}]`);
  console.log(tabEnabledSet);
  if (tabEnabledSet.has(currentTabId)) {
    console.log("Should enable diff button!");
    browser.browserAction.enable();
    if (!browser.browserAction.onClicked.hasListener(startDiff)) {
      browser.browserAction.onClicked.addListener(startDiff);
    }
  } else {
    console.log("Should disable diff button!");
    browser.browserAction.disable();
    browser.browserAction.onClicked.removeListener(startDiff);
  }
};

browser.runtime.onMessage.addListener(
  async (rawMessage: unknown, sender: Runtime.MessageSender) => {
    const message = rawMessage as Message;
    if (sender.tab === undefined) {
      return;
    }
    const senderTabId = sender.tab.id;
    console.log(
      `Received message from [${senderTabId}]: [${JSON.stringify(message)}]`
    );
    if (message.type === "Supported" && message.value === true) {
      console.log("Adding tab to enabled set!");
      tabEnabledSet.add(senderTabId);
      console.log(tabEnabledSet);
    } else {
      console.log("Removing tab from enabled set!");
      tabEnabledSet.delete(senderTabId);
      console.log(tabEnabledSet);
    }
    updateButtonState();
    return false;
  }
);

browser.tabs.onActivated.addListener(updateButtonState);
