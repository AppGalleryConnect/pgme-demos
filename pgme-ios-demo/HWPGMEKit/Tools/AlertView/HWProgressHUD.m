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

#import "HWProgressHUD.h"
#import "HWProgressMaskView.h"

/// 背景默认不透明度
#define kBackViewAlpha              0.08f

/// 遮罩默认不透明度
#define kMaskViewAlpha              0.6f

/// 遮罩宽度
#define kMaskViewWith               0.6f

/// 遮罩默认颜色
#define kMaskViewBackgroundColorRGB @"0x000000"

/// 圆角半径
#define kRadius                      10

@interface HWProgressHUD()

// 内容背景图
@property (nonatomic, strong) HWProgressMaskView *maskView;

// icon图片
@property (nonatomic, strong) UIImage *image;

// 提示内容
@property (nonatomic, strong) NSString *info;

// 倒计时
@property (nonatomic, assign) NSInteger duration;

// 定时器
@property (nonatomic, strong) dispatch_source_t timer;

@end

@implementation HWProgressHUD

+ (void)showInView:(UIView *)inView
             Image:(UIImage *)image
           andInfo:(NSString *)info
       andDuration:(NSInteger)duration {
    [inView addSubview:[HWProgressHUD sharedInstance]];
    [[HWProgressHUD sharedInstance] initUIView];
    [HWProgressHUD sharedInstance].frame = inView.bounds;
    [HWProgressHUD sharedInstance].info = info;
    [HWProgressHUD sharedInstance].image = image;
    [HWProgressHUD sharedInstance].duration = duration;
    [[HWProgressHUD sharedInstance] setDataContent];
}

+ (void)showInView:(UIView *)inView
           andInfo:(NSString *)info
       andDuration:(NSInteger)duration {
    [inView addSubview:[HWProgressHUD sharedInstance]];
    [[HWProgressHUD sharedInstance] initUIView];
    [HWProgressHUD sharedInstance].frame = inView.bounds;
    [HWProgressHUD sharedInstance].info = info;
    [HWProgressHUD sharedInstance].duration = duration;
    [[HWProgressHUD sharedInstance] setDataContent];
}

+ (void)showInView:(UIView *)inView
           andInfo:(NSString *)info {
    [inView addSubview:[HWProgressHUD sharedInstance]];
    [[HWProgressHUD sharedInstance] initUIView];
    [HWProgressHUD sharedInstance].frame = inView.bounds;
    [HWProgressHUD sharedInstance].info = info;
    [[HWProgressHUD sharedInstance] setDataContent];
}

+ (void)hiddenHUD {
    [[HWProgressHUD sharedInstance] dismiss];
}

+ (HWProgressHUD *)sharedInstance {
    static HWProgressHUD *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[HWProgressHUD alloc] initWithFrame:CGRectZero];
    });
    return instance;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor colorWithString:kMaskViewBackgroundColorRGB alpha:kBackViewAlpha];
    }
    return self;
}

- (void)initUIView {
    if (!_maskView) {
        [self addSubview:self.maskView];
    }
}

- (void)setDataContent {
    self.maskView.image = self.image;
    if (self.duration > 0) {
        self.maskView.infoContent = [NSString stringWithFormat:@"%@%ld",self.info,self.duration];
    } else {
        self.maskView.infoContent = self.info;
    }
    [self.maskView setDataContent];
    [self layoutIfNeeded];
    [self stopTimer];
    [self resumeTimer];
}

- (void)updataContent {
    if (self.duration > 0) {
        self.maskView.infoContent = [NSString stringWithFormat:@"%@%ld",self.info,self.duration];
    }
    [self.maskView upDataContent];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat maskViewHeight = 0;
    if (self.image) {
        maskViewHeight += kMargin;
        maskViewHeight += kImageHeight;
    }
    CGSize infoLabelSize = [self.maskView.infoLabel sizeThatFits:CGSizeMake(CGRectGetWidth(self.superview.frame) * kMaskViewWith - 2*kMargin, 0)];
    maskViewHeight += kMargin;
    maskViewHeight += infoLabelSize.height;
    maskViewHeight += kMargin;
    
    [self.maskView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@(CGRectGetWidth(self.superview.frame) * kMaskViewWith));
        make.height.equalTo(@(maskViewHeight));
        make.top.equalTo(@(CGRectGetHeight(self.superview.frame) * 0.4));
        make.centerX.equalTo(self.superview);
    }];
}

- (void)dismiss {
    if (_timer) {
        [self stopTimer];
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        [self removeFromSuperview];
    });
    [self releaseResource];
}

- (void)resumeTimer {
    if (self.duration > 0) {
        dispatch_resume(self.timer);
    }
}

- (void)stopTimer {
    if (_timer) {
        dispatch_source_cancel(self.timer);
        _timer = nil;
    }
}

- (void)timerTask {
    self.duration--;
    if (self.duration <= 0) {
        [self stopTimer];
        [self dismiss];
        return;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        [self updataContent];
    });
}

- (void)releaseResource {
    _duration = 0;
}

#pragma mark =========getter setter================

- (dispatch_source_t)timer {
    if (!_timer) {
        dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        _timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, queue);
        dispatch_time_t startTime = dispatch_walltime(NULL, 1.0 * NSEC_PER_SEC);
        dispatch_source_set_timer(_timer, startTime, 1.0 * NSEC_PER_SEC, 0.0 * NSEC_PER_SEC);
        __weak typeof(self) weakSelf = self;
        dispatch_source_set_event_handler(_timer, ^{
            [weakSelf timerTask];
        });
    }
    return _timer;
}

- (UIView *)maskView {
    if (!_maskView) {
        _maskView = [[HWProgressMaskView alloc]initWithFrame:CGRectZero];
        _maskView.backgroundColor = [UIColor colorWithString:kMaskViewBackgroundColorRGB alpha:kMaskViewAlpha];
        _maskView.layer.cornerRadius = kRadius;
        _maskView.layer.masksToBounds = YES;
    }
    return _maskView;
}

@end
