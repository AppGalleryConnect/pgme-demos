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

@protocol HWDropDownMenuDelegate <NSObject>

/// 下拉菜单选择代理
/// @param view 下拉菜单view
/// @param indexPath 选择的索引
- (void)HWDropDownMenu:(UIView *)view didSelectRowAtIndexPath:(NSIndexPath *)indexPath;

@end

@interface HWDropDownMenu : UIView

/// 列表
@property (nonatomic, strong) UITableView *tableView;

/// 数据源
@property (nonatomic, strong, readonly) NSArray *titles;

/// 需要添加view的rect
@property (nonatomic, assign, readonly) CGRect viewRect;

/// 代理
@property (nonatomic, weak) id<HWDropDownMenuDelegate> delegate;

/// 圆角半径 Default is 6.0
@property (nonatomic, assign) CGFloat cornerRadius;

/// 是否显示背景灰色覆盖层 Default  YES
@property (nonatomic, assign) BOOL showMaskView;

/// item的高度 Default  50;
@property (nonatomic, assign) CGFloat itemHeight;

/// 背景图片
@property (nonatomic, strong) UIImage *bgImage;

/// 指定依赖的view弹出
/// @param view 依赖的view
/// @param titles 弹出菜单数据源
/// @param delegate 代理
/// @param otherSettings 其他设置
+ (HWDropDownMenu *)showOnView:(UIView *)view titles:(NSArray *)titles delegate:(id<HWDropDownMenuDelegate>)delegate otherSettings:(void (^)(HWDropDownMenu *dropDownMenu))otherSettings;

@end

NS_ASSUME_NONNULL_END
