/*
   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.huawei.gmmesdk.demo.util;

import android.os.Build;

import com.huawei.gmmesdk.demo.log.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 随机函数工具类
 */
public class RandomUtil {

    /**
     * 日志标签
     */
    private static final String RANDOM_UTIL = "RandomUtil";

    /**
     * 获取int类型的随机函数
     * @return 随机数
     */
    public static int getRandomNum() {
        try {
            SecureRandom random;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 安全随机数
                 random = SecureRandom.getInstanceStrong();
            } else {
                // 当前随机数方式仅做demo示例，开发者需要使用更安全的算法来生成随机数
                random = new SecureRandom();
            }
            byte[] bytes = new byte[20];
            random.nextBytes(bytes);
            return Math.abs(random.nextInt());
        } catch (NoSuchAlgorithmException e) {
            Log.e(RANDOM_UTIL, e.getMessage());
        }
        return -1;
    }

}
