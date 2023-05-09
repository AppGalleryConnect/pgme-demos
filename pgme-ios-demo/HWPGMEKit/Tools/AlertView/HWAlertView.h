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

@interface HWAlertView : UIView

/// 标题
@property (nonatomic, nullable, readonly) NSString *title;

/// 选项数据源
@property (nonatomic, strong, readonly) NSArray *optionsArray;

/// 已选中选项的索引
@property (nonatomic, assign, readonly) NSInteger selectedIndex;

/// 确认按钮回调
@property (nonatomic, copy, readonly) void (^enterButtonBlock)(NSInteger index);

/// 弹窗类方法
/// @param title 标题
/// @param options 选项数据源
/// @param enter 确认按钮回调
+ (HWAlertView *)alertWithTitle:(NSString *)title isMustSelect:(BOOL)isMustSelect options:(NSArray *)options enter:(void (^)(NSInteger index))enter;

@end

NS_ASSUME_NONNULL_END
