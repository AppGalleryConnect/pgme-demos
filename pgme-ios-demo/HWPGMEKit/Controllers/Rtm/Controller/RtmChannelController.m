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

#import "RtmChannelController.h"
#import "ChannelSendView.h"
#import "LoginLogTableView.h"
#import "ChannelPropertyView.h"
#import "UserPropertyView.h"
#import "RtmMsgCache.h"
#import "HWPGMEDelegate.h"

@interface RtmChannelController () <ChannelSendViewDelegate, HWPGMEngineDelegate, UserPropertyViewDelegate, ChannelPropertyViewDelegate>
@property(nonatomic, strong) ChannelSendView *channelSendView;
@property(nonatomic, strong) LoginLogTableView *logTableView;
@property(nonatomic, strong) ChannelPropertyView *channelPropertyView;
@property(nonatomic, strong) UserPropertyView *userPropertyView;
@property(nonatomic, strong) UILabel *loginedUserLabel;
@property(nonatomic, strong) UIScrollView *scrollView;
/// 已订阅的频道名
@property(nonatomic, strong) NSMutableArray *channelNameArray;
@property(nonatomic, strong) RtmMsgCache *msgCache;
/// 查询玩家属性的openids
@property(nonatomic, strong) NSArray *playerOpenIds;
@end

@implementation RtmChannelController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"频道消息";
    [self createSubviews];
}

- (void)createSubviews {
    [HWPGMEDelegate.getInstance addDelegate:self];
    [self backButton];
    UISegmentedControl *control = [self topControl];
    [self.view addSubview:control];
    self.view.backgroundColor = UIColor.whiteColor;
    [control mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.view).offset(kGetSafeAreaTopHeight + kNavBarHeight + 10);
        make.left.mas_equalTo(self.view).offset(12);
        make.height.mas_equalTo(30);
        make.width.mas_equalTo(200);
    }];
    [self.view addSubview:self.scrollView];
    [self.scrollView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(control.mas_bottom);
        make.left.width.mas_equalTo(self.view);
        make.height.mas_equalTo(320);
    }];
    [self.scrollView addSubview:self.channelSendView];
    [self.channelSendView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.scrollView);
        make.left.mas_equalTo(self.scrollView).offset(12);
        make.width.mas_equalTo(SCREENWIDTH - 24);
        make.height.mas_equalTo(320);
    }];
    
    [self.scrollView addSubview:self.channelPropertyView];
    [self.channelPropertyView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.scrollView);
        make.left.mas_equalTo(self.scrollView).offset(SCREENWIDTH + 12);
        make.width.mas_equalTo(SCREENWIDTH-24);
        make.height.mas_equalTo(320);
    }];
    
    [self.scrollView addSubview:self.userPropertyView];
    [self.userPropertyView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.scrollView);
        make.left.mas_equalTo(self.scrollView).offset(SCREENWIDTH * 2 + 12);
        make.width.mas_equalTo(SCREENWIDTH-24);
        make.height.mas_equalTo(320);
    }];
    
    CGFloat bgImagHeight = SCREENHEIGHT - 400 - kGetSafeAreaTopHeight - kNavBarHeight - kGetSafeAreaBottomHeight;
    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_list"];
    [self.view addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.channelSendView.mas_bottom);
        make.left.mas_equalTo(self.view).offset(12);
        make.right.mas_equalTo(self.view).offset(-12);
        make.height.mas_equalTo(bgImagHeight);
    }];
    [self.view addSubview:self.logTableView];
    [self.logTableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(bgImageView);
        make.top.mas_equalTo(bgImageView).offset(5);
        make.height.mas_equalTo(bgImagHeight - 10);
    }];
    
    [self.view addSubview:self.loginedUserLabel];
    [self.loginedUserLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.view).offset(12);
        make.right.mas_equalTo(self.view).offset(-175);
        make.height.mas_equalTo(40);
        make.bottom.mas_equalTo(self.view).offset(-kGetSafeAreaBottomHeight);
    }];
    
    UIButton *refBtn = [self refreshBtn];
    [self.view addSubview:refBtn];
    [refBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.view).offset(-95);
        make.height.mas_equalTo(40);
        make.width.mas_equalTo(80);
        make.bottom.mas_equalTo(self.view).offset(-kGetSafeAreaBottomHeight);
    }];
    
    UIButton *clearBtn = [self clearBtn];
    [self.view addSubview:clearBtn];
    [clearBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.view).offset(-12);
        make.height.mas_equalTo(40);
        make.width.mas_equalTo(80);
        make.bottom.mas_equalTo(self.view).offset(-kGetSafeAreaBottomHeight);
    }];
}

