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
#import "PlayerLocationModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef void (^PlayerLocationCellUpdateLocationBlock)(OtherLocationModel *location);

typedef void (^PlayerLocationCellDeleteBlock)(void);

@interface PlayerLocationCell : UITableViewCell

/// 更新位置信息
@property(nonatomic, copy) PlayerLocationCellUpdateLocationBlock updateLocationBlock;

/// 删除按钮点击回调
@property(nonatomic, copy) PlayerLocationCellDeleteBlock deleteBlock;

/// 更新cell内容
/// @param model 玩家坐标对象
- (void)configCellData:(OtherLocationModel *)model;

@end

NS_ASSUME_NONNULL_END
