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

#import "UserIdLoginView.h"

@interface UserIdLoginView ()
<
UITextFieldDelegate
>

/// 标题
@property (nonatomic, strong) UILabel *titleLabel;

/// 文本输入框
@property (nonatomic, strong) UITextField *userIdTextField;

/// 初始化引擎按钮
@property (nonatomic, strong) UIButton *engineButton;

/// 存储输入的userID
@property (nonatomic, copy) NSString *userID;

@end

@implementation UserIdLoginView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupView];
    }
    return self;
}

- (void)setupView {
    [self addSubview:self.titleLabel];
    [self addSubview:self.userIdTextField];
    [self addSubview:self.engineButton];
    
    [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.top.equalTo(self).offset(45);
    }];
    [self.userIdTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.top.equalTo(self.titleLabel.mas_bottom).offset(20);
        make.width.mas_equalTo(SCREENWIDTH * 0.6);
        make.height.equalTo(@40);
    }];
    [self.engineButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self);
        make.top.equalTo(self.userIdTextField.mas_bottom).offset(24);
        make.width.mas_equalTo(SCREENWIDTH * 0.5);
        make.height.equalTo(@45);
    }];
}

/// 收起键盘
/// @param touches
/// @param event
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.userIdTextField resignFirstResponder];
}

#pragma mark - engineButton pressed
- (void)engineButtonPressed:(UIButton *)button {
    button.enabled = NO;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        button.enabled = YES;
    });
    [self.userIdTextField resignFirstResponder];
    if (self.delegate && [self.delegate respondsToSelector:@selector(initEnginePressed:button:userID:)]) {
        [self.delegate initEnginePressed:self button:button userID:self.userID];
    }
}

#pragma mark - UITextFieldDelegate
- (void)textFieldDidEndEditing:(UITextField *)textField {
    self.userID = textField.text;
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.font = [UIFont systemFontOfSize:25];
        _titleLabel.text = @"华为多媒体引擎演示";
        [_titleLabel sizeToFit];
    }
    return _titleLabel;
}

- (UITextField *)userIdTextField {
    if (!_userIdTextField) {
        _userIdTextField = [[UITextField alloc] init];
        _userIdTextField.borderStyle = UITextBorderStyleNone;
        _userIdTextField.background = [UIImage imageNamed:@"bg_input"];
        _userIdTextField.textColor = [UIColor whiteColor];
        _userIdTextField.placeholder = @"请输入用户ID，开始引擎初始化";
        _userIdTextField.delegate = self;
        _userIdTextField.font = [UIFont systemFontOfSize:13];
        [_userIdTextField setValue:[UIColor hw_placeholderColor] forKeyPath:@"placeholderLabel.textColor"];
    }
    return _userIdTextField;
}

- (UIButton *)engineButton {
    if (!_engineButton) {
        _engineButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_engineButton setTitle:@"引擎初始化" forState:UIControlStateNormal];
        [_engineButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _engineButton.titleLabel.font = [UIFont systemFontOfSize:12];
        [_engineButton setBackgroundImage:[UIImage imageNamed:@"bt_initengine_normal"] forState:UIControlStateNormal];
        [_engineButton setBackgroundImage:[UIImage imageNamed:@"bt_initengine_down"] forState:UIControlStateHighlighted];
        [_engineButton addTarget:self action:@selector(engineButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _engineButton;
}

@end
