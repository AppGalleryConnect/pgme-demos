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

import static com.huawei.gmmesdk.demo.activity.ChannelMsgActivity.PageNum.PAGE_NUM_CHANNEL_PROP;
import static com.huawei.gmmesdk.demo.activity.ChannelMsgActivity.PageNum.PAGE_NUM_MSG;
import static com.huawei.gmmesdk.demo.activity.ChannelMsgActivity.PageNum.PAGE_NUM_PLAYER_PROP;
import static com.huawei.gmmesdk.demo.constant.Constant.RtmMessageType.MSG_TYPE_TEXT;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.huawei.game.common.constants.GmmeConstants;
import com.huawei.game.common.utils.CollectionUtils;
import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.common.utils.StringUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.handler.IGameMMEEventHandler;
import com.huawei.game.gmme.model.LocalAudioClipStateInfo;
import com.huawei.game.gmme.model.VolumeInfo;
import com.huawei.game.gmme.model.rtm.DeleteRtmChannelPlayerPropertiesReq;
import com.huawei.game.gmme.model.rtm.DeleteRtmChannelPlayerPropertiesResult;
import com.huawei.game.gmme.model.rtm.DeleteRtmChannelPropertiesReq;
import com.huawei.game.gmme.model.rtm.DeleteRtmChannelPropertiesResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelHistoryMessagesReq;
import com.huawei.game.gmme.model.rtm.GetRtmChannelHistoryMessagesResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelInfoReq;
import com.huawei.game.gmme.model.rtm.GetRtmChannelInfoResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelPlayerPropertiesReq;
import com.huawei.game.gmme.model.rtm.GetRtmChannelPlayerPropertiesResult;
import com.huawei.game.gmme.model.rtm.GetRtmChannelPropertiesReq;
import com.huawei.game.gmme.model.rtm.GetRtmChannelPropertiesResult;
import com.huawei.game.gmme.model.rtm.PublishRtmChannelMessageReq;
import com.huawei.game.gmme.model.rtm.PublishRtmChannelMessageResult;
import com.huawei.game.gmme.model.rtm.PublishRtmPeerMessageResult;
import com.huawei.game.gmme.model.rtm.ReceiveRtmChannelMessageNotify;
import com.huawei.game.gmme.model.rtm.ReceiveRtmPeerMessageNotify;
import com.huawei.game.gmme.model.rtm.RtmChannelHistoryMessage;
import com.huawei.game.gmme.model.rtm.RtmChannelMemberInfo;
import com.huawei.game.gmme.model.rtm.RtmChannelPlayerPropertiesNotify;
import com.huawei.game.gmme.model.rtm.RtmChannelPropertiesNotify;
import com.huawei.game.gmme.model.rtm.RtmConnectionStatusNotify;
import com.huawei.game.gmme.model.rtm.RtmMessageContent;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPlayerPropertiesReq;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPlayerPropertiesResult;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPropertiesReq;
import com.huawei.game.gmme.model.rtm.SetRtmChannelPropertiesResult;
import com.huawei.game.gmme.model.rtm.SubscribeRtmChannelReq;
import com.huawei.game.gmme.model.rtm.SubscribeRtmChannelResult;
import com.huawei.game.gmme.model.rtm.UnSubscribeRtmChannelReq;
import com.huawei.game.gmme.model.rtm.UnSubscribeRtmChannelResult;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.adapter.SimplePagerAdapter;
import com.huawei.gmmesdk.demo.adapter.SimpleSpinnerAdapter;
import com.huawei.gmmesdk.demo.component.RepeatSelectionSpinner;
import com.huawei.gmmesdk.demo.constant.Constant;
import com.huawei.gmmesdk.demo.util.KeyboardUtil;
import com.huawei.gmmesdk.demo.util.LogMonitorUtil;
import com.huawei.gmmesdk.demo.util.UserUtil;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChannelMsgActivity extends AppCompatActivity
    implements IGameMMEEventHandler, AdapterView.OnItemSelectedListener {

    private static final String TAG = ChannelMsgActivity.class.getSimpleName();

    /**
     * tab 页列表
     */
    private final List<View> viewList = new ArrayList<>();

    /**
     * 发送消息的临时缓存
     */
    private final Map<String, RtmMessageContent> msgCacheMap = new HashMap<>();

    /**
     * HRTCEngine
     */
    private GameMediaEngine mHwRtcEngine;

    /**
     * ViewPager
     */
    private ViewPager vpMain;

    /**
     * 消息发送页面切换按钮
     */
    private Button btnChannelMsgPage;

    /**
     * 频道属性页面切换按钮
     */
    private Button btnChannelPropPage;

    /**
     * 用户属性页面切换按钮
     */
    private Button btnPlayerPropPage;

    /**
     * 频道消息 tab 页
     */
    private View channelMsgView;

    /**
     * 频道属性 tab 页
     */
    private View channelPropView;

    /**
     * 用户属性 tab 页
     */
    private View playerPropView;

    /**
     * 频道消息页面已订阅频道下拉选择列表
     */
    private RepeatSelectionSpinner spinnerChannelInMsgPage;

    /**
     * 频道属性页面已订阅频道下拉选择列表
     */
    private RepeatSelectionSpinner spinnerChannelInChannelPropPage;

    /**
     * 用户属性页面已订阅频道下拉选择列表
     */
    private RepeatSelectionSpinner spinnerChannelInPlayerPropPage;
    /**
     * 下拉选择列表适配器
     */
    private SimpleSpinnerAdapter spinnerAdapter;

    /**
     * 查询历史消息开始于多少天前的输入框
     */
    private EditText etDaysAgo;

    /**
     * 查询历史消息数量输入框
     */
    private EditText etCount;

    /**
     * 日志打印文本框
     */
    private TextView tvLogMonitor;

    /**
     * 已登录用户打印框
     */
    private TextView tvLoggedInUsers;

    /**
     * 查询频道内已登录用户按钮
     */
    private Button btnQuery;

    /**
     * 清屏按钮
     */
    private Button btnClearLog;

    /**
     * 频道属性网格
     */
    private GridLayout glChannelProperty;

    /**
     * 用户属性网格
     */
    private GridLayout glPlayerProperty;

    /**
     * 当前选中的频道
     */
    private String curChannelId = "";

    /**
     * 频道属性缓存
     */
    private final Map<String, String> channelPropertyMap = new HashMap<>();

    /**
     * 玩家属性缓存
     */
    private final Map<String, String> playerPropertyMap = new HashMap<>();

    /**
     * 日志输出缓存
     */
    private Map<String, String> logMap = new HashMap<>();

    /**
     * 记录上一次用于查询频道属性的频道 ID
     */
    private String lastQueryChannelPropChannelId = "";

    /**
     * 记录上一次用于查询玩家属性的频道 ID
     */
    private String lastQueryPlayerPropChannelId = "";

    /**
     * gson 对象
     */
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_msg);

        ((GmmeApplication) getApplication()).registerEventHandler(this);
        mHwRtcEngine = ((GmmeApplication) getApplication()).getEngine();

        initPageView();
        initChannelMsgPage();
        initChannelPropPage();
        initPlayerPropPage();

        updateSubscribedChannels("");
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

    /**
     * 初始化主页
     */
    private void initPageView() {
        vpMain = findViewById(R.id.vp_main);
        channelMsgView = getLayoutInflater().inflate(R.layout.layout_channel_msg, vpMain, false);
        channelPropView = getLayoutInflater().inflate(R.layout.layout_channel_property, vpMain, false);
        playerPropView = getLayoutInflater().inflate(R.layout.layout_player_property, vpMain, false);
        viewList.add(channelMsgView);
        viewList.add(channelPropView);
        viewList.add(playerPropView);
        vpMain.setAdapter(new SimplePagerAdapter(viewList));

        btnChannelMsgPage = findViewById(R.id.btn_send_msg);
        btnChannelPropPage = findViewById(R.id.btn_channel_property);
        btnPlayerPropPage = findViewById(R.id.btn_player_property);
        btnChannelMsgPage.setOnClickListener(v -> {
            switchPage(PAGE_NUM_MSG);
        });
        btnChannelPropPage.setOnClickListener(v -> {
            queryChannelProperties(false);
            switchPage(PAGE_NUM_CHANNEL_PROP);
        });
        btnPlayerPropPage.setOnClickListener(v -> {
            queryPlayerProperties(UserUtil.getOpenId(), false);
            switchPage(PAGE_NUM_PLAYER_PROP);
        });

        spinnerAdapter = new SimpleSpinnerAdapter(this);

        spinnerChannelInMsgPage = channelMsgView.findViewById(R.id.spinner_channel);
        spinnerChannelInMsgPage.setAdapter(spinnerAdapter);
        spinnerChannelInMsgPage.setOnItemSelectedListener(this);
        spinnerChannelInChannelPropPage = channelPropView.findViewById(R.id.spinner_channel);
        spinnerChannelInChannelPropPage.setAdapter(spinnerAdapter);
        spinnerChannelInChannelPropPage.setOnItemSelectedListener(this);
        spinnerChannelInPlayerPropPage = playerPropView.findViewById(R.id.spinner_channel);
        spinnerChannelInPlayerPropPage.setAdapter(spinnerAdapter);
        spinnerChannelInPlayerPropPage.setOnItemSelectedListener(this);

        tvLogMonitor = findViewById(R.id.tv_log_monitor);
        tvLoggedInUsers = findViewById(R.id.tv_logged_in_users);
        tvLoggedInUsers.setMovementMethod(new ScrollingMovementMethod());
        btnQuery = findViewById(R.id.btn_query);
        btnClearLog = findViewById(R.id.btn_clear_log);

        // 查询已登陆用户信息
        btnQuery.setOnClickListener(v -> {
            GetRtmChannelInfoReq req = new GetRtmChannelInfoReq();
            req.setChannelId(curChannelId);
            req.setReturnMembers(true);
            mHwRtcEngine.getRtmChannelInfo(req);
        });

        // 清除日志输出
        btnClearLog.setOnClickListener(v -> {
            logMap.clear();
            LogMonitorUtil.updateLog(tvLogMonitor, "");
        });
    }

    /**
     * 切换页面
     *
     * @param pageNum 页面编号
     */
    private void switchPage(int pageNum) {
        vpMain.setCurrentItem(pageNum, false);
        btnChannelMsgPage.setBackgroundColor(
            pageNum == PAGE_NUM_MSG ? ContextCompat.getColor(this, R.color.tab_selected_color) : Color.TRANSPARENT);
        btnChannelPropPage.setBackgroundColor(pageNum == PAGE_NUM_CHANNEL_PROP
            ? ContextCompat.getColor(this, R.color.tab_selected_color) : Color.TRANSPARENT);
        btnPlayerPropPage.setBackgroundColor(pageNum == PAGE_NUM_PLAYER_PROP
            ? ContextCompat.getColor(this, R.color.tab_selected_color) : Color.TRANSPARENT);
        updateSubscribedChannels(curChannelId);
    }

    /**
     * 初始化频道消息 tab 页
     */
    private void initChannelMsgPage() {
        TextView tvPlayer = channelMsgView.findViewById(R.id.tv_player);
        EditText etChannelId = channelMsgView.findViewById(R.id.et_channel_id);
        Button btnSubscribe = channelMsgView.findViewById(R.id.btn_subscribe);
        Button btnUnSubscribe = channelMsgView.findViewById(R.id.btn_unsubscribe);
        EditText etSpecifyReceivers = channelMsgView.findViewById(R.id.et_specify_receivers);
        EditText etContent = channelMsgView.findViewById(R.id.et_content);
        Button btnSendText = channelMsgView.findViewById(R.id.btn_send_text);
        Button btnSendBinary = channelMsgView.findViewById(R.id.btn_send_binary);
        CheckBox cbIsCacheMsg = channelMsgView.findViewById(R.id.cb_is_cache_msg);
        CheckBox cbContentIdentify = channelMsgView.findViewById(R.id.cb_content_identify);
        CheckBox cbAdIdentify = channelMsgView.findViewById(R.id.cb_ad_identify);
        etDaysAgo = channelMsgView.findViewById(R.id.et_days_ago);
        etCount = channelMsgView.findViewById(R.id.et_count);

        tvPlayer.setText(UserUtil.getOpenId());

        // 订阅频道
        btnSubscribe.setOnClickListener(v -> {
            SubscribeRtmChannelReq req = new SubscribeRtmChannelReq();
            req.setChannelId(etChannelId.getText().toString());
            mHwRtcEngine.subscribeRtmChannel(req);
        });

        // 取消订阅频道
        btnUnSubscribe.setOnClickListener(v -> {
            UnSubscribeRtmChannelReq req = new UnSubscribeRtmChannelReq();
            req.setChannelId(etChannelId.getText().toString());
            mHwRtcEngine.unSubscribeRtmChannel(req);
        });

        // 发送文本消息
        btnSendText.setOnClickListener(v -> {
            PublishRtmChannelMessageReq req = new PublishRtmChannelMessageReq();
            req.setChannelId(curChannelId);
            String receivers = etSpecifyReceivers.getText().toString();
            if (!TextUtils.isEmpty(receivers)) {
                receivers = receivers.replaceAll(Constant.SPLIT_SEPARATOR_ZH, Constant.SPLIT_SEPARATOR);
                req.setReceivers(Arrays.asList(receivers.split(Constant.SPLIT_SEPARATOR)));
            }
            req.setAllowCacheMsg(cbIsCacheMsg.isChecked());
            req.setMessageType(MSG_TYPE_TEXT);
            req.setMessageString(etContent.getText().toString());
            req.setContentIdentify(cbContentIdentify.isChecked());
            req.setAdsIdentify(cbAdIdentify.isChecked());
            String clientMsgId = mHwRtcEngine.publishRtmChannelMessage(req);
            msgCacheMap.put(clientMsgId, req);
            etContent.setText("");
            KeyboardUtil.hideSoftKeyboard(this, etContent);
        });

        // 发送二进制消息
        btnSendBinary.setOnClickListener(v -> {
            PublishRtmChannelMessageReq req = new PublishRtmChannelMessageReq();
            req.setChannelId(curChannelId);
            String receivers = etSpecifyReceivers.getText().toString();
            if (!TextUtils.isEmpty(receivers)) {
                receivers = receivers.replaceAll(Constant.SPLIT_SEPARATOR_ZH, Constant.SPLIT_SEPARATOR);
                req.setReceivers(Arrays.asList(receivers.split(Constant.SPLIT_SEPARATOR)));
            }
            req.setAllowCacheMsg(cbIsCacheMsg.isChecked());
            req.setMessageType(Constant.RtmMessageType.MSG_TYPE_BYTE);
            String content = etContent.getText().toString();
            req.setMessageString("");
            req.setMessageBytes(content.getBytes(StandardCharsets.UTF_8));
            req.setContentIdentify(cbContentIdentify.isChecked());
            req.setAdsIdentify(cbAdIdentify.isChecked());
            String clientMsgId = mHwRtcEngine.publishRtmChannelMessage(req);
            msgCacheMap.put(clientMsgId, req);
            etContent.setText("");
            KeyboardUtil.hideSoftKeyboard(this, etContent);
        });
    }

    /**
     * 初始化频道属性 tab 页
     */
    private void initChannelPropPage() {
        Button btnSetting = channelPropView.findViewById(R.id.btn_setting);
        Button btnDeleteAll = channelPropView.findViewById(R.id.btn_delete_all);
        glChannelProperty = channelPropView.findViewById(R.id.gl_channel_property);

        btnSetting.setOnClickListener(v -> {
            Map<String, String> map = new HashMap<>(channelPropertyMap);
            SetPropertyPopup popup = new SetPropertyPopup(ChannelMsgActivity.this, Constant.PropertyType.Channel, map);
            popup.showAtLocation(v, Gravity.CENTER, 0, 0);
            popup.setOnPropertyChangedListener((type, propertyMap) -> {
                SetRtmChannelPropertiesReq req = new SetRtmChannelPropertiesReq();
                req.setChannelId(curChannelId);
                req.setChannelProperties(propertyMap);
                mHwRtcEngine.setRtmChannelProperties(req);
            });
        });
        btnDeleteAll.setOnClickListener(v -> {
            DeleteRtmChannelPropertiesReq req = new DeleteRtmChannelPropertiesReq();
            req.setChannelId(curChannelId);
            List<String> keys = new ArrayList<>(channelPropertyMap.keySet());
            req.setKeys(keys);
            mHwRtcEngine.deleteRtmChannelProperties(req);
        });
    }

    private void initPlayerPropPage() {
        EditText etPlayer = playerPropView.findViewById(R.id.et_player);
        Button btnQueryPlayerProp = playerPropView.findViewById(R.id.btn_query_player_property);
        Button btnSetting = playerPropView.findViewById(R.id.btn_setting);
        Button btnDeleteAll = playerPropView.findViewById(R.id.btn_delete_all);
        glPlayerProperty = playerPropView.findViewById(R.id.gl_player_property);

        btnQueryPlayerProp.setOnClickListener(v -> {
            String player = etPlayer.getText().toString();
            queryPlayerProperties(player, false);
        });

        btnSetting.setOnClickListener(v -> {
            Map<String, String> map = new HashMap<>(playerPropertyMap);
            SetPropertyPopup popup = new SetPropertyPopup(ChannelMsgActivity.this, Constant.PropertyType.Player, map);
            popup.showAtLocation(v, Gravity.CENTER, 0, 0);
            popup.setOnPropertyChangedListener((type, propertyMap) -> {
                SetRtmChannelPlayerPropertiesReq req = new SetRtmChannelPlayerPropertiesReq();
                req.setChannelId(curChannelId);
                req.setPlayerProperties(propertyMap);
                mHwRtcEngine.setRtmChannelPlayerProperties(req);
            });
        });
        btnDeleteAll.setOnClickListener(v -> {
            DeleteRtmChannelPlayerPropertiesReq req = new DeleteRtmChannelPlayerPropertiesReq();
            req.setChannelId(curChannelId);
            List<String> keys = new ArrayList<>(playerPropertyMap.keySet());
            req.setKeys(keys);
            mHwRtcEngine.deleteRtmChannelPlayerProperties(req);
        });
    }

    /**
     * 查询玩家属性
     * 
     * @param player 玩家 OpenID
     * @param isFromListener 是否来自 onItemSelected listener
     */
    private void queryPlayerProperties(String player, boolean isFromListener) {
        if (isFromListener && TextUtils.equals(lastQueryPlayerPropChannelId, curChannelId)) {
            return;
        }
        lastQueryPlayerPropChannelId = curChannelId;
        GetRtmChannelPlayerPropertiesReq req = new GetRtmChannelPlayerPropertiesReq();
        req.setChannelId(curChannelId);
        req.setOpenIds(Collections.singletonList(player));
        mHwRtcEngine.getRtmChannelPlayerProperties(req);
    }

    /**
     * 更新已订阅频道
     *
     * @param curChannelId 当前频道，下拉列表需要选中该频道
     */
    private void updateSubscribedChannels(String curChannelId) {
        List<String> subscribedChannelList = mHwRtcEngine.getRtmSubscribedChannelInfo().getChannelIds();
        spinnerAdapter.setTextList(subscribedChannelList);
        spinnerAdapter.notifyDataSetChanged();
        if (subscribedChannelList != null) {
            int position = TextUtils.equals(curChannelId, "") ? 0 : subscribedChannelList.indexOf(curChannelId);
            spinnerChannelInMsgPage.setSelection(position);
            spinnerChannelInChannelPropPage.setSelection(position);
            spinnerChannelInPlayerPropPage.setSelection(position);
        }
    }

    /**
     * 查询频道历史消息
     *
     * @param curChannelId 当前频道 ID
     */
    private void queryChannelHistoryMessages(String curChannelId) {
        GetRtmChannelHistoryMessagesReq req = new GetRtmChannelHistoryMessagesReq();
        req.setChannelId(curChannelId);
        req.setStartTime(parseTimestamp(etDaysAgo.getText().toString()));
        int count = 0;
        try {
            count = Integer.parseInt(etCount.getText().toString());
        } catch (NumberFormatException e) {
            LogUtil.e(TAG, "Input count is invalid.");
        }
        req.setCount(count);
        mHwRtcEngine.getRtmChannelHistoryMessages(req);
    }

    /**
     * 查询频道属性
     * 
     * @param isFromListener 是否来自 onItemSelected listener
     */
    private void queryChannelProperties(boolean isFromListener) {
        if (isFromListener && TextUtils.equals(lastQueryChannelPropChannelId, curChannelId)) {
            return;
        }
        lastQueryChannelPropChannelId = curChannelId;
        GetRtmChannelPropertiesReq req = new GetRtmChannelPropertiesReq();
        req.setChannelId(curChannelId);
        mHwRtcEngine.getRtmChannelProperties(req);
    }

    private void updateChannelProperties(Map<String, String> properties) {
        glChannelProperty.removeAllViews();
        channelPropertyMap.clear();
        if (properties == null) {
            return;
        }
        channelPropertyMap.putAll(properties);
        int row = 0;
        for (Map.Entry<String, String> entry : channelPropertyMap.entrySet()) {
            addKeyValueToGl(row, entry.getKey(), entry.getValue(), glChannelProperty, v -> {
                DeleteRtmChannelPropertiesReq req = new DeleteRtmChannelPropertiesReq();
                req.setChannelId(curChannelId);
                List<String> keys = new ArrayList<>();
                keys.add(entry.getKey());
                req.setKeys(keys);
                mHwRtcEngine.deleteRtmChannelProperties(req);
            });
            row++;
        }
    }

    private void addKeyValueToGl(int row, String key, String value, GridLayout layout,
        View.OnClickListener btnDeleteClickListener) {
        TextView tvKey =
            (TextView) getLayoutInflater().inflate(R.layout.item_property_textview, layout, false);
        tvKey.setText(key);
        addViewToGl(row, 0, layout, tvKey, false);

        TextView tvValue =
            (TextView) getLayoutInflater().inflate(R.layout.item_property_textview, layout, false);
        tvValue.setText(value);
        addViewToGl(row, 1, layout, tvValue, true);

        Button btnDelete =
            (Button) getLayoutInflater().inflate(R.layout.item_property_button, layout, false);
        btnDelete.setOnClickListener(btnDeleteClickListener);
        addViewToGl(row, 2, layout, btnDelete, false);
    }

    private void addViewToGl(int row, int col, GridLayout layout, View view, boolean needColWeight) {
        GridLayout.Spec rowSpec = GridLayout.spec(row);
        GridLayout.Spec columnSpec;
        if (needColWeight) {
            columnSpec = GridLayout.spec(col, 1f);
        } else {
            columnSpec = GridLayout.spec(col);
        }

        GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) view.getLayoutParams();
        layoutParams.rowSpec = rowSpec;
        layoutParams.columnSpec = columnSpec;
        layout.addView(view, layoutParams);
    }

    private void updatePlayerProperties(Map<String, String> properties) {
        glPlayerProperty.removeAllViews();
        playerPropertyMap.clear();
        if (properties == null) {
            return;
        }
        playerPropertyMap.putAll(properties);
        int row = 0;
        for (Map.Entry<String, String> entry : playerPropertyMap.entrySet()) {
            addKeyValueToGl(row, entry.getKey(), entry.getValue(), glPlayerProperty, v -> {
                DeleteRtmChannelPlayerPropertiesReq req = new DeleteRtmChannelPlayerPropertiesReq();
                req.setChannelId(curChannelId);
                List<String> keys = new ArrayList<>();
                keys.add(entry.getKey());
                req.setKeys(keys);
                mHwRtcEngine.deleteRtmChannelPlayerProperties(req);
            });
            row++;
        }
    }

    /**
     * 解析时间戳
     *
     * @param daysAgo 多少天前
     * @return 时间戳
     */
    private long parseTimestamp(String daysAgo) {
        int n = 7;
        try {
            n = Integer.parseInt(daysAgo);
        } catch (NumberFormatException e) {
            LogUtil.e(TAG, "Input days ago is invalid.");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -n);
        return calendar.getTimeInMillis();
    }

    /**
     * 添加日志输出
     * 
     * @param channelId 频道 ID
     * @param textToAdd 要添加的日志内容
     */
    private void appendLog2MonitorView(String channelId, String textToAdd) {
        if (channelId == null) {
            channelId = "";
        }
        String logText = logMap.get(channelId);
        logText = LogMonitorUtil.appendLog(logText, textToAdd);
        logMap.put(channelId, logText);
        if (TextUtils.equals(channelId, curChannelId)) {
            LogMonitorUtil.updateLog(tvLogMonitor, logText);
        }
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
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            int code = result.getCode();
            if (code != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId, String.format(Locale.getDefault(),
                    "订阅：%s订阅频道%s失败，code=%d，msg=%s", UserUtil.getOpenId(), channelId, code, result.getMsg()));
                return;
            }
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "订阅：%s订阅了频道%s", UserUtil.getOpenId(), channelId));
            updateSubscribedChannels(channelId);
            queryChannelHistoryMessages(channelId);
        });
    }

    @Override
    public void onUnSubscribeRtmChannel(UnSubscribeRtmChannelResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            int code = result.getCode();
            if (code != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId, String.format(Locale.getDefault(),
                    "退订：%s退订频道%s失败，code=%d，msg=%s", UserUtil.getOpenId(), channelId, code, result.getMsg()));
                return;
            }
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "退订：%s退订了频道%s", UserUtil.getOpenId(), channelId));
            updateSubscribedChannels("");
        });
    }

    @Override
    public void onPublishRtmChannelMessage(PublishRtmChannelMessageResult result) {
        runOnUiThread(() -> {
            RtmMessageContent rtmMessage = msgCacheMap.remove(result.getClientMsgId());
            String type = "";
            String content = "";
            if (rtmMessage != null) {
                type = rtmMessage.getMessageType() == MSG_TYPE_TEXT ? "文本" : "二进制";
                content = rtmMessage.getMessageType() == MSG_TYPE_TEXT ? rtmMessage.getMessageString()
                    : new String(rtmMessage.getMessageBytes(), StandardCharsets.UTF_8);
            }
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId,
                    String.format(Locale.getDefault(), "频道消息（%s）：%s发送到频道%s的消息：%s，发送失败，clientMsgId=%s，code=%d，msg=%s",
                        type, UserUtil.getOpenId(), channelId, content, result.getClientMsgId(), result.getCode(),
                        result.getMsg()));
                return;
            }
            appendLog2MonitorView(channelId, String.format(Locale.getDefault(),
                "频道消息（%s）：%s发送到频道%s的消息：%s", type, UserUtil.getOpenId(), channelId, content));
        });
    }

    @Override
    public void onPublishRtmPeerMessage(PublishRtmPeerMessageResult result) {

    }

    @Override
    public void onGetRtmChannelInfo(GetRtmChannelInfoResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "查询频道%s信息失败，code=%d，msg=%s",
                    channelId, result.getCode(), result.getMsg()));
                return;
            }
            StringBuilder log = new StringBuilder();
            StringBuilder loggedUsers = new StringBuilder();
            if (CollectionUtils.isNotEmpty(result.getMemberInfos())) {
                for (RtmChannelMemberInfo memberInfo : result.getMemberInfos()) {
                    log.append(memberInfo.getOpenId());
                    log.append("(").append(memberInfo.getStatus() == 1 ? "在线" : "离线").append(")");
                    log.append(":").append(gson.toJson(memberInfo.getPlayerProperties())).append(";");
                    loggedUsers.append(memberInfo.getOpenId());
                    loggedUsers.append("(").append(memberInfo.getStatus() == 1 ? "在线" : "离线").append(")").append(",");
                }
            }
            appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "查询频道%s信息，频道玩家数：%d，玩家属性：%s", channelId,
                result.getMemberCount(), log));
            if (!StringUtil.isEmpty(loggedUsers.toString())) {
                tvLoggedInUsers.setText(loggedUsers.substring(0, loggedUsers.length() - 1));
            } else {
                tvLoggedInUsers.setText("");
            }
        });

    }

    @Override
    public void onReceiveRtmChannelMessage(ReceiveRtmChannelMessageNotify notify) {
        runOnUiThread(() -> {
            String channelId = notify.getChannelId();
            int messageType = notify.getMessageType();
            String type = messageType == MSG_TYPE_TEXT ? "文本" : "二进制";
            String content = messageType == MSG_TYPE_TEXT ? notify.getMessageString()
                : new String(notify.getMessageBytes(), StandardCharsets.UTF_8);
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "频道消息（%s）：%s收到%s发送到频道%s的消息：%s", type, UserUtil.getOpenId(),
                    notify.getSenderId(), channelId, content));
        });
    }

    @Override
    public void onReceiveRtmPeerMessage(ReceiveRtmPeerMessageNotify notify) {

    }

    @Override
    public void onRtmConnectionChanged(RtmConnectionStatusNotify notify) {

    }

    @Override
    public void onSetRtmChannelPlayerProperties(SetRtmChannelPlayerPropertiesResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId, String.format(Locale.getDefault(),
                    "设置用户%s的属性：失败，code=%d，msg=%s", UserUtil.getOpenId(), result.getCode(), result.getMsg()));
                return;
            }
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "设置用户%s的属性：成功", UserUtil.getOpenId()));
        });
    }

    @Override
    public void onGetRtmChannelPlayerProperties(GetRtmChannelPlayerPropertiesResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId,
                    String.format(Locale.getDefault(), "查询用户属性失败，code=%d，msg=%s", result.getCode(), result.getMsg()));
                return;
            }
            List<RtmChannelMemberInfo> memberInfos = result.getMemberInfos();
            if (memberInfos == null || memberInfos.isEmpty() || memberInfos.get(0) == null) {
                appendLog2MonitorView(channelId, "查询用户属性：{}");
                return;
            }
            RtmChannelMemberInfo info = memberInfos.get(0);
            Map<String, String> playerProperties = info.getPlayerProperties();
            if (TextUtils.equals(info.getOpenId(), UserUtil.getOpenId())) {
                updatePlayerProperties(playerProperties);
            }
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "查询用户%s的属性：%s", info.getOpenId(), gson.toJson(playerProperties)));
        });
    }

    @Override
    public void onDeleteRtmChannelPlayerProperties(DeleteRtmChannelPlayerPropertiesResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "用户属性：频道%s属性删除 code=%d, msg=%s",
                channelId, result.getCode(), result.getMsg()));
        });
    }

    @Override
    public void onSetRtmChannelProperties(SetRtmChannelPropertiesResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "设置频道%s的属性：失败，code=%d，msg=%s",
                    channelId, result.getCode(), result.getMsg()));
                return;
            }
            appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "设置频道%s的属性：成功", channelId));
        });
    }

    @Override
    public void onGetRtmChannelProperties(GetRtmChannelPropertiesResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "查询频道%s的属性：失败，code=%d，msg=%s",
                    channelId, result.getCode(), result.getMsg()));
                return;
            }
            Map<String, String> properties = result.getChannelProperties();
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "查询频道%s的属性：%s", channelId, gson.toJson(properties)));
            updateChannelProperties(properties);
        });
    }

    @Override
    public void onDeleteRtmChannelProperties(DeleteRtmChannelPropertiesResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "删除频道%s的属性：失败，code=%d，msg=%s",
                    channelId, result.getCode(), result.getMsg()));
                return;
            }
            appendLog2MonitorView(channelId, String.format(Locale.getDefault(), "删除频道%s的属性：成功", channelId));
        });
    }

    @Override
    public void onGetRtmChannelHistoryMessages(GetRtmChannelHistoryMessagesResult result) {
        runOnUiThread(() -> {
            String channelId = result.getChannelId();
            if (result.getCode() != GmmeConstants.MmsdkResult.SUCCESS) {
                appendLog2MonitorView(channelId,
                    String.format(Locale.getDefault(), "历史消息查询失败，code=%d，msg=%s", result.getCode(), result.getMsg()));
                return;
            }
            List<RtmChannelHistoryMessage> msgs = result.getChannelMessages();
            if (msgs == null) {
                return;
            }

            for (int i = msgs.size() - 1; i >= 0; i--) {
                RtmChannelHistoryMessage msg = msgs.get(i);
                if (msg == null) {
                    continue;
                }
                String type = msg.getMessageType() == MSG_TYPE_TEXT ? "文本" : "二进制";
                String content = msg.getMessageType() == MSG_TYPE_TEXT ? msg.getMessageString()
                    : new String(msg.getMessageBytes(), StandardCharsets.UTF_8);
                String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date(msg.getTimestamp()));
                appendLog2MonitorView(channelId,
                    String.format(Locale.getDefault(), "历史消息（%s）：%s收到%s发送到频道%s的消息：%s，发送时间：%s", type,
                        UserUtil.getOpenId(), msg.getSenderId(), channelId, content, dateString));
            }
        });
    }

    @Override
    public void onRtmChannelPlayerPropertiesChanged(RtmChannelPlayerPropertiesNotify notify) {
        runOnUiThread(() -> {
            String channelId = notify.getChannelId();
            RtmChannelMemberInfo playerInfo = notify.getPlayerInfo();
            Map<String, String> properties = playerInfo.getPlayerProperties();
            if (TextUtils.equals(playerInfo.getOpenId(), UserUtil.getOpenId())) {
                updatePlayerProperties(properties);
            }
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "玩家%s的属性变更：%s", playerInfo.getOpenId(), gson.toJson(properties)));
        });
    }

    @Override
    public void onRtmChannelPropertiesChanged(RtmChannelPropertiesNotify notify) {
        runOnUiThread(() -> {
            String channelId = notify.getChannelId();
            Map<String, String> channelProperties = notify.getChannelProperties();
            appendLog2MonitorView(channelId,
                String.format(Locale.getDefault(), "频道%s的属性变更：%s", channelId, gson.toJson(channelProperties)));
            updateChannelProperties(channelProperties);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        LogUtil.i(TAG, "Selected item: " + curChannelId);
        Object selectedItem = parent.getSelectedItem();
        curChannelId = selectedItem == null ? "" : selectedItem.toString();
        LogMonitorUtil.updateLog(tvLogMonitor, logMap.get(curChannelId));
        if (vpMain.getCurrentItem() == 1) {
            queryChannelProperties(true);
        } else if (vpMain.getCurrentItem() == 2) {
            queryPlayerProperties(UserUtil.getOpenId(), true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LogUtil.i(TAG, "No item selected.");
        curChannelId = "";
        LogMonitorUtil.updateLog(tvLogMonitor, logMap.get(curChannelId));
        glChannelProperty.removeAllViews();
        glPlayerProperty.removeAllViews();
    }

    /**
     * 页面编号
     */
    interface PageNum {
        /**
         * 消息页面
         */
        int PAGE_NUM_MSG = 0;

        /**
         * 频道属性页面
         */
        int PAGE_NUM_CHANNEL_PROP = 1;

        /**
         * 用户属性页面
         */
        int PAGE_NUM_PLAYER_PROP = 2;
    }
}