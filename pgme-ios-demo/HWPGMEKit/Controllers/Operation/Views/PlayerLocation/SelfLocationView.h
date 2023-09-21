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

typedef void (^SelfLocationViewUpdateLocationBlock)(SelfLocationModel *model);

@interface SelfLocationView : UIView

/// 设置当前玩家的位置/朝向 信息
/// @param model 玩家位置对象
- (void)configData:(SelfLocationModel *)model;

/// 更新当前玩家位置信息
@property(nonatomic, copy) SelfLocationViewUpdateLocationBlock updateLocationBlock;
@end

NS_ASSUME_NONNULL_END
