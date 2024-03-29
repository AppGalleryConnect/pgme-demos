/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

let outputDir = 'dist/' + (process.env.DEMO_BUILD_TARGET || 'cn');
const buildPages = {
  index: {
    entry: './index.ts',
    title: 'GMME Demo',
  },
};

if (process.env.DEMO_BUILD_TARGET === 'dev') {
  buildPages.index = {
    entry: './index.dev.ts',
    title: 'GMME Demo (dev)',
  };
} else if (process.env.DEMO_BUILD_TARGET === 'mirror') {
  buildPages.index = {
    entry: './index.mirror.ts',
    title: 'GMME Demo (mirror)',
  };
}

module.exports = {
  productionSourceMap: false,
  lintOnSave: false,
  publicPath: './',
  devServer: {},
  outputDir,
  pages: buildPages,
  chainWebpack: (config) => {
    config.module
      .rule('images')
      .test(/\.(jpg|png|gif)$/)
      .use('url-loader')
      .loader('url-loader')
      .options({
        limit: 1024 * 20,
        outputPath: 'img',
        name: '[name].[ext]',
      })
      .end();
  },
};
