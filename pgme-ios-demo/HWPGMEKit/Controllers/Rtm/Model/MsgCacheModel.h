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

@interface MsgCacheModel : NSObject
/// RTM类型 1 P2P 2 CHANNEL
@property(nonatomic, assign) NSInteger rtmType;
/// 消息类型 1 文本 2 二进制
@property(nonatomic, assign) NSInteger messageType;
/// 消息内容 (二进制消息,也是以字符形式显示)
@property(nonatomic, strong) NSString *message;
@end

NS_ASSUME_NONNULL_END
