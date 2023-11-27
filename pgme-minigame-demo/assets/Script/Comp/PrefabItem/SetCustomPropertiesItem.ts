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
import {CheckType, PropertiesType} from "../../Function/Enum";
import Constant from "../../Function/Constant";

const {ccclass, property} = cc._decorator;

@ccclass
export default class SetCustomPropertiesItem extends cc.Component {
  private id: string = null;

  @property(cc.EditBox)
  key: cc.EditBox = null;

  @property(cc.EditBox)
  value: cc.EditBox = null;

  @property(cc.Button)
  deleteBtn: cc.Button = null;

  private propertiesType: number;

  start() {
    this.initListener();
  }

  private initListener() {
    this.deleteBtn.node.on(cc.Node.EventType.TOUCH_START, this.deleteProperties, this);
  }

  // 输入框处理
  handleEditBox(value, propName) {
    switch (this.propertiesType) {
      case PropertiesType.USER:
        this.setCacheCustomPropertiesItem(GlobalData.cacheUserCustomPropertiesItems, propName, value);
        break;
      case PropertiesType.CHANNEL:
        this.setCacheCustomPropertiesItem(GlobalData.cacheChannelCustomPropertiesItems, propName, value);
        break;
    }
  }

  private setCacheCustomPropertiesItem(customPropertiesItems: CustomPropertiesScrollItem[], propName, value) {
    customPropertiesItems.forEach(userCustomProp => {
      if (userCustomProp.id == this.id) {
        if (propName.node.name == "Key") {
          userCustomProp.key = value;
        }
        if (propName.node.name == "Value") {
          userCustomProp.value = value;
        }
      }
    })
  }

  private deleteProperties() {
    switch (this.propertiesType) {
      case PropertiesType.USER:
        this.deleteCacheProperties(GlobalData.cacheUserCustomPropertiesItems);
        break;
      case PropertiesType.CHANNEL:
        this.deleteCacheProperties(GlobalData.cacheChannelCustomPropertiesItems);
        break;
    }
    // 发送刷新设置属性列表的事件
    this.node.dispatchEvent(new cc.Event.EventCustom(Constant.refreshSetPropertiesEvent, true));
  }

  private deleteCacheProperties(cacheCustomPropertiesItems: CustomPropertiesScrollItem[]) {
    cacheCustomPropertiesItems.map((item, index) => {
      if (item.id == this.id) {
        cacheCustomPropertiesItems.splice(index, 1);
      }
    });
  }

  init(item: CustomPropertiesScrollItem, propertiesType: number) {
    this.id = item.id;
    this.key.string = item.key;
    this.value.string = item.value;
    this.propertiesType = propertiesType;
  }
}
