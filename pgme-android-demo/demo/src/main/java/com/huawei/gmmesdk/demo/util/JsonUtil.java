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

import android.text.TextUtils;

import com.huawei.gmmesdk.demo.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * json串工具类
 */
public class JsonUtil {

    /**
     * 日志标签
     */
    private static final String JSON_UTIL = "JsonUtil";

    /**
     * 判断是否为json字符串
     *
     * @param content 文本内容
     * @return 对象不为空是json串，对象为空不是json串
     */
    public static JSONObject stringToJsonObject(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        if (!content.startsWith("{") || !content.endsWith("}")) {
            return null;
        }
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            Log.e(JSON_UTIL, e.getMessage());
            return null;
        }
    }

}
