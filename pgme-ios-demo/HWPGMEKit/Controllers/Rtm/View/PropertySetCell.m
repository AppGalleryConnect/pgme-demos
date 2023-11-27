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

#import "PropertySetCell.h"

@interface PropertySetCell()<UITextViewDelegate>
/// key
@property(nonatomic, strong) UITextView *keyTextView;
/// value
@property(nonatomic, strong) UITextView *valueTextView;
@end

@implementation PropertySetCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.backgroundColor = [UIColor clearColor];
    UIButton *deleteBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [deleteBtn setTitle:@"删除" forState:UIControlStateNormal];
    [deleteBtn setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
    [deleteBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [deleteBtn addTarget:self action:@selector(deleteFunc) forControlEvents:UIControlEventTouchUpInside];
    deleteBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    [self.contentView addSubview:deleteBtn];
    [deleteBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(80, 36));
        make.right.mas_equalTo(self.contentView).offset(-5);
        make.centerY.mas_equalTo(self.contentView);
    }];
    
    UIView *inputView = [[UIView alloc] init];
    [self.contentView addSubview:inputView];
    [inputView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.height.mas_equalTo(self.contentView);
        make.left.mas_equalTo(self.contentView).offset(5);
        make.right.mas_equalTo(self.contentView).offset(-85);
    }];
    
    UIView *keyView = [self viewWithTitle:@"key" textView:self.keyTextView];
    [inputView addSubview:keyView];
    [keyView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.height.mas_equalTo(inputView);
        make.width.mas_equalTo(inputView).multipliedBy(0.5);
    }];
    
    UIView *valueView = [self viewWithTitle:@"value" textView:self.valueTextView];
    [inputView addSubview:valueView];
    [valueView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.right.height.mas_equalTo(inputView);
        make.width.mas_equalTo(inputView).multipliedBy(0.5);
    }];
}


/// 删除
- (void)deleteFunc {
    if (self.deleteBlock) {
        self.deleteBlock();
    }
}

- (void)configData:(NSString *)key value:(NSString *)value {
    self.keyTextView.text = key;
    self.valueTextView.text = value;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    if ([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
}

- (void)textViewDidEndEditing:(UITextView *)textView {
    if (!StringEmpty(textView.text)) {
        return;
    }
    if (textView == self.keyTextView) {
        if (self.keyEndEditBlock) {
            self.keyEndEditBlock(textView.text);
        }
    } else {
        if (self.valueEndEditBlock) {
            self.valueEndEditBlock(textView.text);
        }
    }
}


- (UIView *)viewWithTitle:(NSString *)title textView:(UITextView *)textView {
    UIView *view = [[UIView alloc] init];
    UILabel *label = [[UILabel alloc] init];
    label.text = title;
    label.font = [UIFont systemFontOfSize:15];
    label.textColor = [UIColor whiteColor];
    [view addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
        make.width.mas_equalTo(40);
    }];
    
    UIImageView *bgImageView = [[UIImageView alloc] init];
    bgImageView.image = [UIImage imageNamed:@"bg_list"];
    [view addSubview:bgImageView];
    [bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.right.mas_equalTo(view);
        make.height.mas_equalTo(70);
        make.left.mas_equalTo(label.mas_right);
    }];
    [view addSubview:textView];
    [textView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.right.mas_equalTo(view);
        make.height.mas_equalTo(70);
        make.left.mas_equalTo(label.mas_right);
    }];
    return view;
}

- (UITextView *)keyTextView {
    if (!_keyTextView) {
        _keyTextView = [[UITextView alloc] init];
        _keyTextView.backgroundColor = [UIColor clearColor];
        _keyTextView.textColor = [UIColor whiteColor];
        _keyTextView.delegate = self;
    }
    return _keyTextView;
}

- (UITextView *)valueTextView {
    if (!_valueTextView) {
        _valueTextView = [[UITextView alloc] init];
        _valueTextView.backgroundColor = [UIColor clearColor];
        _valueTextView.textColor = [UIColor whiteColor];
        _valueTextView.delegate = self;
    }
    return _valueTextView;
}
@end
