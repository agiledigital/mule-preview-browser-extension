import { readFileSync } from "fs";
import { join } from "path";
import { calculate_diff_components } from "mule-preview/mule_preview.client.views.diff";
import { cljToJs } from "mule-preview/mule_preview.client.test_utils";

describe("when diffing two XML files", () => {
  describe("when file B adds elements with nested content", () => {
    it("renders correctly", () => {
      const xmlFileA = readFileSync(
        join(__dirname, "__fixtures__/nested-add-a.xml")
      );
      const xmlFileB = readFileSync(
        join(__dirname, "__fixtures__/nested-add-a.xml")
      );
      const output = cljToJs(
        calculate_diff_components(xmlFileA, xmlFileB, ".")
      );
      expect(output).toMatchSnapshot();
    });
  });
});
