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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.huawei.gmmesdk.demo.log.Log.LogNode;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文本输出视图
 */
@SuppressLint("AppCompatCustomView")
public class LogView extends TextView implements LogNode {

    private LogNode outputText;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public LogView(Context context) {
        super(context);
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param attrs 属性集合
     */
    public LogView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param attrs 属性集合
     * @param defStyle 日志样式
     */
    public LogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LogNode getOutputText() {
        return outputText;
    }

    public void setOutputText(LogNode outputText) {
        this.outputText = outputText;
    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        final StringBuilder strBuilder = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        strBuilder.append(str);
        strBuilder.append(" ");
        strBuilder.append(msg);
        strBuilder.append("\r\n");

        ((Activity) getContext()).runOnUiThread((new Thread(new Runnable() {
            @Override
            public void run() {
                appendToLog(strBuilder.toString());
            }
        })));

        if (outputText != null) {
            outputText.println(priority, tag, msg, tr);
        }
    }

    /**
     * 输出日志
     *
     * @param input 输出
     */
    public void appendToLog(String input) {
        append("\n" + input);
    }
}
