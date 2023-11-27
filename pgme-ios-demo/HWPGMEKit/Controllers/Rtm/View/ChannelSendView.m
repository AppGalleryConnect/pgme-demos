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


#import "ChannelSendView.h"
#import "RtmMessageSendView.h"
#import "ChannelSelectView.h"

@interface ChannelSendView () <UITextFieldDelegate, RtmMessageSendViewDelegate>
///  用户名
@property(nonatomic, strong) UILabel *userNameLabel;
/// 频道名
@property(nonatomic, strong) UITextField *channelNameInput;
/// 接收用户
@property(nonatomic, strong) UITextField *receiveUserInput;
/// 当前频道
@property(nonatomic, strong) ChannelSelectView *selectView;
@property(nonatomic, strong) RtmMessageSendView *sendView;
/// 历史消息时间
@property(nonatomic, strong) UITextField *timeInput;
/// 历史消息数量
@property(nonatomic, strong) UITextField *countInput;
@end

@implementation ChannelSendView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.userNameLabel];
    self.userNameLabel.text = [NSString stringWithFormat:@"用户名:%@",HWPGMEngine.getInstance.engineParam.openId];
    [self.userNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(self);
        make.height.mas_equalTo(40);
        make.width.mas_equalTo(100);
    }];

    UIView *timeCountView = [self messageTimeCountView];
    [self addSubview:timeCountView];
    [timeCountView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.right.mas_equalTo(self);
        make.height.mas_equalTo(40);
        make.left.mas_equalTo(self.userNameLabel.mas_right).offset(5);
    }];

    UIView *subscribeView = [self channelSubscribeView];
    [self addSubview:subscribeView];
    [subscribeView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.userNameLabel.mas_bottom);
        make.left.width.mas_equalTo(self);
        make.height.mas_equalTo(40);
    }];


    [self addSubview:self.selectView];
    [self.selectView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(subscribeView.mas_bottom);
        make.left.width.mas_equalTo(self);
        make.height.mas_equalTo(40);
    }];

    UIView *receiveUserView = [self receiveUserView];
    [self addSubview:receiveUserView];
    [receiveUserView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.selectView.mas_bottom);
        make.left.width.mas_equalTo(self);
        make.height.mas_equalTo(40);
    }];

    [self addSubview:self.sendView];
    [self.sendView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(receiveUserView.mas_bottom);
        make.left.width.mas_equalTo(self);
        make.height.mas_equalTo(160);
    }];
}


- (UIView *)messageTimeCountView {
    UIView *view = [[UIView alloc] init];
    UIView *timeView = [self viewWithTitle:@"时间" input:self.timeInput suffix:@"天前"];
    [view addSubview:timeView];
    [timeView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.height.mas_equalTo(view);
        make.width.mas_equalTo(view).multipliedBy(0.6);
    }];
    
    UIView *countView = [self viewWithTitle:@"数量" input:self.countInput];
    [view addSubview:countView];
    [countView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.top.height.mas_equalTo(view);
        make.left.mas_equalTo(timeView.mas_right).offset(5);
    }];
    return view;
}

- (UIView *)viewWithTitle:(NSString *)title input:(UITextField *)input suffix:(NSString *)suffix {
    UIView *view = [[UIView alloc] init];
    UILabel *label = [self commonLabel];
    label.text = title;
    [view addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
        make.width.mas_equalTo(32);
    }];
    
    UILabel *suffixLabel = [self commonLabel];
    suffixLabel.text = suffix;
    [view addSubview:suffixLabel];
    [suffixLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.centerY.mas_equalTo(view);
        make.width.mas_equalTo(32);
    }];
    
    
    [view addSubview:input];
    [input mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(label.mas_right);
        make.height.mas_equalTo(32);
        make.centerY.mas_equalTo(view);
        make.right.mas_equalTo(suffixLabel.mas_left);
    }];
    return view;
}

- (UIView *)viewWithTitle:(NSString *)title input:(UITextField *)input {
    UIView *view = [[UIView alloc] init];
    UILabel *label = [self commonLabel];
    label.text = title;
    [view addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
        make.width.mas_equalTo(32);
    }];

    [view addSubview:input];
    [input mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(label.mas_right);
        make.height.mas_equalTo(32);
        make.centerY.right.mas_equalTo(view);
    }];
    return view;
}

