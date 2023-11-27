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

#import "ChannelSelectView.h"
#import "HWDropDownMenu.h"

@interface ChannelSelectView () <UITextFieldDelegate, HWDropDownMenuDelegate>
@property(nonatomic, strong) UILabel *titleLabel;
@property(nonatomic, strong) UITextField *switchChnnelInput;
@property(nonatomic, strong) NSArray *channels;
@end

@implementation ChannelSelectView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.titleLabel];
    [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(self);
        make.width.mas_equalTo(130);
    }];

    [self addSubview:self.switchChnnelInput];
    [self.switchChnnelInput mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.titleLabel.mas_right);
        make.centerY.right.mas_equalTo(self);
        make.height.mas_equalTo(36);
    }];

    UIImageView *arrowImageView = [[UIImageView alloc] init];
    arrowImageView.image = [UIImage imageNamed:@"room_select_arrow"];
    [self.switchChnnelInput addSubview:arrowImageView];
    [arrowImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.equalTo(self.switchChnnelInput);
        make.right.equalTo(self.switchChnnelInput).offset(-8);
        make.size.mas_equalTo(CGSizeMake(22, 12));
    }];
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    if ([textField isEqual:self.switchChnnelInput]) {
        [self showMenuFunc];
        return NO;
    }
    return YES;
}

/// 显示菜单弹框
- (void)showMenuFunc {
    [HWDropDownMenu showOnView:self
                        titles:self.channels
                      delegate:self
                 otherSettings:^(HWDropDownMenu *_Nonnull dropDownMenu) {
                     dropDownMenu.bgImage = [UIImage imageNamed:@"bg_dropdown_menu"];
                     dropDownMenu.showMaskView = NO;
                     dropDownMenu.cornerRadius = 0;
                 }];
}

/// 下拉菜单选择代理
- (void)HWDropDownMenu:(UIView *)view didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSString *channelName = [self.channels objectAtIndex:indexPath.row];
    self.switchChnnelInput.text = channelName;
    if (self.selectBlock) {
        self.selectBlock(channelName);
    }
}

- (void)configChannelsArray:(NSArray *)channcelsArray {
    self.channels = channcelsArray;
}

- (void)configTitle:(NSString *)title {
    self.titleLabel.text = title;
}

- (void)configSelectedChannel:(NSString *)channel {
    self.switchChnnelInput.text = channel;
}

- (NSString *)getSelectChannel {
    return self.switchChnnelInput.text;
}

- (void)unSubscribeChannel:(NSString *)channel {
    if ([channel isEqualToString:self.switchChnnelInput.text]) {
        /// 退订的频道和当前选中的频道一致, 则清空当前选中的频道
        self.switchChnnelInput.text = @"";
        /// 默认选中第一个
        if (self.channels.count > 0) {
            self.switchChnnelInput.text = [self.channels firstObject];
            if (self.selectBlock) {
                self.selectBlock([self.channels firstObject]);
            }
        }
    }
}

- (UITextField *)switchChnnelInput {
    if (!_switchChnnelInput) {
        _switchChnnelInput = [[UITextField alloc] init];
        _switchChnnelInput.textColor = [UIColor hw_titleColor];
        _switchChnnelInput.font = [UIFont systemFontOfSize:14 weight:UIFontWeightMedium];
        _switchChnnelInput.background = [UIImage imageNamed:@"bg_input"];
        _switchChnnelInput.delegate = self;
    }
    return _switchChnnelInput;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont systemFontOfSize:15];
        _titleLabel.textColor = [UIColor whiteColor];
    }
    return _titleLabel;
}
@end
