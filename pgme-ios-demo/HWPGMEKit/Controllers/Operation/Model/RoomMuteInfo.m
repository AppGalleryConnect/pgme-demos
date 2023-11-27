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

#import "RoomMuteInfo.h"
#import <HWPGMEKit/HWPGMEngine.h>


@interface RoomMuteInfo ()
@property(nonatomic, strong) NSMutableDictionary *muteDict;
@property(nonatomic, strong) NSMutableDictionary *forbiddenAllDict;
@end

@implementation RoomMuteInfo
static NSString *muteArrayKey = @"muteArrayKey";
static NSString *allPlayerIsMuteKey = @"allPlayerIsMuteKey";

- (void)setObjectMuteArray:(NSMutableArray *)muteArray allPlayerIsMute:(BOOL)allPlayerIsMute roomId:(NSString *)roomId {
    @synchronized (self) {
        NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithDictionary:@{
                muteArrayKey: muteArray,
                allPlayerIsMuteKey: @(allPlayerIsMute)
        }];
        [self.muteDict setObject:dict forKey:roomId];
    }
}

- (NSMutableArray *)muteArrayForKey:(NSString *)roomId {
    NSMutableDictionary *dict = [self.muteDict objectForKey:roomId];
    if (!dict) return [NSMutableArray array];
    if ([dict objectForKey:muteArrayKey]) {
        return [dict objectForKey:muteArrayKey];
    }
    return [NSMutableArray array];
}

- (BOOL)allPlayerIsMuteForKey:(NSString *)roomId {
    NSMutableDictionary *dict = [self.muteDict objectForKey:roomId];
    if (!dict) return NO;
    if ([dict objectForKey:allPlayerIsMuteKey]) {
        return [[dict objectForKey:allPlayerIsMuteKey] boolValue];
    }
    return NO;
}

- (void)setRoomIdArray:(NSArray *)roomIdArray {
    @synchronized (self) {
        _roomIdArray = roomIdArray;
        for (NSString *roomId in self.muteDict.allKeys) {
            if (![roomIdArray containsObject:roomId]) {
                /// 玩家退出了当前房间, 移除缓存信息
                [self.muteDict removeObjectForKey:roomId];
            }
        }
    }
}

- (void)updateMuteArray:(NSMutableArray *)muteArray roomId:(NSString *)roomId {
    @synchronized (self) {
        NSMutableDictionary *dict = [self.muteDict objectForKey:roomId];
        if (dict) {
            [dict setValue:muteArray forKey:muteArrayKey];
        }
    }
}

- (void)removeCacheRoom:(NSString *)roomId {
    NSMutableDictionary *dict = [self.muteDict objectForKey:roomId];
    if (!dict) return;
    @synchronized (self) {
        if ([self.muteDict.allKeys containsObject:roomId]) {
            [self.muteDict removeObjectForKey:roomId];
        }
    }
}

- (void)removeCachePlayerWithRoomId:(NSString *)roomId openId:(NSString *)openId {
    NSMutableDictionary *dict = [self.muteDict objectForKey:roomId];
    if (!dict) return;
    @synchronized (self) {
        NSMutableArray *muteArray = [self muteArrayForKey:roomId];
        [muteArray enumerateObjectsUsingBlock:^(Player *player, NSUInteger idx, BOOL * _Nonnull stop) {
            if ([player.openId isEqualToString:openId]) {
                [muteArray removeObject:player];
                *stop = YES;
            }
        }];
        [dict setObject:muteArray forKey:muteArrayKey];
    }
    
}

- (void)removeAllObject {
    @synchronized (self) {
        [self.muteDict removeAllObjects];
        [self.forbiddenAllDict removeAllObjects];
    }
}

- (void)setForbiddenAll:(BOOL)forbiddenAll roomId:(NSString *)roomId {
    @synchronized (self) {
        [self.forbiddenAllDict setValue:@(forbiddenAll) forKey:roomId];
    }
}

- (void)removeForbiddenAllForKey:(NSString *)roomId {
    @synchronized (self) {
        [self.forbiddenAllDict removeObjectForKey:roomId];
    }
}

- (BOOL)allPlayerIsForbiddenForKey:(NSString *)roomId {
    return [[self.forbiddenAllDict objectForKey:roomId] boolValue];
}

- (NSMutableDictionary *)muteDict {
    if (!_muteDict) {
        _muteDict = [NSMutableDictionary dictionary];
    }
    return _muteDict;
}

- (NSMutableDictionary *)forbiddenAllDict {
    if (!_forbiddenAllDict) {
        _forbiddenAllDict = [NSMutableDictionary dictionary];
    }
    return _forbiddenAllDict;
}
@end
