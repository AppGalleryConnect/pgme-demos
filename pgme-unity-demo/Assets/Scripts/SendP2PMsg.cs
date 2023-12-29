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
using System.Text;
using GMME;
using GMME.Model.Rtm.Req;
using GMME.Model.Rtm.Result;
using Org.BouncyCastle.Asn1.Cmp;
using UnityEngine;
using UnityEngine.UI;
using TMPro;


public class SendP2PMsg : BaseCtr
{
    private GameMediaEngine _engine;
    
    // 返回按钮
    public Button backBtn;
    
    // 用户名
    public Text userNameText;
    
    // 消息接收者
    public InputField playerInput;  
    
    // 消息内容输入
    public InputField contentInput;  
    
    // 发送p2p消息
    public Button sendTextButton;
    
    // 发送二进制消息
    public Button sendDataButton;
    
    // 日志
    public TMP_Text _logContentMsg;
    
    private StringBuilder _logMsgBuilder;
    
    // 消息
    Dictionary<string, PublishRtmPeerMessageReq> _msgDic = new Dictionary<string, PublishRtmPeerMessageReq>();
    
    public delegate void SendP2PMsgEventDelegate();
    
    public static SendP2PMsgEventDelegate EventCloseP2PMsg;

    void Start()
    {
        _engine = GetEngineInstance();
        _logMsgBuilder = new();
        AddListenerFunc();
        ChangeBtnArea();
        UpdateUserName();
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPublishRtmPeerMessageEvent += PublishRtmPeerMessageImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnReceiveRtmPeerMessageEvent +=
            ReceiveRtmPeerMessageImpl;
    }

    void OnDestroy()
    {
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPublishRtmPeerMessageEvent -= PublishRtmPeerMessageImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnReceiveRtmPeerMessageEvent -=
            ReceiveRtmPeerMessageImpl;
    }

    void ChangeBtnArea()
    {
        PhoneAdaptive.ChangeBtnClickArea(backBtn);
    }

    void AddListenerFunc()
    {
        backBtn.onClick.AddListener(BackFunc);
        sendTextButton.onClick.AddListener(SendTextClick);
        sendDataButton.onClick.AddListener(SendDataClick);
    }
    
    void Update()
    {
    }

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

  
    private void SendTextClick()
    {
        string receiverName = playerInput.text;
        string content = contentInput.text;
        PublishRtmPeerMessageReq req = new PublishRtmPeerMessageReq();
        req.PeerId = receiverName;
        req.MessageType = 1;
        req.MessageString = content;
       string clientMsgId = _engine.PublishRtmPeerMessage(req);
       if (clientMsgId != null)
       {
           _msgDic.Add(clientMsgId,req);
       }
    }

    private void SendDataClick()
    {
        string receiverName = playerInput.text;
        PublishRtmPeerMessageReq req = new PublishRtmPeerMessageReq();
        req.PeerId = receiverName;
        req.MessageType = 2;
        if (contentInput.text.Trim() != "")
        {
            req.MessageBytes = System.Text.Encoding.Default.GetBytes(contentInput.text);
        }
        string clientMsgId = _engine.PublishRtmPeerMessage(req);
        if (clientMsgId != null)
        {
            _msgDic.Add(clientMsgId,req);
        }
    }

    private void PublishRtmPeerMessageImpl(PublishRtmPeerMessageResult result)
    {
        MessageLog(result);
        Loom.QueueOnMainThread(() =>
        {
            contentInput.text = "";
        });
    }

    private void ReceiveRtmPeerMessageImpl(ReceiveRtmPeerMessageNotify notify)
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
            logMsg = string.Format("{0:T}", DateTime.Now) + $" 来自{notify.SenderId}的消息:{messageType + messageContent}";
            AppendLogMessage(logMsg);
        }
    }

    private void MessageLog(PublishRtmPeerMessageResult result)
    {
        PublishRtmPeerMessageReq req;
        _msgDic.TryGetValue(result.ClientMsgId, out req);
        string logMsg;
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
    
    private string GetMessageWithContent(byte[] orig)
    {
        if (orig != null && orig.Length > 0)
        {
            return System.Text.Encoding.Default.GetString (orig);
        }

        return "";
    }

    private void UpdateUserName()
    {
        userNameText.text = "用户名 : " +  UserAppCfg.GetUserID();
    }

    // 返回
    void BackFunc()
    {
        EventCloseP2PMsg();
    }
}