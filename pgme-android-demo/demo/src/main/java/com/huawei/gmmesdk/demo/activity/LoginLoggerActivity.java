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

package com.huawei.gmmesdk.demo.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.log.Log;
import com.huawei.gmmesdk.demo.log.LogCatWrapper;
import com.huawei.gmmesdk.demo.log.LogFragment;

/**
 * 登录界面日志输出
 */
public class LoginLoggerActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        initializeLogging();
    }

    private void initializeLogging() {
        LogFragment logFragment = (LogFragment) getFragmentManager().findFragmentById(R.id.login_text_out);
        LogCatWrapper logcat = new LogCatWrapper();
        logcat.setLogView(logFragment.getLogView());
        Log.setLogNode(logcat);
    }
}
