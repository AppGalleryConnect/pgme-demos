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
import {PropertiesType} from "../../Function/Enum";
import CustomPropertiesScrollItem from "../../Function/CustomPropertiesScrollItem";
import Utils from "../../Function/Utils";
import Constant from "../../Function/Constant";

const {ccclass, property} = cc._decorator;

@ccclass
export default class SetCustomProperties extends cc.Component {
  @property(cc.Button)
  addBtn: cc.Button = null;

  @property(cc.Button)
  confirmBtn: cc.Button = null;

  @property(cc.Button)
  cancelBtn: cc.Button = null;

  @property(cc.Layout)
  setCustomPropContent: cc.Layout = null;

  // 确认按钮回调函数
  public static onConfirm: () => any = null;
  // 取消按钮回调函数
  public static onCancel: () => any = null;

  private static isOpen: boolean = false;

  private static node: cc.Node = null;

  private static propertiesType: number = 0;

  private static isShowPropertiesList: boolean = false;

  private static isRefreshSetPropertiesList: boolean = false;

  update(dt) {
    this.node && (this.node.active = SetCustomProperties.isOpen);
    if (SetCustomProperties.isShowPropertiesList) {
      SetCustomProperties.isShowPropertiesList = false;
      this.refreshView(SetCustomProperties.propertiesType);
    }
    if (SetCustomProperties.isRefreshSetPropertiesList) {
      SetCustomProperties.isRefreshSetPropertiesList = false;
      const cacheCustomPropItemList = this.setCustomPropContent.getComponent('SetCustomPropertiesItemList');
      cacheCustomPropItemList.fresh(SetCustomProperties.propertiesType);
    }
  }

  start() {
    this.initListener();
  }

  private refreshView(propertiesType: number) {
    switch (propertiesType) {
      case PropertiesType.CHANNEL:
        // 刷新设置频道属性的列表
        GlobalData.cacheChannelCustomPropertiesItems = [];
        this.refreshPropertiesScrollItem(propertiesType, GlobalData.cacheChannelCustomPropertiesItems,
          GlobalData.channelCustomPropertiesItems);
        break;
      case PropertiesType.USER:
        // 刷新设置用户属性的列表
        GlobalData.cacheUserCustomPropertiesItems = [];
        this.refreshPropertiesScrollItem(propertiesType, GlobalData.cacheUserCustomPropertiesItems,
            GlobalData.userCustomPropertiesItems);
        break;
    }
  }

  // 刷新设置属性的列表
  private refreshPropertiesScrollItem(propertiesType: number, cacheCustomPropertiesItems: CustomPropertiesScrollItem[],
    customPropertiesItems: CustomPropertiesScrollItem[]) {
    customPropertiesItems.forEach(customProp => {
      const channelPropertiesScrollItem = new CustomPropertiesScrollItem();
      channelPropertiesScrollItem.init(Utils.random(), customProp.key, customProp.value);
      cacheCustomPropertiesItems.push(channelPropertiesScrollItem);
    });
    const channelCustomPropItemList = this.setCustomPropContent.getComponent('SetCustomPropertiesItemList');
    channelCustomPropItemList.fresh(propertiesType);
  }

  private initListener() {
    SetCustomProperties.node = this.node;
    this.addBtn.node.on(cc.Node.EventType.TOUCH_START, this.add, this);
    this.confirmBtn.node.on(cc.Node.EventType.TOUCH_START, this.confirm, this);
    this.cancelBtn.node.on(cc.Node.EventType.TOUCH_START, this.cancel, this);
  }

  public add() {
    const customPropertiesScrollItem = new CustomPropertiesScrollItem();
    customPropertiesScrollItem.init(Utils.random(), "", "");
    switch (SetCustomProperties.propertiesType) {
      case PropertiesType.CHANNEL:
        GlobalData.cacheChannelCustomPropertiesItems.push(customPropertiesScrollItem);
        break;
      case PropertiesType.USER:
        GlobalData.cacheUserCustomPropertiesItems.push(customPropertiesScrollItem);
        break;
    }
    SetCustomProperties.isRefreshSetPropertiesList = true;
  }

  private confirm() {
    SetCustomProperties.onConfirm && SetCustomProperties.onConfirm();
    SetCustomProperties.close();
  }

  private cancel() {
    SetCustomProperties.onCancel && SetCustomProperties.onCancel();
    SetCustomProperties.close();
  }

  // 打开设置属性界面
  public static open(propertiesType: number, onConfirm?: () => any, onCancel?: () => any) {
    SetCustomProperties.propertiesType = propertiesType;
    SetCustomProperties.isShowPropertiesList = true;
    SetCustomProperties.onConfirm = onConfirm || null;
    SetCustomProperties.onCancel = onCancel || null;
    SetCustomProperties.isOpen = true;
    SetCustomProperties.node && (SetCustomProperties.node.active = true);
  }

  // 关闭设置属性界面
  public static close() {
    SetCustomProperties.propertiesType = 0;
    SetCustomProperties.isShowPropertiesList = false;
    SetCustomProperties.onConfirm = null;
    SetCustomProperties.onCancel = null;
    SetCustomProperties.isOpen = false;
    SetCustomProperties.node && (SetCustomProperties.node.active = false);
  }

  protected onEnable() {
    this.node.on(Constant.refreshSetPropertiesEvent, function () {
      SetCustomProperties.isRefreshSetPropertiesList = true;
    });
  }

  protected onDisable() {
    this.node.off(Constant.refreshSetPropertiesEvent);
  }

}
