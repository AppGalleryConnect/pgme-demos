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
#import "PropertyCell.h"

@interface PropertyCell()
@property(nonatomic, strong) UILabel *keyLabel;
@property(nonatomic, strong) UILabel *valueLabel;
@property(nonatomic, strong) UIButton *deleteBtn;
@end

@implementation PropertyCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.backgroundColor = [UIColor clearColor];
    UIView *view = [[UIView alloc] init];
    [self.contentView addSubview:view];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.contentView).offset(5);
        make.top.height.mas_equalTo(self.contentView);
        make.right.mas_equalTo(self.contentView).offset(-85);
    }];
    [view addSubview:self.keyLabel];
    [self.keyLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
        make.width.mas_equalTo(view).multipliedBy(0.4);
    }];
    [view addSubview:self.valueLabel];
    [self.valueLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.keyLabel.mas_right).offset(5);
        make.centerY.right.mas_equalTo(view);
    }];
    
    [self.contentView addSubview:self.deleteBtn];
    [self.deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.contentView);
        make.size.mas_equalTo(CGSizeMake(80, 30));
        make.right.mas_equalTo(self.contentView).offset(-5);
    }];
    
    UIView *lineView = [[UIView alloc] init];
    lineView.backgroundColor = [UIColor hw_placeholderColor];
    [self.contentView addSubview:lineView];
    [lineView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.bottom.mas_equalTo(self.contentView);
        make.left.mas_equalTo(self.contentView).offset(2);
        make.right.mas_equalTo(self.contentView).offset(-2);
        make.height.mas_equalTo(1);
    }];
}

- (void)configData:(NSString *)key value:(NSString *)value {
    self.keyLabel.text = key;
    self.valueLabel.text = value;
}

- (void)deleteFunc {
    if (self.deleteBlock) {
        self.deleteBlock();
    }
}

- (UILabel *)commonLabel {
    UILabel *label = [[UILabel alloc] init];
    label.textColor = [UIColor whiteColor];
    label.font = [UIFont systemFontOfSize:12];
    label.numberOfLines = 2;
    return label;
}

- (UILabel *)keyLabel {
    if (!_keyLabel) {
        _keyLabel = [self commonLabel];
    }
    return _keyLabel;
}

- (UILabel *)valueLabel {
    if (!_valueLabel) {
        _valueLabel = [self commonLabel];
    }
    return _valueLabel;
}

- (UIButton *)deleteBtn {
    if (!_deleteBtn) {
        _deleteBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_deleteBtn setTitle:@"删除" forState:UIControlStateNormal];
        [_deleteBtn setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
        [_deleteBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [_deleteBtn addTarget:self action:@selector(deleteFunc) forControlEvents:UIControlEventTouchUpInside];
        _deleteBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    }
    return _deleteBtn;
}
@end
