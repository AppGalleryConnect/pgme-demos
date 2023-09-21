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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.huawei.game.common.utils.StringUtil;
import com.huawei.gmmesdk.demo.R;

/**
 * 玩家位置弹出框
 *
 * @since 2023-08-11
 */
public class PlayPositionDialog extends PopupWindow {

    private EditText editText;

    private Button btnConfirm;

    private Button btnCancel;

    protected CallBackData mCallBackData;

    public PlayPositionDialog(Context context, String placeholder, String value, int dialogType) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.player_position_dialog, null, false);
        setFocusable(true);
        setOutsideTouchable(false);
        editText = contentView.findViewById(R.id.edittext_dialog);
        btnConfirm = contentView.findViewById(R.id.btn_confirm);
        btnCancel = contentView.findViewById(R.id.btn_cancel);

        editText.setHint(placeholder);
        editText.setHintTextColor(context.getResources().getColor(R.color.white));

        if (!StringUtil.isEmpty(value)) {
            editText.setText(value);
        }

        btnConfirm.setOnClickListener(v -> {
            if (editText != null && editText.getText() != null) {
                mCallBackData.onPlayerPositionParam(editText.getText().toString(), dialogType);
            }
            this.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            this.dismiss();
        });

        // 显示视图文件
        setContentView(contentView);
    }

    // 回调方法
    public interface CallBackData {
        // 回调玩家位置的参数信息
        void onPlayerPositionParam(String param, int dialogType);
    }

    public void setOnDataListener(CallBackData mCallBackData) {
        this.mCallBackData = mCallBackData;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
