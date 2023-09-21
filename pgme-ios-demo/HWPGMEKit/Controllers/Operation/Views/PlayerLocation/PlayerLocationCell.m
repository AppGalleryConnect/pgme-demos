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
#import "PlayerLocationCell.h"

@interface PlayerLocationCell ()
/// 玩家ID
@property(nonatomic, strong) UILabel *playerIdLabel;
/// x轴坐标
@property(nonatomic, strong) UISlider *xPositionSlider;
@property(nonatomic, strong) UILabel *xPositionLabel;
/// y轴坐标
@property(nonatomic, strong) UISlider *yPositionSlider;
@property(nonatomic, strong) UILabel *yPositionLabel;
/// z轴坐标
@property(nonatomic, strong) UISlider *zPositionSlider;
@property(nonatomic, strong) UILabel *zPositionLabel;
@property(nonatomic, strong) UIImageView *bgImageView;

@property(nonatomic, strong) OtherLocationModel *playerLocation;
@end

@implementation PlayerLocationCell
static const CGFloat VERTICAL_SPACE = 5.0;
static const CGFloat INPUT_VIEW_HEIGHT = 120.0;
static const CGFloat NAME_VIEW_WIDTH = 75.0;
static const CGFloat TEXT_FIELD_HEIGHT = 36.0;

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    self.backgroundColor = [UIColor clearColor];
    [self.contentView addSubview:self.bgImageView];
    [self.bgImageView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(VERTICAL_SPACE, VERTICAL_SPACE, VERTICAL_SPACE, VERTICAL_SPACE));
    }];

    [self playerNameView];
    [self playerLocationInputView];
}

- (void)playerNameView {
    UIView *view = [[UIView alloc] init];
    [self.bgImageView addSubview:view];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(NAME_VIEW_WIDTH);
        make.height.mas_equalTo(INPUT_VIEW_HEIGHT);
        make.bottom.mas_equalTo(self.bgImageView).offset(-VERTICAL_SPACE);
        make.left.mas_equalTo(self.bgImageView).offset(VERTICAL_SPACE);
    }];

    [view addSubview:self.playerIdLabel];
    [self.playerIdLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(view);
        make.width.mas_lessThanOrEqualTo(32);
    }];

    UILabel *xLabel = [self commonLabelWithTitle:@"X轴"];
    [view addSubview:xLabel];
    [xLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.top.mas_equalTo(view);
        make.height.mas_equalTo(TEXT_FIELD_HEIGHT);
    }];

    UILabel *yLabel = [self commonLabelWithTitle:@"Y轴"];
    [view addSubview:yLabel];
    [yLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.centerY.mas_equalTo(view);
        make.height.mas_equalTo(xLabel);
    }];

    UILabel *zLabel = [self commonLabelWithTitle:@"Z轴"];
    [view addSubview:zLabel];
    [zLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.bottom.mas_equalTo(view);
        make.height.mas_equalTo(xLabel);
    }];
}

- (void)playerLocationInputView {
    UIView *view = [[UIView alloc] init];
    [self.bgImageView addSubview:view];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.bgImageView).offset(NAME_VIEW_WIDTH + VERTICAL_SPACE);
        make.bottom.mas_equalTo(self.bgImageView).offset(-VERTICAL_SPACE);
        make.height.mas_equalTo(INPUT_VIEW_HEIGHT);
        make.right.mas_equalTo(self.bgImageView).offset(-VERTICAL_SPACE);
    }];

    UIButton *deleteButton = [UIButton buttonWithType:UIButtonTypeCustom];
    [deleteButton setTitle:@"删除" forState:UIControlStateNormal];
    [deleteButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    deleteButton.titleLabel.font = [UIFont systemFontOfSize:14];
    [deleteButton setBackgroundImage:[UIImage imageNamed:@"bt_cancel_normal"] forState:UIControlStateNormal];
    deleteButton.layer.cornerRadius = 6;
    [deleteButton addTarget:self action:@selector(deleteLocationFunc) forControlEvents:UIControlEventTouchUpInside];

    [view addSubview:deleteButton];
    [deleteButton mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(view);
        make.size.mas_equalTo(CGSizeMake(50, 30));
        make.right.mas_equalTo(view);
    }];

    UIView *positionView = [[UIView alloc] init];
    [view addSubview:positionView];
    [positionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.height.mas_equalTo(view);
        make.right.mas_equalTo(deleteButton.mas_left).offset(-5);
    }];

    [positionView addSubview:self.xPositionLabel];
    [self.xPositionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(TEXT_FIELD_HEIGHT);
        make.top.right.mas_equalTo(positionView);
    }];
    [positionView addSubview:self.xPositionSlider];
    [self.xPositionSlider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.top.mas_equalTo(positionView);
        make.height.mas_equalTo(TEXT_FIELD_HEIGHT);
        make.right.mas_equalTo(self.xPositionLabel.mas_left).offset(-5);
    }];
    [positionView addSubview:self.yPositionLabel];
    [self.yPositionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(TEXT_FIELD_HEIGHT);
        make.centerY.right.mas_equalTo(positionView);
    }];
    [positionView addSubview:self.yPositionSlider];
    [self.yPositionSlider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(positionView);
        make.height.mas_equalTo(self.xPositionSlider);
        make.right.mas_equalTo(self.yPositionLabel.mas_left).offset(-5);
    }];
    [positionView addSubview:self.zPositionLabel];
    [self.zPositionLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(TEXT_FIELD_HEIGHT);
        make.right.bottom.mas_equalTo(positionView);
    }];
    [positionView addSubview:self.zPositionSlider];
    [self.zPositionSlider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.bottom.mas_equalTo(positionView);
        make.height.mas_equalTo(self.xPositionSlider);
        make.right.mas_equalTo(self.zPositionLabel.mas_left).offset(-5);
    }];
}

