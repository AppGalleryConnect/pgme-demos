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

#import "AudioEffectView.h"

@interface AudioEffectView()<UITextFieldDelegate>
@property(nonatomic, strong) UILabel *audioLabel;

/// 音量大小
@property(nonatomic, strong) UILabel *volumeLabel;

/// 播放次数输入框
@property(nonatomic, strong) UITextField *textField;

/// 音量进度条
@property(nonatomic, strong) UISlider *slider;

/// 播放/停止
@property (nonatomic, strong) UIButton *playStopButton;

/// 暂停/恢复
@property (nonatomic, strong) UIButton *pauseResumeButton;
@end

@implementation AudioEffectView
static const CGFloat HORIZONTAL_SPACE = 12.0;
static const CGFloat VERTICAL_SPACE = 10.0;
- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_list"];
    bgImageView.userInteractionEnabled = YES;
    [self addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(HORIZONTAL_SPACE, HORIZONTAL_SPACE, HORIZONTAL_SPACE, HORIZONTAL_SPACE));
    }];
    [bgImageView addSubview:self.audioLabel];
    [self.audioLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(bgImageView).offset(HORIZONTAL_SPACE);
        make.right.mas_equalTo(bgImageView).offset(-HORIZONTAL_SPACE);
        make.height.mas_equalTo(20);
    }];

    UIView *playInfoView = [[UIView alloc] init];
    [bgImageView addSubview:playInfoView];
    [playInfoView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.audioLabel.mas_bottom).offset(VERTICAL_SPACE);
        make.left.right.mas_equalTo(self.audioLabel);
        make.height.mas_equalTo(50);
    }];
    [self setupPlayInfoView:playInfoView];
    
    UIView *buttonView = [[UIView alloc] init];
    [bgImageView addSubview:buttonView];
    [buttonView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(playInfoView.mas_bottom).offset(VERTICAL_SPACE);
        make.left.right.mas_equalTo(self.audioLabel);
        make.height.mas_equalTo(50);
    }];
    [self setupButtonView:buttonView];
}

- (void)setupPlayInfoView:(UIView *)playInfoView {
    UIView *playCountView = [[UIView alloc] init];
    [playInfoView addSubview:playCountView];
    [playCountView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.height.mas_equalTo(playInfoView);
        make.width.mas_equalTo(playInfoView).multipliedBy(0.4);
    }];
    UILabel *playCountLabel = [self labelWithTitle:@"播放次数"];
    [playCountLabel setContentHuggingPriority:UILayoutPriorityRequired forAxis:UILayoutConstraintAxisHorizontal];
    [playCountView addSubview:playCountLabel];
    [playCountLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.left.mas_equalTo(playCountView);
    }];
    [playCountView addSubview:self.textField];
    [self.textField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(playCountLabel.mas_right);
        make.centerY.right.mas_equalTo(playCountView);
        make.height.mas_equalTo(36);
    }];

    UIView *volumeView = [[UIView alloc] init];
    [playInfoView addSubview:volumeView];
    [volumeView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(playCountView.mas_right).offset(10);
        make.right.top.height.mas_equalTo(playInfoView);
    }];
    [volumeView addSubview:self.volumeLabel];
    [self.volumeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(volumeView);
    }];
    [volumeView addSubview:self.slider];
    [self.slider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.volumeLabel.mas_right);
        make.centerY.right.mas_equalTo(volumeView);
        make.height.mas_equalTo(30);
    }];
}

- (void)setupButtonView:(UIView *)buttonView {
    [buttonView addSubview:self.playStopButton];
    [buttonView addSubview:self.pauseResumeButton];
    NSArray *buttonArray = [NSArray arrayWithObjects:self.playStopButton,self.pauseResumeButton, nil];
    [buttonArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:40 leadSpacing:0 tailSpacing:0];
    [buttonArray mas_updateConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@42);
    }];
}

/// UITextFieldDelegate
- (void)textFieldDidEndEditing:(UITextField *)textField {
    if (!StringEmpty(textField.text)) {
        textField.text = @"1";
    }
}

/// 音量进度条value改变回调
- (void)sliderValueChanged:(UISlider *)slider {
    // 四舍五入, 不需要中间的小数
    int value = (int) roundf(slider.value);
    slider.value = value;
    if (self.delegate && [self.delegate respondsToSelector:@selector(audioEffectView:volumeChanged:)]) {
        [self.delegate audioEffectView:self volumeChanged:value];
    }
}

