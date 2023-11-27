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

import CustomPropertiesScrollItem from "../../Function/CustomPropertiesScrollItem";
import GlobalData from "../../../GlobalData";
import {PropertiesType} from "../../Function/Enum";
import Constant from "../../Function/Constant";
import {
  GameMediaEngine,
  DeleteRtmChannelPlayerPropertiesReq,
  DeleteRtmChannelPlayerPropertiesResult,
  DeleteRtmChannelPropertiesReq,
  DeleteRtmChannelPropertiesResult,
} from "../../../GMME/GMMEForMiniGames";

const {ccclass, property} = cc._decorator;

@ccclass
export default class CustomPropertiesItem extends cc.Component {
  private id: string;

  private propertiesType: number;

  @property(cc.Label)
  key: cc.Label = null;

  @property(cc.Label)
  value: cc.Label = null;

  @property(cc.Button)
  deleteBtn: cc.Button = null;

  start() {
    this.initListener();
  }

  init(item: CustomPropertiesScrollItem, propertiesType: number) {
    this.id = item.id;
    this.key.string = item.key;
    this.value.string = item.value;
    this.propertiesType = propertiesType;
  }

  private initListener() {
    this.deleteBtn.node.on(cc.Node.EventType.TOUCH_START, this.deleteProperties, this);

    // ===== 13.8.1 =====
    GameMediaEngine.on('onDeleteRtmChannelProperties', (result: DeleteRtmChannelPropertiesResult) =>
        this.onDeleteRtmChannelProperty(result)
    );
    GameMediaEngine.on('onDeleteRtmChannelPlayerProperties', (result: DeleteRtmChannelPlayerPropertiesResult) =>
        this.onDeleteRtmChannelPlayerProperty(result)
    );
  }

  private deleteProperties() {
    switch (this.propertiesType) {
      case PropertiesType.CHANNEL:
        this.deleteChannelPropertiesItem();
        break;
      case PropertiesType.USER:
        this.deleteUserPropertiesItem();
        break;
    }
  }

  // 删除频道属性
  private deleteChannelPropertiesItem() {
    const req: DeleteRtmChannelPropertiesReq = {
      channelId: GlobalData.channelIdTemp,
      keys: [this.key.string],
    }
    GlobalData.gameMediaEngine.deleteRtmChannelProperties(req);
  }

  // 删除用户属性
  private deleteUserPropertiesItem() {
    const req: DeleteRtmChannelPlayerPropertiesReq = {
      channelId: GlobalData.channelIdTemp,
      keys: [this.key.string],
    }
    GlobalData.gameMediaEngine.deleteRtmChannelPlayerProperties(req);
  }

  // ========== 回调方法 ==========
  // 删除频道属性回调
  private onDeleteRtmChannelProperty(result: DeleteRtmChannelPropertiesResult) {
      this.sendEvent(result.code, result.msg, result.channelId, PropertiesType.CHANNEL);
  }

  // 删除玩家属性回调
  private onDeleteRtmChannelPlayerProperty(result: DeleteRtmChannelPlayerPropertiesResult) {
      this.sendEvent(result.code, result.msg, result.channelId, PropertiesType.USER);
  }

  // 发送事件
  private sendEvent(code: number, msg:string, channelId: string, propertyType: number) {
    let eventCustom = new cc.Event.EventCustom(Constant.refreshPropertiesEvent, true);
    let userData = {
      code: code,
      msg: msg,
      channelId: channelId,
      propertiesType: propertyType
    }
    eventCustom.setUserData(userData);
    this.node.dispatchEvent(eventCustom);
  }
}