- (void)backButton {
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [backButton setTitle:@"返回" forState:UIControlStateNormal];
    [backButton setTitleColor:[UIColor colorWithString:@"#666666"] forState:UIControlStateNormal];
    [backButton addTarget:self action:@selector(backFunc) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
}

- (UIButton *)refreshBtn {
    UIButton *refreshBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [refreshBtn setTitle:@"查询" forState:UIControlStateNormal];
    [refreshBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
    [refreshBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [refreshBtn addTarget:self action:@selector(refreshLoginedUserFunc) forControlEvents:UIControlEventTouchUpInside];
    refreshBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    return refreshBtn;
}

- (UIButton *)clearBtn {
    UIButton *clearBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [clearBtn setTitle:@"清屏" forState:UIControlStateNormal];
    [clearBtn setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
    [clearBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [clearBtn addTarget:self action:@selector(clearLogFunc) forControlEvents:UIControlEventTouchUpInside];
    clearBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    return clearBtn;
}

- (UISegmentedControl *)topControl {
    UISegmentedControl *control = [[UISegmentedControl alloc] initWithItems:@[@"消息功能", @"频道属性", @"用户属性"]];
    control.selectedSegmentIndex = 0;
    if (@available(iOS 13.0, *)) {
        control.selectedSegmentTintColor = [UIColor colorWithString:@"#7846E5" alpha:1];
    }
    control.tintColor = [UIColor colorWithString:@"#7846E5" alpha:1];
    control.layer.cornerRadius = 4;
    control.selectedSegmentIndex = 0;
    [control setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateSelected];
    [control setTitleTextAttributes:@{NSForegroundColorAttributeName: [UIColor whiteColor]} forState:UIControlStateNormal];
    [control addTarget:self action:@selector(segmentControlChange:) forControlEvents:UIControlEventValueChanged];
    return control;
}

- (void)segmentControlChange:(UISegmentedControl *)control {
    [self.view endEditing:YES];
    [self.scrollView setContentOffset:CGPointMake(control.selectedSegmentIndex * SCREENWIDTH, 0)];
}


#pragma mark ChannelSendViewDelegate

/// 订阅频道
- (void)channelSendView:(ChannelSendView *)channelView subscribeChancel:(NSString *)channel {
    SubscribeRtmChannelReq *req = [[SubscribeRtmChannelReq alloc] init];
    req.channelId = channel;
    [HWPGMEngine.getInstance subscribeRtmChannel:req];
}

/// 退订
- (void)channelSendView:(ChannelSendView *)channelView unSubscribeChancel:(NSString *)channel {
    UnSubscribeRtmChannelReq *req = [[UnSubscribeRtmChannelReq alloc] init];
    req.channelId = channel;
    [HWPGMEngine.getInstance unSubscribeRtmChannel:req];
}

/// 发送消息
- (void)channelSendView:(ChannelSendView *)channelView sendMsgReq:(PublishRtmChannelMessageReq *)msgReq {
    NSString *clientMsgId = [HWPGMEngine.getInstance publishRtmChannelMessage:msgReq];
    if (clientMsgId) {
        /// 消息缓存
        MsgCacheModel *model = [[MsgCacheModel alloc] init];
        model.rtmType = 2;
        model.messageType = msgReq.messageType;
        model.message = msgReq.messageType == 1 ? msgReq.messageString : [[NSString alloc] initWithData:msgReq.messageBytes encoding:NSUTF8StringEncoding];
        [self.msgCache cacheMsg:clientMsgId cacheModel:model];
    }
}

/// 切换频道
- (void)channelSendView:(ChannelSendView *)channelView changeChancel:(NSString *)channel {
    [self getChancelInfo:channel];
}

#pragma mark HWPGMEDelegate

/// 发送频道消息回调
- (void)onPublishRtmChannelMessage:(PublishRtmChannelMessageResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *message = [self.msgCache getCacheMsgWithClientMsgId:result.clientMsgId];
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]发送结果回调:clientMsgId:[%@], serverMsgId:[%@], rtnCode:[%d], errorMsg:[%@] 内容:%@",
                                       [self.msgCache getCurrentTime], result.clientMsgId, result.serverMsgId, result.code, result.msg, message]];
    });
}

