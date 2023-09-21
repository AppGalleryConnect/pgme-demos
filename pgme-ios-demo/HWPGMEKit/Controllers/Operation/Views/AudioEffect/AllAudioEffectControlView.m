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

#import "AllAudioEffectControlView.h"

@interface AllAudioEffectControlView ()
/// 进度条
@property(nonatomic, strong) UISlider *slider;

/// 音量
@property(nonatomic, strong) UILabel *volumeLabel;

/// 全部停止
@property(nonatomic, strong) UIButton *stopButton;

/// 全部暂停
@property(nonatomic, strong) UIButton *pauseButton;

/// 全部恢复
@property(nonatomic, strong) UIButton *resumeButton;
@end

@implementation AllAudioEffectControlView
static const CGFloat SPACE = 12.0;

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    UIView *volumeView = [[UIView alloc] init];
    [self addSubview:volumeView];
    [volumeView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self).offset(SPACE);
        make.right.mas_equalTo(self).offset(-SPACE);
        make.top.mas_equalTo(self);
        make.height.mas_equalTo(50);
    }];
    [volumeView addSubview:self.volumeLabel];
    [self.volumeLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(volumeView);
    }];
    [volumeView addSubview:self.slider];
    [self.slider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.right.mas_equalTo(volumeView);
        make.left.mas_equalTo(self.volumeLabel.mas_right).offset(SPACE);
        make.height.mas_equalTo(30);
    }];
    UIView *buttonView = [[UIView alloc] init];
    [self addSubview:buttonView];
    [buttonView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(volumeView.mas_bottom);
        make.left.mas_equalTo(self).offset(SPACE);
        make.right.mas_equalTo(self).offset(-SPACE);
        make.height.mas_equalTo(42);
    }];
    [buttonView addSubview:self.stopButton];
    [buttonView addSubview:self.pauseButton];
    [buttonView addSubview:self.resumeButton];
    NSArray *buttonArray = [NSArray arrayWithObjects:self.stopButton, self.pauseButton, self.resumeButton, nil];
    [buttonArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:20 leadSpacing:0 tailSpacing:0];
    [buttonArray mas_updateConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@42);
    }];
}

/// 停止按钮点击
- (void)stopButtonClick:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(allAudioEffectControlView:clickStopButton:)]) {
        [self.delegate allAudioEffectControlView:self clickStopButton:button];
    }
}

/// 暂停按钮点击
- (void)pauseButtonClick:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(allAudioEffectControlView:clickPauseButton:)]) {
        [self.delegate allAudioEffectControlView:self clickPauseButton:button];
    }
}

/// 恢复按钮点击
- (void)resumeButtonClick:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(allAudioEffectControlView:clickResumeButton:)]) {
        [self.delegate allAudioEffectControlView:self clickResumeButton:button];
    }
}

/// 音量改变
- (void)sliderValueChanged:(UISlider *)slider {
    // 四舍五入, 不需要中间的小数
    int value = (int) roundf(slider.value);
    slider.value = value;
    if (self.delegate && [self.delegate respondsToSelector:@selector(allAudioEffectControlView:volumeChanged:)]) {
        [self.delegate allAudioEffectControlView:self volumeChanged:value];
    }
}

/// 更新音量
- (void)updateVolume:(int)volume {
    self.volumeLabel.text = [NSString stringWithFormat:@"音量(%d)", volume];
}

- (UIButton *)buttonWithTitle:(NSString *)title action:(SEL)action {
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setTitle:title forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    btn.titleLabel.font = [UIFont systemFontOfSize:16];
    [btn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
    [btn setBackgroundImage:[UIImage imageNamed:@"bt_switch_down"] forState:UIControlStateHighlighted];
    btn.layer.cornerRadius = 6;
    [btn addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    return btn;
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
        _volumeLabel = [[UILabel alloc] init];
        _volumeLabel.font = [UIFont systemFontOfSize:16];
        _volumeLabel.numberOfLines = 1;
        _volumeLabel.textColor = [UIColor whiteColor];
        _volumeLabel.text = @"音量(100)";
    }
    return _volumeLabel;
}

- (UIButton *)stopButton {
    if (!_stopButton) {
        _stopButton = [self buttonWithTitle:@"全部停止" action:@selector(stopButtonClick:)];
    }
    return _stopButton;
}

- (UIButton *)pauseButton {
    if (!_pauseButton) {
        _pauseButton = [self buttonWithTitle:@"全部暂停" action:@selector(pauseButtonClick:)];
    }
    return _pauseButton;
}


- (UIButton *)resumeButton {
    if (!_resumeButton) {
        _resumeButton = [self buttonWithTitle:@"全部恢复" action:@selector(resumeButtonClick:)];
    }
    return _resumeButton;
}
@end
