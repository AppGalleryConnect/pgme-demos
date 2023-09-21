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

#import "PlayerLocationViewController.h"
#import "SelfLocationView.h"
#import "PlayerLocationCell.h"
#import "LocationAlertView.h"

@interface PlayerLocationViewController () <UITableViewDataSource, UITableViewDelegate>
/// 当前玩家位置
@property(nonatomic, strong) SelfLocationView *selfLocationView;
@property(nonatomic, strong) UITableView *tableView;
/// 添加玩家弹框
@property(nonatomic, strong) LocationAlertView *addPlayerAlertView;
/// 设置语音接受范围弹框
@property(nonatomic, strong) LocationAlertView *setRangeAlertView;
/// 当前玩家位置信息
@property(nonatomic, strong) SelfLocationModel *selfLocationModel;
/// 其他玩家位置信息
@property(nonatomic, strong) NSMutableArray<OtherLocationModel *> *otherLocationModels;
/// 语音接受范围
@property(nonatomic, assign) NSInteger audioRecvRange;
@end

@implementation PlayerLocationViewController
static NSString *const PLAYER_LOCATION_CELL_ID = @"PlayerLocationCellCellId";

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"玩家位置管理";
    [self setupViews];
    [self.selfLocationView configData:self.selfLocationModel];
    [self.tableView reloadData];
}

- (void)setupViews {
    [self backButton];
    UIImageView *bgImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_list"]];
    [self.view addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsZero);
    }];

    [self.view addSubview:self.selfLocationView];
    [self.selfLocationView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(self.view);
        make.top.mas_equalTo(self.view).offset(kStatusBarHeight + kNavBarHeight);
        make.height.mas_equalTo(280);
    }];

    UIView *btnView = [self buttonView];
    [self.view addSubview:btnView];
    [btnView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(self.view);
        make.top.mas_equalTo(self.selfLocationView.mas_bottom);
        make.height.mas_equalTo(62);
    }];

    [self.view addSubview:self.tableView];
    [self.tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(btnView.mas_bottom);
        make.left.width.mas_equalTo(self.view);
        make.bottom.mas_equalTo(self.view).offset(-kGetSafeAreaBottomHeight);
    }];
}

#pragma mark UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.otherLocationModels.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PlayerLocationCell *cell = [tableView dequeueReusableCellWithIdentifier:PLAYER_LOCATION_CELL_ID];
    __weak typeof(self) weakself = self;
    OtherLocationModel *model = [self.otherLocationModels objectAtIndex:indexPath.row];
    [cell configCellData:model];
    cell.updateLocationBlock = ^(OtherLocationModel *_Nonnull location) {
        [weakself updateOtherPlayersLocation:location];
    };
    cell.deleteBlock = ^{
        OtherLocationModel *model = [weakself.otherLocationModels objectAtIndex:indexPath.row];
        [weakself.otherLocationModels removeObjectAtIndex:indexPath.row];
        [weakself.tableView reloadData];
        if (weakself.updateOthersLocationBlock) {
            weakself.updateOthersLocationBlock([weakself.otherLocationModels copy]);
        }
        [HWPGMEngine.getInstance clearRemotePlayerPosition:model.openId];
    };
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (void)backButton {
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [backButton setTitle:@"返回" forState:UIControlStateNormal];
    [backButton setTitleColor:[UIColor colorWithString:@"#666666"] forState:UIControlStateNormal];
    [backButton addTarget:self action:@selector(backFunc) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
}

- (UIView *)buttonView {
    UIView *buttonView = [[UIView alloc] init];

    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_input"];
    bgImageView.userInteractionEnabled = YES;
    [buttonView addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(0, 5, 0, 5));
    }];
    UIButton *createButton = [self commonButtonWithTitle:@"新增玩家" imageName:@"bt_switch_normal" action:@selector(createLocationFunc)];
    UIButton *deleteButton = [self commonButtonWithTitle:@"清空所有玩家" imageName:@"bt_cancel_normal" action:@selector(clearAllLocationFunc)];
    UIButton *rangeButton = [self commonButtonWithTitle:@"设置范围" imageName:@"bt_switch_normal" action:@selector(setRangeFunc)];

    [bgImageView addSubview:createButton];
    [bgImageView addSubview:deleteButton];
    [bgImageView addSubview:rangeButton];

    NSArray *buttonArray = [NSArray arrayWithObjects:createButton, deleteButton, rangeButton, nil];
    [buttonArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:5 leadSpacing:5 tailSpacing:10];
    [buttonArray mas_updateConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@42);
        make.centerY.mas_equalTo(bgImageView);
    }];
    return buttonView;
}

- (UIButton *)commonButtonWithTitle:(NSString *)title imageName:(NSString *)imageName action:(SEL)action {
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    [button setTitle:title forState:UIControlStateNormal];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    button.titleLabel.font = [UIFont systemFontOfSize:13];
    [button setBackgroundImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
    button.layer.cornerRadius = 6;
    [button addTarget:self action:action forControlEvents:UIControlEventTouchUpInside];
    return button;
}

#pragma mark func

- (void)configDataSelfModel:(SelfLocationModel *)selfModel othersModel:(NSArray *)othersModel audioRecvRange:(NSInteger)audioRecvRange {
    self.audioRecvRange = audioRecvRange;
    self.selfLocationModel = selfModel;
    [self.otherLocationModels removeAllObjects];
    [self.otherLocationModels addObjectsFromArray:othersModel];
}

- (void)backFunc {
    [self.navigationController popViewControllerAnimated:YES];
}

/// 新增玩家位置信息
- (void)createLocationFunc {
    [self.view addSubview:self.addPlayerAlertView];
    [self.addPlayerAlertView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.width.height.mas_equalTo(self.view);
    }];
}

