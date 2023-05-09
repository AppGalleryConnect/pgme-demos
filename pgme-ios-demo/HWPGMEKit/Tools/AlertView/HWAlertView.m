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

#import "HWAlertView.h"
#import "HWTools.h"

@interface HWAlertOptionCell : UICollectionViewCell
/// 选项图标imageview
@property (nonatomic, strong) UIImageView *iconImageView;
/// 选项内容label
@property (nonatomic, strong) UILabel *titleLabel;
/// 选项内容
@property (nonatomic, copy) NSString *optionTitle;

@end

@implementation HWAlertOptionCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.iconImageView = [[UIImageView alloc] init];
        self.iconImageView.image = [UIImage imageNamed:@"ic_circle_normal"];
        [self addSubview:self.iconImageView];
        [self.iconImageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self);
            make.centerX.equalTo(self).offset(-30);
            make.size.mas_equalTo(CGSizeMake(24, 24));
        }];
        
        self.titleLabel = [[UILabel alloc] init];
        self.titleLabel.font = [UIFont systemFontOfSize:13];
        self.titleLabel.textColor = [UIColor hw_alertTextColor];
        [self addSubview:self.titleLabel];
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self);
            make.left.equalTo(self.iconImageView.mas_right).offset(6);
        }];
    }
    return self;
}

/// item被选中
/// @param selected 是否被选中
- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];
    NSString *iconName = selected ? @"ic_circle_selected" : @"ic_circle_normal";
    self.iconImageView.image = [UIImage imageNamed:iconName];
}
/// 设置选项内容
/// @param optionTitle 选项内容
- (void)setOptionTitle:(NSString *)optionTitle {
    _optionTitle = optionTitle;
    self.titleLabel.text = optionTitle;
}
@end

@interface HWAlertView ()<UICollectionViewDelegate,UICollectionViewDataSource,UICollectionViewDelegateFlowLayout>

@property (nonatomic, assign) BOOL isMustSelect;

/// 弹窗视图
@property (nonatomic, strong) UIView *alertView;

/// 背景图片
@property (nonatomic, strong) UIImageView *bgImageView;

/// 标题label
@property (nonatomic, strong) UILabel *titleLabel;

/// 选项列表
@property (nonatomic, strong) UICollectionView *collectionView;

/// 取消按钮
@property (nonatomic, strong) UIButton *cancelButton;

/// 确认按钮
@property (nonatomic, strong) UIButton *enterButton;

@end

@implementation HWAlertView

- (instancetype)init {
    self = [super init];
    if (self) {
        [self addSubview:self.alertView];
        [self.alertView addSubview:self.bgImageView];
        [self.bgImageView addSubview:self.titleLabel];
        [self.bgImageView addSubview:self.collectionView];
        [self.bgImageView addSubview:self.cancelButton];
        [self.bgImageView addSubview:self.enterButton];
        self.selectedIndex = NotSelected;
    }
    return self;
}

+ (HWAlertView *)alertWithTitle:(NSString *)title
                   isMustSelect:(BOOL)isMustSelect
                        options:(NSArray *)options
                          enter:(void (^)(NSInteger))enter {
    HWAlertView *alertView = [[HWAlertView alloc] init];
    alertView.title = title;
    alertView.optionsArray = options;
    alertView.enterButtonBlock = enter;
    alertView.isMustSelect = isMustSelect;
    [alertView show];
    return alertView;
}

- (void)updateUI {
    self.frame = CGRectMake(0, 0, SCREENWIDTH, SCREENHEIGHT);
    self.backgroundColor = [UIColor colorWithWhite:0.1 alpha:0.7];
    self.alpha = 0;
    
    self.alertView.frame = CGRectMake(0, 0, SCREENWIDTH * 0.88, 260);
    self.alertView.center = MainWindow.center;
    self.alertView.alpha = 0;
    
    self.bgImageView.frame = CGRectMake(0, 0, self.alertView.frame.size.width, self.alertView.frame.size.height);
    
    self.titleLabel.frame = CGRectMake(0, 6, self.bgImageView.frame.size.width, 35);
    self.collectionView.frame = CGRectMake(30, CGRectGetMaxY(self.titleLabel.frame) + 12, self.bgImageView.frame.size.width - 60, 120);
    
    CGFloat btn_y = CGRectGetMaxY(self.collectionView.frame) + 16;
    CGFloat btn_width = 115;
    CGFloat btn_height = 40;
    self.cancelButton.frame = CGRectMake(36, btn_y, btn_width, btn_height);
    self.enterButton.frame = CGRectMake(self.bgImageView.frame.size.width - 36 - btn_width, btn_y, btn_width, btn_height);
}

#pragma mark - set

- (void)setTitle:(NSString *)title {
    _title = title;
    _titleLabel.text = title;
}

