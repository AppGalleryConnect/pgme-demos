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
using UnityEngine.UI;
using UnityEngine;
using TMPro;
using GMME;
using System.Text;
using System.Timers;

public class VoiceToTextCtr : MonoBehaviour
{
    public TMP_Text callbackMsg;
    public Button endBtn;
    public Button cancelBtn;
    
    private GameMediaEngine _engine;
    private Timer _timer;
    private int _timerCount = 10;
    private bool _autoStop;
    private AndroidJavaObject _audioManager;
    private readonly System.Object _sLock = new ();
    private readonly StringBuilder _cbMsgBuilder = new ();
    private int _currentVolStream;
    private const int StreamVoiceCall = 0;

    // Start is called before the first frame update
    public void Start()
    {
        Debug.Log("VoiceToText start !");
        callbackMsg.text = "";
        _engine = GMMEEnginHolder.GetInstance().gMMEEngine;
        if (_engine == null)
        {
            Debug.Log("_engine is NULL !");
            AppendVoiceTextCallbackMessage("_engine is NULL");
            return;
        }

        endBtn.onClick.AddListener(OnVoiceEnd);
        cancelBtn.onClick.AddListener(OnVoiceCancel);

        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnVoiceToTextCompleteEvent += VoiceToTextImpl;

        CloseSpeaker();
        CountDownTimer();
        _engine.StartRecordAudioToText(new VoiceParam
        {
            LanguageCode = "zh"
        });
    }

    private Timer GetTimerInstance()
    {
        if (_timer == null)
        {
            lock (_sLock)
            {
                _timer ??= new Timer();
            }
        }
        return _timer;
    }

    private void CountDownTimer()
    {
        // 开始倒计时录音
        _timerCount = 10;
        _timer = GetTimerInstance();
        _timer.Interval = 1000;
        _timer.Elapsed += delegate
        {
            if (_timerCount > 0)
            {
                AppendVoiceTextCallbackMessage("剩余：" + _timerCount + " 秒");
            }
            else
            {
                AppendVoiceTextCallbackMessage("正在转换中 ... ");
                _autoStop = true;
            }
            _timerCount--;
        };
        _timer.Start();
    }

    private void VoiceToTextImpl(string text, int code, string message)
    {
        AppendVoiceTextCallbackMessage("进入了VoiceToTextImpl回调！");
        if (code == 0)
        {
            AppendVoiceTextCallbackMessage(text);
        }
        else
        {
            AppendVoiceTextCallbackMessage($"转换文字异常，code : {code}, msg : {message} ");
        }

        Loom.QueueOnMainThread(() =>
        {
            endBtn.interactable = false;
        });
        StopTimer();
    }
    
    private void OnVoiceEnd()
    {
        Debug.Log("click on End !");
        StopTimer();
        AppendVoiceTextCallbackMessage("停止录音，转换中 ... ");
        _engine.StopRecordAudioToText();
        OpenSpeaker();
        endBtn.interactable = false;
    }

    private void OnVoiceCancel()
    {
        Debug.Log("click on Cancel !");
        _cbMsgBuilder.Clear();
        callbackMsg.text = "";
        StopTimer();
        _engine.StopRecordAudioToText();
        OpenSpeaker();
        DestroyImmediate(gameObject);
    }

    private void AppendVoiceTextCallbackMessage(string message)
    {
        _cbMsgBuilder.Clear();
        _cbMsgBuilder.Append(message);
    }

    // Update is called once per frame
    public void Update()
    {
        RefreshVoiceTextTipText();
        if (!_autoStop) return;
        
        endBtn.interactable = false;
        _engine.StopRecordAudioToText();
        OpenSpeaker();
        StopTimer();
        _autoStop = false;
    }

    private void StopTimer()
    {
        if (_timer == null) return;
        _timer.Stop();
        _timer.Close();
        _timer = null;
    }

    private void RefreshVoiceTextTipText()
    {
        var callbackMessage = _cbMsgBuilder.ToString();
        if (callbackMessage.Length.Equals(0)) return;
        
        callbackMsg.text = _cbMsgBuilder.ToString();
        _cbMsgBuilder.Clear();
    }

    private void OpenSpeaker()
    {
#if UNITY_ANDROID
        try
        {
            _audioManager.Call("setMode", 2);
            if (!_audioManager.Call<bool>("isSpeakerphoneOn"))
            {
                _audioManager.Call("setSpeakerphoneOn", true);
                _audioManager.Call("setStreamVolume", StreamVoiceCall, _currentVolStream, StreamVoiceCall);
            }
        }
        catch (System.Exception e)
        {
            Debug.Log("OpenSpeaker error msg = " + e.Message);
        }
#endif
    }

    private void CloseSpeaker()
    {
#if UNITY_ANDROID
        var unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        var currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
        _audioManager = currentActivity.Call<AndroidJavaObject>("getSystemService", new AndroidJavaObject("java.lang.String", "audio"));
        _currentVolStream = _audioManager.Call<int>("getStreamVolume", StreamVoiceCall);
        try
        {
            if (_audioManager.Call<bool>("isSpeakerphoneOn"))
            {
                _audioManager.Call("setSpeakerphoneOn", false);
                _audioManager.Call("setStreamVolume", StreamVoiceCall, 0, StreamVoiceCall);
            }
        }
        catch (System.Exception e)
        {
            Debug.Log("CloseSpeaker error msg = " + e.Message);
        }
#endif
    }
    
    private void OnDestroy()
    {
        Debug.LogFormat("VoiceToTextCtr OnDestroy");
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnVoiceToTextCompleteEvent -= VoiceToTextImpl;
    }
}
