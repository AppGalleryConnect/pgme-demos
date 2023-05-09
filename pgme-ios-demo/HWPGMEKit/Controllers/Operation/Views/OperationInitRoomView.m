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

#import "OperationInitRoomView.h"

@interface OperationInitRoomView ()<UITextFieldDelegate>

/// 切换房间 标题
@property (nonatomic, strong) UILabel *switchRoomTitle;

/// 房间名textField
@property (nonatomic, strong) UITextField *switchRoomListTextField;

/// roomID 标题
@property (nonatomic, strong) UILabel *roomIDTitle;

/// roomID输入框
@property (nonatomic, strong) UITextField *roomIdTextField;

/// 创建/加入小队按钮
@property (nonatomic, strong) UIButton *teamButton;

/// 创建/加入国战按钮
@property (nonatomic, strong) UIButton *nationalWarButton;

/// 离开房间按钮
@property (nonatomic, strong) UIButton *leaveRoomButton;

/// 开/关 麦克风按钮
@property (nonatomic, strong) UIButton *turnonMicButton;

/// 语音转文字按钮
@property (nonatomic, strong) UIButton *voiceToTextButton;

/// 下拉箭头
@property (nonatomic, strong) UIImageView *arrowImageView;

@end

@implementation OperationInitRoomView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.switchRoomTitle];
    [self addSubview:self.switchRoomListTextField];
    [self addSubview:self.roomIDTitle];
    [self addSubview:self.roomIdTextField];
    [self addSubview:self.teamButton];
    [self addSubview:self.nationalWarButton];
    [self addSubview:self.leaveRoomButton];
    [self addSubview:self.turnonMicButton];
    [self addSubview:self.voiceToTextButton];
    [self.switchRoomListTextField addSubview:self.arrowImageView];
    
    CGFloat ButtonW = (SCREENWIDTH - 45*3) / 2;
    CGFloat ButtonH = 38;
    
    [self.switchRoomTitle mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self).offset(15);
        make.left.equalTo(self).offset(45);
        make.size.mas_equalTo(CGSizeMake(75, 40));
    }];
    [self.switchRoomListTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.roomIdTextField.mas_left);
        make.centerY.equalTo(self.switchRoomTitle.mas_centerY);
        make.right.equalTo(self).offset(-45);
        make.height.equalTo(@42);
    }];
    [self.roomIDTitle mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.switchRoomTitle.mas_bottom).offset(16);
        make.left.equalTo(self.switchRoomTitle);
        make.size.equalTo(self.switchRoomTitle);
    }];
    [self.roomIdTextField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.roomIDTitle.mas_right).offset(20);
        make.centerY.equalTo(self.roomIDTitle.mas_centerY);
        make.right.equalTo(self).offset(-45);
        make.height.equalTo(@42);
    }];
    [self.teamButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.switchRoomTitle.mas_left);
        make.top.equalTo(self.roomIDTitle.mas_bottom).offset(12);
        make.size.mas_equalTo(CGSizeMake(ButtonW, ButtonH));
    }];
    [self.nationalWarButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.roomIdTextField);
        make.top.equalTo(self.teamButton);
        make.size.equalTo(self.teamButton);
    }];
    [self.leaveRoomButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.equalTo(self.nationalWarButton);
        make.top.equalTo(self.nationalWarButton.mas_bottom).offset(20);
        make.size.mas_equalTo(CGSizeMake(ButtonW * 0.8, ButtonH));
    }];
    [self.turnonMicButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.teamButton);
        make.centerY.equalTo(self.leaveRoomButton);
        make.size.mas_equalTo(CGSizeMake(ButtonW * 0.6, ButtonH));
    }];
    [self.voiceToTextButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.equalTo(self.turnonMicButton.mas_right).offset(28);
        make.centerY.equalTo(self.leaveRoomButton);
        make.size.mas_equalTo(CGSizeMake(ButtonH * 1.1, ButtonH * 0.8));
    }];
    [self.arrowImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.switchRoomListTextField);
        make.right.equalTo(self.switchRoomListTextField).offset(-8);
        make.size.mas_equalTo(CGSizeMake(22, 12));
    }];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.roomIdTextField resignFirstResponder];
}

#pragma mark - set
// 设置房间显示ID
- (void)setRoomID:(NSString *)roomID {
    _roomID = roomID;
    dispatch_async(dispatch_get_main_queue(), ^{
        _switchRoomListTextField.text = roomID;
        _switchRoomListTextField.hidden = !StringEmpty(roomID);
        _roomIdTextField.text = !StringEmpty(roomID) ? roomID : @"";
    });
}

