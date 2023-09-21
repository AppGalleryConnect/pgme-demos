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

#import "SelfLocationView.h"

@interface SelfLocationView ()
/// 当前玩家名称
@property(nonatomic, strong) UILabel *playNameLabel;
/// x轴坐标
@property(nonatomic, strong) UISlider *xPositionSlider;
@property(nonatomic, strong) UILabel *xPositionLabel;
/// y轴坐标
@property(nonatomic, strong) UISlider *yPositionSlider;
@property(nonatomic, strong) UILabel *yPositionLabel;
/// z轴坐标
@property(nonatomic, strong) UISlider *zPositionSlider;
@property(nonatomic, strong) UILabel *zPositionLabel;
/// x轴朝向
@property(nonatomic, strong) UISlider *xAxisSlider;
@property(nonatomic, strong) UILabel *xAxisLabel;
/// y轴朝向
@property(nonatomic, strong) UISlider *yAxisSlider;
@property(nonatomic, strong) UILabel *yAxisLabel;
/// z轴朝向
@property(nonatomic, strong) UISlider *zAxisSlider;
@property(nonatomic, strong) UILabel *zAxisLabel;
@property(nonatomic, strong) UIImageView *bgView;
@property(nonatomic, strong) SelfLocationModel *locationModel;
@end

@implementation SelfLocationView
static const CGFloat VERTICAL_SPACE = 5.0;
static const CGFloat NAME_VIEW_WIDTH = 50.0;
static const CGFloat SLIDER_VIEW_HEIGHT = 130.0;
static const CGFloat SLIDER_HEIGHT = 25.0;

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)setupViews {
    [self addSubview:self.bgView];
    [self.bgView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.mas_equalTo(UIEdgeInsetsMake(VERTICAL_SPACE, VERTICAL_SPACE, VERTICAL_SPACE, VERTICAL_SPACE));
    }];
    [self playerNameView];
    [self playerLocationView];
    [self playerAxisView];
}

- (void)playerNameView {
    UIView *view = [[UIView alloc] init];
    [self.bgView addSubview:view];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.width.mas_equalTo(NAME_VIEW_WIDTH);
        make.left.top.height.mas_equalTo(self.bgView);
    }];

    [view addSubview:self.playNameLabel];
    [self.playNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(view);
        make.left.mas_equalTo(view).offset(VERTICAL_SPACE);
        make.width.mas_lessThanOrEqualTo(32);
    }];
}

- (void)playerLocationView {
    UIView *view = [[UIView alloc] init];
    [self.bgView addSubview:view];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.bgView).offset(NAME_VIEW_WIDTH);
        make.top.mas_equalTo(self.bgView).offset(VERTICAL_SPACE);
        make.height.mas_equalTo(SLIDER_VIEW_HEIGHT);
        make.right.mas_equalTo(self.bgView).offset(-VERTICAL_SPACE);
    }];

    UILabel *titleLabel = [self commonLabelWithTitle:@"位置"];
    [view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.centerX.mas_equalTo(view);
        make.height.mas_equalTo(25);
    }];
    
    UIView *xPositionView = [self viewWithSlider:self.xPositionSlider label:self.xPositionLabel title:@"X轴"];
    [view addSubview:xPositionView];
    [xPositionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(titleLabel.mas_bottom);
        make.left.width.mas_equalTo(view);
        make.height.mas_equalTo(35);
    }];
    UIView *yPositionView = [self viewWithSlider:self.yPositionSlider label:self.yPositionLabel title:@"Y轴"];
    [view addSubview:yPositionView];
    [yPositionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.height.mas_equalTo(xPositionView);
        make.top.mas_equalTo(xPositionView.mas_bottom);
    }];
    UIView *zPositionView = [self viewWithSlider:self.zPositionSlider label:self.zPositionLabel title:@"Z轴"];
    [view addSubview:zPositionView];
    [zPositionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.height.mas_equalTo(yPositionView);
        make.top.mas_equalTo(yPositionView.mas_bottom);
    }];
}

