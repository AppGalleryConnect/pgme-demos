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

#import "RtmP2PController.h"
#import "RtmMessageSendView.h"
#import "LoginLogTableView.h"
#import "HWPGMEDelegate.h"
#import "RtmMsgCache.h"

@interface RtmP2PController () <UITextFieldDelegate, RtmMessageSendViewDelegate, HWPGMEngineDelegate>
@property(nonatomic, strong) RtmMessageSendView *msgSendView;
@property(nonatomic, strong) UITextField *textField;
@property(nonatomic, strong) LoginLogTableView *logTableView;
@property(nonatomic, strong) RtmMsgCache *msgCache;
@end

@implementation RtmP2PController
- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"P2P消息";
    [self createSubviews];
}

- (void)createSubviews {
    [HWPGMEDelegate.getInstance addDelegate:self];
    [self backButton];
    UIView *userView = [self sendReceiveView];
    [self.view addSubview:userView];
    [userView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.view).offset(kGetSafeAreaTopHeight + kNavBarHeight + 10);
        make.left.mas_equalTo(self.view).offset(12);
        make.right.mas_equalTo(self.view).offset(-12);
        make.height.mas_equalTo(40);
    }];

    [self.view addSubview:self.msgSendView];
    [self.msgSendView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.mas_equalTo(userView);
        make.top.mas_equalTo(userView.mas_bottom).offset(10);
        make.height.mas_equalTo(160);
    }];

    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_list"];
    [self.view addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.msgSendView.mas_bottom).offset(20);
        make.left.right.mas_equalTo(userView);
        make.bottom.mas_equalTo(self.view).offset(-kGetSafeAreaBottomHeight);
    }];
    [self.view layoutIfNeeded];
    [bgImageView layoutIfNeeded];

    [self.view addSubview:self.logTableView];
    [self.logTableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(bgImageView);
        make.top.mas_equalTo(bgImageView).offset(5);
        make.height.mas_equalTo(bgImageView.frame.size.height - 10);
    }];
}


- (UIView *)sendReceiveView {
    UIView *view = [[UIView alloc] init];
    NSString *name = [NSString stringWithFormat:@"用户名 %@", HWPGMEngine.getInstance.engineParam.openId];
    UILabel *sendLabel = [self commonLabelWithTitle:name];
    [view addSubview:sendLabel];


    UIView *receiveView = [[UIView alloc] init];
    [view addSubview:receiveView];
    UILabel *receiveLabel = [self commonLabelWithTitle:@"接收用户"];
    [receiveView addSubview:receiveLabel];
    [receiveLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(receiveView);
        make.width.mas_equalTo(70);
    }];
    [receiveView addSubview:self.textField];
    [self.textField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(receiveLabel.mas_right);
        make.height.mas_equalTo(36);
        make.right.centerY.mas_equalTo(receiveView);
    }];

    NSArray *viewArray = @[sendLabel, receiveView];
    [viewArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:0 leadSpacing:0 tailSpacing:0];
    [viewArray mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(40);
        make.centerY.mas_equalTo(view);
    }];
    return view;
}

/// RtmMessageSendViewDelegate
- (void)rtmMessageView:(RtmMessageSendView *)sendView sendBinaryMessage:(NSString *)message {
    [self.view endEditing:YES];
//    if ([self isPeerMsgEmpty:message]) {
//        return;
//    }
    PublishRtmPeerMessageReq *req = [[PublishRtmPeerMessageReq alloc] init];
    req.peerId = self.textField.text;
    req.messageType = 2;
    req.messageBytes = [message dataUsingEncoding:NSUTF8StringEncoding];
    NSString *clientMsgId = [HWPGMEngine.getInstance publishRtmPeerMessage:req];
    if (clientMsgId) {
        /// 缓存消息
        MsgCacheModel *model = [[MsgCacheModel alloc] init];
        model.rtmType = 1;
        model.messageType = 2;
        model.message = message;
        [self.msgCache cacheMsg:clientMsgId cacheModel:model];
    }
}

- (void)rtmMessageView:(RtmMessageSendView *)sendView sendMessage:(NSString *)message {
    [self.view endEditing:YES];
//    if ([self isPeerMsgEmpty:message]) {
//        return;
//    }
    PublishRtmPeerMessageReq *req = [[PublishRtmPeerMessageReq alloc] init];
    req.peerId = self.textField.text;
    req.messageType = 1;
    req.messageString = message;
    NSString *clientMsgId = [HWPGMEngine.getInstance publishRtmPeerMessage:req];
    if (clientMsgId) {
        MsgCacheModel *model = [[MsgCacheModel alloc] init];
        model.rtmType = 1;
        model.messageType = 1;
        model.message = message;
        [self.msgCache cacheMsg:clientMsgId cacheModel:model];
    }
}

/// 发送P2P消息回调
- (void)onPublishRtmPeerMessage:(PublishRtmPeerMessageResult *)result {
    NSString *message = [self.msgCache getCacheMsgWithClientMsgId:result.clientMsgId];
    [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]发送结果回调:clientMsgId:[%@], serverMsgId:[%@], rtnCode:[%d], errorMsg:[%@] 内容:%@",
                                   [self.msgCache getCurrentTime], result.clientMsgId, result.serverMsgId, result.code, result.msg, message]];
    
   
}


/// 接收到P2P消息回调
- (void)onReceiveRtmPeerMessage:(ReceiveRtmPeerMessageNotify *)notify {
    [self.logTableView insertLogData:[self.msgCache getPeerReceiveMessage:notify]];
}

- (BOOL)isPeerMsgEmpty:(NSString *)message {
    if (!StringEmpty(self.textField.text)) {
        [HWTools showMessage:@"请输入接收用户"];
        return YES;
    }
    if (!StringEmpty(message)) {
        [HWTools showMessage:@"请输入消息内容"];
        return YES;
    }
    return NO;
}

- (void)backButton {
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [backButton setTitle:@"返回" forState:UIControlStateNormal];
    [backButton setTitleColor:[UIColor colorWithString:@"#666666"] forState:UIControlStateNormal];
    [backButton addTarget:self action:@selector(backFunc) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
}

- (void)backFunc {
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.view endEditing:YES];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (UILabel *)commonLabelWithTitle:(NSString *)title {
    UILabel *label = [[UILabel alloc] init];
    label.text = title;
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:15];
    return label;
}

- (RtmMessageSendView *)msgSendView {
    if (!_msgSendView) {
        _msgSendView = [[RtmMessageSendView alloc] initWithType:RtmMessageSendViewTypeP2P];
        _msgSendView.delegate = self;
    }
    return _msgSendView;
}

- (UITextField *)textField {
    if (!_textField) {
        _textField = [[UITextField alloc] init];
        _textField.textColor = [UIColor whiteColor];
        _textField.background = [UIImage imageNamed:@"bg_input"];
        _textField.delegate = self;
    }
    return _textField;
}

- (LoginLogTableView *)logTableView {
    if (!_logTableView) {
        _logTableView = [[LoginLogTableView alloc] init];
    }
    return _logTableView;
}

- (RtmMsgCache *)msgCache {
    if (!_msgCache) {
        _msgCache = [[RtmMsgCache alloc] init];
    }
    return _msgCache;
}
@end
