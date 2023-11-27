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

#import "RtmMessageSendView.h"

@interface RtmMessageSendView () <UITextViewDelegate>
@property(nonatomic, assign) RtmMessageSendViewType type;
@property(nonatomic, strong) UITextView *textView;
@property(nonatomic, strong) UIButton *msgBtn;
@property(nonatomic, strong) UIButton *binaryBtn;
@property(nonatomic, strong) UIButton *cacheBtn;
@property(nonatomic, strong) UIButton *auditBtn;
@property(nonatomic, strong) UIButton *adRecoginzeBtn;
@end

@implementation RtmMessageSendView

- (instancetype)initWithType:(RtmMessageSendViewType)type {
    if (self = [super init]) {
        self.type = type;
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.backgroundColor = [UIColor clearColor];
    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_list"];
    [self addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.height.mas_equalTo(self);
        make.right.mas_equalTo(self).offset(-120);
    }];
    [self addSubview:self.textView];
    [self.textView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.width.height.mas_equalTo(bgImageView);
    }];
    [self addSubview:self.msgBtn];
    [self addSubview:self.binaryBtn];
    NSArray *viewArray;
    if (self.type == RtmMessageSendViewTypeChannel) {
        [self addSubview:self.auditBtn];
        [self addSubview:self.adRecoginzeBtn];
        [self addSubview:self.cacheBtn];
        viewArray = @[self.msgBtn, self.binaryBtn, self.auditBtn, self.adRecoginzeBtn, self.cacheBtn];
        [self.msgBtn setTitle:@"发送频道消息" forState:UIControlStateNormal];
        [viewArray mas_distributeViewsAlongAxis:MASAxisTypeVertical withFixedSpacing:0 leadSpacing:0 tailSpacing:0];
        [viewArray mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.mas_equalTo(110);
            make.right.mas_equalTo(self);
        }];
    } else {
        viewArray = @[self.msgBtn, self.binaryBtn];
        [self.msgBtn setTitle:@"发送P2P消息" forState:UIControlStateNormal];
        [viewArray mas_distributeViewsAlongAxis:MASAxisTypeVertical withFixedSpacing:20 leadSpacing:30 tailSpacing:30];
        [viewArray mas_makeConstraints:^(MASConstraintMaker *make) {
            make.width.mas_equalTo(110);
            make.right.mas_equalTo(self);
        }];
    }
}

- (void)sendMessageFunc {
    if (self.delegate && [self.delegate respondsToSelector:@selector(rtmMessageView:sendMessage:)]) {
        [self.delegate rtmMessageView:self sendMessage:self.textView.text];
    }
    self.textView.text = @"";
}

- (void)sendBinaryMessageFunc {
    if (self.delegate && [self.delegate respondsToSelector:@selector(rtmMessageView:sendBinaryMessage:)]) {
        [self.delegate rtmMessageView:self sendBinaryMessage:self.textView.text];
    }
    self.textView.text = @"";
}

- (void)cacheBtnFunc:(UIButton *)btn {
    btn.selected = !btn.selected;
}

- (void)auditBtnFunc:(UIButton *)btn {
    btn.selected = !btn.selected;
}

- (void)adRecoginzeBtnFunc:(UIButton *)btn {
    btn.selected = !btn.selected;
}

/// 是否缓存消息
- (BOOL)isAllowCacheMsg {
    return self.cacheBtn.selected;
}

/// 是否进行内容风控审核
- (BOOL)isContentIdentify {
    return self.auditBtn.selected;
}

/// 是否进行广告识别
- (BOOL)isAdsIdentify {
    return self.adRecoginzeBtn.selected;
}


- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    if ([text isEqualToString:@"\n"]) {
        [self.textView resignFirstResponder];
        return NO;
    }
    return YES;
}

- (UITextView *)textView {
    if (!_textView) {
        _textView = [[UITextView alloc] init];
        _textView.backgroundColor = [UIColor clearColor];
        _textView.textColor = [UIColor whiteColor];
        _textView.delegate = self;
    }
    return _textView;
}

- (UIButton *)msgBtn {
    if (!_msgBtn) {
        _msgBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_msgBtn setTitle:@"发送P2P消息" forState:UIControlStateNormal];
        [_msgBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_msgBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_msgBtn addTarget:self action:@selector(sendMessageFunc) forControlEvents:UIControlEventTouchUpInside];
        _msgBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    }
    return _msgBtn;
}

