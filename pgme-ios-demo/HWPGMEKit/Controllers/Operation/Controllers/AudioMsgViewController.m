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

#import "AudioMsgViewController.h"
#import <Masonry/Masonry.h>
#import <MJExtension/MJExtension.h>
#import <HWPGMEKit/HWPGMEngine.h>
#import "HWPGMEDelegate.h"
#import <AVFoundation/AVFoundation.h>
#import "AudioMessageCell.h"
#import "AudioMsgModel.h"
#import "HWProgressHUD.h"

@interface AudioMsgViewController () <UITableViewDataSource,UITableViewDelegate>
@property(nonatomic, strong) UITableView *tableView;
@property(nonatomic, strong) NSMutableArray *recorderArray;
@property(nonatomic, strong) NSDate *previousDate;
@property(nonatomic, strong) UIButton *recordBtn;
/// 录音时长是否小于1s
@property(nonatomic, assign) BOOL recordTimeTooShort;
@end

@implementation AudioMsgViewController
static NSString *const AUDIO_MESSAGE_CELL_ID = @"AudioMessageCellId";
static NSString *const RECORD_FILE_PATH_KEY = @"recordFilePath";
static NSString *const FILEID_KEY = @"fileId";
static NSString *const DOWNLOAD_FILE_PATH_KEY = @"downloadFilePath";
static NSInteger const MAX_DURATION_TIME = 50;

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"语音消息";
    [self.view addSubview:self.tableView];
    UIView *buttonView = [self bottomButtonView];
    [self.view addSubview:buttonView];
    [buttonView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.left.width.mas_equalTo(self.view);
        make.height.mas_equalTo(40);
        make.bottom.mas_equalTo(self.view).offset(-kGetSafeAreaBottomHeight);
    }];
    [self initialData];
    [HWPGMEDelegate.getInstance addDelegate:self];
    [self audioAuth];
}

- (void)initialData {
    NSMutableArray *recorder = [NSKeyedUnarchiver unarchiveObjectWithFile:[self archivePath]];
    if (recorder && recorder.count > 0) {
        [self.recorderArray addObjectsFromArray:recorder];
        [self.tableView reloadData];
    }
}

- (void)longPressAction:(UILongPressGestureRecognizer *)gesture {
    switch (gesture.state) {
        case UIGestureRecognizerStateBegan: {
            [self.recordBtn setTitle:@"结束录音" forState:UIControlStateNormal];
            self.previousDate = [NSDate date];
            [HWPGMEngine.getInstance startRecordAudioMsg:[self recordFilePath]];
            [HWProgressHUD showInView:self.tableView andInfo:@"录音剩余时间:" andDuration:MAX_DURATION_TIME];
        }
            break;
        case UIGestureRecognizerStateEnded: {
            [self.recordBtn setTitle:@"长按录音" forState:UIControlStateNormal];
            [HWPGMEngine.getInstance stopRecordAudioMsg];
        }
            break;
        default:
            break;
    }
}

#pragma mark UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.recorderArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    AudioMessageCell *cell = [tableView dequeueReusableCellWithIdentifier:AUDIO_MESSAGE_CELL_ID];
    NSDictionary *param = [self.recorderArray objectAtIndex:indexPath.row];
    AudioMsgModel *model = [AudioMsgModel mj_objectWithKeyValues:param];
    cell.uploadBlock = ^{
        [HWPGMEngine.getInstance uploadAudioMsgFile:model.recordFilePath msTimeOut:3 * 1000];
    };
    __weak typeof(self) weakSelf = self;
    cell.downloadBlock = ^{
        [HWPGMEngine.getInstance downloadAudioMsgFile:model.fileId filePath:[weakSelf downloadFilePath] msTimeOut:3 * 1000];
    };
    cell.recoderAudioPlayBlock = ^{
        [HWPGMEngine.getInstance playAudioMsg:model.recordFilePath];
    };
    cell.downloadAudioPlayBlock = ^{
        [HWPGMEngine.getInstance playAudioMsg:model.downloadFilePath];
    };
    cell.getAudioMsgFileInfoBlock = ^{
        AudioMsgFileInfo *fileInfo = [HWPGMEngine.getInstance getAudioMsgFileInfo:model.recordFilePath];
        [HWTools showMessage:[NSString stringWithFormat:@"文件大小:%ld byte, 文件时长:%ld ms",fileInfo.bytes,fileInfo.milliSeconds]];
    };
    NSString *recordFileName = StringEmpty(model.recordFilePath) ? [model.recordFilePath lastPathComponent] : @"";
    NSString *downloadFileName = StringEmpty(model.downloadFilePath) ? [model.downloadFilePath lastPathComponent] : @"";
    [cell configCellDataFileId:model.fileId recordFileName:recordFileName downloadFileName:downloadFileName];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

