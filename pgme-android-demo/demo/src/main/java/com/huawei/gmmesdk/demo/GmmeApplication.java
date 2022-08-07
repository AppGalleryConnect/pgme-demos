/*
   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.huawei.gmmesdk.demo;

import android.app.Application;

import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.EngineCreateParams;
import com.huawei.gmmesdk.demo.handler.GMMECallbackHandler;
import com.huawei.gmmesdk.demo.sign.Signer;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * 应用Application,初始化HRTCEngine
 */
public class GmmeApplication extends Application {
    /**
     * RTC 引擎
     */
    private GameMediaEngine gameAudioEngine;

    /**
     * RTC 引擎回调事件
     */
    private GMMECallbackHandler mHwHandler = new GMMECallbackHandler();

    /**
     * openId
     */
    private String openId;

    /**
     * 构造方法
     */
    public GmmeApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        GameMediaEngine.destroy();
    }

    /**
     * 获取RTC引擎
     *
     * @return HRTCEngine
     */
    public GameMediaEngine getEngine() {
        if (gameAudioEngine == null) {
            EngineCreateParams params = new EngineCreateParams();
            params.setAgcAppId(BuildConfig.agcAppId);
            params.setClientId(BuildConfig.agcClientId);
            params.setClientSecret(BuildConfig.agcClientSecret);
            params.setApiKey(BuildConfig.agcApiKey);
            params.setOpenId(this.openId);
            params.setCountryCode("CN");
            params.setContext(this);
            params.setLogEnable(true);
            try {
                params.setLogPath(this.getFilesDir().getCanonicalPath());
            } catch (IOException ioException) {
                LogUtil.e("log dir not exist, err=" + ioException.getMessage());
            }
            params.setLogSize(Constant.LOG_SIZE);
            setAccessSign(params);
            gameAudioEngine = GameMediaEngine.create(params, mHwHandler);
        }
        return gameAudioEngine;
    }

    private void setAccessSign(EngineCreateParams params) {
        String appId = BuildConfig.agcAppId;
        // 当前游戏密钥的获取方式仅做demo示例，开发者需要放到远端服务器下发给apk
        String gameSecret = BuildConfig.gameSecret;
        // 当前随机数方式仅做demo示例，开发者需要使用更安全的算法来生成随机数
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        int randomCode = Math.abs(random.nextInt());
        String nonce = String.valueOf(randomCode);
        String timestamp = String.valueOf(System.currentTimeMillis());
        params.setSign(Signer.generate(appId, this.openId, nonce, timestamp, gameSecret));
        params.setNonce(nonce);
        params.setTimeStamp(timestamp);
    }

    /**
     * 引擎销毁
     */
    public void destroyGmmEngine() {
        if (gameAudioEngine != null) {
            gameAudioEngine = null;
        }
        GameMediaEngine.destroy();
    }

    public GameMediaEngine getGameAudioEngine() {
        return gameAudioEngine;
    }

    public void setGameAudioEngine(GameMediaEngine gameAudioEngine) {
        this.gameAudioEngine = gameAudioEngine;
    }

    /**
     * 注册回调事件
     *
     * @param handler handler
     */
    public void registerEventHandler(IGameMMEEventHandler handler) {
        mHwHandler.addHandler(handler);
    }

    /**
     * 移除回调事件
     *
     * @param handler handler
     */
    public void removeEventHandler(IGameMMEEventHandler handler) {
        mHwHandler.removeHandler(handler);
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
