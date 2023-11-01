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
using System.Collections.Generic;

public class AudioEffectItem : BaseCtr
{
    
   public Button startStopButton; // 开始/停止 按钮
    public Button pauseResumeButton; // 暂停/恢复 按钮
    public Text voiceNameText; // 音效名
    public Text voiceVolumeText; // 音量大小
    public Slider voiceSlider;// 音量滚动条
    public InputField inputField;// 播放次数输入框
    private bool isPlaying; // 音效是否在播放
    public int soundId; // 音效ID
    public string voiceName; //音效文件名称
    private GameMediaEngine _engine;
    void Start()
    {
        _engine = GetEngineInstance();
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnAudioClipStateChangedNotifyEvent += AudioClipStateChangeImpl;
        startStopButton.onClick.AddListener(StartStopButtonFunc);
        pauseResumeButton.onClick.AddListener(PauseResumeButtonFunc);
        voiceSlider.onValueChanged.AddListener(VoiceChangedFunc);
        inputField.onEndEdit.AddListener(PlayCountInputValueChanged);
        inputField.text = "1";
    }

    void OnDestroy()
    {
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnAudioClipStateChangedNotifyEvent -= AudioClipStateChangeImpl;
    }

    // 开始/停止 按钮点击事件
    void StartStopButtonFunc()
    {
        var path = Application.streamingAssetsPath + "/" + this.voiceName;
        #if UNITY_ANDROID && !UNITY_EDITOR
            path = Application.temporaryCachePath + "/Audio/" + this.voiceName;
        #endif
        var btnComponent = startStopButton.transform.Find("BtnName").GetComponent<Text>();
        if (btnComponent == null) return;
        if (btnComponent.text == "播放")
        {
            LocalAudioInfo localAudioInfo = new LocalAudioInfo();
            localAudioInfo.SoundId = soundId;
            localAudioInfo.FilePath = path;
            localAudioInfo.Loop = PlayCount();
            localAudioInfo.Volume = (int)voiceSlider.value;
            int result =  _engine.PlayLocalAudioClip(localAudioInfo);
            if (result == 0) {
                btnComponent.text = "停止";
            } 
        } else {
            int result = _engine.StopLocalAudioClip(this.soundId);
            if (result == 0) {
                btnComponent.text = "播放";
            }
        }
    }

    // 暂停/恢复 按钮点击事件
    void PauseResumeButtonFunc()
    {
        if (!isPlaying) {
            return;
        }
        var btnComponent = pauseResumeButton.transform.Find("BtnName").GetComponent<Text>();
        if (btnComponent == null) return;
        if (btnComponent.text == "暂停") {
            int result = _engine.PauseLocalAudioClip(this.soundId);
            if (result == 0) {
                btnComponent.text = "恢复";
            }
        } else {
            int result = _engine.ResumeLocalAudioClip(this.soundId);
            if (result == 0) {
                btnComponent.text = "暂停";
            }
        }
    }

    // 音量进度条滚动事件
    void VoiceChangedFunc(float value)
    {
        voiceVolumeText.text = String.Format("音量({0})", value.ToString());
        if (this.isPlaying) {
            _engine.SetVolumeOfLocalAudioClip(this.soundId, (int) value);
        }
    }

    // 播放次数输入内容改变
    void PlayCountInputValueChanged(string content)
    {
        if (content.Trim() == "") {
            inputField.text = "1";
        }
    }

    // 获取设置的播放次数
    int PlayCount()
    {
        return int.Parse(inputField.text);
    }

    // 设置 开始/停止 按钮文案
    void SetStartStopBtnText(string text)
    {
        var component = startStopButton.transform.Find("BtnName").GetComponent<Text>();
        if (component != null) {
            component.text = text;
        }
    }

    // 设置 暂停/恢复 按钮文案
    void SetPauseResumeBtnText(string text)
    {
        var component = pauseResumeButton.transform.Find("BtnName").GetComponent<Text>();
        if (component != null) {
            component.text = text;     
        }  
    }

    // 设置音效文案
    public void RenderVoiceText(string name) 
    {
        voiceNameText.text = name;
    }

    // 设置音效ID
    public void RenderSoundId(int soundId)
    {
        this.soundId = soundId;
    }

    // 设置音效名
    public void RenderVoiceName(string name)
    {
        this.voiceName = name;
    }

    // 全部停止事件
    public void StopAllFunc()
    {
        if (!this.isPlaying) {
            return;
        }
        SetStartStopBtnText("播放");
        SetPauseResumeBtnText("暂停");
    }

    // 全部暂停事件
    public void PauseAllFunc()
    {
        if (!this.isPlaying) {
            return;
        }
        SetPauseResumeBtnText("恢复");
    }

    // 全部恢复事件
    public void ResumeAllFunc()
    {
        if (!this.isPlaying) {
            return;
        }
        SetPauseResumeBtnText("暂停");
    }

    // 音效播放状态改变回调
    void AudioClipStateChangeImpl(AudioPlayStateInfo audioPlayStateInfo)
    {
        Loom.QueueOnMainThread(() =>
        {
            this.CallBackFunc(audioPlayStateInfo);
        });
    }

    public void CallBackFunc(AudioPlayStateInfo audioPlayStateInfo) {
       if (audioPlayStateInfo == null) {
            return;
       }
       if (audioPlayStateInfo.SoundId != this.soundId) {
            return;
       }
       switch(audioPlayStateInfo.StateEnum) {
            case HWAudioClipsStateEnum.HW_AUDIO_CLIP_PLAYING: //音频正在播放
            {
                this.isPlaying = true;
            }
            break;

            case HWAudioClipsStateEnum.HW_AUDIO_CLIP_PLAY_COMPLETED: //音频播放完成
            case HWAudioClipsStateEnum.HW_AUDIO_CLIP_PLAY_STOPPED: //音频停止播放
            {
                this.isPlaying = false;
                SetStartStopBtnText("播放");
                SetPauseResumeBtnText("暂停");
            }
            break;

            case HWAudioClipsStateEnum.HW_AUDIO_CLIP_PLAY_FAILED: // 音频播放失败
            {
                this.isPlaying = false;
                SetStartStopBtnText("播放");
                SetPauseResumeBtnText("暂停");
            }
            break;
       }
    }
}