// 设置离开房间按钮是否可点击以及按钮样式
- (void)setIsLeaveButtonDisable:(BOOL)isLeaveButtonDisable {
    dispatch_async(dispatch_get_main_queue(), ^{
        // 设置按钮 可点击/不可点击
        _leaveRoomButton.enabled = !isLeaveButtonDisable;
    });
}

#pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    if ([textField isEqual:self.switchRoomListTextField]) {
        [self showRoomIdListAction:textField];
        return NO;
    }
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

#pragma mark - event
- (void)teamButtonPressed:(UIButton *)button {
    [self.roomIdTextField resignFirstResponder];
    if (self.delegate && [self.delegate respondsToSelector:@selector(teamButtonPressed:teamButton:roomID:)]) {
        [self.delegate teamButtonPressed:self teamButton:button roomID:self.roomIdTextField.text];
    }
}

- (void)nationalWarButtonPressed:(UIButton *)button {
    [self.roomIdTextField resignFirstResponder];
    if (self.delegate && [self.delegate respondsToSelector:@selector(nationalWarButtonPressed:nationalWarButton:roomID:)]) {
        [self.delegate nationalWarButtonPressed:self nationalWarButton:button roomID:self.roomIdTextField.text];
    }
}

- (void)leaveRoomButtonPressed:(UIButton *)button {
    [self.roomIdTextField resignFirstResponder];
    if (self.delegate && [self.delegate respondsToSelector:@selector(leaveRoomButtonPressed:leaveRoomButton:)]) {
        [self.delegate leaveRoomButtonPressed:self leaveRoomButton:button];
    }
}

- (void)turnonMicButtonPressed:(UIButton *)button {
    [self.roomIdTextField resignFirstResponder];
    button.selected ^= 1;
    if (self.delegate && [self.delegate respondsToSelector:@selector(turnonMicButtonPressed:turnonMicButton:)]) {
        [self.delegate turnonMicButtonPressed:self turnonMicButton:button];
    }
}

- (void)voiceToTextButtonPressed:(UIButton *)button {
    [self.roomIdTextField resignFirstResponder];
    if (self.delegate && [self.delegate respondsToSelector:@selector(voiceToTextButtonPressed:voiceToTextButton:)]) {
        [self.delegate voiceToTextButtonPressed:self voiceToTextButton:button];
    }
}

- (void)showRoomIdListAction:(id)sender {
    [self.roomIdTextField resignFirstResponder];
    if (StringEmpty(self.switchRoomListTextField.text) && self.delegate && [self.delegate respondsToSelector:@selector(switchRoomTextFieldPressed:switchRoomTextField:)]) {
        [self.delegate switchRoomTextFieldPressed:self switchRoomTextField:self.switchRoomListTextField];
    }
}

- (UILabel *)switchRoomTitle {
    if (!_switchRoomTitle) {
        _switchRoomTitle = [[UILabel alloc] init];
        _switchRoomTitle.text = @"房间切换：";
        _switchRoomTitle.textColor = [UIColor hw_textColor];
        _switchRoomTitle.font = [UIFont systemFontOfSize:14];
        _switchRoomTitle.textAlignment = NSTextAlignmentRight;
    }
    return _switchRoomTitle;
}

- (UITextField *)switchRoomListTextField {
    if (!_switchRoomListTextField) {
        _switchRoomListTextField = [[UITextField alloc] init];
        _switchRoomListTextField.textColor = [UIColor hw_titleColor];
        _switchRoomListTextField.font = [UIFont systemFontOfSize:14 weight:UIFontWeightMedium];
        _switchRoomListTextField.background = [UIImage imageNamed:@"bg_input"];
        _switchRoomListTextField.delegate = self;
        _switchRoomListTextField.hidden = YES;
    }
    return _switchRoomListTextField;
}

- (UILabel *)roomIDTitle {
    if (!_roomIDTitle) {
        _roomIDTitle = [[UILabel alloc] init];
        _roomIDTitle.text = @"房间ID：";
        _roomIDTitle.textColor = [UIColor hw_textColor];
        _roomIDTitle.font = [UIFont systemFontOfSize:14];
        _roomIDTitle.textAlignment = NSTextAlignmentRight;
    }
    return _roomIDTitle;
}

