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

#import "OperationListView.h"

#import "RoomPlayerListViewController.h"
#import "RoomLogViewController.h"

@interface OperationListView ()

@property (nonatomic, strong) UIImageView *bgImageView;

/// 房间成员和查看日志view的父容器
@property (nonatomic, strong) UIScrollView *scrollView;

/// 玩家列表Controller
@property (nonatomic, strong) RoomPlayerListViewController *playerListVC;

/// 日志Controller
@property (nonatomic, strong) RoomLogViewController *logVC;

@end

@implementation OperationListView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.bgImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_list"]];
    self.bgImageView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
    self.bgImageView.userInteractionEnabled = YES;
    [self addSubview:self.bgImageView];
    [self.bgImageView addSubview:self.scrollView];
    // 房间用户列表
    RoomPlayerListViewController *playerListVC = [[RoomPlayerListViewController alloc] init];
    playerListVC.view.frame = CGRectMake(0, 0, self.scrollView.frame.size.width, self.scrollView.frame.size.height);
    _playerListVC = playerListVC;
    [self.scrollView addSubview:playerListVC.view];
    // 房间日志
    RoomLogViewController *logVC = [[RoomLogViewController alloc] init];
    logVC.view.frame = CGRectMake(self.scrollView.frame.size.width, 0, self.scrollView.frame.size.width, self.scrollView.frame.size.height);
    _logVC = logVC;
    [self.scrollView addSubview:logVC.view];
}

#pragma mark - set

/// 切换当前显示的列表
/// @param nowViewPage 枚举类型
- (void)setNowViewPage:(kNowViewPage)nowViewPage {
    switch (nowViewPage) {
        case kNowViewIsLog:{
            [UIView animateWithDuration:0.3 animations:^{
                self.scrollView.contentOffset = CGPointMake(self.scrollView.frame.size.width, 0);
            }];
        }
            break;
            
        case kNowViewIsPlayerList:{
            [UIView animateWithDuration:0.3 animations:^{
                self.scrollView.contentOffset = CGPointMake(0, 0);
            }];
        }
            break;
            
        default:
            break;
    }
}

- (void)addLog:(NSString *)log {
    [_logVC addLogMessage:log];
}

- (void)clearAllLog {
    [_logVC clearAllLogs];
}

- (void)refreshPlayerListWithRoomInfo:(Room *)roomInfo ownerId:(NSString *)ownerId {
    dispatch_async(dispatch_get_main_queue(), ^{
        [_playerListVC refreshListWithRoomInfo:roomInfo ownerId:ownerId];
    });
}

- (void)speakingUsers:(NSArray *)users {
    [_playerListVC speakingWithUsers:users];
}

- (void)changePlayerStateWithRoomId:(NSString *)roomId
                             openId:(NSString *)openId
                        playerState:(kPlayerState)playerState
                           isEnable:(BOOL)isEnable {
    switch (playerState) {
        case kPlayerMicState: {
            [_playerListVC forbidPlayer:openId isForbidden:isEnable];
        }
            break;
        case kPlayerSpeakState: {
            [_playerListVC mutePlayer:openId isMuted:isEnable];
        }
            break;
            
        default:
            break;
    }
}

- (void)changeAllPlayerStateWithRoomId:(NSString *)roomId
                               openIds:(NSArray *)openIds
                           playerState:(kPlayerState)playerState
                              isEnable:(BOOL)isEnable {
    switch (playerState) {
        case kPlayerMicState: {
            [_playerListVC forbidAllPlayers:roomId openIds:openIds isForbidden:isEnable];
        }
            break;
        case kPlayerSpeakState: {
            [_playerListVC muteAllPlayers:roomId openIds:openIds isMuted:isEnable];
        }
            break;
            
        default:
            break;
    }
}

- (UIScrollView *)scrollView {
    if (!_scrollView) {
        _scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(2, 3, self.bgImageView.frame.size.width-4, self.bgImageView.frame.size.height-3)];
        _scrollView.contentSize = CGSizeMake(self.scrollView.frame.size.width*2, self.bgImageView.frame.size.height);
        _scrollView.showsVerticalScrollIndicator = NO;
        _scrollView.showsHorizontalScrollIndicator = NO;
        _scrollView.pagingEnabled = YES;
        _scrollView.scrollEnabled = NO;
    }
    return _scrollView;
}

@end
