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

import com.huawei.game.common.utils.CollectionUtils;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.LocalAudioClipStateInfo;
import com.huawei.game.gmme.model.VolumeInfo;
import com.huawei.game.gmme.model.rtm.DeleteRtmChannelPlayerPropertiesResult;
import com.huawei.game.gmme.model.rtm.DeleteRtmChannelPropertiesResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelHistoryMessagesResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelInfoResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelPlayerPropertiesResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelPropertiesResult;
import com.huawei.game.gmme.model.rtm.PublishRtmChannelMessageResult;
import com.huawei.game.gmme.model.rtm.PublishRtmPeerMessageResult;
import com.huawei.game.gmme.model.rtm.ReceiveRtmChannelMessageNotify;
import com.huawei.game.gmme.model.rtm.ReceiveRtmPeerMessageNotify;
import com.huawei.game.gmme.model.rtm.RtmChannelPlayerPropertiesNotify;
import com.huawei.game.gmme.model.rtm.RtmChannelPropertiesNotify;
import com.huawei.game.gmme.model.rtm.RtmConnectionStatusNotify;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPlayerPropertiesResult;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPropertiesResult;
import com.huawei.game.gmme.model.rtm.SubscribeRtmChannelResult;
import com.huawei.game.gmme.model.rtm.UnSubscribeRtmChannelResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用户实现的统一回调
 *
 * @since 2023-04-10
 */
public class GMMECallbackHandler implements IGameMMEEventHandler {
    private static final String TAG = GMMECallbackHandler.class.getSimpleName();

    private List<IGameMMEEventHandler> mHandler = new CopyOnWriteArrayList<>();

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

    /**
     * 创建或者加入范围房间回调
     *
     * @param roomId 房间ID
     * @param code 结果码
     * @param msg 处理结果信息
     */
    @Override
    public void onJoinRangeRoom(String roomId, int code, String msg) {
        StringBuilder sb = new StringBuilder("onJoinRangeRoom : ").append("roomId=")
            .append(roomId)
            .append(", code=")
            .append(code)
            .append(", msg=")
            .append(msg);
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onJoinRangeRoom(roomId, code, msg);
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
        if (CollectionUtils.isNotEmpty(openIds)) {
            for (IGameMMEEventHandler handler : mHandler) {
                handler.onSpeakersDetection(openIds);
            }
        }
    }

    @Override
    public void onSpeakersDetectionEx(List<VolumeInfo> userVolumeInfos) {
        LogUtil.d(TAG, "onSpeakersDetectionEx : userVolumeInfos=" + userVolumeInfos);
        if (CollectionUtils.isNotEmpty(userVolumeInfos)) {
            for (IGameMMEEventHandler handler : mHandler) {
                handler.onSpeakersDetectionEx(userVolumeInfos);
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
        LogUtil.i(TAG, "onVoiceToText text " + text + "status " + status + "message " + message);
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

    @Override
    public void onRemoteMicroStateChanged(String roomId, String openId, boolean isMute) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onRemoteMicroStateChanged(roomId, openId, isMute);
        }
    }

    /**
     * 播放本地音效文件状态回调。
     *
     * @param localAudioClipStateInfo 音频文件状态对象
     */
    @Override
    public void onAudioClipStateChangedNotify(LocalAudioClipStateInfo localAudioClipStateInfo) {
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onAudioClipStateChangedNotify(localAudioClipStateInfo);
        }
    }

