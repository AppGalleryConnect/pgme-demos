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

import { LogType, MessageType } from '../Function/Enum';
import LogScrollItem from '../Function/LogScrollItem';
import GlobalData from '../../GlobalData';
import {
  GameMediaEngine,
  MemberInfo,
  ObtainChannelInfoResult,
  PublishRtmChannelMessageReq,
  PublishRtmChannelMessageResult,
  ReceiveRtmChannelMessageNotify,
  SubscribeRtmChannelReq,
  SubscribeRtmChannelResult,
  UnSubscribeRtmChannelReq,
  UnSubscribeRtmChannelResult,
} from '../../GMME/GMMEForMiniGames';
import Utils from '../Function/Utils';

const { ccclass, property } = cc._decorator;
// 指定频道接收用户
const channelUsersMap: Map<string, string[]> = new Map<string, string[]>();
const regex = /^[a-zA-Z0-9_]+$/;
const rtmChannelContentMap = new Map<string, string | Uint8Array>();

@ccclass
export default class Channel extends cc.Component {
  @property(cc.EditBox)
  sendChannelContent: cc.EditBox = null;

  @property(cc.Button)
  send: cc.Button = null;

  @property(cc.Button)
  sendBinary: cc.Button = null;

  @property(cc.Button)
  back: cc.Button = null;

  @property(cc.Button)
  changeChannel: cc.Button = null;

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

  @property(cc.Label)
  channels: cc.Label = null;

  @property(cc.Label)
  receiverChannelName: cc.Label = null;

  @property(cc.Label)
  senderUserName: cc.Label = null;

  protected update(dt: number) {
    this.send.interactable = this.sendChannelContent.string.length > 0 && regex.test(this.receiverChannelName.string);
    this.sendBinary.interactable = this.sendChannelContent.string.length > 0 && regex.test(this.receiverChannelName.string);
    this.subscribe.interactable = this.channelName.string.length > 0 && regex.test(this.channelName.string);
    this.unSubscribe.interactable = this.channelName.string.length > 0 && regex.test(this.channelName.string);
  }

  start() {
    this.refreshRTMLogItems();
    this.initListener();
    this.initView();
  }

  protected onDestroy() {
    channelUsersMap.clear();
    rtmChannelContentMap.clear();
  }

  private initView() {
    this.userName.string = GlobalData.openId;
    this.senderUserName.string = GlobalData.openId;
    this.send.interactable = false;
    this.sendBinary.interactable = false;
    this.receiverChannelName.string = GlobalData.channelIdTemp;
  }

  private initListener() {
    this.send.node.on(cc.Node.EventType.TOUCH_START, () => this.sendChannelContents(MessageType.TEXT));
    this.sendBinary.node.on(cc.Node.EventType.TOUCH_START, () => this.sendChannelContents(MessageType.BINARY));
    this.changeChannel.node.on(cc.Node.EventType.TOUCH_START, () => this.switchChannel());
    this.back.node.on(cc.Node.EventType.TOUCH_START, () => this.backHall());

    this.subscribe.node.on(cc.Node.EventType.TOUCH_START, () => this.subscribeChannel());
    this.unSubscribe.node.on(cc.Node.EventType.TOUCH_START, () => this.unSubscribeChannel());

    // 回调监听
    GameMediaEngine.on('onSubscribeRtmChannel', (result: SubscribeRtmChannelResult) =>
      this.onSubscribeRtmChannel(result),
    );
    GameMediaEngine.on('onUnSubscribeRtmChannel', (result: UnSubscribeRtmChannelResult) =>
      this.onUnSubscribeRtmChannel(result),
    );
    GameMediaEngine.on('onPublishRtmChannelMessage', (result: PublishRtmChannelMessageResult) =>
      this.onPublishRtmChannelMessage(result),
    );
    GameMediaEngine.on('onReceiveRtmChannelMessage', (notify: ReceiveRtmChannelMessageNotify) =>
      this.onReceiveRtmChannelMessage(notify),
    );
    GameMediaEngine.on('onObtainChannelInfo', (result: ObtainChannelInfoResult) => this.onObtainRtmChannelInfo(result));

    // 定时任务
    this.schedule(this.onScheduleEvent, 5, cc.macro.REPEAT_FOREVER, 0);
  }

  private subscribeChannel() {
    // 订阅频道
    if (this.subscribe.interactable) {
      const req: SubscribeRtmChannelReq = {
        channelId: this.channelName.string,
      };
      GlobalData.gameMediaEngine.subscribeRtmChannel(req).catch((error) => {
        const content = '订阅 code：' + error.code + ', msg：' + error.message;
        this.printRtmChannelContent(content);
      });
    }
  }

