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

@interface PlayerHeaderView : UIView


/// 设置headerView
/// @param roomType 房间类型
/// @param playerCount 玩家人数
/// @param isForbidden 是否禁止所有玩家使用麦克风
/// @param isMute 是否屏蔽所有玩家语音
- (void)headerTitleWithRoomType:(NSString *)roomType
                    playerCount:(NSInteger)playerCount
           allPlayerIsForbidden:(BOOL)isForbidden
           allPlayerSpeakIsMute:(BOOL)isMute;

/// 修改3D音效button 是否可点击
/// @param enable YES:可点击 NO：不可点击
- (void)changeSpatialAudioButtonEnable:(BOOL)enable;

/// 更新3D音效按钮选中状态
/// @param selected true：选中 false：未选中
- (void)updateSpatialAudioButtonSelected:(BOOL)selected;

/// 是否隐藏语音按钮
@property (nonatomic, assign) BOOL isMicHidden;

/// 是否隐藏转移房主按钮
@property (nonatomic, assign) BOOL isOwnerTransferHidden;

/// 语音按钮是否可点击
@property (nonatomic, assign) BOOL isMicEnable;

@end

NS_ASSUME_NONNULL_END
