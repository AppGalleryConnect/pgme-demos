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

#import "UserPropertyView.h"
#import "ChannelSelectView.h"
#import "PropertyTableView.h"
#import "PropertySetView.h"
@interface UserPropertyView()<UITextFieldDelegate>
@property(nonatomic, strong) ChannelSelectView *selectView;
@property(nonatomic, strong) UITextField *userNameInput;
@property(nonatomic, strong) UIButton *searchBtn;
@property(nonatomic, strong) PropertyTableView *tableView;
@property(nonatomic, strong) PropertySetView *propertySetView;
@property(nonatomic, strong) NSDictionary *properties;
@end

@implementation UserPropertyView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.selectView];
    [self.selectView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.width.mas_equalTo(self);
        make.height.mas_equalTo(40);
    }];
    
    UIView *userView = [self userNameView];
    [self addSubview:userView];
    [userView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.selectView.mas_bottom);
        make.left.width.mas_equalTo(self);
        make.height.mas_equalTo(40);
    }];
    
    UIView *attributeView = [self attributeSetView];
    [self addSubview:attributeView];
    [attributeView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(userView.mas_bottom);
        make.left.width.height.mas_equalTo(userView);
    }];
    
    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_list"];
    [self addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(attributeView.mas_bottom);
        make.left.width.bottom.mas_equalTo(self);
    }];
    [self addSubview:self.tableView];
    [self.tableView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(attributeView.mas_bottom).offset(5);
        make.left.width.mas_equalTo(self);
        make.bottom.mas_equalTo(self).offset(-5);
    }];
}

- (UIView *)userNameView {
    UIView *view = [[UIView alloc] init];
    
    UILabel *label = [self commonLabel];
    label.text = @"用户名";
    [view addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
        make.width.mas_equalTo(130);
    }];
    [view addSubview:self.userNameInput];
    [self.userNameInput mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(label.mas_right);
        make.height.mas_equalTo(36);
        make.right.mas_equalTo(view).offset(-90);
        make.centerY.mas_equalTo(view);
    }];
    
    [view addSubview:self.searchBtn];
    [self.searchBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.centerY.mas_equalTo(view);
        make.size.mas_equalTo(CGSizeMake(80, 36));
    }];
    return view;
}

- (UIView *)attributeSetView {
    UIView *view = [[UIView alloc] init];
    UILabel *label = [[UILabel alloc] init];
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:15];
    label.text = @"用户自定义属性";
    [view addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
    }];
    
    UIButton *settingBtn = [self commonButtonWithTitle:@"设置" imageName:@"bt_switch_normal" sel:@selector(setPropertyFunc)];
    [view addSubview:settingBtn];
    [settingBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.centerY.mas_equalTo(view);
        make.size.mas_equalTo(CGSizeMake(80, 36));
    }];
    
    UIButton *deleteBtn = [self commonButtonWithTitle:@"全部删除" imageName:@"bt_cancel_normal" sel:@selector(deleteAllFunc)];
    [view addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(view);
        make.right.mas_equalTo(view).offset(-90);
        make.size.mas_equalTo(CGSizeMake(80, 36));
    }];
    return view;
}

/// 查询
- (void)searchFunc {
    if ([self.userNameInput isFirstResponder]) {
        [self.userNameInput resignFirstResponder];
    }
    if (!StringEmpty([self.selectView getSelectChannel])) {
        [HWTools showMessage:@"请选择频道"];
        return;
    }
    if (!StringEmpty(self.userNameInput.text)) {
        [HWTools showMessage:@"请选择输入用户名"];
        return;
    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(userPropertyView:queryPropertiesChannel:userId:)]) {
        [self.delegate userPropertyView:self queryPropertiesChannel:[self.selectView getSelectChannel] userId:self.userNameInput.text];
    }
}

/// 设置属性
- (void)setPropertyFunc {
    if (!StringEmpty([self.selectView getSelectChannel])) {
        [HWTools showMessage:@"请选择频道"];
        return;
    }
    [self.propertySetView showPropertyView];
    [self.propertySetView configProperties:self.properties];
}

