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

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^AudioMsgCellBtnClickBlock)(void);

@interface AudioMessageCell : UITableViewCell

/// 设置cell数据
/// @param fileId 文件ID
/// @param recordFileName 录音的文件名
/// @param downloadFileName 下载音频的文件名
- (void)configCellDataFileId:(NSString *)fileId recordFileName:(NSString *)recordFileName downloadFileName:(NSString *)downloadFileName;

/// 上传按钮点击
@property(nonatomic, copy) AudioMsgCellBtnClickBlock uploadBlock;

/// 录制的音频播放按钮点击
@property(nonatomic, copy) AudioMsgCellBtnClickBlock recoderAudioPlayBlock;

/// 下载按钮点击
@property(nonatomic, copy) AudioMsgCellBtnClickBlock downloadBlock;

/// 下载的音频播放按钮点击
@property(nonatomic, copy) AudioMsgCellBtnClickBlock downloadAudioPlayBlock;

/// 获取文件大小和时长
@property(nonatomic, copy) AudioMsgCellBtnClickBlock getAudioMsgFileInfoBlock;
@end
NS_ASSUME_NONNULL_END
