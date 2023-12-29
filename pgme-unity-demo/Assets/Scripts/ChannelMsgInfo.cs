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
using System.IO;
using System.Linq;
using System.Text;
using GMME;
using GMME.Model.Rtm.Notify;
using GMME.Model.Rtm.Req;
using GMME.Model.Rtm.Result;
using Org.BouncyCastle.Ocsp;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using TMPro.Examples;


public class ChannelMsgInfo : BaseCtr
{
    // Start is called before the first frame update
    
    // 消息功能用户名
    public Text infoUserNameText;
    
    // 已登录用户
    public Text loginUserNameText;
    
    // 订阅频道
    public Button subChannelBtn;
    
    // 取消订阅频道
    public Button unSubChannelBtn;
    
    // 发送文本消息
    public Button sendTextButton;
    
    // 发送data消息
    public Button sendDataButton;
    
    // 频道id下拉框
    public TMP_Dropdown channelDropdown;   
    
    // 消息日志
    public TMP_Text _logContentMsg;
    
    private StringBuilder _logMsgBuilder;
    
    // 频道玩家输入框
    public InputField playersInput;
    
    // 消息内容输入框
    public InputField contentInput;
    
    // 内容toggle
    public Toggle contentToggle;  
    
    // 广告toggle
    public Toggle adToggle;   
    
    // 缓存toggle
    public Toggle cacheToggle;   

    // 查询
    public Button findBtn;
    
    // 清屏
    public Button clearBtn;
    
    // 几天前输入框
    public InputField dayInput;
    
    // 数量输入框
    public InputField countInput;
    
    // 频道输入框
    public InputField channleInput;
    
    private int _selectedIndex = -1;
    
    // 订阅的频道列表
    private List<string> _channelList = new ();  
    
    // 当前频道
    private string _currentChannelId; 
    
    //几天前
    private int _dayCount = 7;
    
    private GameMediaEngine _engine;
    
    // 消息
    Dictionary<string, PublishRtmChannelMessageReq> _msgDic = new Dictionary<string, PublishRtmChannelMessageReq>();
    
    void Start()
    {
        _engine = GetEngineInstance();
        ChangeBtnArea();
        AddListenerFunc();
        InitDataFunc();
        channelDropdown.options.Clear();
        UpdateUserName();
    }
    
    void ChangeBtnArea()
    {
        PhoneAdaptive.ChangeBtnClickArea(clearBtn);
        PhoneAdaptive.ChangeBtnClickArea(findBtn);
    }
    void AddListenerFunc()
    {
        dayInput.onEndEdit.AddListener(DayInputEndEdit);
        subChannelBtn.onClick.AddListener(SubChannelClick); 
        unSubChannelBtn.onClick.AddListener(UnSubChannelClick); 
        channelDropdown.onValueChanged.AddListener(OnSwitchChannelClick);
        sendTextButton.onClick.AddListener(SendTextClick); 
        sendDataButton.onClick.AddListener(SendDataClick);
        clearBtn.onClick.AddListener(ClearClick);
        findBtn.onClick.AddListener(FindClick);
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSubscribeRtmChannelEvent += OnSubscribeRtmChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelInfoEvent += OnGetRtmChannelInfoImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelHistoryMessagesEvent += GetRtmChannelHistoryMessagesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnUnSubscribeRtmChannelEvent += OnUnSubscribeRtmChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPublishRtmChannelMessageEvent += OnPublishRtmChannelMessageImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnReceiveRtmChannelMessageEvent += OnReceiveRtmChannelMessageImpl;
    }
    
    void OnDestroy()
    {
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSubscribeRtmChannelEvent -= OnSubscribeRtmChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelInfoEvent -= OnGetRtmChannelInfoImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnGetRtmChannelHistoryMessagesEvent -= GetRtmChannelHistoryMessagesImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnUnSubscribeRtmChannelEvent -= OnUnSubscribeRtmChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPublishRtmChannelMessageEvent -= OnPublishRtmChannelMessageImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnReceiveRtmChannelMessageEvent -= OnReceiveRtmChannelMessageImpl;
    }

    void InitDataFunc()
    {
        _logMsgBuilder = new();
        dayInput.text = _dayCount.ToString();
    }

    // Update is called once per frame
    void Update()
    {
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
            loginUserNameText.text = "已登录用户名";  
        }
        else
        {
            if (channelId != _currentChannelId)
            {
                _currentChannelId = channelId;
                GetRtmChannelInfo(_currentChannelId); 
                GetRtmChannelHistoryMessages(_currentChannelId);
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
            GetRtmChannelInfo(_currentChannelId); 
            GetRtmChannelHistoryMessages(_currentChannelId);
        }
    }

    private void OnSubscribeRtmChannelImpl(SubscribeRtmChannelResult result)
    {
        string logMsg = string.Format("{0:T}", DateTime.Now) + $" 订阅频道回调channelId:{result.ChannelId}, code:{result.Code}, msg:{result.Msg}";
        AppendLogMessage(logMsg);
        if (result.Code == 0)
        {
            Loom.QueueOnMainThread(() =>
            {
                channleInput.text = "";
                SendMessageUpwards("ChangeCurrentChannel", result.ChannelId, SendMessageOptions.RequireReceiver);
            });
        }
    }

