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
#import "HWPGMEObject.h"

NS_ASSUME_NONNULL_BEGIN

@interface PlayerListTableViewCell : UITableViewCell

/// 加载玩家数据cell
/// @param indexPath  列表索引
/// @param roomInfo 房间信息
/// @param player 玩家信息
- (void)cellWithIndexPath:(NSIndexPath *)indexPath
                roomInfo:(Room *)roomInfo
                  player:(Player *)player;

@end

NS_ASSUME_NONNULL_END