/// 确认设置属性
- (void)confirmSetProperties:(NSDictionary <NSString *, NSString *> *)properties {
    if (self.delegate && [self.delegate respondsToSelector:@selector(userPropertyView:setChannelPropertiesChanel:channelProperties:)]) {
        [self.delegate userPropertyView:self setChannelPropertiesChanel:[self.selectView getSelectChannel] channelProperties:properties];
    }
}

/// 选中频道的回调
- (void)selectChannel:(NSString *)channel {
    if (self.delegate && [self.delegate respondsToSelector:@selector(userPropertyView:channel:openId:)]) {
        [self.delegate userPropertyView:self channel:channel openId:@""];
    }
}

/// 属性删除
- (void)deleteAttribute:(NSString *)key {
    if (self.delegate && [self.delegate respondsToSelector:@selector(userPropertyView:deleteChanel:keys:)]) {
        [self.delegate userPropertyView:self deleteChanel:[self.selectView getSelectChannel] keys:@[key]];
    }
}

/// 删除全部属性
- (void)deleteAllFunc {
    if (self.delegate && [self.delegate respondsToSelector:@selector(userPropertyView:deleteChanel:keys:)]) {
        [self.delegate userPropertyView:self deleteChanel:[self.selectView getSelectChannel] keys:self.properties.allKeys];
    }
}

- (void)configChannelsArray:(NSArray *)channelsArray {
    [self.selectView configChannelsArray:channelsArray];
}

- (void)configPlayerChannel:(NSString *)channel properties:(NSDictionary *)properties {
    if ([channel isEqualToString:[self.selectView getSelectChannel]]) {
        self.properties = properties;
        [self.tableView configProperties:properties];
    }
}

/// 移除弹框
- (void)removePropertySetView {
    if (self.propertySetView.superview) {
        [self.propertySetView removeFromSuperview];
    }
}

- (void)checkCurentChannelWithUnSubChannel:(NSString *)channel {
    if (!StringEmpty([self.selectView getSelectChannel])) {
        return;
    }
    [self.selectView unSubscribeChannel:channel];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (UILabel *)commonLabel {
    UILabel *label = [[UILabel alloc] init];
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:15];
    label.numberOfLines = 2;
    return label;
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

- (ChannelSelectView *)selectView {
    if (!_selectView) {
        _selectView = [[ChannelSelectView alloc] init];
        [_selectView configTitle:@"频道名"];
        __weak typeof(self) weakSelf = self;
        _selectView.selectBlock = ^(NSString * channel) {
            [weakSelf selectChannel:channel];
        };
    }
    return _selectView;
}

- (UITextField *)userNameInput {
    if (!_userNameInput) {
        _userNameInput = [[UITextField alloc] init];
        _userNameInput.textColor = [UIColor whiteColor];
        _userNameInput.background = [UIImage imageNamed:@"bg_input"];
        _userNameInput.font = [UIFont systemFontOfSize:14];
        _userNameInput.delegate = self;
    }
    return _userNameInput;
}

- (UIButton *)searchBtn {
    if (!_searchBtn) {
        _searchBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_searchBtn setTitle:@"查询" forState:UIControlStateNormal];
        [_searchBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        [_searchBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_searchBtn addTarget:self action:@selector(searchFunc) forControlEvents:UIControlEventTouchUpInside];
        _searchBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    }
    return _searchBtn;
}

- (PropertyTableView *)tableView {
    if (!_tableView) {
        _tableView = [[PropertyTableView alloc] init];
        __weak typeof(self) weakSelf = self;
        _tableView.deleteBlock = ^(NSString * key) {
            [weakSelf deleteAttribute:key];
        };
    }
    return _tableView;
}

- (PropertySetView *)propertySetView {
    if (!_propertySetView) {
        _propertySetView = [[PropertySetView alloc] init];
        __weak typeof(self) weakSelf = self;
        _propertySetView.confirmBlock = ^(NSDictionary <NSString *, NSString *> * properties) {
            [weakSelf confirmSetProperties:properties];
        };
    }
    return _propertySetView;
}
@end
