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

@interface RoomMuteInfo : NSObject

/// 当前玩家所在的房间列表, 当玩家退出房间的时候, 需要移除以roomId为key的数据
@property(nonatomic, strong) NSArray *roomIdArray;

/// 以房间号为key,保存房间屏蔽信息
/// @param muteArray 玩家信息
/// @param allPlayerIsMute 是否屏蔽所有玩家
/// @param roomId 房间号
- (void)setObjectMuteArray:(NSMutableArray *)muteArray allPlayerIsMute:(BOOL)allPlayerIsMute roomId:(NSString *)roomId;

/// 根据房间ID获取包含屏蔽信息的玩家数据
/// @param roomId 房间号
/// @return 玩家数据
- (NSMutableArray *)muteArrayForKey:(NSString *)roomId;

///根据房间ID获取该房间是否屏蔽所有玩家声音
/// @param roomId 房间号
/// @return 是否屏蔽所有玩家
- (BOOL)allPlayerIsMuteForKey:(NSString *)roomId;

/// 更新数据
/// @param muteArray 玩家数据
/// @param roomId 房间号
- (void)updateMuteArray:(NSMutableArray *)muteArray roomId:(NSString *)roomId;

/// 移除房间中的玩家
/// @param roomId 房间Id
/// @param openId 玩家的openId
- (void)removeCachePlayerWithRoomId:(NSString *)roomId openId:(NSString *)openId;

/// 以房间号为key,保存房间的禁言所有人信息
/// @param forbiddenAll 是否禁言所有人
/// @param roomId 房间ID
- (void)setForbiddenAll:(BOOL)forbiddenAll roomId:(NSString *)roomId;

/// 移除对应房间的禁言信息
/// @param roomId 房间ID
- (void)removeForbiddenAllForKey:(NSString *)roomId;

/// 根据房间号获取该房间是否禁言所有玩家
- (BOOL)allPlayerIsForbiddenForKey:(NSString *)roomId;

/// 移除房间
/// @param roomId 房间Id
- (void)removeCacheRoom:(NSString *)roomId;

/// 移除所有数据
- (void)removeAllObject;
@end

NS_ASSUME_NONNULL_END
