import { xml__GT_mast as xmlToMast } from "../../../../dist/mule_preview.client.mast";
import {
  xmlToClj,
  cljToJs
} from "../../../../dist/mule_preview.client.test_utils";
import { readFileSync } from "fs";
import { join } from "path";

describe("when converting an XML file to MAST", () => {
  it("transforms correctly", () => {
    const xmlFile = readFileSync(
      join(__dirname, "__fixtures__/example-mule-file.xml")
    );
    const clojureXmlMap = xmlToClj(xmlFile);
    const transformed = cljToJs(xmlToMast(clojureXmlMap));
    expect(transformed).toMatchSnapshot();
  });
});
