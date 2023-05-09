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

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.gmmesdk.demo.Constant;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.handler.MemberEventClick;

import pl.droidsonroids.gif.GifImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 与会者列表adapter
 */
public class RoomMembersAdapter extends RecyclerView.Adapter {
    /**
     * 成员事件监听器（开启、关闭禁言）
     */
    private MemberEventClick mEventClick;

    /**
     * 成员列表
     */
    private List<String> mEntityList;

    /**
     * 房主ID
     */
    private String ownerId;

    /**
     * 本地用户
     */
    private String localUsr;

    /**
     * 本地多媒体玩家ID
     */
    private String localPlayerId;

    /**
     * 初始屏蔽状态
     */
    private Map<String, Boolean> muteState;

    /**
     * 初始禁言状态
     */
    private Map<String, Boolean> forbidState;

    /**
     * 全员屏蔽图标
     */
    private ImageView muteAllImg;

    /**
     * 禁言所有人复选框
     */
    private ImageView forbidAllImg;

    /**
     * 房间类型
     */
    private Integer roomType;

    /**
     * 正在发言的人
     */

    private List<String> speakingPlayers;

    /**
     * 构造方法
     *
     * @param eventClick 点击事件
     * @param ownerId 房主ID
     * @param user 用户ID
     * @param playerId 多媒体玩家ID
     * @param roomActivityInfo 房间信息
     */
    public RoomMembersAdapter(MemberEventClick eventClick, String ownerId, String user, String playerId,
        Integer roomType, RoomActivityInfo roomActivityInfo) {
        mEventClick = eventClick;
        localUsr = user;
        localPlayerId = playerId;
        this.ownerId = ownerId;
        this.roomType = roomType;
        if (roomActivityInfo == null) {
            mEntityList = new ArrayList<>();
            muteState = new HashMap<>();
            forbidState = new HashMap<>();
            forbidAllImg = null;
            muteAllImg = null;
        } else {
            mEntityList = roomActivityInfo.getRoomMemberList();
            muteState = roomActivityInfo.getMuteState();
            forbidState = roomActivityInfo.getForbidState();
            forbidAllImg = roomActivityInfo.getForbidAllImg();
            muteAllImg = roomActivityInfo.getMuteAllImg();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View view =
            LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_room_member, viewGroup, false);
        return new RoomMemberRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String entity = mEntityList.get(position);
        RoomMemberRecyclerHolder viewHolder = ((RoomMemberRecyclerHolder) holder);
        if (ownerId != null && ownerId.equals(entity)) {
            viewHolder.owner.setVisibility(View.VISIBLE);
        } else {
            viewHolder.owner.setVisibility(View.GONE);
        }
        viewHolder.userName.setText(entity);
        viewHolder.userId = entity;
        if (roomType == Constant.NATIONALROOM) {
            viewHolder.forbidImg.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.forbidImg.setVisibility(View.VISIBLE);
        }
        viewHolder.muteImg.setVisibility(View.VISIBLE);
        handleMutePlayer(viewHolder);
        handleForbidPlayer(viewHolder);
        if (muteState.containsKey(entity)) {
            if (muteState.get(entity)) {
                viewHolder.muteImg.setTag(Constant.UN_SELECT);
                viewHolder.muteImg.setImageResource(R.drawable.btn_mic_off);
            } else {
                viewHolder.muteImg.setTag(Constant.SELECT);
                viewHolder.muteImg.setImageResource(R.drawable.btn_mic_on);
            }
        }
        if (forbidState.containsKey(entity)) {
            if (forbidState.get(entity)) {
                viewHolder.forbidImg.setTag(Constant.UN_SELECT);
                viewHolder.forbidImg.setImageResource(R.drawable.btn_speaker_off);
            } else {
                viewHolder.forbidImg.setTag(Constant.SELECT);
                viewHolder.forbidImg.setImageResource(R.drawable.btn_speaker_on);
            }
        }
        if (speakingPlayers != null && speakingPlayers.contains(entity)) {
            viewHolder.muteImg.setImageResource(R.drawable.btn_speaker_speaking);
        }
        viewHolder.muteImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if (viewHolder.userId.equals(localPlayerId)) {
                    return;
                }
                if (viewHolder.muteImg.getTag().equals(Constant.UN_SELECT)) {
                    mEventClick.muteRemote(viewHolder.userId, false);
                } else {
                    mEventClick.muteRemote(viewHolder.userId, true);
                }
            }
        });
        viewHolder.forbidImg.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if (!localPlayerId.equals(ownerId)) {
                    return;
                }
                if (viewHolder.forbidImg.getTag().equals(Constant.UN_SELECT)) {
                    mEventClick.mutePlayer(viewHolder.userId, false);
                } else {
                    mEventClick.mutePlayer(viewHolder.userId, true);
                }
            }
        });
    }

    private void handleMutePlayer(RoomMemberRecyclerHolder viewHolder) {
        if (muteAllImg == null) {
            viewHolder.muteImg.setImageResource(R.drawable.btn_mic_on);
            viewHolder.muteImg.setTag(Constant.SELECT);
        } else {
            if (muteAllImg.getTag().equals(Constant.UN_SELECT)) {
                if (muteState.containsKey(viewHolder.userId) && muteState.get(viewHolder.userId)) {
                    viewHolder.muteImg.setTag(Constant.UN_SELECT);
                    viewHolder.muteImg.setImageResource(R.drawable.btn_mic_off);
                } else if (speakingPlayers.contains(viewHolder.userId)) {
                    viewHolder.muteImg.setImageResource(R.drawable.btn_speaker_speaking);
                }
            } else {
                viewHolder.muteImg.setTag(Constant.SELECT);
                viewHolder.muteImg.setImageResource(R.drawable.btn_mic_on);
            }
        }
    }

    private void handleForbidPlayer(RoomMemberRecyclerHolder viewHolder) {
        if (forbidAllImg == null) {
            viewHolder.forbidImg.setImageResource(R.drawable.btn_speaker_on);
            viewHolder.forbidImg.setTag(Constant.SELECT);
        } else {
            if (forbidAllImg.getTag().equals(Constant.UN_SELECT)) {
                if (forbidState.containsKey(viewHolder.userId) && forbidState.get(viewHolder.userId)) {
                    viewHolder.forbidImg.setTag(Constant.UN_SELECT);
                    viewHolder.forbidImg.setImageResource(R.drawable.btn_speaker_off);
                }
            } else {
                viewHolder.forbidImg.setTag(Constant.SELECT);
                viewHolder.forbidImg.setImageResource(R.drawable.btn_speaker_on);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEntityList.size();
    }

    public void addPlayer(String openId) {
        if (mEntityList.contains(openId)) {
            return;
        }
        mEntityList.add(openId);
        notifyDataSetChanged();
    }

    public void removePlayer(String openId) {
        if (mEntityList.contains(openId)) {
            mEntityList.remove(openId);
        }
        notifyDataSetChanged();
    }

    public void updatePlayer(List<String> roomMemberList, Map<String, Boolean> muteState,
        Map<String, Boolean> forbidState, Integer roomType) {
        if (roomMemberList != null) {
            mEntityList = roomMemberList;
        }
        this.muteState = muteState;
        this.forbidState = forbidState;
        this.roomType = roomType;
        notifyDataSetChanged();
    }

    public void updateSpeakingUsers(List<String> playerIds) {
        speakingPlayers = playerIds;
        notifyDataSetChanged();
    }

    public void updateOwnerId(String ownerId) {
        this.ownerId = ownerId;
        notifyDataSetChanged();
    }

    public void refreshMuteState(Map<String, Boolean> muteState) {
        this.muteState = muteState;
        notifyDataSetChanged();
    }

    public void refreshForbidState(Map<String, Boolean> forbidState) {
        this.forbidState = forbidState;
        notifyDataSetChanged();
    }

    private static class RoomMemberRecyclerHolder extends RecyclerView.ViewHolder {
        private TextView userName;

        private GifImageView muteImg;

        private ImageView forbidImg;

        private String userId;

        private TextView owner;

        public RoomMemberRecyclerHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.item_user_id);
            muteImg = itemView.findViewById(R.id.img_audio);
            forbidImg = itemView.findViewById(R.id.img_speaker);
            owner = itemView.findViewById(R.id.owner);
        }
    }
}