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

package com.huawei.demo;

import android.content.Context;

import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.EngineCreateParams;
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
import com.huawei.gmmesdk.demo.BuildConfig;
import com.huawei.gmmesdk.demo.util.SignerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.List;

public class GameMMETestUtil {
    public static final String openid = "luoplayer";

    protected IMyHandler handler;
    IGameMMEEventHandler ghandler = new IGameMMEEventHandler() {
        @Override
        public void onCreate(int i, String s) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", i);
                jsonObject.put("msg", s);
                handler.onCallback("onCreate", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMutePlayer(String s, String s1, boolean b, int i, String s2) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("openId", s1);
                jsonObject.put("isMuted", b);
                jsonObject.put("code", i);
                jsonObject.put("msg", s2);
                handler.onCallback("onMutePlayer", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMuteAllPlayers(String s, List<String> list, boolean b, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("openIds", list);
                jsonObject.put("isMuted", b);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onMuteAllPlayers", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onJoinTeamRoom(String s, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onJoinTeamRoom", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onJoinNationalRoom(String s, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onJoinNationalRoom", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /**
         * 创建或者加入范围房间回调
         *
         * @param roomId 房间ID
         * @param code   结果码
         * @param msg    处理结果信息
         */
        @Override
        public void onJoinRangeRoom(String roomId, int code, String msg) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", roomId);
                jsonObject.put("code", code);
                jsonObject.put("msg", msg);
                handler.onCallback("onJoinRangeRoom", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSwitchRoom(String s, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onSwitchRoom", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLeaveRoom(String s, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onLeaveRoom", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSpeakersDetection(List<String> list) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("openIds", list);
                handler.onCallback("onSpeakersDetection", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /**
         * 正在输出音频用户
         *
         * @param userVolumeInfos 多媒体用户输出音频信息列表
         */
        @Override
        public void onSpeakersDetectionEx(List<VolumeInfo> userVolumeInfos) {

        }

        @Override
        public void onForbidAllPlayers(String s, List<String> list, boolean b, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("openIds", list);
                jsonObject.put("isForbidden", b);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onForbidAllPlayers", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onForbidPlayer(String s, String s1, boolean b, int i, String s2) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("openId", s1);
                jsonObject.put("isForbidden", b);
                jsonObject.put("code", i);
                jsonObject.put("msg", s2);
                handler.onCallback("onForbidPlayer", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onForbiddenByOwner(String s, List<String> list, boolean b) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("openId", list);
                jsonObject.put("isForbidden", b);
                handler.onCallback("onForbiddenByOwner", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onVoiceToText(String s, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("text", s);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onVoiceToText", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onPlayerOnline(String s, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("openId", s1);
                handler.onCallback("onPlayerOnline", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onPlayerOffline(String s, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("openId", s1);
                handler.onCallback("onPlayerOffline", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onTransferOwner(String s, int i, String s1) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("roomId", s);
                jsonObject.put("code", i);
                jsonObject.put("msg", s1);
                handler.onCallback("onTransferOwner", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onDestroy(int i, String s) {
            handler.onCallback("onDestroy", s);
        }

        /**
         * 远端麦克风设备状态变更回调。
         *
         * @param roomId 房间ID
         * @param openId 远端用户
         * @param isMute 是否设置麦克风禁言
         */
        @Override
        public void onRemoteMicroStateChanged(String roomId, String openId, boolean isMute) {

        }

        /**
         * 播放本地音效文件状态回调。
         *
         * @param localAudioClipStateInfo 音频文件状态对象
         */
        @Override
        public void onAudioClipStateChangedNotify(LocalAudioClipStateInfo localAudioClipStateInfo) {

        }

        /**
         * 录制语音消息回调。
         *
         * @param filePath 待上传的语音文件的地址
         * @param code     响应码
         * @param msg      响应消息
         */
        @Override
        public void onRecordAudioMsg(String filePath, int code, String msg) {

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

        }

        /**
         * 播放语音消息文件回调。
         *
         * @param filePath 待上传的语音文件的地址
         * @param code     响应码
         * @param msg      响应消息
         */
        @Override
        public void onPlayAudioMsg(String filePath, int code, String msg) {

        }

        /**
         * 订阅RTM频道回调
         *
         * @param result 订阅RTM频道结果
         */
        @Override
        public void onSubscribeRtmChannel(SubscribeRtmChannelResult result) {

        }

        /**
         * 取消订阅RTM频道回调
         *
         * @param result 取消订阅RTM频道结果
         */
        @Override
        public void onUnSubscribeRtmChannel(UnSubscribeRtmChannelResult result) {

        }

        /**
         * 发布RTM频道消息回调
         *
         * @param result 发布RTM频道消息结果
         */
        @Override
        public void onPublishRtmChannelMessage(PublishRtmChannelMessageResult result) {

        }

        /**
         * 发布RTM点对点消息回调
         *
         * @param result 发布RTM点对点消息结果
         */
        @Override
        public void onPublishRtmPeerMessage(PublishRtmPeerMessageResult result) {

        }

        /**
         * 获取RTM频道信息回调
         *
         * @param result 获取RTM频道信息结果
         */
        @Override
        public void onGetRtmChannelInfo(GetRtmChannelInfoResult result) {

        }

        /**
         * 接收RTM频道信息通知
         *
         * @param notify 接收RTM频道信息结果
         */
        @Override
        public void onReceiveRtmChannelMessage(ReceiveRtmChannelMessageNotify notify) {

        }

        /**
         * 接收RTM点对点信息通知
         *
         * @param notify 接收RTM点对点信息结果
         */
        @Override
        public void onReceiveRtmPeerMessage(ReceiveRtmPeerMessageNotify notify) {

        }

        /**
         * RTM连接状态通知
         *
         * @param notify RTM连接状态结果
         */
        @Override
        public void onRtmConnectionChanged(RtmConnectionStatusNotify notify) {

        }

        /**
         * 设置频道内玩家属性结果回调
         *
         * @param result 设置频道内玩家属性结果
         */
        @Override
        public void onSetRtmChannelPlayerProperties(SetRtmChannelPlayerPropertiesResult result) {

        }

        /**
         * 查询频道内玩家属性结果回调
         *
         * @param result 查询频道内玩家属性结果
         */
        @Override
        public void onGetRtmChannelPlayerProperties(GetRtmChannelPlayerPropertiesResult result) {

        }

        /**
         * 删除频道内玩家属性结果回调
         *
         * @param result 删除频道内玩家属性结果
         */
        @Override
        public void onDeleteRtmChannelPlayerProperties(DeleteRtmChannelPlayerPropertiesResult result) {

        }

        /**
         * 设置频道属性结果回调
         *
         * @param result 设置频道属性结果
         */
        @Override
        public void onSetRtmChannelProperties(SetRtmChannelPropertiesResult result) {

        }

        /**
         * 查询频道属性结果回调
         *
         * @param result 查询频道属性结果
         */
        @Override
        public void onGetRtmChannelProperties(GetRtmChannelPropertiesResult result) {

        }

        /**
         * 删除频道属性结果回调
         *
         * @param result 删除频道属性结果
         */
        @Override
        public void onDeleteRtmChannelProperties(DeleteRtmChannelPropertiesResult result) {

        }

        /**
         * 查询频道历史消息回调
         *
         * @param result 查询频道历史消息结果
         */
        @Override
        public void onGetRtmChannelHistoryMessages(GetRtmChannelHistoryMessagesResult result) {

        }

        /**
         * 设置频道内玩家属性变更通知
         *
         * @param notify 设置频道内玩家属性变更
         */
        @Override
        public void onRtmChannelPlayerPropertiesChanged(RtmChannelPlayerPropertiesNotify notify) {

        }

        /**
         * 设置频道属性变更通知
         *
         * @param notify 设置频道属性变更
         */
        @Override
        public void onRtmChannelPropertiesChanged(RtmChannelPropertiesNotify notify) {

        }
    };

    public GameMMETestUtil() {

    }

    public GameMediaEngine init(Context context, IMyHandler handler, String playerid) {
        EngineCreateParams params = new EngineCreateParams();
        params.setOpenId(playerid == null ? openid : playerid); // 玩家ID
        params.setContext(context); // 应用的上下文
        params.setLogEnable(true); // 开启SDK日志记录
        params.setLogPath("mnt/sdcard/"); // 日志路径
        params.setLogSize(1024 * 10); // 日志存储大小
        params.setCountryCode("CN"); // 国家码，用于网关路由，不设置默认CN
        params.setAgcAppId(BuildConfig.agcAppId); // 游戏应用在AGC上注册的APP ID
        params.setClientId(BuildConfig.agcClientId); // 客户端ID
        params.setClientSecret(BuildConfig.agcClientSecret); // 客户端ID对应的秘钥
        params.setApiKey(BuildConfig.agcApiKey); // API秘钥（凭据）
        setAccessSign(params);

        this.handler = handler;
        return GameMediaEngine.create(params, ghandler);
    }

    private void setAccessSign(EngineCreateParams params) {
        String appId = BuildConfig.agcAppId;
        // 当前游戏密钥的获取方式仅做demo示例，开发者需要放到远端服务器下发给apk
        String gameSecret = BuildConfig.gameSecret;
        // 当前随机数方式仅做demo示例，开发者需要使用更安全的算法来生成随机数
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        int randomCode = Math.abs(random.nextInt());
        String nonce = String.valueOf(randomCode);
        String timestamp = String.valueOf(System.currentTimeMillis());
        params.setSign(SignerUtil.generate(appId, openid, nonce, timestamp, gameSecret));
        params.setNonce(nonce);
        params.setTimeStamp(timestamp);
    }

    public interface IMyHandler {
        void onCallback(String method, String value);
    }

}