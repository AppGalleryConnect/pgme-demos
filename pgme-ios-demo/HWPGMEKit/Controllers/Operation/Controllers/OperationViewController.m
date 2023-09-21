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

#import "OperationViewController.h"
#import "OperationInitRoomView.h"
#import "OperationListView.h"
#import "OperationBottomButtonView.h"
#import "RoomIdList.h"
#import "HWDropDownMenu.h"
#import <HWPGMEKit/HWPGMEngine.h>
#import "HWAlertView.h"
#import "HWVoiceAlertView.h"
#import "HWPGMEDelegate.h"
#import "Constants.h"
#import <HWPGMEKit/VoiceParam.h>
#import "AudioMsgViewController.h"
#import "AudioEffectAlertView.h"
#import "PlayerLocationViewController.h"
#import "PlayerLocationCache.h"
#import "MatrixTool.h"
#import <simd/simd.h>


@interface OperationViewController ()<OperationInitRoomViewDelegate,OperationBottomButtonViewDelegate,HWDropDownMenuDelegate,HWPGMEngineDelegate> {
}

/// 全局容器scrollView
@property (nonatomic, strong) UIScrollView *scrollView;
/// 顶部按钮的父容器view
@property (nonatomic, strong) OperationInitRoomView *operationRoomView;
/// 房间成员和日志列表的view
@property (nonatomic, strong) OperationListView *listView;
/// 存储房间列表的容器
@property (nonatomic, strong) RoomIdList *roomIdList;
/// userId
@property (nonatomic, copy) NSString *userId;
/// 语音转文字弹框
@property (nonatomic, strong) HWVoiceAlertView *voiceAlertView;
/// 音效弹框
@property(nonatomic, strong) AudioEffectAlertView *effectAlertView;
@property(nonatomic, strong) PlayerLocationCache *locationCache;
@end

@implementation OperationViewController

- (instancetype)initWithUserId:(NSString *)userId {
    if (self = [super init]) {
        _userId = userId;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupViews];
    [self setupNotificationObserver];
    int code = [[HWPGMEngine getInstance] initSpatialSound];
    [self.listView changeSpatialAudioButtonEnable:!code];
    if (code != SUCCESS) {
        [HWTools showMessage:@"3D音效初始化失败"];
    }
    [self defaultSelfPosition];
}

- (void)setupViews {
    self.view.backgroundColor = [UIColor whiteColor];
    self.title = @"Real Time Voice";
    self.roomIdList = [[RoomIdList alloc] init];
    [HWPGMEDelegate.getInstance addDelegate:self];
    self.scrollView.frame = CGRectMake(0, kGetSafeAreaTopHeight+kNavBarHeight, SCREENWIDTH, SCREENHEIGHT - (kGetSafeAreaTopHeight+kNavBarHeight + kGetSafeAreaBottomHeight));
    [self.view addSubview:self.scrollView];
    OperationInitRoomView *initView = [[OperationInitRoomView alloc] initWithFrame:CGRectMake(0, 0, SCREENWIDTH, 230)];
    initView.delegate = self;
    _operationRoomView = initView;
    [self.scrollView addSubview:initView];
    
    OperationListView *listView = [[OperationListView alloc] initWithFrame:CGRectMake(35, CGRectGetMaxY(initView.frame), SCREENWIDTH-35*2, 350)];
    _listView = listView;
    [self.scrollView addSubview:listView];
    
    OperationBottomButtonView *buttonView = [[OperationBottomButtonView alloc] init];
    buttonView.frame = CGRectMake(0, CGRectGetMaxY(_listView.frame), SCREENWIDTH, [buttonView operationBottomViewHeight]);
    buttonView.delegate = self;
    [self.scrollView addSubview:buttonView];
    
    CGFloat bounceHeight = CGRectGetMaxY(buttonView.frame) > self.scrollView.bounds.size.height ? 80 : 0;
    [self.scrollView setContentSize:CGSizeMake(SCREENWIDTH, CGRectGetMaxY(buttonView.frame) + bounceHeight)];
}

