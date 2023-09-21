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
#import "PlayerLocationModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface PlayerLocationCache : NSObject
/// 语音接受范围
@property(nonatomic, assign) NSInteger audioRecvRange;

/// 当前玩家位置
@property(nonatomic, strong) SelfLocationModel *selfLocationModel;

/// 其他玩家位置
@property(nonatomic, strong) NSArray<OtherLocationModel *> *otherLocationModel;
@end

NS_ASSUME_NONNULL_END
