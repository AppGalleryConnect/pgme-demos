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

package com.huawei.gmmesdk.demo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.constant.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 添加属性弹窗
 *
 * @since 2023-10-26
 */
public class SetPropertyPopup extends PopupWindow implements View.OnClickListener {

    /**
     * 上下文
     */
    private final Context context;

    /**
     * 设置的属性类型
     */
    private final Constant.PropertyType type;

    /**
     * 属性 map
     */
    private final Map<String, String> propertyMap;

    /**
     * 输入区域
     */
    private LinearLayout llInputArea;

    /**
     * 添加属性按钮
     */
    private Button btnAdd;

    /**
     * 确定按钮
     */
    private Button btnOk;

    /**
     * 取消按钮
     */
    private Button btnCancel;

    /**
     * 属性设置 listener，回调数据给调用方
     */
    private OnPropertyChangedListener onPropertyChangedListener;

    public SetPropertyPopup(Context context, Constant.PropertyType type, Map<String, String> propertyMap) {
        super(context);
        setFocusable(true);
        setOutsideTouchable(false);

        this.context = context;
        this.type = type;
        this.propertyMap = propertyMap;

        initView();
        initListener();
        initInputArea();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        View contentView = LayoutInflater.from(this.context).inflate(R.layout.layout_set_property, null, false);
        llInputArea = contentView.findViewById(R.id.ll_input_area);
        btnAdd = contentView.findViewById(R.id.btn_add);
        btnOk = contentView.findViewById(R.id.btn_ok);
        btnCancel = contentView.findViewById(R.id.btn_cancel);
        setContentView(contentView);
    }

    /**
     * 初始化 listener
     */
    private void initListener() {
        btnAdd.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                addInputItem("", "");
                break;

            case R.id.btn_ok:
                int count = llInputArea.getChildCount();
                View viewInputItem;
                EditText etKey;
                EditText etValue;
                Map<String, String> propertyMap = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    viewInputItem = llInputArea.getChildAt(i);
                    etKey = viewInputItem.findViewById(R.id.et_property_key);
                    etValue = viewInputItem.findViewById(R.id.et_property_value);
                    propertyMap.put(etKey.getText().toString(), etValue.getText().toString());
                }
                if (onPropertyChangedListener != null) {
                    onPropertyChangedListener.onPropertyChanged(type, propertyMap);
                }
                dismiss();
                break;

            case R.id.btn_cancel:
                dismiss();
                break;

            default:
                break;
        }
    }

    /**
     * 初始化输入区域
     */
    private void initInputArea() {
        if (propertyMap == null) {
            return;
        }
        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            addInputItem(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 添加输入项
     * 
     * @param key 键
     * @param value 值
     */
    private void addInputItem(String key, String value) {
        View viewInputItem = LayoutInflater.from(this.context).inflate(R.layout.item_set_property, llInputArea, false);
        EditText etKey = viewInputItem.findViewById(R.id.et_property_key);
        EditText etValue = viewInputItem.findViewById(R.id.et_property_value);
        Button btnDelete = viewInputItem.findViewById(R.id.btn_delete);
        etKey.setText(key);
        etValue.setText(value);
        btnDelete.setOnClickListener(v -> llInputArea.removeView(viewInputItem));
        llInputArea.addView(viewInputItem);
    }

    /**
     * 自定义属性数据回调
     */
    public interface OnPropertyChangedListener {
        void onPropertyChanged(Constant.PropertyType type, Map<String, String> propertyMap);
    }

    public void setOnPropertyChangedListener(OnPropertyChangedListener listener) {
        this.onPropertyChangedListener = listener;
    }
}
