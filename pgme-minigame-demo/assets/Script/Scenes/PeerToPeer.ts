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
import GlobalData from '../../GlobalData';
import { ItemList } from '../Comp/ScrollViewItemList/ItemList';
import LogScrollItem from '../Function/LogScrollItem';
import {
  GameMediaEngine,
  PublishRtmPeerMessageReq,
  PublishRtmPeerMessageResult,
  ReceiveRtmPeerMessageNotify,
} from '../../GMME/GMMEForMiniGames';
import Utils from '../Function/Utils';

const { ccclass, property } = cc._decorator;
const regex = /^[a-zA-Z0-9_]+$/;
const rtmP2PContentMap = new Map<string, string | Uint8Array>();

@ccclass
export default class PeerToPeer extends cc.Component {
  @property(cc.EditBox)
  receiverUserName: cc.EditBox = null;

  @property(cc.EditBox)
  sendP2PContent: cc.EditBox = null;

  @property(cc.Button)
  send: cc.Button = null;

  @property(cc.Button)
  sendBinary: cc.Button = null;

  @property(cc.Button)
  back: cc.Button = null;

  @property(cc.Layout)
  content: cc.Layout = null;

  @property(cc.Label)
  userName: cc.Label = null;

  protected update(dt: number) {
    this.send.interactable = this.sendP2PContent.string.length > 0 && regex.test(this.receiverUserName.string);
    this.sendBinary.interactable = this.sendP2PContent.string.length > 0 && regex.test(this.receiverUserName.string);
  }

  start() {
    this.refreshRTMLogItems();
    this.initListener();
    this.initView();
  }

  protected onDestroy() {
    rtmP2PContentMap.clear();
  }

  private initView() {
    this.userName.string = GlobalData.openId;
    this.send.interactable = false;
    this.sendBinary.interactable = false;
  }

  private initListener() {
    this.send.node.on(cc.Node.EventType.TOUCH_START, () => this.sendP2PContents(MessageType.TEXT));
    this.sendBinary.node.on(cc.Node.EventType.TOUCH_START, () => this.sendP2PContents(MessageType.BINARY));
    this.back.node.on(cc.Node.EventType.TOUCH_START, () => this.backHall());

    // 回调监听
    GameMediaEngine.on('onPublishRtmPeerMessage', (result: PublishRtmPeerMessageResult) =>
      this.onPublishRtmPeerMessage(result),
    );
    GameMediaEngine.on('onReceiveRtmPeerMessage', (notify: ReceiveRtmPeerMessageNotify) =>
      this.onReceiveRtmPeerMessage(notify),
    );
  }

  private backHall() {
    cc.director.loadScene('FunctionHall');
  }

  private refreshRTMLogItems() {
    const itemList = this.content.getComponent('ItemList');
    itemList.fresh(LogType.RTM_P2P_CONTENT_TYPE);
  }

  // 发送P2P内容
  private sendP2PContents(messageType: number) {
    if (this.send.interactable) {
      const messageTypeStr = messageType === MessageType.BINARY ? '二进制' : '文本';
      const req: PublishRtmPeerMessageReq = {
        peerId: this.receiverUserName.string,
        messageType: messageType,
        message:
          messageType === MessageType.TEXT
            ? this.sendP2PContent.string
            : Utils.textToUint8Array(this.sendP2PContent.string),
      };
      this.sendP2PContent.string = '';
      GlobalData.gameMediaEngine
        .publishRtmPeerMessage(req)
        .then((seq) => {
          rtmP2PContentMap.set(seq, req.message);
        })
        .catch((error) => {
          const content = '点对点消息 (' + messageTypeStr + ')：code：' + error.code + ', msg：' + error.message;
          this.printRtmp2pContent(content);
        });
    }
  }

  // ========== 回调方法 ==========
  // 发送给对端消息结果回调
  private onPublishRtmPeerMessage(result: PublishRtmPeerMessageResult) {
    let message = rtmP2PContentMap.get(result.clientMsgId);
    const messageType = message instanceof Uint8Array ? '二进制' : '文本';
    if (result.code == 0) {
      if (message instanceof Uint8Array) {
        message = Utils.Uint8ArrayToText(message);
      }
      const content =
        '点对点消息 (' +
        messageType +
        ')：' +
        GlobalData.openId +
        '发送的消息：' +
        Utils.strReplace(message, 15, 5, '...');
      this.printRtmp2pContent(content);
    } else {
      const content = '点对点消息 code (' + messageType + ')：' + result.code + ', msg：' + result.msg;
      this.printRtmp2pContent(content);
    }
  }

  // 接受p2p消息结果回调
  private onReceiveRtmPeerMessage(notify: ReceiveRtmPeerMessageNotify) {
    let message = notify.message;
    const messageType = notify.messageType === MessageType.BINARY ? '二进制' : '文本';
    if (notify.messageType === MessageType.BINARY) {
      message = Utils.Uint8ArrayToText(message as Uint8Array);
    }
    let sender = notify.isFromOpenApi ? "系统消息" : notify.senderId;
    const content =
      '点对点消息 (' +
      messageType +
      ')：' +
      GlobalData.openId +
      '收到' +
      sender +
      '的消息：' +
      Utils.strReplace(message as string, 15, 5, '...');
    this.printRtmp2pContent(content);
  }

  // 打印p2p信息内容
  private printRtmp2pContent(content: string) {
    const p2pContentsItem = new LogScrollItem();
    p2pContentsItem.init(Utils.getCurrentDateTime() + ' ' + content);
    GlobalData.rtmp2pContentItems.push(p2pContentsItem);
    this.refreshRTMLogItems();
  }
}
