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

#import "LoginViewController.h"
#import "UserIdLoginView.h"
#import "LoginLogTableView.h"
#import "HWPGMEDelegate.h"
#import "OperationViewController.h"

@interface LoginViewController ()<UserIdLoginViewDelegate,HWPGMEngineDelegate>

/// 输入框和初始化引擎按钮view
@property (nonatomic, strong) UserIdLoginView *loginView;

/// 初始化引擎log信息列表
@property (nonatomic, strong) LoginLogTableView *logTableView;

/// 输入的用户ID
@property (nonatomic, copy) NSString *userID;

@end

@implementation LoginViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupViews];
}

- (void)setupViews {
    self.title = @"GmmeSdkDemo";
    [HWPGMEDelegate.getInstance addDelegate:self];
    UserIdLoginView *loginView = [[UserIdLoginView alloc] initWithFrame:CGRectMake(0, kGetSafeAreaTopHeight+kNavBarHeight, SCREENWIDTH, SCREENHEIGHT * 0.3)];
    loginView.delegate = self;
    _loginView = loginView;
    [self.view addSubview:loginView];
    
    LoginLogTableView *logTableView = [[LoginLogTableView alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(loginView.frame), SCREENWIDTH, SCREENHEIGHT-loginView.frame.size.height-kGetSafeAreaTopHeight-kNavBarHeight)];
    _logTableView = logTableView;
    [self.view addSubview:logTableView];
}

- (int)initEngineWithOpenId:(NSString *)openId {
    EngineCreateParams *engineParam = [[EngineCreateParams alloc]init];
    engineParam.clientId = @"";
    engineParam.clientSecret = @"";
    engineParam.agcAppId = @"";
    engineParam.openId = openId;
    engineParam.cpAccessToken = @"";
    engineParam.apiKey = @"";
    HWPGMEngine *engine = [HWPGMEngine create:engineParam engineDelegate:HWPGMEDelegate.getInstance];
    if (engine) {
        NSLog(@"init engine success");
        [engine enableMic: false];
        return SUCCESS;
    }
    NSLog(@"init engine failed");
    return ERROR;
}

#pragma mark - UserIdLoginViewDelegate
- (void)initEnginePressed:(UIView *)loginView button:(UIButton *)button userID:(NSString *)userID {
    if (StringEmpty(userID)) {
        _userID = userID;
        [self initEngineWithOpenId:userID];
    }
}

#pragma mark - HWPGMEngineDelegate
- (void)onCreate:(int)code msg:(NSString *)msg {
    NSString *description = @"";
    if (code == 0) {
        description = [NSString stringWithFormat:@"初始化引擎成功 \n [%@] \n currentUser : %@  \n message : %@",[HWTools nowDate], _userID, msg];
        [[HWPGMEngine getInstance] enableSpeakerDetection:1000];
        __weak typeof(self) weakSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            OperationViewController *operationVC = [[OperationViewController alloc] initWithUserId:self.userID];
            // 销毁引擎后得回调信息
            operationVC.destoryBlock = ^(NSString *desc) {
                [weakSelf.logTableView setLogData:desc];
            };
            [self.navigationController pushViewController:operationVC animated:YES];
        });
    } else {
        description = @"初始化引擎失败";
    }
    [self.logTableView setLogData:description];
}

@end
