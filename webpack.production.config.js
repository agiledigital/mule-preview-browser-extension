const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const StyleLintPlugin = require("stylelint-webpack-plugin");
const sharedConfig = require("./webpack.shared.config");
const { CheckerPlugin } = require("awesome-typescript-loader");

module.exports = {
  ...sharedConfig,

  mode: "production",

  plugins: [
    new CheckerPlugin(),
    new StyleLintPlugin(),
    new MiniCssExtractPlugin({
      // Options similar to the same options in webpackOptions.output
      // all options are optional
      filename: "[name].css",
      chunkFilename: "[id].css"
    })
  ],

  devtool: "source-map"
};