/// 清空所有玩家位置信息
- (void)clearAllLocationFunc {
    [self.otherLocationModels removeAllObjects];
    [self.tableView reloadData];
    if (self.updateOthersLocationBlock) {
        self.updateOthersLocationBlock(@[]);
        [HWPGMEngine.getInstance clearAllRemotePositions];
    }
}

/// 设置语音接受范围
- (void)setRangeFunc {
    [self.view addSubview:self.setRangeAlertView];
    self.setRangeAlertView.defautValue = [NSString stringWithFormat:@"%ld", self.audioRecvRange];
    [self.setRangeAlertView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.width.height.mas_equalTo(self.view);
    }];
}

/// 确认添加玩家
- (void)confirmAddPlayer:(NSString *)playerId {
    if (!StringEmpty(playerId)) {
        [HWTools showMessage:@"玩家ID不能为空"];
        return;
    }
    if ([playerId isEqualToString:HWPGMEngine.getInstance.engineParam.openId]) {
        [HWTools showMessage:@"本人不需要添加"];
        return;
    }
    __block BOOL playerExist = NO;
    [self.otherLocationModels enumerateObjectsUsingBlock:^(OtherLocationModel *_Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
        if ([obj.openId isEqualToString:playerId]) {
            playerExist = YES;
            *stop = YES;
        }
    }];
    if (playerExist) {
        [HWTools showMessage:@"该玩家已存在"];
        return;
    }
    OtherLocationModel *model = [[OtherLocationModel alloc] init];
    model.openId = playerId;
    model.position = [[PositionModel alloc] init];
    [self.otherLocationModels addObject:model];
    [self.tableView reloadData];
    [self.addPlayerAlertView closeAlertView];
    if (self.otherLocationModels.count > 0) {
        NSIndexPath *indexpath = [NSIndexPath indexPathForRow:self.otherLocationModels.count - 1 inSection:0];
        [self.tableView scrollToRowAtIndexPath:indexpath atScrollPosition:UITableViewScrollPositionBottom animated:NO];
    }
    if (self.updateOthersLocationBlock) {
        self.updateOthersLocationBlock([self.otherLocationModels copy]);
    }
}

/// 设置语音接受范围
- (void)confirmSetRangeFunc:(NSString *)range {
    NSInteger intRange = 0;
    if (StringEmpty(range)) {
        intRange = [range integerValue];
    }
    if (intRange > INT_MAX) {
        [HWTools showMessage:[NSString stringWithFormat:@"设置失败，最大值应小于等于%d",INT_MAX]];
        return;
    }
    self.audioRecvRange = intRange;
    if (self.updateAudioRecvRangeBlock) {
        self.updateAudioRecvRangeBlock(intRange);
    }
    [self.setRangeAlertView closeAlertView];
}

/// 更新其他玩家位置信息
- (void)updateOtherPlayersLocation:(OtherLocationModel *)model {
    [self.otherLocationModels enumerateObjectsUsingBlock:^(OtherLocationModel *_Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
        if ([obj.openId isEqualToString:model.openId]) {
            obj.position = model.position;
            *stop = YES;
        }
    }];
    if (self.updateOthersLocationBlock) {
        self.updateOthersLocationBlock([self.otherLocationModels copy]);
    }
}

#pragma mark lazy

- (SelfLocationView *)selfLocationView {
    if (!_selfLocationView) {
        _selfLocationView = [[SelfLocationView alloc] init];
        __weak typeof(self) weakSelf = self;
        _selfLocationView.updateLocationBlock = ^(SelfLocationModel *_Nonnull model) {
            if (weakSelf.updateSelfLocationBlock) {
                weakSelf.updateSelfLocationBlock(model);
            }
            weakSelf.selfLocationModel = model;
        };
    }
    return _selfLocationView;
}

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        [_tableView registerClass:[PlayerLocationCell class] forCellReuseIdentifier:PLAYER_LOCATION_CELL_ID];
        _tableView.rowHeight = 160;
        _tableView.dataSource = self;
        _tableView.backgroundColor = [UIColor clearColor];
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag;
    }
    return _tableView;
}

- (NSMutableArray<OtherLocationModel *> *)otherLocationModels {
    if (!_otherLocationModels) {
        _otherLocationModels = [NSMutableArray array];
    }
    return _otherLocationModels;
}

- (SelfLocationModel *)selfLocationModel {
    if (!_selfLocationModel) {
        _selfLocationModel = [[SelfLocationModel alloc] init];
        _selfLocationModel.openId = HWPGMEngine.getInstance.engineParam.openId;
        PositionModel *position = [[PositionModel alloc] init];
        AxisModel *axis = [[AxisModel alloc] init];
        _selfLocationModel.position = position;
        _selfLocationModel.axis = axis;
    }
    return _selfLocationModel;
}

- (LocationAlertView *)addPlayerAlertView {
    if (!_addPlayerAlertView) {
        _addPlayerAlertView = [[LocationAlertView alloc] init];
        _addPlayerAlertView.type = LocationAlertAddPlayer;
        __weak typeof(self) weakSelf = self;
        _addPlayerAlertView.confirmBlock = ^(NSString *_Nonnull value) {
            [weakSelf confirmAddPlayer:value];
        };
    }
    return _addPlayerAlertView;
}

- (LocationAlertView *)setRangeAlertView {
    if (!_setRangeAlertView) {
        _setRangeAlertView = [[LocationAlertView alloc] init];
        _setRangeAlertView.type = LocationAlertAudioRecRange;
        __weak typeof(self) weakSelf = self;
        _setRangeAlertView.confirmBlock = ^(NSString *_Nonnull value) {
            [weakSelf confirmSetRangeFunc:value];
        };
    }
    return _setRangeAlertView;
}
@end
