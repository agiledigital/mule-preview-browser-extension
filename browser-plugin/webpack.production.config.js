const webpack = require("webpack");
const path = require("path");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const CompressionPlugin = require("compression-webpack-plugin");

module.exports = {
  entry: {
    main: path.resolve(__dirname, "src/app/main.js"),
    background: path.resolve(__dirname, "src/app/background.js")
  },

  output: {
    path: path.resolve(__dirname, "extension/dist"),
    filename: "[name].js"
  },

  resolve: {
    extensions: [".js", ".json", ".scss", ".css"],
    alias: {
      images: path.resolve(__dirname, "src/images"),
      styles: path.resolve(__dirname, "src/styles")
    }
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
        use: ExtractTextPlugin.extract({
          fallback: "style-loader",
          use: ["css-loader", "sass-loader"]
        })
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
    new webpack.DefinePlugin({
      "process.env.NODE_ENV": JSON.stringify("production")
    }),

    new webpack.optimize.UglifyJsPlugin({
      sourceMap: true,
      compress: {
        warnings: false
      },
      comments: false
    }),

    new ExtractTextPlugin("[name].css"),

    new CompressionPlugin({
      test: /\.js$|\.css$|\.html$/
    }),

    new webpack.optimize.AggressiveMergingPlugin(),
    new webpack.optimize.OccurrenceOrderPlugin()
  ],

  devtool: "cheap-module-source-map"
};
