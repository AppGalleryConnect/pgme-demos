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

#import "HWTools.h"

@implementation HWTools

+ (void)showMessage:(NSString *)string {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        __block UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"提示：" message:string preferredStyle:UIAlertControllerStyleAlert];
        UIApplication *application = [UIApplication sharedApplication];
        [application.keyWindow.rootViewController presentViewController:alertController animated:YES completion:nil];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [alertController dismissViewControllerAnimated:YES completion:nil];
        });
    });
}

+ (NSString *)nowDate {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    return [formatter stringFromDate:[NSDate date]];
}

+ (NSString *)sign:(NSString *)dataToSign withPrivateKey: (NSString *) privateKey {
    NSData *keyData = [[NSData alloc]initWithBase64EncodedString:privateKey options:0];
    NSMutableDictionary *dicPrikey = [[NSMutableDictionary alloc]initWithCapacity:2];
   [dicPrikey setObject:(__bridge id)kSecAttrKeyTypeRSA forKey:(__bridge id)kSecAttrKeyType];
   [dicPrikey setObject:(__bridge id) kSecAttrKeyClassPrivate forKey:(__bridge id)kSecAttrKeyClass];
   
    CFErrorRef error = NULL;
    SecKeyRef privateKeyRef = SecKeyCreateWithData((__bridge CFDataRef)keyData, (__bridge CFDictionaryRef) dicPrikey, &error);
    if (privateKeyRef == NULL) {
        NSLog(@"init privateKey failed");
        return nil;
    }

    NSData *data = [dataToSign dataUsingEncoding:NSUTF8StringEncoding];
    CFDataRef cfSign = SecKeyCreateSignature(privateKeyRef, kSecKeyAlgorithmRSASignatureMessagePSSSHA256, (__bridge CFDataRef)data, &error);
    if (cfSign == NULL) {
        NSLog(@"init sign failed");
        CFRelease(privateKeyRef);
        return nil;
    }

    NSData *sign = (__bridge_transfer NSData *)cfSign;
    NSString* signStr = [sign base64EncodedStringWithOptions:0];
    CFRelease(privateKeyRef);
    return signStr;
}

@end