/// 接收到频道消息回调
- (void)onReceiveRtmChannelMessage:(ReceiveRtmChannelMessageNotify *)notify {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[self.msgCache getChannelReceiveMessage:notify]];
    });
}

/// 订阅
- (void)onSubscribeRtmChannel:(SubscribeRtmChannelResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]订阅频道回调 [channelId=%@] [code=%d] [msg=%@]",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            if (![self.channelNameArray containsObject:result.channelId]) {
                [self.channelNameArray addObject:result.channelId];
            }
            /// 更新频道数据
            [self updateChannelArray];
            /// 设置当前选中的频道
            [self.channelSendView configCurrentChannel:result.channelId];
            /// 更新当前频道已登录用户
            [self getChancelInfo:result.channelId];
            /// 订阅成功, 如果有输入时间和数量,则去拉取历史消息
            [self getHistoryMessageWithChannel:result.channelId];
        }
    });
}

/// 退订
- (void)onUnSubscribeRtmChannel:(UnSubscribeRtmChannelResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]退订频道回调 [channelId=%@] [code=%d] [msg=%@]",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            self.loginedUserLabel.text = @"已登录用户:";
            if ([self.channelNameArray containsObject:result.channelId]) {
                [self.channelNameArray removeObject:result.channelId];
            }
            [self unSubscribeRtmChannelView:result.channelId];
            [self updateChannelArray];
            [self unSubscribeCheckChannel:result.channelId];
        }
    });
}

/// 获取频道历史消息回调
- (void)onGetRtmChannelHistoryMessages:(GetRtmChannelHistoryMessagesResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]获取频道历史消息回调 [channelId=%@] [code=%d] [msg=%@]",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            [result.channelMessages enumerateObjectsWithOptions:NSEnumerationReverse usingBlock:^(RtmChannelHistoryMessage * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
                [self.logTableView insertLogData:[self.msgCache getHistoryMessage:obj]];
            }];
        }
    });
}

/// 获取频道信息结果
- (void)onGetRtmChannelInfo:(GetRtmChannelInfoResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]获取频道信息回调 [channelId=%@] [code=%d] [msg=%@]",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            NSMutableArray *openIds = [NSMutableArray array];
            for (RtmChannelMemberInfo *info in result.memberInfos) {
                if (info.openId) {
                    [openIds addObject:info.openId];
                }
            }
            self.loginedUserLabel.text = [NSString stringWithFormat:@"已登录用户:%@",[openIds componentsJoinedByString:@","]];
        }
    });
}

/// 频道内用户属性设置回调
- (void)onSetRtmChannelPlayerProperties:(SetRtmChannelPlayerPropertiesResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]设置频道%@内用户%@的属性回调code:%d, msg:%@",[self.msgCache getCurrentTime], result.channelId, HWPGMEngine.getInstance.engineParam.openId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            [self getChannelPlayerPropertiesChannel:result.channelId userIds:@[HWPGMEngine.getInstance.engineParam.openId]];
        }
    });
}

