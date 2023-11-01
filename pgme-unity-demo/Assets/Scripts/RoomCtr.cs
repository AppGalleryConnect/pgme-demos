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
using System.Collections.Generic;
using GMME;
using System;
using System.Linq;
public class RoomCtr : BaseCtr
{
    private const int IntervalPeriod = 1000;
    
    private string _userId;           // 当前玩家id
    private string _currentRoomId;    // 当前房间id
    private string _currentOwnerId;   // 当前房间房主id
    private string _displayRoomId;    // roomIds下拉列表当前显示的房间id
    private int _roomStatus;          // 房间状态 -1：空，0：正常， 1：禁言
    private int _roleType;            // 国战房间角色类型，默认是指挥家

    private IGMMERoomType _roomType;  // 加入房间成功后类型
    private IGMMERoomType _joinRoomType;  // 标记加入房间选择的类型
    private List<string> _roomIdsList = new ();      // 玩家所在的房间列表
    private Dictionary<string, Dictionary<string, bool>> _playerMuteStatusDic = new();  // 所有房间中玩家屏蔽状态
    private Dictionary<string, bool> _roomMuteStatusDic = new();  // 所有房间屏蔽状态
    private List<RoomMember> _roomMembers = new();   // 当前房间玩家列表
    private List<Toggle> _micToggleList = new();     // 当前房间玩家列表麦克风Toggles
    private List<Toggle> _audioToggleList = new();   // 当前房间玩家列表小喇叭Toggles

    // 组件对象
    private GameObject _roleSelectObject;  // 角色选择面板
    private GameObject _transferOwnerObj;  // 转移房主面板
    private GameObject _voiceToTextObj;    // 语音转文本面板
    private GameObject _imChatFrame;        // IM聊天框
    public TMP_Dropdown roomIdsDropdown;   // 房间id下拉框
    public TMP_InputField inputField;      // 房间id输入框
    public Button joinBtn;                 // 加入语音房间按钮
    public Button leaveBtn;                // 离开语音房间按钮
    public Button switchBtn;               // 切换成员或消息窗口按钮
    public Button destroyBtn;              // 引擎销毁按钮
    public Button audioMsgBtn;              // 引擎销毁按钮
    public Button audioEffectBtn;           //媒体音效按钮
    public Button playerPostionBtn;           //玩家位置按钮
    public Button SpatialAudioBtn;             // 切换房主按钮
    public Button clearBtn;                // 清空日志按钮
    public Button transferBtn;             // 切换房主按钮
    public Button sendBtn;                 // 发送消息按钮/（当前如果是群聊时，此按钮为创建/加入频道按钮）
    public Button leaveChannelBtn;         // 离开频道按钮
    public Button voiceToTextBtn;
    public Toggle micToggle;               // 语音通道Toggle
    public Toggle imToggle;                // IM通道Toggle
    public Text roleTypeLabel;             // 角色类型Label
    public Toggle[] roomTypeToggles;       // 房间类型Toggles
    public Toggle[] imTypeToggles;         // IM类型Toggles
    public GameObject callbackMsgObj;      // 日志面板
    public GameObject memberListObj;       // 成员面板
    public Toggle localMicToggle;          // 本地开麦toggle
    public Toggle micAllToggle;            // 禁言所有人toggle
    public Toggle audioAllToggle;          // 屏蔽所有人toggle
    public TMP_Text membersTitle;          // 小队成员/国战成员
    private bool _spatialSoundEnable = false;  // 3D 音效是否能启用
    private bool _micIsOn = true;          // 默认开启语音通道
    private bool _imIsOn;                  // 默认关闭消息通道
    
    private bool _roomMemberChanged;       // 房间成员或成员关系发生改变标识
    private bool _curShowMsgWindow;        // 标识当前展示的是成员窗口还是日志窗口

    private bool _curSelectGroupChannel;   // 标识当前是否是选择群聊
    private string _curInputId;            // 当前输入框中输入的id（未进行创建或加入操作）
    private string _curChannelId;          // 当前群聊ID
    private string _chatReceiveId;         // 私聊玩家ID
    private string _needSendContent;       // 需要发送的消息

    private TMP_InputField _chatSendInput; // 聊天窗口发送消息输入框
    public GameObject msgObj;
    public Button curChannelBtn;
    private GameObject  _audioEffectObject;
    private GameObject _playerPositionObject;  // 玩家位置面板
 
    public void Start()
    {
        // 初始化默认国战角色类型和默认房间类型
        _roleType = Constant.JOINER;
        _roomType = IGMMERoomType.IGMME_ROOM_TYPE_TEAM;
        _joinRoomType = _roomType;
        //添加点击侦听
        joinBtn.onClick.AddListener(OnJoinRoomClick);
        joinBtn.interactable = false;
        leaveBtn.onClick.AddListener(OnLeaveRoomClick);
        leaveBtn.interactable = false;

        voiceToTextBtn.onClick.AddListener(OnVoiceToTextClick);
        switchBtn.onClick.AddListener(OnSwitchBtnClick);
        PhoneAdaptive.ChangeBtnClickArea(switchBtn);
        clearBtn.onClick.AddListener(OnClearBtClick);
        destroyBtn.onClick.AddListener(OnDestroyClick);
        audioMsgBtn.onClick.AddListener(OnAudioMsgClick);
        audioEffectBtn.onClick.AddListener(OnAudioEffectClick);
        playerPostionBtn.onClick.AddListener(PlayerPositionClick);
        PhoneAdaptive.ChangeBtnClickArea(playerPostionBtn);
        micToggle.isOn = _micIsOn;
        imToggle.isOn = _imIsOn;
        micToggle.onValueChanged.AddListener(MicToggleOnValueChanged);
        imToggle.onValueChanged.AddListener(IMToggleOnValueChanged);
        inputField.onValueChanged.AddListener(InputOnValueChanged);
        PhoneAdaptive.ChangeBtnClickArea(SpatialAudioBtn);
        _curChannelId = "";
        _chatReceiveId = "";
        _needSendContent = "";
        leaveChannelBtn.onClick.AddListener(OnLeaveChannelBtnClick);
        curChannelBtn.onClick.AddListener(OnCurChannelBtnClick);
        curChannelBtn.interactable = false;

        if (micToggle.isOn)
        {
            foreach (var roomTypeToggle in roomTypeToggles)
            {
                roomTypeToggle.interactable = true;
                roomTypeToggle.onValueChanged.AddListener(value => RoomTypeSelect(roomTypeToggle));
            }
        }
        if (imToggle.isOn)
        {
            foreach (var imTypeToggle in imTypeToggles)
            {
                imTypeToggle.interactable = true;
                imTypeToggle.onValueChanged.AddListener(value => RoomTypeSelect(imTypeToggle));
            }
        }
        else
        {
            foreach (var imTypeToggle in imTypeToggles)
            {
                imTypeToggle.interactable = false;
            }
        }
        _displayRoomId = inputField.text.Trim();
        _currentRoomId = inputField.text.Trim();
        roomIdsDropdown.onValueChanged.AddListener(OnSwitchRoomClick);
        roomIdsDropdown.options.Clear();
        // 默认隐藏全局禁言和全局屏蔽
        membersTitle.gameObject.SetActive(false);
        micAllToggle.gameObject.SetActive(false);
        audioAllToggle.gameObject.SetActive(false);
        SpatialAudioBtn.gameObject.SetActive(false);
        // 设置事件委托实现
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnDestroyEngineCompleteEvent += OnDestroyEngineImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinTeamRoomCompleteEvent += JoinRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnLeaveRoomCompleteEvent += LeaveRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSwitchRoomCompleteEvent += SwitchRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPlayerOnlineCompleteEvent += PlayerOnlineImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPlayerOfflineCompleteEvent += PlayerOfflineImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSpeakersDetectionExCompleteEvent += OnSpeakersDetectionExImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnMuteAllPlayersCompleteEvent += MuteAllPlayersCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnMutePlayerCompleteEvent += MutePlayerCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnForbidAllPlayersCompleteEvent += ForbidAllPlayersCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnForbidPlayerCompleteEvent += ForbidPlayerCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnForbiddenByOwnerCompleteEvent += ForbiddenByOwnerCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinNationalRoomCompleteEvent += JoinNationalRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinRangeRoomCompleteEvent += JoinRangeRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnTransferOwnerCompleteEvent += TransferOwnerImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRemoteMicroStateChangedCompleteEvent += OnRemoteMicroStateChangedImpl;
        
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinChannelCompleteEvent += JoinChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnLeaveChannelCompleteEvent += LeaveChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSendMsgCompleteEvent += SendMsgImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRecvMsgCompleteEvent += ReceiveMsgImpl;
        AudioEffect.EventCloseAudioEffect += CloseAudioEffectFunc;
        PlayerPositionInfo.EventClosePlayerPosition += ClosePlayerPositionFunc;
        OnLocalMicToggleClick(false);
        SetLocalMicToggleListener(true);
        InitSpatialSound();
        Debug.Log("RoomCtr start");
        AppendCallbackMessage("RoomCtr start !");
        ChangeButtonClickArea();
        UpdateSelfPosition();
    }

    private void OnCurChannelBtnClick()
    {
        if (_imChatFrame)
        {
            _imChatFrame.SetActive(true);
        }
    }
    
