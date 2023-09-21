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

#import "AudioMessageCell.h"

@interface AudioMessageCell()
/// 获取文件大小和时长按钮
@property(nonatomic, strong) UIButton *fileInfoBtn;
/// 录制音频信息View
@property(nonatomic, strong) UIView *recordView;
/// 下载的音频信息View
@property(nonatomic, strong) UIView *downloadView;

/// 文件上传状态
@property(nonatomic, strong) UILabel *uploadStatusLabel;
/// 录制的文件名
@property(nonatomic, strong) UILabel *recordFileNameLabel;
/// 文件上传按钮
@property(nonatomic, strong) UIButton *uploadBtn;
/// 录制的音频播放按钮
@property(nonatomic, strong) UIButton *recordAudioPlayBtn;

/// 文件下载状态
@property(nonatomic, strong) UILabel *downloadStatusLabel;
/// 下载的文件名
@property(nonatomic, strong) UILabel *downloadFileNameLabel;
/// 文件下载按钮 / 下载的音频播放按钮
@property(nonatomic, strong) UIButton *downloadBtn;
@end

@implementation AudioMessageCell
static CGFloat const SPACE = 12.0;
static CGFloat const BTNHEIGHT = 36.0;
static CGFloat const BTNWIDTH = 60.0;

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self initSubviews];
    }
    return self;
}

- (void)initSubviews {
    [self.contentView addSubview:self.fileInfoBtn];
    [self.fileInfoBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.mas_equalTo(self.contentView).offset(-SPACE);
        make.centerY.mas_equalTo(self.contentView);
        make.size.mas_equalTo(CGSizeMake(BTNWIDTH, BTNHEIGHT));
    }];
    [self addRecordView];
    [self addDownloadView];
}

- (void)addRecordView {
    [self.contentView addSubview:self.recordView];
    [self.recordView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.left.mas_equalTo(self.contentView).offset(SPACE);
        make.right.mas_equalTo(self.fileInfoBtn.mas_left).offset(-SPACE);
        make.height.mas_equalTo(BTNHEIGHT).priorityHigh();
        make.bottom.mas_equalTo(self.contentView).offset(-SPACE);
    }];
    
    [self.recordView addSubview:self.recordFileNameLabel];
    [self.recordFileNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(self.recordView);
        make.right.mas_equalTo(self.contentView).offset(-(SPACE * 4 + BTNWIDTH * 3));
    }];
    
    [self.recordView addSubview:self.recordAudioPlayBtn];
    [self.recordAudioPlayBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.centerY.mas_equalTo(self.recordView);
        make.size.mas_equalTo(CGSizeMake(BTNWIDTH, BTNHEIGHT));
    }];

    [self.recordView addSubview:self.uploadBtn];
    [self.uploadBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.recordView);
        make.right.mas_equalTo(self.recordAudioPlayBtn.mas_left).offset(-SPACE);
        make.size.mas_equalTo(CGSizeMake(BTNWIDTH, BTNHEIGHT));
    }];
    
    [self.recordView addSubview:self.uploadStatusLabel];
    [self.uploadStatusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.recordView);
        make.width.mas_equalTo(BTNWIDTH);
        make.right.mas_equalTo(self.recordAudioPlayBtn.mas_left).offset(-SPACE);
    }];
}

- (void)addDownloadView {
    [self.contentView addSubview:self.downloadView];
    [self.downloadView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.right.mas_equalTo(self.recordView);
        make.top.mas_equalTo(self.recordView.mas_bottom);
        make.height.mas_equalTo(0).priorityHigh();
        make.bottom.mas_equalTo(self.contentView).offset(-SPACE);
    }];
    
    [self.downloadView addSubview:self.downloadFileNameLabel];
    [self.downloadFileNameLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.centerY.mas_equalTo(self.downloadView);
        make.right.mas_equalTo(self.contentView).offset(-(SPACE * 4 + BTNWIDTH * 3));
    }];
    
    [self.downloadView addSubview:self.downloadBtn];
    [self.downloadBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.right.centerY.mas_equalTo(self.downloadView);
        make.size.mas_equalTo(CGSizeMake(BTNWIDTH, BTNHEIGHT));
    }];
    
    [self.downloadView addSubview:self.downloadStatusLabel];
    [self.downloadStatusLabel mas_makeConstraints:^(MASConstraintMaker *make) {
        make.centerY.mas_equalTo(self.downloadView);
        make.width.mas_equalTo(BTNWIDTH);
        make.right.mas_equalTo(self.downloadBtn.mas_left).offset(-SPACE);
    }];
    self.downloadView.hidden = YES;
}

- (void)configCellDataFileId:(NSString *)fileId recordFileName:(NSString *)recordFileName downloadFileName:(NSString *)downloadFileName {
    self.recordFileNameLabel.text = recordFileName;
    if (StringEmpty(fileId)) {
        self.uploadStatusLabel.hidden = NO;
        self.uploadBtn.hidden = YES;
        if (StringEmpty(downloadFileName)) { /// 已下载
            [self.downloadBtn setTitle:@"播放" forState:UIControlStateNormal];
            self.downloadStatusLabel.hidden = NO;
        } else { /// 未下载
            [self.downloadBtn setTitle:@"下载" forState:UIControlStateNormal];
            self.downloadStatusLabel.hidden = YES;
        }
        self.downloadFileNameLabel.text = downloadFileName;
        [self.recordView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(self.contentView).offset(-(SPACE+BTNHEIGHT));
        }];
        [self.downloadView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.height.mas_equalTo(BTNHEIGHT);
        }];
        self.downloadView.hidden = NO;
    } else {
        self.uploadStatusLabel.hidden = YES;
        self.uploadBtn.hidden = NO;
        self.downloadFileNameLabel.text = @"";
        [self.recordView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.bottom.mas_equalTo(self.contentView).offset(-SPACE);
        }];
        [self.downloadView mas_updateConstraints:^(MASConstraintMaker *make) {
            make.height.mas_equalTo(0);
        }];
        self.downloadView.hidden = YES;
    }
}