/// 开始/停止按钮点击
- (void)playStopClickFunc:(UIButton *)button {
    button.selected = !button.selected;
    if (self.delegate && [self.delegate respondsToSelector:@selector(audioEffectView:playStopButton:)]) {
        [self.delegate audioEffectView:self playStopButton:button];
    }
}

/// 暂停/恢复按钮点击
- (void)pauseResumeClickFunc:(UIButton *)button {
    button.selected = !button.selected;
    if (self.delegate && [self.delegate respondsToSelector:@selector(audioEffectView:pauseResumeButton:)]) {
        [self.delegate audioEffectView:self pauseResumeButton:button];
    }
}

/// 设置音效名称
- (void)setAudioName:(NSString *)audioName {
    _audioName = audioName;
    self.audioLabel.text = audioName;
}

/// 关闭键盘
- (void)textFieldResignFirstResponder {
    if (self.textField.isFirstResponder) {
        [self.textField resignFirstResponder];
    }
}

/// 更新音量
- (void)updateVolume:(int)volume {
    self.volumeLabel.text = [NSString stringWithFormat:@"音量(%d)", volume];
}

/// 获取音量
- (int)getVolume {
    return (int) self.slider.value;
}

/// 获取播放次数
- (int)getPlayCounts {
    return [self.textField.text intValue];
}

/// 更新 开始/停止按钮的选中状态
- (void)updatePlayStopButtonToPlay:(BOOL)play {
    self.playStopButton.selected = !play;
}

/// 更新 暂停/恢复按钮的选中状态
- (void)updatePauseResumeButtonToPause:(BOOL)pause {
    self.pauseResumeButton.selected = !pause;
}

/// 更新 暂停/恢复按钮是否可以点击
- (void)updatePauseResumeBtnCanClick:(BOOL)canClick {
    self.pauseResumeButton.enabled = canClick;
}

- (UILabel *)labelWithTitle:(NSString *)title {
    UILabel *label = [[UILabel alloc] init];
    label.text = title;
    label.font = [UIFont systemFontOfSize:16];
    label.numberOfLines = 1;
    label.textColor = [UIColor whiteColor];
    return label;
}

- (UILabel *)audioLabel {
    if (!_audioLabel) {
        _audioLabel = [[UILabel alloc] init];
        _audioLabel.font = [UIFont systemFontOfSize:16];
        _audioLabel.numberOfLines = 1;
        _audioLabel.textColor = [UIColor whiteColor];
    }
    return _audioLabel;
}

- (UIButton *)playStopButton {
    if (!_playStopButton) {
        _playStopButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_playStopButton setTitle:@"播放" forState:UIControlStateNormal];
        [_playStopButton setTitle:@"停止" forState:UIControlStateSelected];
        [_playStopButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _playStopButton.titleLabel.font = [UIFont systemFontOfSize:16];
        [_playStopButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_playStopButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_down"] forState:UIControlStateHighlighted];
        _playStopButton.layer.cornerRadius = 6;
        [_playStopButton addTarget:self action:@selector(playStopClickFunc:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _playStopButton;
}

- (UIButton *)pauseResumeButton {
    if (!_pauseResumeButton) {
        _pauseResumeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_pauseResumeButton setTitle:@"暂停" forState:UIControlStateNormal];
        [_pauseResumeButton setTitle:@"恢复" forState:UIControlStateSelected];
        [_pauseResumeButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _pauseResumeButton.titleLabel.font = [UIFont systemFontOfSize:16];
        [_pauseResumeButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_pauseResumeButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_down"] forState:UIControlStateHighlighted];
        _pauseResumeButton.layer.cornerRadius = 6;
        [_pauseResumeButton addTarget:self action:@selector(pauseResumeClickFunc:) forControlEvents:UIControlEventTouchUpInside];
        _pauseResumeButton.enabled = NO;
    }
    return _pauseResumeButton;
}

- (UITextField *)textField {
    if (!_textField) {
        _textField = [[UITextField alloc] init];
        _textField.text = @"1";
        _textField.textColor = [UIColor whiteColor];
        _textField.background = [UIImage imageNamed:@"bg_input"];
        _textField.keyboardType = UIKeyboardTypeNumberPad;
        _textField.delegate = self;
    }
    return _textField;
}

- (UISlider *)slider {
    if (!_slider) {
        _slider = [[UISlider alloc] init];
        _slider.minimumValue = 0.0;
        _slider.maximumValue = 100.0;
        _slider.value = 100.0;
        [_slider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];
    }
    return _slider;
}

- (UILabel *)volumeLabel {
    if (!_volumeLabel) {
        _volumeLabel = [self labelWithTitle:@"音量(100)"];
    }
    return _volumeLabel;
}
@end
