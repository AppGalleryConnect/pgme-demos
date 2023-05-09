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

@interface RoomIdList : NSObject

@property (nonatomic, strong, readonly) NSArray *dataList;

/// 添加数据
/// @param object 要添加的对象
- (void)addObject:(id)object;

/// 获取指定索引的数据
/// @param index 索引
- (id)objectAtIndex:(NSUInteger)index;

/// 获取指定索引的数据并将数据放在第一位
/// @param index 索引
- (id)switchObjectAtIndex:(NSUInteger)index;

/// 删除数据
/// @param index 索引
- (void)removeObjectAtIndex:(NSUInteger)index;

@end

NS_ASSUME_NONNULL_END
