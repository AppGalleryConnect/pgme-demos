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

#import "RtmMsgCache.h"

@interface RtmMsgCache ()
@property(nonatomic, strong) NSMutableDictionary *cache;
@end

@implementation RtmMsgCache

- (void)cacheMsg:(NSString *)clientMsgId cacheModel:(MsgCacheModel *)cacheModel {
    [self.cache setValue:cacheModel forKey:clientMsgId];
}

- (NSString *)getCacheMsgWithClientMsgId:(NSString *)clientMsgId {
    if ([self.cache objectForKey:clientMsgId]) {
        MsgCacheModel *model = [self.cache objectForKey:clientMsgId];
        return [self msgWithModel:model];
    }
    return @"";
}

- (NSString *)msgWithModel:(MsgCacheModel *)model {
    NSString *msgType = @"[二进制]";
    if (model.messageType == 1) {
        msgType = @"[文本]";
    }
    return [NSString stringWithFormat:@"%@%@", msgType, model.message];
}

- (NSString *)getPeerReceiveMessage:(ReceiveRtmPeerMessageNotify *)notify {
    NSString *messageType = @"[二进制]";
    NSString *msgContent = @"";
    if (notify.messageType == 1) {
        messageType = @"[文本]";
        msgContent = notify.messageString;
    } else {
        msgContent = [[NSString alloc] initWithData:notify.messageBytes encoding:NSUTF8StringEncoding];
    }
    return [NSString stringWithFormat:@"[%@]来自%@的消息: %@%@", [self getCurrentTime], notify.senderId, messageType, msgContent];
}

- (NSString *)getChannelReceiveMessage:(ReceiveRtmChannelMessageNotify *)notify {
    NSString *messageType = @"[二进制]";
    NSString *msgContent = @"";
    if (notify.messageType == 1) {
        messageType = @"[文本]";
        msgContent = notify.messageString;
    } else {
        msgContent = [[NSString alloc] initWithData:notify.messageBytes encoding:NSUTF8StringEncoding];
    }
    return [NSString stringWithFormat:@"[%@][channelId=%@]来自%@的消息: %@%@", [self getCurrentTime],notify.channelId, notify.senderId, messageType, msgContent];
}

/// 历史消息拼接
- (NSString *)getHistoryMessage:(RtmChannelHistoryMessage *)message {
    NSString *messageType = @"[二进制]";
    NSString *msgContent = @"";
    if (message.messageType == 1) {
        messageType = @"[文本]";
        msgContent = message.messageString;
    } else {
        msgContent = [[NSString alloc] initWithData:message.messageBytes encoding:NSUTF8StringEncoding];
    }
    return [NSString stringWithFormat:@"[历史消息][%@]来自%@的消息: %@%@", [self getTimeFromTimestamp:message.timestamp], message.senderId, messageType, msgContent];
}

- (NSString *)getTimeFromTimestamp:(long)timestamp {
    NSDate *date = [NSDate dateWithTimeIntervalSince1970:(timestamp/1000)];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    return [formatter stringFromDate:date];
}

- (NSString *)getCurrentTime {
    NSDate *now = [NSDate date];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"H:mm:ss"];
    return [dateFormatter stringFromDate:now];
}

- (NSMutableDictionary *)cache {
    if (!_cache) {
        _cache = [NSMutableDictionary dictionary];
    }
    return _cache;
}
@end
