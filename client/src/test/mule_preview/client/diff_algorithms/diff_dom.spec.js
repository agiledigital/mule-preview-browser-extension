import { diff } from "mule-preview/mule_preview.client.diff_algorithms.diff_dom";
import { jsToClj, cljToJs } from "mule-preview/mule_preview.client.test_utils";
import fileAMast from "./__fixtures__/file-a.mast.json";
import fileBMast from "./__fixtures__/file-b.mast.json";

describe("when diffing two MAST data structures", () => {
  it("produces correct output", () => {
    const diffOutput = cljToJs(diff(jsToClj(fileAMast), jsToClj(fileBMast)));
    console.log(JSON.stringify(diffOutput, null, 2));
    expect(diffOutput).toMatchSnapshot();
  });
});