#pragma mark HWPGMEngineDelegate

- (void)onPlayAudioMsg:(NSString *)filePath code:(int)code msg:(NSString *)msg{
    NSString *description = [NSString stringWithFormat:
                              @"onPlayAudioMsg : code : %ld massage : %@",
                              (long)code,
                              msg];
     NSLog(@"%@",description);
}

- (void)onRecordAudioMsg:(NSString *)filePath code:(int)code msg:(NSString *)msg {
    dispatch_sync(dispatch_get_main_queue(), ^{
        [self checkRecordTime];
    });
    if (self.recordTimeTooShort) {
        self.recordTimeTooShort = NO;
        return;
    }
    if (code != SUCCESS) {
        return;
    }
    NSMutableArray *array = [NSKeyedUnarchiver unarchiveObjectWithFile:[self archivePath]];
    if (!array) {
        array = [NSMutableArray array];
    }
    [array addObject:[NSMutableDictionary dictionaryWithDictionary:@{
        RECORD_FILE_PATH_KEY:filePath,
    }]];
    self.recorderArray = [array mutableCopy];
    [NSKeyedArchiver archiveRootObject:array toFile:[self archivePath]];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}

- (void)onUploadAudioMsgFile:(NSString *)filePath fileId:(NSString *)fileId code:(int)code msg:(NSString *)msg {
    if (code != SUCCESS) {
        return;
    }
    NSMutableArray *array = [NSKeyedUnarchiver unarchiveObjectWithFile:[self archivePath]];
    if (!array) {
        array = [NSMutableArray array];
    }
    for (NSMutableDictionary *dict in array) {
        if ([[dict objectForKey:RECORD_FILE_PATH_KEY] isEqualToString:filePath]) {
            [dict setValue:fileId forKey:FILEID_KEY];
            break;
        }
    }
    self.recorderArray = [array mutableCopy];
    [NSKeyedArchiver archiveRootObject:array toFile:[self archivePath]];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}

- (void)onDownloadAudioMsgFile:(NSString *)filePath fileId:(NSString *)fileId code:(int)code msg:(NSString *)msg {
    // 下载失败，不执行播放流程
    if (code != SUCCESS) {
        [HWTools showMessage:@"下载失败"];
        return;
    }
    NSMutableArray *array = [NSKeyedUnarchiver unarchiveObjectWithFile:[self archivePath]];
    if (!array) {
        array = [NSMutableArray array];
    }
    for (NSMutableDictionary *dict in array) {
        if ([[dict objectForKey:FILEID_KEY] isEqualToString:fileId]) {
            [dict setValue:filePath forKey:DOWNLOAD_FILE_PATH_KEY];
            break;
        }
    }
    self.recorderArray = [array mutableCopy];
    [NSKeyedArchiver archiveRootObject:array toFile:[self archivePath]];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.tableView reloadData];
    });
}

#pragma mark func

- (NSString *)archivePath {
    NSString *urlStr = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject];
    return [urlStr stringByAppendingPathComponent:@"audioMsg.archiver"];
}

