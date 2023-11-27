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

class Constant {
  // 主页日志事件
  public homeLogEvent: string = 'homeLogEvent';

  // 语音消息日志事件
  public audioMsgLogEvent: string = 'audioMsgLogEvent';

  // 语音消息列表事件
  public audioMsgEvent: string = 'audioMsgEvent';

  // 下拉框选中事件
  public selectedChannelEvent: string = 'selectedChannelEvent';

  // 刷新属性列表
  public refreshPropertiesEvent: string = 'refreshPropertiesEvent';

  // 刷新设置的属性列表
  public refreshSetPropertiesEvent: string = 'refreshSetPropertiesEvent';
}

export default new Constant();