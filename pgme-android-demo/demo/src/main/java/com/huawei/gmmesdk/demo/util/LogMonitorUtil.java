/*
   Copyright 2023. Huawei Technologies Co., Ltd. All rights reserved.

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

import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志工具类
 *
 * @since 2023-11-01
 */
public class LogMonitorUtil {
    /**
     * 添加日志内容到开头
     * 
     * @param originalText 原始文本
     * @param textToAdd 待添加的文本
     * @return 拼接后的文本
     */
    public static String appendLog(String originalText, String textToAdd) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        StringBuilder builder = new StringBuilder();
        builder.append(time).append(" ").append(textToAdd);
        if (originalText != null && !originalText.isEmpty()) {
            builder.append(System.lineSeparator()).append(System.lineSeparator()).append(originalText);
        }
        return builder.toString();
    }

    /**
     * 更新日志输出文本框
     * 
     * @param tvLogMonitor 日志输出文本框
     * @param logText 日志内容
     */
    public static void updateLog(TextView tvLogMonitor, String logText) {
        tvLogMonitor.post(() -> {
            tvLogMonitor.setText(logText);
            ScrollView svLogHost = (ScrollView) tvLogMonitor.getParent().getParent();
            svLogHost.post(() -> {
                tvLogMonitor.requestFocus();
                svLogHost.fullScroll(View.FOCUS_UP);
            });
        });
    }
}
