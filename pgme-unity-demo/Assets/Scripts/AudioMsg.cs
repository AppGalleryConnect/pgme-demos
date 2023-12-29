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
using System.Collections;
using System.IO;
using GMME;
using UnityEngine;
using UnityEngine.UI;
using Debug = UnityEngine.Debug;

[RequireComponent(typeof(AudioSource))]
public class AudioMsg : BaseCtr
{
    private static int _time = 50;

    // 以下三个是主界面按钮
    public Button returnBtn; // 返回房间主界面按钮

    public Button clearBtn; // 清理语音消息按钮

    public Button holdRecordBtn; // 长按录音

    public GameObject content;

    public GameObject itemPrefb;

    private GameMediaEngine _engine;

    private bool _isStart = false;
    private long _lastTime = 0;
    private bool _isPress = false;

    private IEnumerator _enumerator;
    private string _fileName;
    private string _savePath;

    private  static readonly int _maxRecordTime = 50;
    
    private volatile bool _isGenerateFile = false;

    void Start()
    {
        _time = _maxRecordTime;
        _isGenerateFile = false;
        returnBtn.onClick.AddListener(OnReturnClick);
        clearBtn.onClick.AddListener(OnClearClick);
        _engine = GetEngineInstance();
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRecordAudioMsgCompleteEvent +=
            OnRecordAudioMsgCallback;
        _savePath = Application.temporaryCachePath + "/AudioMsg/";
        if (!Directory.Exists(_savePath))
        {
            Directory.CreateDirectory(_savePath);
        }
        // 在ios中提前获取麦克风权限， 否则在录音前会弹出对话框中断长按录音的按钮弹起事件。
#if UNITY_IOS
        Log("in start");
        _engine.StartRecordAudioMsg(_savePath+"test.m4a");
        _engine.StopRecordAudioMsg();
#endif
        ChangeButtonClickArea();
    }

    void OnDestroy()
    {
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRecordAudioMsgCompleteEvent -=
            OnRecordAudioMsgCallback;
    }

    void OnApplicationFocus(bool hasFocus)
    {
        Log("in Focus" + hasFocus);
        if (!hasFocus && _isPress)
        {
            StopRecord();
        }
    }

    void OnApplicationPause(bool pauseStatus)
    {
        Log("in Pause" + pauseStatus);
        if (pauseStatus && _isPress)
        {
            StopRecord();
        }
    }

    private void OnRecordAudioMsgCallback(string fileName, int code, string msg)
    {
        Debug.Log(String.Format("record call back {0}, {1}, {2}", fileName, code, msg));
        _fileName = fileName;
        int duration = _maxRecordTime - _time;
        if (duration >= 0 && duration <= 1)
        {
            Log("Recording time must be greater than 1 second");
            return;
        }
        _isGenerateFile = true;
    }

    public void Update()
    {
        base.Update();
        if (_isGenerateFile)
        {
            GenerateItem(_fileName);
            _isGenerateFile = false;
        }
    }

    public void StartRecord()
    {
        _enumerator = Count();
        StartCoroutine(_enumerator);
        _fileName = _savePath + Convert.ToString(new DateTimeOffset(DateTime.UtcNow).ToUnixTimeSeconds()) + ".m4a";
        Log("_fileName is " + _fileName);
        _engine.StopPlayAudioMsg();
        _engine.StartRecordAudioMsg(_fileName);
    }

    public void StopRecord()
    {
        _isPress = false;
        Log("the record time is" + Convert.ToString(_maxRecordTime - _time));
        if (_enumerator != null)
        {
            StopCoroutine(_enumerator);
        }
        Log("stopRecord");
        _engine.StopRecordAudioMsg();
        Log(_fileName);
    }


    // 长按以及松开按钮
    public void LongPress(bool bStart)
    {
        _isStart = bStart;
        // 防止按的过快
        if (new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds() - _lastTime < 500 && _isStart)
        {
            Log("click too quick");
            _lastTime = new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
            return;
        }
        if (_isStart)
        {
            _isPress = true;
            Log("Start record");
            _time = _maxRecordTime;
            StartRecord();
            Debug.Log(_time);
        }
        else if (_isPress)
        {
            StopRecord();
        }
        _lastTime = new DateTimeOffset(DateTime.UtcNow).ToUnixTimeMilliseconds();
    }
    private void GenerateItem(string fileName)
    {
        GameObject item = Instantiate(itemPrefb, content.transform);
        item.SendMessage("RenderItem", fileName, SendMessageOptions.RequireReceiver);
    }

    private IEnumerator Count()
    {
        while (_time > 0)
        {
            yield return new WaitForSeconds(1);
            Log(string.Format("Remaining recording time {0} second(s).", Convert.ToString(_time)));
            _time--;
        }

        StopRecord();
        Log(string.Format("Allows up to {0} seconds of recording.", Convert.ToString(_maxRecordTime)));
    }

    private void OnReturnClick()
    {
        Log("click on return btn!");
        Destroy(this.gameObject);
    }

    private void OnClearClick()
    {
        Log("click on clear btn!");
        foreach (Transform child in content.transform)
        {
            Destroy(child.gameObject);
        }
    }

    private void Log(string msg)
    {
        Debug.Log(msg);
        Loom.QueueOnMainThread(() =>
        {
            ClearCallbackMessage();
            AppendCallbackMessage(msg);
        });
    }

    private void ChangeButtonClickArea()
    {
        PhoneAdaptive.ChangeBtnClickArea(returnBtn);
        PhoneAdaptive.ChangeBtnClickArea(clearBtn);
        PhoneAdaptive.ChangeBtnClickArea(holdRecordBtn);
    }
}