/// 频道内用户属性查询回调
- (void)onGetRtmChannelPlayerProperties:(GetRtmChannelPlayerPropertiesResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (result.code == SUCCESS) {
            NSString *content = @"";
            for (RtmChannelMemberInfo *info in result.memberInfos) {
                if ([info.openId isEqualToString:HWPGMEngine.getInstance.engineParam.openId]) {
                    [self.userPropertyView configPlayerChannel:result.channelId properties:info.playerProperties];
                }
                NSString *str = [NSString stringWithFormat:@"用户%@的属性:%@", info.openId, [self jsonStringFromDictionary:info.playerProperties]];
                content = [content stringByAppendingString:str];
            }
            if (self.playerOpenIds && [self.playerOpenIds containsObject:HWPGMEngine.getInstance.engineParam.openId]) {
                // 如果查询结果没有玩家信息，需要清空下UI
                if (!result.memberInfos || !result.memberInfos.count) {
                    [self.userPropertyView configPlayerChannel:result.channelId properties:@{}];
                }
            }
            [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]查询频道%@内%@",[self.msgCache getCurrentTime], result.channelId, content]];
        } else {
            [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]查询频道%@内用户的属性回调code:%d, msg:%@",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        }
    });
}

/// 频道内用户属性删除回调
- (void)onDeleteRtmChannelPlayerProperties:(DeleteRtmChannelPlayerPropertiesResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]删除频道%@内用户%@的属性回调code:%d, msg=%@",[self.msgCache getCurrentTime], result.channelId, HWPGMEngine.getInstance.engineParam.openId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            [self getChannelPlayerPropertiesChannel:result.channelId userIds:@[HWPGMEngine.getInstance.engineParam.openId]];
        }
    });
}

/// 频道属性设置回调
- (void)onSetRtmChannelProperties:(SetRtmChannelPropertiesResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]频道%@属性设置code=%d, msg:%@",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            [self getChannelProperties:result.channelId];
        }
    });
}

/// 频道属性查询回调
- (void)onGetRtmChannelProperties:(GetRtmChannelPropertiesResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        if (result.code == SUCCESS) {
            [self.channelPropertyView configChannel:result.channelId properties:result.channelProperties];
            [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]查询频道%@的属性:%@",[self.msgCache getCurrentTime], result.channelId, [self jsonStringFromDictionary:result.channelProperties]]];
        } else {
            [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]查询频道%@属性code:%d, msg:%@",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        }
    });
}

/// 频道属性删除回调
- (void)onDeleteRtmChannelProperties:(DeleteRtmChannelPropertiesResult *)result {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]删除频道%@属性code:%d, msg:%@",[self.msgCache getCurrentTime], result.channelId, result.code, result.msg]];
        if (result.code == SUCCESS) {
            [self getChannelProperties:result.channelId];
        }
    });
}

/// 频道内用户属性变更通知
- (void)onRtmChannelPlayerPropertiesChanged:(RtmChannelPlayerPropertiesNotify *)notify {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]频道%@内用户%@属性变更:%@" ,[self.msgCache getCurrentTime], notify.channelId, notify.playerInfo.openId, [self jsonStringFromDictionary:notify.playerInfo.playerProperties]]];
    });
}


/// 频道属性变更通知
- (void)onRtmChannelPropertiesChanged:(RtmChannelPropertiesNotify *)notify {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self getChannelProperties:notify.channelId];
        [self.logTableView insertLogData:[NSString stringWithFormat:@"[%@]频道%@属性变更:%@",[self.msgCache getCurrentTime], notify.channelId, [self jsonStringFromDictionary:notify.channelProperties]]];
    });
}

/// 引擎销毁回调
- (void)onDestory:(int)code msg:(NSString *)msg {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.channelPropertyView removePropertySetView];
        [self.userPropertyView removePropertySetView];
    });
}

