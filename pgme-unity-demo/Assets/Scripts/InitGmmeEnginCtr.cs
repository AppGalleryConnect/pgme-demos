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
using UnityEngine.UI;
using TMPro;
using GMME;
using UnityEngine.Android;

public class InitGmmeEnginCtr : BaseCtr
{
    public TMP_Text inputUserIdText;
    public Button initBtn;
    private readonly string[] _permissions = {
        "android.permission.RECORD_AUDIO",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.READ_PHONE_STATE"
    };

    public void Start()
    {
        CheckPermissions();
        //添加点击侦听
        initBtn.onClick.AddListener(OnClick);

        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnEngineCreateCompleteEvent += EngineCreateImpl;

        AppendCallbackMessage("InitGmmeEnginCtr start.");
        Debug.Log("InitGmmeEnginCtr start !");
    }

    private void CheckPermissions() {
        var isGetPermissions = false;
        foreach (var permission in _permissions)
        {
            if (!Permission.HasUserAuthorizedPermission(permission))
            {
                Debug.LogFormat("Request requires {0}", permission);
                isGetPermissions = true;
                break;
            }
            Debug.LogFormat("the [{0}] already exists", permission);
        }
        if (isGetPermissions) {
            Permission.RequestUserPermissions(_permissions);
        }
    }

    private void OnClick()
    {
        Debug.Log("InitGmmeEngine click!");
        var openId = inputUserIdText.text.Trim();
        openId = openId.Replace("\u200B", "");
        if (openId.Equals(""))
        {
            AppendCallbackMessage("please input userid");
            Debug.Log("userid is null");
            return;
        }

        var engine = GMMEEnginHolder.GetInstance().GetEngine(openId);
        if (engine == null)
        {
            Debug.Log("invoke engine create fail");
            return;
        }
        // 保存UserID
        UserAppCfg.SetUserID(openId);
        Debug.Log("invoke engine create success");
    }
    
    private void EngineCreateImpl(int code, string msg)
    {
        Debug.LogFormat($"EngineCreateImpl. code={code}, msg={msg}");
        if (code == 0)
        {
            AppendCallbackMessage("create engine success!");
            // 引擎初始化成功切换到房间场景
            Loom.QueueOnMainThread(() =>
            {
                GameObject obj = Resources.Load<GameObject>("Prefabs/RoomScene");
                Instantiate(obj);
                DestroyImmediate(gameObject);
            });
        }
        else
        {
            AppendCallbackMessage($"create engine error! code : {code}, message : {msg}");
            GMMEEnginHolder.GetInstance().Destory();
            GMMEEnginHolder.GetInstance().setIGMMEEngine(null);
        }
    }

    public void OnDestroy()
    {
        Debug.LogFormat("InitGmmeEnginCtr OnDestroy");
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnEngineCreateCompleteEvent -= EngineCreateImpl;
    }
}