- (NSString *)recordFilePath {
    NSString *urlStr = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject];
    NSString *time = [self timeStringWithDate:self.previousDate];
    urlStr = [urlStr stringByAppendingPathComponent:[NSString stringWithFormat:@"record%@.m4a", time]];
    return urlStr;
}

- (NSString *)downloadFilePath {
    NSString *urlStr = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) lastObject];
    NSString *time = [self timeStringWithDate:[NSDate date]];
    urlStr = [urlStr stringByAppendingPathComponent:[NSString stringWithFormat:@"download%@.m4a", time]];
    return urlStr;
}

- (NSString *)timeStringWithDate:(NSDate *)date {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd-HH-mm-ss"];
    return [formatter stringFromDate:date];
}

- (void)checkRecordTime {
    NSTimeInterval interval = [[NSDate date] timeIntervalSinceDate:self.previousDate];
    if (interval < 1) { /// 录音时长小于1s
        [HWTools showMessage:@"录音时长小于1s"];
        self.recordTimeTooShort = YES;
    }
    [HWProgressHUD hiddenHUD];
}

- (void)backFunc {
    [HWPGMEngine.getInstance stopPlayAudioMsg];
    [HWPGMEDelegate.getInstance removeDelegate:self];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)clearBtnFunc {
    [self.recorderArray removeAllObjects];
    [NSKeyedArchiver archiveRootObject:[NSMutableArray array] toFile:[self archivePath]];
    [self.tableView reloadData];
}

- (void)audioAuth {
    AVAuthorizationStatus microPhoneStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeAudio];
    switch (microPhoneStatus) {
        case AVAuthorizationStatusDenied:
        case AVAuthorizationStatusRestricted: {
            /// 被拒绝
            [HWTools showMessage:@"没有麦克风权限"];
        }
            break;
        case AVAuthorizationStatusNotDetermined: {
            /// 申请授权
            [AVCaptureDevice requestAccessForMediaType:AVMediaTypeAudio completionHandler:^(BOOL granted) {
            }];
        }
            break;
        default:
            break;
    }
}

- (UIView *)bottomButtonView {
    UIView *view = [[UIView alloc] init];

    UIButton *backBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [backBtn setTitle:@"返回" forState:UIControlStateNormal];
    [backBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
    [backBtn addTarget:self action:@selector(backFunc) forControlEvents:UIControlEventTouchUpInside];

    UIButton *clearBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    [clearBtn setTitle:@"清空" forState:UIControlStateNormal];
    [clearBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
    [clearBtn addTarget:self action:@selector(clearBtnFunc) forControlEvents:UIControlEventTouchUpInside];

    UIButton *recordBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    self.recordBtn = recordBtn;
    [recordBtn setTitle:@"长按录音" forState:UIControlStateNormal];
    [recordBtn setBackgroundImage:[UIImage imageNamed:@"bt_switch_normal"] forState:UIControlStateNormal];
    UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPressAction:)];
    [self.recordBtn addGestureRecognizer:longPress];

    [view addSubview:backBtn];
    [view addSubview:clearBtn];
    [view addSubview:recordBtn];

    NSArray *btnArray = [NSArray arrayWithObjects:backBtn, clearBtn, self.recordBtn, nil];
    [btnArray mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:10 tailSpacing:10];
    [btnArray mas_updateConstraints:^(MASConstraintMaker *make) {
        make.height.mas_equalTo(view);
    }];
    return view;
}

#pragma mark lazy

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, SCREENWIDTH, SCREENHEIGHT - 40 - kGetSafeAreaBottomHeight) style:UITableViewStylePlain];
        [_tableView registerClass:[AudioMessageCell class] forCellReuseIdentifier:AUDIO_MESSAGE_CELL_ID];
        _tableView.estimatedRowHeight = 80;
        _tableView.rowHeight = UITableViewAutomaticDimension;
        _tableView.delegate = self;
        _tableView.dataSource = self;
    }
    return _tableView;
}

- (NSMutableArray *)recorderArray {
    if (!_recorderArray) {
        _recorderArray = [NSMutableArray array];
    }
    return _recorderArray;
}

@end
