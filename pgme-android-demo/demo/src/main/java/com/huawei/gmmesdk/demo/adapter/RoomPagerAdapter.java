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

package com.huawei.gmmesdk.demo.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

/**
 * 日志和房间成员adapter
 *
 * @since 2023-04-10
 */
public class RoomPagerAdapter extends PagerAdapter {
    private ArrayList<View> pageView;

    public RoomPagerAdapter(ArrayList<View> pageView) {
        this.pageView = pageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (pageView.get(position) != null) {
            container.removeView(pageView.get(position));
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pageView.get(position));
        return pageView.get(position);
    }

    @Override
    public int getCount() {
        return pageView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }
}
