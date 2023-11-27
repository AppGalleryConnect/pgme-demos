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

import GlobalData from "../../../GlobalData";
import instantiate = cc.instantiate;
import Prefab = cc.Prefab;
import {PropertiesType} from "../../Function/Enum";
import CustomPropertiesScrollItem from "../../Function/CustomPropertiesScrollItem";

const {ccclass, property} = cc._decorator;

@ccclass
export default class SetCustomPropertiesItemList extends cc.Component {
  @property(Prefab)
  itemPrefab: Prefab | null = null;

  fresh(propertiesType: number) {
    this.node.removeAllChildren(true);
    let items: CustomPropertiesScrollItem[] = [];
    switch (propertiesType) {
      case PropertiesType.CHANNEL:
        items = GlobalData.cacheChannelCustomPropertiesItems;
        break;
      case PropertiesType.USER:
        items = GlobalData.cacheUserCustomPropertiesItems;
        break;
    }
    if (items) {
      for (let i = 0; i < items.length; ++i) {
        const item = instantiate(this.itemPrefab);
        const data = items[i];
        this.node.addChild(item);
        item.getComponent('SetCustomPropertiesItem').init(data, propertiesType);
      }
    }
  }
}
