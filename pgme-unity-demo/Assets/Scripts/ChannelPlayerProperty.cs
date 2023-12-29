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
using GMME;
using GMME.Model.Rtm.Notify;
using GMME.Model.Rtm.Req;
using GMME.Model.Rtm.Result;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using TMPro.Examples;


public class ChannelPlayerProperty : BaseCtr
{
    // 频道id下拉框
    public TMP_Dropdown channelDropdown;   
    
    // 已登录用户
    public Text loginUserNameText;
    
    // 消息日志
    public TMP_Text logContentMsg;
    
    public RectTransform itemContent;
    
    public GameObject itemPrefab;
    
    private StringBuilder _logMsgBuilder;
    
    // 查询玩家
    public Button findPlayerBtn;
    
    // 频道玩家输入框
    public InputField playerInput;
    
    // 删除
    public Button deleteBtn;
    
    // 设置
    public Button setBtn;
    
    // 查询
    public Button findBtn;
    
    // 清屏
    public Button clearBtn;
    
    // 订阅的频道列表
    private List<string> _channelList = new ();  
    
    // 玩家属性列表
    private List<Dictionary<string, string>> _propertyDicList = new ();

    private List<string> _OpenIds = new List<string>();
    
    // 当前频道
    private string _currentChannelId;  
    
    // 属性设置
    private GameObject _rangeSetObj;
    
    private GameMediaEngine _engine;

    
   void Start()
    {
        _engine = GetEngineInstance();
        ChangeBtnArea();
        AddListenerFunc();
        InitDataFunc();
        channelDropdown.options.Clear();
    }
   
   void ChangeBtnArea()
   {
       PhoneAdaptive.ChangeBtnClickArea(clearBtn);
       PhoneAdaptive.ChangeBtnClickArea(findBtn);
   }

    void AddListenerFunc()
    {
        channelDropdown.onValueChanged.AddListener(OnSwitchChannelClick);
        clearBtn.onClick.AddListener(ClearClick);
        findBtn.onClick.AddListener(FindClick);
        setBtn.onClick.AddListener(ShowSettingProperty);
        deleteBtn.onClick.AddListener(DeleteBtnAllPropertyClick); 
        findPlayerBtn.onClick.AddListener(FindPlayerBtnPropertyClick); 
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSetRtmChannelPlayerPropertiesEvent += OnSetRtmChannelPlayerPropertiesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelPlayerPropertiesEvent += OnGetRtmChannelPlayerPropertiesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnDeleteRtmChannelPlayerPropertiesEvent += OnDeleteRtmChannelPlayerPropertiesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRtmChannelPlayerPropertiesChangedEvent += OnRtmChannelPlayerPropertiesChangedImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelInfoEvent += OnGetRtmChannelInfoImpl;
        
    }

    void OnDestroy()
    {
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSetRtmChannelPlayerPropertiesEvent -= OnSetRtmChannelPlayerPropertiesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelPlayerPropertiesEvent -= OnGetRtmChannelPlayerPropertiesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnDeleteRtmChannelPlayerPropertiesEvent -= OnDeleteRtmChannelPlayerPropertiesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRtmChannelPlayerPropertiesChangedEvent -= OnRtmChannelPlayerPropertiesChangedImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelInfoEvent -= OnGetRtmChannelInfoImpl;



    }
    
    private void UpdatePropertyList(List<Dictionary<string, string>> list)
    {
        _propertyDicList = list;
        ReloadProperyFunc();
    }

    private void SelectedChannelList(List<string> list)
    {
        _channelList = list;
    }

    private void SelectedChannel(string channelId)
    {
        if (channelId == "")
        {
            _currentChannelId = channelId;
            UpdateChannelDropDownItem();
            ReloadProperyFunc();
            loginUserNameText.text = "已登录用户名";  
        }
        else
        {
            if (channelId != _currentChannelId)
            {
                _currentChannelId = channelId;
                GetRtmChannelInfo(_currentChannelId);
                GetPropertyImp();
            } 
            UpdateChannelDropDownItem();
        } 
    }
    
    private void SwitchSelectedChannel(string channelId)
    {
        if (channelId == "")
        {
            _currentChannelId = channelId;
            UpdateChannelDropDownItem();
            ReloadProperyFunc();
            loginUserNameText.text = "已登录用户名"; 
        }
        else
        {
            if (_currentChannelId == channelId)
            {
                return;
            }
            _currentChannelId = channelId;
            UpdateChannelDropDownItem();
            GetPropertyImp();
            GetRtmChannelInfo(_currentChannelId);
        }
    }


