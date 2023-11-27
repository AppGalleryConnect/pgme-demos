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

#import <UIKit/UIKit.h>
#import "HWPGMEKit/HWPGMEngine.h"

@class ChannelSendView;
NS_ASSUME_NONNULL_BEGIN

@protocol ChannelSendViewDelegate <NSObject>

/// 订阅频道
/// @param channelView 频道View
/// @param channel 频道名
- (void)channelSendView:(ChannelSendView *)channelView subscribeChancel:(NSString *)channel;

/// 退订频道
/// @param channelView 频道View
/// @param channel 频道名
- (void)channelSendView:(ChannelSendView *)channelView unSubscribeChancel:(NSString *)channel;

/// 切换频道
/// @param channelView 频道View
/// @param channel 频道名
- (void)channelSendView:(ChannelSendView *)channelView changeChancel:(NSString *)channel;

/// 发送频道消息
/// @param channelView 频道View
/// @param msgReq 消息体
- (void)channelSendView:(ChannelSendView *)channelView sendMsgReq:(PublishRtmChannelMessageReq *)msgReq;

@end

@interface ChannelSendView : UIView

@property(nonatomic, weak) id <ChannelSendViewDelegate> delegate;


/// 退订成功后, 检查当前频道是否和退订的频道一致
- (void)checkCurentChannelWithUnSubChannel:(NSString *)channel;

/// 设置频道
- (void)configCurrentChannel:(NSString *)channel;

/// 设置已订阅的频道
- (void)configChannelsArray:(NSArray *)channelsArray;

/// 获取当前选中的频道
- (NSString *)getSelectChannel;

/// 获取时间戳
- (long)getHistoryTimestamp;

/// 获取输入的数量
- (NSString *)getHistoryCount;
@end

NS_ASSUME_NONNULL_END
