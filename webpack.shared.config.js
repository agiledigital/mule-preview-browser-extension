const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const { TsConfigPathsPlugin } = require("awesome-typescript-loader");

module.exports = {
  entry: {
    main: path.resolve(__dirname, "src/app/main.ts"),
    background: path.resolve(__dirname, "src/app/background.ts"),
    scraper: path.resolve(__dirname, "src/app/scraper.ts")
  },

  output: {
    path: path.resolve(__dirname, "extension/dist"),
    filename: "[name].js"
  },

  resolve: {
    extensions: [".json", ".scss", ".css", ".ts", ".tsx"],
    plugins: [new TsConfigPathsPlugin()]
  },

  node: {
    fs: "empty"
  },

  module: {
    rules: [
      {
        test: /\.tsx?$/,
        enforce: "pre",
        use: [
          {
            loader: "tslint-loader",
            options: { typeCheck: true, failOnHint: true }
          }
        ]
      },
      {
        test: /\.(t|j)sx?$/,
        loader: ["awesome-typescript-loader?module=es6"],
        exclude: [/node_modules/]
      },
      {
        test: /\.js$/,
        use: ["source-map-loader"],
        enforce: "pre",
        include: [/mule-preview/],
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