- (UIButton *)binaryBtn {
    if (!_binaryBtn) {
        _binaryBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_binaryBtn setTitle:@"发送消息(二进制)" forState:UIControlStateNormal];
        [_binaryBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_binaryBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_binaryBtn addTarget:self action:@selector(sendBinaryMessageFunc) forControlEvents:UIControlEventTouchUpInside];
        _binaryBtn.titleLabel.font = [UIFont systemFontOfSize:12];
    }
    return _binaryBtn;
}

- (UIButton *)cacheBtn {
    if (!_cacheBtn) {
        _cacheBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_cacheBtn setTitle:@"存入历史消息" forState:UIControlStateNormal];
        [_cacheBtn setTitleColor:[UIColor systemGrayColor] forState:UIControlStateNormal];
        [_cacheBtn setTitleColor:[UIColor hw_textColor] forState:UIControlStateSelected];
        [_cacheBtn setImage:[UIImage imageNamed:@"btn_mymic_normal"] forState:UIControlStateNormal];
        [_cacheBtn setImage:[UIImage imageNamed:@"btn_mymic_selected"] forState:UIControlStateSelected];
        [_cacheBtn setImage:[UIImage imageNamed:@"btn_mymic_disable"] forState:UIControlStateDisabled];
        [_cacheBtn setTitleEdgeInsets:UIEdgeInsetsMake(0, 5, 0, -5)];
        [_cacheBtn setImageEdgeInsets:UIEdgeInsetsMake(0, -5, 0, 5)];
        _cacheBtn.titleLabel.font = [UIFont systemFontOfSize:12];
        _cacheBtn.enabled = YES;
        [_cacheBtn addTarget:self action:@selector(cacheBtnFunc:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cacheBtn;
}

- (UIButton *)auditBtn {
    if (!_auditBtn) {
        _auditBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_auditBtn setTitle:@"内容风控审核" forState:UIControlStateNormal];
        [_auditBtn setTitleColor:[UIColor systemGrayColor] forState:UIControlStateNormal];
        [_auditBtn setTitleColor:[UIColor hw_textColor] forState:UIControlStateSelected];
        [_auditBtn setImage:[UIImage imageNamed:@"btn_mymic_normal"] forState:UIControlStateNormal];
        [_auditBtn setImage:[UIImage imageNamed:@"btn_mymic_selected"] forState:UIControlStateSelected];
        [_auditBtn setImage:[UIImage imageNamed:@"btn_mymic_disable"] forState:UIControlStateDisabled];
        [_auditBtn setTitleEdgeInsets:UIEdgeInsetsMake(0, 5, 0, -5)];
        [_auditBtn setImageEdgeInsets:UIEdgeInsetsMake(0, -5, 0, 5)];
        _auditBtn.titleLabel.font = [UIFont systemFontOfSize:12];
        _auditBtn.enabled = YES;
        [_auditBtn addTarget:self action:@selector(auditBtnFunc:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _auditBtn;
}

- (UIButton *)adRecoginzeBtn {
    if (!_adRecoginzeBtn) {
        _adRecoginzeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_adRecoginzeBtn setTitle:@"是否广告识别" forState:UIControlStateNormal];
        [_adRecoginzeBtn setTitleColor:[UIColor systemGrayColor] forState:UIControlStateNormal];
        [_adRecoginzeBtn setTitleColor:[UIColor hw_textColor] forState:UIControlStateSelected];
        [_adRecoginzeBtn setImage:[UIImage imageNamed:@"btn_mymic_normal"] forState:UIControlStateNormal];
        [_adRecoginzeBtn setImage:[UIImage imageNamed:@"btn_mymic_selected"] forState:UIControlStateSelected];
        [_adRecoginzeBtn setImage:[UIImage imageNamed:@"btn_mymic_disable"] forState:UIControlStateDisabled];
        [_adRecoginzeBtn setTitleEdgeInsets:UIEdgeInsetsMake(0, 5, 0, -5)];
        [_adRecoginzeBtn setImageEdgeInsets:UIEdgeInsetsMake(0, -5, 0, 5)];
        _adRecoginzeBtn.titleLabel.font = [UIFont systemFontOfSize:12];
        _adRecoginzeBtn.enabled = YES;
        [_adRecoginzeBtn addTarget:self action:@selector(adRecoginzeBtnFunc:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _adRecoginzeBtn;
}
@end
