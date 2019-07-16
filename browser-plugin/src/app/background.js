import browser from "webextension-polyfill";

browser.runtime.onMessage.addListener((message, sender, sendResponse) => {
  console.log(typeof window.bitbucket);
  sendResponse({
    message: `typeof window.bitbucket === ${typeof window.bitbucket}`
  });
});
