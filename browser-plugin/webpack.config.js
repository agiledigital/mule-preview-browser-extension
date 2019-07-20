const webpack = require("webpack");
const path = require("path");

module.exports = {
  mode: 'development',

  entry: {
    main: path.resolve(__dirname, "src/app/main.js"),
    background: path.resolve(__dirname, "src/app/background.js")
  },

  output: {
    path: path.resolve(__dirname, "extension/dist"),
    filename: "[name].js"
  },

  resolve: {
    extensions: [".js", ".json", ".scss", ".css"]
  },

  module: {
    rules: [
      {
        test: /\.js$/,
        loader: "babel-loader",
        exclude: /node_modules/
      },
      {
        test: /\.html$/,
        loaders: ["html-loader"]
      },
      {
        test: /\.(scss|css)$/,
        loaders: ["css-loader", "sass-loader"]
      },
      {
        test: /\.(png|jpg|gif|svg|eot|ttf|woff|woff2)$/,
        loader: "url-loader",
        options: {
          limit: 10000
        }
      }
    ]
  },

  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NoEmitOnErrorsPlugin(),
  ],

  devtool: "eval-cheap-module-source-map"
};
