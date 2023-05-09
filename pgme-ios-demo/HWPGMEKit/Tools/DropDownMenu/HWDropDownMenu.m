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

#import "HWDropDownMenu.h"

@interface HWDropDownMenu ()<UITableViewDataSource, UITableViewDelegate>

@end

@implementation HWDropDownMenu

- (instancetype)init {
    self = [super init];
    if (self) {
        _cornerRadius = 6.0;
        _showMaskView = YES;
        _itemHeight = 50;
        [self addSubview:self.tableView];
    }
    return self;
}

+ (HWDropDownMenu *)showOnView:(UIView *)view titles:(NSArray *)titles delegate:(id<HWDropDownMenuDelegate>)delegate otherSettings:(void (^)(HWDropDownMenu *dropDownMenu))otherSettings {
    HWDropDownMenu *dropDownMenu = [[HWDropDownMenu alloc] init];
    dropDownMenu.delegate = delegate;
    dropDownMenu.titles = titles;
    dropDownMenu.viewRect = [view convertRect:view.bounds toView:MainWindow];
    if (otherSettings) otherSettings(dropDownMenu);
    [dropDownMenu show];
    return dropDownMenu;
}

- (void)updateUI {
    self.frame = CGRectMake(0, 0, SCREENWIDTH, SCREENHEIGHT);
    self.tableView.frame = CGRectMake(_viewRect.origin.x , _viewRect.origin.y + _viewRect.size.height, _viewRect.size.width, 0);
    self.tableView.layer.cornerRadius = _cornerRadius;
    if (_bgImage) {
        self.tableView.backgroundColor = [UIColor clearColor];
        self.tableView.backgroundView = [[UIImageView alloc] initWithImage:_bgImage];
    }
}

#pragma mark - set
- (void)setTitles:(NSArray *)titles {
    _titles = titles;
}

- (void)setViewRect:(CGRect)viewRect {
    _viewRect = viewRect;
}

- (void)setCornerRadius:(CGFloat)cornerRadius {
    _cornerRadius = cornerRadius;
}

- (void)setShowMaskView:(BOOL)showMaskView {
    _showMaskView = showMaskView;
}

- (void)setItemHeight:(CGFloat)itemHeight {
    _itemHeight = itemHeight;
}

- (void)setBgImage:(UIImage *)bgImage {
    _bgImage = bgImage;
}

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.dataSource = self;
        _tableView.delegate = self;
        _tableView.layer.cornerRadius = _cornerRadius;
        _tableView.backgroundColor = [UIColor whiteColor];
        _tableView.bounces = NO;
    }
    return _tableView;
}

#pragma mark - UITableViewDataSource UITableViewDelegate

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.titles.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return _itemHeight;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *DropDownMenuCell = @"DropDownMenuCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:DropDownMenuCell];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:DropDownMenuCell];
    }
    cell.textLabel.textColor = [UIColor hw_textColor];
    cell.textLabel.font = [UIFont systemFontOfSize:15];
    cell.textLabel.text = [self.titles objectAtIndex:indexPath.row];
    cell.separatorInset = UIEdgeInsetsMake(0, 0, 0, 0);
    if (_bgImage) {
        cell.backgroundColor = [UIColor clearColor];
        cell.backgroundView = [[UIImageView alloc] initWithImage:_bgImage];
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.delegate && [self.delegate respondsToSelector:@selector(HWDropDownMenu:didSelectRowAtIndexPath:)]) {
        [self.delegate HWDropDownMenu:self didSelectRowAtIndexPath:indexPath];
    }
    [self dismiss];
}

/// 弹出下拉菜单view
- (void)show {
    [self updateUI];
    [MainWindow addSubview:self];
    __weak typeof(self) weakSelf = self;
    [UIView animateWithDuration:0.2 animations:^{
        weakSelf.backgroundColor = weakSelf.showMaskView ? [UIColor colorWithRed:0.25 green:0.25 blue:0.25 alpha:0.3] : [UIColor clearColor];
        weakSelf.tableView.frame = CGRectMake(weakSelf.viewRect.origin.x, weakSelf.viewRect.origin.y + weakSelf.viewRect.size.height, weakSelf.viewRect.size.width, weakSelf.titles.count * weakSelf.itemHeight);
    }];
}

/// 移除下拉菜单view
- (void)dismiss {
    __weak typeof(self) weakSelf = self;
    [UIView animateWithDuration:0.2 animations:^{
        weakSelf.backgroundColor = [UIColor clearColor];
        weakSelf.tableView.frame = CGRectMake(weakSelf.viewRect.origin.x, weakSelf.viewRect.origin.y + weakSelf.viewRect.size.height, weakSelf.viewRect.size.width, 0);
    } completion:^(BOOL finished) {
        [weakSelf removeFromSuperview];
    }];
}

/// 点击背景移除菜单
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    UITouch *touch = [touches anyObject];
    if (![touch.view isEqual:self.tableView]) {
        [self dismiss];
    }
}

@end