- (void)setupNotificationObserver {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(clearButtonPressed) name:CLEAR_SCREEN object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(micButtonPressed:) name:FORBID_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(speakingButtonPressed:) name:MUTE_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(forbidAllButtonPressed:) name:FORBID_ALL_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(muteAllButtonPressed:) name:MUTE_ALL_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(ownerTransferButtonPressed) name:OWNER_TRANSFER object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(spatialAudioButtonPressed:) name:ENABLE_SPATIAL_AUDIO object:nil];
    [_roomIdList addObserver:self forKeyPath:@"roomIdMArr" options:NSKeyValueObservingOptionNew context:nil];
}

// 返回到登陆页
- (void)backToLogin {
    [HWPGMEDelegate.getInstance removeDelegate:self];
    __block UIViewController *loginVC;
    [self.navigationController.viewControllers enumerateObjectsUsingBlock:^(__kindof UIViewController * _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:NSClassFromString(@"LoginViewController")]) {
            loginVC = obj;
            *stop = YES;
        }
    }];
    [self.navigationController popToViewController:loginVC animated:YES];
}

/// 通过监听数组来改变离开房间按钮的状态
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    if ([keyPath isEqualToString:@"roomIdMArr"]) {
        RoomIdList *roomIdList = (RoomIdList *)object;
        // 数据源为0时，离开房间按钮不可点击
        self.operationRoomView.isLeaveButtonDisable = roomIdList.dataList.count > 0 ? NO : YES;
    }
}

#pragma mark - private func

// 获取房间3D音效开启状态，更新房间3D音效状态按钮
- (void)getRoomSpatialAudioEnable:(NSString *)roomId {
    if ([roomId isEqual: self.operationRoomView.roomID]) {
        BOOL enable = [[HWPGMEngine getInstance] isEnableSpatialSound:roomId];
        [self.listView updateSpatialAudioButtonSelected:enable];
    }
}

- (void)getRoomInfoWithRoomId:(NSString *)roomId {
    if ([roomId isEqual: self.operationRoomView.roomID]) {
        Room *room = [[HWPGMEngine getInstance] getRoom:roomId];
        if (![[self getRoomIdsArray] containsObject:roomId]) {
            if (room) {
                [self.roomIdList addObject:room];
            }
        }
        [self.listView refreshPlayerListWithRoomInfo:room ownerId:self.userId];
    }
}

- (NSArray *)getRoomIdsArray {
    NSMutableArray *array = [NSMutableArray array];
    for (int i = 0; i < self.roomIdList.dataList.count; i ++) {
        Room *room = [self.roomIdList objectAtIndex:i];
        [array addObject:room.roomId];
    }
    return array;
}

/// 离开房间选择房主
- (void)leaveChangeRole {
    NSUInteger roomCount = self.roomIdList.dataList.count;
    if (roomCount == 0) {
        return;
    }
    
    if (roomCount == 1) {
        [self leaveRoomByRoomId: self.operationRoomView.roomID];
    } else {
        NSMutableArray *roomIds = [NSMutableArray array];
        for (int i = 0; i < self.roomIdList.dataList.count; i ++) {
            Room *room = [self.roomIdList objectAtIndex:i];
            [roomIds addObject:room.roomId];
        }
        HWAlertView *hwAlert = [HWAlertView alertWithTitle:@"请选择需要离开的房间"
                                              isMustSelect:TRUE
                                                  options:roomIds
                                            enter:^(NSInteger index) {
            NSString *roomId = (index == NotSelected) ? @"" : roomIds[index];
            [self leaveRoomByRoomId:roomId];
        }];
    }
}

- (void) leaveRoomByRoomId: (NSString *)roomId{
    Room *room = [[HWPGMEngine getInstance] getRoom:roomId];
    if (room.players.count > 1 && [room.ownerId isEqualToString:self.userId]) {
        NSMutableArray *playerIds = [NSMutableArray array];
        for (Player *player in room.players) {
            if (![player.openId isEqualToString:self.userId]) {
                [playerIds addObject:player.openId];
            }
        }
        [HWAlertView alertWithTitle:@"离开房间指定下一任房主"
                       isMustSelect:FALSE
                            options:playerIds
                              enter:^(NSInteger index) {
            NSString *ownerId = (index == NotSelected) ? @"" : playerIds[index];
            [[HWPGMEngine getInstance] leaveRoom:roomId ownerId:ownerId];
        }];
    }else {
        [[HWPGMEngine getInstance] leaveRoom:roomId ownerId:@""];
    }
}


#pragma mark - observerEvent

