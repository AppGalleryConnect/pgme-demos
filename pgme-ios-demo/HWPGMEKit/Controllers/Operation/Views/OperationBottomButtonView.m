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

#import "OperationBottomButtonView.h"
#import "OperationBottomCollectionViewCell.h"

@interface OperationBottomButtonView ()<UICollectionViewDelegate,UICollectionViewDataSource>

/// collectionView
@property (nonatomic, strong) UICollectionView *collectionView;

/// 数据
@property (nonatomic, strong) NSArray *dataArr;

@end

@implementation OperationBottomButtonView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupViews];
        [self initData];
        [self.collectionView reloadData];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.collectionView];
    [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self).inset(20);
        make.top.equalTo(self).inset(8);
        make.bottom.equalTo(self).inset(kGetSafeAreaBottomHeight + 8);
    }];
}

- (void)initData {
    self.dataArr = @[@{NORMALTITLE : @"查看日志",
                       SELECTEDTITLE : @"房间成员",
                       NORMALIMAGE : @"bt_switch_normal",
                       LIGHTEDIMAGE : @"bt_switch_down",
                       TYPE : @(BottomButtonType_Switch),
                     },
                     @{NORMALTITLE : @"引擎销毁",
                       NORMALIMAGE : @"bt_cancel_normal",
                       LIGHTEDIMAGE : @"bt_cancel_down",
                       TYPE : @(BottomButtonType_Destory),
                     },
                     @{NORMALTITLE : @"语音消息",
                       NORMALIMAGE : @"bt_switch_normal",
                       LIGHTEDIMAGE : @"bt_switch_normal",
                       TYPE : @(BottomButtonType_AudioMsg),
                     },
                     @{NORMALTITLE : @"媒体音效",
                       NORMALIMAGE : @"bt_switch_normal",
                       LIGHTEDIMAGE : @"bt_switch_normal",
                       TYPE : @(BottomButtonType_AduioEffect),
                     },
                     @{NORMALTITLE : @"玩家位置",
                       NORMALIMAGE : @"bt_switch_normal",
                       LIGHTEDIMAGE : @"bt_switch_normal",
                       TYPE : @(BottomButtonType_PlayerPosition),
                     },
    ];
}

- (void)switchButtonPressed:(UIButton *)button {
    button.selected ^= 1;
    if (self.delegate && [self.delegate respondsToSelector:@selector(switchButtonPressed:switchButton:)]) {
        [self.delegate switchButtonPressed:self switchButton:button];
    }
}

- (void)destoryButtonPressed:(UIButton *)button {
    if (self.delegate && [self.delegate respondsToSelector:@selector(destoryButtonPressed:destoryButton:)]) {
        [self.delegate destoryButtonPressed:self destoryButton:button];
    }
}

- (void)audioMsgButtonPressed:(UIButton *)button {
    if(self.delegate && [self.delegate respondsToSelector:@selector(audioMsgButtonPressed:audioMsgButton:)]){
        [self.delegate audioMsgButtonPressed:self audioMsgButton:button];
    }
}

- (void)audioEffectButtonPressed:(UIButton *)button {
    if(self.delegate && [self.delegate respondsToSelector:@selector(audioEffectButtonPressed:audioEffectButton:)]){
        [self.delegate audioEffectButtonPressed:self audioEffectButton:button];
    }
}

- (void)playerPositionButtonPressed:(UIButton *)button {
    if(self.delegate && [self.delegate respondsToSelector:@selector(playerPositionButtonPressed:playerPositionButton:)]){
        [self.delegate playerPositionButtonPressed:self playerPositionButton:button];
    }
}

- (void)buttonnPressed:(UIButton *)button type:(BottomButtonType)type {
    switch (type) {
        case BottomButtonType_Switch:
            [self switchButtonPressed:button];
            break;
        case BottomButtonType_Destory:
            [self destoryButtonPressed:button];
            break;
        case BottomButtonType_AudioMsg:
            [self audioMsgButtonPressed:button];
            break;
        case BottomButtonType_AduioEffect:
            [self audioEffectButtonPressed:button];
            break;
        case BottomButtonType_PlayerPosition:
            [self playerPositionButtonPressed:button];
            break;
        default:
            break;
    }
}

#pragma mark  设置CollectionView的组数

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

#pragma mark  设置CollectionView每组所包含的个数

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.self.dataArr.count;
}

#pragma mark  设置CollectionCell的内容

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSString *identify = NSStringFromClass([OperationBottomCollectionViewCell class]);
    OperationBottomCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:identify forIndexPath:indexPath];
    cell.dataDic = self.dataArr[indexPath.row];
    __weak typeof(self) weakSelf = self;
    cell.touchBlock = ^(UIButton * _Nonnull btn, BottomButtonType type) {
        [weakSelf buttonnPressed:btn type:type];
    };
    return cell;
}

#pragma mark  定义每个UICollectionView的大小

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return  CGSizeMake(CGRectGetWidth(self.bounds) / 3 - 30, 44);
}

#pragma mark  定义整个CollectionViewCell与整个View的间距

- (UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section {
    return UIEdgeInsetsMake(0, 10, 0, 10);
}

- (CGFloat)operationBottomViewHeight {
    return (self.dataArr.count / 3 + (self.dataArr.count % 3 ? 1 : 0)) * 64 + kGetSafeAreaBottomHeight + 16;
}

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.minimumLineSpacing = 10;
        flowLayout.minimumInteritemSpacing = 0;
        [flowLayout setScrollDirection:UICollectionViewScrollDirectionVertical];
        _collectionView = [[UICollectionView alloc] initWithFrame:self.bounds collectionViewLayout:flowLayout];
        _collectionView.backgroundColor = [UIColor clearColor];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        _collectionView.bounces = NO;
        _collectionView.scrollEnabled = NO;
        [_collectionView registerClass:[OperationBottomCollectionViewCell class] forCellWithReuseIdentifier:NSStringFromClass([OperationBottomCollectionViewCell class])];
    }
    return _collectionView;
}

@end
