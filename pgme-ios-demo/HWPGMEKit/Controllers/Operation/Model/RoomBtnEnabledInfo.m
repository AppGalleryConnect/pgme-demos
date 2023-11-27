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

#import "RoomBtnEnabledInfo.h"
#import <HWPGMEKit/HWPGMEObject.h>

@interface RoomBtnEnabledInfo()
@property(nonatomic, strong) NSMutableDictionary *btnEnabledDict;
@end
static NSString *playerArrayKey = @"playerArrayKey";
@implementation RoomBtnEnabledInfo

- (void)setObjectPlayerArray:(NSArray *)playerArray roomId:(NSString *)roomId {
    @synchronized (self) {
        [self.btnEnabledDict setObject:[NSMutableArray arrayWithArray:playerArray] forKey:roomId];
    }
}

- (NSArray *)playerArrayForKey:(NSString *)roomId {
    NSMutableArray *array = [self.btnEnabledDict objectForKey:roomId];
    return array ? array : [NSMutableArray array];
}


- (void)removeCacheRoom:(NSString *)roomId {
    NSMutableDictionary *dict = [self.btnEnabledDict objectForKey:roomId];
    if (!dict) return;
    @synchronized (self) {
        if ([self.btnEnabledDict.allKeys containsObject:roomId]) {
            [self.btnEnabledDict removeObjectForKey:roomId];
        }
    }
}

- (void)removeCachePlayerWithRoomId:(NSString *)roomId openId:(NSString *)openId {
    NSMutableArray *array = [self.btnEnabledDict objectForKey:roomId];
    if (!array) return;
    @synchronized (self) {
        [array enumerateObjectsUsingBlock:^(Player *player, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([player.openId isEqualToString:openId]) {
                [array removeObject:player];
                *stop = YES;
            }
        }];
    }
}

- (void)removeAllObject {
    @synchronized (self) {
        [self.btnEnabledDict removeAllObjects];
    }
}

- (NSMutableDictionary *)btnEnabledDict {
    if (!_btnEnabledDict) {
        _btnEnabledDict = [NSMutableDictionary dictionary];
    }
    return _btnEnabledDict;
}

@end
