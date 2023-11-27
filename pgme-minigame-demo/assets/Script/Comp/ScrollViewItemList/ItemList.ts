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

import ccclass = cc._decorator.ccclass;
import property = cc._decorator.property;
import Prefab = cc.Prefab;
import instantiate = cc.instantiate;
import LogScrollItem from '../../Function/LogScrollItem';
import GlobalData from '../../../GlobalData';
import {LogType} from '../../Function/Enum';

@ccclass
export class ItemList extends cc.Component {
  @property(Prefab)
  itemPrefab: Prefab | null = null;

  // type 1:主页日志  2语音消息日志  3RTM-p2p内容日志  4RTM-频道内容日志
  fresh(type: number) {
    this.node.removeAllChildren(true);
    let items: LogScrollItem[] = [];
    switch (type) {
      case LogType.HOME_LOG_TYPE:
        items = GlobalData.homeLogItems;
        break;
      case LogType.AUDIO_MSG_TYPE:
        items = GlobalData.audioMsgLogItems;
        break;
      case LogType.RTM_P2P_CONTENT_TYPE:
        items = GlobalData.rtmp2pContentItems;
        break;
      case LogType.RTM_CHANNEL_CONTENT_TYPE:
        items = GlobalData.rtmChannelContentItems;
        break;
    }
    if (items) {
      for (let i = 0; i < items.length; ++i) {
        const item = instantiate(this.itemPrefab);
        const data = items[i];
        this.node.addChild(item);
        item.getComponent('LogItem').init(data);
      }
    }
  }
}