- (UIView *)channelSubscribeView {
    UIView *view = [[UIView alloc] init];
    UIView *channelNameView = [[UIView alloc] init];
    [view addSubview:channelNameView];
    [channelNameView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.height.mas_equalTo(view);
        make.width.mas_equalTo(view).multipliedBy(0.5);
    }];
    UILabel *label = [self commonLabel];
    label.text = @"频道名";
    [channelNameView addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(channelNameView);
        make.width.mas_equalTo(70);
    }];
    [channelNameView addSubview:self.channelNameInput];
    [self.channelNameInput mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(label.mas_right);
        make.height.mas_equalTo(36);
        make.centerY.right.mas_equalTo(channelNameView);
    }];

    UIView *btnView = [[UIView alloc] init];
    [view addSubview:btnView];
    [btnView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.top.height.mas_equalTo(view);
        make.width.mas_equalTo(view).multipliedBy(0.5);
    }];
    UIButton *subscribeBtn = [self commonButtonWithTitle:@"订阅" sel:@selector(subscribeFunc)];
    [btnView addSubview:subscribeBtn];
    UIButton *unSubscribeBtn = [self commonButtonWithTitle:@"退订" sel:@selector(unSubscribeFunc)];
    [btnView addSubview:unSubscribeBtn];
    NSArray *btnArray = @[subscribeBtn, unSubscribeBtn];
    [btnArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:10 tailSpacing:0];
    [btnArray mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(40);
        make.centerY.mas_equalTo(view);
    }];
    return view;
}


- (UIView *)receiveUserView {
    UIView *view = [[UIView alloc] init];
    UILabel *label = [self commonLabel];
    label.text = @"指定频道接收用户";
    [view addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
        make.width.mas_equalTo(130);
    }];
    [view addSubview:self.receiveUserInput];
    [self.receiveUserInput mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(label.mas_right);
        make.height.mas_equalTo(36);
        make.centerY.right.mas_equalTo(view);
    }];
    return view;
}


/// 订阅
- (void)subscribeFunc {
    [self closeKeyboard];
    if (!StringEmpty(self.channelNameInput.text)) {
        [HWTools showMessage:@"请输入频道名"];
        return;
    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelSendView:subscribeChancel:)]) {
        [self.delegate channelSendView:self subscribeChancel:self.channelNameInput.text];
    }
    self.channelNameInput.text = @"";
}

/// 退订
- (void)unSubscribeFunc {
    [self closeKeyboard];
    if (!StringEmpty([self.selectView getSelectChannel])) {
        [HWTools showMessage:@"请选择要退订的频道"];
        return;
    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelSendView:unSubscribeChancel:)]) {
        [self.delegate channelSendView:self unSubscribeChancel:[self.selectView getSelectChannel]];
    }
    self.channelNameInput.text = @"";
}


- (void)checkCurentChannelWithUnSubChannel:(NSString *)channel {
    if (!StringEmpty([self.selectView getSelectChannel])) {
        return;
    }
    [self.selectView unSubscribeChannel:channel];
}

- (void)configChannelsArray:(NSArray *)channelsArray {
    [self.selectView configChannelsArray:channelsArray];
}

- (void)configCurrentChannel:(NSString *)channel {
    [self.selectView configSelectedChannel:channel];
}

- (long)getHistoryTimestamp {
    int day = [self.timeInput.text intValue];
    if (day == 0) {
        return (long)[[NSDate date] timeIntervalSince1970] * 1000;
    } else {
        NSTimeInterval time = 24 * 60 * 60 * (-day);
        NSDate *preDate = [[NSDate date] initWithTimeIntervalSinceNow: time];
        return (long)[preDate timeIntervalSince1970] * 1000;
    }
}

- (NSString *)getHistoryCount {
    return self.countInput.text;
}

- (void)closeKeyboard {
    [self endEditing:YES];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self closeKeyboard];
}

#pragma mark RtmMessageSendViewDelegate

- (void)rtmMessageView:(RtmMessageSendView *)sendView sendMessage:(NSString *)message {
    [self closeKeyboard];
//    if ([self isPeerMsgEmpty:message]) {
//        return;
//    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelSendView:sendMsgReq:)]) {
        [self.delegate channelSendView:self sendMsgReq:[self channelMessageWithMessage:message msgType:1]];
    }
}

- (void)rtmMessageView:(RtmMessageSendView *)sendView sendBinaryMessage:(NSString *)message {
    [self closeKeyboard];
//    if ([self isPeerMsgEmpty:message]) {
//        return;
//    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelSendView:sendMsgReq:)]) {
        [self.delegate channelSendView:self sendMsgReq:[self channelMessageWithMessage:message msgType:2]];
    }
}

- (PublishRtmChannelMessageReq *)channelMessageWithMessage:(NSString *)message msgType:(int)msgType {
    PublishRtmChannelMessageReq *req = [[PublishRtmChannelMessageReq alloc] init];
    req.channelId = [self.selectView getSelectChannel];
    req.isAllowCacheMsg = self.sendView.isAllowCacheMsg;
    req.isAdsIdentify = self.sendView.isAdsIdentify;
    req.isContentIdentify = self.sendView.isContentIdentify;
    if (StringEmpty(self.receiveUserInput.text)) {
        NSArray *recievers = [self arrayWithString:self.receiveUserInput.text];
        req.receivers = recievers;
    }
    req.messageType = msgType;
    if (msgType == 1) { /// 文本
        req.messageString = message;
    } else { /// 二进制
        req.messageBytes = [message dataUsingEncoding:NSUTF8StringEncoding];
    }
    return req;
}

