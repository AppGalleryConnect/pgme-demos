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

@protocol OperationInitRoomViewDelegate <NSObject>

@optional
/// 加入小队 按钮点击代理
/// @param initRoomView 顶部操作view
/// @param teamButton 加入小队按钮
/// @param roomID 房间ID
- (void)teamButtonPressed:(UIView *)initRoomView teamButton:(UIButton *)teamButton roomID:(NSString *)roomID;

/// 加入国战 按钮点击代理
/// @param initRoomView 顶部操作view
/// @param nationalWarButton 加入国战按钮
/// @param roomID 房间ID
- (void)nationalWarButtonPressed:(UIView *)initRoomView nationalWarButton:(UIButton *)nationalWarButton roomID:(NSString *)roomID;

/// 加入范围语音 按钮点击代理
/// @param initRoomView 顶部操作view
/// @param joinRangeButton 加入范围按钮
/// @param roomID 房间ID
- (void)joinRangeButtonPressed:(UIView *)initRoomView joinRangeButton:(UIButton *)joinRangeButton roomID:(NSString *)roomID;

/// 离开房间 按钮点击代理
/// @param initRoomView 顶部操作view
/// @param leaveRoomButton 离开房间按钮
- (void)leaveRoomButtonPressed:(UIView *)initRoomView leaveRoomButton:(UIButton *)leaveRoomButton;

/// 开/关麦克风 按钮点击代理
/// @param initRoomView 顶部操作view
/// @param turnonMicButton 开/关麦克风按钮
- (void)turnonMicButtonPressed:(UIView *)initRoomView turnonMicButton:(UIButton *)turnonMicButton;

/// 语音转文字 按钮点击代理
/// @param initRoomView 顶部操作view
/// @param voiceToTextButton 语音转文字按钮
- (void)voiceToTextButtonPressed:(UIView *)initRoomView voiceToTextButton:(UIButton *)voiceToTextButton;

/// 房间切换 按钮点击代理
/// @param initRoomView 顶部操作view
/// @param switchRoomTextField 房间切换TextField
- (void)switchRoomTextFieldPressed:(UIView *)initRoomView switchRoomTextField:(UITextField *)switchRoomTextField;

@end

@interface OperationInitRoomView : UIView

@property (nonatomic, weak) id<OperationInitRoomViewDelegate> delegate;

/// 设置显示的房间ID
@property (nonatomic, copy) NSString *roomID;

/// 设置离开房间按钮是否可点击
@property (nonatomic, assign) BOOL isLeaveButtonDisable;

@end

NS_ASSUME_NONNULL_END
