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

@interface UIColor (HWColor)

/// 从十六进制字符串获取颜色，
/// @param color 支持@“#123456”、 @“0X123456”、 @“123456”三种格式
+ (UIColor *)colorWithString:(NSString *)color;

/// 从十六进制字符串获取颜色，
/// @param color :支持@“#123456”、 @“0X123456”、 @“123456”三种格式
/// @param alpha <#alpha description#>
+ (UIColor *)colorWithString:(NSString *)color alpha:(CGFloat)alpha;

/// textField的站位文字颜色
+ (UIColor *)hw_placeholderColor;

/// 标题颜色
+ (UIColor *)hw_titleColor;

/// 文字颜色
+ (UIColor *)hw_textColor;

/// 房主信息颜色
+ (UIColor *)hw_roomOwnerColor;

/// 玩家信息颜色
+ (UIColor *)hw_playerColor;

/// 弹窗标题颜色
+ (UIColor *)hw_alertTitleColor;

/// 弹窗选项文字颜色
+ (UIColor *)hw_alertTextColor;

@end

NS_ASSUME_NONNULL_END
