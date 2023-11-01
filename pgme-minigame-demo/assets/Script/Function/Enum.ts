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

// 枚举类
export enum LogType {
  HOME_LOG_TYPE = 1, // 主页日志
  AUDIO_MSG_TYPE = 2, //  语音消息日志
  RTM_P2P_CONTENT_TYPE = 3, // RTM-p2p内容日志
  RTM_CHANNEL_CONTENT_TYPE = 4, // RTM-频道内容日志
}

// 枚举类
export enum MessageType {
  TEXT = 1,
  BINARY = 2,
}
