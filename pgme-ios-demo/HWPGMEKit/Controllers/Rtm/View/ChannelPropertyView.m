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

#import "ChannelPropertyView.h"
#import "ChannelSelectView.h"
#import "PropertyTableView.h"
#import "PropertySetView.h"
@interface ChannelPropertyView()
@property(nonatomic, strong) ChannelSelectView *selectView;
@property(nonatomic, strong) PropertyTableView *tableView;
@property(nonatomic, strong) PropertySetView *propertySetView;
@property(nonatomic, strong) NSMutableDictionary *channelProperties;
@end

@implementation ChannelPropertyView

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
    
    UIView *attributeView = [self attributeSetView];
    [self addSubview:attributeView];
    [attributeView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.selectView.mas_bottom);
        make.left.width.mas_equalTo(self);
        make.height.mas_equalTo(40);
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

- (UIView *)attributeSetView {
    UIView *view = [[UIView alloc] init];
    UILabel *label = [[UILabel alloc] init];
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:15];
    label.text = @"频道自定义属性";
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

/// 设置属性
- (void)setPropertyFunc {
    if (!StringEmpty([self.selectView getSelectChannel])) {
        [HWTools showMessage:@"请选择频道"];
        return;
    }
    [self.propertySetView showPropertyView];
    NSDictionary *temp = @{};
    if ([self.channelProperties.allKeys containsObject:[self.selectView getSelectChannel]]) {
        temp = [self.channelProperties objectForKey:[self.selectView getSelectChannel]];
    }
    [self.propertySetView configProperties:temp];
}

/// 确认设置属性
- (void)confirmSetProperties:(NSDictionary <NSString *, NSString *> *)properties {
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelPropertyView:setChannel:properties:)]) {
        [self.delegate channelPropertyView:self setChannel:[self.selectView getSelectChannel] properties:properties];
    }
}

/// 选中频道的回调
- (void)selectChannel:(NSString *)channel {
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelPropertyView:channel:)]) {
        [self.delegate channelPropertyView:self channel:channel];
    }
}

/// 属性删除
- (void)deleteAttribute:(NSString *)key {
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelPropertyView:deleteChannel:keys:)]) {
        [self.delegate channelPropertyView:self deleteChannel:[self.selectView getSelectChannel] keys:@[key]];
    }
}

/// 删除全部属性
- (void)deleteAllFunc {
    if (self.delegate && [self.delegate respondsToSelector:@selector(channelPropertyView:deleteChannel:keys:)]) {
        NSDictionary *temp = @{};
        if ([self.channelProperties.allKeys containsObject:[self.selectView getSelectChannel]]) {
            temp = [self.channelProperties objectForKey:[self.selectView getSelectChannel]];
        }
        [self.delegate channelPropertyView:self deleteChannel:[self.selectView getSelectChannel] keys:temp.allKeys];
    }
}

- (void)configChannelsArray:(NSArray *)channelsArray {
    [self.selectView configChannelsArray:channelsArray];
}
- (void)checkCurentChannelWithUnSubChannel:(NSString *)channel {
    if (!StringEmpty([self.selectView getSelectChannel])) {
        return;
    }
    [self.selectView unSubscribeChannel:channel];
}

- (void)configChannel:(NSString *)channel properties:(NSDictionary *)properties {
    [self.channelProperties setValue:properties forKey:channel];
    if ([channel isEqualToString:[self.selectView getSelectChannel]]) {
        [self.tableView configProperties:properties];
    }
}

/// 移除弹框
- (void)removePropertySetView {
    if (self.propertySetView.superview) {
        [self.propertySetView removeFromSuperview];
    }
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
        __weak typeof(self) weakSelf = self;
        [_selectView configTitle:@"频道名"];
        _selectView.selectBlock = ^(NSString * channel) {
            [weakSelf selectChannel:channel];
        };
    }
    return _selectView;
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

- (NSMutableDictionary *)channelProperties {
    if (!_channelProperties) {
        _channelProperties = [NSMutableDictionary dictionary];
    }
    return _channelProperties;
}
@end
