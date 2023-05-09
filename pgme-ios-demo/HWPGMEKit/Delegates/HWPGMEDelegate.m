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

#import "HWPGMEDelegate.h"
#import <objc/runtime.h>

@interface HWPGMEDelegate ()<HWPGMEngineDelegate>

@end

@implementation HWPGMEDelegate

- (void)addDelegate:(NSObject *)delegate {
    [self.delegateArray addObject:delegate];
}

- (void)removeDelegate:(NSObject *)delegate {
    [self.delegateArray removeObject:delegate];
}

+ (HWPGMEDelegate *)getInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _hwDelegate = [class_createInstance([self class], 0) initWithSingleton:true];
        _hwDelegate.delegateArray = [[NSMutableArray alloc]init];
    });
    return _hwDelegate;
}

+ (id)allocWithZone:(struct _NSZone*)zone {
    return [self getInstance];
}

- (id)copy {
    return [[self class] getInstance];
}

- (instancetype)initWithSingleton:(BOOL)singleton {
    self = [super init];
    return self;
}

- (instancetype)init {
    NSCAssert(FALSE, @"There can only be one instance.");
    return nil;
}


#pragma mark - HWPGMEngineDelegate
- (void)onCreate:(int)code
             msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onCreate:msg:)]) {
            [self.delegateArray[i] onCreate:code msg:msg];
        }
    }
}

- (void)onJoinTeamRoom:(NSString*)roomId
                  code:(int)code
                   msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onJoinTeamRoom:code:msg:)]) {
            [self.delegateArray[i] onJoinTeamRoom:roomId code:code msg:msg];
        }
    }
}

- (void)onJoinNationalRoom:(NSString*)roomId
                      code:(int)code
                       msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onJoinNationalRoom:code:msg:)]) {
            [self.delegateArray[i] onJoinNationalRoom:roomId code:code msg:msg];
        }
    }
}

- (void)onLeaveRoom:(NSString*)roomId
               code:(int)code
                msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onLeaveRoom:code:msg:)]) {
            [self.delegateArray[i] onLeaveRoom:roomId code:code msg:msg];
        }
    }
}

- (void)onDestory:(int)code
              msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onDestory:msg:)]) {
            [self.delegateArray[i] onDestory:code msg:msg];
        }
    }
}

- (void)onSpeakerDetection:(NSMutableArray<NSString*> *)openIds {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onSpeakerDetection:)]) {
            [self.delegateArray[i] onSpeakerDetection:openIds];
        }
    }
}

- (void)onSpeakerDetectionEx:(NSMutableArray<VolumeInfo *> *)userVolumeInfos {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onSpeakerDetectionEx:)]) {
            [self.delegateArray[i] onSpeakerDetectionEx:userVolumeInfos];
        }
    }
}

- (void)onForbiddenByOwner:(NSString*)roomId
                   openIds:(NSMutableArray<NSString*> *)openIds
               isForbidden:(Boolean)isForbidden {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onForbiddenByOwner:openIds:isForbidden:)]) {
            [self.delegateArray[i] onForbiddenByOwner:roomId openIds:openIds isForbidden:isForbidden];
        }
    }
}

- (void)onForbidPlayer:(NSString*)roomId
                openId:(NSString*)openId
           isForbidden:(BOOL)isForbidden
                  code:(int)code
                   msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onForbidPlayer:openId:isForbidden:code:msg:)]) {
            [self.delegateArray[i] onForbidPlayer:roomId openId:openId isForbidden:isForbidden code:code msg:msg];
        }
    }
}

- (void)onForbidAllPlayers:(NSString*)roomId
                   openIds:(NSArray*)openIds
               isForbidden:(BOOL)isForbidden
                      code:(int)code
                       msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onForbidAllPlayers:openIds:isForbidden:code:msg:)]) {
            [self.delegateArray[i] onForbidAllPlayers:roomId openIds:openIds isForbidden:isForbidden code:code msg:msg];
        }
    }
}

- (void)onSwitchRoom:(NSString *)roomId
                code:(int)code
                 msg:(NSString *)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onSwitchRoom:code:msg:)]) {
            [self.delegateArray[i] onSwitchRoom:roomId code:code msg:msg];
        }
    }
}

- (void)onMutePlayer:(NSString*)roomId
              openId:(NSString*)openId
             isMuted:(BOOL)isMuted
                code:(int)code
                 msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onMutePlayer:openId:isMuted:code:msg:)]) {
            [self.delegateArray[i] onMutePlayer:roomId openId:openId isMuted:isMuted code:code msg:msg];
        }
    }
}

- (void)onMuteAllPlayers:(NSString*)roomId
                 openIds:(NSArray*)openIds
                 isMuted:(BOOL)isMuted
                    code:(int)code
                     msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onMuteAllPlayers:openIds:isMuted:code:msg:)]) {
            [self.delegateArray[i] onMuteAllPlayers:roomId openIds:openIds isMuted:isMuted code:code msg:msg];
        }
    }
}

- (void)onPlayerOnline:(NSString*)roomId
                openId:(NSString*)openId {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onPlayerOnline:openId:)]) {
            [self.delegateArray[i] onPlayerOnline:roomId openId:openId];
        }
    }
}

- (void)onPlayerOffline:(NSString*)roomId
                 openId:(NSString*)openId {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onPlayerOffline:openId:)]) {
            [self.delegateArray[i] onPlayerOffline:roomId openId:openId];
        }
    }
}

- (void)onTransferOwner:(NSString*)roomId
                   code:(int)code
                    msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onTransferOwner:code:msg:)]) {
            [self.delegateArray[i] onTransferOwner:roomId code:code msg:msg];
        }
    }
}
- (void)onVoiceToText:(NSString *)text code:(int)code msg:(NSString *)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onVoiceToText:code:msg:)]) {
            [self.delegateArray[i] onVoiceToText:text code:code msg:msg];
        }
    }
}

-(void) onRemoteMicroStateChanged:(NSString *)roomId openId:(NSString *)openId isMute:(BOOL)isMute {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onRemoteMicroStateChanged:openId:isMute:)]) {
            [self.delegateArray[i] onRemoteMicroStateChanged:roomId openId:openId isMute:isMute];
        }
    }
}
@end
