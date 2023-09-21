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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.huawei.gmmesdk.demo.R;
import java.util.List;

/**
 * 房间ID下拉列表页面
 *
 * @since 2023-04-10
 */

public class RoomIdsAdapter extends RecyclerView.Adapter<RoomIdsAdapter.ViewHolder> {
    /**
     * 房间ID列表
     */
    private List<String> roomIdList;

    @NonNull
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(String roomId);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomId;

        public ViewHolder(View view) {
            super(view);
            roomId = (TextView) view.findViewById(R.id.text);
        }
    }

    public RoomIdsAdapter(List<String> list) {
        roomIdList = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View view =
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        holder.roomId.setOnClickListener(view1 -> {
            int position = holder.getAbsoluteAdapterPosition();
            String roomId = roomIdList.get(position);
            mOnItemClickListener.OnItemClick(roomId);
        });
        return holder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        String roomId = roomIdList.get(position);
        holder.roomId.setText(roomId);
    }

    public int getItemCount() {
        return roomIdList.size();
    }
}

