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

#import "PropertyTableView.h"
#import "PropertyCell.h"
@interface PropertyTableView()<UITableViewDataSource, UITableViewDelegate>
@property(nonatomic, strong) UITableView *tableView;
@property(nonatomic, strong) NSMutableDictionary *proDict;
@end

@implementation PropertyTableView
static NSString *PropertyCellID = @"PropertyCell";
- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.backgroundColor = [UIColor clearColor];
    [self addSubview:self.tableView];
    [self.tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.height.width.mas_equalTo(self);
    }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.proDict.allKeys.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    PropertyCell *cell = [tableView dequeueReusableCellWithIdentifier:PropertyCellID];
    NSArray *keys = self.proDict.allKeys;
    NSString *key = [self.proDict.allKeys objectAtIndex:indexPath.row];
    [cell configData:key value:[self.proDict objectForKey:key]];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    __weak typeof(self) weakSelf = self;
    cell.deleteBlock = ^{
        if (weakSelf.deleteBlock) {
            weakSelf.deleteBlock(key);
        }
    };
    return cell;
}

- (void)configProperties:(NSDictionary *)properties {
    [self.proDict removeAllObjects];
    [self.proDict addEntriesFromDictionary:properties];
    [self.tableView reloadData];
}

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.dataSource = self;
        _tableView.delegate = self;
        _tableView.rowHeight = 45;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.backgroundColor = [UIColor clearColor];
        [_tableView registerClass:[PropertyCell class] forCellReuseIdentifier:PropertyCellID];
    }
    return _tableView;
}

- (NSMutableDictionary *)proDict {
    if (!_proDict) {
        _proDict = [NSMutableDictionary dictionary];
    }
    return _proDict;
}
@end
