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

#import "HWSpeakButton.h"

@interface HWSpeakButton (){
    // 播放图片数组
    NSMutableArray *beginArray;
    // 停止图片数组
    NSMutableArray *stopArray;
}

@end

@implementation HWSpeakButton

- (instancetype)init
{
    self = [super init];
    if (self) {
        beginArray = [NSMutableArray arrayWithCapacity:2];
        for (int i = 0; i < 3; i++) {
            NSString *imageStr = [NSString stringWithFormat:@"btn_speaker_speaking%d",i];
            UIImage *image = [UIImage imageNamed:imageStr];
            [beginArray addObject:image];
        }
        stopArray = [NSMutableArray arrayWithCapacity:4];
        for (int i = 2; i < 4; i++) {
            NSString *imageStr = [NSString stringWithFormat:@"btn_speaker_speaking%d",i];
            UIImage *image = [UIImage imageNamed:imageStr];
            [stopArray addObject:image];
        }
    }
    return self;
}

- (void)beginSpeaking {
    self.isSpeaking = YES;
    [self startAnimationWithImages:beginArray duration:1 repeatCount:0];
}

- (void)stopSpeaking {
    if (self.isSpeaking == YES) {
        [self startAnimationWithImages:stopArray duration:1 repeatCount:1];
    }
}

- (void)startAnimationWithImages:(NSArray *)images
                        duration:(NSTimeInterval)duration
                     repeatCount:(NSInteger)repeatCount {
    self.imageView.animationImages = images;
    self.imageView.animationDuration = duration;
    self.imageView.animationRepeatCount = repeatCount;
    if (!self.imageView.isAnimating) {
        [self.imageView startAnimating];
        self.isSpeaking = NO;
    }
}

@end

