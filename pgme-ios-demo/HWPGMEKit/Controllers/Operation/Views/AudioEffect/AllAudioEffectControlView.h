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

@class AllAudioEffectControlView;
NS_ASSUME_NONNULL_BEGIN

@protocol AllAudioEffectControlViewDelegate <NSObject>

@optional
/// 停止按钮点击事件回调
/// @param controlView AllAudioEffectControlView实例
/// @param stopButton 按钮
- (void)allAudioEffectControlView:(AllAudioEffectControlView *_Nonnull)controlView clickStopButton:(UIButton *)stopButton;

/// 暂停按钮点击事件回调
/// @param controlView AllAudioEffectControlView实例
/// @param pauseButton 按钮
- (void)allAudioEffectControlView:(AllAudioEffectControlView *_Nonnull)controlView clickPauseButton:(UIButton *)pauseButton;

/// 恢复按钮点击事件回调
/// @param controlView AllAudioEffectControlView实例
/// @param resumeButton 按钮
- (void)allAudioEffectControlView:(AllAudioEffectControlView *_Nonnull)controlView clickResumeButton:(UIButton *)resumeButton;

/// 音量改变回调
/// @param controlView AllAudioEffectControlView实例
/// @param volume 音量
- (void)allAudioEffectControlView:(AllAudioEffectControlView *_Nonnull)controlView volumeChanged:(int)volume;
@end

@interface AllAudioEffectControlView : UIView

@property(nonatomic, weak) id <AllAudioEffectControlViewDelegate> delegate;

/// 更新音量
/// @param volume 当前音量大小
- (void)updateVolume:(int)volume;
@end

NS_ASSUME_NONNULL_END
