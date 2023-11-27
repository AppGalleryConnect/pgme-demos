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

NS_ASSUME_NONNULL_BEGIN
@class RtmMessageSendView;

typedef NS_ENUM(NSInteger, RtmMessageSendViewType) {
    // P2P消息
    RtmMessageSendViewTypeP2P = 1,
    
    // 频道消息
    RtmMessageSendViewTypeChannel = 2
};

@protocol RtmMessageSendViewDelegate <NSObject>

/// 发送消息
/// @param sendView 消息发送View
/// @param message 消息
- (void)rtmMessageView:(RtmMessageSendView *)sendView sendMessage:(NSString *)message;

/// 发送二进制消息
/// @param sendView 消息发送View
/// @param message 消息
- (void)rtmMessageView:(RtmMessageSendView *)sendView sendBinaryMessage:(NSString *)message;

@end

@interface RtmMessageSendView : UIView

@property(nonatomic, weak) id <RtmMessageSendViewDelegate> delegate;

- (instancetype)initWithType:(RtmMessageSendViewType)type;

/// 是否缓存消息
/// @return YES 缓存, NO 不缓存
- (BOOL)isAllowCacheMsg;

/// 是否进行内容风控审核
/// @return YES 进行内容风控审核, NO 不进行内容风控审核
- (BOOL)isContentIdentify;

/// 是否进行广告识别
///  @return YES 进行广告识别, NO 不进行广告识别
- (BOOL)isAdsIdentify;
@end

NS_ASSUME_NONNULL_END
