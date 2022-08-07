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

import static com.huawei.game.common.https.Platform.ANDROID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.game.common.exception.CommonErrorCode;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.exception.GmmeErrCode;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.Player;
import com.huawei.game.gmme.model.Room;
import com.huawei.gmmesdk.demo.Constant;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.adapter.RoomActivityInfo;
import com.huawei.gmmesdk.demo.adapter.RoomIdsAdapter;
import com.huawei.gmmesdk.demo.adapter.RoomMembersAdapter;
import com.huawei.gmmesdk.demo.handler.MemberEventClick;
import com.huawei.gmmesdk.demo.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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

    private long mLastClickTime = 0L;

    private boolean isForbid = false;

    private boolean isMute = false;

    private int newOwner = -1;

    private int userChoiceIdentity = 0;

    /**
     * 当前房间ID
     */
    protected String mRoomId;

    /**
     * 当前房间房主
     */
    protected String mOwnerId;

    private ImageView muteImg;

    private VoiceToTextPopup popup;

    private List<String> roomMemberBeansList = new ArrayList<>();

    private List<String> roomIdList = new ArrayList<>();

    private Timer mTimer;

    private Iterator<String> iterator;

    private int roomType;

    private int roomRoleType;

    private int lastOperate = View.FOCUS_LEFT;

    protected TimerTask mTimerTask;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            appendLog2MonitorView("status =" + msg.arg1 + ",message=" + msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((GmmeApplication) getApplication()).registerEventHandler(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 点击向上键
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnter:
                onEnterTeamRoom();
                break;
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
            case R.id.btnNational:
                onEnterNationalRoom(v);
                break;
            case R.id.room_id:
                onClickRoomId();
                break;
            default:
                break;
        }
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
            if (mLocalOpenId.equals(getOwnerId()) && getRoomMemberList().size() > 1) {
                showSingleChoiceDialog();
            } else {
                mHwRtcEngine.leaveRoom(mRoomId, null);
                appendLog2MonitorView("leaved room");
            }
        }
    }

    /**
     * 指定房主的弹窗
     */
    private void showSingleChoiceDialog() {
        List<String> playerIdList = getRoomMemberList();
        playerIdList.remove(mLocalOpenId);
        String[] items = new String[playerIdList.size()];
        String[] players = playerIdList.toArray(items);
        newOwner = -1;

        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(GmmeRoomActivity.this);
        View view = getLayoutInflater().inflate(R.layout.next_room_owner, null);
        AlertDialog aDialog = singleChoiceDialog.create();
        aDialog.setCancelable(false);
        aDialog.setView(view);

        view.findViewById(R.id.btn_cancel).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aDialog.dismiss();
            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newOwner != -1) {
                    mHwRtcEngine.leaveRoom(mRoomId, players[newOwner]);
                } else {
                    mHwRtcEngine.leaveRoom(mRoomId, null);
                }
                aDialog.dismiss();
                appendLog2MonitorView("left room");
            }
        });
        addRadioButton(view.findViewById(R.id.radio_grp_l), view.findViewById(R.id.radio_grp_r), playerIdList);
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
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
            if (TextUtils.isEmpty(roomId)) {
                appendLog2MonitorView("create a teamRoom");
                mHwRtcEngine.joinTeamRoom("");
                return;
            }
            if (!TextUtils.isDigitsOnly(roomId)) {
                appendLog2MonitorView("please input number");
                return;
            }
            appendLog2MonitorView("entered teamRoom, roomId:" + roomId + " user:" + mLocalUserId);
            mHwRtcEngine.joinTeamRoom(roomId);
        }

    }

    /**
     * 点击加入国战弹出身份选择
     */
    private void onEnterNationalRoom(View v) {
        if (mHwRtcEngine == null) {
            finish();
        } else {
            userChoiceIdentity = 0;
            AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(GmmeRoomActivity.this);
            View view = getLayoutInflater().inflate(R.layout.role_type, null);
            AlertDialog aDialog = singleChoiceDialog.create();
            aDialog.setCancelable(false);
            aDialog.setView(view);

            view.findViewById(R.id.cancel).setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aDialog.dismiss();
                }
            });
            view.findViewById(R.id.confirm).setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userChoiceIdentity == 0) {
                        roomRoleType = Constant.JOINER;
                        onEnterNationalRoom(Constant.JOINER);
                    } else {
                        roomRoleType = Constant.PLAYER;
                        onEnterNationalRoom(Constant.PLAYER);
                    }
                    aDialog.dismiss();
                }
            });
            RadioGroup radioGroup = view.findViewById(R.id.radio_grp);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    userChoiceIdentity = (i == R.id.conductors ? 0 : 1);
                }
            });

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
            if (TextUtils.isEmpty(roomId)) {
                appendLog2MonitorView("create a NationalRoom");
                mHwRtcEngine.joinNationalRoom("", roleType);
                return;
            }
            if (!TextUtils.isDigitsOnly(roomId)) {
                appendLog2MonitorView("please input number");
                return;
            }
            appendLog2MonitorView("entered NationalRoom, roomId:" + roomId + " user:" + mLocalUserId);
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
            mRoomMember.setVisibility(View.VISIBLE);
            roomMemberBeansList = getRoomMemberList();
            roomType = getRoomType();
            mOwnerId = getOwnerId();
            muteAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHwRtcEngine == null) {
                        finish();
                    } else {
                        mHwRtcEngine.muteAllPlayers(mRoomId, !isMute);
                    }

                }
            });
            forbidAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHwRtcEngine == null) {
                        finish();
                    } else {
                        isForbid = getRoomStatus();
                        mHwRtcEngine.forbidAllPlayers(mRoomId, !isForbid);
                    }
                }
            });
            TextView tvTitle = findViewById(R.id.tv_title);
            tvTitle.setText(Constant.roomTitle[roomType]);
            // TODO 放开国战禁言
            if (mLocalOpenId.equals(getOwnerId()) && roomType != Constant.NATIONALROOM) {
                forbidAll.setVisibility(View.VISIBLE);
                forbidAll.setEnabled(roomMemberBeansList.size() != 1);
            } else {
                forbidAll.setVisibility(View.GONE);
            }
            updateMemberCount();
            getRoomMemberListWithThread(roomMemberView);
        }
    }

    private void updateMemberCount() {
        // 更新房间人员总数
        TextView rtcMemberTitleCount = findViewById(R.id.room_members);
        roomMemberBeansList = getRoomMemberList();
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
        if (mHwRtcEngine == null) {
            finish();
        } else {
            int ret = mHwRtcEngine.enableMic(((CheckBox) view).isChecked());
            if (ret != 0) {
                appendLog2MonitorView("OnMuteAudioClicked failed: CODE:" + ret);
                LogUtil.e(ROOM_TAG, "OnMuteAudioClicked failed: CODE:" + ret);
            }
        }
    }

    private List<String> getRoomMemberList() {
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
        if (mHwRtcEngine == null) {
            finish();
        } else {
            Boolean isChecked = mEnableMicView.isChecked();
            AudioManager audioManager = ((AudioManager) getSystemService(AUDIO_SERVICE));
            popup = new VoiceToTextPopup(this, mHwRtcEngine, mEnableMicView, isChecked, audioManager);
            if (isChecked) {
                ANDROID.defaultExecutor().execute(() -> mHwRtcEngine.enableMic(false));
            }
            popup.showAsDropDown(findViewById(R.id.voiceLinear), 0, 0);
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
     * @return
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    allMuteImage.put(roomId, isMuted);
                    recoveryImage();
                    if (mRoomId == roomId) {
                        rtcMemberListAdapter.refreshMuteState(muteState);
                    }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRoomId == roomId) {
                    rtcMemberListAdapter.refreshMuteState(muteState);
                }
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
                mEnableMicView.setVisibility(View.VISIBLE);
                onShowOrUpdateMembers();
                mLeaveRoom.setEnabled(true);
                mEnableMicView.setEnabled(true);
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
                if (getRoomType() == Constant.NATIONALROOM && roomRoleType == Constant.PLAYER) {
                    mEnableMicView.setVisibility(View.INVISIBLE);
                }
                roomIdView.setVisibility(View.VISIBLE);
                mRoomIdView.setText("");
                currentRoom.setText(mRoomId);
                onShowOrUpdateMembers();
                mLeaveRoom.setEnabled(true);
                mEnableMicView.setEnabled(true);
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
                mEnableMicView.setVisibility(View.VISIBLE);
                onShowOrUpdateMembers();
                mLeaveRoom.setEnabled(true);
                if (getRoomType() == Constant.NATIONALROOM && roomRoleType == Constant.PLAYER) {
                    mEnableMicView.setVisibility(View.INVISIBLE);
                } else {
                    mEnableMicView.setChecked(mEnableMicView.isChecked());
                }
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
            if (iterator != null && iterator.hasNext()) {
                iterator.remove();
            } else {
                roomIdList.remove(mRoomId);
                allMuteImage.remove(mRoomId);
                allMuteState.remove(mRoomId);
                allForbidState.remove(mRoomId);
                allForbidImage.remove(mRoomId);
            }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (roomIdList.size() == 0) {
                    mRoomMember.setVisibility(View.GONE);
                    mEnableMicView.setVisibility(View.VISIBLE);
                    mEnableMicView.setEnabled(false);
                    mLeaveRoom.setEnabled(false);
                    allMuteState.clear();
                    allForbidState.clear();
                    allMuteImage.clear();
                    allForbidImage.clear();
                }
            }
        });
        notifyMainThreadToUpdateView(code, jsonObject);
    }

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
        Message msg = new Message();
        StringBuilder sb = new StringBuilder();
        for (String playerId : openIds) {
            sb.append(playerId).append(Constant.EMPTY_STRING);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        msg.obj = sb.toString();
        myHandler.sendMessage(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allForbidImage.put(roomId, isForbidden);
                recoveryImage();
                if (mRoomId.equals(roomId)) {
                    rtcMemberListAdapter.refreshForbidState(forbidState);
                }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRoomId.equals(roomId)) {
                    rtcMemberListAdapter.refreshForbidState(forbidState);
                }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRoomId.equals(roomId)) {
                    rtcMemberListAdapter.refreshForbidState(forbidState);
                }
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
        runOnUiThread(() -> {
            mEnableMicView.setVisibility(View.VISIBLE);
            onShowOrUpdateMembers();
        });
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRoomId.equals(roomId)) {
                    rtcMemberListAdapter.removePlayer(openId);
                }
                updateMemberCount();
            }
        });
        mOwnerId = getOwnerId();
        allForbidImage.put(mRoomId, getRoomStatus());
        recoveryImage();
        runOnUiThread(() -> {
            onShowOrUpdateMembers();
        });
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
        notifyMainThreadToUpdateView(code, jsonObject);
    }
}