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

import static com.huawei.game.gmme.exception.GmmeErrCode.SUCCESS;
import static com.huawei.gmmesdk.demo.activity.LoginActivity.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.game.common.constants.SdkThreadPool;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.common.utils.StringUtil;
import com.huawei.game.gmme.exception.GmmeErrCode;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.AudioMsgFileInfo;
import com.huawei.game.gmme.model.LocalAudioClipStateInfo;
import com.huawei.game.gmme.model.Player;
import com.huawei.game.gmme.model.Room;
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
import com.huawei.gmmesdk.demo.adapter.RoomActivityInfo;
import com.huawei.gmmesdk.demo.adapter.RoomIdsAdapter;
import com.huawei.gmmesdk.demo.adapter.RoomMembersAdapter;
import com.huawei.gmmesdk.demo.component.PlayPositionDialog;
import com.huawei.gmmesdk.demo.constant.Constant;
import com.huawei.gmmesdk.demo.handler.MemberEventClick;
import com.huawei.gmmesdk.demo.log.Log;
import com.huawei.gmmesdk.demo.util.RandomUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 应用房间界面
 *
 * @since 2023-04-10
 */
public class GmmeRoomActivity extends BaseActivity implements MemberEventClick, IGameMMEEventHandler {
    /**
     * 日志标签
     */
    private static final String ROOM_TAG = GmmeRoomActivity.class.getSimpleName();

    private static final int OFFSET_Y = 600;

    /**
     * 申请权限请求码
     */
    private static final int REQUEST_PERMISSIONS_CODE = 0X002;

    private static final String REG = "^[1-9]\\d*$";

