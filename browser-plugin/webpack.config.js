const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const StyleLintPlugin = require("stylelint-webpack-plugin");
const sharedConfig = require("./webpack.shared.config");

module.exports = {
  ...sharedConfig,

  mode: "development",

  plugins: [
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
