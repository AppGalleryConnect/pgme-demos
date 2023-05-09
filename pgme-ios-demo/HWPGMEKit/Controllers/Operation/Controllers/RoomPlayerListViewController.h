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

#import "HWBaseViewController.h"
@class HWPGMEObject;

NS_ASSUME_NONNULL_BEGIN

/// 玩家列表Controller
@interface RoomPlayerListViewController : HWBaseViewController

/// 根据房间信息刷新用户列表
/// @param roomInfo  Room对象
/// @param ownerId  房主的openId
- (void)refreshListWithRoomInfo:(Room * _Nullable)roomInfo ownerId:(NSString *)ownerId;

/// 刷新正在说话的玩家列表
/// @param users 正在说话的玩家列表
- (void)speakingWithUsers:(NSArray *)users;

/// 是否禁止玩家使用麦克风
/// @param openId 玩家openId
/// @param isForbidden 麦克风是否被禁止
- (void)forbidPlayer:(NSString *)openId isForbidden:(BOOL)isForbidden;

/// 是否禁止所有玩家使用麦克风
/// @param roomId 房间Id
/// @param openIds 被禁止的openId数组
/// @param isForbidden 麦克风是否被禁止
- (void)forbidAllPlayers:(NSString *)roomId
                 openIds:(NSArray *)openIds
             isForbidden:(BOOL)isForbidden;

/// 是否屏蔽玩家语音
/// @param openId 玩家openId
/// @param isMuted 是否屏蔽玩家语音
- (void)mutePlayer:(NSString *)openId isMuted:(BOOL)isMuted;

/// 是否屏蔽所有玩家语音
/// @param roomId 房间Id
/// @param openIds 被禁止的openId数组
/// @param isMuted 是否被屏蔽
- (void)muteAllPlayers:(NSString *)roomId
               openIds:(NSArray *)openIds
               isMuted:(BOOL)isMuted;

@end

NS_ASSUME_NONNULL_END
