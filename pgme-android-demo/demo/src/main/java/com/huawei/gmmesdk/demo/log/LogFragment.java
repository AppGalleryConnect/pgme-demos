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

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.huawei.gmmesdk.demo.constant.Constant;

/**
 * 日志视图
 *
 * @since 2023-04-10
 */
public class LogFragment extends Fragment {
    private ScrollView scrollView;

    private LogView logView;

    /**
     * 构造方法
     */
    public LogFragment() {
    }

    private View initViews() {
        if (getActivity() == null) {
            return null;
        }

        logView = new LogView(getActivity());
        logView.setClickable(true);
        scrollView = new ScrollView(getActivity());
        scrollView.setLayoutParams(
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.addView(logView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (Constant.IS_LOG == 0) {
            scrollView.setVisibility(View.GONE);
        }
        return scrollView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = initViews();
        logView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        final GestureDetector detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                logView.setText("");
                return true;
            }
        });

        logView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

        return result;
    }

    public LogView getLogView() {
        return logView;
    }
}
