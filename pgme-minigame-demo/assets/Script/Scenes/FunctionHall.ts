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

import GlobalData from '../../GlobalData';

const { ccclass, property } = cc._decorator;

@ccclass
export default class FunctionHall extends cc.Component {
  @property(cc.Button)
  audioMsgButton: cc.Button = null;

  @property(cc.Button)
  peerToPeerButton: cc.Button = null;

  @property(cc.Button)
  channelButton: cc.Button = null;

  @property(cc.Button)
  destroyEngine: cc.Button = null;

  start() {
    this.init();
  }

  private init() {
    this.initListener();
  }

  private initListener() {
    this.audioMsgButton.node.on(cc.Node.EventType.TOUCH_START, () => this.JumpToAudioMsgView());
    this.peerToPeerButton.node.on(cc.Node.EventType.TOUCH_START, () => this.JumpToP2PView());
    this.channelButton.node.on(cc.Node.EventType.TOUCH_START, () => this.JumpToChannelView());
    this.destroyEngine.node.on(cc.Node.EventType.TOUCH_START, () => this.destroyGMMEEngine());
  }

  private destroyGMMEEngine() {
    // 销毁引擎
    GlobalData.gameMediaEngine.destroy();
    GlobalData.homeLogItems = [];
    GlobalData.audioMsgLogItems = [];
    GlobalData.audioMsgItems = [];
    GlobalData.rtmp2pContentItems = [];
    GlobalData.rtmChannelContentItems = [];
    GlobalData.channelCustomPropertiesItems = [];
    GlobalData.userCustomPropertiesItems = [];
    GlobalData.cacheChannelCustomPropertiesItems = [];
    GlobalData.cacheUserCustomPropertiesItems = [];
    GlobalData.receiverChannelsItems = [];
    GlobalData.channelContentMapTemp.clear();
    GlobalData.channelIdTemp = '';
    GlobalData.openId = '';
    cc.director.loadScene('HomeView');
  }

  private JumpToAudioMsgView() {
    cc.director.loadScene('AudioMsg');
  }

  private JumpToP2PView() {
    cc.director.loadScene('PeerToPeer');
  }

  private JumpToChannelView() {
    cc.director.loadScene('Channel');
  }
}
