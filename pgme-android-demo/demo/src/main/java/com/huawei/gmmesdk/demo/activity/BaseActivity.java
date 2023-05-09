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

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.gmmesdk.demo.Constant;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.adapter.NoSlideViewPager;
import com.huawei.gmmesdk.demo.adapter.RoomMembersAdapter;
import com.huawei.gmmesdk.demo.adapter.RoomPagerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 界面初始化
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener,
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, TextWatcher {

    /**
     * 日志标签
     */
    private static final String BASE_TAG = "BaseActivity";

    /**
     * HRTCEngine
     */
    protected GameMediaEngine mHwRtcEngine;

    /**
     * 本地用户类
     */

    protected EditText mRoomIdView;

    /**
     * 本地用户ID
     */
    protected String mLocalUserId;

    /**
     * 本地用户名
     */
    protected String mLocalUserName;

    protected String mLocalOpenId;

    protected Map<String, Map> allMuteState = new HashMap<>();

    protected Map<String, Map> allForbidState = new HashMap<>();

    protected Map<String, Boolean> allMuteImage = new HashMap<>();

    protected Map<String, Boolean> allForbidImage = new HashMap<>();

    protected ImageView forbidImg;

    protected RoomMembersAdapter rtcMemberListAdapter;

    protected NoSlideViewPager viewPager;

    protected View memberPager;

    protected View printLogsPager;

    protected ArrayList<View> pageView = new ArrayList<View>();

    protected RecyclerView roomMemberView;

    protected CheckBox mEnableMicView;

    protected ScrollView mLogHostView;

    protected TextView mLogMonitorView;

    protected Button mLeaveRoom;

    protected LinearLayout mRoomMember;

    protected Button mSwitchView;

    protected TextView currentRoom;

    protected LinearLayout roomIdView;

    protected PopupWindow popupWindow;

    protected RecyclerView recyclerView;

    protected CheckBox voiceChannelCb;

    protected RadioGroup voiceRg;

    protected RadioButton voiceTeamRb;

    protected RadioButton voiceWarRb;

    protected CheckBox msgChannelCb;

    protected RadioGroup msgRg;

    protected RadioButton msgSingleRb;

    protected RadioButton msgGroupRb;

    protected Button joinTeamOrWarBtn;

    protected Button sendTextMsgBtn;

    protected View popTopView;

    protected Button leaveChannelBtn;

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
        initRoot();
        setContentView(R.layout.activity_room);
        initPageView();
        initViews();
        initData();
        initPop();
    }

    /**
     * 引擎初始化和回调注册
     */
    protected void initRoot() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHwRtcEngine = ((GmmeApplication) getApplication()).getEngine();
        LogUtil.i(BASE_TAG, "registerEventHandler: before!");
        LogUtil.i(BASE_TAG, "registerEventHandler: after!");
    }

    private void initPageView() {
        viewPager = findViewById(R.id.viewPager);
        memberPager = getLayoutInflater().inflate(R.layout.room_members, null);
        printLogsPager = getLayoutInflater().inflate(R.layout.print_logs, null);
        pageView.add(memberPager);
        pageView.add(printLogsPager);
        viewPager.setAdapter(new RoomPagerAdapter(pageView));
    }

    private void initPop() {
        View view = getLayoutInflater().inflate(R.layout.recycler_view, null);
        recyclerView = view.findViewById(R.id.room_ids);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(null);

        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setFocusable(true);
    }

    private void initViews() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Real Time Voice");
        findViewById(R.id.ll_root).setOnClickListener(this);
        findViewById(R.id.btnQuit).setOnClickListener(this);
        findViewById(R.id.hw_gmme_destroy).setOnClickListener(this);
        findViewById(R.id.voiceToText).setOnClickListener(this);
        findViewById(R.id.room_id).setOnClickListener(this);
        roomMemberView = memberPager.findViewById(R.id.recycler_member);
        mEnableMicView = findViewById(R.id.checkEnableMic);
        mEnableMicView.setOnClickListener(this);
        mRoomIdView = findViewById(R.id.roomId_input);
        mRoomIdView.addTextChangedListener(this);
        mLogMonitorView = printLogsPager.findViewById(R.id.log_Monitor);
        mLogHostView = printLogsPager.findViewById(R.id.log_host);
        mLeaveRoom = findViewById(R.id.btnQuit);
        mRoomMember = memberPager.findViewById(R.id.mRoomMember);
        forbidImg = memberPager.findViewById(R.id.forbid_all);
        mSwitchView = findViewById(R.id.btnSwitchView);
        currentRoom = findViewById(R.id.current_room);
        roomIdView = findViewById(R.id.room_id);

        voiceChannelCb = findViewById(R.id.checkbox_voice);
        voiceRg = findViewById(R.id.rg_voice);
        voiceTeamRb = findViewById(R.id.rb_voice_team);
        voiceWarRb = findViewById(R.id.rb_voice_war);

        msgChannelCb = findViewById(R.id.checkbox_msg);
        msgRg = findViewById(R.id.rg_msg);
        msgSingleRb = findViewById(R.id.rb_single_msg);
        msgGroupRb = findViewById(R.id.rb_group_msg);

        joinTeamOrWarBtn = findViewById(R.id.btnJoin);
        sendTextMsgBtn = findViewById(R.id.btnSendTextMsg);
        leaveChannelBtn = findViewById(R.id.btnLeaveChannel);

        popTopView = findViewById(R.id.pop_top_view);

        voiceChannelCb.setOnCheckedChangeListener(this);
        voiceRg.setOnCheckedChangeListener(this);
        voiceWarRb.setOnClickListener(this);
        msgChannelCb.setOnCheckedChangeListener(this);
        msgRg.setOnCheckedChangeListener(this);
        joinTeamOrWarBtn.setOnClickListener(this);
        sendTextMsgBtn.setOnClickListener(this);
        leaveChannelBtn.setOnClickListener(this);
    }

    private void initData() {
        try {
            mLocalUserName = getIntent().getStringExtra(Constant.KEY_USER_NAME);
        } catch (Exception exception) {
            LogUtil.e(BASE_TAG, "getIntent exception =" + exception.getMessage());
            return;
        }
        try {
            mLocalUserId = getIntent().getStringExtra(Constant.KEY_USER_ID);
        } catch (Exception exception) {
            LogUtil.e(BASE_TAG, "getIntent exception =" + exception.getMessage());
            return;
        }
        if (mLocalUserName == null) {
            mLocalUserName = Constant.EMPTY_STRING;
        }
        if (mLocalUserId == null) {
            mLocalUserId = Constant.EMPTY_STRING;
        }
        mLocalOpenId = ((GmmeApplication) getApplication()).getOpenId();
        allMuteState.clear();
        allForbidState.clear();
        allMuteImage.clear();
        allForbidImage.clear();
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

    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}