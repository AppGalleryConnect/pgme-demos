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

package com.huawei.gmmesdk.demo.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Spinner 在 setSelection 时如果 position 和当前一致，不会回调 onItemSelected（即使是数据源已发生改变），
 * 自定义 Spinner 以确保 setSelection 都会调用 onItemSelected
 */
@SuppressLint("AppCompatCustomView")
public class RepeatSelectionSpinner extends Spinner {

    public RepeatSelectionSpinner(Context context) {
        super(context);
    }

    public RepeatSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepeatSelectionSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        if (position == getSelectedItemPosition()) {
            OnItemSelectedListener listener = getOnItemSelectedListener();
            if (listener != null) {
                listener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == getSelectedItemPosition()) {
            OnItemSelectedListener listener = getOnItemSelectedListener();
            if (listener != null) {
                listener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    }
}