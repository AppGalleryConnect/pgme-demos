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
#import "PlayerLocationModel.h"
NS_ASSUME_NONNULL_BEGIN

typedef void(^PlayerLocationUpdateSelfLocationBlock)(SelfLocationModel *model);
typedef void(^PlayerLocationUpdateOthersLocationBlock)(NSArray<OtherLocationModel *> *models);
typedef void(^PlayerLocationUpdateRangeBlock)(NSInteger range);
@interface PlayerLocationViewController : HWBaseViewController

/// 设置位置和范围
/// @param selfModel 自身位置
/// @param othersModel 其他玩家位置
/// @param audioRecvRange 语音接受范围
- (void)configDataSelfModel:(SelfLocationModel *)selfModel
                othersModel:(NSArray *)othersModel
             audioRecvRange:(NSInteger)audioRecvRange;

/// 更新当前玩家位置信息
@property(nonatomic, copy) PlayerLocationUpdateSelfLocationBlock updateSelfLocationBlock;

/// 更新其他玩家位置信息
@property(nonatomic, copy) PlayerLocationUpdateOthersLocationBlock updateOthersLocationBlock;

/// 更新语音接受范围
@property(nonatomic, copy) PlayerLocationUpdateRangeBlock updateAudioRecvRangeBlock;

@end

NS_ASSUME_NONNULL_END
