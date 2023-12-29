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
using System.Collections.Generic;
using System;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using GMME;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using TMPro.Examples;


public class ChannelMsgTab : BaseCtr
{
    // Start is called before the first frame update

    // 返回按钮
    public Button backBtn;
    
    // channelMsg
    public Button channelMsgBtn;
    
    // 频道属性
    public Button channelPropertyBtn;
    
    // 频道玩家属性
    public Button playerPropertyBtn;
    
    // 频道消息信息
    public GameObject  channelInfoPrefab;
    
    
    private int _selectedIndex = -1;
    
    // 订阅的频道列表
    private List<string> _channelList = new ();  
    
    // 当前频道
    private string _currentChannelId; 
    
    // 频道属性
    public GameObject  channelPropertyPrefab;
    
    // 频道玩家属性
    public GameObject  PlayerPropertyPrefab;
    
    // 属性设置
    public GameObject  _PropertySetObj;
    
    // 频道属性列表
    private List<Dictionary<string, string>> _propertyDicList = new ();  
    
    // 玩家属性列表
    private List<Dictionary<string, string>> _playerPropertyDicList = new ();  
    
    public delegate void SendChannelMsgEventDelegate();
    
    public static SendChannelMsgEventDelegate EventCloseChannelMsg;

    void Start()
    {
        AddListenerFunc();
        ChangeBtnArea();
        SelectedButton(0);
    }
    

    void ChangeBtnArea()
    {
        PhoneAdaptive.ChangeBtnClickArea(backBtn);
        PhoneAdaptive.ChangeBtnClickArea(channelMsgBtn);
        PhoneAdaptive.ChangeBtnClickArea(channelPropertyBtn);
        PhoneAdaptive.ChangeBtnClickArea(playerPropertyBtn);
    }

    void AddListenerFunc()
    {
        backBtn.onClick.AddListener(BackFunc);
        channelMsgBtn.onClick.AddListener(ChannelMsgClick); 
        channelPropertyBtn.onClick.AddListener(ChannelPropertyClick); 
        playerPropertyBtn.onClick.AddListener(PlayerPropertyClick); 
    }
    
    void Update()
    {
    }
    
    // 修该订阅频道通知
    private void ChangeCurrentChannel(string channel)
    {
        _currentChannelId = channel;
        Loom.QueueOnMainThread(() =>
        {
            if (_currentChannelId != "" && !_channelList.Contains(channel))
            {
                _channelList.Add(channel);
                channelInfoPrefab.SendMessage("SelectedChannelList", _channelList, SendMessageOptions.RequireReceiver); 
                channelPropertyPrefab.SendMessage("SelectedChannelList", _channelList, SendMessageOptions.RequireReceiver); 
                PlayerPropertyPrefab.SendMessage("SelectedChannelList", _channelList, SendMessageOptions.RequireReceiver); 
            }
        
            channelInfoPrefab.SendMessage("SelectedChannel", (_currentChannelId == null ? "" : _currentChannelId), SendMessageOptions.RequireReceiver); 
            channelPropertyPrefab.SendMessage("SelectedChannel", (_currentChannelId == null ? "" : _currentChannelId), SendMessageOptions.RequireReceiver); 
            PlayerPropertyPrefab.SendMessage("SelectedChannel", (_currentChannelId == null ? "" : _currentChannelId), SendMessageOptions.RequireReceiver);           
        });
    }
    
    // 切换频道
    private void SwitchCurrentChannel(string channel)
    {
        _currentChannelId = channel;
    }

    private void UnSubChannel(string channel)
    {
        if (_channelList.Contains(channel))
        {
            _channelList.Remove(channel);
            Loom.QueueOnMainThread(() =>
            {
                channelPropertyPrefab.SendMessage("SelectedChannelList", _channelList, SendMessageOptions.RequireReceiver); 
                PlayerPropertyPrefab.SendMessage("SelectedChannelList", _channelList, SendMessageOptions.RequireReceiver);      
            });
        } 
        if (_channelList.Count > 0)
        {
            _currentChannelId = _channelList.First();
        }
        else
        {
            _currentChannelId = "";
        }
        _propertyDicList = new List<Dictionary<string, string>>();
        _playerPropertyDicList = new List<Dictionary<string, string>>();
        SwitchCurrentChannel(_currentChannelId);
    }

