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

#import "HWProgressMaskView.h"

@interface HWProgressMaskView ()

@end

@implementation HWProgressMaskView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self initUIView];
    }
    return self;
}

- (void)initUIView {
    [self addSubview:self.infoLabel];
    [self addSubview:self.iconImageView];
}

- (void)setDataContent {
    if (self.image) {
        self.iconImageView.image = self.image;
    }
    self.iconImageView.hidden = !self.image;
    self.infoLabel.text = self.infoContent;
}

- (void)upDataContent {
    self.infoLabel.text = self.infoContent;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(@(kMargin));
        make.centerX.equalTo(self);
        make.height.width.equalTo(@(kImageHeight));
    }];
    [self.infoLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.equalTo(self.image ? self.iconImageView.mas_bottom : self).offset(kMargin);
        make.left.right.equalTo(self).inset(kMargin);
    }];
}

#pragma mark =========getter setter================

- (UILabel *)infoLabel {
    if (!_infoLabel) {
        _infoLabel = [[UILabel alloc] init];
        _infoLabel.font = [UIFont systemFontOfSize:kFontSize];
        _infoLabel.textAlignment = NSTextAlignmentCenter;
        _infoLabel.textColor = [UIColor colorWithString:kFontColor alpha:1];
        _infoLabel.numberOfLines = 0;
    }
    return _infoLabel;
}

- (UIImageView *)iconImageView {
    if (!_iconImageView) {
        _iconImageView = [[UIImageView alloc] init];
        _iconImageView.hidden = YES;
    }
    return _iconImageView;
}

@end
