import React from "react";
import renderer from "react-test-renderer";
import {
  MuleComponent,
  MuleContainer
} from "mule-preview/mule_preview.client.components";
import {
  jsToClj,
  makeAtom,
  makeSet
} from "mule-preview/mule_preview.client.test_utils";

describe("when rendering a Mule component", () => {
  describe("when the component has mapping", () => {
    it("renders correctly", () => {
      const tree = renderer
        .create(
          <MuleComponent
            name="set-payload"
            description="Set some payload"
            css-class="some-css-class"
            content-root="some-content-root"
            location={jsToClj({ line: 1, position: 4 })}
            showing-atom={makeAtom(false)}
          />
        )
        .toJSON();
      expect(tree).toMatchSnapshot();
    });
  });
  describe("when the component is unknown", () => {
    it("renders correctly", () => {
      const tree = renderer
        .create(
          <MuleComponent
            name="sjdsfjlsdf"
            description="Set some payload"
            css-class="some-css-class"
            content-root="some-content-root"
            location={jsToClj({ line: 1, position: 4 })}
            showing-atom={makeAtom(false)}
          />
        )
        .toJSON();
      expect(tree).toMatchSnapshot();
    });
  });
});

describe("when rendering a Mule container", () => {
  describe("when it has no children", () => {
    it("renders correctly", () => {
      const tree = renderer
        .create(
          <MuleContainer
            name="flow"
            description="Do something"
            css-class="some-css-class"
            content-root="some-content-root"
            location={jsToClj({ line: 2, position: 3 })}
            showing-atom={makeAtom(false)}
          />
        )
        .toJSON();
      expect(tree).toMatchSnapshot();
    });
  });
  describe("when it has children", () => {
    it("renders correctly", () => {
      const tree = renderer
        .create(
          <MuleContainer
            name="flow"
            description="Do something"
            css-class="some-css-class"
            content-root="some-content-root"
            location={jsToClj({ line: 2, position: 3 })}
            showing-atom={makeAtom(false)}
          >
            <MuleComponent
              name="set-payload"
              description="Set some payload"
              css-class="some-css-class"
              content-root="some-content-root"
              location={jsToClj({ line: 1, position: 4 })}
              showing-atom={makeAtom(false)}
              labels={makeSet(["edited"])}
            />
            <MuleComponent
              name="message-properties-transformer"
              description="Transform something"
              css-class="some-css-class"
              content-root="some-content-root"
              location={jsToClj({ line: 4, position: 4 })}
              showing-atom={makeAtom(false)}
              labels={makeSet(["removed"])}
            />
            <MuleComponent
              name="http:request"
              description="Make some request"
              css-class="some-css-class"
              content-root="some-content-root"
              location={jsToClj({ line: 7, position: 4 })}
              showing-atom={makeAtom(false)}
              labels={makeSet(["added"])}
            />
          </MuleContainer>
        )
        .toJSON();
      expect(tree).toMatchSnapshot();
    });
  });
});
