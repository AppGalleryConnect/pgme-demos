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

#import "Player+RoomPlayer.h"
#import <objc/runtime.h>

static const void *objc_is_speaking = @"objc_is_speaking";
static const void *objc_is_mute = @"objc_is_mute";
static const void *objc_is_forbidden_btn_disabled = @"objc_is_forbidden_btn_disabled";

@implementation Player (RoomPlayer)

- (void)setIsSpeaking:(BOOL)isSpeaking {
    NSNumber *num = [NSNumber numberWithBool:isSpeaking];
    objc_setAssociatedObject(self, &objc_is_speaking, num, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

- (BOOL)isSpeaking {
    return [objc_getAssociatedObject(self, &objc_is_speaking) boolValue];
}

- (void)setIsMute:(BOOL)isMute {
    NSNumber *num = [NSNumber numberWithBool:isMute];
    objc_setAssociatedObject(self, &objc_is_mute, num, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

- (BOOL)isMute {
    return [objc_getAssociatedObject(self, &objc_is_mute) boolValue];
}

- (void)setIsForbiddenBtnDisabled:(BOOL)isForbiddenBtnDisabled {
    NSNumber *num = [NSNumber numberWithBool:isForbiddenBtnDisabled];
    objc_setAssociatedObject(self, &objc_is_forbidden_btn_disabled, num, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

- (BOOL)isForbiddenBtnDisabled {
    return [objc_getAssociatedObject(self, &objc_is_forbidden_btn_disabled) boolValue];
}
@end
