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
using System;
using GMME;
using TMPro;
using UnityEngine;
using UnityEngine.UI;

public class Item : BaseCtr
{
    // 条目里的按钮

    public Text fileName;

    public Button uploadBtn;

    public Button downloadBtn;

    public Button queryBtn;

    public Button playRecordBtn;

    public Button stopPlayRecordBtn;

    private GameMediaEngine _engine;

    private string _fileId;

    private string _msg = "";

    private string _fullFileName;

    void Update()
    {
        if (_msg != "")
        {
            // 该方法只能通过主线程调用， 回调线程不能调用。
            SendMessageUpwards("Log", _msg, SendMessageOptions.RequireReceiver);
            _msg = "";
        }
    }


    void Start()
    {
        uploadBtn.onClick.AddListener(OnUploadClick);
        downloadBtn.onClick.AddListener(OnDownloadClick);
        queryBtn.onClick.AddListener(OnQueryClick);
        playRecordBtn.onClick.AddListener(OnPlayRecordClick);
        stopPlayRecordBtn.onClick.AddListener(OnStopPlayRecordClick);
        _engine = GetEngineInstance();
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnDownloadAudioMsgFileCompleteEvent +=
            OnDownloadAudioMsgFileCallback;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnUploadAudioMsgFileCompleteEvent +=
            OnUploadAudioMsgFileCallback;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPlayAudioMsgCompleteEvent +=
            OnPlayAudioMsgCompleteCallback;
    }

    #region // 回调

    private void OnDownloadAudioMsgFileCallback(string filePath, string fileId, int code, string msg)
    {
        Debug.Log("on download audio msg file call back");
        CallBackLog(code, msg, "filePath : " + filePath + ", fileId : " + fileId);
    }

    private void OnUploadAudioMsgFileCallback(string filePath, string fileId, int code, string msg)
    {
        Debug.Log("on Upload audio msg file call back");
        CallBackLog(code, msg, "filePath : " + filePath + ", fileId : " + fileId);
        _fileId = fileId;
    }

    private void OnPlayAudioMsgCompleteCallback(string filePath, int code, string msg)
    {
        CallBackLog(code, msg, "filePath : " + filePath);
    }

    #endregion

    #region // 点击事件处理

    private void OnUploadClick()
    {
        ClickLog("on Upload click");
        _engine.UploadAudioMsgFile(_fullFileName, 5000);
    }

    private void OnDownloadClick()
    {
        ClickLog("on download click");
        _engine.DownloadAudioMsgFile(_fileId, _fullFileName, 5000);
    }

    private void OnQueryClick()
    {
        AudioMsgFileInfo fileInfo = _engine.GetAudioMsgFileInfo(_fullFileName);
        ClickLog(_fullFileName);
        ClickLog(string.Format("MilliSeconds={0}, Bytes={1}", fileInfo?.MilliSeconds, fileInfo?.Bytes));
    }

    private void OnPlayRecordClick()
    {
        ClickLog("on play record click");
        _engine.PlayAudioMsg(_fullFileName);
    }

    private void OnStopPlayRecordClick()
    {
        ClickLog("on stop play record click");
        _engine.StopPlayAudioMsg();
    }

    #endregion

    public void RenderItem(string info)
    {
        fileName.text = System.IO.Path.GetFileName(info);
        _fullFileName = info;
    }

    private void CallBackLog(int code, string msg, string other)
    {
        _msg = "code: " + Convert.ToString(code) + ", msg:" + msg + ", " + other;
        Debug.LogFormat(_msg);
    }

    private void ClickLog(string msg)
    {
        Debug.LogFormat(msg);
        SendMessageUpwards("Log", msg, SendMessageOptions.RequireReceiver);
    }

    void OnDestroy()
    {
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnDownloadAudioMsgFileCompleteEvent -=
            OnDownloadAudioMsgFileCallback;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnUploadAudioMsgFileCompleteEvent -=
            OnUploadAudioMsgFileCallback;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPlayAudioMsgCompleteEvent -=
            OnPlayAudioMsgCompleteCallback;
    }
}