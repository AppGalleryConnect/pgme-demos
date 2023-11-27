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
import LogScrollItem from './LogScrollItem';
import { LogType } from './Enum';
import Utils from './Utils';
import Constant from "./Constant";

export default class LogUtil {
  static printLog(data: string, type: number, node: cc.Node) {
    const logScrollItem = new LogScrollItem();
    logScrollItem.init(Utils.getCurrentDateTime() + ' ' + data);
    if (type === LogType.HOME_LOG_TYPE) {
      GlobalData.homeLogItems.push(logScrollItem);
      node.dispatchEvent(new cc.Event.EventCustom(Constant.homeLogEvent, true));
    }
    if (type === LogType.AUDIO_MSG_TYPE) {
      GlobalData.audioMsgLogItems.push(logScrollItem);
      node.dispatchEvent(new cc.Event.EventCustom(Constant.audioMsgLogEvent, true));
    }
  }
}
