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

package com.huawei.gmmesdk.demo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.game.common.constants.SdkThreadPool;
import com.huawei.game.common.exception.CommonErrorCode;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.exception.GmmeErrCode;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.AudioMsgFileInfo;
import com.huawei.game.gmme.model.Player;
import com.huawei.game.gmme.model.Room;
import com.huawei.game.gmme.model.VolumeInfo;
import com.huawei.gmmesdk.demo.Constant;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.IMLogConstant;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.adapter.AudioConstant;
import com.huawei.gmmesdk.demo.adapter.ChatMsgAdapter;
import com.huawei.gmmesdk.demo.adapter.RoomActivityInfo;
import com.huawei.gmmesdk.demo.adapter.RoomIdsAdapter;
import com.huawei.gmmesdk.demo.adapter.RoomMembersAdapter;
import com.huawei.gmmesdk.demo.handler.MemberEventClick;
import com.huawei.gmmesdk.demo.log.Log;
import com.huawei.gmmesdk.demo.util.RandomUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * 应用房间界面
 */
public class GmmeRoomActivity extends BaseActivity implements MemberEventClick, IGameMMEEventHandler {

    /**
     * 日志标签
     */
    private static final String ROOM_TAG = "GmmeRoomActivity";

    private static final int OFFSET_Y = 600;

    private static final int CHAT_TYPE_1V1 = 1;

    private static final int CHAT_TYPE_GROUP = 2;

    private static final long MILLIS_IN_FUTURE = 51000L;

    private static final long COUNT_DOWN_INTERVAL = 1000L;

    private static final int LOCAL_CACHE_POSITION = -1;

    private static boolean IS_SEND = false;


    private static long startTime = 0;

    /**
     * 申请权限请求码
     */
    private static final int REQUEST_PERMISSIONS_CODE = 0X002;

    /**
     * 当前房间ID
     */
    protected String mRoomId;

    /**
     * 当前房间房主
     */
    protected String mOwnerId;

    protected TimerTask mTimerTask;

    @SuppressLint("HandlerLeak")
    Handler recyclerHandler = new Handler() {
        private void handleLeave(Message msg) {
            if (msg.obj == null) {
                return;
            }
            if (msg.obj != "") {
                mHwRtcEngine.switchRoom(msg.obj.toString());
                currentRoom.setText(msg.obj.toString());
            } else {
                roomIdView.setVisibility(View.GONE);
            }
        }

        private void handleSwitch(Message msg) {
            if (msg.obj == null) {
                return;
            }
            currentRoom.setText(msg.obj.toString());
            if (!mRoomId.equals(msg.obj)) {
                appendLog2MonitorView("switch room:" + "\n" + "user: " + mLocalUserId + "\n" + "oldRoomId: " + mRoomId
                    + "\n" + "newRoomId: " + msg.obj.toString());
                mHwRtcEngine.switchRoom(msg.obj.toString());
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case Constant.LEAVEROOM:
                    handleLeave(msg);
                    break;
                case Constant.SWITCHROOM:
                    handleSwitch(msg);
                    break;
                default:
                    break;
            }
        }
    };

    private AudioConstant audioConstant;

    private long mLastClickTime = 0L;

    private boolean isForbid = false;

    private boolean isMute = false;

    private int newOwner = -1;

    private int userChoiceIdentity = 0;

    private ImageView muteImg;

    private VoiceToTextPopup popup;

    private List<String> roomMemberBeansList = new ArrayList<>();

    private List<String> roomIdList = new ArrayList<>();

    private final List<String> channelIdList = new ArrayList<>();

