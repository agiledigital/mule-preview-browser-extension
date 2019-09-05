import { readFileSync } from "fs";
import { join } from "path";
import { calculate_diff } from "../../../../../dist/mule_preview.client.views.diff";
import { cljToJs } from "../../../../../dist/mule_preview.client.test_utils";

describe("when diffing two XML files", () => {
  describe("when file B adds elements with nested content", () => {
    it("augments the MAST correctly", () => {
      const xmlFileA = readFileSync(
        join(__dirname, "__fixtures__/nested-add-a.xml")
      );
      const xmlFileB = readFileSync(
        join(__dirname, "__fixtures__/nested-add-b.xml")
      );
      const output = cljToJs(calculate_diff(xmlFileA, xmlFileB, "."));
      expect(output).toMatchSnapshot();
    });
  });

  describe("when diffing MUnit tests", () => {
    it("augments the MAST correctly", () => {
      const xmlFileA = readFileSync(
        join(__dirname, "__fixtures__/munit-a.xml")
      );
      const xmlFileB = readFileSync(
        join(__dirname, "__fixtures__/munit-b.xml")
      );
      const output = cljToJs(calculate_diff(xmlFileA, xmlFileB, "."));
      expect(output).toMatchSnapshot();
    });
  });

  describe("when doing a diff with a non existant initial state", () => {
    it("augments the MAST correctly", () => {
      const xmlFile = readFileSync(join(__dirname, "__fixtures__/munit-a.xml"));
      const output = cljToJs(calculate_diff(undefined, xmlFile, "."));
      expect(output).toMatchSnapshot();
    });
  });

  describe("when doing a diff with a non existant final state", () => {
    it("augments the MAST correctly", () => {
      const xmlFile = readFileSync(join(__dirname, "__fixtures__/munit-b.xml"));
      const output = cljToJs(calculate_diff(xmlFile, undefined, "."));
      expect(output).toMatchSnapshot();
    });
  });
});