- (void)setOptionsArray:(NSArray *)optionsArray {
    _optionsArray = optionsArray;
    _selectedIndex = [self.optionsArray containsObject:@"指挥家"] ? 0 : NotSelected;
}

- (void)setEnterButtonBlock:(void (^)(NSInteger))enterButtonBlock {
    _enterButtonBlock = enterButtonBlock;
}

- (void)setSelectedIndex:(NSInteger)selectedIndex {
    _selectedIndex = selectedIndex;
}

/// 弹出弹窗
- (void)show {
    [self updateUI];
    [MainWindow addSubview:self];
    __weak typeof(self) weakSelf = self;
    [UIView animateWithDuration:0.25 animations:^{
        weakSelf.alpha = 1;
        weakSelf.alertView.alpha = 1;
    }];
}

/// 销毁弹窗
- (void)dismiss {
    __weak typeof(self) weakSelf = self;
    [UIView animateWithDuration:0.25 animations:^{
        weakSelf.backgroundColor = [UIColor clearColor];
        weakSelf.alertView.alpha = 0;
    } completion:^(BOOL finished) {
        [weakSelf removeFromSuperview];
    }];
}

/// 点击背景移除弹窗
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    if (![touch.view isEqual:self.bgImageView]) {
        [self dismiss];
    }
}

#pragma mark - event
- (void)cancelButtonPressed {
    [self dismiss];
}

- (void)enterButtonPressed:(UIButton *)button {
    if (self.isMustSelect && self.selectedIndex == NotSelected) {
        [HWTools showMessage:@"请选择"];
        return;
    }
    if (self.enterButtonBlock) {
        self.enterButtonBlock(self.selectedIndex);
    }
    [self dismiss];
}

#pragma mark - UICollectionViewDelegate,UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.optionsArray.count;
}
- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    HWAlertOptionCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:NSStringFromClass([HWAlertOptionCell class]) forIndexPath:indexPath];
    cell.optionTitle = [self.optionsArray objectAtIndex:indexPath.item];
    if ([self.optionsArray containsObject:@"指挥家"] && indexPath.item == 0) {
        [collectionView selectItemAtIndexPath:indexPath animated:YES scrollPosition:(UICollectionViewScrollPositionNone)];
    }
    return cell;
}
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    self.selectedIndex = indexPath.item;
}

#pragma mark - UICollectionViewDelegateFlowLayout
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    if (self.optionsArray.count <= 2) {
        return CGSizeMake((self.collectionView.frame.size.width - 10), (self.collectionView.frame.size.height-20) / 2);
    }else {
        return CGSizeMake((self.collectionView.frame.size.width - 10) / 2, 38);
    }
}

- (UIView *)alertView {
    if (!_alertView) {
        _alertView = [[UIView alloc] init];
    }
    return _alertView;
}

- (UIImageView *)bgImageView {
    if (!_bgImageView) {
        _bgImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_list"]];
        _bgImageView.userInteractionEnabled = YES;
    }
    return _bgImageView;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.font = [UIFont systemFontOfSize:16 weight:UIFontWeightMedium];
        _titleLabel.textColor = [UIColor hw_alertTitleColor];
    }
    return _titleLabel;
}

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
        layout.minimumLineSpacing = 0;
        layout.minimumInteritemSpacing = 0;
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:layout];
        _collectionView.backgroundView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"bg_list"]];
        _collectionView.backgroundColor = [UIColor clearColor];
        _collectionView.contentInset = UIEdgeInsetsMake(10, 5, 10, 5);
        _collectionView.bounces = NO;
        _collectionView.dataSource = self;
        _collectionView.delegate = self;
        [_collectionView registerClass:[HWAlertOptionCell class] forCellWithReuseIdentifier:NSStringFromClass([HWAlertOptionCell class])];
    }
    return _collectionView;
}

- (UIButton *)cancelButton {
    if (!_cancelButton) {
        _cancelButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_cancelButton setTitle:@"取消" forState:UIControlStateNormal];
        [_cancelButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _cancelButton.titleLabel.font = [UIFont systemFontOfSize:16];
        _cancelButton.layer.cornerRadius = 6;
        [_cancelButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
        [_cancelButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_down"] forState:UIControlStateHighlighted];
        [_cancelButton addTarget:self action:@selector(cancelButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    }
    return _cancelButton;
}

- (UIButton *)enterButton {
    if (!_enterButton) {
        _enterButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_enterButton setTitle:@"确定" forState:UIControlStateNormal];
        [_enterButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _enterButton.titleLabel.font = [UIFont systemFontOfSize:16];
        _enterButton.layer.cornerRadius = 6;
        [_enterButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_enterButton setBackgroundImage:[UIImage imageNamed:@"bt_switch_down"] forState:UIControlStateHighlighted];
        [_enterButton addTarget:self action:@selector(enterButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _enterButton;
}

@end
