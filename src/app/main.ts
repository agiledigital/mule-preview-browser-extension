import { get } from "total-functions";
import { bitbucketServerScmModule } from "~app/scms/bitbucket-server";
import "../scss/extension.scss";
import { init } from "./plugin";
import { ScmModule } from "./types/scms";

console.log("[Mule Preview] Plugin Initialising. Determining SCM module...");

const scmModules: readonly ScmModule[] = [bitbucketServerScmModule];
const supportedModules = scmModules.filter(module => module.isSupported());
const scmModule = get(supportedModules, 0);

if (scmModule === undefined) {
  console.log("[Mule Preview] Page not supported SCM");
} else {
  init(scmModule);
}
