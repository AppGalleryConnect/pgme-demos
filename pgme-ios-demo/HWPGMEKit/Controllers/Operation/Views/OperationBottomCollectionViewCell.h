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

typedef enum : NSUInteger {
    BottomButtonType_Switch,
    BottomButtonType_Destory,
    BottomButtonType_AudioMsg,
    BottomButtonType_AduioEffect,
    BottomButtonType_PlayerPosition,
    BottomButtonType_RtmP2P,
    BottomButtonType_RtmChannel,
} BottomButtonType;

static NSString *NORMALTITLE = @"normalTitle";
static NSString *SELECTEDTITLE = @"selectedTitle";
static NSString *NORMALIMAGE = @"normalImage";
static NSString *LIGHTEDIMAGE = @"highlightedImage";
static NSString *TYPE = @"type";

typedef void(^ButtonTouchBlock)(UIButton *btn, BottomButtonType type);

@interface OperationBottomCollectionViewCell : UICollectionViewCell

/// cell 数据
@property(nonatomic, strong) NSDictionary *dataDic;

/// 点击事件回调
@property(nonatomic, copy) ButtonTouchBlock touchBlock;

@end

NS_ASSUME_NONNULL_END
