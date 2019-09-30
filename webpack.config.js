const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const StyleLintPlugin = require("stylelint-webpack-plugin");
const sharedConfig = require("./webpack.shared.config");
// `CheckerPlugin` is optional. Use it if you want async error reporting.
// We need this plugin to detect a `--watch` mode. It may be removed later
// after https://github.com/webpack/webpack/issues/3460 will be resolved.
const { CheckerPlugin } = require("awesome-typescript-loader");
const HardSourceWebpackPlugin = require("hard-source-webpack-plugin");

module.exports = {
  ...sharedConfig,

  mode: "development",

  plugins: [
    new HardSourceWebpackPlugin(),
    new CheckerPlugin(),
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NoEmitOnErrorsPlugin(),
    new MiniCssExtractPlugin({
      // Options similar to the same options in webpackOptions.output
      // all options are optional
      filename: "[name].css",
      chunkFilename: "[id].css"
    }),
    new StyleLintPlugin()
  ],

  devtool: "eval-cheap-module-source-map"
};