    private void SelectedButton(int index)
    {
        if (index == _selectedIndex)
        {
            return;
        }
        
        var tempPosition = channelInfoPrefab.transform.position;
        if (index == 0)
        {
            channelMsgBtn.interactable = false;
            channelPropertyBtn.interactable = true;
            playerPropertyBtn.interactable = true;
            channelInfoPrefab.SetActive(true);
            channelPropertyPrefab.SetActive(false);
            PlayerPropertyPrefab.SetActive(false);
            channelInfoPrefab.transform.position = tempPosition;
            
            Task task = Task.Factory.StartNew(async () =>
            {
                await Task.Delay(200);
                Loom.QueueOnMainThread(() =>
                {
                    channelInfoPrefab.SendMessage("SelectedChannelList", _channelList, SendMessageOptions.RequireReceiver); 
                    channelInfoPrefab.SendMessage("SelectedChannel", (_currentChannelId == null ? "" : _currentChannelId), SendMessageOptions.RequireReceiver); 
                });
            });
        }
        else if (index == 1)
        {
            channelMsgBtn.interactable = true;
            channelPropertyBtn.interactable = false;
            playerPropertyBtn.interactable = true;
            channelInfoPrefab.SetActive(false);
            channelPropertyPrefab.SetActive(true);
            PlayerPropertyPrefab.SetActive(false);
            channelPropertyPrefab.transform.position = tempPosition;
            Task task = Task.Factory.StartNew(async () =>
            {
                await Task.Delay(200);
                Loom.QueueOnMainThread(() =>
                {
                    channelPropertyPrefab.SendMessage("SelectedChannelList", _channelList,
                        SendMessageOptions.RequireReceiver);
                    channelPropertyPrefab.SendMessage("SelectedChannel", (_currentChannelId == null ? "" : _currentChannelId),
                        SendMessageOptions.RequireReceiver);
                    channelPropertyPrefab.SendMessage("UpdatePropertyList", _propertyDicList,
                        SendMessageOptions.RequireReceiver);
                });
            });
        }
        else if (index == 2)
        {
            channelMsgBtn.interactable = true;
            channelPropertyBtn.interactable = true;
            playerPropertyBtn.interactable = false;
            channelInfoPrefab.SetActive(false);
            channelPropertyPrefab.SetActive(false);
            PlayerPropertyPrefab.SetActive(true);
            PlayerPropertyPrefab.transform.position = tempPosition;
            Task task = Task.Factory.StartNew(async () =>
            {
                await Task.Delay(200);
                Loom.QueueOnMainThread(() =>
                {
                    PlayerPropertyPrefab.SendMessage("SelectedChannelList", _channelList, SendMessageOptions.RequireReceiver); 
                    PlayerPropertyPrefab.SendMessage("SelectedChannel", (_currentChannelId == null ? "" : _currentChannelId),
                        SendMessageOptions.RequireReceiver);
                    PlayerPropertyPrefab.SendMessage("UpdatePropertyList", _playerPropertyDicList, SendMessageOptions.RequireReceiver);  
                });
            });
        }
        _selectedIndex = index;
    }
    
    // 返回
    private void BackFunc()
    {
        EventCloseChannelMsg();
    }
    
    private void ChannelMsgClick()
    {
        SelectedButton(0);
    }
    
    private void ChannelPropertyClick()
    {
        SelectedButton(1);
    }
    
    private void PlayerPropertyClick()
    {
        SelectedButton(2);
    }

    void SettingProperty()
    {
        _PropertySetObj = Resources.Load<GameObject>("Prefabs/SettingProperty");
        _PropertySetObj = Instantiate(_PropertySetObj, gameObject.transform);
        _PropertySetObj.transform.localPosition = new Vector3(0,0,0);
        _PropertySetObj.transform.localScale = new Vector3(1, 1, 1);
        _PropertySetObj.SendMessage("UpdatePropertyList", _propertyDicList, SendMessageOptions.RequireReceiver);
    }
    
    void SettingPlayerProperty()
    {
        _PropertySetObj = Resources.Load<GameObject>("Prefabs/SettingProperty");
        _PropertySetObj = Instantiate(_PropertySetObj, gameObject.transform);
        _PropertySetObj.transform.localPosition = new Vector3(0,0,0);
        _PropertySetObj.transform.localScale = new Vector3(1, 1, 1);
        _PropertySetObj.SendMessage("UpdatePropertyList", _playerPropertyDicList, SendMessageOptions.RequireReceiver);
    }

    private void AddPropertyFunc(List<Dictionary<string, string>> list)
    {
        Loom.QueueOnMainThread(() =>
        {
            if (_selectedIndex == 1)
            {
                channelPropertyPrefab.SendMessage("AddPropertyImpl", list, SendMessageOptions.RequireReceiver);  
            } 
            else if (_selectedIndex == 2)
            {
                PlayerPropertyPrefab.SendMessage("AddPropertyImpl", list, SendMessageOptions.RequireReceiver);  
            }
            DestroyImmediate(_PropertySetObj);
        });
    }

    private void UpdataPropertyDicList(List<Dictionary<string, string>> list)
    {
        List<Dictionary<string, string>> newList = new List<Dictionary<string, string>>();
        foreach (var dic in list)
        {
            newList.Add(dic);
        }

        _propertyDicList = newList;
    }

    private void UpdataPlayerPropertyDicList(List<Dictionary<string, string>> list)
    {
        List<Dictionary<string, string>> newList = new List<Dictionary<string, string>>();
        foreach (var dic in list)
        {
            newList.Add(dic);
        }

        _playerPropertyDicList = newList;
    }

    private void SetPropertyCancel()
    {
        DestroyImmediate(_PropertySetObj);
    }
    
}