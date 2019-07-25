const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

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
    extensions: [".js", ".json", ".scss", ".css"]
  },

  module: {
    rules: [
      {
        enforce: "pre",
        test: /\.js$/,
        loader: "eslint-loader",
        exclude: [/node_modules/, /release.js/],
        options: {
          cache: true
        }
      },
      {
        test: /\.js$/,
        loader: "babel-loader",
        exclude: [/node_modules/, /release.js/]
      },
      {
        test: /\.html$/,
        loaders: ["html-loader"]
      },
      {
        test: /\.(scss|css)$/,
        use: [
          {
            loader: MiniCssExtractPlugin.loader
          },
          "css-loader",
          "sass-loader"
        ]
      },
      {
        test: /\.(png|jpg|gif|svg|eot|ttf|woff|woff2)$/,
        loader: "url-loader",
        options: {
          limit: 10000
        }
      }
    ]
  }
};
