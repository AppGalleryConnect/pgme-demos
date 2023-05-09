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
@class Room;

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, kNowViewPage) {
    kNowViewIsLog,              // 查看日志
    kNowViewIsPlayerList,       // 玩家列表
};

typedef NS_ENUM(NSInteger, kPlayerState) {
    kPlayerMicState,            // 玩家麦克风状态
    kPlayerSpeakState,          // 玩家语音状态
};

@interface OperationListView : UIView

/// 当前展示的列表
@property (nonatomic, assign) kNowViewPage nowViewPage;

/// 添加日志
/// @param log 日志信息
- (void)addLog:(NSString *)log;

/// 日志清屏
- (void)clearAllLog;

/// 根据房间信息刷新用户列表
/// @param roomInfo  Room对象
/// @param ownerId  房主的openId
- (void)refreshPlayerListWithRoomInfo:(Room * _Nullable)roomInfo ownerId:(NSString *)ownerId;

/// 刷新正在说话的玩家列表
/// @param users 正在说话的玩家列表
- (void)speakingUsers:(NSArray *)users;

/// 修改一个玩家状态
/// @param roomId 房间Id
/// @param openId 玩家openId
/// @param playerState 麦克风或语音状态
/// @param isEnable 是否禁止
- (void)changePlayerStateWithRoomId:(NSString *)roomId
                             openId:(NSString *)openId
                        playerState:(kPlayerState)playerState
                           isEnable:(BOOL)isEnable;

/// 修改全部玩家状态
/// @param roomId 房间Id
/// @param openId 玩家openId
/// @param playerState 麦克风或语音状态
/// @param isEnable 是否屏蔽
- (void)changeAllPlayerStateWithRoomId:(NSString *)roomId
                               openIds:(NSArray *)openIds
                           playerState:(kPlayerState)playerState
                              isEnable:(BOOL)isEnable;

@end

NS_ASSUME_NONNULL_END