  private unSubscribeChannel() {
    // 退订频道
    if (this.unSubscribe.interactable) {
      const req: UnSubscribeRtmChannelReq = {
        channelId: this.channelName.string,
      };
      GlobalData.gameMediaEngine.unSubscribeRtmChannel(req).catch((error) => {
        const content = '退订 code：' + error.code + ', msg：' + error.message;
        this.printRtmChannelContent(content);
      });
    }
  }

  private backHall() {
    cc.director.loadScene('FunctionHall');
  }

  // 切换频道
  private switchChannel() {
    if (!this.channelName.string) {
      this.loginUsers.string = '';
    }
    this.receiverChannelName.string = this.channelName.string;
    GlobalData.channelIdTemp = this.channelName.string;
    GlobalData.rtmChannelContentItems = GlobalData.channelContentMapTemp.get(this.receiverChannelName.string);
    this.refreshRTMLogItems();
  }

  private refreshRTMLogItems() {
    const itemList = this.content.getComponent('ItemList');
    itemList.fresh(LogType.RTM_CHANNEL_CONTENT_TYPE);
  }

  private sendChannelContents(messageType: number) {
    // 发送频道内容
    if (this.send.interactable) {
      const messageTypeStr = messageType === MessageType.BINARY ? '二进制' : '文本';
      const req: PublishRtmChannelMessageReq = {
        channelId: this.receiverChannelName.string,
        messageType: messageType,
        message:
          messageType === MessageType.TEXT
            ? this.sendChannelContent.string
            : Utils.textToUint8Array(this.sendChannelContent.string),
        receivers: this.receiveUsers.string
            ? this.receiveUsers.string.replace(/，/g, ",").split(',')
            : [],
      };
      GlobalData.gameMediaEngine
        .publishRtmChannelMessage(req)
        .then((seq) => {
          rtmChannelContentMap.set(seq, req.message);
        })
        .catch((error) => {
          const content = '频道消息 code (' + messageTypeStr + ')：' + error.code + ', msg：' + error.message;
          this.printRtmChannelContent(content);
        });
    }
  }

  // ========== 回调方法 ==========
  // 订阅频道回调
  private onSubscribeRtmChannel(result: SubscribeRtmChannelResult) {
    if (result.code == 0) {
      const content = '订阅：' + GlobalData.openId + '订阅了频道' + result.channelId;
      this.receiverChannelName.string = result.channelId;
      GlobalData.channelIdTemp = result.channelId;
      this.printRtmChannelContent(content);
    } else {
      const content = '订阅 code：' + result.code + ', msg：' + result.msg;
      this.printRtmChannelContent(content);
    }
  }

  // 退订频道回调
  private onUnSubscribeRtmChannel(result: UnSubscribeRtmChannelResult) {
    if (result.code == 0) {
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
      this.sendChannelContent.string = '';
    } else {
      const content = '频道消息 code (' + messageType + ')：' + result.code + ', msg：' + result.msg;
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
    if (notify.channelId == this.receiverChannelName.string) {
      this.printRtmChannelContent(content);
    } else {
      // 处理不在同一个频道的内容
      this.handleChannelContent(content, notify.channelId);
    }
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

  // 接收已登录用户列表
  private onObtainRtmChannelInfo(result: ObtainChannelInfoResult) {
    const members: Array<MemberInfo> = result.memberInfos;
    let loginUsersStr = '';
    members.forEach((member) => {
      const playerStatus = member.status === 1 ? '在线' : '离线';
      loginUsersStr += member.openId + '(' + playerStatus + ')' + ',';
    });
    this.loginUsers.string = loginUsersStr.substring(0, loginUsersStr.length - 1);
  }

  // 打印频道信息内容
  private printRtmChannelContent(content: string) {
    GlobalData.rtmChannelContentItems = this.handleChannelContent(content, this.receiverChannelName.string);
    this.refreshRTMLogItems();
  }

  // 定时任务回调方法
  private onScheduleEvent() {
    // 查询已登录用户
    if (this.receiverChannelName.string) {
      GlobalData.gameMediaEngine.obtainChannelInfo({
        channelId: this.receiverChannelName.string,
        isReturnMembers: true,
      });
    }
    // 查询已创建频道
    GlobalData.gameMediaEngine.obtainSubscribedChannelInfoResult().then((result) => {
      this.channels.string = result.channelIds.join(',');
    });
  }
}
