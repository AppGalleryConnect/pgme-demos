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

@interface HWVoiceAlertView : UIView

/// 语音文本
@property (nonatomic, nullable) NSString *voiceText;

/// 确认按钮回调
@property (nonatomic, copy, readonly) void (^enterButtonBlock)();

/// 弹窗类方法
/// @param title 标题
/// @param enter 确认按钮回调
+ (HWVoiceAlertView *)alert:(void (^)())enter;
@end

NS_ASSUME_NONNULL_END
