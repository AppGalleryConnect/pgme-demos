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

#import "RoomLogViewController.h"
#import "Constants.h"

static NSString *const RoomLogTableViewCellID = @"RoomLogTableViewCellID";

@interface RoomLogViewController ()<UITableViewDataSource,UITableViewDelegate>

/// 日志列表
@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, strong) NSMutableArray *logMArr;

@end

@implementation RoomLogViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupViews];
}

- (void)setupViews {
    self.tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
    self.view = self.tableView;
    self.tableView.estimatedRowHeight = 100;
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.bounces = NO;
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    if (@available(iOS 15.0, *)) {
        self.tableView.sectionHeaderTopPadding = 0;
    }
    self.tableView.backgroundColor = [UIColor clearColor];
    self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

#pragma mark - event
- (void)clearButtonPressed:(UIButton *)button {
    [[NSNotificationCenter defaultCenter] postNotificationName:CLEAR_SCREEN object:nil];
}

#pragma mark - func

- (void)addLogMessage:(NSString *)msg {
    [self.logMArr addObject:msg];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}

- (void)clearAllLogs {
    [self.logMArr removeAllObjects];
    [self.tableView reloadData];
}

#pragma mark - UITableViewDataSource UITableViewDelegate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.logMArr.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:RoomLogTableViewCellID];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:RoomLogTableViewCellID];
    }
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.backgroundColor = [UIColor clearColor];
    cell.textLabel.numberOfLines = 0;
    cell.separatorInset = UIEdgeInsetsMake(0, 15, 0, 15);
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    cell.textLabel.text = self.logMArr[indexPath.row];
    cell.textLabel.textColor = [UIColor hw_titleColor];
    return cell;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
    // 添加清屏按钮
    UIButton *clearButton = [UIButton buttonWithType:UIButtonTypeCustom];
    clearButton.frame = CGRectMake(15, 10, 60, 35);
    [clearButton setTitle:@"清屏" forState:UIControlStateNormal];
    [clearButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    clearButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [clearButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
    [clearButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_down"] forState:UIControlStateHighlighted];
    [clearButton addTarget:self action:@selector(clearButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
    
    UIImageView *bg = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 55)];
    bg.image = [UIImage imageNamed:@"bg_cell"];
    
    UIView *headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, 55)];
    [headerView addSubview:bg];
    [headerView addSubview:clearButton];
    return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 55;
}

- (NSMutableArray *)logMArr {
    if (!_logMArr) {
        _logMArr = [NSMutableArray array];
    }
    return _logMArr;
}

@end