/// 用户列表麦克风按钮点击
/// @param notification notification
- (void)micButtonPressed:(NSNotification *)notification {
    NSDictionary *info = [notification userInfo];
    UIButton *button = (UIButton *)info[@"button"];
    NSString *userId = info[@"userId"];
    [[HWPGMEngine getInstance] forbidPlayer:self.operationRoomView.roomID openId:userId isForbidden:!button.selected];
}

/// 小队麦克风按钮点击
/// @param notification notification
- (void)forbidAllButtonPressed:(NSNotification *)notification {
    NSDictionary *info = [notification userInfo];
    UIButton *button = (UIButton *)info[@"button"];
    [[HWPGMEngine getInstance] forbidAllPlayers:self.operationRoomView.roomID isForbidden:!button.selected];
}

/// 用户列表扬声器按钮点击
/// @param notification notification
- (void)speakingButtonPressed:(NSNotification *)notification {
    NSDictionary *info = [notification userInfo];
    UIButton *button = (UIButton *)info[@"button"];
    NSString *userId = info[@"userId"];
    [[HWPGMEngine getInstance] mutePlayer:self.operationRoomView.roomID openId:userId isMuted:!button.selected];
}

/// 扬声器按钮点击
/// @param notification notification
- (void)muteAllButtonPressed:(NSNotification *)notification {
    NSDictionary *info = [notification userInfo];
    UIButton *button = (UIButton *)info[@"button"];
    [[HWPGMEngine getInstance] muteAllPlayers:self.operationRoomView.roomID isMuted:!button.selected];
}

/// 转移房主按钮点击
- (void)ownerTransferButtonPressed {
    Room *room = [[HWPGMEngine getInstance] getRoom:self.operationRoomView.roomID];
    if (room.players.count > 1 && [room.ownerId isEqualToString:self.userId]) {
        NSMutableArray *playerIds = [NSMutableArray array];
        for (Player *player in room.players) {
            if (![player.openId isEqualToString:self.userId]) {
                [playerIds addObject:player.openId];
            }
        }
        [HWAlertView alertWithTitle:@"指定下一任房主"
                       isMustSelect:FALSE
                            options:playerIds
                              enter:^(NSInteger index) {
            NSString *ownerId = (index == NotSelected) ? @"" : playerIds[index];
            [[HWPGMEngine getInstance] transferOwner:self.operationRoomView.roomID ownerId:ownerId];
        }];
    }
}

/// 开启/关闭3D音效按钮点击
/// @param notification 通知内容
- (void)spatialAudioButtonPressed:(NSNotification *)notification {
    NSDictionary *info = [notification userInfo];
    UIButton *spatialAudioButton = (UIButton *)info[@"button"];
    int code = [[HWPGMEngine getInstance] enableSpatialSound:self.operationRoomView.roomID enable:spatialAudioButton.isSelected];
    if (code != SUCCESS) {
        [self.listView updateSpatialAudioButtonSelected:!spatialAudioButton.isSelected];
        [HWTools showMessage:(spatialAudioButton.isSelected ? @"开启3D音效失败" : @"关闭3D音效失败")];
    }
}

/// 日志清屏按钮点击
- (void)clearButtonPressed {
    NSLog(@"清屏");
    [self.listView clearAllLog];
}

#pragma mark - HWDropDownMenuDelegate

/// 选择要切换的房间id
/// @param view 下拉菜单view
/// @param indexPath 选中下拉菜单的索引
- (void)HWDropDownMenu:(UIView *)view didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    Room *room = [self.roomIdList switchObjectAtIndex:indexPath.row];
    self.operationRoomView.roomID = room.roomId;
    [[HWPGMEngine getInstance] switchRoom:room.roomId];
}

#pragma mark - OperationInitRoomViewDelegate
// 切换房间
- (void)switchRoomTextFieldPressed:(UIView *)initRoomView switchRoomTextField:(UITextField *)switchRoomTextField {
    [HWDropDownMenu showOnView:switchRoomTextField
                        titles:[self getRoomIdsArray]
                      delegate:self
                 otherSettings:^(HWDropDownMenu * _Nonnull dropDownMenu) {
        dropDownMenu.bgImage = [UIImage imageNamed:@"bg_dropdown_menu"];
        dropDownMenu.showMaskView = NO;
        dropDownMenu.cornerRadius = 0;
    }];
}

