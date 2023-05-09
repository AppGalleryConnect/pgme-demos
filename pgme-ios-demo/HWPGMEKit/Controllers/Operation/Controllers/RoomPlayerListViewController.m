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
#import "RoomPlayerListViewController.h"
#import "PlayerListTableViewCell.h"
#import "PlayerHeaderView.h"
#import "HWPGMEObject.h"
#import "Player+RoomPlayer.h"

@interface RoomPlayerListViewController ()<UITableViewDataSource,UITableViewDelegate>

/// 玩家列表
@property (nonatomic, strong) UITableView *tableView;

/// 小队成员view
@property (nonatomic, strong) PlayerHeaderView *headerView;

/// 玩家列表数据源
@property (nonatomic, strong) NSMutableArray *playerListMArr;

/// 房间信息
@property (nonatomic, strong) Room *roomInfo;

/// 是否禁止所有玩家麦克风
@property (nonatomic, assign) BOOL allPlayerIsForbidden;

/// 是否屏蔽所有玩家声音
@property (nonatomic, assign) BOOL allPlayerIsMute;

/// 房主的openId
@property (nonatomic, copy) NSString *ownerId;

@end

@implementation RoomPlayerListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupViews];
}

- (void)setupViews {
    self.tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    self.view = self.tableView;
    self.tableView.estimatedRowHeight = 50;
    self.tableView.bounces = NO;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    if (@available(iOS 15.0, *)) {
        self.tableView.sectionHeaderTopPadding = 0;
    }
    self.tableView.backgroundColor = [UIColor clearColor];
    [self.tableView registerClass:[PlayerListTableViewCell class]
           forCellReuseIdentifier:NSStringFromClass([PlayerListTableViewCell class])];
}

#pragma mark - func

- (void)refreshListWithRoomInfo:(Room *)roomInfo ownerId:(NSString *)ownerId {
    [_playerListMArr removeAllObjects];
    if (roomInfo == nil) {
        self.allPlayerIsForbidden = NO;
        self.allPlayerIsMute = NO;
        [self.tableView reloadData];
        return;
    }
    _roomInfo = roomInfo;
    _ownerId = ownerId;
    for (Player *player in roomInfo.players) {
        [_playerListMArr addObject:player];
    }
    [self.tableView reloadData];
}

- (void)speakingWithUsers:(NSArray *)users {
    for (Player *player in self.playerListMArr) {
        if ([users containsObject:player.openId]) {
            player.isSpeaking = YES;
        }else {
            player.isSpeaking = NO;
        }
    }
    [self reloadData];
}

- (void)forbidPlayer:(NSString *)openId isForbidden:(BOOL)isForbidden {
    for (Player *player in self.playerListMArr) {
        if ([player.openId isEqualToString:openId]) {
            player.isForbidden = isForbidden;
        }
    }
    [self reloadData];
}

- (void)forbidAllPlayers:(NSString *)roomId openIds:(NSArray *)openIds isForbidden:(BOOL)isForbidden {
    self.allPlayerIsForbidden = isForbidden;
    for (Player *player in self.playerListMArr) {
        if (![player.openId isEqualToString:self.ownerId]) {
            player.isForbidden = isForbidden;
        }
    }
    [self reloadData];
}

- (void)mutePlayer:(NSString *)openId isMuted:(BOOL)isMuted {
    for (Player *player in self.playerListMArr) {
        if ([player.openId isEqualToString:openId]) {
            player.isMute = isMuted;
        }
    }
    [self reloadData];
}

- (void)muteAllPlayers:(NSString *)roomId openIds:(NSArray *)openIds isMuted:(BOOL)isMuted {
    self.allPlayerIsMute = isMuted;
    for (Player *player in self.playerListMArr) {
        if (![player.openId isEqualToString:self.ownerId]) {
            player.isMute = isMuted;
        }
    }
    [self reloadData];
}

- (void)reloadData {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}

#pragma mark - UITableViewDataSource UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 55;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    self.headerView = [[PlayerHeaderView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 55)];
    if (self.roomInfo.roomType == ROOM_TYPE_TEAM && [self.roomInfo.ownerId isEqualToString:self.ownerId]) {
        self.headerView.isMicHidden = NO;
    }else {
        self.headerView.isMicHidden = YES;
    }
    if ([self.roomInfo.ownerId isEqualToString:self.ownerId] && self.playerListMArr.count == 1) {
        self.headerView.isMicEnable = NO;
    }else {
        self.headerView.isMicEnable = YES;
    }
    
    self.headerView.isOwnerTransferHidden = ![self.roomInfo.ownerId isEqualToString:self.ownerId];
    NSString *roomTitle = self.roomInfo.roomType == ROOM_TYPE_TEAM ? @"小队" : @"国战";
    [self.headerView headerTitleWithRoomType:roomTitle
                                 playerCount:self.playerListMArr.count
                        allPlayerIsForbidden:self.allPlayerIsForbidden
                        allPlayerSpeakIsMute:self.allPlayerIsMute];
    self.headerView.hidden = self.playerListMArr.count > 0 ? NO : YES;
    return self.headerView;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.playerListMArr.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PlayerListTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:NSStringFromClass([PlayerListTableViewCell class])];
    if (!cell) {
        cell = [[PlayerListTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                              reuseIdentifier:NSStringFromClass([PlayerListTableViewCell class])];
    }
    Player *player = self.playerListMArr[indexPath.row];
    [cell cellWithIndexPath:indexPath
                  roomInfo:self.roomInfo
                    player:player];
    return cell;
}

- (NSMutableArray *)playerListMArr {
    if (!_playerListMArr) {
        _playerListMArr = [NSMutableArray array];
    }
    return _playerListMArr;
}

@end
