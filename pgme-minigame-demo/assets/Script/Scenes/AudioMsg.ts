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
import { AudioMsgItemList } from '../Comp/AudioMsgItemList';
import { ItemList } from '../Comp/ItemList';
import { LogType } from '../Function/Enum';
import AudioMsgScrollItem from '../Function/AudioMsgScrollItem';
import { GameMediaEngine } from '../../GMME/GMMEForMiniGames';
import LogUtil from '../Function/LogUtils';

const { ccclass, property } = cc._decorator;

@ccclass
export default class AudioMsg extends cc.Component {
  @property(cc.Button)
  log: cc.Button = null;

  @property(cc.Button)
  record: cc.Button = null;

  @property(cc.Layout)
  content: cc.Layout = null;

  @property(cc.Layout)
  audioMsgContent: cc.Layout = null;

  @property(cc.ScrollView)
  loginTextOut: cc.ScrollView = null;

  @property(cc.Prefab)
  audioMsgLog: cc.Prefab = null;

  @property(cc.Layout)
  countdownLayout: cc.Layout = null;

  @property(cc.Label)
  countdownLabel: cc.Label = null;

  @property(cc.Button)
  back: cc.Button = null;

  isShowLogConsole = true;

  countdown = 50;

  start() {
    this.initLogConsole();
    this.refreshAudioMsgItems();
    this.initComponent();
  }

  update(dt: number) {
    if (GlobalData.isRefreshAudioMsg) {
      this.refreshAudioMsgItems();
      GlobalData.isRefreshAudioMsg = !GlobalData.isRefreshAudioMsg;
    }
  }

  private initComponent() {
    this.log.node.on(cc.Node.EventType.TOUCH_START, () => this.showOrHideLogConsole());
    this.record.node.on(cc.Node.EventType.TOUCH_START, () => this.startRecordAudio());
    this.record.node.on(cc.Node.EventType.TOUCH_END, () => this.stopRecordAudio());
    this.back.node.on(cc.Node.EventType.TOUCH_START, () => this.backHall());
    GameMediaEngine.on(
      'onRecordAudioMsg',
      (filePath: string, code: number, msg: string, duration: number, size: number) =>
        this.onRecordAudioMsg(filePath, code, msg, duration, size),
    );
    this.countdownLayout.node.active = false;
    this.loginTextOut.node.active = false;
  }

  private backHall() {
    cc.director.loadScene('FunctionHall');
  }

  // ========== 回调方法 ==========
  private onRecordAudioMsg(filePath: string, code: number, msg: string, duration: number, size: number) {
    // 录制完成
    LogUtil.printLog('录制语音消息，code:' + code + ',msg:' + msg, LogType.AUDIO_MSG_TYPE, this.node);
    if (code === 0) {
      this.recordFinish(filePath);
    }
  }

  // 完成录制,刷新语音消息界面
  private recordFinish(filePath: string) {
    const audioMsgScrollItem = new AudioMsgScrollItem();
    audioMsgScrollItem.init(0, filePath, filePath, false);
    GlobalData.audioMsgItems.push(audioMsgScrollItem);
    this.refreshAudioMsgItems();
  }

  private startRecordAudio() {
    // 开始录音
    this.countdownLabel.string = '开始录制：50s';
    this.countdown = 50;
    this.countdownLayout.node.active = true;
    this.schedule(this.onScheduleEvent, 1, this.countdown - 1, 0);
    GlobalData.gameMediaEngine.startRecordAudioMsg();
  }

  private stopRecordAudio() {
    // 停止录音
    this.unschedule(this.onScheduleEvent);
    this.countdownLayout.node.active = false;
    GlobalData.gameMediaEngine.stopRecordAudioMsg();
  }

  // 定时任务回调方法
  private onScheduleEvent() {
    this.countdown -= 1;
    if (this.countdown <= 0) {
      this.unschedule(this.onScheduleEvent);
      this.countdownLayout.node.active = false;
      GlobalData.gameMediaEngine.stopRecordAudioMsg();
    }
    this.countdownLabel.string = '开始录制：' + this.countdown + 's';
  }

  private refreshAudioMsgItems() {
    const audioMsgItemList = this.audioMsgContent.getComponent('AudioMsgItemList');
    audioMsgItemList.fresh();
  }

  private showOrHideLogConsole() {
    this.loginTextOut.node.active = this.isShowLogConsole;
    this.isShowLogConsole = !this.isShowLogConsole;
  }

  // 初始化日志控制台
  private initLogConsole() {
    const itemList = this.content.getComponent('ItemList');
    itemList.fresh(LogType.AUDIO_MSG_TYPE);
  }

  // 事件监听
  protected onEnable() {
    this.node.on('audioMsgLogEvent', this.callBackAudioMsgLog, this);
    this.node.on('audioMsgEvent', this.refreshAudioMsgItems, this);
  }

  protected onDisable() {
    this.node.off('audioMsgLogEvent', this.callBackAudioMsgLog, this);
    this.node.off('audioMsgEvent', this.refreshAudioMsgItems, this);
  }

  private callBackAudioMsgLog() {
    this.initLogConsole();
  }
}
