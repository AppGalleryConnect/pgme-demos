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

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面房间信息
 */
public class RoomActivityInfo {

    private ImageView muteAllImg;

    private ImageView forbidAllImg;

    private List<String> roomMemberList = new ArrayList<>();

    private Map<String, Boolean> muteState = new HashMap<>();

    private Map<String, Boolean> forbidState = new HashMap<>();

    /**
     * 房间页面信息，传入成员列表页面
     *
     * @param roomMemberList  成员列表
     * @param muteStates      屏蔽状态
     * @param forbidStates    禁言状态
     * @param muteImageView   屏蔽图标
     * @param forbidImageView 禁言图标
     */
    public RoomActivityInfo(List<String> roomMemberList, Map<String, Boolean> muteStates,
                            Map<String, Boolean> forbidStates, ImageView muteImageView, ImageView forbidImageView) {

        muteState = muteStates;
        forbidState = forbidStates;
        muteAllImg = muteImageView;
        forbidAllImg = forbidImageView;
        this.roomMemberList = roomMemberList;
    }

    public ImageView getMuteAllImg() {
        return muteAllImg;
    }

    public void setMuteAllImg(ImageView muteAllImg) {
        this.muteAllImg = muteAllImg;
    }

    public List<String> getRoomMemberList() {
        return roomMemberList;
    }

    public void setRoomMemberList(List<String> roomMemberList) {
        this.roomMemberList = roomMemberList;
    }

    public Map<String, Boolean> getMuteState() {
        return muteState;
    }

    public void setMuteState(Map<String, Boolean> muteState) {
        this.muteState = muteState;
    }

    public ImageView getForbidAllImg() {
        return forbidAllImg;
    }

    public void setForbidAllImg(ImageView forbidAllImg) {
        this.forbidAllImg = forbidAllImg;
    }

    public Map<String, Boolean> getForbidState() {
        return forbidState;
    }

    public void setForbidState(Map<String, Boolean> forbidState) {
        this.forbidState = forbidState;
    }
}
