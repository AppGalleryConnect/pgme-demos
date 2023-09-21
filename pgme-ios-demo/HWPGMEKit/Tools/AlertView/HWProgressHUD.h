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

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface HWProgressHUD : UIView

/// 展示文案
/// - Parameters:
///   - inView: 需要展示到的view
///   - image: icon 可以为空
///   - info: 展示的文案
///   - duration: 倒计时时间
+ (void)showInView:(UIView *)inView
             Image:(UIImage *)image
           andInfo:(NSString *)info
       andDuration:(NSInteger)duration;

/// 展示文案
/// - Parameters:
///   - inView: 需要展示到的view
///   - info: 展示的文案
///   - duration: 倒计时时间
+ (void)showInView:(UIView *)inView
           andInfo:(NSString *)info
       andDuration:(NSInteger)duration;

/// 展示文案
/// - Parameters:
///   - inView: 需要展示到的view
///   - info: 展示的文案
+ (void)showInView:(UIView *)inView
           andInfo:(NSString *)info;

/// 移除倒计时框
+ (void)hiddenHUD;

@end

NS_ASSUME_NONNULL_END