    private void OnVoiceToTextClick()
    {
        Debug.Log("click on BtnVoice2Text!");
        if (_voiceToTextObj == null)
        {
            _voiceToTextObj = Resources.Load<GameObject>("Prefabs/VoiceToText");
            _voiceToTextObj = Instantiate(_voiceToTextObj, gameObject.transform);
            _voiceToTextObj.transform.localScale = Vector3.one;
            _voiceToTextObj.transform.localPosition = new Vector3(0, -1000, 0);
        }
    }
    
    /**
     * 监听输入框变化
     */
    private void InputOnValueChanged(string content)
    {
        if (micToggle.isOn)
        {
            joinBtn.interactable = !content.Trim().Equals("");
        }

        if (imToggle.isOn && _curSelectGroupChannel)
        {
            sendBtn.interactable = !content.Trim().Equals("");
        }
    }

    /**
     * 监听语音通道是否选中
     */
    private void MicToggleOnValueChanged(bool isOn)
    {
        _micIsOn = isOn;
        if (_micIsOn)
        {
            foreach (var roomTypeToggle in roomTypeToggles)
            {
                roomTypeToggle.interactable = true;
                roomTypeToggle.onValueChanged.AddListener(value => RoomTypeSelect(roomTypeToggle));
            }
        }
        else
        {
            foreach (var roomTypeToggle in roomTypeToggles)
            {
                roomTypeToggle.onValueChanged.RemoveListener(value => RoomTypeSelect(roomTypeToggle));
                roomTypeToggle.interactable = false;
            }
        }
        joinBtn.interactable = isOn && !inputField.text.Trim().Equals("");
        leaveBtn.interactable = !(_userId == null || _currentRoomId.Equals(""));
    }

    /**
     * 监听加入国战房间时的身份选择
     */
    private void RoomTypeSelect(Toggle toggle)
    {
        if (toggle.isOn)
        {
            switch (toggle.name)
            {
                case "ToggleTeam":
                    _roomType = IGMMERoomType.IGMME_ROOM_TYPE_TEAM;
                    _joinRoomType = _roomType;
                    break;
                case "ToggleNational":
                    _roomType = IGMMERoomType.IGMME_ROOM_TYPE_NATIONAL;
                    _joinRoomType = _roomType;
                    if (_roleSelectObject == null)
                    {
                        _roleSelectObject = Resources.Load<GameObject>("Prefabs/RoleSelect");
                        _roleSelectObject = Instantiate(_roleSelectObject, gameObject.transform);
                        _roleSelectObject.transform.localPosition = new Vector3(0, 0, 0);
                        _roleSelectObject.transform.localScale = new Vector3(1.6f, 1.6f, 1);
                        
                        Button confirmBtn = _roleSelectObject.transform.Find("Confirm").GetComponent<Button>();
                        confirmBtn.onClick.AddListener(OnConfirm);
                        Button cancelBtn = _roleSelectObject.transform.Find("Cancel").GetComponent<Button>();
                        cancelBtn.onClick.AddListener(() => OnCancel(_roleSelectObject));
                    }
                    break;    
                case "ToggleRange":
                    _roomType = IGMMERoomType.IGMME_ROOM_TYPE_RANGE;
                    _joinRoomType = _roomType;
                    break;
            }
        }
        else
        {
            if (toggle.name == "ToggleNational")
            {
                roleTypeLabel.text = "国战";
            }
        }
    }
    
    /**
     * 监听IM通道是否选中
     */
    private void IMToggleOnValueChanged(bool isOn)
    {
        _imIsOn = isOn;
        if (_imIsOn)
        {
            foreach (var toggle in imTypeToggles)
            {
                toggle.interactable = true;
                // 首次选中主动触发一次
                ImTypeSelect(toggle);
                toggle.onValueChanged.AddListener(value => ImTypeSelect(toggle));
            }
        }
        else
        {
            foreach (var toggle in imTypeToggles)
            {
                toggle.onValueChanged.RemoveAllListeners();
                toggle.interactable = false;
            }
        }
        sendBtn.interactable = isOn &&  (!_curSelectGroupChannel || (_curSelectGroupChannel && !inputField.text.Trim().Equals("")));
        leaveChannelBtn.interactable = !_curChannelId.Equals("");
    }
    
    
    /**
     * 监听IM私聊或群聊的选择
     */
    private void ImTypeSelect(Toggle toggle)
    {
        if (toggle.isOn)
        {
            switch (toggle.name)
            {
                case "Toggle1V1":
                    AppendCallbackMessage("私聊");
                    _curSelectGroupChannel = false;
                    sendBtn.interactable = true;
                    leaveChannelBtn.interactable = false;
                    sendBtn.GetComponentInChildren<TMP_Text>().text = "发送消息";
                    sendBtn.onClick.RemoveAllListeners();
                    sendBtn.onClick.AddListener(OnPrivateChatBtnClick);
                    break;
                case "ToggleChannel":
                    AppendCallbackMessage("群聊");
                    _curSelectGroupChannel = true;
                    sendBtn.interactable = true;
                    leaveChannelBtn.interactable = false;
                    sendBtn.GetComponentInChildren<TMP_Text>().text = "创建/加入频道";
                    sendBtn.interactable = !inputField.text.Trim().Equals("");
                    sendBtn.onClick.RemoveAllListeners();
                    sendBtn.onClick.AddListener(OnCreateOrJoinChannelBtnClick);
                    break;    
            }
        }
        else
        {
            sendBtn.interactable = false;
            leaveChannelBtn.interactable = false;
        }
    }