    private static boolean threeDimensionalEnable = false;

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
    Handler recyclerHandler = new Handler(Looper.myLooper()) {
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
                appendLog2MonitorView(
                    "switch room:" + System.lineSeparator() + "user: " + mLocalUserId + System.lineSeparator()
                        + "oldRoomId: " + mRoomId + System.lineSeparator() + "newRoomId: " + msg.obj.toString());
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

    /**
     * 范围值
     */
    private String range = null;

    private boolean isForbid = false;

    private boolean isMute = false;

    private int newOwner = -1;

    private int userChoiceIdentity = 0;

    private ImageView muteImg;

    private VoiceToTextPopup popup;

    private AudioClipViewPopup audioClipViewPopup;

    private List<String> roomMemberBeansList = new ArrayList<>();

    private List<String> roomIdList = new ArrayList<>();

    private Timer mTimer;

    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler(Looper.getMainLooper()) {
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

    private ActivityResultLauncher<Intent> launcher = null;

    private SharedPreferences shareData = null;

    private Button btnThreeDimensionalEnable;

    /**
     * 当前麦克风的操作
     */
    private Constant.MicOperateTypeEnum currentMicOperate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        });
        ((GmmeApplication) getApplication()).registerEventHandler(this);
        shareData = getSharedPreferences("playerPositions", MODE_PRIVATE);
        shareData.edit().clear().apply();
        SelfPositionRotateInfo selfPositionRotateInfo = SelfPositionRotateInfo.getInstance();
        selfPositionRotateInfo.setHwRtcEngine(mHwRtcEngine);
        selfPositionRotateInfo.updateSelfPosition(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 点击向上键
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnQuit:
                onExitRoomClick();
                break;
            case R.id.hw_gmme_destroy:
                destroyGmme();
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
                } else if (voiceWarRb.isChecked()) {
                    if (userChoiceIdentity == 0) {
                        onEnterNationalRoom(Constant.JOINER);
                    } else {
                        onEnterNationalRoom(Constant.PLAYER);
                    }
                } else {
                    onEnterRangeRoom();
                }
                break;
            case R.id.rb_voice_war:
                onEnterNationalRoom();
                break;
            case R.id.textAudioClipView:
                onTextAudioClip();
                break;
            case R.id.textPlayerPosition:
                onTextPlayerPosition();
                break;
            case R.id.three_dimensional_enable:
                int result = mHwRtcEngine.enableSpatialSound(mRoomId, !threeDimensionalEnable);
                if (result == 0) {
                    threeDimensionalEnable = !threeDimensionalEnable;
                    btnThreeDimensionalEnable
                        .setText(threeDimensionalEnable ? getResources().getString(R.string.three_dimensional_disable)
                            : getResources().getString(R.string.three_dimensional_enable));
                    Toast.makeText(this, "设置3D音效成功", Toast.LENGTH_SHORT).show();
                } else {
                    String msg = result == 6022 ? "3D音效未集成" : "设置3D音效失败";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            case R.id.set_scope:
                onSetScope();
                break;
            case R.id.btnChannelMsg:
                onChannelMsgClick();
                break;
            case R.id.btnP2pMsg:
                onP2pMsgClick();
                break;
            case R.id.btnAudioMsg:
                audioMsg();
                break;
            default:
                break;
        }
    }

    private void audioMsg() {
        Intent intent = new Intent(this, AudioMsgActivity.class);
        launcher.launch(intent);
    }

    private void onChannelMsgClick() {
        Intent intent = new Intent(this, ChannelMsgActivity.class);
        launcher.launch(intent);
    }

    private void onP2pMsgClick() {
        Intent intent = new Intent(this, P2pMsgActivity.class);
        launcher.launch(intent);
    }

    /**
     * 设置范围range
     */
    private void onSetScope() {
        PlayPositionDialog playPositionDialog =
            new PlayPositionDialog(this, "请输入range", range, Constant.PlayerPositionDialogType.Scope);
        playPositionDialog.showAtLocation(btnSetScope, Gravity.CENTER, 0, 0);
        playPositionDialog.setOnDataListener((param, dialogType) -> {
            if (!StringUtil.isEmpty(param)) {
                String msg = "设置失败";
                String number = getNumber(param);
                int scopeValue;
                try {
                    scopeValue = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "设置失败，最大值小于2147483648", Toast.LENGTH_SHORT).show();
                    return;
                }
                int result = mHwRtcEngine.setAudioRecvRange(scopeValue);
                if (result == 0) {
                    range = number;
                    msg = "设置成功";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private String getNumber(String text) {
        Pattern pattern = Pattern.compile(REG);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches() ? text : "0";
    }

    /**
     * 设置玩家位置
     */
    private void onTextPlayerPosition() {
        Intent intent = new Intent(this, PlayerPositionActivity.class);
        launcher.launch(intent);
    }

    private void onTextAudioClip() {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            audioClipViewPopup = new AudioClipViewPopup(this, mHwRtcEngine);
            audioClipViewPopup.showAtLocation(findViewById(R.id.textAudioClipView), Gravity.CENTER, 0, 0);
        }
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
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        joinTeamOrWarBtn.setEnabled(!TextUtils.isEmpty(s) && voiceChannelCb.isChecked());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void onClickRoomId() {
        if (!popupWindow.isShowing()) {
            RoomIdsAdapter roomIdsAdapter = new RoomIdsAdapter(roomIdList);
            roomIdsAdapter.setOnItemClickListener(roomId -> {
                popupWindow.dismiss();
                Message threadMsg = new Message();
                threadMsg.obj = roomId;
                threadMsg.arg1 = Constant.SWITCHROOM;
                recyclerHandler.sendMessage(threadMsg);
            });
            recyclerView.setAdapter(roomIdsAdapter);
            popupWindow.showAsDropDown(roomIdView, 0, 0, Gravity.BOTTOM);
        }
    }

    private void destroyGmme() {
        SelfPositionRotateInfo.getInstance().clear();
        shareData.edit().clear().apply();
        ((GmmeApplication) getApplication()).destroyGmmEngine();
        Log.i(ROOM_TAG, "engine destroy success");
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(ROOM_TAG, "onDestroy!");
        roomIdList = new ArrayList<>();
        shareData.edit().clear().apply();
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
                Room curRoom = mHwRtcEngine.getRoom(rooms[newOwner]);
                String ownerId = curRoom.getOwnerId();
                if (mLocalOpenId.equals(ownerId) && getRoomMemberList(mRoomId).size() > 1) {
                    showSingleChoiceDialog(rooms[newOwner]);
                } else {
                    mHwRtcEngine.leaveRoom(rooms[newOwner], null);
                }
            }

            aDialog.dismiss();
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
        } else if (mLocalOpenId.equals(getOwnerId())) {
            showSingleChoiceDialog2();
        }
    }

    /**
     * 指定房主的弹窗
     */
    private void showSingleChoiceDialog2() {
        List<String> playerIdList = getRoomMemberList(mRoomId);
        if (playerIdList.size() <= 1) {
            return;
        }
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
            radioGroup
                .setOnCheckedChangeListener((radioGroup1, i) -> userChoiceIdentity = (i == R.id.conductors ? 0 : 1));

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
     * 进入范围房间
     */
    private void onEnterRangeRoom() {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            String roomId = mRoomIdView.getText().toString().trim();
            appendLog2MonitorView("join rangeRoom, roomId:" + roomId + " user:" + mLocalUserId);
            mHwRtcEngine.joinRangeRoom(roomId);
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
            // 设置3D音效
            btnThreeDimensionalEnable = findViewById(R.id.three_dimensional_enable);
            btnThreeDimensionalEnable.setOnClickListener(v -> {
                int result = mHwRtcEngine.enableSpatialSound(mRoomId, !threeDimensionalEnable);
                if (result == 0) {
                    threeDimensionalEnable = !threeDimensionalEnable;
                    btnThreeDimensionalEnable
                        .setText(threeDimensionalEnable ? getResources().getString(R.string.three_dimensional_disable)
                            : getResources().getString(R.string.three_dimensional_enable));
                    Toast.makeText(this, "设置3D音效成功", Toast.LENGTH_SHORT).show();
                } else {
                    String msg = result == 6022 ? "3D音效未集成" : "设置3D音效失败";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            });

            getRoomMemberListWithThread(roomMemberView, this);
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

    private boolean checkRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)) {
                mEnableMicView.setChecked(false);

                // 如果权限未获取，则申请权限
                requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_PERMISSIONS_CODE) {
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

    private void doMuteAudio() {
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
            LogUtil.e(TAG, "engine init fail");
            return new ArrayList<>();
        }
        Room curRoom = mHwRtcEngine.getRoom(removeRoomId);
        List<Player> players = curRoom.getPlayers();
        if (players == null || players.isEmpty()) {
            LogUtil.e(TAG, "players is empty");
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
            LogUtil.e(TAG, "engine init fail");
            return new ArrayList<>();
        }
        Room curRoom = mHwRtcEngine.getRoom(mRoomId);
        List<Player> players = curRoom.getPlayers();
        if (players == null || players.isEmpty()) {
            LogUtil.e(TAG, "players is empty");
            return new ArrayList<>();
        }
        return players;
    }

    private String getOwnerId() {
        if (mHwRtcEngine == null) {
            LogUtil.e(TAG, "engine init fail");
            return null;
        }
        Room curRoom = mHwRtcEngine.getRoom(mRoomId);
        return curRoom.getOwnerId();
    }

    private int getRoomType() {
        if (mHwRtcEngine == null) {
            LogUtil.e(TAG, "engine init fail");
            return 0;
        }
        Room curRoom = mHwRtcEngine.getRoom(mRoomId);
        return curRoom.getRoomType();
    }

    private boolean getRoomStatus() {
        if (mHwRtcEngine == null) {
            LogUtil.e(TAG, "engine init fail");
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
    protected void getRoomMemberListWithThread(RecyclerView rtcMemberListView, Context context) {

        // 此时已在主线程中，可以更新UI了，定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(GmmeRoomActivity.this);

        // 设置布局管理器
        rtcMemberListView.setLayoutManager(manager);

        // 设置adapter
        boolean allOneKeyMuteState = allMuteImage.get(mRoomId) != null ? allMuteImage.get(mRoomId) : false;
        Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
        Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
        rtcMemberListAdapter = new RoomMembersAdapter(context, GmmeRoomActivity.this, getOwnerId(), mLocalUserId,
            mLocalOpenId, roomType,
            new RoomActivityInfo(roomMemberBeansList, allOneKeyMuteState, muteState, forbidState, muteImg, forbidImg),
            mHwRtcEngine);
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
        LogUtil.i(TAG, "VoiceToText button Clicked !");
        currentMicOperate = Constant.MicOperateTypeEnum.VoiceToText;
        if (checkRecordAudioPermission()) {
            doVoiceToText();
        }
    }

    private void doVoiceToText() {
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
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
                if (rtcMemberListAdapter != null && !StringUtil.isEmpty(mRoomId) && mRoomId.equals(roomId)) {
                    rtcMemberListAdapter.refreshMuteState(muteState);
                    rtcMemberListAdapter.refreshAllOneKeyMuteState(isMuted);
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
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
    }

    @Override
    public void onMutePlayer(String roomId, String openId, boolean isMuted, int code, String msg) {
        Map muteState = allMuteState.get(mRoomId) != null ? allMuteState.get(mRoomId) : new HashMap<>();
        if (code == 0) {
            muteState.put(openId, isMuted);
        }
        allMuteState.put(mRoomId, muteState);
        runOnUiThread(() -> {
            if (rtcMemberListAdapter != null && !StringUtil.isEmpty(mRoomId) && mRoomId.equals(roomId)) {
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
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
                threeDimensionalEnable = false;
                btnThreeDimensionalEnable.setText(getResources().getString(R.string.three_dimensional_enable));
            });
        }
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
                threeDimensionalEnable = false;
                btnThreeDimensionalEnable.setText(getResources().getString(R.string.three_dimensional_enable));
            });
        }
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constant.LogConstants.TYPE, Constant.RANGEROOM);
            jsonObject.put(Constant.LogConstants.ROOMID, roomId);
            jsonObject.put(Constant.LogConstants.CODE, code);
            jsonObject.put(Constant.LogConstants.MESSAGE, msg);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG,
                "onJoinRangeRoom : roomId=" + roomId + ",+type" + Constant.RANGEROOM + "code=" + code + ", msg=" + msg);
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
                threeDimensionalEnable = false;
                btnThreeDimensionalEnable.setText(getResources().getString(R.string.three_dimensional_enable));
            });
        }
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
            boolean spatialSoundEnable = mHwRtcEngine.isEnableSpatialSound(roomId);
            threeDimensionalEnable = spatialSoundEnable;
            btnThreeDimensionalEnable
                .setText(spatialSoundEnable ? getResources().getString(R.string.three_dimensional_disable)
                    : getResources().getString(R.string.three_dimensional_enable));
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
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
        ((GmmeApplication) getApplication()).removeGmmEngine();
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
                threeDimensionalEnable = false;
                mLeaveRoom.setEnabled(false);
                allMuteState.clear();
                allForbidState.clear();
                allMuteImage.clear();
                allForbidImage.clear();
            }
        });
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
                    runOnUiThread(() -> {
                        if (rtcMemberListAdapter != null) {
                            rtcMemberListAdapter.updateSpeakingUsers(null);
                        }
                    });
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
        runOnUiThread(() -> {
            if (rtcMemberListAdapter != null) {
                rtcMemberListAdapter.updateSpeakingUsers(openIds);
            }
        });
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
            if (rtcMemberListAdapter != null && !StringUtil.isEmpty(mRoomId) && mRoomId.equals(roomId)) {
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
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
    }

    @Override
    public void onForbidPlayer(String roomId, String openId, boolean isForbidden, int code, String msg) {
        Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
        if (code == 0) {
            forbidState.put(openId, isForbidden);
        }
        allForbidState.put(mRoomId, forbidState);
        runOnUiThread(() -> {
            if (rtcMemberListAdapter != null && !StringUtil.isEmpty(mRoomId) && mRoomId.equals(roomId)) {
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
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
    }

    @Override
    public void onForbiddenByOwner(String roomId, List<String> openIds, boolean isForbidden) {
        Map forbidState = allForbidState.get(roomId) != null ? allForbidState.get(roomId) : new HashMap<>();
        for (String openId : openIds) {
            forbidState.put(openId, isForbidden);
        }
        allForbidState.put(roomId, forbidState);
        runOnUiThread(() -> {
            if (rtcMemberListAdapter != null && !StringUtil.isEmpty(mRoomId) && mRoomId.equals(roomId)) {
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
        appendLog2MonitorView("status = 0" + ",message=" + jsonObject);
    }

    @Override
    public void onVoiceToText(String text, int status, String message) {
        runOnUiThread(() -> {
            if (popup != null) {
                setPopup(text, status, message);

                popup.clearTimerAndOnSpeaker();
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", text);
            jsonObject.put("message", message);
        } catch (JSONException e) {
            LogUtil.e(ROOM_TAG, "status:" + status + ",text:" + text + ",message:" + message);
            return;
        }
        appendLog2MonitorView("status =" + status + ",message=" + jsonObject);
    }

    private void setPopup(String text, int status, String message) {
        if (status == SUCCESS.getCode()) {
            popup.setVoiceToText(text);
            return;
        }

        if (status == GmmeErrCode.VOICE_TO_TEXT_FEATURE_DISABLE.getCode()) {
            popup.setVoiceToText("语音转文字功能未开通");
            return;
        }

        popup.setVoiceToText("网络繁忙，请取消重试");
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
        muteState.put(openId, false);
        Map forbidState = allForbidState.get(mRoomId) != null ? allForbidState.get(mRoomId) : new HashMap<>();
        Boolean allOneKeyMuteState = allMuteImage.get(mRoomId) != null ? allMuteImage.get(mRoomId) : false;
        List<Player> players = getRoomMemberInfoList();
        for (Player player : players) {
            forbidState.put(player.getOpenId(), player.getIsForbidden() == 1);
        }
        allForbidState.put(mRoomId, forbidState);
        allMuteState.put(mRoomId, muteState);
        allMuteImage.put(mRoomId, allOneKeyMuteState);
        allForbidImage.put(mRoomId, getRoomStatus());
        recoveryImage();
        runOnUiThread(this::onShowOrUpdateMembers);
        mOwnerId = getOwnerId();
        appendLog2MonitorView("status = 0" + ",message=" + jsonObject);
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
            if (rtcMemberListAdapter != null && !StringUtil.isEmpty(mRoomId) && mRoomId.equals(roomId)) {
                rtcMemberListAdapter.removePlayer(openId);
            }
            updateMemberCount();
        });
        mOwnerId = getOwnerId();
        allForbidImage.put(mRoomId, getRoomStatus());
        recoveryImage();
        runOnUiThread(this::onShowOrUpdateMembers);
        appendLog2MonitorView("status = 0" + ",message=" + jsonObject);
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
        boolean isForbidden = getRoomStatus();
        allForbidImage.put(roomId, isForbidden);
        mOwnerId = getOwnerId();
        recoveryImage();
        runOnUiThread(() -> {
            if (!StringUtil.isEmpty(mRoomId) && mRoomId.equals(roomId)) {
                onShowOrUpdateMembers();
            }
        });
        appendLog2MonitorView("status =" + code + ",message=" + jsonObject);
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
        appendLog2MonitorView("status = 0" + ",message=" + jsonObject);
    }

    /**
     * 播放本地音效文件状态回调。
     *
     * @param localAudioClipStateInfo 音频文件状态对象
     */
    @Override
    public void onAudioClipStateChangedNotify(LocalAudioClipStateInfo localAudioClipStateInfo) {
        if (audioClipViewPopup != null) {
            runOnUiThread(() -> audioClipViewPopup.onAudioClipStateChangedNotify(localAudioClipStateInfo));
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
     * @param code 响应码
     * @param msg 响应消息
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
        runOnUiThread(() -> appendLog2MonitorView(
            "onRtmConnectionChanged status:" + notify.getStatus() + ",reason:" + notify.getReason()));
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
     * 频道内玩家属性变更通知
     *
     * @param notify 频道内玩家属性变更
     */
    @Override
    public void onRtmChannelPlayerPropertiesChanged(RtmChannelPlayerPropertiesNotify notify) {

    }

    /**
     * 频道属性变更通知
     *
     * @param notify 频道属性变更
     */
    @Override
    public void onRtmChannelPropertiesChanged(RtmChannelPropertiesNotify notify) {

    }

}