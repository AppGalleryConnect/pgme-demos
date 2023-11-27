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

@class UserPropertyView;
@protocol UserPropertyViewDelegate <NSObject>

/// 删除频道属性
/// @param propertyView 用户属性View
/// @param channel 频道ID
/// @param userId 用户ID
/// @param keys 频道key
- (void)userPropertyView:(UserPropertyView *_Nullable)propertyView  deleteChanel:(NSString *_Nonnull)channel keys:(NSArray *_Nonnull)keys;

/// 频道切换
/// @param propertyView 用户属性View
/// @param channel 切换后的频道
/// @param openId 输入的用户名
- (void)userPropertyView:(UserPropertyView *_Nullable)propertyView channel:(NSString *_Nonnull)channel openId:(NSString *_Nonnull)openId;

/// 设置用户属性
/// @param propertyView 用户属性View
/// @param channel 频道
/// @param channelProperties 频道属性
- (void)userPropertyView:(UserPropertyView *_Nullable)propertyView setChannelPropertiesChanel:(NSString *_Nonnull)channel channelProperties:(NSDictionary <NSString *, NSString *> *_Nonnull)channelProperties;

///  查询用户属性
/// @param propertyView 用户属性View
/// @param channel 频道
/// @param userId 用户ID
- (void)userPropertyView:(UserPropertyView *_Nullable)propertyView queryPropertiesChannel:(NSString *_Nonnull)channel userId:(NSString *_Nullable)userId;
@end

NS_ASSUME_NONNULL_BEGIN

@interface UserPropertyView : UIView

@property(nonatomic, weak) id<UserPropertyViewDelegate> delegate;

/// 退订成功后, 检查当前频道是否和退订的频道一致
- (void)checkCurentChannelWithUnSubChannel:(NSString *)channel;

/// 设置已订阅的频道
- (void)configChannelsArray:(NSArray *)channelsArray;

/// 设置频道用户属性数据
- (void)configPlayerChannel:channel properties:(NSDictionary *)properties;

/// 移除弹框
- (void)removePropertySetView;
@end

NS_ASSUME_NONNULL_END
