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

package com.huawei.gmmesdk.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huawei.gmmesdk.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单文本类型 Spinner 适配器
 */
public class SimpleSpinnerAdapter extends BaseAdapter {
    /**
     * 上下文
     */
    private final Context context;

    /**
     * 文本数据列表
     */
    private final List<String> textList = new ArrayList<>();

    public SimpleSpinnerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return textList.size();
    }

    @Override
    public String getItem(int position) {
        return textList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_simple_spinner, null);
            viewHolder = new ViewHolder();
            viewHolder.tv = convertView.findViewById(R.id.tv_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(getItem(position));
        return convertView;
    }

    public void setTextList(List<String> textList) {
        this.textList.clear();
        if (textList == null) {
            return;
        }
        this.textList.addAll(textList);
    }

    static class ViewHolder {
        private TextView tv;
    }
}