- (NSArray *)arrayWithString:(NSString *)string {
    NSString *str = [string stringByReplacingOccurrencesOfString:@"，" withString:@","];
    return [str componentsSeparatedByString:@","];
}

- (BOOL)isPeerMsgEmpty:(NSString *)message {
    if (!StringEmpty([self.selectView getSelectChannel])) {
        [HWTools showMessage:@"请选择频道"];
        return YES;
    }
    if (!StringEmpty(message)) {
        [HWTools showMessage:@"请输入消息内容"];
        return YES;
    }
    return NO;
}

/// 切换频道
- (void)changeChannel:(NSString *)channel {
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelSendView:changeChancel:)]) {
        [self.delegate channelSendView:self changeChancel:channel];
    }
}


/// 获取当前选中的频道
- (NSString *)getSelectChannel {
    return [self.selectView getSelectChannel];
}

#pragma mark UITextFieldDelegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (UILabel *)commonLabel {
    UILabel *label = [[UILabel alloc] init];
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:15];
    return label;
}

- (UIButton *)commonButtonWithTitle:(NSString *)title sel:(SEL)sel {
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setTitle:title forState:UIControlStateNormal];
    [btn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [btn addTarget:self action:sel forControlEvents:UIControlEventTouchUpInside];
    btn.titleLabel.font = [UIFont systemFontOfSize:14];
    return btn;
}


- (UILabel *)userNameLabel {
    if (!_userNameLabel) {
        _userNameLabel = [self commonLabel];
        _userNameLabel.font = [UIFont systemFontOfSize:15];
    }
    return _userNameLabel;
}

- (UITextField *)channelNameInput {
    if (!_channelNameInput) {
        _channelNameInput = [[UITextField alloc] init];
        _channelNameInput.textColor = [UIColor whiteColor];
        _channelNameInput.background = [UIImage imageNamed:@"bg_input"];
        _channelNameInput.font = [UIFont systemFontOfSize:14];
        _channelNameInput.delegate = self;
    }
    return _channelNameInput;
}


- (UITextField *)receiveUserInput {
    if (!_receiveUserInput) {
        _receiveUserInput = [[UITextField alloc] init];
        _receiveUserInput.textColor = [UIColor whiteColor];
        _receiveUserInput.background = [UIImage imageNamed:@"bg_input"];
        _receiveUserInput.placeholder = @"指定多个用户时,账号用逗号分割";
        [_receiveUserInput setValue:[UIColor hw_placeholderColor] forKeyPath:@"placeholderLabel.textColor"];
        _receiveUserInput.font = [UIFont systemFontOfSize:14];
        _receiveUserInput.delegate = self;
    }
    return _receiveUserInput;
}


- (RtmMessageSendView *)sendView {
    if (!_sendView) {
        _sendView = [[RtmMessageSendView alloc] initWithType:RtmMessageSendViewTypeChannel];
        _sendView.delegate = self;
    }
    return _sendView;
}

- (UITextField *)timeInput {
    if (!_timeInput) {
        _timeInput = [[UITextField alloc] init];
        _timeInput.textColor = [UIColor whiteColor];
        _timeInput.background = [UIImage imageNamed:@"bg_input"];
        _timeInput.font = [UIFont systemFontOfSize:14];
        _timeInput.keyboardType = UIKeyboardTypeNumberPad;
        _timeInput.placeholder = @"历史时间";
        [_timeInput setValue:[UIColor hw_placeholderColor] forKeyPath:@"placeholderLabel.textColor"];
        _timeInput.delegate = self;
        _timeInput.text = @"7";
    }
    return _timeInput;
}

- (UITextField *)countInput {
    if (!_countInput) {
        _countInput = [[UITextField alloc] init];
        _countInput.textColor = [UIColor whiteColor];
        _countInput.background = [UIImage imageNamed:@"bg_input"];
        _countInput.font = [UIFont systemFontOfSize:14];
        _countInput.keyboardType = UIKeyboardTypeNumberPad;
        _countInput.placeholder = @"数量";
        [_countInput setValue:[UIColor hw_placeholderColor] forKeyPath:@"placeholderLabel.textColor"];
        _countInput.delegate = self;
        _countInput.text = @"0";
    }
    return _countInput;
}

- (ChannelSelectView *)selectView {
    if (!_selectView) {
        _selectView = [[ChannelSelectView alloc] init];
        [_selectView configTitle:@"当前频道"];
        __weak typeof(self) weakSelf = self;
        _selectView.selectBlock = ^(NSString *channel) {
            [weakSelf changeChannel:channel];
        };
    }
    return _selectView;
}
@end
