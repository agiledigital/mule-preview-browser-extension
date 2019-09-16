const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = {
  entry: {
    main: path.resolve(__dirname, "src/app/main.js"),
    background: path.resolve(__dirname, "src/app/background.js"),
    scraper: path.resolve(__dirname, "src/app/scraper.js")
  },

  output: {
    path: path.resolve(__dirname, "extension/dist"),
    filename: "[name].js"
  },

  resolve: {
    extensions: [".js", ".json", ".scss", ".css"]
  },

  node: {
    fs: "empty"
  },

  module: {
    rules: [
      {
        enforce: "pre",
        test: /\.js$/,
        loader: "eslint-loader",
        include: [/browser-plugin\/src/],
        options: {
          cache: true
        }
      },
      {
        test: /\.js$/,
        use: ["source-map-loader"],
        enforce: "pre",
        include: [/mule-preview/],
        exclude: [/node_modules/]
      },
      {
        test: /\.js$/,
        loader: "babel-loader",
        exclude: [/node_modules/]
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