// 加入小队
- (void)teamButtonPressed:(UIView *)initRoomView teamButton:(UIButton *)teamButton roomID:(NSString *)roomID {
    [[HWPGMEngine getInstance] joinTeamRoom:roomID];
}

// 加入国战
- (void)nationalWarButtonPressed:(UIView *)initRoomView
               nationalWarButton:(UIButton *)nationalWarButton
                          roomID:(NSString *)roomID {
    [HWAlertView alertWithTitle:@"请选择加入国战房间的身份"
                   isMustSelect:FALSE
                        options:@[@"指挥家",@"群众"]
                          enter:^(NSInteger index) {
        RoomRole roleType = index == 0 ? ROOM_ROLE_JOINER : ROOM_ROLE_PLAYER;
        [[HWPGMEngine getInstance] joinNationalRoom:roomID roleType:roleType];
    }];
}

// 加入范围
- (void)joinRangeButtonPressed:(UIView *)initRoomView
               joinRangeButton:(UIButton *)joinRangeButton
                            roomID:(NSString *)roomID {
    [[HWPGMEngine getInstance] joinRangeRoom:roomID];
}

// 离开房间
- (void)leaveRoomButtonPressed:(UIView *)initRoomView leaveRoomButton:(UIButton *)leaveRoomButton {
    NSLog(@"离开房间");
    [self leaveChangeRole];
}

// 开麦
- (void)turnonMicButtonPressed:(UIView *)initRoomView turnonMicButton:(UIButton *)turnonMicButton {
    NSLog(@"%@",turnonMicButton.isSelected ? @"开麦" : @"关麦");
    [[HWPGMEngine getInstance] enableMic:turnonMicButton.isSelected];
}

// 语音转文字
- (void)voiceToTextButtonPressed:(UIView *)initRoomView voiceToTextButton:(UIButton *)voiceToTextButton {
    self.voiceAlertView = [HWVoiceAlertView alert:^() {
        [[HWPGMEngine getInstance] stopRecordAudioToText];
    }];
    VoiceParam * voiceParam = [[VoiceParam alloc] init];
    voiceParam.languageCode = @"zh";
    [[HWPGMEngine getInstance] startRecordAudioToText:voiceParam];
}

#pragma mark - OperationBottomButtonViewDelegate
// 房间成员和查看日志切换button
- (void)switchButtonPressed:(UIView *)buttonView switchButton:(UIButton *)switchButton {
    self.listView.nowViewPage = switchButton.isSelected ? kNowViewIsLog : kNowViewIsPlayerList;
}

// 引擎销毁button
- (void)destoryButtonPressed:(UIView *)buttonView destoryButton:(UIButton *)destoryButton {
    [[HWPGMEngine getInstance] destroy];
}

/// 语音消息
- (void)audioMsgButtonPressed:(UIView *)buttonView audioMsgButton:(UIButton *)audioMsgButton {
    AudioMsgViewController *audioMsgVC = [[AudioMsgViewController alloc] init];
    [self.navigationController pushViewController:audioMsgVC animated:YES];
}
    
/// 音效
- (void)audioEffectButtonPressed:(UIView *)buttonView audioEffectButton:(UIButton *)audioEffectButton {
    [self.effectAlertView showAlerOnView:self.view];
}

- (PlayerPosition *)convertToPlayerPosition:(PositionModel *)positionModel {
    PlayerPosition *playerPosition = [[PlayerPosition alloc]init];
    playerPosition.forward = positionModel.forward;
    playerPosition.right = positionModel.right;
    playerPosition.up = positionModel.up;
    return playerPosition;
}

- (Axis *)convertToAxis:(AxisModel *)axisModel {
    Axis *axis = [[Axis alloc]init];
    NSArray* matrix = [MatrixTool getRotateMatrix:axisModel.right up:axisModel.up forward:axisModel.forward];
    axis.forward = simd_make_float3([matrix[0][0] floatValue], [matrix[1][0] floatValue], [matrix[2][0] floatValue]);
    axis.right = simd_make_float3([matrix[0][1] floatValue], [matrix[1][1] floatValue], [matrix[2][1] floatValue]);
    axis.up = simd_make_float3([matrix[0][2] floatValue], [matrix[1][2] floatValue], [matrix[2][2] floatValue]);
    return axis;
}

