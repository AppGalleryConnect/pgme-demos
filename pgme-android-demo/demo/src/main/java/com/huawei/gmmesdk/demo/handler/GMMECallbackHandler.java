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

package com.huawei.gmmesdk.demo.handler;

import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户实现的统一回调
 */
public class GMMECallbackHandler implements IGameMMEEventHandler {
    private static final String TAG = "RtcEventHandler";

    private ArrayList<IGameMMEEventHandler> mHandler = new ArrayList<>();

    @Override
    public void onCreate(int code, String msg) {
        LogUtil.d(TAG, "onCreate : code=" + code + ", msg=" + msg);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onCreate(code, msg);
        }
    }

    @Override
    public void onMuteAllPlayers(String roomId, List<String> openIds, boolean isMuted, int code, String msg) {
        StringBuilder sb = new StringBuilder("onMuteAllPlayers : ").append("roomId=")
            .append(roomId)
            .append(", openIds=")
            .append(openIds)
            .append(", isMuted=")
            .append(isMuted)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onMuteAllPlayers(roomId, openIds, isMuted, code, msg);
        }
    }

    @Override
    public void onMutePlayer(String roomId, String openId, boolean isMuted, int code, String msg) {
        StringBuilder sb = new StringBuilder("onMutePlayer : ").append("roomId=")
            .append(roomId)
            .append(", openId=")
            .append(openId)
            .append(", isMuted=")
            .append(isMuted)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onMutePlayer(roomId, openId, isMuted, code, msg);
        }
    }

    @Override
    public void onJoinTeamRoom(String roomId, int code, String msg) {
        StringBuilder sb = new StringBuilder("onJoinTeamRoom : ").append("roomId=")
            .append(roomId)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onJoinTeamRoom(roomId, code, msg);
        }
    }

    @Override
    public void onJoinNationalRoom(String roomId, int code, String msg) {
        StringBuilder sb = new StringBuilder("onJoinNationalRoom : ").append("roomId=")
            .append(roomId)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onJoinNationalRoom(roomId, code, msg);
        }
    }

    @Override
    public void onSwitchRoom(String roomId, int code, String msg) {
        StringBuilder sb = new StringBuilder("onSwitchRoom : ").append("roomId=")
            .append(roomId)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onSwitchRoom(roomId, code, msg);
        }
    }

    @Override
    public void onDestroy(int code, String message) {
        LogUtil.d(TAG, "onDestroy : msg=" + message);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onDestroy(code, message);
        }
    }

    @Override
    public void onLeaveRoom(String roomId, int code, String msg) {
        LogUtil.d(TAG, "onLeaveRoom : status=" + code + ", msg=" + msg);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onLeaveRoom(roomId, code, msg);
        }
    }

    @Override
    public void onSpeakersDetection(List<String> openIds) {
        LogUtil.d(TAG, "onSpeakersDetection : openIds=" + openIds);
        if (openIds != null && !openIds.isEmpty()) {
            for (IGameMMEEventHandler handler : mHandler) {
                handler.onSpeakersDetection(openIds);
            }
        }
    }

    @Override
    public void onForbidAllPlayers(String roomId, List<String> openIds, boolean isForbidden, int code, String msg) {
        StringBuilder sb = new StringBuilder("onForbidAllPlayers : ").append("roomId=")
            .append(roomId)
            .append(", openIds=")
            .append(openIds)
            .append(", isForbidden=")
            .append(isForbidden)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onForbidAllPlayers(roomId, openIds, isForbidden, code, msg);
        }
    }

    @Override
    public void onForbidPlayer(String roomId, String openId, boolean isForbidden, int code, String msg) {
        StringBuilder sb = new StringBuilder("onForbidPlayer : ").append("roomId=")
            .append(roomId)
            .append(", openId=")
            .append(openId)
            .append(", isForbidden=")
            .append(isForbidden)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onForbidPlayer(roomId, openId, isForbidden, code, msg);
        }
    }

    @Override
    public void onForbiddenByOwner(String roomId, List<String> openIds, boolean isForbidden) {
        StringBuilder sb = new StringBuilder("onForbiddenByOwner : ").append("roomId=")
            .append(roomId)
            .append(", openIds=")
            .append(openIds)
            .append(", isForbidden=")
            .append(isForbidden);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onForbiddenByOwner(roomId, openIds, isForbidden);
        }
    }

    @Override
    public void onVoiceToText(String text, int status, String message) {
        LogUtil.i(TAG, "onVoiceToText" + "status" + status + "message" + message);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onVoiceToText(text, status, message);
        }
    }

    @Override
    public void onPlayerOnline(String roomId, String openId) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPlayerOnline(roomId, openId);
        }
    }

    @Override
    public void onPlayerOffline(String roomId, String openId) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPlayerOffline(roomId, openId);
        }
    }

    @Override
    public void onTransferOwner(String roomId, int code, String msg) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onTransferOwner(roomId, code, msg);
        }
    }

    public void addHandler(IGameMMEEventHandler handler) {
        LogUtil.i(TAG, "addHandler! ");
        mHandler.add(handler);
    }

    public void removeHandler(IGameMMEEventHandler handler) {
        LogUtil.i(TAG, "removeHandler! ");
        mHandler.remove(handler);
    }
}
