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

#import "MatrixTool.h"

@implementation MatrixTool

+ (NSArray *)getRotateMatrix:(NSInteger)right up:(NSInteger)up forward:(NSInteger)forward {
    
    NSArray *row1 = [NSArray arrayWithObjects:[NSNumber numberWithDouble:[self cos:forward] * [self cos:up]],
                                              [NSNumber numberWithDouble:[self sin:forward] * [self cos:right] + [self cos:forward] * [self sin:up] * [self sin:right]],
                                              [NSNumber numberWithDouble:[self sin:forward] * [self sin:right] - [self cos:forward] * [self sin:up] * [self cos:right]], nil];
    NSArray *row2 = [NSArray arrayWithObjects:[NSNumber numberWithDouble:-[self sin:forward] * [self cos:up]],
                                              [NSNumber numberWithDouble:[self cos:forward] * [self cos:right] - [self sin:forward] * [self sin:up] * [self sin:right]],
                                              [NSNumber numberWithDouble:[self cos:forward] * [self sin:right] + [self sin:forward] * [self sin:up] * [self cos:right]], nil];
    NSArray *row3 = [NSArray arrayWithObjects:[NSNumber numberWithDouble:[self sin:up]],
                                              [NSNumber numberWithDouble:-[self cos:up] * [self sin:right]],
                                              [NSNumber numberWithDouble:[self cos:up] * [self cos:right]], nil];
    return [NSArray arrayWithObjects:row1, row2, row3, nil];
}

+ (double)cos:(NSInteger)theta {
    return cos(theta * 3.14 / 180);
}

+ (double)sin:(NSInteger)theta {
    return sin(theta * 3.14 / 180);
}
@end