/// 玩家位置
- (void)playerPositionButtonPressed:(UIView *)buttonView playerPositionButton:(UIButton *)playerPositionButton {
    PlayerLocationViewController *locationVC = [[PlayerLocationViewController alloc] init];
    [locationVC configDataSelfModel:self.locationCache.selfLocationModel othersModel:self.locationCache.otherLocationModel audioRecvRange:self.locationCache.audioRecvRange];
    __weak typeof(self) weakSelf = self;
    locationVC.updateSelfLocationBlock = ^(SelfLocationModel * _Nonnull model) {
        SelfPosition *selfPosition = [[SelfPosition alloc]init];
        selfPosition.position = [weakSelf convertToPlayerPosition:model.position];
        selfPosition.axis = [weakSelf convertToAxis:model.axis];
        [HWPGMEngine.getInstance updateSelfPosition:selfPosition];
        weakSelf.locationCache.selfLocationModel = model;
    };
    locationVC.updateOthersLocationBlock = ^(NSArray<OtherLocationModel *> * _Nonnull models) {
        if (models != nil) {
            NSMutableArray<RemotePlayerPosition *> *remotePlayers = [[NSMutableArray alloc]init];
            for (OtherLocationModel *otherLocationModel in models) {
                RemotePlayerPosition *position = [[RemotePlayerPosition alloc]init];
                position.openId = otherLocationModel.openId;
                position.position =  [weakSelf convertToPlayerPosition:otherLocationModel.position];
                [remotePlayers addObject:position];
                
            }
            [HWPGMEngine.getInstance updateRemotePosition:remotePlayers];
        }
        weakSelf.locationCache.otherLocationModel = models;
    };
    locationVC.updateAudioRecvRangeBlock = ^(NSInteger range) {
        int result = [[HWPGMEngine getInstance]setAudioRecvRange:(int)range];
        NSString *description = [NSString stringWithFormat:
                                 @"setAudioRecvRange : code : %d",
                                 result];
        NSLog(@"%@",description);
        if (result != 0) {
            [HWTools showMessage:[NSString stringWithFormat:@"设置失败，code:%d", result]];
            return;
        }
        weakSelf.locationCache.audioRecvRange = range;
    };
    [self.navigationController pushViewController:locationVC animated:YES];
}

#pragma mark - HWPGMEngineDelegate
- (void)onJoinTeamRoom:(NSString *)roomId code:(int)code msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onJoinTeamRoomLog : \n [%@] \n roomId : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        self.operationRoomView.roomID = roomId;
        [self getRoomInfoWithRoomId:roomId];
        [self getRoomSpatialAudioEnable:roomId];
    } else {
        [HWTools showMessage:description];
    }
}

- (void)onJoinNationalRoom:(NSString *)roomId code:(int)code msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onJoinNationalRoom : \n [%@] \n roomId : %@  \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        self.operationRoomView.roomID = roomId;
        [self getRoomInfoWithRoomId:roomId];
        [self getRoomSpatialAudioEnable:roomId];
    } else {
        [HWTools showMessage:description];
    }
}

- (void)onJoinRangeRoom:(NSString *)roomId code:(int)code msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
            @"onJoinRangeRoom : \n [%@] \n roomId : %@ \n code : %d \n massage : %@",
            [HWTools nowDate],
            roomId,
            code,
            msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        self.operationRoomView.roomID = roomId;
        [self getRoomInfoWithRoomId:roomId];
        [self getRoomSpatialAudioEnable:roomId];
    } else {
        [HWTools showMessage:description];
    }
}

- (void)onLeaveRoom:(NSString *)roomId code:(int)code msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onLeaveRoomLog : \n [%@] \n roomId : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    [self.listView leaveRoom:roomId];
    if (code == 0 && self.roomIdList.dataList.count > 0) {
        NSUInteger idx = [[self getRoomIdsArray] indexOfObject:roomId];
        [self.roomIdList removeObjectAtIndex:idx];
        if (self.roomIdList.dataList.count == 0) {
            self.operationRoomView.roomID = @"";
            [self.listView refreshPlayerListWithRoomInfo:nil ownerId:self.userId];
        }else {
            dispatch_async(dispatch_get_main_queue(), ^{
                Room *room = [self.roomIdList objectAtIndex:0];
                [[HWPGMEngine getInstance] switchRoom:room.roomId];
            });
        }
    }
}

