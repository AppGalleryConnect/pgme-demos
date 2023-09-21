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
#import "AudioEffectAlertView.h"
#import "AudioEffectView.h"
#import "AllAudioEffectControlView.h"
#import "HWPGMEDelegate.h"
#import "Constants.h"
#import <HWPGMEKit/HWPGMEngine.h>

@interface AudioEffectAlertView () <AudioEffectViewDelegate, AllAudioEffectControlViewDelegate, HWPGMEngineDelegate>
/// 全部音效控制
@property(nonatomic, strong) AllAudioEffectControlView *effectControlView;

/// 音效1
@property(nonatomic, strong) AudioEffectView *effectViewOne;

/// 音效2
@property(nonatomic, strong) AudioEffectView *effectViewTwo;

/// 弹框视图
@property(nonatomic, strong) UIView *alertView;

/// 背景图片
@property(nonatomic, strong) UIImageView *bgImageView;
@end

@implementation AudioEffectAlertView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupSubviews];
    }
    return self;
}

- (void)setupSubviews {
    [HWPGMEDelegate.getInstance addDelegate:self];
    [self addSubview:self.alertView];
    [self.alertView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(self);
        make.width.mas_equalTo(self).multipliedBy(0.9);
        make.height.mas_equalTo(468);
    }];
    [self.alertView addSubview:self.bgImageView];
    [self.bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(self.alertView);
    }];
    [self.alertView addSubview:self.effectControlView];
    [self.effectControlView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.width.mas_equalTo(self.alertView);
        make.height.mas_equalTo(92);
    }];
    [self.alertView addSubview:self.effectViewOne];
    [self.effectViewOne mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(self.alertView);
        make.top.mas_equalTo(self.effectControlView.mas_bottom);
        make.height.mas_equalTo(188);
    }];
    [self.alertView addSubview:self.effectViewTwo];
    [self.effectViewTwo mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(self.alertView);
        make.top.mas_equalTo(self.effectViewOne.mas_bottom);
        make.height.mas_equalTo(188);
    }];
    self.alertView.alpha = 0;
}

- (void)showAlerOnView:(UIView *)view {
    self.frame = CGRectMake(0, 0, SCREENWIDTH, SCREENHEIGHT);
    [view addSubview:self];
    self.backgroundColor = [UIColor colorWithWhite:0.1 alpha:0.7];
    [UIView animateWithDuration:0.25 animations:^{
        self.alertView.alpha = 1;
    }];
}

- (void)dismiss {
    [UIView animateWithDuration:0.25 animations:^{
        self.backgroundColor = [UIColor clearColor];
        self.alertView.alpha = 0;
    }                completion:^(BOOL finished) {
        [self removeFromSuperview];
    }];
}

/// 引擎销毁
- (void)alertViewDestory {
    [HWPGMEDelegate.getInstance removeDelegate:self];
    if (self.superview) {
        [self removeFromSuperview];
    }
}

/// 点击背景移除弹窗
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self closeKeyboard];
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self.alertView];
    if (![self.alertView.layer containsPoint:point]) {
        [self dismiss];
    }
}

/// 关闭键盘
- (void)closeKeyboard {
    [self.effectViewOne textFieldResignFirstResponder];
    [self.effectViewTwo textFieldResignFirstResponder];
}

#pragma mark AudioEffectViewDelegate

/// 开始/停止 按钮点击回调
- (void)audioEffectView:(AudioEffectView *)audioEffectView playStopButton:(UIButton *)playStopButton {
    [self closeKeyboard];
    if (playStopButton.selected) {
        [self playLocalAudioClipEffectView:audioEffectView];
    } else {
        [self stopLocalAudioClipEffectView:audioEffectView];
    }
}

/// 暂停/恢复 按钮点击回调
- (void)audioEffectView:(AudioEffectView *)audioEffectView pauseResumeButton:(UIButton *)pauseResumeButton {
    [self closeKeyboard];
    if (pauseResumeButton.selected) {
        [self pauseLocalAudioClipEffectView:audioEffectView];
    } else {
        [self resumeLocalAudioClipEffectView:audioEffectView];
    }
}

/// 声音改变回调
- (void)audioEffectView:(AudioEffectView *)audioEffectView volumeChanged:(int)volume {
    [self closeKeyboard];
    if (audioEffectView.isAudioEffectPlaying) {
        [self setVolumeOfLocalAudio:audioEffectView volume:volume];
    } else  {
        [audioEffectView updateVolume:volume];
    }
}

