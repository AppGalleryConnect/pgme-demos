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

@class ChannelPropertyView;
@protocol ChannelPropertyViewDelegate <NSObject>

/// 删除频道属性
/// @param propertyView 频道属性View
/// @param channel 频道
/// @param keys 频道key
- (void)channelPropertyView:(ChannelPropertyView *)propertyView deleteChannel:(NSString *)channel keys:(NSArray *)keys;

/// 频道切换
/// @param propertyView 频道属性View
/// @param channel 切换后的频道
- (void)channelPropertyView:(ChannelPropertyView *)propertyView channel:(NSString *)channel;

/// 设置频道属性
/// @param propertyView 频道属性View
/// @param channelProperties 频道属性
- (void)channelPropertyView:(ChannelPropertyView *)propertyView setChannel:(NSString *)channel properties:(NSDictionary <NSString *, NSString *> *)channelProperties;

@end


@interface ChannelPropertyView : UIView

@property(nonatomic, weak) id<ChannelPropertyViewDelegate> delegate;

/// 退订成功后, 检查当前频道是否和退订的频道一致
- (void)checkCurentChannelWithUnSubChannel:(NSString *)channel;

/// 设置已订阅的频道
- (void)configChannelsArray:(NSArray *)channelsArray;

/// 设置频道属性
- (void)configChannel:(NSString *)channel properties:(NSDictionary *)properties;

/// 移除弹框
- (void)removePropertySetView;
@end

NS_ASSUME_NONNULL_END