- (void)onSwitchRoom:(NSString *)roomId code:(int)code msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onSwitchRoom : \n [%@] \n roomId : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        self.operationRoomView.roomID = roomId;
        [self getRoomInfoWithRoomId:roomId];
        [self getRoomSpatialAudioEnable:roomId];
    }else {
        Room *room = [self.roomIdList objectAtIndex:0];
        self.operationRoomView.roomID = room.roomId;
    }
}

- (void)onPlayerOnline:(NSString *)roomId openId:(NSString *)openId {
    NSString *description = [NSString stringWithFormat:
                             @"onPlayerOnline : \n [%@] \n roomId : %@ \n openId : %@",
                             [HWTools nowDate],
                             roomId,
                             openId];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    [self getRoomInfoWithRoomId:roomId];
}

- (void)onPlayerOffline:(NSString *)roomId openId:(NSString *)openId {
    NSString *description = [NSString stringWithFormat:
                             @"onPlayerOffline : \n [%@] \n roomId : %@ \n openId : %@",
                             [HWTools nowDate],
                             roomId,
                             openId];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    [self.listView playerOffline:roomId openId:openId];
    [self getRoomInfoWithRoomId:roomId];
}

- (void)onDestory:(int)code msg:(NSString *)msg {
    if (code == 0) {
        // 关闭麦克风
        NSString *description = [NSString stringWithFormat:
                          @"onDestory : \n [%@] \n code : %d \n massage : %@",
                          [HWTools nowDate],
                          code,
                          msg];
        self.destoryBlock(description);
        NSLog(@"%@",description);
        dispatch_async(dispatch_get_main_queue(), ^{
            [self backToLogin];
            if (_effectAlertView) {
                [_effectAlertView alertViewDestory];
            }
        });
    }
}

- (void)onSpeakerDetection:(NSMutableArray<NSString *> *)openIds {
}

- (void)onSpeakerDetectionEx:(NSMutableArray<VolumeInfo *> *)userVolumeInfos {
    if (userVolumeInfos && userVolumeInfos.count > 0) {
        NSMutableArray* openIds = [NSMutableArray arrayWithCapacity:[userVolumeInfos count]];
        for (VolumeInfo* volumeInfo in userVolumeInfos) {
            [openIds addObject:volumeInfo.openId];
        }
        [self.listView speakingUsers:openIds];
    }
}

- (void)onForbidPlayer:(NSString *)roomId
                openId:(NSString *)openId
           isForbidden:(BOOL)isForbidden
                  code:(int)code
                   msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onForbidPlayer : \n [%@] \n roomId : %@ \n openId : %@ \n isForbidden : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             openId,
                             isForbidden ? @"true" : @"false",
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        [self.listView changePlayerStateWithRoomId:roomId openId:openId playerState:kPlayerMicState isEnable:isForbidden];
    }
}

- (void)onForbidAllPlayers:(NSString *)roomId
                   openIds:(NSArray *)openIds
               isForbidden:(BOOL)isForbidden
                      code:(int)code
                       msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onForbidAllPlayers : \n [%@] \n roomId : %@ \n openIds : %@ \n isForbidden : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             openIds,
                             isForbidden ? @"true" : @"false",
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        [self.listView changeAllPlayerStateWithRoomId:roomId openIds:openIds playerState:kPlayerMicState isEnable:isForbidden];
    }
}

- (void)onForbiddenByOwner:(NSString *)roomId
                   openIds:(NSMutableArray<NSString *> *)openIds
               isForbidden:(Boolean)isForbidden {
    NSString *description = [NSString stringWithFormat:
                             @"onForbiddenByOwner : \n [%@] \n roomId : %@ \n openIds : %@ \n isForbidden : %@",
                             [HWTools nowDate],
                             roomId,
                             openIds,
                             isForbidden ? @"true" : @"false"];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        Room *room = [[HWPGMEngine getInstance] getRoom:roomId];
        for (Player *player in room.players) {
            for (NSString *openId in openIds) {
                if ([openId isEqualToString:player.openId]) {
                    [self.listView changePlayerStateWithRoomId:roomId
                                                        openId:openId
                                                   playerState:kPlayerMicState
                                                      isEnable:isForbidden];
                }
            }
        }
    });
}

