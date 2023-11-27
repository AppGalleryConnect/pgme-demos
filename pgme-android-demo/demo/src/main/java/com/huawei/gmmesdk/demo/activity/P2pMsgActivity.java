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

import static com.huawei.gmmesdk.demo.constant.Constant.RtmMessageType.MSG_TYPE_TEXT;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.game.common.constants.GmmeConstants;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
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
import com.huawei.game.gmme.model.rtm.PublishRtmPeerMessageReq;
import com.huawei.game.gmme.model.rtm.PublishRtmPeerMessageResult;
import com.huawei.game.gmme.model.rtm.ReceiveRtmChannelMessageNotify;
import com.huawei.game.gmme.model.rtm.ReceiveRtmPeerMessageNotify;
import com.huawei.game.gmme.model.rtm.RtmChannelPlayerPropertiesNotify;
import com.huawei.game.gmme.model.rtm.RtmChannelPropertiesNotify;
import com.huawei.game.gmme.model.rtm.RtmConnectionStatusNotify;
import com.huawei.game.gmme.model.rtm.RtmMessageContent;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPlayerPropertiesResult;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPropertiesResult;
import com.huawei.game.gmme.model.rtm.SubscribeRtmChannelResult;
import com.huawei.game.gmme.model.rtm.UnSubscribeRtmChannelResult;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.constant.Constant;
import com.huawei.gmmesdk.demo.util.KeyboardUtil;
import com.huawei.gmmesdk.demo.util.LogMonitorUtil;
import com.huawei.gmmesdk.demo.util.UserUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class P2pMsgActivity extends AppCompatActivity implements IGameMMEEventHandler {

    private static final String TAG = P2pMsgActivity.class.getSimpleName();

    /**
     * HRTCEngine
     */
    private GameMediaEngine mHwRtcEngine;

    /**
     * 用户名文本框
     */
    private TextView tvSender;

    /**
     * 发送文本消息按钮
     */
    private Button btnSendText;

    /**
     * 发送二进制消息按钮
     */
    private Button btnSendBinary;

    /**
     * 接收者输入框
     */
    private EditText etReceiver;

    /**
     * 消息内容输入框
     */
    private EditText etContent;

    /**
     * 日志打印文本框
     */
    private TextView tvLogMonitor;

    /**
     * 发送消息的临时缓存
     */
    private final Map<String, RtmMessageContent> msgCacheMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p_msg);

        ((GmmeApplication) getApplication()).registerEventHandler(this);
        mHwRtcEngine = ((GmmeApplication) getApplication()).getEngine();

        initView();
    }

    @Override
    protected void onDestroy() {
        ((GmmeApplication) getApplication()).removeEventHandler(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        ((GmmeApplication) getApplication()).removeEventHandler(this);
        super.finish();
    }

    private void initView() {
        tvSender = findViewById(R.id.tv_sender);
        btnSendText = findViewById(R.id.btn_send_text);
        btnSendBinary = findViewById(R.id.btn_send_binary);
        etReceiver = findViewById(R.id.et_receiver);
        etContent = findViewById(R.id.et_content);
        tvLogMonitor = findViewById(R.id.tv_log_monitor);

        tvSender.setText(UserUtil.getOpenId());

        // 发送文本消息
        btnSendText.setOnClickListener(v -> {
            sendMessage(Constant.RtmMessageType.MSG_TYPE_TEXT);
            etContent.setText("");
            KeyboardUtil.hideSoftKeyboard(this, etContent);
        });

        // 发送二进制消息
        btnSendBinary.setOnClickListener(v -> {
            sendMessage(Constant.RtmMessageType.MSG_TYPE_BYTE);
            etContent.setText("");
            KeyboardUtil.hideSoftKeyboard(this, etContent);
        });
    }

    private void sendMessage(int msgType) {
        String receiver = etReceiver.getText().toString();
        if (TextUtils.isEmpty(receiver)) {
            Toast.makeText(P2pMsgActivity.this, "receiver is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(P2pMsgActivity.this, "content is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        PublishRtmPeerMessageReq req = new PublishRtmPeerMessageReq();
        req.setPeerId(receiver);
        req.setMessageType(msgType);
        req.setMessageString(content);
        req.setMessageBytes(content.getBytes(StandardCharsets.UTF_8));
        String clientMsgId = mHwRtcEngine.publishRtmPeerMessage(req);
        msgCacheMap.put(clientMsgId, req);
    }

    /**
     * 添加日志输出
     * 
     * @param textToAdd 要添加的内容
     */
    private void appendLog2MonitorView(String textToAdd) {
        String logText = tvLogMonitor.getText().toString();
        logText = LogMonitorUtil.appendLog(logText, textToAdd);
        LogMonitorUtil.updateLog(tvLogMonitor, logText);
    }

    @Override
    public void onCreate(int code, String msg) {

    }

    @Override
    public void onMutePlayer(String roomId, String openId, boolean isMuted, int code, String msg) {

    }

    @Override
    public void onMuteAllPlayers(String roomId, List<String> openIds, boolean isMuted, int code, String msg) {

    }

    @Override
    public void onJoinTeamRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onJoinNationalRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onJoinRangeRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onSwitchRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onLeaveRoom(String roomId, int code, String msg) {

    }

    @Override
    public void onSpeakersDetection(List<String> openIds) {

    }

    @Override
    public void onSpeakersDetectionEx(List<VolumeInfo> userVolumeInfos) {

    }

    @Override
    public void onForbidAllPlayers(String roomId, List<String> openIds, boolean isForbidden, int code, String msg) {

    }

    @Override
    public void onForbidPlayer(String roomId, String openId, boolean isForbidden, int code, String msg) {

    }

    @Override
    public void onForbiddenByOwner(String roomId, List<String> openIds, boolean isForbidden) {

    }

    @Override
    public void onVoiceToText(String text, int code, String msg) {

    }

    @Override
    public void onPlayerOnline(String roomId, String openId) {

    }

    @Override
    public void onPlayerOffline(String roomId, String openId) {

    }

    @Override
    public void onTransferOwner(String roomId, int code, String msg) {

    }

    @Override
    public void onRemoteMicroStateChanged(String roomId, String openId, boolean isMute) {

    }

    @Override
    public void onAudioClipStateChangedNotify(LocalAudioClipStateInfo localAudioClipStateInfo) {

    }

    @Override
    public void onDestroy(int code, String msg) {
        LogUtil.i(TAG, "onDestroy : message=" + msg);
        finish();
    }

    @Override
    public void onRecordAudioMsg(String filePath, int code, String msg) {

    }

    @Override
    public void onUploadAudioMsgFile(String filePath, String fileId, int code, String msg) {

    }

    @Override
    public void onDownloadAudioMsgFile(String filePath, String fileId, int code, String msg) {

    }

    @Override
    public void onPlayAudioMsg(String filePath, int code, String msg) {

    }

    @Override
    public void onSubscribeRtmChannel(SubscribeRtmChannelResult result) {

    }

    @Override
    public void onUnSubscribeRtmChannel(UnSubscribeRtmChannelResult result) {

    }

    @Override
    public void onPublishRtmChannelMessage(PublishRtmChannelMessageResult result) {

    }

    @Override
    public void onPublishRtmPeerMessage(PublishRtmPeerMessageResult result) {
        runOnUiThread(() -> {
            RtmMessageContent rtmMessage = msgCacheMap.remove(result.getClientMsgId());
            String type = "";
            String content = "";
            if (rtmMessage != null) {
                type = rtmMessage.getMessageType() == MSG_TYPE_TEXT ? "文本" : "二进制";
                content = rtmMessage.getMessageType() == MSG_TYPE_TEXT ? rtmMessage.getMessageString()
                    : new String(rtmMessage.getMessageBytes(), StandardCharsets.UTF_8);
            }
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(
                    String.format(Locale.getDefault(), "点对点消息（%s）：%s发送的消息：%s，发送失败，clientMsgId=%s，code=%d，msg=%s", type,
                        UserUtil.getOpenId(), content, result.getClientMsgId(), result.getCode(), result.getMsg()));
                return;
            }
            appendLog2MonitorView(
                String.format(Locale.getDefault(), "点对点消息（%s）：%s发送的消息：%s", type, UserUtil.getOpenId(), content));
        });
    }

    @Override
    public void onGetRtmChannelInfo(GetRtmChannelInfoResult result) {

    }

    @Override
    public void onReceiveRtmChannelMessage(ReceiveRtmChannelMessageNotify notify) {

    }

    @Override
    public void onReceiveRtmPeerMessage(ReceiveRtmPeerMessageNotify notify) {
        runOnUiThread(() -> {
            int messageType = notify.getMessageType();
            String type = messageType == MSG_TYPE_TEXT ? "文本" : "二进制";
            String content = messageType == MSG_TYPE_TEXT ? notify.getMessageString()
                : new String(notify.getMessageBytes(), StandardCharsets.UTF_8);
            appendLog2MonitorView(String.format(Locale.getDefault(),
                "点对点消息（%s）：%s收到%s的消息：%s", type, UserUtil.getOpenId(), notify.getSenderId(), content));
        });

    }

    @Override
    public void onRtmConnectionChanged(RtmConnectionStatusNotify notify) {

    }

    @Override
    public void onSetRtmChannelPlayerProperties(SetRtmChannelPlayerPropertiesResult result) {

    }

    @Override
    public void onGetRtmChannelPlayerProperties(GetRtmChannelPlayerPropertiesResult result) {

    }

    @Override
    public void onDeleteRtmChannelPlayerProperties(DeleteRtmChannelPlayerPropertiesResult result) {

    }

    @Override
    public void onSetRtmChannelProperties(SetRtmChannelPropertiesResult result) {

    }

    @Override
    public void onGetRtmChannelProperties(GetRtmChannelPropertiesResult result) {

    }

    @Override
    public void onDeleteRtmChannelProperties(DeleteRtmChannelPropertiesResult result) {

    }

    @Override
    public void onGetRtmChannelHistoryMessages(GetRtmChannelHistoryMessagesResult result) {

    }

    @Override
    public void onRtmChannelPlayerPropertiesChanged(RtmChannelPlayerPropertiesNotify notify) {

    }

    @Override
    public void onRtmChannelPropertiesChanged(RtmChannelPropertiesNotify notify) {

    }
}