- (void)playerAxisView {
    UIView *view = [[UIView alloc] init];
    [self.bgView addSubview:view];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.mas_equalTo(self.bgView).offset(NAME_VIEW_WIDTH);
        make.top.mas_equalTo(self.bgView).offset(SLIDER_VIEW_HEIGHT+VERTICAL_SPACE);
        make.height.mas_equalTo(SLIDER_VIEW_HEIGHT);
        make.right.mas_equalTo(self.bgView).offset(-VERTICAL_SPACE);
    }];

    UILabel *titleLabel = [self commonLabelWithTitle:@"朝向"];
    [view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.centerX.mas_equalTo(view);
        make.height.mas_equalTo(25);
    }];
    
    UIView *xPositionView = [self viewWithSlider:self.xAxisSlider label:self.xAxisLabel title:@"X轴"];
    [view addSubview:xPositionView];
    [xPositionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(titleLabel.mas_bottom);
        make.left.width.mas_equalTo(view);
        make.height.mas_equalTo(35);
    }];
    UIView *yPositionView = [self viewWithSlider:self.yAxisSlider label:self.yAxisLabel title:@"Y轴"];
    [view addSubview:yPositionView];
    [yPositionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.height.mas_equalTo(xPositionView);
        make.top.mas_equalTo(xPositionView.mas_bottom);
    }];
    UIView *zPositionView = [self viewWithSlider:self.zAxisSlider label:self.zAxisLabel title:@"Z轴"];
    [view addSubview:zPositionView];
    [zPositionView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.height.mas_equalTo(yPositionView);
        make.top.mas_equalTo(yPositionView.mas_bottom);
    }];
}

- (UIView *)viewWithSlider:(UISlider *)slider label:(UILabel *)label title:(NSString *)title {
    UIView *view = [[UIView alloc] init];
    
    UILabel *titleLabel = [self commonLabelWithTitle:title];
    [view addSubview:titleLabel];
    [titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.left.mas_equalTo(view);
        make.width.mas_equalTo(30);
    }];
    
    [view addSubview:label];
    [label mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.right.mas_equalTo(view);
        make.width.mas_equalTo(35);
    }];

    [view addSubview:slider];
    [slider mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(view);
        make.left.mas_equalTo(titleLabel.mas_right);
        make.height.mas_equalTo(SLIDER_HEIGHT);
        make.right.mas_equalTo(label.mas_left);
    }];
    return view;
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
    } else if (slider == self.zPositionSlider) {
        self.zPositionLabel.text = [NSString stringWithFormat:@"%d", value];
    } else if (slider == self.xAxisSlider) {
        self.xAxisLabel.text = [NSString stringWithFormat:@"%d", value];
    } else if (slider == self.yAxisSlider) {
        self.yAxisLabel.text = [NSString stringWithFormat:@"%d", value];
    } else {
        self.zAxisLabel.text = [NSString stringWithFormat:@"%d", value];
    }
}

- (void)sliderTouchUpInSide:(UISlider *)slider {
    if (self.updateLocationBlock) {
        self.locationModel.position.forward = (NSInteger) self.zPositionSlider.value;
        self.locationModel.position.right = (NSInteger) self.xPositionSlider.value;
        self.locationModel.position.up = (NSInteger) self.yPositionSlider.value;
        self.locationModel.axis.forward = (NSInteger) self.zAxisSlider.value;
        self.locationModel.axis.right = (NSInteger) self.xAxisSlider.value;
        self.locationModel.axis.up = (NSInteger) self.yAxisSlider.value;
        self.updateLocationBlock(self.locationModel);
    }
}