- (UITextField *)roomIdTextField {
    if (!_roomIdTextField) {
        _roomIdTextField = [[UITextField alloc] init];
        _roomIdTextField.textColor = [UIColor hw_titleColor];
        _roomIdTextField.background = [UIImage imageNamed:@"bg_input"];
        _roomIdTextField.placeholder = @"请输入仅包含数字、字母、下划线的roomId";
        _roomIdTextField.delegate = self;
        _roomIdTextField.font = [UIFont systemFontOfSize:13];
        [_roomIdTextField setValue:[UIColor hw_placeholderColor] forKeyPath:@"placeholderLabel.textColor"];
    }
    return _roomIdTextField;
}

- (UIButton *)teamButton {
    if (!_teamButton) {
        _teamButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_teamButton setTitle:@"创建/加入小队" forState:UIControlStateNormal];
        [_teamButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _teamButton.titleLabel.font = [UIFont systemFontOfSize:13];
        [_teamButton setBackgroundImage:[UIImage imageNamed:@"bt_join_normal"] forState:UIControlStateNormal];
        [_teamButton setBackgroundImage:[UIImage imageNamed:@"bt_join_down"] forState:UIControlStateHighlighted];
        _teamButton.layer.cornerRadius = 6;
        [_teamButton addTarget:self action:@selector(teamButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _teamButton;
}

- (UIButton *)nationalWarButton {
    if (!_nationalWarButton) {
        _nationalWarButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_nationalWarButton setTitle:@"创建/加入国战" forState:UIControlStateNormal];
        [_nationalWarButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _nationalWarButton.titleLabel.font = [UIFont systemFontOfSize:13];
        [_nationalWarButton setBackgroundImage:[UIImage imageNamed:@"bt_join_normal"] forState:UIControlStateNormal];
        [_nationalWarButton setBackgroundImage:[UIImage imageNamed:@"bt_join_down"] forState:UIControlStateHighlighted];
        _nationalWarButton.layer.cornerRadius = 6;
        [_nationalWarButton addTarget:self action:@selector(nationalWarButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _nationalWarButton;
}

- (UIButton *)leaveRoomButton {
    if (!_leaveRoomButton) {
        _leaveRoomButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_leaveRoomButton setTitle:@"离开房间" forState:UIControlStateNormal];
        [_leaveRoomButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _leaveRoomButton.titleLabel.font = [UIFont systemFontOfSize:13];
        [_leaveRoomButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
        [_leaveRoomButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_down"] forState:UIControlStateHighlighted];
        [_leaveRoomButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_disable"] forState:UIControlStateDisabled];
        _leaveRoomButton.layer.cornerRadius = 6;
        _leaveRoomButton.enabled = NO;
        [_leaveRoomButton addTarget:self action:@selector(leaveRoomButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _leaveRoomButton;
}

- (UIButton *)turnonMicButton {
    if (!_turnonMicButton) {
        _turnonMicButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_turnonMicButton setTitle:@"开麦" forState:UIControlStateNormal];
        [_turnonMicButton setTitleColor:[UIColor systemGrayColor] forState:UIControlStateNormal];
        [_turnonMicButton setTitleColor:[UIColor hw_textColor] forState:UIControlStateSelected];
        [_turnonMicButton setImage:[UIImage imageNamed:@"btn_mymic_normal"] forState:UIControlStateNormal];
        [_turnonMicButton setImage:[UIImage imageNamed:@"btn_mymic_selected"] forState:UIControlStateSelected];
        [_turnonMicButton setImage:[UIImage imageNamed:@"btn_mymic_disable"] forState:UIControlStateDisabled];
        [_turnonMicButton setTitleEdgeInsets:UIEdgeInsetsMake(0, 5, 0, -5)];
        [_turnonMicButton setImageEdgeInsets:UIEdgeInsetsMake(0, -5, 0, 5)];
        _turnonMicButton.titleLabel.font = [UIFont systemFontOfSize:18];
        _turnonMicButton.enabled = YES;
        [_turnonMicButton addTarget:self action:@selector(turnonMicButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _turnonMicButton;
}

- (UIButton *)voiceToTextButton {
    if (!_voiceToTextButton) {
        _voiceToTextButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_voiceToTextButton setBackgroundImage:[UIImage imageNamed:@"btn_voice_to_text"] forState:UIControlStateNormal];
        [_voiceToTextButton addTarget:self action:@selector(voiceToTextButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _voiceToTextButton;
}

- (UIImageView *)arrowImageView {
    if (!_arrowImageView) {
        _arrowImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"room_select_arrow"]];
    }
    return _arrowImageView;
}

@end