    private Timer mTimer;

    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mTimer != null) {
                stopTimer();
            }
            startTimer();
        }
    };


    private int roomType;

    private int lastOperate = View.FOCUS_LEFT;

    private boolean voiceCheckBoxIsChecked = true;

    private boolean msgCheckBoxIsChecked = true;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            appendLog2MonitorView("status =" + msg.arg1 + ",message=" + msg.obj);
        }
    };

    private ChatMsgAdapter mChatMsgAdapter;

    private List<com.huawei.game.gmme.model.Message> mChatMsgDataList;

    private RecyclerView mChatMsgContentRv;

    private EditText mChatMsgRecIdEt;

    private Button mChatMsgSend;

    private EditText mChatMsgContentEt;

    private LinearLayout mMsgRecIdLl;

    private LinearLayout mAudioView;

    private TextView mAudioViewTip;

    private TextView mSpeakerAudioTime;


    /**
     * 当前麦克风的操作
     */
    private Constant.MicOperateTypeEnum currentMicOperate;

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

    private String currentChannelId;

    private final TextWatcher chatMsgInputListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (msgSingleRb.isChecked()) {
                mChatMsgSend.setEnabled(
                    !TextUtils.isEmpty(mChatMsgRecIdEt.getText()) && !TextUtils.isEmpty(mChatMsgContentEt.getText()));
            } else {
                mChatMsgSend.setEnabled(!TextUtils.isEmpty(s));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GmmeApplication) getApplication()).registerEventHandler(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 点击向上键
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.audio_view_send) {
            // 语音录制处理
            onAudioTouchHandle(event);
        }
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnQuit:
                onExitRoomClick();
                break;
            case R.id.hw_gmme_destroy:
                if (System.currentTimeMillis() - mLastClickTime >= Constant.TIME_INTERVAL) {
                    mLastClickTime = System.currentTimeMillis();
                    destroyGmme();
                }
                break;
            case R.id.voiceToText:
                onVoiceToTextClick();
                break;
            case R.id.checkEnableMic:
                onMuteAudioClicked(v);
                break;
            case R.id.room_id:
                onClickRoomId();
                break;
            case R.id.transfer_owner:
                onTransferOwnerClick();
                break;
            case R.id.btnJoin:
                if (voiceTeamRb.isChecked()) {
                    onEnterTeamRoom();
                } else {
                    if (userChoiceIdentity == 0) {
                        onEnterNationalRoom(Constant.JOINER);
                    } else {
                        onEnterNationalRoom(Constant.PLAYER);
                    }
                }
                break;
            case R.id.btnSendTextMsg:
                if (mHwRtcEngine == null) {
                    return;
                }
                String channelId = mRoomIdView.getText().toString();
                if (msgGroupRb.isChecked() && !TextUtils.isEmpty(channelId) && !channelIdList.contains(channelId)) {
                    appendLog2MonitorView("join group channel , channel id :" + channelId);
                    mHwRtcEngine.joinGroupChannel(channelId);
                    return;
                }
                if (msgGroupRb.isChecked()) {
                    currentChannelId = channelId;
                }
                createSendTextMsgView();
                break;
            case R.id.btnLeaveChannel:
                if (mHwRtcEngine == null) {
                    return;
                }
                appendLog2MonitorView("leave channel");
                leaveAllChannel();
                break;
            case R.id.rb_voice_war:
                onEnterNationalRoom();
                break;
            case R.id.msg_view_send:
                String content = mChatMsgContentEt.getText().toString();
                onSendChatMsg(content);
                break;
            default:
                break;
        }
    }

    private void onSendChatMsg(String content) {
        if (mHwRtcEngine == null) {
            return;
        }
        int type = mMsgRecIdLl.getVisibility() == View.VISIBLE ? CHAT_TYPE_1V1 : CHAT_TYPE_GROUP;
        String recvId = type == CHAT_TYPE_1V1 ? mChatMsgRecIdEt.getText().toString() : currentChannelId;
        appendLog2MonitorView("send text msg , type : " + type + " ; recvId : " + recvId + " ; content : " + content);
        mHwRtcEngine.sendTextMsg(recvId, type, content);
        if (mChatMsgContentEt != null) {
            mChatMsgContentEt.setText("");
        }
    }

    private void onAudioTouchHandle(MotionEvent event) {
        Log.i(ROOM_TAG, "onAudioTouchHandle");
        currentMicOperate = Constant.MicOperateTypeEnum.VoiceMsg;
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
                LogUtil.e(ROOM_TAG, e.getMessage());
            }
            appendLog2MonitorView("start record audio , filePath is :" + filePath);
            mHwRtcEngine.startRecordAudioMsg(filePath);
            timer.start();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            y2 = event.getY();
            mAudioViewTip.setText(y1 - y2 > 50 ? getString(R.string.cancel_send_audio) : getString(R.string.recording));
            return;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            y2 = event.getY();
            if (timer != null) {
                timer.cancel();
            }
            if (System.currentTimeMillis() - startTime > 1000) {
                IS_SEND = y1 - y2 < 50;
                appendLog2MonitorView("stop record audio then send audio");
                mHwRtcEngine.stopRecordAudioMsg();
            } else {
                IS_SEND = false;
                appendLog2MonitorView("stop record audio, recording less than 1 second");
                mHwRtcEngine.stopRecordAudioMsg();
                Toast.makeText(getApplicationContext(), getString(R.string.speak_short), Toast.LENGTH_SHORT).show();
            }
            mAudioViewTip.setText("");
            mAudioView.setVisibility(View.GONE);
        }
    }



    private void leaveAllChannel() {
        if (mHwRtcEngine == null) {
            return;
        }
        mHwRtcEngine.stopPlayAudioMsg();
        for (String channelId : channelIdList) {
            mHwRtcEngine.leaveChannel(channelId);
        }
    }

    private void createSendTextMsgView() {
        View msgView = View.inflate(this, R.layout.msg_view_layout, null);
        mMsgRecIdLl = msgView.findViewById(R.id.msg_view_rece_title);
        TextView mPlayerCountTv = msgView.findViewById(R.id.msg_view_people_count);
        mChatMsgRecIdEt = msgView.findViewById(R.id.msg_view_rec_id);
        mChatMsgContentRv = msgView.findViewById(R.id.msg_view_content_rv);
        mChatMsgContentEt = msgView.findViewById(R.id.msg_view_content_et);
        mChatMsgSend = msgView.findViewById(R.id.msg_view_send);
        Button mChatAudioSend = msgView.findViewById(R.id.audio_view_send);
        mAudioView = msgView.findViewById(R.id.audio_view);
        mAudioViewTip = msgView.findViewById(R.id.audio_view_tip);
        mSpeakerAudioTime = msgView.findViewById(R.id.speaker_audio_time);
        if (msgSingleRb.isChecked()) {
            mMsgRecIdLl.setVisibility(View.VISIBLE);
            mPlayerCountTv.setVisibility(View.GONE);
            mChatMsgRecIdEt.requestFocus();
        } else {
            mMsgRecIdLl.setVisibility(View.GONE);
            mPlayerCountTv.setVisibility(View.VISIBLE);
            mPlayerCountTv.setText("群聊ID：" + currentChannelId);

            mChatMsgContentEt.requestFocus();
        }
        mChatMsgContentEt.addTextChangedListener(chatMsgInputListener);
        mChatMsgRecIdEt.addTextChangedListener(chatMsgInputListener);
        mChatMsgSend.setOnClickListener(this);
        mChatAudioSend.setOnTouchListener(this);
        initChatContent(msgView);
    }

    private void initChatContent(View msgView) {
        mChatMsgDataList = new ArrayList<>();
        audioConstant = new AudioConstant();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mChatMsgContentRv.setLayoutManager(layoutManager);
        mChatMsgAdapter = new ChatMsgAdapter(this, mLocalUserId, mChatMsgDataList, audioConstant);
        mChatMsgContentRv.setAdapter(mChatMsgAdapter);

        PopupWindow popWindow =
            new PopupWindow(msgView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.showAtLocation(popTopView, Gravity.CENTER_HORIZONTAL, 0, OFFSET_Y);

    }

    private void insertChatData(com.huawei.game.gmme.model.Message message) {
        runOnUiThread(() -> {
            if (mChatMsgDataList != null && mChatMsgAdapter != null && mChatMsgContentRv != null) {
                mChatMsgDataList.add(message);
                mChatMsgAdapter.notifyItemInserted(mChatMsgDataList.size() - 1);
                mChatMsgContentRv.scrollToPosition(mChatMsgDataList.size() - 1);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rg_voice:
                if (!voiceCheckBoxIsChecked) {
                    return;
                }
                joinTeamOrWarBtn.setEnabled(mRoomIdView.getText().toString().length() > 0);
                if (checkedId == R.id.rb_voice_team) {
                    voiceWarRb.setText("国战语音");
                }
                break;
            case R.id.rg_msg:
                if (!msgCheckBoxIsChecked) {
                    return;
                }
                if (checkedId == R.id.rb_single_msg) {
                    sendTextMsgBtn.setText("发送消息");
                    sendTextMsgBtn.setEnabled(true);
                } else {
                    sendTextMsgBtn.setText("创建/加入频道");
                    if (mRoomIdView.getText().toString().length() > 0) {
                        if (mRoomIdView.getText().toString().equals(currentChannelId)) {
                            sendTextMsgBtn.setText("发送消息");
                        }
                        sendTextMsgBtn.setEnabled(true);
                    } else {
                        sendTextMsgBtn.setEnabled(false);
                    }
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == null) {
            return;
        }
        int roomIdLength = mRoomIdView.getText().toString().length();
        switch (buttonView.getId()) {
            case R.id.checkbox_voice:
                voiceCheckBoxIsChecked = isChecked;
                if (isChecked) {
                    voiceTeamRb.setEnabled(true);
                    voiceWarRb.setEnabled(true);
                    joinTeamOrWarBtn.setEnabled(roomIdLength > 0);
                } else {
                    voiceTeamRb.setEnabled(false);
                    voiceWarRb.setEnabled(false);
                    joinTeamOrWarBtn.setEnabled(false);
                }
                break;
            case R.id.checkbox_msg:
                msgCheckBoxIsChecked = isChecked;
                if (isChecked) {
                    msgSingleRb.setEnabled(true);
                    msgGroupRb.setEnabled(true);
                    if ((msgSingleRb.isChecked() || (roomIdLength > 0 && msgGroupRb.isChecked()))) {
                        sendTextMsgBtn.setEnabled(true);
                    }
                } else {
                    msgSingleRb.setEnabled(false);
                    msgGroupRb.setEnabled(false);
                    sendTextMsgBtn.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        joinTeamOrWarBtn.setEnabled(!TextUtils.isEmpty(s) && voiceChannelCb.isChecked());
        sendTextMsgBtn.setText("发送消息");
        if (msgGroupRb.isChecked() && (TextUtils.isEmpty(s) || !channelIdList.contains(s.toString()))) {
            sendTextMsgBtn.setText("创建/加入频道");
        }
        sendTextMsgBtn.setEnabled(
            msgChannelCb.isChecked() && (msgSingleRb.isChecked() || (msgGroupRb.isChecked() && !TextUtils.isEmpty(s))));

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void onClickRoomId() {
        if (!popupWindow.isShowing()) {
            RoomIdsAdapter roomIdsAdapter = new RoomIdsAdapter(roomIdList);
            roomIdsAdapter.setOnItemClickListener(new RoomIdsAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(String roomId) {
                    popupWindow.dismiss();
                    Message threadMsg = new Message();
                    threadMsg.obj = roomId;
                    threadMsg.arg1 = Constant.SWITCHROOM;
                    recyclerHandler.sendMessage(threadMsg);
                }
            });
            recyclerView.setAdapter(roomIdsAdapter);
            popupWindow.showAsDropDown(roomIdView, 0, 0, Gravity.BOTTOM);
        }
    }

    private void destroyGmme() {
        ((GmmeApplication) getApplication()).destroyGmmEngine();
        Log.i(ROOM_TAG, "engine destroy success");
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(ROOM_TAG, "onDestroy!");
        roomIdList = new ArrayList<>();
        ((GmmeApplication) getApplication()).removeEventHandler(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        roomIdList = new ArrayList<>();
        ((GmmeApplication) getApplication()).removeEventHandler(this);
        super.finish();
    }

    /**
     * 离开房间
     */
    private void onExitRoomClick() {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            if (roomIdList.size() > 1) {
                showRoomChoiceDialog();
                return;
            }

            if (mLocalOpenId.equals(getOwnerId()) && getRoomMemberList(mRoomId).size() > 1) {
                showSingleChoiceDialog(mRoomId);
            } else {
                mHwRtcEngine.leaveRoom(mRoomId, null);
                appendLog2MonitorView("leaved room");
            }
        }
    }

    /**
     * 指定房间的弹窗
     */
    private void showRoomChoiceDialog() {
        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(GmmeRoomActivity.this);
        View view = getLayoutInflater().inflate(R.layout.room_list, null);
        AlertDialog aDialog = singleChoiceDialog.create();
        aDialog.setCancelable(false);
        aDialog.setView(view);

        String[] rooms = roomIdList.toArray(new String[roomIdList.size()]);
        newOwner = -1;
        view.findViewById(R.id.btn_cancel).setOnClickListener(view1 -> aDialog.dismiss());
        view.findViewById(R.id.btn_confirm).setOnClickListener(view12 -> {
            if (newOwner != -1) {
                if (mLocalOpenId.equals(getOwnerId()) && getRoomMemberList(mRoomId).size() > 1) {
                    showSingleChoiceDialog(rooms[newOwner]);
                } else {
                    mHwRtcEngine.leaveRoom(rooms[newOwner], null);
                }
            }

            aDialog.dismiss();
            appendLog2MonitorView("leaved room");
        });
        addRadioButton(view.findViewById(R.id.radio_grp_l), view.findViewById(R.id.radio_grp_r), roomIdList);
        aDialog.show();
    }

    /**
     * 指定房主的弹窗
     */
    private void showSingleChoiceDialog(String removeRoomId) {
        List<String> playerIdList = getRoomMemberList(removeRoomId);
        playerIdList.remove(mLocalOpenId);
        String[] items = new String[playerIdList.size()];
        String[] players = playerIdList.toArray(items);
        newOwner = -1;

        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(GmmeRoomActivity.this);
        View view = getLayoutInflater().inflate(R.layout.next_room_owner, null);
        AlertDialog aDialog = singleChoiceDialog.create();
        aDialog.setCancelable(false);
        aDialog.setView(view);

        view.findViewById(R.id.btn_cancel).setOnClickListener(view12 -> aDialog.dismiss());
        view.findViewById(R.id.btn_confirm).setOnClickListener(view1 -> {
            if (newOwner != -1) {
                mHwRtcEngine.leaveRoom(removeRoomId, players[newOwner]);
            } else {
                mHwRtcEngine.leaveRoom(removeRoomId, null);
            }
            aDialog.dismiss();
            appendLog2MonitorView("leaved room");
        });
        addRadioButton(view.findViewById(R.id.radio_grp_l), view.findViewById(R.id.radio_grp_r), playerIdList);
        aDialog.show();
    }

    /**
     * 转移房主
     */
    private void onTransferOwnerClick() {
        if (mHwRtcEngine == null) {
            finish();
        } else if (mLocalOpenId.equals(getOwnerId()) && getRoomMemberList(mRoomId).size() > 1) {
            showSingleChoiceDialog2();
        }
    }

    /**
     * 指定房主的弹窗
     */
    private void showSingleChoiceDialog2() {
        List<String> playerIdList = getRoomMemberList(mRoomId);
        playerIdList.remove(mLocalOpenId);
        String[] items = new String[playerIdList.size()];
        String[] players = playerIdList.toArray(items);
        newOwner = -1;

        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(GmmeRoomActivity.this);
        View view = getLayoutInflater().inflate(R.layout.transfer_owner, null);
        AlertDialog aDialog = singleChoiceDialog.create();
        aDialog.setCancelable(false);
        aDialog.setView(view);

        view.findViewById(R.id.btn_cancel).setOnClickListener(view12 -> aDialog.dismiss());
        view.findViewById(R.id.btn_confirm).setOnClickListener(view1 -> {
            if (newOwner != -1) {
                mHwRtcEngine.transferOwner(mRoomId, players[newOwner]);
            } else {
                mHwRtcEngine.transferOwner(mRoomId, null);
            }
            aDialog.dismiss();
            appendLog2MonitorView("transferOwner ");
        });
        addRadioButton(view.findViewById(R.id.transfer_grp_l), view.findViewById(R.id.transfer_grp_r), playerIdList);
        aDialog.show();
    }

    /**
     * 动态添加RadioButton到选择下一任房主的弹框中
     */
    private void addRadioButton(RadioGroup radioGroupL, RadioGroup radioGroupR, List<String> playerIdList) {
        for (int i = 0; i < playerIdList.size(); i++) {
            RadioButton radioButton = new RadioButton(GmmeRoomActivity.this);
            radioButton.setButtonDrawable(R.drawable.radio_button);
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, 38);
            radioButton.setPadding(10, 0, 0, 0);
            radioButton.setTextColor(getResources().getColor(R.color.radio_color));
            radioButton.setText(playerIdList.get(i));
            radioButton.setId(i);
            RadioGroup.LayoutParams lp =
                new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 20);
            radioButton.setOnClickListener(view -> {
                int id = radioButton.getId();
                newOwner = id;
                if (id % 2 == 0) {
                    radioGroupL.check(id);
                    if (radioGroupR.getCheckedRadioButtonId() != -1) {
                        radioGroupR.clearCheck();
                    }
                } else {
                    radioGroupR.check(id);
                    if (radioGroupL.getCheckedRadioButtonId() != -1) {
                        radioGroupL.clearCheck();
                    }
                }
            });
            if (i % 2 == 0) {
                radioGroupL.addView(radioButton, lp);
            } else {
                radioGroupR.addView(radioButton, lp);
            }
        }
    }

    /**
     * 进入小队房间
     */
    private void onEnterTeamRoom() {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            String roomId = mRoomIdView.getText().toString().trim();
            appendLog2MonitorView("join teamRoom, roomId:" + roomId + " user:" + mLocalUserId);
            mHwRtcEngine.joinTeamRoom(roomId);
        }

    }

    /**
     * 点击加入国战弹出身份选择
     */
    private void onEnterNationalRoom() {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            userChoiceIdentity = 0;
            AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(GmmeRoomActivity.this);
            View view = getLayoutInflater().inflate(R.layout.role_type, null);
            AlertDialog aDialog = singleChoiceDialog.create();
            aDialog.setCancelable(false);
            aDialog.setView(view);

            view.findViewById(R.id.cancel).setOnClickListener(view12 -> {
                voiceWarRb.setText(userChoiceIdentity == 0 ? "指挥家" : "群众");
                aDialog.dismiss();
            });
            view.findViewById(R.id.confirm).setOnClickListener(view1 -> {
                voiceWarRb.setText(userChoiceIdentity == 0 ? "指挥家" : "群众");
                aDialog.dismiss();
            });
            RadioGroup radioGroup = view.findViewById(R.id.radio_grp);
            radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> userChoiceIdentity = (i == R.id.conductors ? 0 : 1));

            aDialog.show();
        }
    }

    /**
     * 进入国战房间
     */
    private void onEnterNationalRoom(int roleType) {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            String roomId = mRoomIdView.getText().toString().trim();
            appendLog2MonitorView("join NationalRoom, roomId:" + roomId + " user:" + mLocalUserId);
            mHwRtcEngine.joinNationalRoom(roomId, roleType);
        }
    }

    /**
     * 显示房间成员,绑定事件监听
     */
    public void onShowOrUpdateMembers() {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            ImageView muteAll = findViewById(R.id.mute_all);
            ImageView forbidAll = findViewById(R.id.forbid_all);
            View transferOwner = findViewById(R.id.transfer_owner);
            mRoomMember.setVisibility(View.VISIBLE);
            roomMemberBeansList = getRoomMemberList(mRoomId);
            roomType = getRoomType();
            mOwnerId = getOwnerId();
            muteAll.setOnClickListener(v -> {
                if (mHwRtcEngine == null) {
                    finish();
                } else {
                    mHwRtcEngine.muteAllPlayers(mRoomId, !isMute);
                }

            });
            forbidAll.setOnClickListener(v -> {
                if (mHwRtcEngine == null) {
                    finish();
                } else {
                    isForbid = getRoomStatus();
                    mHwRtcEngine.forbidAllPlayers(mRoomId, !isForbid);
                }
            });
            TextView tvTitle = findViewById(R.id.tv_title);
            tvTitle.setText(Constant.roomTitle[roomType]);
            // 放开国战禁言
            if (mLocalOpenId.equals(getOwnerId()) && roomType != Constant.NATIONALROOM) {
                forbidAll.setVisibility(View.VISIBLE);
                forbidAll.setEnabled(roomMemberBeansList.size() != 1);
            } else {
                forbidAll.setVisibility(View.GONE);
            }
            if (mLocalOpenId.equals(getOwnerId())) {
                transferOwner.setVisibility(View.VISIBLE);
                transferOwner.setOnClickListener(this);
            } else {
                transferOwner.setVisibility(View.GONE);
            }
            updateMemberCount();
            getRoomMemberListWithThread(roomMemberView);
        }
    }

    private void updateMemberCount() {
        // 更新房间人员总数
        TextView rtcMemberTitleCount = findViewById(R.id.room_members);
        roomMemberBeansList = getRoomMemberList(mRoomId);
        String memberText = "(" + roomMemberBeansList.size() + ")";
        rtcMemberTitleCount.setText(memberText);
    }

    private void recoveryImage() {
        ImageView tempImage = findViewById(R.id.mute_all);
        if (allMuteImage.get(mRoomId) != null) {
            if (allMuteImage.get(mRoomId)) {
                tempImage.setTag(Constant.UN_SELECT);
                tempImage.setImageResource(R.drawable.btn_mic_off);
            } else {
                tempImage.setTag(Constant.SELECT);
                tempImage.setImageResource(R.drawable.btn_mic_on);
            }
        }
        ImageView imageViewCheck = findViewById(R.id.forbid_all);
        if (allForbidImage.get(mRoomId) != null) {
            if (allForbidImage.get(mRoomId)) {
                imageViewCheck.setTag(Constant.UN_SELECT);
                imageViewCheck.setImageResource(R.drawable.btn_speaker_off);
            } else {
                imageViewCheck.setTag(Constant.SELECT);
                imageViewCheck.setImageResource(R.drawable.btn_speaker_on);
            }
        }
    }

    /**
     * 禁用本地音频按钮的点击事件
     *
     * @param view 禁用本地音频按钮
     */
    public void onMuteAudioClicked(View view) {
        LogUtil.i(ROOM_TAG, "onMuteAudioClicked !");
        currentMicOperate = Constant.MicOperateTypeEnum.MicOperate;
        if (checkRecordAudioPermission()) {
            doMuteAudio();
        }
    }

    private boolean checkRecordAudioPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.RECORD_AUDIO)) {
                mEnableMicView.setChecked(false);

                // 如果权限未获取，则申请权限
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode !=  REQUEST_PERMISSIONS_CODE) {
            return;
        }
        if (grantResults.length == 0) {
            return;
        }
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 权限通过
        if (currentMicOperate == Constant.MicOperateTypeEnum.MicOperate) {
            mEnableMicView.setChecked(true);
            doMuteAudio();
            return;
        }

        if (currentMicOperate == Constant.MicOperateTypeEnum.VoiceToText) {
            doVoiceToText();
        }
    }




    private void doMuteAudio(){
        if (mHwRtcEngine == null) {
            finish();
        } else {
            int ret = mHwRtcEngine.enableMic(mEnableMicView.isChecked());
            if (ret != 0) {
                appendLog2MonitorView("OnMuteAudioClicked failed: CODE:" + ret);
                LogUtil.e(ROOM_TAG, "OnMuteAudioClicked failed: CODE:" + ret);
            }
        }
    }

    private List<String> getRoomMemberList(String removeRoomId) {
        if (mHwRtcEngine == null) {
            LogUtil.e("engine init fail");
            return new ArrayList<>();
        }
        Room curRoom = mHwRtcEngine.getRoom(removeRoomId);
        List<Player> players = curRoom.getPlayers();
        if (players == null || players.isEmpty()) {
            LogUtil.e("players is empty");
            return new ArrayList<>();
        }
        List<String> playerIdList = new ArrayList<>();
        playerIdList.add(mLocalOpenId);
        for (Player playerInfo : players) {
            if (!mLocalOpenId.equals(playerInfo.getOpenId())) {
                playerIdList.add(playerInfo.getOpenId());
            }
        }
        return playerIdList;
    }

    private List<Player> getRoomMemberInfoList() {
        if (mHwRtcEngine == null) {
            LogUtil.e("engine init fail");
            return new ArrayList<>();
        }
        Room curRoom = mHwRtcEngine.getRoom(mRoomId);
        List<Player> players = curRoom.getPlayers();
        if (players == null || players.isEmpty()) {
            LogUtil.e("players is empty");
            return new ArrayList<>();
        }
        return players;
    }

    private String getOwnerId() {
        if (mHwRtcEngine == null) {
            LogUtil.e("engine init fail");
            return null;
        }
        Room curRoom = mHwRtcEngine.getRoom(mRoomId);
        return curRoom.getOwnerId();
    }

    private int getRoomType() {
        if (mHwRtcEngine == null) {
            LogUtil.e("engine init fail");
            return 0;
        }
        Room curRoom = mHwRtcEngine.getRoom(mRoomId);
        return curRoom.getRoomType();
    }

    private boolean getRoomStatus() {
        if (mHwRtcEngine == null) {
            LogUtil.e("engine init fail");
            return false;
        }
        Room curRoom = mHwRtcEngine.getRoom(mRoomId);
        return curRoom.getRoomStatus() == 1;
    }

    /**
     * 与会列表初始化
     *
     * @param rtcMemberListView 用户列表父布局
     */
    protected void getRoomMemberListWithThread(RecyclerView rtcMemberListView) {
        // 此时已在主线程中，可以更新UI了，定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(GmmeRoomActivity.this);

        // 设置布局管理器
        rtcMemberListView.setLayoutManager(manager);

        // 设置adapter
        Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
        Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
        rtcMemberListAdapter = new RoomMembersAdapter(GmmeRoomActivity.this, getOwnerId(), mLocalUserId, mLocalOpenId,
            roomType, new RoomActivityInfo(roomMemberBeansList, muteState, forbidState, muteImg, forbidImg));
        rtcMemberListView.setAdapter(rtcMemberListAdapter);
        // 增加分割线
        if (rtcMemberListView.getItemDecorationCount() != RecyclerView.VERTICAL) {
            rtcMemberListView
                .addItemDecoration(new DividerItemDecoration(GmmeRoomActivity.this, RecyclerView.VERTICAL));
        }
    }

    /**
     * 语音转文字
     */
    public void onVoiceToTextClick() {
        LogUtil.i(ROOM_TAG, "VoiceToText button Clicked !");
        currentMicOperate = Constant.MicOperateTypeEnum.VoiceToText;
        if (checkRecordAudioPermission()) {
            doVoiceToText();
        }
    }

    private void doVoiceToText(){
        if (mHwRtcEngine == null) {
            finish();
        } else {
            Boolean isChecked = mEnableMicView.isChecked();
            AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
            popup = new VoiceToTextPopup(this, mHwRtcEngine, mEnableMicView, isChecked, audioManager);
            if (isChecked) {
                SdkThreadPool.DEFAULT_THREAD_POOL.defaultExecutor().execute(() -> mHwRtcEngine.enableMic(false));
            }
            popup.showAtLocation(findViewById(R.id.voiceLinear), Gravity.CENTER_HORIZONTAL, 0, OFFSET_Y);
            popup.init();
            setBackgroundAlpha(0.6f);

            // 弹窗关闭时，恢复页面置灰部分
            popup.setOnDismissListener(() -> setBackgroundAlpha(1f));
        }
    }


    /**
     * 修改默认触摸事件传递
     *
     * @param event 触摸事件
     * @return 若弹窗出现，不传递对应事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (popup != null && popup.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 语音转文字时屏蔽返回按钮
     *
     * @param keyCode 按键事件码
     * @param event 点击事件
     * @return false表示阻止
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && popup != null && popup.isShowing()) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击语音转文字，置灰其他部分页面
     *
     * @param alpha 置灰的灰度
     */
    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 追加日志
     *
     * @param text 打印日志
     */
    private void appendLog2MonitorView(String text) {
        String logText = mLogMonitorView.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
        String time = sdf.format(new Date());
        if (!logText.isEmpty()) {
            logText = logText + "\n\n" + time + "\n" + text;
        } else {
            logText = time + "\n" + text;
        }
        mLogMonitorView.setText(logText);
        mLogHostView.fullScroll(View.FOCUS_DOWN);
    }

    /**
     * 日志清屏
     */
    public void onClearClicked(View view) {
        mLogMonitorView.setText("");
        mLogHostView.fullScroll(View.FOCUS_DOWN);
    }

    /**
     * 页面切换
     */
    public void onSwitchView(View view) {
        if (lastOperate == View.FOCUS_LEFT) {
            viewPager.arrowScroll(View.FOCUS_RIGHT);
            lastOperate = View.FOCUS_RIGHT;
            mSwitchView.setText("房间成员");
        } else {
            viewPager.arrowScroll(View.FOCUS_LEFT);
            lastOperate = View.FOCUS_LEFT;
            mSwitchView.setText("查看日志");
        }
    }

    @Override
    public void onCreate(int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("msg", msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onError : code=" + code + ", msg=" + msg);
            return;
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onMuteAllPlayers(String roomId, List<String> openIds, boolean isMuted, int code, String msg) {
        if (code == 0) {
            isMute = isMuted;
            Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
            for (String openId : openIds) {
                muteState.put(openId, isMuted);
            }
            allMuteState.put(mRoomId, muteState);
            runOnUiThread(() -> {
                allMuteImage.put(roomId, isMuted);
                recoveryImage();
                if (mRoomId.equals(roomId)) {
                    rtcMemberListAdapter.refreshMuteState(muteState);
                }
            });
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.USER_IDS, openIds);
            jsonObject.put(Constant.LogConstants.IS_MUTED, isMuted);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onMuteAllPlayers : roomId=" + roomId + ", openIds=" + openIds + ", isMuted=" + isMuted
                + ", code=" + code + ", msg=" + msg);
            return;
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onMutePlayer(String roomId, String openId, boolean isMuted, int code, String msg) {
        Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
        if (code == 0) {
            muteState.put(openId, isMuted);
        }
        allMuteState.put(mRoomId, muteState);
        runOnUiThread(() -> {
            if (mRoomId.equals(roomId)) {
                rtcMemberListAdapter.refreshMuteState(muteState);
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.USER_ID, openId);
            jsonObject.put(Constant.LogConstants.IS_MUTED, isMuted);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onMutePlayer : roomId=" + roomId + ", openId=" + openId + ", isMuted=" + isMuted
                + ", code=" + code + ", msg=" + msg);
            return;
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onJoinTeamRoom(String roomId, int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.TYPE, Constant.TEAMROOM);
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG,
                "onJoinTeamRoom : roomId=" + roomId + ",+type" + Constant.TEAMROOM + "code=" + code + ", msg=" + msg);
            return;
        }
        if (code == 0) {
            mHwRtcEngine.enableSpeakersDetection(roomId, Constant.INTERVAL_PERIOD);
            mRoomId = roomId;
            roomIdList.add(roomId);
            muteImg = null;
            forbidImg = null;
            Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
            Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
            List<Player> players = getRoomMemberInfoList();
            for (Player player : players) {
                forbidState.put(player.getOpenId(), player.getIsForbidden() == 1);
            }
            allMuteState.put(mRoomId, muteState);
            allForbidState.put(mRoomId, forbidState);
            allMuteImage.put(mRoomId, false);
            allForbidImage.put(mRoomId, getRoomStatus());
            recoveryImage();
            runOnUiThread(() -> {
                currentRoom.setText(mRoomId);
                roomIdView.setVisibility(View.VISIBLE);
                mRoomIdView.setText("");
                onShowOrUpdateMembers();
                mLeaveRoom.setEnabled(true);
            });
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onJoinNationalRoom(String roomId, int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.TYPE, Constant.NATIONALROOM);
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onJoinNationalRoom : roomId=" + roomId + ",+type" + Constant.NATIONALROOM + "code="
                + code + ", msg=" + msg);
            return;
        }
        if (code == 0) {
            mHwRtcEngine.enableSpeakersDetection(roomId, Constant.INTERVAL_PERIOD);
            mRoomId = roomId;
            roomIdList.add(roomId);
            muteImg = null;
            forbidImg = null;
            allMuteImage.put(mRoomId, false);
            allForbidImage.put(mRoomId, false);
            Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
            Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
            allMuteState.put(mRoomId, muteState);
            allForbidState.put(mRoomId, forbidState);
            recoveryImage();
            runOnUiThread(() -> {
                roomIdView.setVisibility(View.VISIBLE);
                mRoomIdView.setText("");
                currentRoom.setText(mRoomId);
                onShowOrUpdateMembers();
                mLeaveRoom.setEnabled(true);
            });
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onSwitchRoom(String roomId, int code, String msg) {
        mRoomId = roomId;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onSwitchRoom: code=" + code + ", msg=" + msg);
            return;
        }
        if (code == 0) {
            mHwRtcEngine.enableSpeakersDetection(roomId, Constant.INTERVAL_PERIOD);
            mRoomId = roomId;
            muteImg = null;
            forbidImg = null;
            Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
            Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
            List<Player> players = getRoomMemberInfoList();
            for (Player player : players) {
                forbidState.put(player.getOpenId(), player.getIsForbidden() == 1);
            }
            allMuteState.put(mRoomId, muteState);
            allForbidState.put(mRoomId, forbidState);
            allForbidImage.put(mRoomId, getRoomStatus());
            recoveryImage();
            runOnUiThread(() -> {
                onShowOrUpdateMembers();
                mLeaveRoom.setEnabled(true);
            });
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    private void notifyMainThreadToUpdateView(Integer status, JSONObject jsonObject) {
        Message createInfo = new Message();
        createInfo.arg1 = status;
        try {
            jsonObject.put("Method", Thread.currentThread().getStackTrace()[3].getMethodName());
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "put method error", e);
        }
        createInfo.obj = jsonObject;
        mHandler.sendMessage(createInfo);
    }

    @Override
    public void onDestroy(int code, String message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, message);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onDestroy : message=" + message, e);
            return;
        }
        LogUtil.i(ROOM_TAG, "onDestroy : message=" + message);
        notifyMainThreadToUpdateView(code, jsonObject);
        ((GmmeApplication) getApplication()).setGameAudioEngine(null);
        finish();
        // 返回到初始页面
    }

    @Override
    public boolean muteRemote(String openId, boolean state) {
        mHwRtcEngine.mutePlayer(mRoomId, openId, state);
        if (state) {
            appendLog2MonitorView("shield " + openId);
        } else {
            appendLog2MonitorView("unshield " + openId);
        }
        return true;
    }

    @Override
    public boolean mutePlayer(String openId, boolean state) {
        mHwRtcEngine.forbidPlayer(mRoomId, openId, state);
        if (state) {
            appendLog2MonitorView("mute " + openId);
        } else {
            appendLog2MonitorView("unmute " + openId);
        }
        return true;
    }

    @Override
    public void onLeaveRoom(String roomId, int code, String msg) {
        if (code == 0) {
            roomIdList.remove(roomId);
            allMuteImage.remove(roomId);
            allMuteState.remove(roomId);
            allForbidState.remove(roomId);
            allForbidImage.remove(roomId);
            
            Message threadMsg = new Message();
            if (roomIdList.size() > 0) {
                threadMsg.obj = roomIdList.get(0);
            } else {
                threadMsg.obj = "";
            }
            threadMsg.arg1 = Constant.LEAVEROOM;
            recyclerHandler.sendMessage(threadMsg);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onLeaveRoom: code=" + code + ", msg=" + msg);
            return;
        }
        runOnUiThread(() -> {
            if (roomIdList.size() == 0) {
                mRoomMember.setVisibility(View.GONE);
                mLeaveRoom.setEnabled(false);
                allMuteState.clear();
                allForbidState.clear();
                allMuteImage.clear();
                allForbidImage.clear();
            }
        });
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    myHandler.sendMessage(msg);
                    runOnUiThread(() -> rtcMemberListAdapter.updateSpeakingUsers(null));
                }
            };
        }
        if (mTimer != null && mTimerTask != null) {
            mTimer.schedule(mTimerTask, Constant.REFRESH_MEMBER_LIST_PERIOD);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void onSpeakersDetection(List<String> openIds) {

    }

    @Override
    public void onSpeakersDetectionEx(List<VolumeInfo> userVolumeInfos) {
        Message msg = new Message();
        StringBuilder sb = new StringBuilder();
        List<String> openIds = new ArrayList<>();
        for (VolumeInfo volumeInfo : userVolumeInfos) {
            sb.append(volumeInfo.getOpenId()).append(Constant.EMPTY_STRING);
            openIds.add(volumeInfo.getOpenId());
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        msg.obj = sb.toString();
        myHandler.sendMessage(msg);
        runOnUiThread(() -> rtcMemberListAdapter.updateSpeakingUsers(openIds));
    }

    @Override
    public void onForbidAllPlayers(String roomId, List<String> openIds, boolean isForbidden, int code, String msg) {
        Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
        if (code == 0) {
            isForbid = isForbidden;
            for (String openId : openIds) {
                forbidState.put(openId, isForbidden);
            }
        }
        allForbidState.put(mRoomId, forbidState);
        runOnUiThread(() -> {
            allForbidImage.put(roomId, isForbidden);
            recoveryImage();
            if (mRoomId.equals(roomId)) {
                rtcMemberListAdapter.refreshForbidState(forbidState);
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.USER_IDS, openIds);
            jsonObject.put(Constant.LogConstants.IS_FORBIDDEN, isForbidden);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onForbidAllPlayers : roomId=" + roomId + ", openIds=" + openIds + ", isForbidden="
                + isForbidden + ", code=" + code + ", msg=" + msg);
            return;
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onForbidPlayer(String roomId, String openId, boolean isForbidden, int code, String msg) {
        Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
        if (code == 0) {
            forbidState.put(openId, isForbidden);
        }
        allForbidState.put(mRoomId, forbidState);
        runOnUiThread(() -> {
            if (mRoomId.equals(roomId)) {
                rtcMemberListAdapter.refreshForbidState(forbidState);
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.USER_ID, openId);
            jsonObject.put(Constant.LogConstants.IS_FORBIDDEN, isForbidden);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onForbidPlayer : roomId=" + roomId + ", openId=" + openId + ", isForbidden="
                + isForbidden + ", code=" + code + ", msg=" + msg);
            return;
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onForbiddenByOwner(String roomId, List<String> openIds, boolean isForbidden) {
        Map forbidState = allForbidState.get(roomId) != null ? allForbidState.get(roomId) : new HashMap<>();
        for (String openId : openIds) {
            forbidState.put(openId, isForbidden);
        }
        allForbidState.put(roomId, forbidState);
        runOnUiThread(() -> {
            if (mRoomId.equals(roomId)) {
                rtcMemberListAdapter.refreshForbidState(forbidState);
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.USER_IDS, openIds);
            jsonObject.put(Constant.LogConstants.IS_FORBIDDEN, isForbidden);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onForbiddenByOwner : roomId=" + roomId + ", isForbidden=" + isForbidden);
            return;
        }
        notifyMainThreadToUpdateView(0, jsonObject);
    }

    @Override
    public void onVoiceToText(String text, int status, String message) {
        runOnUiThread(() -> {
            if (status == CommonErrorCode.SUCCESS.getCode()) {
                popup.setVoiceToText(text);
            } else if (status == GmmeErrCode.VOICE_TO_TEXT_FEATURE_DISABLE.getCode()) {
                popup.setVoiceToText("语音转文字功能未开通");
            } else {
                popup.setVoiceToText("网络繁忙，请取消重试");
            }
            popup.clearTimerAndOnSpeaker();
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
            jsonObject.put("message", message);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "status:" + status + ",text:" + text + ",message:" + message);
            return;
        }
        notifyMainThreadToUpdateView(status, jsonObject);
    }

    @Override
    public void onPlayerOnline(String roomId, String openId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.USER_ID, openId);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onPlayerOnline : roomId=" + roomId + ", openId=" + openId);
            return;
        }
        mHwRtcEngine.enableSpeakersDetection(roomId, Constant.INTERVAL_PERIOD);
        if (!mRoomId.equals(roomId)) {
            return;
        }
        mRoomId = roomId;
        muteImg = null;
        forbidImg = null;
        Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
        Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
        List<Player> players = getRoomMemberInfoList();
        for (Player player : players) {
            forbidState.put(player.getOpenId(), player.getIsForbidden() == 1);
        }
        allForbidState.put(mRoomId, forbidState);
        allMuteState.put(mRoomId, muteState);
        allMuteImage.put(mRoomId, false);
        allForbidImage.put(mRoomId, getRoomStatus());
        recoveryImage();
        runOnUiThread(this::onShowOrUpdateMembers);
        mOwnerId = getOwnerId();
        notifyMainThreadToUpdateView(0, jsonObject);
    }

    @Override
    public void onPlayerOffline(String roomId, String openId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.USER_ID, openId);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onPlayerOffline : roomId=" + roomId + ", openId=" + openId);
            return;
        }
        runOnUiThread(() -> {
            if (mRoomId.equals(roomId)) {
                rtcMemberListAdapter.removePlayer(openId);
            }
            updateMemberCount();
        });
        mOwnerId = getOwnerId();
        allForbidImage.put(mRoomId, getRoomStatus());
        recoveryImage();
        runOnUiThread(this::onShowOrUpdateMembers);
        notifyMainThreadToUpdateView(0, jsonObject);
    }

    @Override
    public void onTransferOwner(String roomId, int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onTransferOwner: status=" + code + ", msg=" + msg);
            return;
        }
        mOwnerId = getOwnerId();
        recoveryImage();
        runOnUiThread(() -> {
            if (mRoomId.equals(roomId)) {
                onShowOrUpdateMembers();
            }
        });
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onJoinChannel(String channelId, int code, String msg) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(IMLogConstant.Common.CODE, code);
            jsonObject.put(IMLogConstant.CHANNEL_ID, channelId);
            jsonObject.put(IMLogConstant.MSG, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onJoinChannel : code=" + code + ", channelId=" + channelId + ", msg=" + msg);
            return;
        }
        notifyMainThreadToUpdateView(code, jsonObject);
        if (code == 0) {
            channelIdList.add(channelId);
            currentChannelId = channelId;
            runOnUiThread(() -> {
                mRoomIdView.setText("");
                leaveChannelBtn.setEnabled(true);
                createSendTextMsgView();
                Toast
                    .makeText(getApplicationContext(),
                        "当前群聊人数：" + mHwRtcEngine.getChannelInfo(channelId).getPlayerCount(), Toast.LENGTH_SHORT)
                    .show();
            });
        }
    }

    @Override
    public void onLeaveChannel(String channelId, int code, String msg) {
        if (code == 0) {
            channelIdList.remove(channelId);
            if (channelIdList.size() > 0) {
                currentChannelId = channelIdList.get(channelIdList.size() - 1);
            }
            runOnUiThread(() -> {
                mRoomIdView.setText("");
                leaveChannelBtn.setEnabled(false);
            });
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(IMLogConstant.Common.CODE, code);
            jsonObject.put(IMLogConstant.CHANNEL_ID, channelId);
            jsonObject.put(IMLogConstant.MSG, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onLeaveChannel : code=" + code + ", channelId=" + channelId + ", msg=" + msg);
            return;
        }
        notifyMainThreadToUpdateView(code, jsonObject);
    }

    @Override
    public void onSendMsg(com.huawei.game.gmme.model.Message msg) {
        insertChatData(msg);
        JSONObject jsonObject = new JSONObject();
        int chatType = msg.getChatType();
        int code = msg.getCode();
        String errMsg = msg.getErrMsg();
        String senderId = msg.getSenderId();
        String recvId = msg.getRecvId();
        String content = msg.getContent();
        try {
            jsonObject.put(IMLogConstant.Common.CODE, code);
            jsonObject.put(IMLogConstant.CHAT_TYPE, chatType);
            jsonObject.put(IMLogConstant.ERR_MSG, errMsg);
            jsonObject.put(IMLogConstant.SENDER_ID, senderId);
            jsonObject.put(IMLogConstant.RECV_ID, recvId);
            jsonObject.put(IMLogConstant.CONTENT, content);

        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onSendMsg : code=" + code + ", chatType=" + chatType + ", errMsg=" + errMsg
                + ", senderId=" + senderId + ", recvId=" + recvId + ", content=" + content);
            return;
        }
        notifyMainThreadToUpdateView(msg.getCode(), jsonObject);
    }

    @Override
    public void onRecvMsg(com.huawei.game.gmme.model.Message msg) {
        insertChatData(msg);
        JSONObject jsonObject = new JSONObject();
        int chatType = msg.getChatType();
        int code = msg.getCode();
        String errMsg = msg.getErrMsg();
        String senderId = msg.getSenderId();
        String recvId = msg.getRecvId();
        String content = msg.getContent();
        try {
            jsonObject.put(IMLogConstant.Common.CODE, code);
            jsonObject.put(IMLogConstant.CHAT_TYPE, chatType);
            jsonObject.put(IMLogConstant.ERR_MSG, errMsg);
            jsonObject.put(IMLogConstant.SENDER_ID, senderId);
            jsonObject.put(IMLogConstant.RECV_ID, recvId);
            jsonObject.put(IMLogConstant.CONTENT, content);

        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "OnRecvMsg : code=" + code + ", chatType=" + chatType + ", errMsg=" + errMsg
                + ", senderId=" + senderId + ", recvId=" + recvId + ", content=" + content);
            return;
        }
        notifyMainThreadToUpdateView(msg.getCode(), jsonObject);
    }

    @Override
    public void onRemoteMicroStateChanged(String roomId, String openId, boolean isMute) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("roomId", roomId);
            jsonObject.put("openId", openId);
            jsonObject.put("isMute", isMute);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG,
                "onRemoteMicrophoneStateChanged : roomId=" + roomId + ", openId=" + openId + ", isMute=" + isMute);
            return;
        }
        notifyMainThreadToUpdateView(0, jsonObject);
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
        runOnUiThread(
            () -> appendLog2MonitorView("onRecordAudioMsg filePath:" + filePath + ",code:" + code + ",msg:" + msg));
        if (IS_SEND) {
            mHwRtcEngine.uploadAudioMsgFile(filePath, 5000);
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
        // 调用sdk发送语音
        AudioMsgFileInfo audioMsgFileInfo = mHwRtcEngine.getAudioMsgFileInfo(filePath);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msgType", Constant.MsgType.MSG_TYPE_AUDIO);
            jsonObject.put("audioMilliSeconds", audioMsgFileInfo.getMilliSeconds());
            jsonObject.put("audioBytes", audioMsgFileInfo.getBytes());
            jsonObject.put("fileId", fileId);
            jsonObject.put("filePath", filePath);
            // 必须在主线程中更新UI视图
            runOnUiThread(() -> onSendChatMsg(jsonObject.toString()));
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "onUploadAudioMsgFile filePath:" + filePath + ",fileId:" + fileId + ",code:" + code
                + ",message:" + msg);
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
        runOnUiThread(() -> appendLog2MonitorView(
            "onDownloadAudioMsgFile filePath:" + filePath + ",fileId:" + fileId + ",code:" + code + ",message:" + msg));
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
        runOnUiThread(
            () -> appendLog2MonitorView("onPlayAudioMsg filePath:" + filePath + ",code:" + code + ",message:" + msg));
        if (code == GmmeErrCode.AUDIO_MSG_PLAY_LAST_UNFINISHED.getCode()) {
            if (LOCAL_CACHE_POSITION == audioConstant.getLocalCachePosition()) {
                audioConstant.setLocalCachePosition(-1);
                mHwRtcEngine.stopPlayAudioMsg();
            } else {
                mHwRtcEngine.stopPlayAudioMsg();
                mHwRtcEngine.playAudioMsg(filePath);
            }
        }
    }
}