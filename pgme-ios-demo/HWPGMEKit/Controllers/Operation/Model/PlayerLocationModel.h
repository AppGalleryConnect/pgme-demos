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

@interface PositionModel : NSObject <NSCoding>
/// 前 对应z
@property(nonatomic, assign) NSInteger forward;
/// 右 对应x
@property(nonatomic, assign) NSInteger right;
/// 上 对应y
@property(nonatomic, assign) NSInteger up;
@end

@interface AxisModel : NSObject <NSCoding>
@property(nonatomic, assign) NSInteger forward;
@property(nonatomic, assign) NSInteger right;
@property(nonatomic, assign) NSInteger up;
@end


@interface SelfLocationModel : NSObject <NSCoding>
/// 玩家ID
@property(nonatomic, strong) NSString *openId;
@property(nonatomic, strong) PositionModel *position;
@property(nonatomic, strong) AxisModel *axis;
@end

@interface OtherLocationModel : NSObject <NSCoding>
/// 玩家ID
@property(nonatomic, strong) NSString *openId;
@property(nonatomic, strong) PositionModel *position;
@end

NS_ASSUME_NONNULL_END