#pragma mark UserPropertyViewDelegate
/// 切换频道
- (void)userPropertyView:(UserPropertyView *)propertyView channel:(NSString *)channel openId:(NSString * _Nonnull)openId {
    openId = HWPGMEngine.getInstance.engineParam.openId;
    /// 获取用户属性
    [self getChannelPlayerPropertiesChannel:channel userIds:@[openId]];
}

/// 删除用户属性
- (void)userPropertyView:(UserPropertyView *)propertyView deleteChanel:(NSString *)channel keys:(NSArray * _Nonnull)keys {
    DeleteRtmChannelPlayerPropertiesReq *req = [[DeleteRtmChannelPlayerPropertiesReq alloc] init];
    req.channelId = channel;
    req.keys = keys;
    [HWPGMEngine.getInstance deleteRtmChannelPlayerProperties:req];
}

/// 设置用户属性
- (void)userPropertyView:(UserPropertyView *)propertyView setChannelPropertiesChanel:(NSString * _Nonnull)channel channelProperties:(NSDictionary<NSString *,NSString *> * _Nonnull)channelProperties {
    SetRtmChannelPlayerPropertiesReq *req = [[SetRtmChannelPlayerPropertiesReq alloc] init];
    req.channelId = channel;
    req.playerProperties = channelProperties;
    [HWPGMEngine.getInstance setRtmChannelPlayerProperties:req];
}

/// 查询用户属性
- (void)userPropertyView:(UserPropertyView *)propertyView queryPropertiesChannel:(NSString *)channel userId:(NSString *)userId {
    [self getChannelPlayerPropertiesChannel:channel userIds:@[userId]];
}

#pragma mark ChannelPropertyViewDelegate

/// 切换频道
- (void)channelPropertyView:(ChannelPropertyView *)propertyView channel:(NSString *)channel {
    /// 查询频道属性
    [self getChannelProperties:channel];
}

/// 删除频道属性
- (void)channelPropertyView:(ChannelPropertyView *)propertyView deleteChannel:(nonnull NSString *)channel keys:(nonnull NSArray *)keys {
    DeleteRtmChannelPropertiesReq *req = [[DeleteRtmChannelPropertiesReq alloc] init];
    req.channelId = channel;
    req.keys = keys;
    [HWPGMEngine.getInstance deleteRtmChannelProperties:req];
}

/// 设置频道属性
- (void)channelPropertyView:(ChannelPropertyView *)propertyView setChannel:(nonnull NSString *)channel properties:(nonnull NSDictionary<NSString *,NSString *> *)channelProperties {
    SetRtmChannelPropertiesReq *req = [[SetRtmChannelPropertiesReq alloc] init];
    req.channelId = channel;
    req.channelProperties = channelProperties;
    [HWPGMEngine.getInstance setRtmChannelProperties:req];
}


/// 获取频道用户属性
- (void)getChannelPlayerPropertiesChannel:(NSString *)channel userIds:(NSArray *)userIds {
    GetRtmChannelPlayerPropertiesReq *req = [[GetRtmChannelPlayerPropertiesReq alloc] init];
    req.channelId = channel;
    req.openIds = userIds;
    self.playerOpenIds = userIds;
    [HWPGMEngine.getInstance getRtmChannelPlayerProperties:req];
}

/// 获取频道属性
- (void)getChannelProperties:(NSString *)channel {
    GetRtmChannelPropertiesReq *req = [[GetRtmChannelPropertiesReq alloc] init];
    req.channelId = channel;
    [HWPGMEngine.getInstance getRtmChannelProperties:req];
}

- (void)backFunc {
    [self.navigationController popViewControllerAnimated:YES];
}

/// 刷新登录用户
- (void)refreshLoginedUserFunc {
    [self getChancelInfo:[self.channelSendView getSelectChannel]];
}

/// 清空日志
- (void)clearLogFunc {
    [self.logTableView clearAllLogs];
}

