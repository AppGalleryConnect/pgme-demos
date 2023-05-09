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

#import "UITextField+HWTextField.h"

@implementation UITextField (HWTextField)

- (instancetype)init
{
    self = [super init];
    if (self) {
        // 给textfield添加一个空白view 使其光标向右偏移
        UIView *blankView = [[UIView alloc] initWithFrame:CGRectMake(self.frame.origin.x,self.frame.origin.y,15.0, self.frame.size.height)];
        self.leftView = blankView;
        self.leftViewMode = UITextFieldViewModeAlways;
    }
    return self;
}

@end