    void InitDataFunc()
    {
        _logMsgBuilder = new();
    }

    // Update is called once per frame
    void Update()
    {
    }
    
    private void OnRtmChannelPlayerPropertiesChangedImpl(RtmChannelPlayerPropertiesNotify result)
    {
        List<Dictionary<string, string>> newList = new List<Dictionary<string, string>>();
        string logMsg = string.Format("{0:T}", DateTime.Now) + $"查询频道{result.ChannelId}玩家{result.PlayerInfo.OpenId}的属性:{DictionaryToStringData(result.PlayerInfo.PlayerProperties)}";
        AppendLogMessage(logMsg);
        
        if (result.PlayerInfo.PlayerProperties != null && result.PlayerInfo.PlayerProperties.Count > 0)
        {
            foreach (string key in result.PlayerInfo.PlayerProperties.Keys)
            {
                Dictionary<string, string> dataDic = new Dictionary<string, string>();
                string keyText = key;
                string valueText = result.PlayerInfo.PlayerProperties[key];
                dataDic.Add(keyText,valueText);
                newList.Add(dataDic);
            }
        }

        Loom.QueueOnMainThread(() =>
        {
            if (result.PlayerInfo.OpenId == UserAppCfg.GetUserID())
            {
                _propertyDicList = newList;
                ReloadProperyFunc();
                SendMessageUpwards("UpdataPlayerPropertyDicList", _propertyDicList, SendMessageOptions.RequireReceiver);
            }
        }); 
    }

    private void OnGetRtmChannelInfoImpl(GetRtmChannelInfoResult result)
    {
        string logMsg = string.Format("{0:T}", DateTime.Now) + $" 获取频道信息回调channelId:{result.ChannelId}, code:{result.Code}, msg:{result.Msg}";
        AppendLogMessage(logMsg);
        if (result.Code == 0)
        {
            List<RtmChannelMemberInfo> memberInfos = result.MemberInfos;
            UpdateLoginUserName(memberInfos);
        }
    }
    
    private void OnSetRtmChannelPlayerPropertiesImpl(SetRtmChannelPlayerPropertiesResult result)
    {
        Loom.QueueOnMainThread(() =>
        {
            string logMsg = string.Format("{0:T}", DateTime.Now) + $"设置频道{result.ChannelId}玩家{UserAppCfg.GetUserID()}属性code:{result.Code}, msg:{result.Msg}";
            AppendLogMessage(logMsg);
            if (result.ChannelId != _currentChannelId)
            {
                return;
            }
            if (result.Code == 0)
            {
                GetPropertyImp();
            }
        });
    }

    private void OnDeleteRtmChannelPlayerPropertiesImpl(DeleteRtmChannelPlayerPropertiesResult result)
    {
        Loom.QueueOnMainThread(()=>{
            string logMsg = string.Format("{0:T}", DateTime.Now) + $"删除频道{result.ChannelId}玩家{UserAppCfg.GetUserID()}属性code:{result.Code}, msg:{result.Msg}";
            AppendLogMessage(logMsg);
            if (result.ChannelId != _currentChannelId)
            {
                return;
            }
            if (result.Code == 0)
            {
                GetPropertyImp();
            }
        });
    }
    
