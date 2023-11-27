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

#import "PropertySetView.h"
#import "PropertySetCell.h"
@interface PropertySetView()<UITableViewDataSource>
/// 弹框视图
@property(nonatomic, strong) UIView *alertView;
/// 背景图片
@property(nonatomic, strong) UIImageView *bgImageView;
@property(nonatomic, strong) UITableView *tableView;
@property(nonatomic, strong) UIButton *addBtn;
@property(nonatomic, strong) NSMutableArray *dataArray;
@end

@implementation PropertySetView
static NSString *PropertySetCellID = @"PropertySetCell";
static NSString *PropertyKey = @"PropertyKey";
static NSString *PropertyValueKey = @"PropertyValueKey";
- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.alertView];
    [self.alertView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.mas_equalTo(self);
        make.width.mas_equalTo(self).multipliedBy(0.9);
        make.height.mas_equalTo(465);
    }];
    [self.alertView addSubview:self.bgImageView];
    [self.bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(self.alertView);
    }];
    
    [self.alertView addSubview:self.tableView];
    [self.tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(self.alertView);
        make.height.mas_equalTo(400);
        make.top.mas_equalTo(self.alertView).offset(5);
    }];
    
    UIView *btnView = [[UIView alloc] init];
    [self.alertView addSubview:btnView];
    [btnView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.tableView.mas_bottom).offset(10);
        make.left.width.mas_equalTo(self.alertView);
        make.height.mas_equalTo(40);
    }];
    self.addBtn = [self commonButtonWithTitle:@"新增" imageName:@"bt_switch_normal" sel:@selector(addFunc)];
    UIButton *confirmBtn = [self commonButtonWithTitle:@"确定" imageName:@"bt_switch_normal" sel:@selector(confirmFunc)];
    UIButton *cancelBtn = [self commonButtonWithTitle:@"取消" imageName:@"bt_cancel_normal" sel:@selector(cancelFunc)];
    [btnView addSubview:self.addBtn];
    [btnView addSubview:confirmBtn];
    [btnView addSubview:cancelBtn];
    
    NSArray *viewArray = @[self.addBtn, confirmBtn, cancelBtn];
    [viewArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:20 leadSpacing:10 tailSpacing:10];
    [viewArray mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(40);
        make.centerY.mas_equalTo(btnView);
    }];
    [self addFunc];
}

/// 新增
- (void)addFunc {
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setValue:@"" forKey:PropertyKey];
    [dict setValue:@"" forKey:PropertyValueKey];
    [self.dataArray addObject:dict];
    [self.tableView reloadData];
    [self addBtnVisible];
}

/// 确定
- (void)confirmFunc {
//    __block BOOL contentEmpty = NO;
//    [self.dataArray enumerateObjectsUsingBlock:^(NSMutableDictionary *  _Nonnull dict, NSUInteger idx, BOOL * _Nonnull stop) {
//        if (!StringEmpty([dict objectForKey:PropertyKey]) || !StringEmpty([dict objectForKey:PropertyValueKey])) {
//            contentEmpty = YES;
//            *stop = YES;
//        }
//    }];
//    if (contentEmpty) {
//        [HWTools showMessage:@"请把内容输入完整"];
//        return;
//    }
    NSMutableDictionary *mutDict = [NSMutableDictionary dictionary];
    for (NSDictionary *dict in self.dataArray) {
        [mutDict setValue:[dict objectForKey:PropertyValueKey] forKey:[dict objectForKey:PropertyKey]];
    }
    if (self.confirmBlock) {
        self.confirmBlock([mutDict copy]);
    }
    [self.dataArray removeAllObjects];
    [self dismiss];
}

/// 取消
- (void)cancelFunc {
    [self.dataArray removeAllObjects];
    [self dismiss];
}

/// 删除数据
- (void)deleteDataWithIndex:(NSInteger)index {
//    if (self.dataArray.count == 1) {
//        [HWTools showMessage:@"至少保留一条数据"];
//        return;
//    }
    [self.dataArray removeObjectAtIndex:index];
    [self.tableView reloadData];
    [self addBtnVisible];
}

/// 更新key
- (void)updateKey:(NSString *)key index:(NSInteger)index {
    if (index < self.dataArray.count) {
        NSMutableDictionary *dict = [self.dataArray objectAtIndex:index];
        [dict setValue:key forKey:PropertyKey];
    }
}

/// 更新value
- (void)updateValue:(NSString *)value index:(NSInteger)index {
    if (index < self.dataArray.count) {
        NSMutableDictionary *dict = [self.dataArray objectAtIndex:index];
        [dict setValue:value forKey:PropertyValueKey];
    }
}

- (void)addBtnVisible {
    self.addBtn.hidden = self.dataArray.count >= 5;
}

/// 点击背景
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [self endEditing:YES];
}

- (void)configProperties:(NSDictionary<NSString *,NSString *> *)properties {
    [self.dataArray removeAllObjects];
    if (properties.allKeys.count == 0) {
        [self addFunc];
        return;
    }
    for (NSString *key in properties.allKeys) {
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        [dict setValue:key forKey:PropertyKey];
        [dict setValue:[properties objectForKey:key] forKey:PropertyValueKey];
        [self.dataArray addObject:dict];
    }
    [self.tableView reloadData];
    [self addBtnVisible];
}

/// 引擎销毁
- (void)alertViewDestory {
    if (self.superview) {
        [self removeFromSuperview];
    }
}

- (void)showPropertyView {
    self.frame = CGRectMake(0, 0, SCREENWIDTH, SCREENHEIGHT);
    [[UIApplication sharedApplication].keyWindow addSubview:self];
    self.backgroundColor = [UIColor colorWithWhite:0.1 alpha:0.7];
    [UIView animateWithDuration:0.25 animations:^{
        self.alertView.alpha = 1;
    }];
}

- (void)dismiss {
    [UIView animateWithDuration:0.25 animations:^{
        self.backgroundColor = [UIColor clearColor];
        self.alertView.alpha = 0;
    }                completion:^(BOOL finished) {
        [self removeFromSuperview];
    }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PropertySetCell *cell = [tableView dequeueReusableCellWithIdentifier:PropertySetCellID];
    NSDictionary *dict = [self.dataArray objectAtIndex:indexPath.row];
    [cell configData:[dict objectForKey:PropertyKey] value:[dict objectForKey:PropertyValueKey]];
    __weak typeof(self) weakSelf = self;
    cell.deleteBlock = ^{
        [weakSelf deleteDataWithIndex:indexPath.row];
    };
    cell.keyEndEditBlock = ^(NSString * key) {
        [weakSelf updateKey:key index:indexPath.row];
    };
    cell.valueEndEditBlock = ^(NSString * value) {
        [weakSelf updateValue:value index:indexPath.row];
    };
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

- (UIButton *)commonButtonWithTitle:(NSString *)title imageName:(NSString *)imageName sel:(SEL)sel {
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setTitle:title forState:UIControlStateNormal];
    [btn setBackgroundImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [btn addTarget:self action:sel forControlEvents:UIControlEventTouchUpInside];
    btn.titleLabel.font = [UIFont systemFontOfSize:14];
    return btn;
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

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.dataSource = self;
        _tableView.rowHeight = 80;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.backgroundColor = [UIColor clearColor];
        [_tableView registerClass:[PropertySetCell class] forCellReuseIdentifier:PropertySetCellID];
    }
    return _tableView;
}

- (NSMutableArray *)dataArray {
    if (!_dataArray) {
        _dataArray = [NSMutableArray array];
    }
    return _dataArray;
}
@end
