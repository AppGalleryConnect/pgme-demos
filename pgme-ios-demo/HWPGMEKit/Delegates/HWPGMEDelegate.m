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

- (void)onJoinRangeRoom:(NSString*)roomId
                   code:(int)code
                    msg:(NSString*)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onJoinRangeRoom:code:msg:)]) {
            [self.delegateArray[i] onJoinRangeRoom:roomId code:code msg:msg];
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

- (void)onUploadAudioMsgFile:(NSString *)filePath fileId:(NSString *)fileId code:(int)code msg:(NSString *)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onUploadAudioMsgFile:fileId:code:msg:)]) {
            [self.delegateArray[i] onUploadAudioMsgFile:filePath fileId:fileId code:code msg:msg];
        }
    }
}

- (void)onDownloadAudioMsgFile:(NSString *)filePath fileId:(NSString *)fileId code:(int)code msg:(NSString *)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onDownloadAudioMsgFile:fileId:code:msg:)]) {
            [self.delegateArray[i] onDownloadAudioMsgFile:filePath fileId:fileId code:code msg:msg];
        }
    }
}

- (void)onRecordAudioMsg:(NSString *)filePath code:(int)code msg:(NSString *)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onRecordAudioMsg:code:msg:)]) {
            [self.delegateArray[i] onRecordAudioMsg:filePath code:code msg:msg];
        }
    }
}

- (void)onPlayAudioMsg:(NSString *)filePath code:(int)code msg:(NSString *)msg {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onPlayAudioMsg:code:msg:)]) {
            [self.delegateArray[i] onPlayAudioMsg:filePath code:code msg:msg];
        }
    }
}

- (void)onAudioClipStateChangedNotify:(LocalAudioClipStateInfo *)stateInfo {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onAudioClipStateChangedNotify:)]) {
            [self.delegateArray[i] onAudioClipStateChangedNotify:stateInfo];
        }
    }
}

/// 订阅频道
/// @param result 结果
- (void)onSubscribeRtmChannel:(SubscribeRtmChannelResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onSubscribeRtmChannel:)]) {
            [self.delegateArray[i] onSubscribeRtmChannel:result];
        }
    }
}

/// 取消频道订阅
/// @param result 结果
- (void)onUnSubscribeRtmChannel:(UnSubscribeRtmChannelResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onUnSubscribeRtmChannel:)]) {
            [self.delegateArray[i] onUnSubscribeRtmChannel:result];
        }
    }
}

/// 推送频道消息
/// @param result 结果
- (void)onPublishRtmChannelMessage:(PublishRtmChannelMessageResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onPublishRtmChannelMessage:)]) {
            [self.delegateArray[i] onPublishRtmChannelMessage:result];
        }
    }
}

/// p2p消息
/// @param result 结果
- (void)onPublishRtmPeerMessage:(PublishRtmPeerMessageResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onPublishRtmPeerMessage:)]) {
            [self.delegateArray[i] onPublishRtmPeerMessage:result];
        }
    }
}

/// 获取频道信息结果
/// @param result 结果
- (void)onGetRtmChannelInfo:(GetRtmChannelInfoResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onGetRtmChannelInfo:)]) {
            [self.delegateArray[i] onGetRtmChannelInfo:result];
        }
    }
}

/// 接收频道消息
/// @param notify 消息
- (void)onReceiveRtmChannelMessage:(ReceiveRtmChannelMessageNotify *)notify {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onReceiveRtmChannelMessage:)]) {
            [self.delegateArray[i] onReceiveRtmChannelMessage:notify];
        }
    }
}

/// 接收peer消息
/// @param notify 消息
- (void)onReceiveRtmPeerMessage:(ReceiveRtmPeerMessageNotify *)notify {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onReceiveRtmPeerMessage:)]) {
            [self.delegateArray[i] onReceiveRtmPeerMessage:notify];
        }
    }
}

/// 状态消息
/// @param notify 消息
- (void)onRtmConnectionChanged:(RtmConnectionStatusNotify *)notify {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onRtmConnectionChanged:)]) {
            [self.delegateArray[i] onRtmConnectionChanged:notify];
        }
    }
}

/// 频道属性设置回调
- (void)onSetRtmChannelProperties:(SetRtmChannelPropertiesResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onSetRtmChannelProperties:)]) {
            [self.delegateArray[i] onSetRtmChannelProperties:result];
        }
    }
}

/// 频道属性查询回调
- (void)onGetRtmChannelProperties:(GetRtmChannelPropertiesResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onGetRtmChannelProperties:)]) {
            [self.delegateArray[i] onGetRtmChannelProperties:result];
        }
    }
}

/// 频道属性删除回调
- (void)onDeleteRtmChannelProperties:(DeleteRtmChannelPropertiesResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onDeleteRtmChannelProperties:)]) {
            [self.delegateArray[i] onDeleteRtmChannelProperties:result];
        }
    }
}

/// 频道内用户属性设置回调
- (void)onSetRtmChannelPlayerProperties:(SetRtmChannelPlayerPropertiesResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onSetRtmChannelPlayerProperties:)]) {
            [self.delegateArray[i] onSetRtmChannelPlayerProperties:result];
        }
    }
}

/// 频道内用户属性查询回调
- (void)onGetRtmChannelPlayerProperties:(GetRtmChannelPlayerPropertiesResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onGetRtmChannelPlayerProperties:)]) {
            [self.delegateArray[i] onGetRtmChannelPlayerProperties:result];
        }
    }
}

/// 频道内用户属性删除回调
- (void)onDeleteRtmChannelPlayerProperties:(DeleteRtmChannelPlayerPropertiesResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onDeleteRtmChannelPlayerProperties:)]) {
            [self.delegateArray[i] onDeleteRtmChannelPlayerProperties:result];
        }
    }
}


/// 获取频道历史消息回调
- (void)onGetRtmChannelHistoryMessages:(GetRtmChannelHistoryMessagesResult *)result {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onGetRtmChannelHistoryMessages:)]) {
            [self.delegateArray[i] onGetRtmChannelHistoryMessages:result];
        }
    }
}

/// 频道内用户属性变更通知
- (void)onRtmChannelPlayerPropertiesChanged:(RtmChannelPlayerPropertiesNotify *)notify {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onRtmChannelPlayerPropertiesChanged:)]) {
            [self.delegateArray[i] onRtmChannelPlayerPropertiesChanged:notify];
        }
    }
}


/// 频道属性变更通知
- (void)onRtmChannelPropertiesChanged:(RtmChannelPropertiesNotify *)notify {
    for (int i = 0; i < self.delegateArray.count; i++) {
        if (self.delegateArray[i] && [self.delegateArray[i] respondsToSelector:@selector(onRtmChannelPropertiesChanged:)]) {
            [self.delegateArray[i] onRtmChannelPropertiesChanged:notify];
        }
    }
}
@end
