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

#import "PlayerListTableViewCell.h"
#import "HWSpeakButton.h"
#import "Player+RoomPlayer.h"
#import "Constants.h"

@interface PlayerListTableViewCell ()

/// 玩家头像视图
@property (nonatomic, strong) UIImageView *headerImageView;

/// 用户id label
@property (nonatomic, strong) UILabel *userIdLabel;

/// 身份 label
@property (nonatomic, strong) UILabel *identityLabel;

/// 扬声器按钮
@property (nonatomic, strong) HWSpeakButton *speakButton;

/// 麦克风按钮
@property (nonatomic, strong) UIButton *micButton;

/// 用户id
@property (nonatomic, copy) NSString *userId;

/// 索引
@property (nonatomic, strong) NSIndexPath *indexPath;

/// 底部分割线
@property (nonatomic, strong) UIImageView *lineImageView;

@property (nonatomic, strong) NSArray *imgArr;

@end

@implementation PlayerListTableViewCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    self.backgroundColor = [UIColor clearColor];
    [self.contentView addSubview:self.headerImageView];
    [self.contentView addSubview:self.userIdLabel];
    [self.contentView addSubview:self.identityLabel];
    [self.contentView addSubview:self.speakButton];
    [self.contentView addSubview:self.micButton];
    [self.contentView addSubview:self.lineImageView];
    
    [self.headerImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.contentView.mas_centerY);
        make.left.equalTo(self.contentView).offset(15);
        make.size.mas_equalTo(CGSizeMake(30, 30));
    }];
    [self.userIdLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(12);
        make.bottom.equalTo(self.contentView).offset(-12);
        make.left.equalTo(self.headerImageView.mas_right).offset(4);
        make.width.equalTo(@100);
    }];
    [self.identityLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.contentView).offset(12);
        make.bottom.equalTo(self.contentView).offset(-12);
        make.left.equalTo(self.userIdLabel.mas_right).offset(2);
        make.width.equalTo(@90);
    }];
    [self.speakButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.contentView.mas_centerY);
        make.right.equalTo(self.contentView).offset(-15);
        make.size.mas_equalTo(CGSizeMake(28, 28));
    }];
    [self.micButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.contentView.mas_centerY);
        make.right.equalTo(self.speakButton.mas_left).offset(-15);
        make.size.mas_equalTo(CGSizeMake(28, 28));
    }];
    [self.lineImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerX.equalTo(self.contentView.mas_centerX);
        make.bottom.equalTo(self.contentView);
        make.width.mas_equalTo(self.contentView.frame.size.width * 0.9);
    }];
}

#pragma mark - func

- (void)cellWithIndexPath:(NSIndexPath *)indexPath
                 roomInfo:(Room *)roomInfo
                   player:(Player *)player {
    NSString *identity = [player.openId isEqualToString:roomInfo.ownerId] ? @"房主" : @"";
    _identityLabel.text = identity;
    _indexPath = indexPath;
    _userId = player.openId;
    _userIdLabel.text = player.openId;
    _micButton.selected = player.isForbidden;
    _speakButton.selected = player.isMute;
    _micButton.hidden = roomInfo.roomType == ROOM_TYPE_NATIONAL ? YES : NO;
    if ([player.openId isEqualToString:roomInfo.ownerId]) {
        _userIdLabel.textColor = [UIColor hw_roomOwnerColor];
        _identityLabel.textColor = [UIColor hw_roomOwnerColor];
    }else {
        _userIdLabel.textColor = [UIColor hw_playerColor];
        _identityLabel.textColor = [UIColor hw_playerColor];
    }
    if (player.isSpeaking) {
        [_speakButton beginSpeaking];
    }else {
        [_speakButton stopSpeaking];
    }
}

#pragma mark - event

/// 屏蔽/解屏蔽指定成员
/// @param button 扬声器按钮
- (void)speakingButtonPressed:(HWSpeakButton *)button {
    [[NSNotificationCenter defaultCenter] postNotificationName:MUTE_OR_NOT object:nil userInfo:@{
        @"userId" : self.userId,
        @"index" : self.indexPath,
        @"button" : self.speakButton
    }];
}

/// 禁言/解禁言指定成员
/// @param button 麦克风按钮
- (void)micButtonPressed:(UIButton *)button {
    [[NSNotificationCenter defaultCenter] postNotificationName:FORBID_OR_NOT object:nil userInfo:@{
        @"userId" : self.userId,
        @"index" : self.indexPath,
        @"button" : button
    }];
}

- (UIImageView *)headerImageView {
    if (!_headerImageView) {
        _headerImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ic_member"]];
    }
    return _headerImageView;
}

- (UILabel *)userIdLabel {
    if (!_userIdLabel) {
        _userIdLabel = [[UILabel alloc] init];
        _userIdLabel.font = [UIFont systemFontOfSize:14];
        _userIdLabel.textColor = [UIColor hw_playerColor];
        _userIdLabel.textAlignment = NSTextAlignmentLeft;
        _userIdLabel.numberOfLines = 0;
        [_userIdLabel sizeToFit];
    }
    return _userIdLabel;
}

- (UILabel *)identityLabel {
    if (!_identityLabel) {
        _identityLabel = [[UILabel alloc] init];
        _identityLabel.font = [UIFont systemFontOfSize:14];
        _identityLabel.textColor = [UIColor hw_playerColor];
        _identityLabel.textAlignment = NSTextAlignmentLeft;
        _identityLabel.numberOfLines = 0;
        [_identityLabel sizeToFit];
    }
    return _identityLabel;
}

- (HWSpeakButton *)speakButton {
    if (!_speakButton) {
        _speakButton = [[HWSpeakButton alloc] init];
        [_speakButton setImage:[UIImage imageNamed:@"btn_speaker_speaking3"] forState:UIControlStateNormal];
        [_speakButton setImage:[UIImage imageNamed:@"btn_voice_off"] forState:UIControlStateSelected];
        [_speakButton addTarget:self action:@selector(speakingButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _speakButton;
}

- (UIButton *)micButton {
    if (!_micButton) {
        _micButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_micButton setBackgroundImage:[UIImage imageNamed:@"btn_mic_on"] forState:UIControlStateNormal];
        [_micButton setBackgroundImage:[UIImage imageNamed:@"btn_mic_off"] forState:UIControlStateSelected];
        [_micButton addTarget:self action:@selector(micButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _micButton;
}

- (UIImageView *)lineImageView {
    if (!_lineImageView) {
        _lineImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"cell_line"]];
    }
    return _lineImageView;
}

@end
