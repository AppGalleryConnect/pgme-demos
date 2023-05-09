# Game Multimedia Service<a name="EN-US_TOPIC_0000001263279341"></a>

## Contents<a name="section106mcpsimp"></a>

-   [Introduction](#section119mcpsimp)
-   [Environment Requirements](#section123mcpsimp)
-   [Preparations](#section126mcpsimp)
-   [Result](#section137mcpsimp)
-   [License](#section147mcpsimp)

## Introduction<a name="section119mcpsimp"></a>

The sample code demonstrates how to integrate Game Multimedia Service into your Android game.

## Environment Requirements<a name="section123mcpsimp"></a>

Android SDK \(API level 21 or higher\), JDK 1.8 or later, and HMS Core \(APK\) 4.0 or later.

## Preparations<a name="section126mcpsimp"></a>

1.  Verify that Android Studio is ready for development.
2.  Register a  [HUAWEI ID](https://developer.huawei.com/consumer/en/doc/start/registration-and-verification-0000001053628148).
3.  Create an app and  [configure app information in AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/config-agc-0000001050196065).
4.  Import the sample code to Android Studio \(3.0 or later version\) in advance.
5.  Configure the sample code as follows:

    \(1\) Download  **agconnect-services.json**  from AppGallery Connect, and copy it to the app root directory of your project.

    \(2\) Open the app-level  **build.gradle**  file, and set  **applicationId**  to your app package name.

6.  Run your game on an Android device.

## Result<a name="section137mcpsimp"></a>

The sample code can be used when your app needs to:

1.  Initialize or destroy the multimedia engine.
2.  Allow players to create or join a voice chat room.
3.  Allow players to mute themselves or a specific player.
4.  Support voice chat ban controlled by the room owner.
5.  Support speech-to-text.

![](figures/mmsdk_sample_result.jpg)

## License<a name="section147mcpsimp"></a>

The sample code is licensed under  [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

