import browser from "webextension-polyfill";

const tabEnabledSet = new Set();

browser.tabs.onUpdated.addListener(() => {
  console.log("[Mule Preview] change detected. Resetting...");
  browser.tabs.query({ currentWindow: true, active: true }, tabArray => {
    const currentTabId = tabArray[0].id;
    browser.tabs.sendMessage(currentTabId, {
      type: "reset"
    });
  });
});

const startDiff = tabId => {
  browser.tabs.query({ currentWindow: true, active: true }, tabArray => {
    const currentTabId = tabArray[0].id;
    browser.tabs.sendMessage(currentTabId, {
      type: "toggle-diff"
    });
  });
};

browser.browserAction.disable();

const updateButtonState = () => {
  browser.tabs.query({ currentWindow: true, active: true }, tabArray => {
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
  });
};

browser.runtime.onMessage.addListener(function(message, sender, sendResponse) {
  const senderTabId = sender.tab.id;
  console.log(
    `Received message from [${senderTabId}]: [${JSON.stringify(message)}]`
  );
  if (message.type === "supported" && message.value) {
    console.log("Adding tab to enabled set!");
    tabEnabledSet.add(senderTabId);
    console.log(tabEnabledSet);
  } else {
    console.log("Removing tab from enabled set!");
    tabEnabledSet.delete(senderTabId);
    console.log(tabEnabledSet);
  }
  updateButtonState();
});

browser.tabs.onActivated.addListener(updateButtonState);
