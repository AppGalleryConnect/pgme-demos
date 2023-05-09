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

#import "RoomIdList.h"

@implementation RoomIdList
{
    dispatch_queue_t concurrentQueue;
    NSMutableArray *roomIdMArr;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        concurrentQueue = dispatch_queue_create("Room_ID_List_QUEUE", DISPATCH_QUEUE_CONCURRENT);
        roomIdMArr = [NSMutableArray array];
    }
    return self;
}

#pragma mark - getter
- (NSArray *)dataList {
    return roomIdMArr;
}

#pragma mark - 多读单写

/// 查询数据 
/// @param index 查询数据的下标
- (id)objectAtIndex:(NSUInteger)index {
    __block id obj;
    // 同步获取数据
    dispatch_sync(concurrentQueue, ^{
        // 防止数组越界
        if (index < roomIdMArr.count) {
            NSMutableArray *tempArr = [self mutableArrayValueForKeyPath:@"roomIdMArr"];
            obj = [tempArr objectAtIndex:index];
        }
    });
    return obj;
}

/// 获取指定索引的数据并将数据放在第一位
/// @param index 索引
- (id)switchObjectAtIndex:(NSUInteger)index {
    __block id obj;
    // 同步获取数据
    dispatch_sync(concurrentQueue, ^{
        // 防止数组越界
        if (index < roomIdMArr.count) {
            NSMutableArray *tempArr = [self mutableArrayValueForKeyPath:@"roomIdMArr"];
            obj = [tempArr objectAtIndex:index];
            NSInteger tempIndex = 0;
            // 将查询的数据放到第一位，将其他数据依次排序
            while (index > tempIndex) {
                [tempArr exchangeObjectAtIndex:index withObjectAtIndex:tempIndex];
                tempIndex ++;
            }
        }
    });
    return obj;
}

/// 新增数据 并将新数据放在第一位 当数组容量大于5时删除最后一项数据
/// @param object 新增的数据
- (void)addObject:(id)object {
    dispatch_barrier_async(concurrentQueue, ^{
        [[self mutableArrayValueForKeyPath:@"roomIdMArr"] insertObject:object atIndex:0];
        if (roomIdMArr.count > 5) {
            [[self mutableArrayValueForKeyPath:@"roomIdMArr"] removeLastObject];
        }
    });
}


/// 删除数据
- (void)removeObjectAtIndex:(NSUInteger)index {
    dispatch_sync(concurrentQueue, ^{
        if (roomIdMArr.count > 0) {        
            [[self mutableArrayValueForKeyPath:@"roomIdMArr"] removeObjectAtIndex:index];
        }
    });
}

@end