    private void InitSpatialSound()
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        _spatialSoundEnable = engine.InitSpatialSound() == 0 ? true : false;
    }

    private void OnPrivateChatBtnClick()
    {
        if (_imChatFrame)
        {
            Destroy(_imChatFrame);
        }
        _imChatFrame = Resources.Load<GameObject>("Prefabs/ChatFrame");
        _imChatFrame = Instantiate(_imChatFrame, gameObject.transform);
        _imChatFrame.transform.Find("MaskBtn").gameObject.GetComponent<Button>().onClick.AddListener(OnMaskBtnClick);
        _imChatFrame.transform.localPosition = new Vector3(0, -700, 0);
            
        _imChatFrame.transform.Find("ChannelId").gameObject.SetActive(false);
        _imChatFrame.transform.Find("SendTo").gameObject.SetActive(true);
        _imChatFrame.transform.Find("InputReceiveId").gameObject.SetActive(true);
        
        _imChatFrame.transform.Find("InputReceiveId").gameObject.GetComponent<TMP_InputField>().onValueChanged.AddListener(ReceiveIdInputValueChanged);
        _chatSendInput = _imChatFrame.transform.Find("InputSendInfo").gameObject.GetComponent<TMP_InputField>();
        _chatSendInput.onValueChanged.AddListener(ChatInputValueChanged);
        _imChatFrame.transform.Find("BtnSend").gameObject.GetComponent<Button>().onClick.AddListener(OnSendMsgBtnClick);
        
        _imChatFrame.SetActive(true);
    }

    private void OnMaskBtnClick()
    {
        Debug.Log("OnMaskBtnClick");
        if (_curChannelId.Equals(""))
        {
            DestroyImmediate(_imChatFrame);
        }
        else
        {
            _imChatFrame.SetActive(false);
        }
    }
    
    
    private void OnSendMsgBtnClick()
    {
        Debug.Log("into OnSendMsgBtnClick");
        if(_needSendContent.Equals("")) return;
        Debug.Log("into OnSendMsgBtnClick 1");

        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        var receiveId = "";

        if (_curSelectGroupChannel)
        {
            receiveId = _curChannelId;
        }
        else
        {
            if(_chatReceiveId.Equals("")) return;
            receiveId = _chatReceiveId;
        }

        var chatType = _curSelectGroupChannel ? ChatType.ChannelChat : ChatType.PrivateChat;
        engine.SendTextMsg(receiveId, (int)chatType, _needSendContent);
        _chatSendInput.text = "";
        
        AppendCallbackMessage($"OnSendMsgBtnClick. receiveId={receiveId}, chatType={chatType}, msg={_needSendContent}");
    }
    
    private void ReceiveIdInputValueChanged(string content)
    {
        _chatReceiveId = content.Trim();
    }
    
    private void ChatInputValueChanged(string content)
    {
        _needSendContent = content.Trim();
    }
    
    private void OnCreateOrJoinChannelBtnClick()
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        var tempChannelId = inputField.text.Trim();
        if (_curChannelId.Equals(tempChannelId))
        {
            inputField.text = "";
            _imChatFrame.SetActive(true);
        }
        else
        {
            engine.JoinGroupChannel(tempChannelId);
            AppendCallbackMessage("invoke JoinGroupChannel success");
        }
    }

    private void OnLeaveChannelBtnClick()
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        engine.LeaveChannel(_curChannelId);
        AppendCallbackMessage("invoke LeaveChannel success");
    }

    private void OnConfirm()
    {
        ToggleGroup toggleGroup = _roleSelectObject.transform.Find("RoleToggleGroup").GetComponent<ToggleGroup>();

        Toggle toggle = toggleGroup.GetFirstActiveToggle();

        if (toggle == null)
        {
            Debug.Log("no active toggle");
            return;
        }

        switch (toggle.name)
        {
            case "ToggleCommander":
                _roleType = Constant.JOINER;
                roleTypeLabel.text = "指挥家";
                break;
            case "ToggleMasses":
                _roleType = Constant.PLAYER;
                roleTypeLabel.text = "群众";
                break;
        }
        Debug.LogFormat("OnConfirm. _roleType is {0}", _roleType);
        DestroyImmediate(_roleSelectObject);
    }

    private void OnCancel(GameObject obj)
    {
        roleTypeLabel.text = _roleType == Constant.JOINER ? "指挥家" : "群众";
        DestroyImmediate(obj);
    }

    private void OnDestroyClick()
    {
        Debug.Log("click OnDestroyClick !");

        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        GameMediaEngine.Destroy();
        Debug.Log("invoke DestroyEngine success");
    }

    private void OnAudioMsgClick()
    {
        Debug.Log("click on AudioMsgClick !");
        GameObject obj = Resources.Load<GameObject>("Prefabs/AudioMsgScene");
        Instantiate(obj);
    }

    private void OnAudioEffectClick()
    {
        if (_audioEffectObject == null) {
            _audioEffectObject = Resources.Load<GameObject>("Prefabs/AudioEffect");
            _audioEffectObject = Instantiate(_audioEffectObject,gameObject.transform);
            _audioEffectObject.transform.localPosition = new Vector3(0,0,0);
            _audioEffectObject.transform.localScale = new Vector3(1, 1, 1);
        } else {
            _audioEffectObject.SetActive(!_audioEffectObject.active);
        }
    }

    private void PlayerPositionClick()
    {
        Debug.Log("PlayerPositionClick");
        if (_playerPositionObject == null)
        {
            _playerPositionObject = Resources.Load<GameObject>("Prefabs/PlayerPosition");
            _playerPositionObject = Instantiate(_playerPositionObject,gameObject.transform);
            _playerPositionObject.transform.localPosition = new Vector3(0,0,0);
            _playerPositionObject.transform.localScale = new Vector3(1, 1, 1);
        } else {
            _playerPositionObject.SetActive(!_playerPositionObject.active);
        }
    }
    
    private void OnDestroyEngineImpl(int code, string msg)
    {
        Debug.LogFormat("OnDestroyEngineImpl. code={0}, msg={1}", code, msg);

        if (code == 0)
        {
            string message = "destroy engine success!";
            AppendCallbackMessage(message);
            ClearAllData();
            GMMEEnginHolder.GetInstance().setIGMMEEngine(null);

            // 引擎销毁成功切换到初始化场景
            Loom.QueueOnMainThread(() =>
            {
                GameObject obj = Resources.Load<GameObject>("Prefabs/InitEngineScene");
                Instantiate(obj);
                DestroyImmediate(gameObject);
            });
        }
        else
        {
            AppendCallbackMessage("destroy engine error! code : " + code + ", message : " + msg);
        }
    }

    private void ClearAllData()
    {
        _roomIdsList.Clear();
        _roomMembers.Clear();
        _playerMuteStatusDic.Clear();
        _roomMuteStatusDic.Clear();
    }

    private void OnJoinRoomClick()
    {
        _userId = UserAppCfg.GetUserID();
        var roomId = inputField.text.Trim();
        Debug.Log("_userId: " + _userId + ", roomId: " + roomId);
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        if (_joinRoomType == IGMMERoomType.IGMME_ROOM_TYPE_TEAM)
        {
            engine.JoinTeamRoom(roomId);
            Debug.Log("invoke JoinTeamRoom success");
        } 
        else if (_joinRoomType == IGMMERoomType.IGMME_ROOM_TYPE_RANGE)
        {
            engine.JoinRangeRoom(roomId);
            Debug.Log("invoke joinRangeRoom success");
        }
        else
        {
            engine.JoinNationalRoom(roomId, _roleType);
            Debug.Log("invoke JoinNationalRoom success");
        }
    }

    private void JoinRoomImpl(string roomId, int code, string msg)
    {
        Debug.LogFormat("JoinRoomImpl. roomId={0}, code={1}, msg={2}", roomId, code, msg);
        
        if (code == 0)
        {
            // 加入房间成功,开始接收房间回调
            GameMediaEngine engine = GetEngineInstance();
            if (engine == null)
            {
                return;
            }
            
            engine.EnableSpeakersDetection(roomId, IntervalPeriod);
            Debug.LogFormat("EnableSpeakersDetection. roomId={0}, IntervalPeriod={1}ms", roomId, IntervalPeriod);

            _roomIdsList.Add(roomId);
            _currentRoomId = roomId;
            
            

            // 加入房间成功后获取房间信息
            GetRoomMembers(roomId);
            _roomMemberChanged = true;
            
            AppendCallbackMessage("join room success! roomId : " + roomId);
            Loom.QueueOnMainThread(() =>
            {
                leaveBtn.interactable = true;
                inputField.text = "";
            });
            
        }
        else
        {
            AppendCallbackMessage("join room failure! roomId: " + roomId + ", code: " + code + ", msg: " + msg);
        }
    }
    
    private void JoinNationalRoomImpl(string roomId, int code, string msg)
    {
        Debug.LogFormat("JoinNationalRoomImpl. roomId={0}, code={1}, msg={2}", roomId, code, msg);
        if (code == 0)
        {
            // 加入国战房间成功,开始接收房间回调
            GameMediaEngine engine = GetEngineInstance();
            if (engine == null)
            {
                return;
            }
            engine.EnableSpeakersDetection(roomId, IntervalPeriod);
            Debug.LogFormat("EnableSpeakersDetection. roomId={0}, INTERVAL_PERIOD={1}ms", roomId, IntervalPeriod);

            _roomIdsList.Add(roomId);
            _currentRoomId = roomId;

            // 加入房间成功后获取房间信息
            GetRoomMembers(roomId);
            _roomMemberChanged = true;
            AppendCallbackMessage("join national room success! roomId : " + roomId);
            
            Loom.QueueOnMainThread(() =>
            {
                leaveBtn.interactable = true;
                inputField.text = "";
            });
        }
        else
        {
            AppendCallbackMessage("join national room failure! roomId: " + roomId + ", code: " + code + ", msg: " + msg);
        }
    }
    
    private void JoinRangeRoomImpl(string roomId, int code, string msg)
    {
        Debug.LogFormat("JoinRangeRoomImpl. roomId={0}, code={1}, msg={2}", roomId, code, msg);
        
        if (code == 0)
        {
            // 加入房间成功,开始接收房间回调
            GameMediaEngine engine = GetEngineInstance();
            if (engine == null)
            {
                return;
            }
            
            engine.EnableSpeakersDetection(roomId, IntervalPeriod);
            Debug.LogFormat("EnableSpeakersDetection. roomId={0}, IntervalPeriod={1}ms", roomId, IntervalPeriod);
        
            _roomIdsList.Add(roomId);
            _currentRoomId = roomId;
            
            // 加入房间成功后获取房间信息
            GetRoomMembers(roomId);
            _roomMemberChanged = true;
            
            AppendCallbackMessage("joinRange room success! roomId : " + roomId);
            Loom.QueueOnMainThread(() =>
            {
                leaveBtn.interactable = true;
                inputField.text = "";
            });
            
        }
        else
        {
            AppendCallbackMessage("join RangeRoom failure! roomId: " + roomId + ", code: " + code + ", msg: " + msg);
        }
    }

    private void PlayerOnlineImpl(string roomId, string openId)
    {
        Debug.LogFormat("PlayerOnlineImpl. roomId={0}, openId={1}", roomId, openId);
        // 非本房间成员上线，直接退出
        if (!_currentRoomId.Equals(roomId))
        {
            Debug.LogFormat("PlayerOnlineImpl. roomId={0} can't match currentRoomId={1}, exit.", roomId, _currentRoomId);
            return;
        }

        // 有成员上线时，可能玩家禁言状态或者房间状态已经更新了（不知道新上线玩家的禁言状态),需要重新获取下room信息，页面UI需要刷新
        GetRoomMembers(roomId);
        _roomMemberChanged = true;
        Debug.LogFormat("handle player join room. roomId={0}, openId={1}", roomId, openId);
        string message = "handle player join room. roomId:" + roomId + ", openId:" + openId;
        AppendCallbackMessage(message);
    }

    private void PlayerOfflineImpl(string roomId, string openId)
    {
        Debug.LogFormat("PlayerOfflineImpl. roomId={0}, openId={1}", roomId, openId);
        // 非本房间成员下线，直接退出
        if (!_currentRoomId.Equals(roomId))
        {
            Debug.LogFormat("PlayerOnlineImpl. roomId={0} can't match currentRoomId={1}, exit.", roomId, _currentRoomId);
            return;
        }

        // 成员下线时清除本地保存的该成员屏蔽状态
        if (_playerMuteStatusDic.TryGetValue(roomId, out Dictionary<string, bool> muteOldStatus))
        {
            muteOldStatus.Remove(openId);
            _playerMuteStatusDic.Remove(roomId);
            _playerMuteStatusDic.Add(roomId, muteOldStatus);
        }
        _roomMuteStatusDic.Remove(roomId);
        // 有成员下线时，需要重新获取下room信息，可能房主易主了，页面UI需要刷新
        GetRoomMembers(roomId);
        _roomMemberChanged = true;
        Debug.LogFormat("handle player leave room. roomId={0}, openId={1}", roomId, openId);
        string message = "handle player leave room. roomId:" + roomId + ", openId:" + openId;
        AppendCallbackMessage(message);
    }

    private void OnSpeakersDetectionExImpl(List<VolumeInfo> userVolumeInfos)
    {
        Debug.LogFormat("OnSpeakersDetectionExImpl. count={0}", userVolumeInfos.Count);
        foreach (VolumeInfo volumeInfo in userVolumeInfos)
        {
            Debug.LogFormat("handle OnSpeakersDetectionExImpl. openId={0}, volume={1}", volumeInfo.OpenId, volumeInfo.Volume);
        }
    }

    private void OnLeaveRoomClick()
    {
        AppendCallbackMessage("click leave room button! currentRoomId:" + _currentRoomId);

        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        if (_userId == null || _currentRoomId == "")
        {
            Debug.Log("Please join the room first");
            return;
        }

        List<string> openIds = new List<string>();
        bool isShowTransferOwnerUI = false;
        if (_roomType != IGMMERoomType.IGMME_ROOM_TYPE_RANGE)
        {
            if (_userId == _currentOwnerId && _roomMembers.Count > 1)
            {
                foreach (var roomMember in _roomMembers)
                {
                    if (roomMember.OpenId != _currentOwnerId)
                    {
                        openIds.Add(roomMember.OpenId);
                    }
                }
                isShowTransferOwnerUI = true;
            } 
        }
        
        if (isShowTransferOwnerUI)
        {
            LoadTransferOwnerResource(openIds, true);
        }
        else
        {
            engine.LeaveRoom(_currentRoomId, null);
            Debug.Log("invoke LeaveRoom success");
            AppendCallbackMessage("invoke LeaveRoom success");
        }
    }
    
    private void LoadTransferOwnerResource(List<string> openIds, bool isForLeaveTransfer)
    {
        if (_transferOwnerObj == null)
        {
            _transferOwnerObj = Resources.Load<GameObject>("Prefabs/TransferOwner");
            _transferOwnerObj = Instantiate(_transferOwnerObj, gameObject.transform);
            _transferOwnerObj.transform.localScale = Vector3.one;

            TMP_Text tipsContent = _transferOwnerObj.transform.Find("TitleTips").GetComponent<TMP_Text>();
            tipsContent.text = isForLeaveTransfer ? "离开房间指定新房主" : "指定新房主";

            Transform openIdGroupObj = _transferOwnerObj.transform.Find("OpenIdToggleGroup");
            ToggleGroup toggleGroup = openIdGroupObj.GetComponent<ToggleGroup>();

            // 动态添加toggle组件  最多添加5个，需要限制,否则显示有问题
            int maxLen = openIds.Count > 5 ? 5 : openIds.Count;
            for (int i = 0; i < maxLen; i++)
            {
                if (!string.IsNullOrEmpty(openIds[i]))
                {
                    GameObject openIdGameObject = Resources.Load<GameObject>("Prefabs/OpenIdChoice");
                    openIdGameObject = Instantiate(openIdGameObject, openIdGroupObj);
                    openIdGameObject.GetComponent<RectTransform>().sizeDelta = Vector2.zero;
                    openIdGameObject.transform.localPosition = new Vector3(-70, 110 - 80 * i, 0);
                    openIdGameObject.transform.localScale = Vector3.one;

                    Transform toggleObj = openIdGameObject.transform.Find("ToggleOpenId");
                    toggleObj.transform.localScale = Vector3.one;
                    Toggle openIdToggle = toggleObj.GetComponent<Toggle>();
                    openIdToggle.isOn = false;

                    Text text = openIdToggle.transform.GetComponentInChildren<Text>();
                    text.text = openIds[i];
                    openIdToggle.group = toggleGroup;
                    openIdToggle.gameObject.SetActive(true);
                }
            }
        }
        Button confirmBtn = _transferOwnerObj.transform.Find("OwnerConfirm").GetComponent<Button>();
        confirmBtn.onClick.AddListener(() =>OnOwnerConfirm(isForLeaveTransfer));

        Button cancelBtn = _transferOwnerObj.transform.Find("OwnerCancel").GetComponent<Button>();
        cancelBtn.onClick.AddListener(() => DestroyImmediate(_transferOwnerObj));
    }

    private void OnOwnerConfirm(bool isForLeaveTransfer)
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        Transform openIdGroupObj = _transferOwnerObj.transform.Find("OpenIdToggleGroup");
        ToggleGroup toggleGroup = openIdGroupObj.GetComponent<ToggleGroup>();

        Toggle toggle = toggleGroup.GetFirstActiveToggle();

        if (isForLeaveTransfer && toggle == null)
        {
            // ownerId为空，走随机切换房主的逻辑
            engine.LeaveRoom(_currentRoomId, null);
            Debug.Log("invoke LeaveRoom success");
            DestroyImmediate(_transferOwnerObj);
            return;
        }

        Text[] openIds = toggle.GetComponentsInChildren<Text>();
        string transferOwnerId = openIds[0].text;

        if (isForLeaveTransfer)
        {
            Debug.LogFormat("OnOwnerConfirm. transferOwnerId is {0}", transferOwnerId);
            engine.LeaveRoom(_currentRoomId, transferOwnerId);
            Debug.Log("invoke LeaveRoom success");
        }
        else
        {
            AppendCallbackMessage("OnOwnerConfirm. transferOwnerId is " + transferOwnerId);
            engine.TransferOwner(_currentRoomId, transferOwnerId);
            Debug.Log("invoke TransferOwner success");
            AppendCallbackMessage("invoke TransferOwner success");
        }
        
        DestroyImmediate(_transferOwnerObj);
    }

    private void LeaveRoomImpl(string roomId, int code, string msg)
    {
        Debug.LogFormat("LeaveRoomImpl. roomId={0}, code={1}, msg={2}", roomId, code, msg);
        string message;
        if (code == 0)
        {
            _roomIdsList.Remove(roomId);
            _currentRoomId = "";
            _currentOwnerId = "";
            _roomMembers.Clear();
            _roomMemberChanged = true;
            _playerMuteStatusDic.Remove(roomId);
            _roomMuteStatusDic.Remove(roomId);
         
            Loom.QueueOnMainThread(() =>
            {
                SetLocalMicToggle();
                _roomStatus = -1;
                leaveBtn.interactable = false;
            });
           
            message = "LeaveRoomImpl leave room success! roomId : " + roomId;
        }
        else
        {
            message = "LeaveRoomImpl leave room failure! roomId: " + roomId + ", code: " + code + ", msg: " + msg;
        }
        AppendCallbackMessage(message);
    }
    
    private void TransferOwnerImpl(string roomId, int code, string msg)
    {
        Debug.LogFormat("TransferOwnerImpl. roomId={0}, code={1}, msg={2}", roomId, code, msg);
        string message;
        if (code == 0)
        {
            GetRoomMembers(roomId);
            _roomMemberChanged = true;
            message = "transferOwner success! roomId : " + roomId;
        }
        else
        {
            message = "transferOwner failure! roomId: " + roomId + ", code: " + code + ", msg: " + msg;
        }
        AppendCallbackMessage(message);
    }

    private void OnRemoteMicroStateChangedImpl(string roomId, string openId, bool isMute)
    {
        Debug.LogFormat("OnRemoteMicroStateChangedImpl. roomId={0}, openId={1}, isMute={2}", roomId, openId, isMute);
        string message = "OnRemoteMicroStateChangedImpl. roomId:" +  roomId +", openId:" + openId + ", isMute:" + isMute;
        AppendCallbackMessage(message);
    }
    
    private void JoinChannelImpl(string channelId, int code, string msg)
    {
        AppendCallbackMessage($"JoinChannelImpl. channelId={channelId}, code={code}, msg={msg}");
        if (!code.Equals(0)) return;

        Loom.QueueOnMainThread(() =>
        {
            if (_imChatFrame)
            {
                AppendCallbackMessage("Destroy _imChatFrame Immediate.");
                DestroyImmediate(_imChatFrame);
            }
        
            inputField.text = "";
            _curChannelId = channelId;
            curChannelBtn.interactable = true;
            leaveChannelBtn.interactable = true;
    
            _imChatFrame = Resources.Load<GameObject>("Prefabs/ChatFrame");
            _imChatFrame = Instantiate(_imChatFrame, gameObject.transform);
            _imChatFrame.transform.Find("MaskBtn").gameObject.GetComponent<Button>().onClick.AddListener(OnMaskBtnClick);
            _imChatFrame.transform.localPosition = new Vector3(0, -700, 0);
        
            var channel = _imChatFrame.transform.Find("ChannelId");
            channel.GetComponent<Text>().text = $"群聊ID：{channelId}";
            channel.gameObject.SetActive(true);
            _imChatFrame.transform.Find("SendTo").gameObject.SetActive(false);
            _imChatFrame.transform.Find("InputReceiveId").gameObject.SetActive(false);
            _chatSendInput = _imChatFrame.transform.Find("InputSendInfo").gameObject.GetComponent<TMP_InputField>();
            _chatSendInput.onValueChanged.AddListener(ChatInputValueChanged);
            _imChatFrame.transform.Find("BtnSend").gameObject.GetComponent<Button>().onClick.AddListener(OnSendMsgBtnClick);
            _imChatFrame.SetActive(true);
        });
    }
    
    
    private void LeaveChannelImpl(string channelId, int code, string msg)
    {
        AppendCallbackMessage($"LeaveChannelImpl. channelId={channelId}, code={code}, msg={msg}");
        if (!code.Equals(0)) return;
        Loom.QueueOnMainThread(() =>
        {
            _curChannelId = "";
            leaveChannelBtn.interactable = false;
            curChannelBtn.interactable = false;
            DestroyImmediate(_imChatFrame);
        });
    }

    private void OnSwitchRoomClick(int index)
    {
        var switchRoomId = roomIdsDropdown.options[index].text;
        if (_currentRoomId.Equals(switchRoomId))
        {
            return;
        }

        Debug.LogFormat("click switch room dropdown! roomId={0}", switchRoomId);
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        engine.SwitchRoom(switchRoomId);
        Debug.Log("invoke SwitchRoom success");
    }

    private void SwitchRoomImpl(string roomId, int code, string msg)
    {
        Debug.LogFormat("SwitchRoomImpl. roomId={0}, code={1}, msg={2}", roomId, code, msg);
        string message;
        if (code == 0)
        {
            _currentRoomId = roomId;
            GetRoomMembers(roomId);
            _roomMemberChanged = true;

            Loom.QueueOnMainThread(() =>
            {
                leaveBtn.interactable = true;
                inputField.text = "";
            });
            message = "switch room success! roomId : " + roomId;
        }
        else
        {
            message = "switch room failure! roomId: " + roomId + ", code: " + code + ", msg: " + msg;
        }
        AppendCallbackMessage(message);
    }

    private  void GetRoomMembers(string roomId)
    {
        GameMediaEngine engine = GMMEEnginHolder.GetInstance().gMMEEngine;
        if (engine == null)
        {
            AppendCallbackMessage("engine is null, please init engine first");
            Debug.Log("engine is null, please init engine first");
            return;
        }

        Room roomInfo = engine.GetRoom(roomId);
        if (roomInfo == null)
        {
            AppendCallbackMessage("engine GetRoom error, roomInfo is null");
            return;
        }
        _currentOwnerId = roomInfo.OwnerId;
        _roomStatus = roomInfo.RoomStatus;

        // 根据房间类型初始化房主/成员的权限
        Debug.LogFormat($"GetRoomMembers roomId={roomId} RoomType={roomInfo.RoomType}");
        _roomType = (IGMMERoomType)Enum.ToObject(typeof(IGMMERoomType), roomInfo.RoomType);
        Debug.LogFormat($"invoke GetRoom success. CurRoomId={roomId}, OwnerId={roomInfo.OwnerId}, Players size={roomInfo.Players.Count}");

        _roomMembers.Clear();
        var players = roomInfo.Players;
        // 获取每个玩家的禁言状态
        foreach (var player in players)
        {
            if (_roomType == IGMMERoomType.IGMME_ROOM_TYPE_NATIONAL && _userId == player.OpenId)
            {
                _roleType = player.RoleType;
            }
            var roomMember = new RoomMember
            {
                OpenId = player.OpenId,
                MicIsOn = player.IsForbidden == 0,
                /***
                 * 1.先获取本地是否有房间成员状态，如果有，根据本地的状态进行刷新处理；
                 * 2.如果本地没有，则需要添加该成员在该房间的屏蔽状态，默认都是打开的，即值为false
                 */
                AudioIsOn = !GetMemberMuteStatus(roomId, player.OpenId)
            };
            _roomMembers.Add(roomMember);
            Debug.LogFormat("add roomMember openId={0}", roomMember.OpenId);
        }
    }

    private bool GetMemberMuteStatus(string roomId, string openId)
    {
        if (_playerMuteStatusDic.TryGetValue(roomId, out var muteOldStatus))
        {
            if (muteOldStatus.TryGetValue(openId, out var muteStatus))
            {
                return muteStatus;
            }
            muteOldStatus.Add(openId, false);
            _playerMuteStatusDic.Remove(roomId);
            _playerMuteStatusDic.Add(roomId, muteOldStatus);
            return false;
        }
        Dictionary<string, bool> memberMuteStatus = new Dictionary<string, bool>{ {openId, false} };
        _playerMuteStatusDic.Add(roomId, memberMuteStatus);
        return false;
    }

    private void UpdateMemberMuteStatus(string roomId, string openId, bool isMute)
    {
        if (_playerMuteStatusDic.TryGetValue(roomId, out var muteOldStatus))
        {
            if (muteOldStatus.ContainsKey(openId))
            {
                muteOldStatus.Remove(openId);
                muteOldStatus.Add(openId, isMute);
            }
            else
            {
                muteOldStatus.Add(openId, isMute);
            }
            _playerMuteStatusDic.Remove(roomId);
            _playerMuteStatusDic.Add(roomId, muteOldStatus);
        }
        else
        {
            Dictionary<string, bool> memberMuteStatus = new Dictionary<string, bool> { {openId, isMute} };
            _playerMuteStatusDic.Add(roomId, memberMuteStatus);
        }
    }

    private void OnClearBtClick()
    {
        ClearCallbackMessage();
    }

    // 音效弹框关闭回调
    void CloseAudioEffectFunc()
    {
        _audioEffectObject.SetActive(false);
    }
    
    // 玩家位置关闭
    void ClosePlayerPositionFunc()
    {
        _playerPositionObject.SetActive(false);
    }
    private void OnSwitchBtnClick()
    {
        var switchBtnName = switchBtn.transform.Find("BtnName").GetComponent<TMP_Text>();
        var tempPosition = callbackMsgObj.transform.position;
        if (!_curShowMsgWindow)
        {
            callbackMsgObj.transform.position = memberListObj.transform.position;
            memberListObj.transform.position = tempPosition;
            switchBtnName.text = "查看成员";
        }
        else
        {
            callbackMsgObj.transform.position = memberListObj.transform.position;
            memberListObj.transform.position = tempPosition;
            switchBtnName.text = "查看日志";
        }

        _curShowMsgWindow = !_curShowMsgWindow;
    }

    private void OnTransferBtnClick()
    {
        var openIds = new List<string>();
        var isShowTransferOwnerUI = false;
        if (_userId.Equals(_currentOwnerId) && _roomMembers.Count > 1)
        {
            foreach (var member in _roomMembers)
            {
                if (!member.OpenId.Equals(_currentOwnerId))
                {
                    openIds.Add(member.OpenId);
                }
            }
            isShowTransferOwnerUI = true;
        }

        if (isShowTransferOwnerUI)
        {
            LoadTransferOwnerResource(openIds, false);
        }
    }

    // Update is called once per frame
    public override void Update()
    {
        base.Update();
        if (_displayRoomId != _currentRoomId)
        {
            Debug.LogFormat("Update currentRoomId={0}", _currentRoomId);
            _displayRoomId = _currentRoomId;
            UpdateRoomIdsDropDownItem();
            UpdateSpatialAudioBtnTitle();
        }

        if (_roomMemberChanged)
        {
            Debug.LogFormat("Update RoomMembers. size={0}", _roomMembers.Count);
            _roomMemberChanged = false;
            UpdateRoomMembers();
        }
    }

    private void UpdateRoomIdsDropDownItem()
    {
        Debug.LogFormat("UpdateDropDownItem currentRoomId={0}", _currentRoomId);
        roomIdsDropdown.captionText.text = _currentRoomId;
        var tempIdx = _roomIdsList.IndexOf(_currentRoomId);
        roomIdsDropdown.options.Clear();
        roomIdsDropdown.AddOptions(_roomIdsList);
        roomIdsDropdown.value = tempIdx;
    }

    private void UpdateSpatialAudioBtnTitle()
    {
        var titleName = SpatialAudioBtn.transform.Find("TitleName").GetComponent<Text>();
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        if (_currentRoomId == "")
        {
            return;
        }

        bool isOn = engine.IsEnableSpatialSound(_currentRoomId);
        titleName.text = isOn ? "关闭音效" : "开启音效";
    }

    private void UpdateRoomMembers()
    {
        Debug.Log("UpdateRoomMembers start");
        AppendCallbackMessage("UpdateRoomMembers start, count: " + _roomMembers.Count);
        // 去重操作，规避SDK返回的2次通知的bug
        _roomMembers = _roomMembers.Distinct(new Compare()).ToList();
        // 设置本地麦克风组件
        SetLocalMicToggle();
        // 设置切换房主按钮
        SetTransferBtn();
        GameObject playersObj = GameObject.Find("Players");
        int count = playersObj.transform.childCount;
        for (int i = 0; i < count; i++)
        {
            DestroyImmediate(playersObj.transform.GetChild(0).gameObject);
        }

        // 成员列表，成员自己第一行，其他依次排序
        RoomMember currentMember = _roomMembers.Find(item => item.OpenId == _userId);
        if (currentMember != null)
        {
            _roomMembers.Remove(currentMember);
            _roomMembers.Insert(0, currentMember);
        }

        if (_roomType == IGMMERoomType.IGMME_ROOM_TYPE_TEAM)
        {
            // 显示房间成员人数
            membersTitle.SetText("小队(" + _roomMembers.Count + ")");
        }
        else if (_roomType == IGMMERoomType.IGMME_ROOM_TYPE_NATIONAL)
        {
            // 显示国战成员人数
            membersTitle.SetText("国战(" + _roomMembers.Count + ")");
        }
        else if (_roomType == IGMMERoomType.IGMME_ROOM_TYPE_RANGE)
        {
            // 显示范围成员人数
            membersTitle.SetText("范围(" + _roomMembers.Count + ")");
        }
        membersTitle.gameObject.SetActive(_roomMembers.Count > 0);
        SetSpatialAudioBtnListener(_spatialSoundEnable);
        SpatialAudioBtn.gameObject.SetActive(_roomMembers.Count > 0);
        _micToggleList.Clear();
        _audioToggleList.Clear();
        var allMicIsOn = true;
        var allAudioIsOn = true;
        for (var i = 0; i < _roomMembers.Count; i++)
        {
            Debug.LogFormat("UpdateRoomMembers add member-{0} begin.", _roomMembers[i].OpenId);
            var member = _roomMembers[i];
            // 循环的用户列表项是否当前用户
            var itemIsSelf = member.OpenId == _userId;
            // 设置玩家显示位置
            var player = (GameObject)Resources.Load("Prefabs/Player");
            player = Instantiate(player, playersObj.transform);
            player.transform.localPosition = new Vector3(0, 330 - 120 * i, 0);
            // 设置玩家昵称
            var playerName = player.transform.Find("PlayerName").GetComponentInChildren<Text>();
            playerName.text = member.OpenId;
            //获取玩家麦克风按钮
            var mToggle = player.transform.Find("Mic").GetComponent<Toggle>();
            // 获取音频（小喇叭）按钮
            var audioToggle = player.transform.Find("Audio").GetComponent<Toggle>();
            // 设置房主标签 范围房间没有房主
            if (_roomType == IGMMERoomType.IGMME_ROOM_TYPE_RANGE)
            {
                mToggle.gameObject.SetActive(false);
                audioToggle.gameObject.SetActive(false);
            }
            else
            {
                if (member.OpenId == _currentOwnerId)
                {
                    var ownerText = player.transform.Find("Owner");
                    ownerText.gameObject.SetActive(true);
                }
                //设置玩家麦克风按钮
                SetMicToggleIsOn(member.OpenId, mToggle, member.MicIsOn);
                allMicIsOn = allMicIsOn && member.MicIsOn;
                SetToggleBg(mToggle);
                mToggle.gameObject.SetActive(true);
                SetMicListener(member.OpenId, mToggle, _currentOwnerId == _userId);
                mToggle.interactable = _currentOwnerId == _userId;
                // 设置音频（小喇叭）按钮
                // 小队不支持屏蔽单个玩家语音，国战除自己外可屏蔽他人语音，故这两种情况下，audio直接不可交互
                SetAudioToggleIsOn(member.OpenId, audioToggle, member.AudioIsOn);
                allAudioIsOn = allAudioIsOn && member.AudioIsOn;
                SetToggleBg(audioToggle);
                audioToggle.gameObject.SetActive(true);
                if (itemIsSelf)
                {
                    audioToggle.interactable = false;
                }
                else
                {
                    SetAudioListener(member.OpenId, audioToggle, true);
                }
                _micToggleList.Add(mToggle);
                _audioToggleList.Add(audioToggle);
            }
        }
        // 根据全员状态设置一键禁言和一键静音按钮
        InitAllMicAndAudioStatus(allAudioIsOn);
        
        // 更新房间的audio状态
        if (_roomMuteStatusDic.TryGetValue(_currentRoomId, out var muteStatus))
        {
            SetAudioAllToggleIsOn(!muteStatus);
        }
    }

    private void SetTransferBtn()
    {
        // 房主才显示切换按钮
        if (_userId.Equals(_currentOwnerId))
        {
            // 房间人数大于1时才能切换房主
            if (_roomMembers.Count > 1)
            {
                transferBtn.gameObject.SetActive(true);
                transferBtn.onClick.AddListener(OnTransferBtnClick);
            }
            else
            {
                transferBtn.gameObject.SetActive(false);
                transferBtn.onClick.RemoveAllListeners();
            }
        }
        else
        {
            transferBtn.gameObject.SetActive(false);
            transferBtn.onClick.RemoveAllListeners();
        }
    }

    private void SetLocalMicToggle()
    {
        // 如果是国战房间的群众成员，本地麦克风不启用（在小队房间或不在任何房间时均启用）
        localMicToggle.gameObject.SetActive(
            !(
                _roomType == IGMMERoomType.IGMME_ROOM_TYPE_NATIONAL && 
                _roleType == Constant.PLAYER
            )
        );
    }

    /// <summary>
    /// 初始化一键禁言和一键屏蔽的状态
    /// </summary>
    private void InitAllMicAndAudioStatus(bool audioIsOn)
    {
        //移除所有监听事件
        SetMicAllToggleListener(false);
        SetAudioAllToggleListener(false);
        // 重置状态(禁言根据后端返回的房间状态决定，屏蔽只要房间存在就是开的)
        SetMicAllToggleIsOn(_roomStatus == 0);
        SetAudioAllToggleIsOn(audioIsOn);
        // 是否可交互
        micAllToggle.interactable = _roomMembers.Count > 1;
        audioAllToggle.interactable = _roomMembers.Count > 0;   
        if (_roomType == IGMMERoomType.IGMME_ROOM_TYPE_RANGE)
        {
            micAllToggle.gameObject.SetActive(false);
            SetMicAllToggleListener(false);
        }
        else
        {
            micAllToggle.gameObject.SetActive(_roomMembers.Count > 0 && _currentOwnerId == _userId);
            SetMicAllToggleListener(_currentOwnerId == _userId);
        }
        // 是否显示组件
        audioAllToggle.gameObject.SetActive(_roomMembers.Count > 0);

        // 是否启用监听事件
        SetAudioAllToggleListener(true);
    }

    /// <summary>
    /// 开启/禁用本地麦克风
    /// </summary>
    /// <param name="micEnabled"></param>
    private void OnLocalMicToggleClick(bool micEnabled)
    {
        Debug.LogFormat("localMic click,  micEnabled ={0}", micEnabled);
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        int result = engine.EnableMic(micEnabled);

        // 回调失败，不触发valueChange更新isOn的值
        if (result != 0)
        {
            UIChangeLocalAudioToggleValue(!micEnabled);
        }
        Debug.Log($"invoke EnableMic success. roomId={_currentRoomId}, micEnabled={micEnabled}, result={result}");
        AppendCallbackMessage($"invoke EnableMic success. roomId={_currentRoomId}, micEnabled={micEnabled}, result={result}");
    }

    /// <summary>
    /// 房主禁言/解禁所有玩家（除自己外）
    /// </summary>
    /// <param name="isOn">true：解禁  false：禁言</param>
    private void OnMicAllToggleClick(bool isOn)
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        engine.ForbidAllPlayers(_currentRoomId, !isOn);
        AppendCallbackMessage($"invoke ForbidAllPlayers success. roomId={_currentRoomId}, isOn={isOn}");
    }

    /// <summary>
    /// 房主禁言/解禁某个玩家
    /// </summary>
    /// <param name="openId">玩家id</param>
    /// <param name="isOn">true：解禁  false：禁言</param>
    private void OnMicToggleClick(string openId, bool isOn)
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        engine.ForbidPlayer(_currentRoomId, openId, !isOn);
        AppendCallbackMessage($"invoke ForbidPlayer success. roomId={_currentRoomId}, playerOpenId={openId}, isOn={isOn}");
    }

    /// <summary>
    /// 屏蔽某个玩家的音频
    /// </summary>
    /// <param name="openId">玩家id</param>
    /// <param name="muteFlag">true：屏蔽</param>
    private void OnAudioToggleClick(string openId, bool muteFlag)
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        engine.MutePlayer(_currentRoomId, openId, !muteFlag);
        AppendCallbackMessage($"invoke MutePlayer success. roomId={_currentRoomId}, playerOpenId={openId}, muteFlag={muteFlag}");
    }

    /// <summary>
    /// 屏蔽其他成员的音频
    /// </summary>
    /// <param name="muteFlag">true：屏蔽</param>
    private void OnAudioAllToggleClick(bool muteFlag)
    {
        Debug.LogFormat("AudioAllToggle click,  muteFlag ={0}", muteFlag);
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        engine.MuteAllPlayers(_currentRoomId, !muteFlag);
        AppendCallbackMessage($"invoke MuteAllPlayers success. roomId={_currentRoomId}, muteFlag={muteFlag}");
    }
    
    /// <summary>
    /// 开启关闭音效点击响应事件
    /// </summary>
    private void OnSpatialAudioBtnClick()
    {
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }

        if (_currentRoomId == "")
        {
            return;
        }

        bool isOn = engine.IsEnableSpatialSound(_currentRoomId);
        int code = engine.EnableSpatialSound(_currentRoomId, !isOn);
        if (code == 0)
        {
            UpdateSpatialAudioBtnTitle();
        }
    }

    /// <summary>
    /// 设置本地麦克风Toggle监听
    /// </summary>
    /// <param name="enableListener">是否启用监听事件</param>
    private void SetLocalMicToggleListener(bool enableListener)
    {
        if (enableListener)
        {
            localMicToggle.onValueChanged.AddListener(value => OnLocalMicToggleClick(value));
        }
        else
        {
            localMicToggle.onValueChanged.RemoveAllListeners();
        }
    }
    
    /// <summary>
    /// 给SpatialAudioBtn设置监听
    /// </summary>
    /// <param name="enableListener">是否启用监听事件</param>
    private void SetSpatialAudioBtnListener(bool enableListener)
    {
        SpatialAudioBtn.onClick.RemoveAllListeners();
        if (enableListener)
        {
            SpatialAudioBtn.onClick.AddListener(OnSpatialAudioBtnClick);
        }
        SpatialAudioBtn.interactable = enableListener;
    }


    /// <summary>
    /// 给全局禁言Toggle设置监听
    /// </summary>
    /// <param name="enableListener">是否启用监听事件</param>
    private void SetMicAllToggleListener(bool enableListener)
    {
        if (enableListener)
        {
            micAllToggle.onValueChanged.AddListener(value => OnMicAllToggleClick(value));
        }
        else
        {
            micAllToggle.onValueChanged.RemoveAllListeners();
        }
    }

    /// <summary>
    /// 给全局屏蔽Toggle设置监听
    /// </summary>
    /// <param name="enableListener">是否启用监听事件</param>
    private void SetAudioAllToggleListener(bool enableListener)
    {
        if (enableListener)
        {
            audioAllToggle.onValueChanged.AddListener(value=> OnAudioAllToggleClick(value));
        }
        else
        {
            audioAllToggle.onValueChanged.RemoveAllListeners();
        }
    }

    /// <summary>
    /// 给个人成员的麦克风Toggle设置监听
    /// </summary>
    /// <param name="openId">成员openId</param>
    /// <param name="toggle">禁言Toggle</param>
    /// <param name="addListener">是否启用监听事件</param>
    private void SetMicListener(string openId, Toggle toggle, bool addListener)
    {
        if (addListener)
        {
            toggle.onValueChanged.AddListener(value => OnMicToggleClick(openId, value));
        }
        else
        {
            toggle.onValueChanged.RemoveAllListeners();
        }
    }

    /// <summary>
    /// 给个人成员的音频小喇叭Toggle设置监听
    /// </summary>
    /// <param name="openId">成员openId</param>
    /// <param name="toggle">屏蔽Toggle</param>
    /// <param name="addListener">是否启用监听事件</param>
    private void SetAudioListener(string openId, Toggle toggle, bool addListener)
    {
        if (addListener)
        {
            AppendCallbackMessage($"SetAudioListener AddListener openId {openId}");
            toggle.onValueChanged.AddListener(value => OnAudioToggleClick(openId, value));
        }
        else
        {
            AppendCallbackMessage($"SetAudioListener RemoveAllListeners openId {openId}");
            toggle.onValueChanged.RemoveAllListeners();
        }
    }
    
    /// <summary>
    /// 主动触发成员的mic状态变更
    /// </summary>
    /// <param name="openId">openId</param>
    /// <param name="isOn">是否被选中</param>
    private void UpdatePlayerMic(string openId, bool isOn)
    {
        // 根据openId找到toggle
        Toggle toggle = null;
        var index = -1;
        for (var i = 0; i < _roomMembers.Count; i++)
        {
            if(!_roomMembers[i].OpenId.Equals(openId)) continue;
            index = i;
            _roomMembers[i].MicIsOn = isOn;
            break;
        }
        if (index >= 0)
        {
            toggle = _micToggleList[index];
        }
        if (!toggle)
        {
            return;
        }

        SetMicToggleIsOn(openId, toggle, isOn);
    }

    /// <summary>
    /// 主动触发玩家的audio状态变更
    /// </summary>
    /// <param name="roomId">roomId</param>
    /// <param name="openId">openId</param>
    /// <param name="isMute">是否被屏蔽</param>
    private void UpdatePlayerAudio(string roomId, string openId, bool isMute)
    {
        // 根据openId找到toggle
        Toggle audioToggle = null;
        var index = -1;
        for (var i = 0; i < _roomMembers.Count; i++)
        {
            if (!_roomMembers[i].OpenId.Equals(openId)) continue;
            index = i;
            _roomMembers[i].AudioIsOn = !isMute;
            break;
        }
        if (index >= 0)
        {
            audioToggle = _audioToggleList[index];
        }
        if (!audioToggle)
        {
            return;
        }
        SetAudioToggleIsOn(openId, audioToggle, !isMute);

        //  保存或者更新每个玩家的屏蔽状态值
        UpdateMemberMuteStatus(roomId, openId, isMute);
    }


    /// <summary>
    /// 不触发valueChange事件，更新LocalMic的isOn值
    /// </summary>
    /// <param name="selected">是否被选中</param>
    private void UIChangeLocalAudioToggleValue(bool selected)
    {
        SetLocalMicToggleListener(false);
        localMicToggle.isOn = selected;
        SetLocalMicToggleListener(true);
    }

    /// <summary>
    /// 不触发valueChange事件，更新全局mic的isOn值
    /// </summary>
    /// <param name="isOn">是否被选中</param>
    private void SetMicAllToggleIsOn(bool isOn)
    {
        if (_currentOwnerId == _userId)
        {
            SetMicAllToggleListener(false);
            micAllToggle.isOn = isOn;
            SetMicAllToggleListener(true);
        }
        else
        {
            micAllToggle.isOn = isOn;
        }
        SetToggleBg(micAllToggle);
    }

    /// <summary>
    /// 不触发valueChange事件，更新全局audio的isOn值
    /// </summary>
    /// <param name="isOn">是否被选中</param>
    private void SetAudioAllToggleIsOn(bool isOn)
    {
        SetAudioAllToggleListener(false);
        audioAllToggle.isOn = isOn;
        SetAudioAllToggleListener(true);
        SetToggleBg(audioAllToggle);
    }

    /// <summary>
    /// 主动触发-不触发valueChange事件，设置单个玩家mic的isOn值
    /// </summary>
    /// <param name="openId">openId</param>
    /// <param name="toggle">UI组件Toggle</param>
    /// <param name="isOn">是否被选中</param>
    private void SetMicToggleIsOn(string openId, Toggle toggle, bool isOn)
    {
        if (_currentOwnerId.Equals(_userId))
        {
            SetMicListener(openId, toggle, false);
            toggle.isOn = isOn;
            SetMicListener(openId, toggle, true);
        }
        else
        {
            toggle.isOn = isOn;
        }
        SetToggleBg(toggle);
    }

    /// <summary>
    /// 主动触发-不触发valueChange事件，设置单个玩家audio的isOn值
    /// </summary>
    /// <param name="openId">openId</param>
    /// <param name="toggle">UI组件Toggle</param>
    /// <param name="isOn">是否被选中</param>
    private void SetAudioToggleIsOn(string openId, Toggle toggle, bool isOn)
    {
        if (_roomType.Equals(IGMMERoomType.IGMME_ROOM_TYPE_NATIONAL))
        {
            SetAudioListener(openId, toggle, false);
            toggle.isOn = isOn;
            SetAudioListener(openId, toggle, true);
        }
        else
        {
            toggle.isOn = isOn;
        }
        SetToggleBg(toggle);
    }

    /// <summary>
    /// 设置toggle的Bg的透明度
    /// </summary>
    /// <param name="toggle">UI组件Toggle</param>
    private void SetToggleBg(Toggle toggle)
    {
        var img = toggle.transform.Find("Background").GetComponent<Image>();
        img.color = toggle.isOn ? new Color(255, 255, 255, 0) : new Color(255, 255, 255, 255);
    }

    private void MutePlayerCompleteImpl(string roomId, string openId, bool isMuted, int code, string msg)
    {
        // 开麦/闭麦（muteLocal）不再回调，屏蔽单个玩家或者其他所有玩家成功结果集的retCode都为0
        var successFlag = code == 0;
        var result = successFlag ? "success" : "failed";
        var resp = $"mute player {result}. roomId={roomId}, openId={openId}, isMuted={isMuted}, code={code}, msg={msg}";
        AppendCallbackMessage(resp);
        Debug.Log(resp);

        Loom.QueueOnMainThread(() =>
        {
            if (!string.IsNullOrEmpty(openId))
            {
                UpdatePlayerAudio(roomId, openId, isMuted);
            }
        });
    }
    
    private void MuteAllPlayersCompleteImpl(string roomId, List<string> openIds, bool isMuted, int code, string msg)
    {
        // 开麦/闭麦（muteLocal）不再回调，屏蔽单个玩家或者其他所有玩家成功结果集的retCode都为0
        var successFlag = code == 0;
        var openIdsStr = string.Join(",", openIds.ToArray());
        var result = successFlag ? "success" : "failed";
        var resp = $"mute all players {result}. roomId={roomId}, openIds={openIdsStr}, isMuted={isMuted} code={code}, msg={msg}";
        Debug.Log(resp);
        AppendCallbackMessage(resp);
        _roomMuteStatusDic.Remove(roomId);
       _roomMuteStatusDic.Add(roomId,isMuted);
        Loom.QueueOnMainThread(() =>
        {
            // 如果没有openIds，不更新全局状态
            if (openIdsStr == "" || openIds.Count == 0)
            {
                SetAudioAllToggleIsOn(!isMuted);
                AppendCallbackMessage("No member to be shielded.audioAllToggle.isOn is " + (audioAllToggle.isOn ? "true" : "false"));
                Debug.LogFormat("No member to be shielded.");
                return;
            }
            SetAudioAllToggleIsOn(!isMuted);
            openIds.ForEach(openId =>
            {
                if (!string.IsNullOrEmpty(openId) && openId != _userId)
                {
                    UpdatePlayerAudio(roomId, openId, isMuted);
                }
            });
        });
    }

    private void ForbidPlayerCompleteImpl(string roomId, string openId, bool isForbidden, int code, string msg)
    {
        // 禁言单个玩家或者所有玩家成功结果集的retCode都为0
        var successFlag = code == 0;
        var result = successFlag ? "success" : "failed";
        var resp = $"forbid player {result}. roomId={roomId}, openId={openId}, isForbidden= {isForbidden} code={code}, msg={msg}";
        Debug.Log(resp);
        AppendCallbackMessage(resp);

        Loom.QueueOnMainThread(() =>
        {
            if (!string.IsNullOrEmpty(openId))
            {
                UpdatePlayerMic(openId, successFlag ? !isForbidden : isForbidden);
            }
        });
    }
    private void ForbidAllPlayersCompleteImpl(string roomId, List<string> openIds, bool isForbidden, int code, string msg)
    {
        // 禁言单个玩家或者所有玩家成功结果集的retCode都为0
        var successFlag = code == 0;
        var openIdsStr = string.Join(",", openIds.ToArray());
        var result = successFlag ? "success" : "failed";
        var resp = $"forbid all players {result}. roomId={roomId}, openIds={openIdsStr}, isForbidden={isForbidden}, code={code}, msg={msg}";
        Debug.Log(resp);
        AppendCallbackMessage(resp);

        GameMediaEngine engine = GMMEEnginHolder.GetInstance().gMMEEngine;
        if (engine == null)
        {
            AppendCallbackMessage("engine is null, please init engine first");
            Debug.Log("engine is null, please init engine first");
            return;
        }

        // 需要重新获取下房间状态，刷新UI房间状态
        Room roomInfo = engine.GetRoom(roomId);
        if (roomInfo == null)
        {
            AppendCallbackMessage("engine GetRoom error, roomInfo is null");
            return;
        }
        _roomStatus = roomInfo.RoomStatus;
        Debug.LogFormat($"ForbidAllPlayersCompleteImpl roomStatus={_roomStatus}");

        Loom.QueueOnMainThread(() =>
        {
            // 如果没有openIds，不变更全局状态
            if (openIds.Count == 0)
            {
                AppendCallbackMessage("No member needs to be forbidden.micAllToggle.isOn is " + (micAllToggle.isOn ? "true" : "false"));
                Debug.LogFormat("No member needs to be forbidden.");
                return;
            }
            SetMicAllToggleIsOn(!isForbidden);
            openIds.ForEach(openId =>
            {
                if (!string.IsNullOrEmpty(openId))
                {
                    UpdatePlayerMic(openId, successFlag ? !isForbidden : isForbidden);
                }
            });
        });
    }

    /// <summary>
    /// 被禁言玩家收到禁言指令的回调
    /// </summary>
    /// <param name="openIds">玩家id列表</param>
    /// <param name="roomId">房间id</param>
    /// <param name="isForbidden">是否被禁言</param>
    private void ForbiddenByOwnerCompleteImpl(string roomId, List<string> openIds, bool isForbidden)
    {
        var openIdsStr = string.Join(",", openIds.ToArray());
        var resp = $"forbidden by owner. roomId={roomId}, openIds={openIdsStr}, isForbidden={isForbidden}";
        Debug.Log(resp);
        AppendCallbackMessage(resp);

        Loom.QueueOnMainThread(() =>
        {
            openIds.ForEach(openId =>
            {
                if (!string.IsNullOrEmpty(openId))
                {
                    UpdatePlayerMic(openId, !isForbidden);
                }
            });
        });
    }

    private void ReceiveMsgImpl(Message msg)
    {
        AppendCallbackMessage($"ReceiveMsgImpl success. msg={msg}");
        if (!msg.Code.Equals(0)) return;
        Loom.QueueOnMainThread(() =>
        {
            var message = Instantiate(msgObj, _imChatFrame.transform.Find("Backpack").Find("Mask").Find("Grid"));
            message.transform.Find("NameAndTime").GetComponent<Text>().text = $"{msg.SenderId}  {GetDateTime(msg.Time)}";
            message.transform.Find("Content").GetComponent<Text>().text = $"{msg.Content}";
        });
    }

    private void SendMsgImpl(Message msg)
    {
        AppendCallbackMessage($"SendMsgImpl success. msg={msg}");
        if (!msg.Code.Equals(0)) return;
        Loom.QueueOnMainThread(() =>
        {
            var message = Instantiate(msgObj, _imChatFrame.transform.Find("Backpack").Find("Mask").Find("Grid"));
            message.transform.Find("NameAndTime").GetComponent<Text>().text = $"{msg.SenderId}  {GetDateTime(msg.Time)}";
            message.transform.Find("Content").GetComponent<Text>().text = $"{msg.Content}";
        });
    }

    private static DateTime GetDateTime(long timestamp)
    {
        long beginTime = timestamp * 10000;
        DateTime dt1970 = new DateTime(1970, 1, 1, 8, 0, 0);
        long ticks1970 = dt1970.Ticks;
        long timeTicks = ticks1970 + beginTime;
        DateTime dt = new DateTime(timeTicks);
        return dt;
    }

    private void OnDestroy()
    {
        Debug.LogFormat("RoomCtr OnDestroy");
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnDestroyEngineCompleteEvent -= OnDestroyEngineImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinTeamRoomCompleteEvent -= JoinRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnLeaveRoomCompleteEvent -= LeaveRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSwitchRoomCompleteEvent -= SwitchRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPlayerOnlineCompleteEvent -= PlayerOnlineImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnPlayerOfflineCompleteEvent -= PlayerOfflineImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSpeakersDetectionExCompleteEvent -= OnSpeakersDetectionExImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnMuteAllPlayersCompleteEvent -= MuteAllPlayersCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnMutePlayerCompleteEvent -= MutePlayerCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnForbidAllPlayersCompleteEvent -= ForbidAllPlayersCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnForbidPlayerCompleteEvent -= ForbidPlayerCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnForbiddenByOwnerCompleteEvent -= ForbiddenByOwnerCompleteImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinNationalRoomCompleteEvent -= JoinNationalRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinRangeRoomCompleteEvent -= JoinRangeRoomImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnTransferOwnerCompleteEvent -= TransferOwnerImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRemoteMicroStateChangedCompleteEvent -= OnRemoteMicroStateChangedImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnJoinChannelCompleteEvent -= JoinChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnLeaveChannelCompleteEvent -= LeaveChannelImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnSendMsgCompleteEvent -= SendMsgImpl;
        GMMEEnginHolder.GetInstance().GetGameMMEEventHandler().OnRecvMsgCompleteEvent -= ReceiveMsgImpl;
        AudioEffect.EventCloseAudioEffect -= CloseAudioEffectFunc;
        PlayerPositionInfo.EventClosePlayerPosition -= ClosePlayerPositionFunc;
    }
    
    private void ChangeButtonClickArea()
    {
        Button[] btns =  { switchBtn, destroyBtn, audioMsgBtn, audioEffectBtn };
        foreach (Button btn in btns)
        {
            PhoneAdaptive.ChangeBtnClickArea(btn);
        }
    }

        // 上报个人坐标
    private void UpdateSelfPosition()
    {
        SelfPosition selfPosition = new SelfPosition();
        PlayerPosition position = new PlayerPosition();
        position.Forward = 0.0f;
        position.Right = 0.0f;
        position.Up = 0.0f;
        selfPosition.Position = position;
        Axis axis = new Axis();
        axis.Forward = MatrixTool.GetForwardMatrix(0, 0, 0);
        axis.Right = MatrixTool.GetForwardMatrix(0, 0, 0);
        axis.Up = MatrixTool.GetForwardMatrix(0, 0, 0);
        selfPosition.Axis = axis;
        GameMediaEngine engine = GetEngineInstance();
        if (engine == null)
        {
            return;
        }
        engine.UpdateSelfPosition(selfPosition);
    }
    
}

public enum ChatType
{
    PrivateChat = 1,// 1V1 私聊
    ChannelChat = 2,// 群聊
};
public class RoomMember
{
    public string OpenId
    {
        get;
        set;
    }

    // 0 打开 1 关闭
    public bool MicIsOn
    {
        get;
        set;
    }

    // 0 打开 1 关闭
    public bool AudioIsOn
    {
        get;
        set;
    }
}

public class Compare : IEqualityComparer<RoomMember>
{
    public bool Equals(RoomMember x, RoomMember y)
    {
        return x.OpenId.Equals(y.OpenId);
    }

    public int GetHashCode(RoomMember obj)
    {
        return obj.OpenId.GetHashCode();
    }
}