    /**
     * 录制语音消息回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onRecordAudioMsg(String filePath, int code, String msg) {
        if (code != 0) {
            LogUtil.e(TAG, "onRecordAudioMsg! filePath:" + filePath + ", code: " + code + ", msg: " + msg);
            return;
        }

        LogUtil.i(TAG, "onRecordAudioMsg success! filePath:" + filePath);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onRecordAudioMsg(filePath, code, msg);
        }
    }

    /**
     * 上传录制语音消息文件回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param fileId 待下载文件唯一标识
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onUploadAudioMsgFile(String filePath, String fileId, int code, String msg) {
        if (code != 0) {
            LogUtil.e(TAG, "onUploadAudioMsgFile! filePath:" + filePath + ", fileId:" + fileId + ", code: " + code
                + ", msg: " + msg);
            return;
        }

        LogUtil.i(TAG, "onUploadAudioMsgFile success! filePath:" + filePath + ", fileId:" + fileId);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onUploadAudioMsgFile(filePath, fileId, code, msg);
        }
    }

    /**
     * 下载录制语音消息文件回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param fileId 待下载文件唯一标识
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onDownloadAudioMsgFile(String filePath, String fileId, int code, String msg) {
        if (code != 0) {
            LogUtil.e(TAG, "onDownloadAudioMsgFile! filePath:" + filePath + ", fileId:" + fileId + ", code: " + code
                + ", msg: " + msg);
            return;
        }

        LogUtil.i(TAG, "onDownloadAudioMsgFile success! filePath:" + filePath + ", fileId:" + fileId);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onDownloadAudioMsgFile(filePath, fileId, code, msg);
        }
    }

    /**
     * 播放语音消息文件回调。
     *
     * @param filePath 待上传的语音文件的地址
     * @param code 响应码
     * @param msg 响应消息
     */
    @Override
    public void onPlayAudioMsg(String filePath, int code, String msg) {
        LogUtil.i(TAG, "onPlayAudioMsg! filePath:" + filePath + ", code: " + code + ", msg: " + msg);
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPlayAudioMsg(filePath, code, msg);
        }
    }

    /**
     * 订阅RTM频道回调
     *
     * @param result 订阅RTM频道结果
     */
    @Override
    public void onSubscribeRtmChannel(SubscribeRtmChannelResult result) {
        LogUtil.i(TAG, "onSubscribeRtmChannel! channelId:" + result.getChannelId() + ", code: " + result.getCode()
            + ", msg: " + result.getMsg());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onSubscribeRtmChannel(result);
        }
    }

    /**
     * 取消订阅RTM频道回调
     *
     * @param result 取消订阅RTM频道结果
     */
    @Override
    public void onUnSubscribeRtmChannel(UnSubscribeRtmChannelResult result) {
        LogUtil.i(TAG, "onUnSubscribeRtmChannel! channelId:" + result.getChannelId() + ", code: " + result.getCode()
            + ", msg: " + result.getMsg());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onUnSubscribeRtmChannel(result);
        }
    }

    /**
     * 发布RTM频道消息回调
     *
     * @param result 发布RTM频道消息结果
     */
    @Override
    public void onPublishRtmChannelMessage(PublishRtmChannelMessageResult result) {
        LogUtil.i(TAG, "onPublishRtmChannelMessage! " + ", code: " + result.getCode());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPublishRtmChannelMessage(result);
        }
    }

    /**
     * 发布RTM点对点消息回调
     *
     * @param result 发布RTM点对点消息结果
     */
    @Override
    public void onPublishRtmPeerMessage(PublishRtmPeerMessageResult result) {
        LogUtil.i(TAG, "onPublishRtmPeerMessage! " + ", code: " + result.getCode());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onPublishRtmPeerMessage(result);
        }
    }

    /**
     * 获取RTM频道信息回调
     *
     * @param result 获取RTM频道信息结果
     */
    @Override
    public void onGetRtmChannelInfo(GetRtmChannelInfoResult result) {
        LogUtil.i(TAG, "onGetRtmChannelInfo! " + ", channelId: " + result.getChannelId() + ", code: "
                + result.getCode() + ", message: " + result.getMsg());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onGetRtmChannelInfo(result);
        }
    }

    /**
     * 接收RTM频道信息通知
     *
     * @param notify 接收RTM频道信息结果
     */
    @Override
    public void onReceiveRtmChannelMessage(ReceiveRtmChannelMessageNotify notify) {
        LogUtil.i(TAG, "onReceiveRtmChannelMessage! " + ", channelId: " + notify.getChannelId() + ", clientId: "
            + notify.getClientMsgId() + ", msgId: " + notify.getServerMsgId());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onReceiveRtmChannelMessage(notify);
        }
    }

    /**
     * 接收RTM点对点信息通知
     *
     * @param notify 接收RTM点对点信息结果
     */
    @Override
    public void onReceiveRtmPeerMessage(ReceiveRtmPeerMessageNotify notify) {
        LogUtil.i(TAG,
            "onReceiveRtmPeerMessage! clientId: " + notify.getClientMsgId() + ", msgId: " + notify.getServerMsgId());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onReceiveRtmPeerMessage(notify);
        }
    }

    /**
     * RTM连接状态通知
     *
     * @param notify RTM连接状态结果
     */
    @Override
    public void onRtmConnectionChanged(RtmConnectionStatusNotify notify) {
        StringBuilder sb = new StringBuilder("onRtmConnectionChanged : ").append("status=")
            .append(notify.getStatus())
            .append(", reason=")
            .append(notify.getReason());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onRtmConnectionChanged(notify);
        }
    }

    /**
     * 设置频道内玩家属性结果回调
     *
     * @param result 设置频道内玩家属性结果
     */
    @Override
    public void onSetRtmChannelPlayerProperties(SetRtmChannelPlayerPropertiesResult result) {
        StringBuilder sb =
            new StringBuilder("onGetRtmChannelPlayerProperties : ").append("channelId=").append(result.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onSetRtmChannelPlayerProperties(result);
        }
    }

    /**
     * 查询频道内玩家属性结果回调
     *
     * @param result 查询频道内玩家属性结果
     */
    @Override
    public void onGetRtmChannelPlayerProperties(GetRtmChannelPlayerPropertiesResult result) {
        StringBuilder sb =
            new StringBuilder("onGetRtmChannelPlayerProperties : ").append("channelId=").append(result.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onGetRtmChannelPlayerProperties(result);
        }
    }

    /**
     * 删除频道内玩家属性结果回调
     *
     * @param result 删除频道内玩家属性结果
     */
    @Override
    public void onDeleteRtmChannelPlayerProperties(DeleteRtmChannelPlayerPropertiesResult result) {
        StringBuilder sb = new StringBuilder("onDeleteRtmChannelPlayerProperties : ").append("channelId=")
            .append(result.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onDeleteRtmChannelPlayerProperties(result);
        }
    }

    /**
     * 设置频道属性结果回调
     *
     * @param result 设置频道属性结果
     */
    @Override
    public void onSetRtmChannelProperties(SetRtmChannelPropertiesResult result) {
        StringBuilder sb =
            new StringBuilder("onSetRtmChannelProperties : ").append("channelId=").append(result.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onSetRtmChannelProperties(result);
        }
    }

    /**
     * 查询频道属性结果回调
     *
     * @param result 查询频道属性结果
     */
    @Override
    public void onGetRtmChannelProperties(GetRtmChannelPropertiesResult result) {
        StringBuilder sb =
            new StringBuilder("onGetRtmChannelProperties : ").append("channelId=").append(result.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onGetRtmChannelProperties(result);
        }
    }

    /**
     * 删除频道属性结果回调
     *
     * @param result 删除频道属性结果
     */
    @Override
    public void onDeleteRtmChannelProperties(DeleteRtmChannelPropertiesResult result) {
        StringBuilder sb =
            new StringBuilder("onDeleteRtmChannelProperties : ").append("channelId=").append(result.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onDeleteRtmChannelProperties(result);
        }
    }

    /**
     * 查询频道历史消息回调
     *
     * @param result 查询频道历史消息结果
     */
    @Override
    public void onGetRtmChannelHistoryMessages(GetRtmChannelHistoryMessagesResult result) {
        LogUtil.i(TAG, "onGetRtmChannelHistoryMessages! " + ", channelId: " + result.getChannelId() + ", code: "
                + result.getCode() + ", message: " + result.getMsg());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onGetRtmChannelHistoryMessages(result);
        }
    }

    /**
     * 频道内玩家属性变更通知
     *
     * @param notify 频道内玩家属性变更
     */
    @Override
    public void onRtmChannelPlayerPropertiesChanged(RtmChannelPlayerPropertiesNotify notify) {
        StringBuilder sb = new StringBuilder("onRtmChannelPlayerPropertiesChanged : ").append("channelId=")
            .append(notify.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onRtmChannelPlayerPropertiesChanged(notify);
        }
    }

    /**
     * 频道属性变更通知
     *
     * @param notify 频道属性变更
     */
    @Override
    public void onRtmChannelPropertiesChanged(RtmChannelPropertiesNotify notify) {
        StringBuilder sb =
            new StringBuilder("onRtmChannelPropertiesChanged : ").append("channelId=").append(notify.getChannelId());
        LogUtil.d(TAG, sb.toString());
        for (IGameMMEEventHandler handler : mHandler) {
            handler.onRtmChannelPropertiesChanged(notify);
        }
    }
}