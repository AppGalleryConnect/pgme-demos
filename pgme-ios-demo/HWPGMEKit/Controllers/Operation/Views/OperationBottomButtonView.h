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

@protocol OperationBottomButtonViewDelegate <NSObject>

/// 查看日志/房间成员 按钮切换代理
/// @param buttonView 底部按钮的view
/// @param switchButton 功能切换按钮
- (void)switchButtonPressed:(UIView *)buttonView switchButton:(UIButton *)switchButton;

/// 引擎销毁 按钮点击代理
/// @param buttonView  底部按钮的view
/// @param destoryButton 引擎销毁按钮
- (void)destoryButtonPressed:(UIView *)buttonView destoryButton:(UIButton *)destoryButton;

/// 语音消息 按钮点击代理
/// @param buttonView  底部按钮的view
/// @param audioMsgButton 语音消息按钮
- (void)audioMsgButtonPressed:(UIView *)buttonView audioMsgButton:(UIButton *)audioMsgButton;

/// 音效 按钮点击代理
/// @param buttonView  底部按钮的view
/// @param audioEffectButton 音效按钮
- (void)audioEffectButtonPressed:(UIView *)buttonView audioEffectButton:(UIButton *)audioEffectButton;

/// 玩家位置 按钮点击代理
/// @param buttonView  底部按钮的view
/// @param playerPositonButton 玩家位置按钮
- (void)playerPositionButtonPressed:(UIView *)buttonView playerPositionButton:(UIButton *)playerPositionButton;

@end

@interface OperationBottomButtonView : UIView

/// 代理
@property (nonatomic, weak) id<OperationBottomButtonViewDelegate> delegate;

/// 获取view总高度
/// @return view高度
- (CGFloat)operationBottomViewHeight;

@end

NS_ASSUME_NONNULL_END