#pragma mark func

/// 进度条value改变回调
- (void)sliderValueChanged:(UISlider *)slider {
    int value = (int) roundf(slider.value);
    slider.value = value;
    if (slider == self.xPositionSlider) {
        self.xPositionLabel.text = [NSString stringWithFormat:@"%d", value];
    } else if (slider == self.yPositionSlider) {
        self.yPositionLabel.text = [NSString stringWithFormat:@"%d", value];
    } else {
        self.zPositionLabel.text = [NSString stringWithFormat:@"%d", value];
    }
}

- (void)sliderTouchUpInSide:(UISlider *)slider {
    if (self.updateLocationBlock) {
        self.playerLocation.position.forward = (NSInteger) self.zPositionSlider.value;
        self.playerLocation.position.right = (NSInteger) self.xPositionSlider.value;
        self.playerLocation.position.up = (NSInteger) self.yPositionSlider.value;
        self.updateLocationBlock(self.playerLocation);
    }
}

- (void)configCellData:(OtherLocationModel *)model {
    self.playerLocation = model;
    self.playerIdLabel.text = model.openId;
    self.xPositionLabel.text = [NSString stringWithFormat:@"%ld", model.position.right];
    self.xPositionSlider.value = model.position.right;
    self.yPositionLabel.text = [NSString stringWithFormat:@"%ld", model.position.up];
    self.yPositionSlider.value = model.position.up;
    self.zPositionLabel.text = [NSString stringWithFormat:@"%ld", model.position.forward];
    self.zPositionSlider.value = model.position.forward;
}

/// 删除位置信息
- (void)deleteLocationFunc {
    if (self.deleteBlock) {
        self.deleteBlock();
    }
}

#pragma mark lazy

- (UILabel *)playerIdLabel {
    if (!_playerIdLabel) {
        _playerIdLabel = [self commonLabelWithTitle:@""];
        _playerIdLabel.numberOfLines = 0;
    }
    return _playerIdLabel;
}

- (UISlider *)xPositionSlider {
    if (!_xPositionSlider) {
        _xPositionSlider = [self commonSlider];
    }
    return _xPositionSlider;
}

- (UILabel *)xPositionLabel {
    if (!_xPositionLabel) {
        _xPositionLabel = [self commonLabelWithTitle:@"0"];
    }
    return _xPositionLabel;
}

- (UISlider *)yPositionSlider {
    if (!_yPositionSlider) {
        _yPositionSlider = [self commonSlider];
    }
    return _yPositionSlider;
}

- (UILabel *)yPositionLabel {
    if (!_yPositionLabel) {
        _yPositionLabel = [self commonLabelWithTitle:@"0"];
    }
    return _yPositionLabel;
}

- (UISlider *)zPositionSlider {
    if (!_zPositionSlider) {
        _zPositionSlider = [self commonSlider];
    }
    return _zPositionSlider;
}

- (UILabel *)zPositionLabel {
    if (!_zPositionLabel) {
        _zPositionLabel = [self commonLabelWithTitle:@"0"];
    }
    return _zPositionLabel;
}

- (UISlider *)commonSlider {
    UISlider *slider = [[UISlider alloc] init];
    slider.minimumValue = -100.0;
    slider.maximumValue = 100.0;
    [slider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];
    [slider addTarget:self action:@selector(sliderTouchUpInSide:) forControlEvents:UIControlEventTouchUpOutside];
    [slider addTarget:self action:@selector(sliderTouchUpInSide:) forControlEvents:UIControlEventTouchUpInside];

    return slider;
}

- (UIImageView *)bgImageView {
    if (!_bgImageView) {
        _bgImageView = [[UIImageView alloc] init];
        _bgImageView.image = [UIImage imageNamed:@"bg_list"];
        _bgImageView.userInteractionEnabled = YES;
    }
    return _bgImageView;
}

- (UILabel *)commonLabelWithTitle:(NSString *)title {
    UILabel *label = [[UILabel alloc] init];
    label.text = title;
    label.font = [UIFont systemFontOfSize:16];
    label.numberOfLines = 1;
    label.textColor = [UIColor whiteColor];
    return label;
}
@end
