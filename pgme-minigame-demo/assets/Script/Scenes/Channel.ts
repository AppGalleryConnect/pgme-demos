/*
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

import {CheckType, ConnectionStatus, LogType, MessageType, PropertiesType, SelectTabType} from '../Function/Enum';
import LogScrollItem from '../Function/LogScrollItem';
import GlobalData from '../../GlobalData';
import {
  RtmChannelPlayerPropertiesNotify,
  RtmChannelPropertiesNotify,
  DeleteRtmChannelPlayerPropertiesReq,
  DeleteRtmChannelPropertiesReq,
  GameMediaEngine,
  GetRtmChannelHistoryMessagesReq,
  GetRtmChannelHistoryMessagesResult,
  GetRtmChannelPlayerPropertiesReq,
  GetRtmChannelPlayerPropertiesResult,
  GetRtmChannelPropertiesReq,
  GetRtmChannelPropertiesResult,
  GetRtmChannelInfoResult,
  PublishRtmChannelMessageReq,
  PublishRtmChannelMessageResult,
  ReceiveRtmChannelMessageNotify,
  SetRtmChannelPlayerPropertiesReq,
  SetRtmChannelPlayerPropertiesResult,
  SetRtmChannelPropertiesReq,
  SetRtmChannelPropertiesResult,
  SubscribeRtmChannelReq,
  SubscribeRtmChannelResult,
  UnSubscribeRtmChannelReq,
  UnSubscribeRtmChannelResult,
} from '../../GMME/GMMEForMiniGames';
import Utils from '../Function/Utils';
import ReceiverChannelScrollItem from "../Function/ReceiverChannelScrollItem";
import Constant from "../Function/Constant";
import SetCustomProperties from "../Comp/PrefabItem/SetCustomProperties";
import CustomPropertiesScrollItem from "../Function/CustomPropertiesScrollItem";
import {RtmChannelMemberInfo} from "../../../../gmme-sdk-ForMiniGames/src/modules/define/RtmClient";

const { ccclass, property } = cc._decorator;
const regex = /^[a-zA-Z0-9_]+$/;
// 指定频道接收用户
const channelUsersMap: Map<string, string[]> = new Map<string, string[]>();
const rtmChannelContentMap = new Map<string, string | Uint8Array>();
// 控制下拉框是否显示
let messageIsShowScrollView = false;
let channelPropIsShowScrollView = false;
let userPropIsShowScrollView = false;

@ccclass
export default class Channel extends cc.Component {
  // 内容风控审核、广告识别、缓存历史消息
  private isContentIdentify: boolean = false;

  private isAdsIdentify: boolean = false;

  private isAllowCacheMsg: boolean = false;

  @property(cc.EditBox)
  sendChannelContent: cc.EditBox = null;

  @property(cc.Button)
  send: cc.Button = null;

  @property(cc.Button)
  sendBinary: cc.Button = null;

  @property(cc.Button)
  back: cc.Button = null;

  @property(cc.Button)
  subscribe: cc.Button = null;

  @property(cc.Button)
  unSubscribe: cc.Button = null;

  @property(cc.Layout)
  content: cc.Layout = null;

  @property(cc.EditBox)
  channelName: cc.EditBox = null;

  @property(cc.EditBox)
  receiveUsers: cc.EditBox = null;

  @property(cc.Label)
  loginUsers: cc.Label = null;

  @property(cc.Label)
  userName: cc.Label = null;

  @property(cc.EditBox)
  userPropUserName: cc.EditBox = null;

  @property(cc.EditBox)
  curMessageChannelId: cc.EditBox = null;

  @property(cc.EditBox)
  curChannelPropChannelId: cc.EditBox = null;

  @property(cc.EditBox)
  curUserPropChannelId: cc.EditBox = null;

  @property(cc.Button)
  messageTab: cc.Button = null;

  @property(cc.Button)
  channelPropertiesTab: cc.Button = null;

  @property(cc.Button)
  userPropertiesTab: cc.Button = null;

  @property(cc.Layout)
  messageLayout: cc.Layout = null;

  @property(cc.Layout)
  channelPropertiesLayout: cc.Layout = null;

  @property(cc.Layout)
  userPropertiesLayout: cc.Layout = null;

  @property(cc.Layout)
  channelPropertiesContent: cc.Layout = null;

  @property(cc.Layout)
  userPropertiesContent: cc.Layout = null;

  @property(cc.Layout)
  messageChannelIdsContent: cc.Layout = null;

  @property(cc.Layout)
  channelPropChannelIdsContent: cc.Layout = null;

  @property(cc.Layout)
  userPropChannelIdsContent: cc.Layout = null;

  @property(cc.Button)
  messageSelect: cc.Button = null;

  @property(cc.Button)
  channelPropSelect: cc.Button = null;

  @property(cc.Button)
  userPropSelect: cc.Button = null;

  @property(cc.ScrollView)
  messageScrollView: cc.ScrollView = null;

  @property(cc.ScrollView)
  channelPropScrollView: cc.ScrollView = null;

  @property(cc.ScrollView)
  userPropScrollView: cc.ScrollView = null;

  @property(cc.Button)
  searchLoginUsers: cc.Button = null;

  @property(cc.Button)
  setChannelPropBtn: cc.Button = null;

  @property(cc.Button)
  setUserPropBtn: cc.Button = null;

  @property(cc.Button)
  searchUserPropBtn: cc.Button = null;

  @property(cc.Prefab)
  setPropertiesDialogPrefab: cc.Prefab = null;

  @property(cc.Prefab)
  dialogPrefab: cc.Prefab = null;

  @property(cc.EditBox)
  days: cc.EditBox = null;

  @property(cc.EditBox)
  count: cc.EditBox = null;

  @property(cc.Button)
  deleteAllUserPropBtn: cc.Button = null;

  @property(cc.Button)
  deleteAllChannelPropBtn: cc.Button = null;

  @property(cc.Button)
  clearAppLogsBtn: cc.Button = null;

  protected update(dt: number) {
    this.send.interactable = this.channelName.string.length > 0 && regex.test(this.channelName.string);
    this.sendBinary.interactable = this.channelName.string.length > 0 && regex.test(this.channelName.string);
    this.subscribe.interactable = this.channelName.string.length > 0 && regex.test(this.channelName.string);
    this.unSubscribe.interactable = this.curMessageChannelId.string.length > 0 && regex.test(this.curMessageChannelId.string);
  }

  start() {
    this.refreshRTMLogItems();
    this.initListener();
    this.initView();
  }

  private initView() {
    this.userName.string = GlobalData.openId;
    this.curMessageChannelId.string = GlobalData.channelIdTemp;
    this.send.interactable = false;
    this.sendBinary.interactable = false;
    this.messageTab.interactable = true;
    this.channelPropertiesTab.interactable = false;
    this.userPropertiesTab.interactable = false;
    this.messageLayout.node.active = true;
    this.channelPropertiesLayout.node.active = false;
    this.userPropertiesLayout.node.active = false;

    // 设置dialog
    const dialogNode = cc.instantiate(this.dialogPrefab) as cc.Node;
    dialogNode.parent = this.node;

    // 设置频道和用户属性dialog
    const setPropDialogNode = cc.instantiate(this.setPropertiesDialogPrefab) as cc.Node;
    setPropDialogNode.parent = this.node;
  }

  private initListener() {
    this.back.node.on(cc.Node.EventType.TOUCH_START, () => this.backHall());
    this.subscribe.node.on(cc.Node.EventType.TOUCH_START, () => this.subscribeChannel());
    this.unSubscribe.node.on(cc.Node.EventType.TOUCH_START, () => this.unSubscribeChannel());
    this.send.node.on(cc.Node.EventType.TOUCH_START, () => this.sendChannelContents(MessageType.TEXT));
    this.sendBinary.node.on(cc.Node.EventType.TOUCH_START, () => this.sendChannelContents(MessageType.BINARY));

    // 显示频道列表下拉框
    this.messageSelect.node.on(cc.Node.EventType.TOUCH_START, () => this.showScrollView(SelectTabType.MESSAGE));
    this.channelPropSelect.node.on(cc.Node.EventType.TOUCH_START, () => this.showScrollView(SelectTabType.CHANNEL_PROPERTIES));
    this.userPropSelect.node.on(cc.Node.EventType.TOUCH_START, () => this.showScrollView(SelectTabType.USER_PROPERTIES));

    // 切换标签页
    this.messageTab.node.on(cc.Node.EventType.TOUCH_START, () => this.handleSelectTab(SelectTabType.MESSAGE));
    this.channelPropertiesTab.node.on(cc.Node.EventType.TOUCH_START, () => this.handleSelectTab(SelectTabType.CHANNEL_PROPERTIES));
    this.userPropertiesTab.node.on(cc.Node.EventType.TOUCH_START, () => this.handleSelectTab(SelectTabType.USER_PROPERTIES));

    // 查询登录用户
    this.searchLoginUsers.node.on(cc.Node.EventType.TOUCH_START, () => this.queryLoginUsers());

    // 设置频道和用户属性按钮
    this.setChannelPropBtn.node.on(cc.Node.EventType.TOUCH_START, () => this.setCustomProperties(PropertiesType.CHANNEL));
    this.setUserPropBtn.node.on(cc.Node.EventType.TOUCH_START, () => this.setCustomProperties(PropertiesType.USER));

    // 查询用户属性按钮
    this.searchUserPropBtn.node.on(cc.Node.EventType.TOUCH_START, () =>
      this.queryUserProperties(this.curUserPropChannelId.string, this.userPropUserName.string)
    );

    this.deleteAllUserPropBtn.node.on(cc.Node.EventType.TOUCH_START, () => this.deleteAllUserProperties());
    this.deleteAllChannelPropBtn.node.on(cc.Node.EventType.TOUCH_START, () => this.deleteAllChannelProperties());

    // 清屏
    this.clearAppLogsBtn.node.on(cc.Node.EventType.TOUCH_START, () => this.clearAllLogs());

    // 回调监听
    // ===== 13.7.5 =====
    GameMediaEngine.on('onSubscribeRtmChannel', (result: SubscribeRtmChannelResult) =>
      this.onSubscribeRtmChannel(result)
    );
    GameMediaEngine.on('onUnSubscribeRtmChannel', (result: UnSubscribeRtmChannelResult) =>
      this.onUnSubscribeRtmChannel(result)
    );
    GameMediaEngine.on('onPublishRtmChannelMessage', (result: PublishRtmChannelMessageResult) =>
      this.onPublishRtmChannelMessage(result)
    );
    GameMediaEngine.on('onReceiveRtmChannelMessage', (notify: ReceiveRtmChannelMessageNotify) =>
      this.onReceiveRtmChannelMessage(notify)
    );
    GameMediaEngine.on('onGetRtmChannelInfo', (result: GetRtmChannelInfoResult) => this.onGetRtmChannelInfo(result));

    // ===== 13.8.1 =====
    GameMediaEngine.on('onGetRtmChannelHistoryMessages', (result: GetRtmChannelHistoryMessagesResult) =>
      this.onGetRtmChannelHistoryMessage(result)
    );
    GameMediaEngine.on('onGetRtmChannelPlayerProperties', (result: GetRtmChannelPlayerPropertiesResult) =>
        this.onGetRtmChannelPlayerProperty(result)
    );
    GameMediaEngine.on('onGetRtmChannelProperties', (result: GetRtmChannelPropertiesResult) =>
        this.onGetRtmChannelProperty(result)
    );
    GameMediaEngine.on('onRtmChannelPlayerPropertiesChanged', (notify: RtmChannelPlayerPropertiesNotify) =>
        this.onRtmChannelPlayerPropertiesChange(notify)
    );
    GameMediaEngine.on('onRtmChannelPropertiesChanged', (notify: RtmChannelPropertiesNotify) =>
        this.onRtmChannelPropertiesChange(notify)
    );
    GameMediaEngine.on('onSetRtmChannelPlayerProperties', (result: SetRtmChannelPlayerPropertiesResult) =>
        this.onSetRtmChannelPlayerProperty(result)
    );
    GameMediaEngine.on('onSetRtmChannelProperties', (result: SetRtmChannelPropertiesResult) =>
        this.onSetRtmChannelProperty(result)
    );

    // 定时任务
    this.schedule(this.onScheduleEvent, 1, cc.macro.REPEAT_FOREVER, 0);
  }

  // 清屏
  private clearAllLogs() {
    GlobalData.rtmChannelContentItems = [];
    GlobalData.channelContentMapTemp.clear();
    this.refreshRTMLogItems();
  }

  // 订阅频道
  private subscribeChannel() {
    if (this.subscribe.interactable) {
      const req: SubscribeRtmChannelReq = {
        channelId: this.channelName.string,
        playerProperties: {}
      };
      GlobalData.gameMediaEngine.subscribeRtmChannel(req).catch((error) => {
        const content = '订阅 code：' + error.code + ', msg：' + error.message;
        this.printRtmChannelContent(content);
      });
    }
  }

  // 退订频道
  private unSubscribeChannel() {
    if (this.unSubscribe.interactable) {
      const req: UnSubscribeRtmChannelReq = {
        channelId: this.curMessageChannelId.string,
      };
      GlobalData.gameMediaEngine.unSubscribeRtmChannel(req).catch((error) => {
        const content = '退订 code：' + error.code + ', msg：' + error.message;
        this.printRtmChannelContent(content);
      });
    }
  }

  // 查询已登录用户
  private queryLoginUsers() {
    if (this.curMessageChannelId.string) {
      GlobalData.gameMediaEngine.getRtmChannelInfo({
        channelId: this.curMessageChannelId.string,
        isReturnMembers: true,
      });
    }
  }

  // 发送频道内容
  private sendChannelContents(messageType: number) {
    if (this.send.interactable) {
      const messageTypeStr = messageType === MessageType.BINARY ? '二进制' : '文本';
      const req: PublishRtmChannelMessageReq = {
        channelId: this.curMessageChannelId.string,
        messageType: messageType,
        message:
          messageType === MessageType.TEXT
            ? this.sendChannelContent.string
            : Utils.textToUint8Array(this.sendChannelContent.string),
        receivers: this.receiveUsers.string
            ? this.receiveUsers.string.replace(/，/g, ",").split(',')
            : [],
        isAllowCacheMsg: this.isAllowCacheMsg,
        isContentIdentify: this.isContentIdentify,
        isAdsIdentify: this.isAdsIdentify,
      };
      this.sendChannelContent.string = '';
      GlobalData.gameMediaEngine
        .publishRtmChannelMessage(req)
        .then((seq) => {
          rtmChannelContentMap.set(seq, req.message);
        })
        .catch((error) => {
          const content = '频道消息 (' + messageTypeStr + ')：code：' + error.code + ', msg：' + error.message;
          this.printRtmChannelContent(content);
        });
    }
  }

  // 查询历史消息
  private queryHistoryMsg() {
    const req: GetRtmChannelHistoryMessagesReq = {
      channelId: GlobalData.channelIdTemp,
      startTime: Utils.getCurDateTimeBeforeDays(Number(this.days.string)),
      count: Number(this.count.string)
    };
    GlobalData.gameMediaEngine.getRtmChannelHistoryMessages(req).catch((error) => {
      const content = '查询历史消息 code：' + error.code + ', msg：' + error.message;
      this.printRtmChannelContent(content);
    });
  }

  // 查询频道属性
  private queryChannelProperties(channelId: string) {
    if (channelId) {
      const req: GetRtmChannelPropertiesReq = {
        channelId: channelId
      };
      GlobalData.gameMediaEngine.getRtmChannelProperties(req).catch((error) => {
        const content = '查询频道属性 code：' + error.code + ', msg：' + error.message;
        this.printRtmChannelContent(content);
      });
    } else {
      GlobalData.channelCustomPropertiesItems = [];
      GlobalData.cacheChannelCustomPropertiesItems = [];
      this.refreshCustomPropertiesItems(PropertiesType.CHANNEL);
    }
  }

  // 查询用户属性
  private queryUserProperties(channelId: string, openId: string) {
    if (channelId && openId) {
      const req: GetRtmChannelPlayerPropertiesReq = {
        channelId: channelId,
        openIds: [openId]
      };
      GlobalData.gameMediaEngine.getRtmChannelPlayerProperties(req).catch((error) => {
        const content = '查询频道属性 code：' + error.code + ', msg：' + error.message;
        this.printRtmChannelContent(content);
      });
    } else {
      GlobalData.userCustomPropertiesItems = [];
      GlobalData.cacheUserCustomPropertiesItems = [];
      this.refreshCustomPropertiesItems(PropertiesType.USER);
    }
  }

  // 设置用户属性
  private setRtmChannelPlayerProperty() {
    let playerPropertyList = {};
    GlobalData.cacheUserCustomPropertiesItems.forEach(userCustomProperty => {
      playerPropertyList[userCustomProperty.key] = userCustomProperty.value;
    });
    const req: SetRtmChannelPlayerPropertiesReq = {
      channelId: GlobalData.channelIdTemp,
      playerProperties: playerPropertyList
    };
    GlobalData.gameMediaEngine.setRtmChannelPlayerProperties(req).catch((error) => {
      const content = '设置玩家属性 code：' + error.code + ', msg：' + error.message;
      this.printRtmChannelContent(content);
    });
  }

  // 设置频道属性
  private setRtmChannelProperty() {
    let channelPropertyList = {};
    GlobalData.cacheChannelCustomPropertiesItems.forEach(channelCustomProperty => {
      channelPropertyList[channelCustomProperty.key] = channelCustomProperty.value;
    });
    const req: SetRtmChannelPropertiesReq = {
      channelId: GlobalData.channelIdTemp,
      channelProperties: channelPropertyList
    };
    GlobalData.gameMediaEngine.setRtmChannelProperties(req).catch((error) => {
      const content = '设置频道属性 code：' + error.code + ', msg：' + error.message;
      this.printRtmChannelContent(content);
    });
  }

  private deleteAllUserProperties() {
    let userKeys = [];
    GlobalData.userCustomPropertiesItems.forEach(userCustomProp => {
      userKeys.push(userCustomProp.key);
    });
    const req: DeleteRtmChannelPlayerPropertiesReq = {
      channelId: GlobalData.channelIdTemp,
      keys: userKeys,
    }
    GlobalData.gameMediaEngine.deleteRtmChannelPlayerProperties(req);
  }

  private deleteAllChannelProperties() {
    let channelKeys = [];
    GlobalData.channelCustomPropertiesItems.forEach(channelCustomProp => {
      channelKeys.push(channelCustomProp.key);
    });
    const req: DeleteRtmChannelPropertiesReq = {
      channelId: GlobalData.channelIdTemp,
      keys: channelKeys,
    }
    GlobalData.gameMediaEngine.deleteRtmChannelProperties(req);
  }

  private backHall() {
    cc.director.loadScene('FunctionHall');
  }

  // 多选框处理
  handleCheckBtn(event, value) {
    switch (value) {
      case CheckType.IS_CONTENT_IDENTIFY.toString():
        this.isContentIdentify = event.isChecked;
        break;
      case CheckType.IS_ADS_IDENTIFY.toString():
        this.isAdsIdentify = event.isChecked;
        break;
      case CheckType.IS_ALLOW_CACHE_MSG.toString():
        this.isAllowCacheMsg = event.isChecked;
        break;
    }
  }

  private setCustomProperties(propertiesType: number) {
    SetCustomProperties.open(propertiesType, () => {
      switch (propertiesType) {
        case PropertiesType.CHANNEL:
          this.setRtmChannelProperty();
          break;
        case PropertiesType.USER:
          this.setRtmChannelPlayerProperty();
          break;
      }
    }, () => {
      SetCustomProperties.close();
    });
  }

  private showScrollView(selectType: number) {
    this.messageScrollView.node.active = selectType === SelectTabType.MESSAGE && !messageIsShowScrollView;
    this.channelPropScrollView.node.active = selectType === SelectTabType.CHANNEL_PROPERTIES && !channelPropIsShowScrollView;
    this.userPropScrollView.node.active = selectType === SelectTabType.USER_PROPERTIES && !userPropIsShowScrollView;
    messageIsShowScrollView = selectType === SelectTabType.MESSAGE && !messageIsShowScrollView;
    channelPropIsShowScrollView = selectType === SelectTabType.CHANNEL_PROPERTIES && !channelPropIsShowScrollView;
    userPropIsShowScrollView = selectType === SelectTabType.USER_PROPERTIES && !userPropIsShowScrollView;
  }

  // 切换标签页做的处理
  private handleSelectTab(selectType: number) {
    if (!this.channelName.string) {
      this.loginUsers.string = '';
    }
    this.curMessageChannelId.string = GlobalData.channelIdTemp;
    this.curChannelPropChannelId.string = GlobalData.channelIdTemp;
    this.curUserPropChannelId.string = GlobalData.channelIdTemp;

    this.messageTab.interactable = selectType === SelectTabType.MESSAGE;
    this.channelPropertiesTab.interactable = selectType === SelectTabType.CHANNEL_PROPERTIES;
    this.userPropertiesTab.interactable = selectType === SelectTabType.USER_PROPERTIES;
    this.messageLayout.node.active = selectType === SelectTabType.MESSAGE;
    this.channelPropertiesLayout.node.active = selectType === SelectTabType.CHANNEL_PROPERTIES;
    this.userPropertiesLayout.node.active = selectType === SelectTabType.USER_PROPERTIES;

    switch (selectType) {
      case SelectTabType.CHANNEL_PROPERTIES:
        this.queryChannelProperties(GlobalData.channelIdTemp);
        break;
      case SelectTabType.USER_PROPERTIES:
        this.queryUserProperties(GlobalData.channelIdTemp, GlobalData.openId);
        break;
    }
  }

  // ========== 回调方法 ==========
  // 订阅频道回调
  private onSubscribeRtmChannel(result: SubscribeRtmChannelResult) {
    if (result.code == 0) {
      const content = '订阅：' + GlobalData.openId + '订阅了频道' + result.channelId;
      this.curMessageChannelId.string = result.channelId;
      GlobalData.channelIdTemp = result.channelId;
      this.printRtmChannelContent(content);
      // 订阅成功进行历史消息的查询
      this.queryHistoryMsg();
    } else {
      const content = '订阅 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 退订频道回调
  private onUnSubscribeRtmChannel(result: UnSubscribeRtmChannelResult) {
    if (result.code == 0) {
      GlobalData.channelIdTemp = "";
      this.curMessageChannelId.string = "";
      if (GlobalData.receiverChannelsItems.length > 1) {
        for (const data of GlobalData.receiverChannelsItems)  {
          if(data.data !== result.channelId) {
            GlobalData.channelIdTemp = data.data;
            this.curMessageChannelId.string = data.data;
            break;
          }
        }
      }
      const content = '退订：' + GlobalData.openId + '退订了频道' + result.channelId;
      this.printRtmChannelContent(content);
    } else {
      const content = '退订 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 发送给对端消息结果回调
  private onPublishRtmChannelMessage(result: PublishRtmChannelMessageResult) {
    let message = rtmChannelContentMap.get(result.clientMsgId);
    const messageType = message instanceof Uint8Array ? '二进制' : '文本';
    if (result.code == 0) {
      if (message instanceof Uint8Array) {
        message = Utils.Uint8ArrayToText(message);
      }
      const content =
        '频道消息 (' +
        messageType +
        ')：' +
        GlobalData.openId +
        '发送到频道' +
        result.channelId +
        '的消息：' +
        Utils.strReplace(message, 15, 5, '...');
      this.printRtmChannelContent(content);
    } else {
      const content = '频道消息 (' + messageType + ')：code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 接受频道消息结果回调
  private onReceiveRtmChannelMessage(notify: ReceiveRtmChannelMessageNotify) {
    let message = notify.message;
    const messageType = notify.messageType === MessageType.BINARY ? '二进制' : '文本';
    if (notify.messageType === MessageType.BINARY) {
      message = Utils.Uint8ArrayToText(message as Uint8Array);
    }
    let sender = notify.isFromOpenApi ? "系统消息" : notify.senderId;
    const content =
      '频道消息 (' +
      messageType +
      ')：' +
      GlobalData.openId +
      '收到' +
      sender +
      '发送到频道' +
      notify.channelId +
      '的消息：' +
      Utils.strReplace(message as string, 15, 5, '...');
    if (notify.channelId === this.curMessageChannelId.string) {
      this.printRtmChannelContent(content);
    } else {
      // 处理不在同一个频道的内容
      this.handleChannelContent(content, notify.channelId);
    }
  }

  // 接收历史消息回调
  private onGetRtmChannelHistoryMessage(result: GetRtmChannelHistoryMessagesResult) {
    if (result.code == 0) {
      let messages = result.channelMessages;
      for (let i = messages.length - 1; i >= 0 ;i--) {
        let notify = messages[i];
        let message = notify.message;
        const messageType = notify.messageType === MessageType.BINARY ? '二进制' : '文本';
        if (notify.messageType === MessageType.BINARY) {
          message = Utils.Uint8ArrayToText(message as Uint8Array);
        }
        let sender = notify.isFromOpenApi ? "系统消息" : notify.senderId;
        const content =
          '历史消息 (' +
          messageType +
          ')：' +
          GlobalData.openId +
          '收到' +
          sender +
          '发送到频道' +
          result.channelId +
          '的消息：' +
          Utils.strReplace(message as string, 15, 5, '...') +
          '，发送时间：' + Utils.getDateTime(notify.timestamp);
        this.printRtmChannelContent(content);
      }
    } else {
      const content = '历史消息 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  };

  // 接收查询频道玩家属性回调
  private onGetRtmChannelPlayerProperty(result: GetRtmChannelPlayerPropertiesResult) {
    if (result.code == 0) {
      let memberInfos = result.memberInfos;
      if (memberInfos.length > 0) {
        for (const memberInfo of memberInfos)  {
          let content = "查询用户" + memberInfo.openId + "的属性："
            + (memberInfo.playerProperties ? JSON.stringify(memberInfo.playerProperties) : "{}");
          this.printRtmChannelContent(content);
          // 当前玩家
          if (memberInfo.openId === GlobalData.openId) {
            GlobalData.userCustomPropertiesItems = [];
            this.handlePlayerPropertiesItem(memberInfo.playerProperties, true);
            this.refreshCustomPropertiesItems(PropertiesType.USER);
            break;
          }
        }
      } else {
        let content = "查询用户" + this.userPropUserName.string + "的属性：{}";
        this.printRtmChannelContent(content);
      }
    } else {
      const content = '查询用户' + this.userPropUserName.string + '的属性 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 接收查询频道属性回调
  private onGetRtmChannelProperty(result: GetRtmChannelPropertiesResult) {
    if (result.code == 0) {
      // 当前频道
      if (GlobalData.channelIdTemp === result.channelId) {
        GlobalData.channelCustomPropertiesItems = [];
        this.handleChannelPropertiesItem(result.channelProperties, true);
        this.refreshCustomPropertiesItems(PropertiesType.CHANNEL);
      }
      let content = "查询频道" + result.channelId + "的属性：" +
        (result.channelProperties ? JSON.stringify(result.channelProperties) : "{}");
      this.printRtmChannelContent(content);
    } else {
      const content = '查询频道' + result.channelId + '的属性 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 接收频道内玩家属性变更通知回调
  private onRtmChannelPlayerPropertiesChange(notify: RtmChannelPlayerPropertiesNotify) {
    let playerInfo = notify.playerInfo;
    this.handlePlayerPropertiesItem(playerInfo.playerProperties, false);
    let content = "玩家" + playerInfo.openId + "的属性状态通知： 属性变更为：" +
      (playerInfo.playerProperties ? JSON.stringify(playerInfo.playerProperties) : "{}");
    this.printRtmChannelContent(content);
  }

  // 接收频道属性变更通知回调
  private onRtmChannelPropertiesChange(notify: RtmChannelPropertiesNotify) {
    this.handleChannelPropertiesItem(notify.channelProperties, false);
    let content = "频道" + notify.channelId + "的属性状态通知： 属性变更为：" +
      (notify.channelProperties ? JSON.stringify(notify.channelProperties) : "{}");
    // 设置成功刷新页面
    this.queryChannelProperties(notify.channelId);
    this.printRtmChannelContent(content);
  }

  // 接收玩家属性状态变更回调
  private onSetRtmChannelPlayerProperty(result: SetRtmChannelPlayerPropertiesResult) {
    if (result.code == 0) {
      let content = "玩家" + GlobalData.openId + "的属性状态变更： 属性变更成功";
      this.printRtmChannelContent(content);
      // 设置成功刷新页面
      this.queryUserProperties(result.channelId, GlobalData.openId);
    } else {
      const content = '玩家属性状态通知 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 接收频道属性状态通知回调
  private onSetRtmChannelProperty(result: SetRtmChannelPropertiesResult) {
    if (result.code == 0) {
      let content = "频道" + result.channelId + "的属性状态通知： 属性变更成功";
      this.printRtmChannelContent(content);
      // 设置成功刷新页面
      this.queryChannelProperties(result.channelId);
    } else {
      const content = '频道' + result.channelId + '的属性状态通知 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 接收已登录用户列表回调
  private onGetRtmChannelInfo(result: GetRtmChannelInfoResult) {
    const members: Array<RtmChannelMemberInfo> = result.memberInfos;
    let loginUsersStr = '';
    let content = '查询频道' + result.channelId + '的信息，频道玩家数：' + result.memberCount + '，玩家状态：[';
    members.forEach((member) => {
      const playerStatus = member.status === ConnectionStatus.CONNECT ? '在线' : '离线';
      let appendStr = member.openId + '(' + playerStatus + ')' + '，'
      loginUsersStr += appendStr;
      content += appendStr + '属性：' + (member.playerProperties ? JSON.stringify(member.playerProperties) : "{}") + '、';
    });
    this.loginUsers.string = loginUsersStr.substring(0, loginUsersStr.length - 1);
    this.printRtmChannelContent(content.substring(0, content.length - 1) + "]");
  }

  // ========== 定时任务回调方法 ==========
  private onScheduleEvent() {
    // 查询已创建频道
    GlobalData.gameMediaEngine.getRtmSubscribedChannelInfo().then((result) => {
      let channelIds = result.channelIds;
      GlobalData.receiverChannelsItems = [];
      channelIds.forEach(channelId => {
        const receiverChannelScrollItem = new ReceiverChannelScrollItem();
        receiverChannelScrollItem.init(channelId);
        GlobalData.receiverChannelsItems.push(receiverChannelScrollItem);
      });
      this.refreshChannelItems();
    });
  }

  // ========== 事件监听 ==========
  protected onEnable() {
    // 处理下拉框点击之后的事件处理
    this.handleSelectedChannelEvent();

    // 处理刷新属性列表
    this.handleRefreshPropertiesEvent();
  }

  // 处理刷新属性列表
  private handleRefreshPropertiesEvent() {
    this.node.on(Constant.refreshPropertiesEvent, function (event) {
      let userData = event.getUserData();
      let code = userData.code;
      let msg = userData.msg;
      let channelId = userData.channelId;
      if (code !== 0) {
        const content = '删除属性 code：' + code + ', msg：' + msg;
        this.printRtmChannelContent(content);
      } else {
        let propertiesType = userData.propertiesType;
        switch (propertiesType) {
          case PropertiesType.CHANNEL:
            this.queryChannelProperties(channelId);
            break;
          case PropertiesType.USER:
            this.queryUserProperties(channelId, GlobalData.openId);
            break;
        }
      }
    }, this);
  }

  // 处理下拉框点击之后的事件处理
  private handleSelectedChannelEvent() {
    this.node.on(Constant.selectedChannelEvent, function (event) {
      let userData = event.getUserData();
      let selectType = userData.selectType;
      let channelId = userData.channelId;
      GlobalData.channelIdTemp = channelId;
      this.curMessageChannelId.string = channelId;
      this.curChannelPropChannelId.string = channelId;
      this.curUserPropChannelId.string = channelId;
      switch (selectType) {
        case SelectTabType.CHANNEL_PROPERTIES:
          channelPropIsShowScrollView = false;
          this.channelPropScrollView.node.active = false;
          this.queryChannelProperties(channelId);
          break;
        case SelectTabType.USER_PROPERTIES:
          userPropIsShowScrollView = false;
          this.userPropScrollView.node.active = false;
          this.queryUserProperties(channelId, GlobalData.openId);
          break;
        case SelectTabType.MESSAGE:
          messageIsShowScrollView = false;
          this.messageScrollView.node.active = false;
          break;
      }
      // 刷新控制台日志
      GlobalData.rtmChannelContentItems = GlobalData.channelContentMapTemp.get(channelId);
      this.refreshRTMLogItems();
    }, this);
  }

  private handlePlayerPropertiesItem(playerProperties: { [k: string]: string; } | null, isCurPlayer: boolean) {
    for (let key in playerProperties) {
      const playerContentsItem = new CustomPropertiesScrollItem();
      playerContentsItem.init(Utils.random(), key, playerProperties[key]);
      if (isCurPlayer) {
        GlobalData.userCustomPropertiesItems.push(playerContentsItem);
      }
    }
  }

  private handleChannelPropertiesItem(channelProperties: { [k: string]: string; } | null, isCurChannel: boolean) {
    for (let key in channelProperties) {
      const channelContentsItem = new CustomPropertiesScrollItem();
      channelContentsItem.init(Utils.random(), key, channelProperties[key]);
      if (isCurChannel) {
        GlobalData.channelCustomPropertiesItems.push(channelContentsItem);
      }
    }
  }

  protected onDestroy() {
    channelUsersMap.clear();
    rtmChannelContentMap.clear();
  }

  protected onDisable() {
    this.node.off(Constant.selectedChannelEvent);
  }

  // 刷新下拉框中的频道数据
  private refreshChannelItems() {
    const messageChannelIdsItemList = this.messageChannelIdsContent.getComponent('ReceiverChannelsItemList');
    messageChannelIdsItemList.fresh(SelectTabType.MESSAGE);

    const channelPropChannelIdsItemList = this.channelPropChannelIdsContent.getComponent('ReceiverChannelsItemList');
    channelPropChannelIdsItemList.fresh(SelectTabType.CHANNEL_PROPERTIES);

    const userPropChannelIdsItemList = this.userPropChannelIdsContent.getComponent('ReceiverChannelsItemList');
    userPropChannelIdsItemList.fresh(SelectTabType.USER_PROPERTIES);
  }

  // 刷新自定义属性数据
  private refreshCustomPropertiesItems(propertiesType: number) {
    let customPropertiesItemList;
    switch (propertiesType) {
      case PropertiesType.CHANNEL:
        customPropertiesItemList = this.channelPropertiesContent.getComponent('CustomPropertiesItemList');
        break;
      case PropertiesType.USER:
        customPropertiesItemList = this.userPropertiesContent.getComponent('CustomPropertiesItemList');
        break;
    }
    customPropertiesItemList.fresh(propertiesType);
  }

  // 刷新频道消息日志
  private refreshRTMLogItems() {
    const itemList = this.content.getComponent('ItemList');
    itemList.fresh(LogType.RTM_CHANNEL_CONTENT_TYPE);
  }

  // 打印频道信息内容
  private printRtmChannelContent(content: string) {
    GlobalData.rtmChannelContentItems = this.handleChannelContent(content, this.curMessageChannelId.string);
    this.refreshRTMLogItems();
  }

  // 处理的内容
  private handleChannelContent(content: string, channelId: string) {
    const channelContentsItem = new LogScrollItem();
    channelContentsItem.init(Utils.getCurrentDateTime() + ' ' + content);
    let logsItem: LogScrollItem[] = GlobalData.channelContentMapTemp.get(channelId);
    if (!logsItem) {
      logsItem = [];
    }
    logsItem.push(channelContentsItem);
    GlobalData.channelContentMapTemp.set(channelId, logsItem);
    return logsItem;
  }
}
