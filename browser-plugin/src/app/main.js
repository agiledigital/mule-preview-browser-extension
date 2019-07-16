import mule_preview from "../../../client/dist/index.bundle";
import browser from "webextension-polyfill";

console.log("[Mule Preview] Plugin Initialising");

function handleResponse({ message }) {
  console.log(`background script sent a response: ${message}`);
}

function handleError(error) {
  console.log(`Error: ${error}`);
}

const main = () => {
  console.log("[Mule Preview] Will attempt to load overlay.");
  console.dir(mule_preview);
  browser.runtime.sendMessage({}).then(handleResponse, handleError);
};

main();
// https://stash.agiledigital.com.au/rest/api/latest/projects/CSC/repos/system-mercer/browse/src/main/app/configurations/config-functions.xml?at=bugfix%2FCSC-448-pipe-correlation-header-correctly&start=0&limit=20000
// https://stash.agiledigital.com.au/projects/CSC/repos/system-mercer/pull-requests/64/diff
// curl 'https://stash.agiledigital.com.au/projects/CSC/repos/system-mercer/pull-requests/64/diff' -H 'Pragma: no-cache' -H 'Accept-Encoding: gzip, deflate, br' -H 'Accept-Language: en-GB,en-US;q=0.9,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (X11; Fedora; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36' -H 'Accept: application/json, text/javascript, */*; q=0.01' -H 'Referer: https://stash.agiledigital.com.au/projects/CSC/repos/system-mercer/pull-requests/64/diff' -H 'X-Requested-With: XMLHttpRequest' -H 'Cookie: BITBUCKETSESSIONID=6E921F866328F69DCD27B0347921F15D; _atl_bitbucket_remember_me=ODZjNjg5ZDQwY2Y1NDc5Y2MwNzQ2ZTcxY2E0N2VlZThlMGFmNTgyZDo1ZDBiYTY3ZDcyYTgxNDNlM2M1NzQ0NDViNzUyMWNlNGMwZTEyOGZk' -H 'Connection: keep-alive' -H 'Cache-Control: no-cache' --compressed
