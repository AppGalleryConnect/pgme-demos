# 华为游戏多媒体服务Web示例代码

## 目录<a name="section106mcpsimp"></a>

- [简介](#section119mcpsimp)
- [环境要求](#section123mcpsimp)
- [开发准备](#section126mcpsimp)
- [运行结果](#section137mcpsimp)
- [授权许可](#section147mcpsimp)

## 简介<a name="section119mcpsimp"></a>

该web端示例代码是对华为游戏多媒体服务（HUAWEI Game Mmsdk）的客户端接口进行封装，包含丰富的示例程序，方便您参考或直接使用。

## 环境要求<a name="section123mcpsimp"></a>

推荐使用最新的现代浏览器进行预览，本地构建时建议 nodejs 版本在 14 及以上。

## 开发准备<a name="section126mcpsimp"></a>

1. 确保本地已安装 nodejs 且版本为 14 及以上。
2. 克隆代码到本地，切换到工程根目录，使用 **npm install** 安装工程依赖。
3. 注册[华为帐号](https://developer.huawei.com/consumer/cn/doc/start/registration-and-verification-0000001053628148)。
4. 创建应用并在 AppGallery Connect 中配置相关信息，具体请参考[开发准备](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/web-preparations-0000001050050891)。
5. 在 AppGallery Connect 下载您应用的 **agconnect-services.json** 文件。
6. 打开 **index.ts** 文件，在上一步下载的 json 文件中找到对应的信息并配置到示例代码中：

```ts
...
EngineParamsConfig.setAppId('xxx');
EngineParamsConfig.setClientId('xxx');
EngineParamsConfig.setClientSecret('xxx');
EngineParamsConfig.setGameSecret(''); // 未开通安全加固时无需配置
...
```

7. 执行 **npm run build** 编译打包，打包后的文件会输出到 **dist** 目录下。
8. 在浏览器中运行示例程序。

> 建议在生产环境中开启安全加固，从服务器获取安全签名，有关安全加固可以参考[使用签名初始化SDK](https://developer.huawei.com/consumer/cn/doc/development/AppGallery-connect-Guides/gamemme-signature-web-0000001267991749)。


## 运行结果<a name="section137mcpsimp"></a>

本示例代码提供了以下使用场景：

1. 多媒体引擎初始化/引擎销毁。
2. 创建/加入小队语音房间。
3. 创建/加入国战语音房间。
4. 房间切换。
3. 玩家静默自身语音，屏蔽指定玩家语音。
4. 房主禁言玩家，房主静默房间。

![](figures/mmsdk_sample_result.png)

## 授权许可<a name="section147mcpsimp"></a>

华为游戏多媒体服务web示例代码经过[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)授权许可。

