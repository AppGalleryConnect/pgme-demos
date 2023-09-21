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

#import "LocationAlertView.h"

@interface LocationAlertView ()
@property(nonatomic, strong) UITextField *textField;
@end

@implementation LocationAlertView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.backgroundColor = [UIColor colorWithWhite:0.1 alpha:0.7];
    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_list"];
    bgImageView.userInteractionEnabled = YES;
    [self addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(self);
        make.width.mas_equalTo(self).multipliedBy(0.8);
        make.height.mas_equalTo(170);
    }];

    [bgImageView addSubview:self.textField];
    [self.textField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.mas_equalTo(bgImageView);
        make.height.mas_equalTo(40);
        make.top.mas_equalTo(bgImageView).offset(30);
        make.width.mas_equalTo(200);
    }];

    UIButton *confirmButton = [self commonButtonWithTitle:@"确定" imageName:@"bt_switch_normal" action:@selector(confirmFunc)];
    UIButton *cancellButton = [self commonButtonWithTitle:@"取消" imageName:@"bt_cancel_normal" action:@selector(closeAlertView)];
    [bgImageView addSubview:confirmButton];
    [confirmButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.textField.mas_bottom).offset(30);
        make.width.mas_equalTo(90);
        make.height.mas_equalTo(40);
        make.left.mas_equalTo(self.textField);
    }];

    [bgImageView addSubview:cancellButton];
    [cancellButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.width.height.mas_equalTo(confirmButton);
        make.right.mas_equalTo(self.textField);
    }];
}

- (UIButton *)commonButtonWithTitle:(NSString *)title imageName:(NSString *)imageName action:(SEL)action {
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    [button setTitle:title forState:UIControlStateNormal];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    button.titleLabel.font = [UIFont systemFontOfSize:16];
    [button setBackgroundImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
    button.layer.cornerRadius = 6;
    [button addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    return button;
}

#pragma mark func

/// 限制最大输入长度为 10
- (void)textFieldDidChange:(UITextField *)textField {
    if (textField == self.textField) {
        if (textField.text.length > 10) {
            textField.text = [textField.text substringToIndex:10];
        }
    }
}

/// 设置弹框类型
- (void)setType:(LocationAlertType)type {
    _type = type;
    NSString *placeholder = @"";
    if (type == LocationAlertAddPlayer) {
        self.textField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
        placeholder = @"请输入其他玩家";
    } else {
        self.textField.keyboardType = UIKeyboardTypeNumberPad;
        placeholder = @"请输入range";
    }
    NSMutableAttributedString *attr = [[NSMutableAttributedString alloc] initWithString:placeholder];
    [attr addAttributes:@{
            NSForegroundColorAttributeName: [UIColor whiteColor]
    }             range:NSMakeRange(0, placeholder.length)];
    self.textField.attributedPlaceholder = attr;
}

/// 设置默认值
- (void)setDefautValue:(NSString *)defautValue {
    _defautValue = defautValue;
    self.textField.text = defautValue;
}

/// 确认按钮点击
- (void)confirmFunc {
    if (self.confirmBlock) {
        self.confirmBlock(self.textField.text);
    }
}

/// 取消按钮点击
- (void)closeAlertView {
    self.textField.text = @"";
    if (self.superview) {
        [self removeFromSuperview];
    }
}

#pragma mark lazy

- (UITextField *)textField {
    if (!_textField) {
        _textField = [[UITextField alloc] init];
        _textField.textColor = [UIColor whiteColor];
        _textField.background = [UIImage imageNamed:@"bg_input"];
        [_textField addTarget:self action:@selector(textFieldDidChange:) forControlEvents:UIControlEventEditingChanged];
    }
    return _textField;
}
@end
