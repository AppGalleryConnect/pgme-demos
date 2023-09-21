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

#import "OperationBottomCollectionViewCell.h"

@interface OperationBottomCollectionViewCell ()

/// 事件按钮
@property(nonatomic, strong) UIButton *button;

@end

@implementation OperationBottomCollectionViewCell

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self initUI];
    }
    return self;
}

- (void)initUI {
    [self addSubview:self.button];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    [self.button mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
}

- (void)buttonFun:(UIButton *)sender {
    if (self.touchBlock) {
        BottomButtonType type = (BottomButtonType) [[_dataDic objectForKey:TYPE] integerValue];
        self.touchBlock(sender, type);
    }
}

#pragma mark ==============getter setter===============

- (void)setDataDic:(NSDictionary *)dataDic {
    _dataDic = dataDic;
    [self.button setTitle:[_dataDic objectForKey:NORMALTITLE] forState:UIControlStateNormal];
    NSString *selectedTitle = [_dataDic objectForKey:SELECTEDTITLE];
    if (selectedTitle) {
        [self.button setTitle:selectedTitle forState:UIControlStateSelected];
    }
    [self.button setBackgroundImage:[UIImage imageNamed:[_dataDic objectForKey:NORMALIMAGE]] forState:UIControlStateNormal];
    [self.button setBackgroundImage:[UIImage imageNamed:[_dataDic objectForKey:LIGHTEDIMAGE]] forState:UIControlStateHighlighted];
}

- (UIButton *)button {
    if (!_button) {
        _button = [UIButton buttonWithType:UIButtonTypeCustom];
        _button.titleLabel.font = [UIFont systemFontOfSize:16];
        _button.layer.cornerRadius = 6;
        _button.layer.masksToBounds = YES;
        [_button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_button addTarget:self action:@selector(buttonFun:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _button;
}

@end