- (void)configData:(SelfLocationModel *)model {
    if (!model.position) {
        model.position = [[PositionModel alloc] init];
    }
    if (!model.axis) {
        model.axis = [[AxisModel alloc] init];
    }
    self.locationModel = model;
    self.playNameLabel.text = model.openId;
    self.xPositionLabel.text = [NSString stringWithFormat:@"%ld", model.position.right];
    self.xPositionSlider.value = model.position.right;
    self.yPositionLabel.text = [NSString stringWithFormat:@"%ld", model.position.up];
    self.yPositionSlider.value = model.position.up;
    self.zPositionLabel.text = [NSString stringWithFormat:@"%ld", model.position.forward];
    self.zPositionSlider.value = model.position.forward;

    self.xAxisLabel.text = [NSString stringWithFormat:@"%ld", model.axis.right];
    self.xAxisSlider.value = model.axis.right;
    self.yAxisLabel.text = [NSString stringWithFormat:@"%ld", model.axis.up];
    self.yAxisSlider.value = model.axis.up;
    self.zAxisLabel.text = [NSString stringWithFormat:@"%ld", model.axis.forward];
    self.zAxisSlider.value = model.axis.forward;
}

#pragma mark lazy

- (UILabel *)playNameLabel {
    if (!_playNameLabel) {
        _playNameLabel = [self commonLabelWithTitle:@""];
        _playNameLabel.numberOfLines = 0;
    }
    return _playNameLabel;
}

- (UISlider *)xPositionSlider {
    if (!_xPositionSlider) {
        _xPositionSlider = [self commonSliderWithValue:100.0];
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
        _yPositionSlider = [self commonSliderWithValue:100.0];
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
        _zPositionSlider = [self commonSliderWithValue:100.0];
    }
    return _zPositionSlider;
}

- (UILabel *)zPositionLabel {
    if (!_zPositionLabel) {
        _zPositionLabel = [self commonLabelWithTitle:@"0"];
    }
    return _zPositionLabel;
}

- (UISlider *)xAxisSlider {
    if (!_xAxisSlider) {
        _xAxisSlider = [self commonSliderWithValue:180.0];
    }
    return _xAxisSlider;
}

- (UILabel *)xAxisLabel {
    if (!_xAxisLabel) {
        _xAxisLabel = [self commonLabelWithTitle:@"0"];
    }
    return _xAxisLabel;
}

- (UISlider *)yAxisSlider {
    if (!_yAxisSlider) {
        _yAxisSlider = [self commonSliderWithValue:180.0];
    }
    return _yAxisSlider;
}

- (UILabel *)yAxisLabel {
    if (!_yAxisLabel) {
        _yAxisLabel = [self commonLabelWithTitle:@"0"];
    }
    return _yAxisLabel;
}

- (UISlider *)zAxisSlider {
    if (!_zAxisSlider) {
        _zAxisSlider = [self commonSliderWithValue:180.0];
    }
    return _zAxisSlider;
}

- (UILabel *)zAxisLabel {
    if (!_zAxisLabel) {
        _zAxisLabel = [self commonLabelWithTitle:@"0"];
    }
    return _zAxisLabel;
}

- (UIImageView *)bgView {
    if (!_bgView) {
        _bgView = [[UIImageView alloc] init];
        _bgView.image = [UIImage imageNamed:@"bg_list"];
        _bgView.userInteractionEnabled = YES;
    }
    return _bgView;
}

- (UISlider *)commonSliderWithValue:(float)value {
    UISlider *slider = [[UISlider alloc] init];
    slider.minimumValue = -value;
    slider.maximumValue = value;
    [slider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];
    [slider addTarget:self action:@selector(sliderTouchUpInSide:) forControlEvents:UIControlEventTouchUpInside];
    [slider addTarget:self action:@selector(sliderTouchUpInSide:) forControlEvents:UIControlEventTouchUpOutside];
    return slider;
}

- (UILabel *)commonLabelWithTitle:(NSString *)title {
    UILabel *label = [[UILabel alloc] init];
    label.text = title;
    label.font = [UIFont systemFontOfSize:15];
    label.numberOfLines = 1;
    label.textColor = [UIColor whiteColor];
    label.textAlignment = NSTextAlignmentCenter;
    return label;
}
@end
