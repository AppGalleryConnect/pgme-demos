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

import { GameMediaEngine } from './GMME/GMMEForMiniGames';
import LogScrollItem from './Script/Function/LogScrollItem';
import AudioMsgScrollItem from './Script/Function/AudioMsgScrollItem';
import ReceiverChannelScrollItem from "./Script/Function/ReceiverChannelScrollItem";
import CustomPropertiesScrollItem from "./Script/Function/CustomPropertiesScrollItem";

class GlobalData {
  // 主页日志
  public homeLogItems: LogScrollItem[] = [];
  // 语音消息日志
  public audioMsgLogItems: LogScrollItem[] = [];
  // 语音消息列表
  public audioMsgItems: AudioMsgScrollItem[] = [];
  // rtm p2p日志
  public rtmp2pContentItems: LogScrollItem[] = [];
  // rtm 频道日志
  public rtmChannelContentItems: LogScrollItem[] = [];
  // 频道自定义属性
  public channelCustomPropertiesItems: CustomPropertiesScrollItem[] = [];
  // 用户自定义属性
  public userCustomPropertiesItems: CustomPropertiesScrollItem[] = [];
  // 缓存设置频道自定义属性
  public cacheChannelCustomPropertiesItems: CustomPropertiesScrollItem[] = [];
  // 缓存设置用户自定义属性
  public cacheUserCustomPropertiesItems: CustomPropertiesScrollItem[] = [];
  // 缓存每个频道的日志
  public channelContentMapTemp: Map<string, LogScrollItem[]> = new Map<string, LogScrollItem[]>();
  // 接收频道列表
  public receiverChannelsItems: ReceiverChannelScrollItem[] = [];

  public channelIdTemp= '';
  public openId = '';
  public isRefreshAudioMsg = false;
  public gameMediaEngine: GameMediaEngine | null = null;
}

export default new GlobalData();
