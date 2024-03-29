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

package com.huawei.gmmesdk.demo.log;

import com.huawei.gmmesdk.demo.log.Log.LogNode;

/**
 * 日志封装
 *
 * @since 2023-04-10
 */
public class LogCatWrapper implements LogNode {
    private LogNode logView;

    public LogNode getLogView() {
        return logView;
    }

    public void setLogView(LogNode node) {
        logView = node;
    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        String useMsg = msg;
        if (useMsg == null) {
            useMsg = "";
        }

        if (tr != null) {
            useMsg += System.lineSeparator() + android.util.Log.getStackTraceString(tr);
        }

        android.util.Log.println(priority, tag, useMsg);

        if (logView != null) {
            logView.println(priority, tag, msg, tr);
        }
    }
}
