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

NS_ASSUME_NONNULL_BEGIN

@interface RoomBtnEnabledInfo : NSObject
/// 以房间号为key,保存玩家
/// @param muteArray 玩家信息
/// @param roomId 房间号
- (void)setObjectPlayerArray:(NSArray *)playerArray  roomId:(NSString *)roomId;

/// 根据房间ID获取包含的玩家数据
/// @param roomId 房间号
/// @return 玩家数据
- (NSArray *)playerArrayForKey:(NSString *)roomId;

/// 移除房间
/// @param roomId 房间Id
- (void)removeCacheRoom:(NSString *)roomId;

/// 移除房间中的玩家
/// @param roomId 房间Id
/// @param openId 玩家的openId
- (void)removeCachePlayerWithRoomId:(NSString *)roomId openId:(NSString *)openId;

/// 移除所有数据
- (void)removeAllObject;
@end

NS_ASSUME_NONNULL_END
