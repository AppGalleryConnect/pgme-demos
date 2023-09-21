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

/// 字体大小
#define kFontSize                    16

/// 字体颜色
#define kFontColor                   @"0xf5f5f5"

/// icon宽高
#define kImageHeight                 30

/// 与父视图间隙
#define kMargin                      16

@interface HWProgressMaskView : UIView

// icon imageView
@property(nonatomic, strong) UIImageView *iconImageView;

// 内容
@property(nonatomic, strong) UILabel *infoLabel;

// icon图片
@property(nonatomic, strong) UIImage *image;

// 提示内容
@property(nonatomic, strong) NSString *infoContent;

/// 设置内容
- (void)setDataContent;

/// 更新内容
- (void)upDataContent;

@end

NS_ASSUME_NONNULL_END