/// 上传按钮点击
- (void)uploadBtnAction {
    if (self.uploadBlock) {
        self.uploadBlock();
    }
}

/// 录制音频的播放点击
- (void)recordAudioPlayBtnAction {
    if (self.recoderAudioPlayBlock) {
        self.recoderAudioPlayBlock();
    }
}

/// 下载按钮点击
- (void)downlaodBtnAction {
    if ([self.downloadBtn.titleLabel.text isEqualToString:@"下载"]) {
        if (self.downloadBlock) {
            self.downloadBlock();
        }
    } else {
        if (self.downloadAudioPlayBlock) {
            self.downloadAudioPlayBlock();
        }
    }
}

/// 获取文件时长和大小
- (void)getFileInfoBtnAction {
    if (self.getAudioMsgFileInfoBlock) {
        self.getAudioMsgFileInfoBlock();
    }
}

- (UIButton *)fileInfoBtn {
    if (!_fileInfoBtn) {
        _fileInfoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_fileInfoBtn setTitle:@"查询" forState:UIControlStateNormal];
        [_fileInfoBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _fileInfoBtn.titleLabel.font = [UIFont systemFontOfSize:16];
        [_fileInfoBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        _fileInfoBtn.layer.cornerRadius = 6;
        [_fileInfoBtn addTarget:self action:@selector(getFileInfoBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _fileInfoBtn;
}

- (UIView *)recordView {
    if (!_recordView) {
        _recordView = [[UIView alloc] init];
    }
    return _recordView;
}

- (UIView *)downloadView {
    if (!_downloadView) {
        _downloadView = [[UIView alloc] init];
    }
    return _downloadView;
}

- (UILabel *)uploadStatusLabel {
    if (!_uploadStatusLabel) {
        _uploadStatusLabel = [[UILabel alloc] init];
        _uploadStatusLabel.font = [UIFont systemFontOfSize:12];
        _uploadStatusLabel.textColor = [UIColor hw_playerColor];
        _uploadStatusLabel.text = @"上传成功";
    }
    return _uploadStatusLabel;
}

- (UILabel *)recordFileNameLabel {
    if (!_recordFileNameLabel) {
        _recordFileNameLabel = [[UILabel alloc] init];
        _recordFileNameLabel.font = [UIFont systemFontOfSize:12];
        _recordFileNameLabel.numberOfLines = 2;
    }
    return _recordFileNameLabel;
}

- (UIButton *)uploadBtn {
    if (!_uploadBtn) {
        _uploadBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_uploadBtn setTitle:@"上传" forState:UIControlStateNormal];
        [_uploadBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _uploadBtn.titleLabel.font = [UIFont systemFontOfSize:16];
        [_uploadBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        _uploadBtn.layer.cornerRadius = 6;
        [_uploadBtn addTarget:self action:@selector(uploadBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _uploadBtn;
}

- (UIButton *)recordAudioPlayBtn {
    if (!_recordAudioPlayBtn) {
        _recordAudioPlayBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_recordAudioPlayBtn setTitle:@"播放" forState:UIControlStateNormal];
        [_recordAudioPlayBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _recordAudioPlayBtn.titleLabel.font = [UIFont systemFontOfSize:16];
        [_recordAudioPlayBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        _recordAudioPlayBtn.layer.cornerRadius = 6;
        [_recordAudioPlayBtn addTarget:self action:@selector(recordAudioPlayBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _recordAudioPlayBtn;
}

- (UILabel *)downloadStatusLabel {
    if (!_downloadStatusLabel) {
        _downloadStatusLabel = [[UILabel alloc] init];
        _downloadStatusLabel.font = [UIFont systemFontOfSize:12];
        _downloadStatusLabel.textColor = [UIColor hw_playerColor];
        _downloadStatusLabel.text = @"下载成功";
    }
    return _downloadStatusLabel;
}

- (UILabel *)downloadFileNameLabel {
    if (!_downloadFileNameLabel) {
        _downloadFileNameLabel = [[UILabel alloc] init];
        _downloadFileNameLabel.font = [UIFont systemFontOfSize:12];
        _downloadFileNameLabel.numberOfLines = 2;
    }
    return _downloadFileNameLabel;
}

- (UIButton *)downloadBtn {
    if (!_downloadBtn) {
        _downloadBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_downloadBtn setTitle:@"下载" forState:UIControlStateNormal];
        [_downloadBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _downloadBtn.titleLabel.font = [UIFont systemFontOfSize:16];
        [_downloadBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
        _downloadBtn.layer.cornerRadius = 6;
        [_downloadBtn addTarget:self action:@selector(downlaodBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _downloadBtn;
}

@end
