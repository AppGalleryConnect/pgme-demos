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
import GlobalData from '../../../GlobalData';
import AudioMsgScrollItem from '../../Function/AudioMsgScrollItem';

@ccclass
export class AudioMsgItemList extends cc.Component {
  @property(Prefab)
  itemPrefab: Prefab | null = null;

  fresh() {
    this.node.removeAllChildren(true);
    const items: AudioMsgScrollItem[] = GlobalData.audioMsgItems;
    if (items) {
      for (let i = 0; i < items.length; ++i) {
        const item = instantiate(this.itemPrefab);
        const data = items[i];
        this.node.addChild(item);
        item.getComponent('AudioMsgItem').init(data);
      }
    }
  }
}
