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

typedef NS_ENUM(NSInteger, LocationAlertType) {
    /// 添加玩家
    LocationAlertAddPlayer = 1,
    /// 语音接受范围
    LocationAlertAudioRecRange = 2,
};

typedef void(^LocationAlertViewConfirmBlock)(NSString *value);

@interface LocationAlertView : UIView
/// 弹框类型
@property(nonatomic, assign) LocationAlertType type;

/// 默认值
@property(nonatomic, strong) NSString *defautValue;

/// 确认按钮点击回调
@property(nonatomic, copy) LocationAlertViewConfirmBlock confirmBlock;

/// 关闭弹框
- (void)closeAlertView;
@end

NS_ASSUME_NONNULL_END
