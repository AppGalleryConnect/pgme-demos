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

#import "HWVoiceAlertView.h"

@interface HWVoiceAlertView()
/// 弹窗视图
@property (nonatomic, strong) UIView *alertView;

/// 背景图片
@property (nonatomic, strong) UIImageView *bgImageView;

/// 语音文字和倒计时视图
@property (nonatomic, strong) UITextView *voiceTextView;

/// 语音文字和倒计时背景图片
@property (nonatomic, strong) UIImageView *voiceBgImageView;

/// 取消按钮
@property (nonatomic, strong) UIButton *cancelButton;

/// 确认按钮
@property (nonatomic, strong) UIButton *enterButton;

/// 倒计时
@property (nonatomic, strong) dispatch_source_t timer;

@end

@implementation HWVoiceAlertView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.alertView];
        [self.alertView addSubview:self.bgImageView];
        [self.bgImageView addSubview:self.voiceBgImageView];
        [self.bgImageView addSubview:self.voiceTextView];
        [self.bgImageView addSubview:self.cancelButton];
        [self.bgImageView addSubview:self.enterButton];
    }
    return self;
}

+ (HWVoiceAlertView *)alert:(void (^)())enter {
    HWVoiceAlertView *alertView = [[HWVoiceAlertView alloc] init];
    alertView.enterButtonBlock = enter;
    [alertView show];
    return alertView;
}

- (void)updateUI {
    self.frame = CGRectMake(0, 0, SCREENWIDTH, SCREENHEIGHT);
    self.backgroundColor = [UIColor colorWithWhite:0.1 alpha:0.7];
    self.alpha = 0;
    
    self.alertView.frame = CGRectMake(0, 0, SCREENWIDTH * 0.88, 260);
    self.alertView.center = MainWindow.center;
    self.alertView.alpha = 0;
    
    self.bgImageView.frame = CGRectMake(0, 0, self.alertView.frame.size.width, self.alertView.frame.size.height);
    
    self.voiceBgImageView.frame = CGRectMake(30, 55, self.bgImageView.frame.size.width - 60, 120);
    
    self.voiceTextView.frame = CGRectMake(self.voiceBgImageView.frame.origin.x, self.voiceBgImageView.frame.origin.y, self.voiceBgImageView.frame.size.width, self.voiceBgImageView.frame.size.height);
    
    CGFloat btn_y = CGRectGetMaxY(self.voiceBgImageView.frame) + 16;
    CGFloat btn_width = 115;
    CGFloat btn_height = 40;
    self.cancelButton.frame = CGRectMake(36, btn_y, btn_width, btn_height);
    self.enterButton.frame = CGRectMake(self.bgImageView.frame.size.width - 36 - btn_width, btn_y, btn_width, btn_height);
}

#pragma mark - set
- (void)setVoiceText:(NSString *)voiceText {
    [self stopTimer];
    _voiceText = voiceText;
    _voiceTextView.text = voiceText;
    _enterButton.enabled = false;
}

- (void)setEnterButtonBlock:(void (^)())enterButtonBlock {
    _enterButtonBlock = enterButtonBlock;
}

/// 弹出弹窗
- (void)show {
    [self updateUI];
    [MainWindow addSubview:self];
    __weak typeof(self) weakSelf = self;
    [UIView animateWithDuration:0.25 animations:^{
        weakSelf.alpha = 1;
        weakSelf.alertView.alpha = 1;
    }];
    [self recordingCountdown];
}

- (void)stopCountDownAndRecording {
    [self stopTimer];
    if (self.enterButtonBlock) {
        self.enterButtonBlock();
    }
}

/// 销毁弹窗
- (void)dismiss {
    [self stopCountDownAndRecording];
    __weak typeof(self) weakSelf = self;
    [UIView animateWithDuration:0.25 animations:^{
        weakSelf.backgroundColor = [UIColor clearColor];
        weakSelf.alertView.alpha = 0;
    } completion:^(BOOL finished) {
        [weakSelf removeFromSuperview];
    }];
}

/// 点击背景移除弹窗
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    if (![touch.view isEqual:self.bgImageView]) {
        [self dismiss];
    }
}

#pragma mark - event
- (void)cancelButtonPressed {
    [self dismiss];
}

- (void)enterButtonPressed:(UIButton *)button {
    [self stopCountDownAndRecording];
    [self setVoiceLabelConverting];
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

- (UIImageView *)voiceBgImageView {
    if (!_voiceBgImageView) {
        _voiceBgImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_alert_content"]];
        _voiceBgImageView.userInteractionEnabled = YES;
    }
    return _voiceBgImageView;
}

- (UITextView *)voiceTextView {
    if (!_voiceTextView) {
        _voiceTextView = [[UITextView alloc] init];
        _voiceTextView.font = [UIFont systemFontOfSize:16 weight:UIFontWeightMedium];
        _voiceTextView.backgroundColor = [UIColor clearColor];
        _voiceTextView.textColor = [UIColor hw_alertTitleColor];
        _voiceTextView.editable = NO;
    }
    return _voiceTextView;
}

- (UIButton *)cancelButton {
    if (!_cancelButton) {
        _cancelButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_cancelButton setTitle:@"取消" forState:UIControlStateNormal];
        [_cancelButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _cancelButton.titleLabel.font = [UIFont systemFontOfSize:16];
        _cancelButton.layer.cornerRadius = 6;
        [_cancelButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
        [_cancelButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_down"] forState:UIControlStateHighlighted];
        [_cancelButton addTarget:self action:@selector(cancelButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cancelButton;
}

- (UIButton *)enterButton {
    if (!_enterButton) {
        _enterButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_enterButton setTitle:@"结束" forState:UIControlStateNormal];
        [_enterButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _enterButton.titleLabel.font = [UIFont systemFontOfSize:16];
        _enterButton.layer.cornerRadius = 6;
        [_enterButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_enterButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_down"] forState:UIControlStateHighlighted];
        [_enterButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_disable"] forState:UIControlStateDisabled];
        [_enterButton addTarget:self action:@selector(enterButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _enterButton;
}

-(void)recordingCountdown {
    // 倒计时10s，定时器间隔1s执行一次，count取10
    __block int timeCount = 10;
    self.timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0,
                                        dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0));
    dispatch_source_set_timer(self.timer, DISPATCH_TIME_NOW, 1 * NSEC_PER_SEC, 0);
    dispatch_source_set_event_handler(self.timer, ^{
        if (timeCount == 0) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self setVoiceLabelConverting];
            });
            [self stopTimer];
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                // 异步执行，timeCount从9开始倒计，需要加1，才能从10s->1s
                _voiceTextView.text = [NSString stringWithFormat:@"剩余：%ds", timeCount + 1];
            });
            timeCount--;
        }
    });
    dispatch_resume(self.timer);
}

-(void)stopTimer {
    if (self.timer) {
        dispatch_cancel(self.timer);
    }
}

-(void)setVoiceLabelConverting {
    _voiceTextView.text = @"正在转换中";
}
@end
