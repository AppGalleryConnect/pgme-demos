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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.exception.GmmeErrCode;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.AudioMsgFileInfo;
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
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.constant.Constant;
import com.huawei.gmmesdk.demo.log.Log;
import com.huawei.gmmesdk.demo.util.RandomUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioMsgActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnTouchListener, IGameMMEEventHandler {
    /**
     * 日志标签
     */
    private static final String AUDIO_MSG_TAG = PlayerPositionActivity.class.getSimpleName();

    protected LinearLayout audioMsgList;

    protected Button btnBack;

    protected Button btnRecord;

    protected LinearLayout mAudioView;

    protected TextView mAudioViewTip;

    protected TextView mSpeakerAudioTime;

    /**
     * 申请权限请求码
     */
    private static final int REQUEST_PERMISSIONS_CODE = 0X002;

    /**
     * HRTCEngine
     */
    protected GameMediaEngine mHwRtcEngine;

    private static long startTime = 0;

    private static final long MILLIS_IN_FUTURE = 51000L;

    private static final long COUNT_DOWN_INTERVAL = 1000L;

    private static boolean IS_SEND = false;

    private static Map<String, View> viewsMap = new ConcurrentHashMap<>();

    private static Map<String, String> remotePlayMap = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GmmeApplication) getApplication()).registerEventHandler(this);
        mHwRtcEngine = ((GmmeApplication) getApplication()).getEngine();
        setContentView(R.layout.activity_audio_msg);

        audioMsgList = this.findViewById(R.id.audioMsgList);
        btnBack = this.findViewById(R.id.btnBack);
        btnRecord = this.findViewById(R.id.btnRecord);
        mAudioView = this.findViewById(R.id.audio_view);
        mAudioViewTip = this.findViewById(R.id.audio_view_tip);
        mSpeakerAudioTime = this.findViewById(R.id.speaker_audio_time);

        btnBack.setOnClickListener(this);
        btnRecord.setOnTouchListener(this);
        btnRecord.setFocusable(true);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                back();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.btnRecord:
                // 语音录制处理
                onAudioTouchHandle(event);
                break;
        }
        return false;
    }

    private boolean checkRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)) {
                // 如果权限未获取，则申请权限
                requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 录音倒计时显示
     */
    CountDownTimer timer = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (mSpeakerAudioTime != null) {
                mSpeakerAudioTime.setText("还剩" + millisUntilFinished / 1000 + "秒");
            }
        }

        @Override
        public void onFinish() {
            IS_SEND = true;
            mAudioViewTip.setText("");
            mHwRtcEngine.stopRecordAudioMsg();
            mAudioView.setVisibility(View.GONE);
            cancel();
        }
    };

    private void onAudioTouchHandle(MotionEvent event) {
        Log.i(AUDIO_MSG_TAG, "onAudioTouchHandle");
        float y1 = 0;
        float y2;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!checkRecordAudioPermission()) {
                return;
            }
            startTime = System.currentTimeMillis();
            y1 = event.getY();
            mAudioViewTip.setText(getString(R.string.recording));
            mAudioView.setVisibility(View.VISIBLE);
            String filePath = null;
            try {
                filePath =
                    getExternalCacheDir().getCanonicalPath() + "/" + RandomUtil.getRandomNum() + Constant.AUDIO_TYPE;
            } catch (IOException e) {
                LogUtil.e(AUDIO_MSG_TAG, e.getMessage());
            }
            mHwRtcEngine.startRecordAudioMsg(filePath);
            timer.start();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            y2 = event.getY();
            mAudioViewTip.setText(y1 - y2 > 50 ? getString(R.string.cancel_send_audio) : getString(R.string.recording));
            return;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!checkRecordAudioPermission()) {
                return;
            }
            y2 = event.getY();
            if (timer != null) {
                timer.cancel();
            }
            if (System.currentTimeMillis() - startTime > 1000) {
                IS_SEND = y1 - y2 < 50;
                mHwRtcEngine.stopRecordAudioMsg();
            } else {
                IS_SEND = false;
                mHwRtcEngine.stopRecordAudioMsg();
                Toast.makeText(getApplicationContext(), getString(R.string.speak_short), Toast.LENGTH_SHORT).show();
            }
            mAudioViewTip.setText("");
            mAudioView.setVisibility(View.GONE);
        }
    }

    private void uploadAudioMsg(String filePath) {
        try {
            mHwRtcEngine.uploadAudioMsgFile(getExternalCacheDir().getCanonicalPath() + "/" + filePath, 5000);
        } catch (IOException e) {
            LogUtil.e(AUDIO_MSG_TAG, e.getMessage());
        }
    }

    private void downloadAudioMsg(String filePath) {
        String fileId = remotePlayMap.get(filePath);
        try {
            filePath = getExternalCacheDir().getCanonicalPath() + "/" + RandomUtil.getRandomNum() + Constant.AUDIO_TYPE;
            mHwRtcEngine.downloadAudioMsgFile(fileId, filePath, 5000);
        } catch (IOException e) {
            LogUtil.e(AUDIO_MSG_TAG, e.getMessage());
        }
    }

    private void localPlay(String filePath) {
        try {
            filePath = getExternalCacheDir().getCanonicalPath() + "/" + filePath;
            mHwRtcEngine.playAudioMsg(filePath);
        } catch (IOException e) {
            LogUtil.e(AUDIO_MSG_TAG, e.getMessage());
        }
    }

    private void downloadPlay(String filePath) {
        try {
            filePath = getExternalCacheDir().getCanonicalPath() + "/" + filePath;
            mHwRtcEngine.playAudioMsg(filePath);
        } catch (IOException e) {
            LogUtil.e(AUDIO_MSG_TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewsMap.clear();
        remotePlayMap.clear();
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
        if (IS_SEND) {
            runOnUiThread(() -> {
                View view = LayoutInflater.from(this).inflate(R.layout.audio_msg_item, null);

                TextView localFilePath = view.findViewById(R.id.localFilePath);
                TextView remoteFilePath = view.findViewById(R.id.remoteFilePath);
                TextView uploadSuccess = view.findViewById(R.id.uploadSuccess);

                Button btnUploadAudioMsg = view.findViewById(R.id.btnUploadAudioMsg);
                Button btnDownloadAudioMsg = view.findViewById(R.id.btnDownloadAudioMsg);
                Button btnLocalPlay = view.findViewById(R.id.btnLocalPlay);
                Button btnDownloadPlay = view.findViewById(R.id.btnDownloadPlay);

                btnUploadAudioMsg.setOnClickListener(v -> uploadAudioMsg(localFilePath.getText().toString()));
                btnDownloadAudioMsg.setOnClickListener(v -> downloadAudioMsg(localFilePath.getText().toString()));
                btnLocalPlay.setOnClickListener(v -> localPlay(localFilePath.getText().toString()));
                btnDownloadPlay.setOnClickListener(v -> downloadPlay(remoteFilePath.getText().toString()));

                String fileSubPath = splitFilePath(filePath);
                viewsMap.put(buildKey(fileSubPath, "localFilePath"), localFilePath);
                viewsMap.put(buildKey(fileSubPath, "remoteFilePath"), remoteFilePath);
                viewsMap.put(buildKey(fileSubPath, "uploadSuccess"), uploadSuccess);

                viewsMap.put(buildKey(fileSubPath, "btnUploadAudioMsg"), btnUploadAudioMsg);
                viewsMap.put(buildKey(fileSubPath, "btnDownloadAudioMsg"), btnDownloadAudioMsg);
                viewsMap.put(buildKey(fileSubPath, "btnDownloadPlay"), btnDownloadPlay);

                localFilePath.setText(fileSubPath);
                audioMsgList.addView(view);
            });
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
        AudioMsgFileInfo audioMsgFileInfo = mHwRtcEngine.getAudioMsgFileInfo(filePath);
        String fileSubPath = splitFilePath(filePath);
        remotePlayMap.put(fileSubPath, fileId);
        TextView remoteFilePath = (TextView) viewsMap.get(buildKey(fileSubPath, "remoteFilePath"));
        Button btnDownloadAudioMsg = (Button) viewsMap.get(buildKey(fileSubPath, "btnDownloadAudioMsg"));
        Button btnDownloadPlay = (Button) viewsMap.get(buildKey(fileSubPath, "btnDownloadPlay"));
        TextView uploadSuccess = (TextView) viewsMap.get(buildKey(fileSubPath, "uploadSuccess"));
        Button btnUploadAudioMsg = (Button) viewsMap.get(buildKey(fileSubPath, "btnUploadAudioMsg"));
        viewsMap.put(buildKey(fileId, "remoteFilePath"), remoteFilePath);
        viewsMap.put(buildKey(fileId, "btnDownloadAudioMsg"), btnDownloadAudioMsg);
        viewsMap.put(buildKey(fileId, "btnDownloadPlay"), btnDownloadPlay);
        runOnUiThread(() -> {
            if (uploadSuccess != null && btnUploadAudioMsg != null && btnDownloadAudioMsg != null) {
                btnDownloadAudioMsg.setVisibility(View.VISIBLE);
                btnUploadAudioMsg.setVisibility(View.GONE);
                uploadSuccess.setVisibility(View.VISIBLE);
            }
        });
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
        runOnUiThread(() -> {
            TextView remoteFilePath = (TextView) viewsMap.get(buildKey(fileId, "remoteFilePath"));
            Button btnDownloadAudioMsg = (Button) viewsMap.get(buildKey(fileId, "btnDownloadAudioMsg"));
            Button btnDownloadPlay = (Button) viewsMap.get(buildKey(fileId, "btnDownloadPlay"));
            if (btnDownloadAudioMsg != null && btnDownloadPlay != null && remoteFilePath != null) {
                remoteFilePath.setText(splitFilePath(filePath));
                btnDownloadAudioMsg.setVisibility(View.GONE);
                btnDownloadPlay.setVisibility(View.VISIBLE);
            }
        });
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
        if (code == GmmeErrCode.AUDIO_MSG_PLAY_CURRENT_UNFINISHED.getCode()) {
            mHwRtcEngine.stopPlayAudioMsg();
        }
        if (code == GmmeErrCode.AUDIO_MSG_PLAY_LAST_UNFINISHED.getCode()) {
            mHwRtcEngine.stopPlayAudioMsg();
            mHwRtcEngine.playAudioMsg(filePath);
        }
    }

    private String splitFilePath(String filePath) {
        String[] fileSplit = filePath.split("/");
        return fileSplit.length > 0 ? fileSplit[fileSplit.length - 1] : "";
    }

    private String buildKey(String filePath, String viewType) {
        return filePath + "," + viewType;
    }

    private void back() {
        finish();
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

    }

    @Override
    public void onGetRtmChannelInfo(GetRtmChannelInfoResult result) {

    }

    @Override
    public void onReceiveRtmChannelMessage(ReceiveRtmChannelMessageNotify notify) {

    }

    @Override
    public void onReceiveRtmPeerMessage(ReceiveRtmPeerMessageNotify notify) {

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