- (void)onMutePlayer:(NSString *)roomId
              openId:(NSString *)openId
             isMuted:(BOOL)isMuted
                code:(int)code
                 msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onMutePlayer : \n [%@] \n roomId : %@ \n openId : %@ \n isMuted : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             openId,
                             isMuted ? @"true" : @"false",
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        [self.listView changePlayerStateWithRoomId:roomId openId:openId playerState:kPlayerSpeakState isEnable:isMuted];
    }
}

- (void)onMuteAllPlayers:(NSString *)roomId
                 openIds:(NSArray *)openIds
                 isMuted:(BOOL)isMuted
                    code:(int)code
                     msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onMuteAllPlayers : \n [%@] \n roomId : %@ \n openIds : %@ \n isMuted : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             openIds,
                             isMuted ? @"true" : @"false",
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    if (code == 0) {
        [self.listView changeAllPlayerStateWithRoomId:roomId openIds:openIds playerState:kPlayerSpeakState isEnable:isMuted];
    }
}

- (void)onTransferOwner:(NSString *)roomId code:(int)code msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onTransferOwner : \n [%@] \n roomId : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             roomId,
                             code,
                             msg];
    NSLog(@"%@",description);
    if (code == 0) {
        [self.listView addLog:description];
        [self getRoomInfoWithRoomId:roomId];
    }
}

- (void)onVoiceToText:(NSString *)text code:(int)code msg:(NSString *)msg {
    NSString *description = [NSString stringWithFormat:
                             @"onVoiceText : \n [%@] \n text : %@ \n code : %d \n massage : %@",
                             [HWTools nowDate],
                             text,
                             code,
                             msg];
    NSLog(@"%@",description);
    [self.listView addLog:description];
    dispatch_async(dispatch_get_main_queue(), ^{
        if (code == 0) {
            self.voiceAlertView.voiceText = text;
        } else if (code == VOICE_TO_TEXT_FEATURE_DISABLE) {
            self.voiceAlertView.voiceText = @"语音转文字功能未开通";
        } else if (code == AUDIO_AUTH_DENIED) {
            self.voiceAlertView.voiceText = @"请打开麦克风权限";
        } else {
            self.voiceAlertView.voiceText = @"网络繁忙，请取消重试";
        }
        [self.voiceAlertView setNeedsLayout];
    });
}


- (void)onRemoteMicroStateChanged:(NSString *)roomId openId:(NSString *)openId isMute:(BOOL)isMute {
    NSString *description = [NSString stringWithFormat:
                             @"onRemoteMicroStateChanged : \n [%@] \n roomId : %@ \n openIds : %@ \n isMute : %@",
                             [HWTools nowDate],
                             roomId,
                             openId,
                             isMute ? @"true" : @"false"];
    NSLog(@"%@",description);
    [self.listView addLog:description];
}

- (void)defaultSelfPosition {
    SelfPosition *selfPosition = [[SelfPosition alloc]init];
    selfPosition.position = [[PlayerPosition alloc] init];
    AxisModel *axisModel = [[AxisModel alloc] init];
    selfPosition.axis = [self convertToAxis:axisModel];
    [HWPGMEngine.getInstance updateSelfPosition:selfPosition];
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:CLEAR_SCREEN object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MUTE_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:FORBID_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:FORBID_ALL_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MUTE_ALL_OR_NOT object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:OWNER_TRANSFER object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:ENABLE_SPATIAL_AUDIO object:nil];
    [_roomIdList removeObserver:self forKeyPath:@"roomIdMArr"];
}

- (UIScrollView *)scrollView {
    if (!_scrollView) {
        _scrollView = [[UIScrollView alloc] init];
    }
    return _scrollView;
}

- (AudioEffectAlertView *)effectAlertView {
    if (!_effectAlertView) {
        _effectAlertView = [[AudioEffectAlertView alloc] init];
    }
    return _effectAlertView;
}
   
- (PlayerLocationCache *)locationCache {
    if (!_locationCache) {
        _locationCache = [[PlayerLocationCache alloc] init];
        SelfLocationModel *selfModel = [[SelfLocationModel alloc] init];
        selfModel.openId = HWPGMEngine.getInstance.engineParam.openId;
        _locationCache.selfLocationModel = selfModel;
        _locationCache.otherLocationModel = @[];
    }
    return _locationCache;
}
@end
 
