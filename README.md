# Mule Preview

A project to take a Mule configuration XML file and render the flows in HTML.

It uses Clojurescript to transform the XML into a React virtual DOM
using the Reagent bindings library.

This was the result of a single Fedex day at Agile Digital.
I will hopefully work on it to improve it.

The eventual goal is to embed this in a Bitbucket Server plugin so we can preview
Mule files directly in the browser. A visual diff would also be helpful.


### Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

To start with nREPL (if you are connecting in with CIDER or Calva for VS Code) run the following command in the terminal:

```
lein repl
```

Then in the repl run the following command

```
(use 'figwheel-sidecar.repl-api) (start-figwheel!) (cljs-repl)
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `public/index.html` page in the browser.


### Building for production

```
lein clean
lein package
```
