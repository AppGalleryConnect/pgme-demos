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

typedef void(^ChancelSelectBlock)(NSString *_Nonnull);

NS_ASSUME_NONNULL_BEGIN

@interface ChannelSelectView : UIView

/// 频道列表
- (void)configChannelsArray:(NSArray *)channcelsArray;

/// 设置左侧文字标题
- (void)configTitle:(NSString *)title;

/// 设置当前选中的频道
- (void)configSelectedChannel:(NSString *)channel;

/// 退订频道
- (void)unSubscribeChannel:(NSString *)channel;

/// 获取当前选中的频道
- (NSString *)getSelectChannel;

/// 选中频道的回调
@property(nonatomic, copy) ChancelSelectBlock selectBlock;
@end

NS_ASSUME_NONNULL_END
