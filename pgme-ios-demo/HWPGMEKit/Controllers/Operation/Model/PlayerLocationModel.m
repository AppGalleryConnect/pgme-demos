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

#import "PlayerLocationModel.h"

@implementation PositionModel
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeInteger:self.forward forKey:@"forward"];
    [coder encodeInteger:self.right forKey:@"right"];
    [coder encodeInteger:self.up forKey:@"up"];
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    if (self = [super init]) {
        self.forward = [coder decodeIntegerForKey:@"forward"];
        self.right = [coder decodeIntegerForKey:@"right"];
        self.up = [coder decodeIntegerForKey:@"up"];
    }
    return self;
}
@end

@implementation AxisModel
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeInteger:self.forward forKey:@"forward"];
    [coder encodeInteger:self.right forKey:@"right"];
    [coder encodeInteger:self.up forKey:@"up"];
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    if (self = [super init]) {
        self.forward = [coder decodeIntegerForKey:@"forward"];
        self.right = [coder decodeIntegerForKey:@"right"];
        self.up = [coder decodeIntegerForKey:@"up"];
    }
    return self;
}
@end

@implementation SelfLocationModel
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.openId forKey:@"playerId"];
    [coder encodeObject:self.position forKey:@"position"];
    [coder encodeObject:self.axis forKey:@"axis"];
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    if (self = [super init]) {
        self.openId = [coder decodeObjectForKey:@"playerId"];
        self.position = [coder decodeObjectForKey:@"position"];
        self.axis = [coder decodeObjectForKey:@"axis"];
    }
    return self;
}
@end

@implementation OtherLocationModel
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.openId forKey:@"playerId"];
    [coder encodeObject:self.position forKey:@"position"];
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    if (self = [super init]) {
        self.openId = [coder decodeObjectForKey:@"playerId"];
        self.position = [coder decodeObjectForKey:@"position"];
    }
    return self;
}
@end
