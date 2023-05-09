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
#import "HWPGMEKit/HWPGMEngineDelegate.h"

NS_ASSUME_NONNULL_BEGIN

@interface HWPGMEDelegate : NSObject
/// 回调集合
@property (nonatomic, strong) NSMutableArray *delegateArray;

- (void)addDelegate:(NSObject *)delegate;

- (void)removeDelegate:(NSObject *)delegate;

/// 创建单例
+ (instancetype)getInstance;

+ (id)allocWithZone:(struct _NSZone*)zone NS_UNAVAILABLE;

+ (id)alloc NS_UNAVAILABLE;

+ (id)new NS_UNAVAILABLE;

- (id)copy NS_UNAVAILABLE;

@end

static HWPGMEDelegate* _hwDelegate = nil;

NS_ASSUME_NONNULL_END