    private void OnGetRtmChannelPlayerPropertiesImpl(GetRtmChannelPlayerPropertiesResult result)
    {
        Loom.QueueOnMainThread(() =>
        {
            string logMsg = "";
            if (result.Code == 0)
            {
                List<Dictionary<string, string>> newList = new List<Dictionary<string, string>>();
                string openId = "";
                if (result.MemberInfos == null || result.MemberInfos.Count == 0)
                {
                    _propertyDicList = newList;
                    ReloadProperyFunc();
                    Loom.QueueOnMainThread(() =>
                    {
                        SendMessageUpwards("UpdataPlayerPropertyDicList", _propertyDicList, SendMessageOptions.RequireReceiver);
                    });                          
                }
                foreach (RtmChannelMemberInfo memberInfo  in result.MemberInfos)
                {
                    logMsg = string.Format("{0:T}", DateTime.Now) + $"查询频道{result.ChannelId}玩家{memberInfo.OpenId}的属性:{DictionaryToStringData(memberInfo.PlayerProperties)}";
                
                    if (memberInfo.PlayerProperties != null && memberInfo.PlayerProperties.Count > 0)
                    {
                        openId = memberInfo.OpenId;
                        foreach (string key in memberInfo.PlayerProperties.Keys)
                        {
                            Dictionary<string, string> dataDic = new Dictionary<string, string>();
                            string keyText = key;
                            string valueText = memberInfo.PlayerProperties[key];
                            dataDic.Add(keyText,valueText);
                            newList.Add(dataDic);
                        }
                        if (openId == UserAppCfg.GetUserID())
                        {
                            _propertyDicList = newList;
                            ReloadProperyFunc();
                            Loom.QueueOnMainThread(() =>
                            {
                                SendMessageUpwards("UpdataPlayerPropertyDicList", _propertyDicList, SendMessageOptions.RequireReceiver);
                            });
                        }
                    }
                    else
                    {
                        _propertyDicList = newList;
                        ReloadProperyFunc();
                        Loom.QueueOnMainThread(() =>
                        {
                            SendMessageUpwards("UpdataPlayerPropertyDicList", _propertyDicList, SendMessageOptions.RequireReceiver);
                        });
                    }
                }
                if (result.ChannelId != _currentChannelId)
                {
                    AppendLogMessage(logMsg);
                    return;
                }
            }
            else
            {
                logMsg = string.Format("{0:T}", DateTime.Now) + $"查询频道{result.ChannelId}玩家{_OpenIds.First()}的属性code:{result.Code}, msg:{result.Msg}";
            }

            _OpenIds = new List<string>();
            playerInput.text = "";
            AppendLogMessage(logMsg);
        });
    }
    
    private void UpdatePlayerPropertyList(List<Dictionary<string, string>> list)
    {
        _propertyDicList = list;
        ReloadProperyFunc();
    }
    
    private void UpdateChannelDropDownItem()
    {
        channelDropdown.captionText.text = _currentChannelId;
        var tempIdx = _channelList.IndexOf(_currentChannelId);
        channelDropdown.options.Clear();
        channelDropdown.AddOptions(_channelList);
        channelDropdown.value = tempIdx;
    }
    
    // 监听频道下拉列表
    private void OnSwitchChannelClick(int index)
    {
        var switchChannel = channelDropdown.options[index].text;
        if (switchChannel == _currentChannelId)
        {
            return;
        }
        Loom.QueueOnMainThread(() =>
        {
            SendMessageUpwards("SwitchCurrentChannel", switchChannel, SendMessageOptions.RequireReceiver);
        });
        _currentChannelId = switchChannel;
        GetPropertyImp();
        GetRtmChannelInfo(_currentChannelId);
    }
    
    // ReSharper disable Unity.PerformanceAnalysis
    private void GetPropertyImp()
    {
        GetRtmChannelPlayerPropertiesReq req = new GetRtmChannelPlayerPropertiesReq();
        req.ChannelId = _currentChannelId;
        List<string> players = new List<string>();
        players.Add(UserAppCfg.GetUserID());
        req.OpenIds = players;
        _OpenIds = players;
        _engine.GetRtmChannelPlayerProperties(req);
    }
    
    private void GetRtmChannelInfo(string channelId)
    {
        GetRtmChannelInfoReq req = new GetRtmChannelInfoReq();
        req.ChannelId = channelId;
        req.IsReturnMembers = true;
        _engine.GetRtmChannelInfo(req);
    }
    
    
    // 查询
    private void FindClick()
    {
        GetRtmChannelInfo(_currentChannelId);
    }

    private void FindPlayerBtnPropertyClick()
    {
            GetRtmChannelPlayerPropertiesReq req = new GetRtmChannelPlayerPropertiesReq();
            req.ChannelId = _currentChannelId;
            List<string> players = new List<string>();
            if (playerInput.text.Trim() != "")
            {
                players.Add(playerInput.text);
                req.OpenIds = players;
            }
            else
            {
                return;
            }
            _OpenIds = players;
            _engine.GetRtmChannelPlayerProperties(req);
    }

    // 清屏
    private void ClearClick()
    {
        _logMsgBuilder.Clear();
        Loom.QueueOnMainThread(() =>
        {
            logContentMsg.text = _logMsgBuilder.ToString();
        });
    }
    
