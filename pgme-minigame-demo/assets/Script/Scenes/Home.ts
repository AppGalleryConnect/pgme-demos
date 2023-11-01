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

import { EngineCreateParams, GameMediaEngine } from '../../GMME/GMMEForMiniGames';
import { getSign } from '../Function/Utils';
import configs from '../../Config';
import LogUtil from '../Function/LogUtils';
import { LogType } from '../Function/Enum';
import GlobalData from '../../GlobalData';

const { ccclass, property } = cc._decorator;
const regex = /^[a-zA-Z0-9_]+$/;

@ccclass
export default class Home extends cc.Component {
  @property(cc.Layout)
  content: cc.Layout = null;

  @property(cc.EditBox)
  openIdEdit: cc.EditBox = null;

  @property(cc.Button)
  initButton: cc.Button = null;

  protected update(dt: number) {
    this.initButton.interactable = this.openIdEdit.string.length > 0 && regex.test(this.openIdEdit.string);
  }

  start() {
    this.init();
  }

  private init() {
    this.initListener();
    this.initLogConsole();
  }

  private initListener() {
    this.initButton.node.on(cc.Node.EventType.TOUCH_START, () => this.onInitEngine());
  }

  onInitEngine() {
    const openId = this.openIdEdit.string;
    const signParams = getSign(configs.appId, openId);
    const options: EngineCreateParams = {
      openId: openId, // 玩家ID
      appId: configs.appId, // 应用ID
      clientId: configs.clientId, // 客户端ID
      clientSecret: configs.clientSecret, // 客户端ID对应的秘钥
      ...signParams,
    };
    GameMediaEngine.create(options)
      .then((gameMediaEngine) => {
        GlobalData.gameMediaEngine = gameMediaEngine;
        GlobalData.openId = openId;
        cc.director.loadScene('FunctionHall');
      })
      .catch((e) => {
        LogUtil.printLog('初始化失败:' + e.message, LogType.HOME_LOG_TYPE, this.node);
      });
  }

  // 初始化日志控制台
  private initLogConsole() {
    const scriptComponent = this.content.getComponent('ItemList');
    scriptComponent.fresh(LogType.HOME_LOG_TYPE);
  }

  // 事件监听
  protected onEnable() {
    this.node.on('homeLogEvent', this.callBackHomeLog, this);
  }

  protected onDisable() {
    this.node.off('homeLogEvent', this.callBackHomeLog, this);
  }

  private callBackHomeLog() {
    this.initLogConsole();
  }
}