#pragma mark AllAudioEffectControlViewDelegate

/// 全部停止点击
- (void)allAudioEffectControlView:(AllAudioEffectControlView *)controlView clickStopButton:(UIButton *)stopButton {
    [self stopAllLocalAudio];
}

/// 全部暂停点击
- (void)allAudioEffectControlView:(AllAudioEffectControlView *)controlView clickPauseButton:(UIButton *)pauseButton {
    [self pauseAllLocalAudio];
}

/// 全部恢复点击
- (void)allAudioEffectControlView:(AllAudioEffectControlView *)controlView clickResumeButton:(UIButton *)resumeButton {
    [self resumeAllLocalAudio];
}

/// 声音改变回调
- (void)allAudioEffectControlView:(AllAudioEffectControlView *)controlView volumeChanged:(int)volume {
    [self setLocalAudioVolume:volume];
}

#pragma mark HWPGMEngineDelegate

/// 音效播放状态改变回调
- (void)onAudioClipStateChangedNotify:(LocalAudioClipStateInfo *)stateInfo {
    dispatch_async(dispatch_get_main_queue(), ^{
        AudioEffectView *effectView = [self effectViewWithSoundId:stateInfo.soundId];
        switch (stateInfo.state) {
            // 音频正在播放
            case HW_AUDIO_CLIP_PLAYING: {
                [self changeEffectViewBtnStatusByPlay:effectView];
            }
                break;
                // 音频播放完成
            case HW_AUDIO_CLIP_PLAY_COMPLETED:
                // 音频停止播放
            case HW_AUDIO_CLIP_PLAY_STOPPED: {
                [self changeEffectViewStatusByStop:effectView];
            }
                break;
                // 音频播放失败
            case HW_AUDIO_CLIP_PLAY_FAILED: {
                [self changeEffectViewStatusByPlayFailed:effectView];
            }
                break;
            default:
                break;
        }
    });
}

#pragma mark rtc func

/// 播放本地音效
- (void)playLocalAudioClipEffectView:(AudioEffectView *)effectView {
    NSString *filePath = [[[NSBundle mainBundle] pathForResource:@"audioEffect" ofType:@"bundle"] stringByAppendingPathComponent:[self fileNameWithEffectView:effectView]];
    LocalAudioInfo *info = [[LocalAudioInfo alloc] init];
    info.soundId = [self soundIdWithEffectView:effectView];
    info.filePath = filePath;
    info.loop = [effectView getPlayCounts];
    info.volume = [effectView getVolume];
    int flag = [HWPGMEngine.getInstance playLocalAudioClip:info];
    if (flag != 0) {
        [effectView updatePlayStopButtonToPlay:YES];
    }
}

/// 停止播放本地音效
- (void)stopLocalAudioClipEffectView:(AudioEffectView *)effectView {
    int flag = [HWPGMEngine.getInstance stopLocalAudioClip:[self soundIdWithEffectView:effectView]];
    if (flag != 0) {
        [effectView updatePlayStopButtonToPlay:NO];
    }
}

/// 暂停播放本地音效
- (void)pauseLocalAudioClipEffectView:(AudioEffectView *)effectView {
    int flag = [HWPGMEngine.getInstance pauseLocalAudioClip:[self soundIdWithEffectView:effectView]];
    if (flag != 0) {
        [effectView updatePauseResumeButtonToPause:YES];
    }
}

/// 从暂停中恢复音效
- (void)resumeLocalAudioClipEffectView:(AudioEffectView *)effectView {
    int flag = [HWPGMEngine.getInstance resumeLocalAudioClip:[self soundIdWithEffectView:effectView]];
    if (flag != 0) {
        [effectView updatePauseResumeButtonToPause:NO];
    }
}

/// 停止播放所有本地音效
- (void)stopAllLocalAudio {
    int flag = [HWPGMEngine.getInstance stopAllLocalAudioClips];
    if (flag == 0) {
        [self clearAllButtonStatus];
    }
}

/// 暂停播放所有本地音效
- (void)pauseAllLocalAudio {
    int flag = [HWPGMEngine.getInstance pauseAllLocalAudioClips];
    if (flag == 0) {
        [self changeButtonStatusAfterPauseOrResume:NO];
    }
}

/// 从暂停中恢复所有本地音效
- (void)resumeAllLocalAudio {
    int flag = [HWPGMEngine.getInstance resumeAllLocalAudioClips];
    if (flag == 0) {
        [self changeButtonStatusAfterPauseOrResume:YES];
    }
}

