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

@interface LoginLogTableView : UIView

/// 初始化引擎时，需要打印的日志信息
/// @param desc log信息
- (void)setLogData:(NSString *)desc;

/// 倒序打印日志
- (void)insertLogData:(NSString *)desc;

/// 清空所有日志
- (void)clearAllLogs;
@end

NS_ASSUME_NONNULL_END
