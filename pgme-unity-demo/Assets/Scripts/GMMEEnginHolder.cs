/**
 * Copyright 2023. Huawei Technologies Co., Ltd. All rights reserved.
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
using UnityEngine;
using GMME;
using System;
using System.Text;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Crypto;

public class GMMEEnginHolder
    {
    public GameMediaEngine gMMEEngine;

    private IGameMMEEventHandler eventHandler;

    private static GMMEEnginHolder gMMEEnginHolder = new GMMEEnginHolder();

    private GMMEEnginHolder()
    {
        eventHandler = new IGameMMEEventHandler();
    }

    public static GMMEEnginHolder GetInstance() {

        return gMMEEnginHolder;
    }

    public void setIGMMEEngine(GameMediaEngine gMMEEngine)
    {
        this.gMMEEngine = gMMEEngine;
    }

    public GameMediaEngine GetEngine(string userId) {
        if (gMMEEngine == null)
        {
            System.Random random = new System.Random();
            string nonce = random.Next(10, 100).ToString();
            TimeSpan ts = DateTime.Now.ToUniversalTime() - new DateTime(1970, 1, 1);
            // 得到精确到毫秒的时间戳（长度13位）
            long time = (long)ts.TotalMilliseconds;
            string timestamp = time.ToString();

            EngineCreateParams engineParams = new EngineCreateParams
            {
                OpenId = userId,
                ClientId = UserAppCfg.GetClientID(),
                ClientSecret = UserAppCfg.GetClientSecret(),
                AppId = UserAppCfg.GetAppID(),
                ApiKey = UserAppCfg.GetApiKey(),
                LogEnable = true,
                LogPath = Application.temporaryCachePath,
                LogSize = 10240,
                Timestamp = timestamp,
                Nonce = nonce,
                Sign = GenerateSign(UserAppCfg.GetAppID(), userId, nonce, timestamp, UserAppCfg.GetGameSecret())
            };
            gMMEEngine = GameMediaEngine.Create(engineParams, eventHandler);
        }
        return gMMEEngine;
    }

    private AsymmetricKeyParameter GetPrivateKeyParameter(string privateKey)
    {
        privateKey = privateKey.Replace("\r", "").Replace("\n", "").Replace(" ", "");
        byte[] privateInfoByte = Convert.FromBase64String(privateKey);
        AsymmetricKeyParameter priKey = PrivateKeyFactory.CreateKey(privateInfoByte);
        return priKey;
    }

    private string GenerateSign(string appId,string openId, string nonce, string timestamp,string gameSecret)
    {
        StringBuilder sourceStr = new StringBuilder().Append("appId=")
        .Append(appId)
        .Append("&nonce=")
        .Append(nonce)
        .Append("&openId=")
        .Append(openId)
        .Append("&timestamp=")
        .Append(timestamp);

        byte[] byteData = Encoding.UTF8.GetBytes(sourceStr.ToString());
        ISigner normalSig = SignerUtilities.GetSigner("SHA256withRSAandMGF1");
        normalSig.Init(true, GetPrivateKeyParameter(gameSecret));
        normalSig.BlockUpdate(byteData, 0, byteData.Length);
        byte[] normalResult = normalSig.GenerateSignature(); //签名结果
        return Convert.ToBase64String(normalResult);
    }

    public IGameMMEEventHandler GetGameMMEEventHandler()
    {
        return eventHandler;
    }

    public void Destory()
    {
        if (gMMEEngine != null)
        {
            GameMediaEngine.Destroy();
        }
    }
}

