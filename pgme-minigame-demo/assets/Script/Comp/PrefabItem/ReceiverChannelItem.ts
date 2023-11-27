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

import ReceiverChannelScrollItem from "../../Function/ReceiverChannelScrollItem";
import Constant from "../../Function/Constant";

const { ccclass, property } = cc._decorator;

@ccclass
export default class ReceiverChannelItem extends cc.Component {
  @property(cc.Label)
  label: cc.Label = null;

  private selectType: number;

  start() {
    this.initListener();
  }

  private initListener() {
    this.label.node.on(cc.Node.EventType.TOUCH_END, () => this.selectChannel(this.label.string));
  }

  private selectChannel(channelId: string) {
    // 通知事件
    let eventCustom = new cc.Event.EventCustom(Constant.selectedChannelEvent, true);
    let userData = {
      channelId: channelId,
      selectType: this.selectType
    }
    eventCustom.setUserData(userData);
    this.node.dispatchEvent(eventCustom);
  }

  init(item: ReceiverChannelScrollItem, selectType: number) {
    this.label.string = item.data;
    this.selectType = selectType;
  }
}