    private void ReloadProperyFunc()
    {
        Loom.QueueOnMainThread(() =>
        {
            foreach (Transform child in itemContent.transform)
            {
                Destroy(child.gameObject);
            }

            if (_propertyDicList.Count > 0)
            {
                for (int i = 0; i < _propertyDicList.Count; i++)
                {
                    GameObject item = Instantiate(itemPrefab, itemContent.transform);
                    item.transform.localScale = Vector3.one;
                    Dictionary<string, string> dic = _propertyDicList[i];
                    item.SendMessage("RenderItem", dic, SendMessageOptions.RequireReceiver);
                }
            }
        });
    }
   
    private void AddPropertyImpl(List<Dictionary<string, string>> list)
    {
        Dictionary<string, string> dataDic = new Dictionary<string, string>();
        foreach (Dictionary<string, string> dic in list)
        {
            
            foreach (string key in dic.Keys)
            {
                string keyText = key == null ? "" :key;
                string valueText = dic[key] == null ? "" : dic[key];
                if (!dataDic.Keys.Contains(dic.Keys.ElementAt(0)))
                {
                    dataDic.Add(keyText,valueText); 
                } 
            }
        }
        SetRtmChannelPlayerPropertiesReq req = new SetRtmChannelPlayerPropertiesReq();
        req.ChannelId = _currentChannelId;
        req.PlayerProperties = dataDic;
        _engine.SetRtmChannelPlayerProperties(req);
    }

    private void ItemDeleteFunc(string key)
    {
        DeleteRtmChannelPlayerPropertiesReq req = new DeleteRtmChannelPlayerPropertiesReq();
        req.ChannelId = _currentChannelId;
        List<string> keys = new List<string>();
        keys.Add(key);
        req.Keys = keys;
        _engine.DeleteRtmChannelPlayerProperties(req);
    }
    
    private void DeleteBtnAllPropertyClick()
    {
        List<string> keys = new List<string>();

        if (_propertyDicList.Count > 0)
        {
            for (int i = 0; i < _propertyDicList.Count; i++)
            {
                Dictionary<string, string> dic = _propertyDicList[i];
                if (dic != null && dic.Count > 0)
                {
                    keys.Add(dic.Keys.ElementAt(0)); 
                }
            }
        }
        DeleteRtmChannelPlayerPropertiesReq req = new DeleteRtmChannelPlayerPropertiesReq();
        req.ChannelId = _currentChannelId;
        req.Keys = keys;
        _engine.DeleteRtmChannelPlayerProperties(req);
    }

    private void ShowSettingProperty()
    {
        Loom.QueueOnMainThread(() =>
        {
            SendMessageUpwards("SettingPlayerProperty", SendMessageOptions.RequireReceiver);
        });
    }
    
    // 日志
    protected void AppendLogMessage(string message)
    {
        if (_logMsgBuilder.Length > 20000)
        {
            _logMsgBuilder.Clear();
        }
        Loom.QueueOnMainThread(() =>
        {
            StringBuilder stringTem = new StringBuilder();
            stringTem.Append(message).Append(Environment.NewLine);
            stringTem.Append(_logMsgBuilder);
            _logMsgBuilder = stringTem;
            logContentMsg.text = _logMsgBuilder.ToString();
        });
    }
    
    public static string DictionaryToStringData (Dictionary<string,string> dict) 
    {
        StringBuilder sb = new StringBuilder();
        foreach (var item in dict)
        {
            sb.Append("{\""+item.Key+"\":\""+item.Value+"\"}\n");
        }
        return (sb.ToString());
    }
    
    // 更新已登录的用户
    private void UpdateLoginUserName(List<RtmChannelMemberInfo> memberInfos)
    {
        Loom.QueueOnMainThread(() =>
        {
            if (memberInfos != null)
            {
               
                string loginuserNameStr = "已登录用户名：";
                string openId = null;
                foreach (RtmChannelMemberInfo info in memberInfos)
                {
                    if (openId != null)
                    {
                        loginuserNameStr =  loginuserNameStr + "," + info.OpenId;
                    }
                    else
                    {
                        loginuserNameStr = loginuserNameStr + info.OpenId;
                    }
                    openId = info.OpenId;
                }
                loginUserNameText.text = loginuserNameStr;
               
            }
            else
            {
                loginUserNameText.text = "已登录用户名";  
            }
        });
        
    }
}