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

#import <HWPGMEKit/HWPGMEObject.h>

NS_ASSUME_NONNULL_BEGIN

@interface Player (RoomPlayer)

/// 玩家是否正在说话
@property (nonatomic, assign) BOOL isSpeaking;

/// 玩家声音是否被屏蔽
@property (nonatomic, assign) BOOL isMute;

/// 禁言按钮是否可以点击
@property(nonatomic, assign) BOOL isForbiddenBtnDisabled;
@end

NS_ASSUME_NONNULL_END
