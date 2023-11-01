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

namespace GMME
{
    public class UserAppCfg
    {
        public static readonly string clientId = "";
        public static readonly string clientSecret = "";
        public static readonly string appId = "";
        public static readonly string apiKey = "";
        public static readonly string gameSecret = "";
        public static string GetUserID()
        {
            return PlayerPrefs.GetString("UserID", "");
        }
        public static void SetUserID(string userID)
        {
            PlayerPrefs.SetString("UserID", userID);
        }
        public static string GetClientID()
        {
            return PlayerPrefs.GetString("ClientId", clientId);
            
        }
        public static void SetClientID(string ClientId)
        {
            PlayerPrefs.SetString("ClientId", ClientId);
        }
        public static string GetAppID()
        {
            return PlayerPrefs.GetString("AppID", appId);

        }
        public static void SetAppID(string appID)
        {
            PlayerPrefs.SetString("AppID", appID);
        }
        public static string GetClientSecret()
        {
            return PlayerPrefs.GetString("ClientSecret", clientSecret);
        }
        public static void SetClientSecret(string clientSecret)
        {
            PlayerPrefs.SetString("ClientSecret", clientSecret);
        }
        public static string GetApiKey()
        {
            return PlayerPrefs.GetString("ApiKey", apiKey);
        }
        public static void SetApiKey(string apiKey)
        {
            PlayerPrefs.SetString("ApiKey", apiKey);
        }
        public static string GetGameSecret()
        {
            return PlayerPrefs.GetString("GameSecret", gameSecret);
        }
        public static void SetGameSecret(string gameSecret)
        {
            PlayerPrefs.SetString("GameSecret", gameSecret);
        }
    }
}
