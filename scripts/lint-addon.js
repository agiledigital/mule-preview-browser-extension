// The CLI doesn't let you exclude files so I had to write this little wrapper script
const linter = require("addons-linter");
const { join } = require("path");

const sourceDir = join(__dirname, "../extension");

console.log(`Linting source dir: [${sourceDir}]`);

const instance = linter.createInstance({
  config: {
    // This mimics the first command line argument from yargs,
    // which should be the directory to the extension.
    _: [sourceDir],
    logLevel: process.env.VERBOSE ? "debug" : "fatal",
    stack: Boolean(process.env.VERBOSE),
    pretty: false,
    warningsAsErrors: true,
    metadata: false,
    output: "none",
    boring: false,
    selfHosted: false,
    // Hot update files are full of evals etc.
    shouldScanFile: fileName => !/.*dist\/.*js/.test(fileName),

    runAsBinary: false
  }
});

instance
  .run()
  .then(linterResults => console.dir(linterResults))
  .catch(err => {
    console.error("addons-linter failure: ", err);
    process.exit(1);
  });
