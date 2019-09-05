import { xml__GT_mast as xmlToMast } from "../../../../../dist/mule_preview.client.mast";
import { augment_mast_with_diff as augmentMastWithDiff } from "../../../../../dist/mule_preview.client.transformers.apply_patch";
import {
  xmlToClj,
  cljToJs,
  jsToClj
} from "../../../../../dist/mule_preview.client.test_utils";
import { readFileSync } from "fs";
import { join } from "path";
import exampleDiff from "./__fixtures__/example-diff-dom-output.json";

describe("when converting applying a diff to MAST", () => {
  it("transforms correctly", () => {
    const xmlFile = readFileSync(
      join(__dirname, "__fixtures__/example-mule-file.xml")
    );
    const clojureXmlMap = xmlToClj(xmlFile);
    const mast = xmlToMast(clojureXmlMap);
    const diff = jsToClj(exampleDiff);
    const transformedMast = cljToJs(augmentMastWithDiff(mast, diff));
    expect(transformedMast).toMatchSnapshot();
  });
});
