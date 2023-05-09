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

#import "PlayerHeaderView.h"
#import "Constants.h"

@interface PlayerHeaderView ()

/// 背景图
@property (nonatomic, strong) UIImageView *bgImageView;

/// 小队成员title
@property (nonatomic, strong) UILabel *playerHeaderTitle;

/// 转换房主按钮
@property (nonatomic, strong) UIButton *ownerTransferButton;

/// 麦克风按钮
@property (nonatomic, strong) UIButton *micButton;

/// 扬声器按钮
@property (nonatomic, strong) UIButton *speakButton;
@end

@implementation PlayerHeaderView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.bgImageView];
    [self addSubview:self.playerHeaderTitle];
    [self addSubview:self.ownerTransferButton];
    [self addSubview:self.micButton];
    [self addSubview:self.speakButton];
    
    [self.bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
    [self.playerHeaderTitle mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self);
        make.left.equalTo(self).offset(15);
    }];
    [self.ownerTransferButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.mas_centerY);
        make.right.equalTo(self.micButton.mas_left).offset(-16);
    }];
    [self.micButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.mas_centerY);
        make.right.equalTo(self.speakButton.mas_left).offset(-6);
        make.size.mas_equalTo(CGSizeMake(36, 36));
    }];
    [self.speakButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.mas_centerY);
        make.right.equalTo(self).offset(-12);
        make.size.mas_equalTo(CGSizeMake(36, 36));
    }];
}

#pragma mark - set

- (void)setIsMicHidden:(BOOL)isMicHidden {
    self.micButton.hidden = isMicHidden;
}

- (void)setIsOwnerTransferHidden:(BOOL)IsOwnerTransferHidden {
    self.ownerTransferButton.hidden = IsOwnerTransferHidden;
}

- (void)setIsMicEnable:(BOOL)isMicEnable {
    self.micButton.enabled = isMicEnable;
}

#pragma mark - func

- (void)headerTitleWithRoomType:(NSString *)roomType
                    playerCount:(NSInteger)playerCount
           allPlayerIsForbidden:(BOOL)isForbidden
           allPlayerSpeakIsMute:(BOOL)isMute {
    self.playerHeaderTitle.text = [NSString stringWithFormat:@"%@成员（%ld）",roomType,(long)playerCount];
    self.micButton.selected = isForbidden;
    self.speakButton.selected = isMute;
}

#pragma mark - event

/// 转移房主按钮点击
/// @param button 转移房主按钮
- (void)ownerTransferButtonPressed:(UIButton *)button {
    [[NSNotificationCenter defaultCenter] postNotificationName:OWNER_TRANSFER object:nil userInfo:@{
        @"button" : button
    }];
}

/// 禁言/解禁言所有人
/// @param button 麦克风按钮
- (void)micButtonPressed:(UIButton *)button {
    [[NSNotificationCenter defaultCenter] postNotificationName:FORBID_ALL_OR_NOT object:nil userInfo:@{
        @"button" : button
    }];
}

/// 屏蔽/解屏蔽所有人
/// @param button 扬声器按钮
- (void)speakButtonPressed:(UIButton *)button {
    [[NSNotificationCenter defaultCenter] postNotificationName:MUTE_ALL_OR_NOT object:nil userInfo:@{
        @"button" : button
    }];
}

- (UIImageView *)bgImageView {
    if (!_bgImageView) {
        _bgImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_cell"]];
    }
    return _bgImageView;
}

- (UILabel *)playerHeaderTitle {
    if (!_playerHeaderTitle) {
        _playerHeaderTitle = [[UILabel alloc] init];
        _playerHeaderTitle.font = [UIFont systemFontOfSize:14 weight:UIFontWeightBold];
        _playerHeaderTitle.textColor = [UIColor hw_textColor];
        _playerHeaderTitle.text = @"小队成员（0）";
    }
    return _playerHeaderTitle;
}

- (UIButton *)ownerTransferButton {
    if (!_ownerTransferButton) {
        _ownerTransferButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_ownerTransferButton setBackgroundImage:[UIImage imageNamed:@"btn_owner_transfer"] forState:UIControlStateNormal];
        [_ownerTransferButton addTarget:self action:@selector(ownerTransferButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _ownerTransferButton;
}

- (UIButton *)micButton {
    if (!_micButton) {
        _micButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_micButton setBackgroundImage:[UIImage imageNamed:@"btn_mic_on"] forState:UIControlStateNormal];
        [_micButton setBackgroundImage:[UIImage imageNamed:@"btn_mic_off"] forState:UIControlStateSelected];
        [_micButton setBackgroundImage:[UIImage imageNamed:@"btn_mic_on"] forState:UIControlStateDisabled];
        [_micButton addTarget:self action:@selector(micButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _micButton;
}

- (UIButton *)speakButton {
    if (!_speakButton) {
        _speakButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_speakButton setBackgroundImage:[UIImage imageNamed:@"btn_voice_on"] forState:UIControlStateNormal];
        [_speakButton setBackgroundImage:[UIImage imageNamed:@"btn_voice_off"] forState:UIControlStateSelected];
        [_speakButton addTarget:self action:@selector(speakButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _speakButton;
}

@end