    private void OnUnSubscribeRtmChannelImpl(UnSubscribeRtmChannelResult result)
    {
        string logMsg = string.Format("{0:T}", DateTime.Now) + $" 退订频道回调channelId:{result.ChannelId}, code:{result.Code}, msg:{result.Msg}";
        AppendLogMessage(logMsg);
        if (result.Code == 0)
        {
            Loom.QueueOnMainThread(() =>
            {
                SendMessageUpwards("UnSubChannel", result.ChannelId, SendMessageOptions.RequireReceiver);
            });
        }
        Loom.QueueOnMainThread(() =>
        {
            if (_channelList.Contains(result.ChannelId))
            {
                _channelList.Remove(result.ChannelId);
            } 
            
            if (_channelList.Count > 0)
            {
                SwitchSelectedChannel(_channelList.First());
            }
            else
            {
                SwitchSelectedChannel("");
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

    private void OnPublishRtmChannelMessageImpl(PublishRtmChannelMessageResult result)
    {
        PublishRtmChannelMessageReq req;
        _msgDic.TryGetValue(result.ClientMsgId, out req);
        string logMsg;
        Loom.QueueOnMainThread(() =>
        {
            contentInput.text = "";
        });
        if (req != null)
        {
            _msgDic.Remove(result.ClientMsgId);   
            string messageType = "[二进制]";
            string messageContent = "";
            if (req.MessageType == 1)
            { 
                messageType = "[文本]";
                messageContent = req.MessageString;
            }
            else
            {
                if (req.MessageBytes != null && req.MessageBytes.Length > 0)
                {
                    messageContent = GetMessageWithContent(req.MessageBytes);   
                }
            }
            logMsg = string.Format("{0:T}", DateTime.Now) + $" 发送结果回调:clientMsgId:{result.ClientMsgId}, serverMsgId:{result.ServerMsgId}, rtnCode:{result.Code}, errorMsg:{result.Msg}, 内容:{messageType + messageContent}";
            AppendLogMessage(logMsg);
        }
    }
    
    private void OnReceiveRtmChannelMessageImpl(ReceiveRtmChannelMessageNotify notify)
    {
        if (notify != null)
        {
            string logMsg;
            string messageType = "[二进制]";
            string messageContent = "";
            if (notify.MessageType == 1)
            { 
                messageType = "[文本]";
                messageContent = notify.MessageString;
            }
            else
            {
                if (notify.MessageBytes != null && notify.MessageBytes.Length > 0)
                {
                    messageContent = GetMessageWithContent(notify.MessageBytes);   
                }
            }
            logMsg = string.Format("{0:T}", DateTime.Now) + $" channelId:{notify.ChannelId}来自{notify.SenderId}的消息:{messageType + messageContent}";
            AppendLogMessage(logMsg);
        }
    }

    private void GetRtmChannelHistoryMessagesImpl(GetRtmChannelHistoryMessagesResult result)
    {
        string logMsg = string.Format("{0:T}", DateTime.Now) + $" 获取频道历史消息回调channelId:{result.ChannelId}, code:{result.Code}, msg:{result.Msg}";
        AppendLogMessage(logMsg);
        if (result.Code == 0)
        {
            Loom.QueueOnMainThread(() =>
                {
                    if (result.ChannelMessages != null && result.ChannelMessages.Count > 0)
                    {
                        List<RtmChannelHistoryMessageNotify> newList = result.ChannelMessages;
                        newList.Reverse();
                        foreach (RtmChannelHistoryMessageNotify message in newList)
                        {
                            string messageType = "[二进制]";
                            string messageContent = "";
                            if (message.MessageType == 1)
                            { 
                                messageType = "[文本]";
                                messageContent = message.MessageString;
                            }
                            else
                            {
                                if (message.MessageBytes != null && message.MessageBytes.Length > 0)
                                {
                                    messageContent = GetMessageWithContent(message.MessageBytes);   
                                }
                            }
                            logMsg = string.Format("{0:T}", DateTime.Now) + $" [历史消息]来自频道{message.ChannelId}玩家{message.SenderId}的消息: {messageType + messageContent}";
                            AppendLogMessage(logMsg);
                        } 
                    }
                }
            );
        }
    }

    // 订阅频道
    private void SubChannelClick()
    {
        SubscribeRtmChannelReq req = new SubscribeRtmChannelReq();
        req.ChannelId = channleInput.text;
        _engine.SubscribeRtmChannel(req);
    }
    
    // 取消订阅频道
    private void UnSubChannelClick()
    {
        UnSubscribeRtmChannelReq req = new UnSubscribeRtmChannelReq();
        req.ChannelId = _currentChannelId;
        _engine.UnSubscribeRtmChannel(req);
    }
    
    private void UpdateChannelDropDownItem()
    {
        channelDropdown.captionText.text = _currentChannelId;
        var tempIdx = _channelList.IndexOf(_currentChannelId);
        channelDropdown.options.Clear();
        channelDropdown.AddOptions(_channelList);
        channelDropdown.value = tempIdx;
    }

    // 发送文本消息
    private void SendTextClick()
    {
        PublishRtmChannelMessageReq req = new PublishRtmChannelMessageReq();
        req.ChannelId = _currentChannelId;
        req.IsAllowCacheMsg = cacheToggle.isOn;
        req.IsAdsIdentify = adToggle.isOn;
        req.IsContentIdentify = contentToggle.isOn;
        if (playersInput.text.Trim() != "")
        {
            char[] charTemp = {',', '，'};
            string[] playerArr = playersInput.text.Split(charTemp);
            req.Receivers = playerArr.ToList();
        }

        req.MessageType = 1;
        req.MessageString = contentInput.text;
        string clientMsgId = _engine.PublishRtmChannelMessage(req);
        if (clientMsgId != null)
        {
            _msgDic.Add(clientMsgId,req);
        }
    }
    
    // 发送数据消息
    private void SendDataClick()
    {
        PublishRtmChannelMessageReq req = new PublishRtmChannelMessageReq();
        req.ChannelId = _currentChannelId;
        req.IsAllowCacheMsg = cacheToggle.isOn;
        req.IsAdsIdentify = adToggle.isOn;
        req.IsContentIdentify = contentToggle.isOn;
        if (playersInput.text.Trim() != "")
        {
            char[] charTemp = {',', '，'};
            string[] playerArr = playersInput.text.Split(charTemp);
            req.Receivers = playerArr.ToList();
        }
        if (contentInput.text.Trim() != "")
        {
            req.MessageBytes = System.Text.Encoding.Default.GetBytes(contentInput.text);
        }
        req.MessageType = 2;
        string clientMsgId = _engine.PublishRtmChannelMessage(req);
        if (clientMsgId != null)
        {
            _msgDic.Add(clientMsgId,req);
        }
    }
    
    // 监听频道下拉列表
    private void OnSwitchChannelClick(int index)
    {
        var switchChannel = channelDropdown.options[index].text;
        if (switchChannel == _currentChannelId)
        {
            return;
        }

        Loom.QueueOnMainThread(()=>{
            SendMessageUpwards("SwitchCurrentChannel", switchChannel, SendMessageOptions.RequireReceiver);
        });
        _currentChannelId = switchChannel;
        GetRtmChannelInfo(_currentChannelId); 
        GetRtmChannelHistoryMessages(_currentChannelId);
    }

    // 几天前输入校验
    private void DayInputEndEdit(string content)
    {
        if (content.Trim() == "")
        {
            dayInput.text = "0";
            _dayCount = 0;
        }
        long day = long.Parse(dayInput.text.ToString());
        if (day > Int32.MaxValue)
        {
            dayInput.text = "0";
            _dayCount = 0;
        }
        _dayCount = (int)day;
    }
    
    // 查询
    private void FindClick()
    {
        GetRtmChannelInfo(_currentChannelId);
    }
    
    // 清屏
    private void ClearClick()
    {
        _logMsgBuilder.Clear();
        Loom.QueueOnMainThread(() =>
        {
            _logContentMsg.text = _logMsgBuilder.ToString();
        });
    }
    
    private void GetRtmChannelHistoryMessages(string channelId)
    {
        GetRtmChannelHistoryMessagesReq req = new GetRtmChannelHistoryMessagesReq();
        req.ChannelId = channelId;
        long count = 0;
        if (countInput.text.Trim() == "")
        {
            count = 0;
        }
        else
        {
            count = long.Parse(countInput.text.ToString());
        }
        req.Count = (int)count;
        req.StartTime = GetHistoryTime();
        _engine.GetRtmChannelHistoryMessages(req);
    }

    private void GetRtmChannelInfo(string channelId)
    {
        GetRtmChannelInfoReq req = new GetRtmChannelInfoReq();
        req.ChannelId = channelId;
        req.IsReturnMembers = true;
        _engine.GetRtmChannelInfo(req);
    }

    // 更新用户名
    private void UpdateUserName()
    {
        infoUserNameText.text = "用户名 : " +  UserAppCfg.GetUserID();
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

    // 查询开始时间
    private long GetHistoryTime()
    {
        long time = 0;
        if (_dayCount == 0)
        {
            TimeSpan ts = DateTime.Now.ToUniversalTime() - new DateTime(1970, 1, 1);
            time = (long)ts.TotalMilliseconds;
        }
        else
        {
            TimeSpan ts = DateTime.Now.AddDays(-_dayCount) - new DateTime(1970, 1, 1);
            time = (long)ts.TotalMilliseconds;
        }
        
        return time;
    }
    
    private string GetMessageWithContent(byte[] orig)
    {
        if (orig != null && orig.Length > 0)
        {
            return System.Text.Encoding.Default.GetString (orig);
        }

        return "";
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
            _logContentMsg.text = _logMsgBuilder.ToString();
        });
    }
}