/// 设置所有本地音效播放文件的总音量,实际播放音量=总音量*自身音量 / 100，默认100.
- (void)setLocalAudioVolume:(int)volume {
    [HWPGMEngine.getInstance setLocalAudioClipsVolume:volume];

    // 获取本地音效总音量
    int audioVolume = [HWPGMEngine.getInstance getLocalAudioClipsVolume];
    if (audioVolume != -1) {
        [self.effectControlView updateVolume:audioVolume];
    }
}

/// 设置本地音效id的音量
- (void)setVolumeOfLocalAudio:(AudioEffectView *)effectView volume:(int)volume {
    int soundId = [self soundIdWithEffectView:effectView];
    [HWPGMEngine.getInstance setVolumeOfLocalAudioClip:soundId volume:volume];

    // 获取指定soundId本地音效音量
    int audioVolume = [HWPGMEngine.getInstance getVolumeOfLocalAudioClip:soundId];
    if (audioVolume == -1) return;
    [effectView updateVolume:audioVolume];
}

/// 开始播放后,修改按钮状态
- (void)changeEffectViewBtnStatusByPlay:(AudioEffectView *)effectView {
    [effectView updatePauseResumeBtnCanClick:YES];
    [effectView updatePauseResumeButtonToPause:YES];
    effectView.isAudioEffectPlaying = YES;
}

/// 停止播放后,修改按钮状态
- (void)changeEffectViewStatusByStop:(AudioEffectView *)effectView {
    [effectView updatePlayStopButtonToPlay:YES];
    [effectView updatePauseResumeButtonToPause:YES];
    [effectView updatePauseResumeBtnCanClick:NO];
    effectView.isAudioEffectPlaying = NO;
}

/// 播放失败, 修改按钮状态
- (void)changeEffectViewStatusByPlayFailed:(AudioEffectView *)effectView {
    effectView.isAudioEffectPlaying = NO;
}

/// 全部暂停/全部恢复 后修改 暂停/恢复按钮状态
- (void)changeButtonStatusAfterPauseOrResume:(BOOL)status {
    if (self.effectViewOne.isAudioEffectPlaying) {
        [self.effectViewOne updatePauseResumeButtonToPause:status];
    }
    if (self.effectViewTwo.isAudioEffectPlaying) {
        [self.effectViewTwo updatePauseResumeButtonToPause:status];
    }
}

/// 清空所有按钮的状态
- (void)clearAllButtonStatus {
    [self changeEffectViewStatusByStop:self.effectViewOne];
    [self changeEffectViewStatusByStop:self.effectViewTwo];
}

- (NSString *)fileNameWithEffectView:(AudioEffectView *)effectView {
    return effectView == self.effectViewOne ? @"video1.mp3" : @"video2.3gp";
}

- (int)soundIdWithEffectView:(AudioEffectView *)effectView {
    return effectView == self.effectViewOne ? 1 : 2;
}

- (AudioEffectView *)effectViewWithSoundId:(int)soundId {
    if (soundId == 1) {
        return self.effectViewOne;
    } else if (soundId == 2) {
        return self.effectViewTwo;
    } else {
        return nil;
    }
}

- (AllAudioEffectControlView *)effectControlView {
    if (!_effectControlView) {
        _effectControlView = [[AllAudioEffectControlView alloc] init];
        _effectControlView.delegate = self;
    }
    return _effectControlView;
}

- (AudioEffectView *)effectViewOne {
    if (!_effectViewOne) {
        _effectViewOne = [[AudioEffectView alloc] init];
        _effectViewOne.delegate = self;
        _effectViewOne.audioName = @"音效1";
    }
    return _effectViewOne;
}

- (AudioEffectView *)effectViewTwo {
    if (!_effectViewTwo) {
        _effectViewTwo = [[AudioEffectView alloc] init];
        _effectViewTwo.delegate = self;
        _effectViewTwo.audioName = @"音效2";
    }
    return _effectViewTwo;
}

- (UIView *)alertView {
    if (!_alertView) {
        _alertView = [[UIView alloc] init];
    }
    return _alertView;
}

- (UIImageView *)bgImageView {
    if (!_bgImageView) {
        _bgImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_list"]];
        _bgImageView.userInteractionEnabled = YES;
    }
    return _bgImageView;
}
@end
