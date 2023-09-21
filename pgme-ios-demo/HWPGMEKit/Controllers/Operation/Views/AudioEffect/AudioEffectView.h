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

@class AudioEffectView;

NS_ASSUME_NONNULL_BEGIN

@protocol AudioEffectViewDelegate <NSObject>

@optional
/// 开始/停止 按钮点击
/// @param audioEffectView 底部按钮的view
/// @param playStopButton 功能切换按钮
- (void)audioEffectView:(AudioEffectView *_Nonnull)audioEffectView playStopButton:(UIButton *)playStopButton;

/// 暂停/恢复 按钮点击
/// @param audioEffectView 底部按钮的view
/// @param pauseResumeButton 功能切换按钮
- (void)audioEffectView:(AudioEffectView *_Nonnull)audioEffectView pauseResumeButton:(UIButton *)pauseResumeButton;

/// 音量改变回调
/// @param audioEffectView AudioEffectView实例
/// @param volume 音量
- (void)audioEffectView:(AudioEffectView *_Nonnull)audioEffectView volumeChanged:(int)volume;

@end

@interface AudioEffectView : UIView

@property(nonatomic, weak) id <AudioEffectViewDelegate> delegate;

@property(nonatomic, copy) NSString *audioName;

/// 音效是否在播放
@property(nonatomic, assign) BOOL isAudioEffectPlaying;

/// 关闭键盘
- (void)textFieldResignFirstResponder;

/// 更新音量
/// @param volume 当前音量大小
- (void)updateVolume:(int)volume;

/// 更新 开始/停止按钮的选中状态
/// @param play 选中状态
- (void)updatePlayStopButtonToPlay:(BOOL)play;

/// 更新 暂停/恢复按钮的选中状态
/// @param pause 选中状态
- (void)updatePauseResumeButtonToPause:(BOOL)pause;

/// 更新 暂停/恢复按钮是否可以点击
/// @param canClick 是否可以点击
- (void)updatePauseResumeBtnCanClick:(BOOL)canClick;

/// 获取播放次数
/// @return 设置的播放次数
- (int)getPlayCounts;

/// 获取音量
/// @return 设置的音量
- (int)getVolume;
@end

NS_ASSUME_NONNULL_END
