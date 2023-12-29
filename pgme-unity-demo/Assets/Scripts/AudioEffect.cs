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
using System.IO;
using UnityEngine.Networking;

public class AudioEffect : BaseCtr
{
    public Button stopAllButton; // 全部停止
    public Button pauseAllButton; // 全部暂停
    public Button resumeAllButton; //全部恢复
    public Text voiceText; // 全部音量文案
    public Slider allVoiceSlider;// 全部音量滚动条
    public Button closeBtn;// 关闭按钮

    private GameObject _audioEffect1;// 音效1
    private GameObject _audioEffect2;// 音效2
    private GameMediaEngine _engine;
    public delegate void AudioEventDelegate();
    public static AudioEventDelegate EventCloseAudioEffect;

    void Start()
    {
        _engine = GetEngineInstance();
        AddSubviews();
        stopAllButton.onClick.AddListener(StopAllButtonClickFunc);
        pauseAllButton.onClick.AddListener(PauseAllButtonClickFunc);
        resumeAllButton.onClick.AddListener(ResumeAllButtonFunc);
        closeBtn.onClick.AddListener(CloseButtonFunc);
        allVoiceSlider.onValueChanged.AddListener(AllVoiceChangedFunc);
#if UNITY_ANDROID && !UNITY_EDITOR
            CopyAudioToTemporaryCachePath();
#endif
        PhoneAdaptive.ChangeBtnClickArea(closeBtn);
    }

    void AddSubviews()
    {
        if (_audioEffect1 == null) {
             _audioEffect1 = Resources.Load<GameObject>("Prefabs/AudioEffectItem");
             _audioEffect1.name = "AudioEffectItem1";
             _audioEffect1 = Instantiate(_audioEffect1, gameObject.transform);
             _audioEffect1.transform.localPosition = new Vector3(0,160,0);
             _audioEffect1.transform.localScale = new Vector3(1, 1, 1);
        }

        if (_audioEffect2 == null) {
             _audioEffect2 = Resources.Load<GameObject>("Prefabs/AudioEffectItemClone");
             _audioEffect2.name = "AudioEffectItem2";
             _audioEffect2 = Instantiate(_audioEffect2,gameObject.transform);
             _audioEffect2.transform.localPosition = new Vector3(0,-360,0);
             _audioEffect2.transform.localScale = new Vector3(1, 1, 1);
        }
        SetVoiceText();
        SetSoundId();
        SetVoiceName();
    }

    // 设置音效文案
    void SetVoiceText()
    {
        _audioEffect1.SendMessage("RenderVoiceText", "音效1", SendMessageOptions.RequireReceiver);
        _audioEffect2.SendMessage("RenderVoiceText", "音效2", SendMessageOptions.RequireReceiver);
    }

    // 设置音效ID
    void SetSoundId()
    {
        _audioEffect1.SendMessage("RenderSoundId", 1, SendMessageOptions.RequireReceiver);
        _audioEffect2.SendMessage("RenderSoundId", 2, SendMessageOptions.RequireReceiver);
    }

    // 设置音效名
    void SetVoiceName()
    {
        _audioEffect1.SendMessage("RenderVoiceName", "audio1.mp3", SendMessageOptions.RequireReceiver);
        _audioEffect2.SendMessage("RenderVoiceName", "audio2.3gp", SendMessageOptions.RequireReceiver);
    }

    // 全部停止 按钮点击事件
   void StopAllButtonClickFunc()
    {
        _engine.StopAllLocalAudioClips();
        _audioEffect1.SendMessage("StopAllFunc", SendMessageOptions.RequireReceiver);
        _audioEffect2.SendMessage("StopAllFunc", SendMessageOptions.RequireReceiver);
    }

    // 全部暂停 按钮点击事件
   void PauseAllButtonClickFunc()
    {
        _engine.PauseAllLocalAudioClips();
        _audioEffect1.SendMessage("PauseAllFunc", SendMessageOptions.RequireReceiver);
        _audioEffect2.SendMessage("PauseAllFunc", SendMessageOptions.RequireReceiver);
    }

    // 全部恢复 按钮点击事件
    void ResumeAllButtonFunc()
    {
        _engine.ResumeAllLocalAudioClips();
        _audioEffect1.SendMessage("ResumeAllFunc", SendMessageOptions.RequireReceiver);
        _audioEffect2.SendMessage("ResumeAllFunc", SendMessageOptions.RequireReceiver);
    }

    // 关闭 按钮点击事件
    void CloseButtonFunc()
    {
        EventCloseAudioEffect();
    }

    // 全部音量进度条
    void AllVoiceChangedFunc(float value) {
        voiceText.text = String.Format("音量({0})", value.ToString());
        _engine.SetLocalAudioClipsVolume((int) value);
    }
    private void CopyAudioToTemporaryCachePath()
    {
        CopyAudio("audio1.mp3");
        CopyAudio("audio2.3gp");
    }

    public  void CopyAudio(string filename)
    {
        string toLocation = Application.temporaryCachePath + "/Audio/";
        string fromLocation = Application.streamingAssetsPath + "/" + filename;
        byte[] fileData = null;
        // // 从 StreamingAssets 文件夹读取文件数据
        if (Application.platform == RuntimePlatform.Android)
        {
            using (UnityWebRequest www = UnityWebRequest.Get(fromLocation))
            {
                www.SendWebRequest();
                while (!www.isDone) { }
                fileData = www.downloadHandler.data;
            }
        }
        else
        {
            fileData = File.ReadAllBytes(fromLocation);
        }
        // 创建目标文件夹（如果不存在）
            string destinationFolder = Path.GetDirectoryName(toLocation);
            if (!Directory.Exists(destinationFolder))
            {
                Directory.CreateDirectory(destinationFolder);
            }
            // 将文件数据写入目标文件
            File.WriteAllBytes(toLocation + filename, fileData);
    }
}