/// 获取频道信息
- (void)getChancelInfo:(NSString *)channel {
    GetRtmChannelInfoReq *req = [[GetRtmChannelInfoReq alloc] init];
    req.channelId = channel;
    req.isReturnMembers = YES;
    [HWPGMEngine.getInstance getRtmChannelInfo:req];
}

/// 退订 刷新频道属性。用户属性
- (void)unSubscribeRtmChannelView:(NSString *)channel {
    [self.channelPropertyView configChannel:channel properties:@{}];
    [self.userPropertyView configPlayerChannel:channel properties:@{}];
}

/// 更新频道数据
- (void)updateChannelArray {
    [self.channelSendView configChannelsArray:self.channelNameArray];
    [self.userPropertyView configChannelsArray:self.channelNameArray];
    [self.channelPropertyView configChannelsArray:self.channelNameArray];
}

/// 取消订阅后, 检查当前选中的频道是否是退订的频道
- (void)unSubscribeCheckChannel:(NSString *)channel {
    [self.channelSendView checkCurentChannelWithUnSubChannel:channel];
    [self.channelPropertyView checkCurentChannelWithUnSubChannel:channel];
    [self.userPropertyView checkCurentChannelWithUnSubChannel:channel];
}

- (void)getHistoryMessageWithChannel:(NSString *)channel {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *count = [self.channelSendView getHistoryCount];
        GetRtmChannelHistoryMessagesReq *req = [[GetRtmChannelHistoryMessagesReq alloc] init];
        req.channelId = channel;
        if (StringEmpty(count)) {
            req.count = [count integerValue];
        }
        req.startTime = [self.channelSendView getHistoryTimestamp];
        [HWPGMEngine.getInstance getRtmChannelHistoryMessages:req];
    });
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self.view endEditing:YES];
}

- (NSString *)jsonStringFromDictionary:(NSDictionary *)dict {
    if (!dict || dict.allKeys.count == 0) return @"";
    NSError *parseError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:&parseError];
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
}

- (ChannelSendView *)channelSendView {
    if (!_channelSendView) {
        _channelSendView = [[ChannelSendView alloc] init];
        _channelSendView.delegate = self;
    }
    return _channelSendView;
}

- (LoginLogTableView *)logTableView {
    if (!_logTableView) {
        _logTableView = [[LoginLogTableView alloc] init];
    }
    return _logTableView;
}

- (UILabel *)loginedUserLabel {
    if (!_loginedUserLabel) {
        _loginedUserLabel = [[UILabel alloc] init];
        _loginedUserLabel.textColor = [UIColor whiteColor];
        _loginedUserLabel.font = [UIFont systemFontOfSize:13];
        _loginedUserLabel.numberOfLines = 0;
        _loginedUserLabel.lineBreakMode = UILineBreakModeCharacterWrap;
        _loginedUserLabel.text = @"已登录用户:";
    }
    return _loginedUserLabel;
}

- (UIScrollView *)scrollView {
    if (!_scrollView) {
        _scrollView = [[UIScrollView alloc] init];
        _scrollView.contentSize = CGSizeMake(SCREENWIDTH * 3, 320);
        _scrollView.scrollEnabled = NO;
    }
    return _scrollView;
}

- (ChannelPropertyView *)channelPropertyView {
    if (!_channelPropertyView) {
        _channelPropertyView = [[ChannelPropertyView alloc] init];
        _channelPropertyView.delegate = self;
    }
    return _channelPropertyView;
}

- (UserPropertyView *)userPropertyView {
    if (!_userPropertyView) {
        _userPropertyView = [[UserPropertyView alloc] init];
        _userPropertyView.delegate = self;
    }
    return _userPropertyView;
}

- (NSMutableArray *)channelNameArray {
    if (!_channelNameArray) {
        _channelNameArray = [NSMutableArray array];
    }
    return _channelNameArray;
}

- (RtmMsgCache *)msgCache {
    if (!_msgCache) {
        _msgCache = [[RtmMsgCache alloc] init];
    }
    return _msgCache;
}
@end
