/**
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


#import <Foundation/Foundation.h>
#import <HWPGMEKit/HWPGMEngine.h>
#import "MsgCacheModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface RtmMsgCache : NSObject

/// 消息缓存
/// @param clientMsgId  端消息Id
/// @param cacheModel 缓存的消息对象
- (void)cacheMsg:(NSString *)clientMsgId cacheModel:(MsgCacheModel *)cacheModel;

/// 根据端消息ID获取 消息内容
/// @param clientMsgId 端消息ID
/// @return 消息内容
- (NSString *)getCacheMsgWithClientMsgId:(NSString *)clientMsgId;

/// 拼接receive 消息
/// @param notify 接收到P2P消息回调参数
/// @return 拼接后的消息
- (NSString *)getPeerReceiveMessage:(ReceiveRtmPeerMessageNotify *)notify;

/// 拼接receive 消息
/// @param notify 接收到频道消息回调参数
/// @return 拼接后的消息
- (NSString *)getChannelReceiveMessage:(ReceiveRtmChannelMessageNotify *)notify;

/// 历史消息拼接
- (NSString *)getHistoryMessage:(RtmChannelHistoryMessage *)message;

/// 获取当前的时分秒， 格式为H:mm:ss
/// @return 当前的时分秒
- (NSString *)getCurrentTime;
@end

NS_ASSUME_NONNULL_END
