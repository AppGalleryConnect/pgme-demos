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

#import "HWBaseViewController.h"
#import "UIColor+HWColor.h"
#import "UIImage+HWColor.h"

@interface HWBaseViewController ()


/// 状态栏
@property (nonatomic, strong) UIView *statusBar;

@end

@implementation HWBaseViewController

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    //恢复webView状态栏为白色
    if (@available(iOS 13.0, *)) {
        if ([[UIApplication sharedApplication].keyWindow.subviews containsObject:self.statusBar]) {
            [self.statusBar removeFromSuperview];
        }
    } else {
        UIView *statusBar = [[[UIApplication sharedApplication] valueForKey:@"statusBarWindow"] valueForKey:@"statusBar"];
        if ([statusBar respondsToSelector:@selector(setBackgroundColor:)]) {
            statusBar.backgroundColor = UIColor.clearColor;
        }
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    UIImageView *bgimageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, SCREENWIDTH, SCREENHEIGHT)];
    bgimageView.image = [UIImage imageNamed:@"bg_login"];
    [self.view addSubview:bgimageView];
    
    [self addNotificationObserver];
    [self setupNavigation];
}

/// 设置navigationbar的样式
- (void)setupNavigation {
    UIImage *bgImage = [UIImage imageNamed:@"bg_navigationbar"];
    if (@available(iOS 13.0, *)) {
        UINavigationBarAppearance *appearance = [UINavigationBarAppearance new];
        [appearance configureWithOpaqueBackground];
        //设置背景图
        appearance.backgroundImage = bgImage;
        appearance.titleTextAttributes = @{NSFontAttributeName:[UIFont systemFontOfSize:18 weight:UIFontWeightSemibold],
                                           NSForegroundColorAttributeName:[UIColor hw_titleColor]};
        self.navigationController.navigationBar.standardAppearance = appearance;
        self.navigationController.navigationBar.scrollEdgeAppearance = self.navigationController.navigationBar.standardAppearance;
    }else{
        [self.navigationController.navigationBar setBackgroundImage:bgImage forBarMetrics:UIBarMetricsDefault];
        self.navigationController.navigationBar.translucent = NO;
        [self.navigationController.navigationBar setTitleTextAttributes:
         @{NSFontAttributeName:[UIFont systemFontOfSize:18 weight:UIFontWeightSemibold],
           NSForegroundColorAttributeName:[UIColor hw_titleColor]}];
    }
    self.navigationItem.hidesBackButton = YES;
}

- (void)addNotificationObserver {
    // app生命后台
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(observeApplicationDidEnterBackground:) name:UIApplicationWillResignActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(observeApplicationWillEnterForeground:) name:UIApplicationDidBecomeActiveNotification object:nil];
}

- (void)observeApplicationWillEnterForeground:(NSNotification *)notification {
    
}

- (void)observeApplicationDidEnterBackground:(NSNotification *)notification {
    
}

- (UIView *)statusBar{
    if (!_statusBar) {
        if (@available(iOS 13.0, *)) {
            _statusBar = [[UIView alloc] initWithFrame:[UIApplication sharedApplication].keyWindow.windowScene.statusBarManager.statusBarFrame];
        }
    }
    return _statusBar;
}

- (void)dealloc {
    NSLog(@"%@--->dealloc 释放了",NSStringFromClass([self class]));
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
