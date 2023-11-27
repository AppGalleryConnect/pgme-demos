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
#import <HWPGMEKit/HWPGMEObject.h>
#import "Player+RoomPlayer.h"
#import "RoomMuteInfo.h"
#import "RoomBtnEnabledInfo.h"
#import "Constants.h"
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

/// 房间的屏蔽信息
@property(nonatomic, strong) RoomMuteInfo *roomMuteInfo;
@property(nonatomic, strong) RoomBtnEnabledInfo *roomBtnEnabledInfo;
@end

@implementation RoomPlayerListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupViews];
    [self addNotification];
}

- (void)addNotification {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(ForbiddenFunc:) name:FORBID_OR_NOT object:nil];
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
    BOOL allPlayerIsMute = [self.roomMuteInfo allPlayerIsMuteForKey:roomInfo.roomId];
    NSMutableArray *muteArray = [self.roomMuteInfo muteArrayForKey:roomInfo.roomId];
    for (Player *player in roomInfo.players) {
        if (allPlayerIsMute) {
            player.isMute = ![player.openId isEqualToString:self.ownerId];
        } else {
            for (Player *mutePlayer in muteArray) {
                if ([player.openId isEqualToString:mutePlayer.openId]) {
                    player.isMute = mutePlayer.isMute;
                }
            }
        }
        for (Player *btnPlayer in [self.roomBtnEnabledInfo playerArrayForKey:roomInfo.roomId]) {
            if ([player.openId isEqualToString:btnPlayer.openId]) {
                player.isForbiddenBtnDisabled = btnPlayer.isForbiddenBtnDisabled;
            }
        }
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

- (void)forbidPlayer:(NSString *)roomId openId:(NSString *)openId isForbidden:(BOOL)isForbidden {
    for (Player *player in self.playerListMArr) {
        if ([player.openId isEqualToString:openId]) {
            player.isForbidden = isForbidden;
            player.isForbiddenBtnDisabled = NO;
        }
    }
    [self.roomBtnEnabledInfo setObjectPlayerArray:[self.playerListMArr copy] roomId:roomId];
    [self reloadData];
}

- (void)forbidAllPlayers:(NSString *)roomId openIds:(NSArray *)openIds isForbidden:(BOOL)isForbidden {
    self.allPlayerIsForbidden = isForbidden;
    [self.roomMuteInfo setForbiddenAll:isForbidden roomId:roomId];
    for (Player *player in self.playerListMArr) {
        if (![player.openId isEqualToString:self.ownerId]) {
            player.isForbidden = isForbidden;
        }
    }
    [self reloadData];
}

- (void)mutePlayer:(NSString *)roomId openId:(NSString *)openId isMuted:(BOOL)isMuted {
    for (Player *player in self.playerListMArr) {
        if ([player.openId isEqualToString:openId]) {
            player.isMute = isMuted;
        }
    }
    [self.roomMuteInfo setObjectMuteArray:[self.playerListMArr mutableCopy] allPlayerIsMute:NO roomId:roomId];
    [self reloadData];
}

- (void)muteAllPlayers:(NSString *)roomId openIds:(NSArray *)openIds isMuted:(BOOL)isMuted {
    self.allPlayerIsMute = isMuted;
    for (Player *player in self.playerListMArr) {
        if (![player.openId isEqualToString:self.ownerId]) {
            player.isMute = isMuted;
        }
    }
    [self.roomMuteInfo setObjectMuteArray:[self.playerListMArr mutableCopy] allPlayerIsMute:isMuted roomId:roomId];
    [self reloadData];
}

- (void)leaveRoom:(NSString *)roomId {
    [self.roomMuteInfo removeCacheRoom:roomId];
    [self.roomBtnEnabledInfo removeCacheRoom:roomId];
    [self.roomMuteInfo removeForbiddenAllForKey:roomId];
}

- (void)playerOffline:(NSString *)roomId openId:(NSString *)openId {
    [self.roomMuteInfo removeCachePlayerWithRoomId:roomId openId:openId];
    [self.roomBtnEnabledInfo removeCachePlayerWithRoomId:roomId openId:openId];
}

- (void)changeSpatialAudioButtonEnable:(BOOL)enable {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.headerView changeSpatialAudioButtonEnable:enable];
    });
}

- (void)updateSpatialAudioButtonSelected:(BOOL)selected {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.headerView updateSpatialAudioButtonSelected:selected];
    });
}

- (void)reloadData {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}

/// 禁言
- (void)ForbiddenFunc:(NSNotification *)notification {
    if (![[HWPGMEngine getInstance].engineParam.openId isEqualToString:self.roomInfo.ownerId]) {
        return;
    }
    NSDictionary *info = [notification userInfo];
    NSString *userId = info[@"userId"];
    for (Player *player in self.playerListMArr) {
        if ([player.openId isEqualToString:userId]) {
            player.isForbiddenBtnDisabled = YES;
        }
    }
    [self.roomBtnEnabledInfo setObjectPlayerArray:[self.playerListMArr copy] roomId:self.roomInfo.roomId];
}


#pragma mark - UITableViewDataSource UITableViewDelegate

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 55;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    if (!self.headerView) {
        self.headerView = [[PlayerHeaderView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 55)];
    }
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
    
    [self.headerView changeSpatialAudioButtonEnable:self.isEnableSpatialAudio];
    NSString *roomTitle = @"小队";
    if (self.roomInfo.roomType == ROOM_TYPE_NATIONAL) {
        roomTitle = @"国战";
    }
    if (self.roomInfo.roomType == ROOM_TYPE_RANGE) {
        roomTitle = @"范围";
    }
    [self.headerView headerTitleWithRoomType:roomTitle
                                 playerCount:self.playerListMArr.count
                        allPlayerIsForbidden:[self.roomMuteInfo allPlayerIsForbiddenForKey:self.roomInfo.roomId]
                        allPlayerSpeakIsMute:[self.roomMuteInfo allPlayerIsMuteForKey:self.roomInfo.roomId]];
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

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:FORBID_OR_NOT object:nil];
}
#pragma mark - getter setter -

- (void)setIsEnableSpatialAudio:(BOOL)isEnableSpatialAudio {
    _isEnableSpatialAudio = isEnableSpatialAudio;
}


- (NSMutableArray *)playerListMArr {
    if (!_playerListMArr) {
        _playerListMArr = [NSMutableArray array];
    }
    return _playerListMArr;
}

- (RoomMuteInfo *)roomMuteInfo {
    if (!_roomMuteInfo) {
        _roomMuteInfo = [[RoomMuteInfo alloc] init];
    }
    return _roomMuteInfo;
}

- (RoomBtnEnabledInfo *)roomBtnEnabledInfo {
    if (!_roomBtnEnabledInfo) {
        _roomBtnEnabledInfo = [[RoomBtnEnabledInfo alloc] init];
    }
    return _roomBtnEnabledInfo;
}
@end
