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

#import "OperationBottomButtonView.h"

@interface OperationBottomButtonView ()

/// 查看日志/房间成员切换按钮
@property (nonatomic, strong) UIButton *switchButton;

/// 引擎销毁按钮
@property (nonatomic, strong) UIButton *destoryButton;

@end

@implementation OperationBottomButtonView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.switchButton];
    [self addSubview:self.destoryButton];
    
    NSArray *buttonArray = [NSArray arrayWithObjects:self.switchButton,self.destoryButton, nil];
    [buttonArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:70 leadSpacing:50 tailSpacing:50];
    [buttonArray mas_updateConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self).offset(18);
        make.height.equalTo(@42);
    }];
}

- (void)switchButtonPressed:(UIButton *)button {
    button.selected ^= 1;
    if (self.delegate && [self.delegate respondsToSelector:@selector(switchButtonPressed:switchButton:)]) {
        [self.delegate switchButtonPressed:self switchButton:button];
    }
}

- (void)destoryButtonPressed:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(destoryButtonPressed:destoryButton:)]) {
        [self.delegate destoryButtonPressed:self destoryButton:button];
    }
}

- (UIButton *)switchButton {
    if (!_switchButton) {
        _switchButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_switchButton setTitle:@"查看日志" forState:UIControlStateNormal];
        [_switchButton setTitle:@"房间成员" forState:UIControlStateSelected];
        [_switchButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _switchButton.titleLabel.font = [UIFont systemFontOfSize:16];
        [_switchButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_switchButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_down"] forState:UIControlStateHighlighted];
        _switchButton.layer.cornerRadius = 6;
        [_switchButton addTarget:self action:@selector(switchButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _switchButton;
}

- (UIButton *)destoryButton {
    if (!_destoryButton) {
        _destoryButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_destoryButton setTitle:@"引擎销毁" forState:UIControlStateNormal];
        [_destoryButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _destoryButton.titleLabel.font = [UIFont systemFontOfSize:16];
        [_destoryButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
        [_destoryButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_down"] forState:UIControlStateHighlighted];
        _destoryButton.layer.cornerRadius = 6;
        [_destoryButton addTarget:self action:@selector(destoryButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _destoryButton;
}

@end
