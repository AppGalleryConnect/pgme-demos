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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.game.common.utils.StringUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.PlayerPosition;
import com.huawei.game.gmme.model.RemotePlayerPosition;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.adapter.ScopePlayerListsAdapter;
import com.huawei.gmmesdk.demo.component.PlayPositionDialog;
import com.huawei.gmmesdk.demo.constant.Constant;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class PlayerPositionActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 日志标签
     */
    private static final String PLAYER_POSITION_TAG = PlayerPositionActivity.class.getSimpleName();

    private RecyclerView recyclerView;

    protected ScopePlayerListsAdapter scopePlayerListsAdapter;

    /**
     * HRTCEngine
     */
    protected GameMediaEngine mHwRtcEngine;

    private Button btnAddPlayer;

    private Button btnClearAllPlayer;

    // 远程玩家位置数据
    private List<RemotePlayerPosition> mPlayerList = new ArrayList<>();

    private SharedPreferences shareData = null;

    private Gson gson = new Gson();

    private String mLocalOpenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_position);
        recyclerView = findViewById(R.id.play_list);

        btnAddPlayer = findViewById(R.id.add_player);
        btnClearAllPlayer = findViewById(R.id.clear_all_player);

        btnAddPlayer.setOnClickListener(this);
        btnClearAllPlayer.setOnClickListener(this);

        shareData = getSharedPreferences("playerPositions", MODE_PRIVATE);

        mLocalOpenId = ((GmmeApplication) getApplication()).getOpenId();

        initAllPlayerPosition();

        refreshPlayerListWithThread(recyclerView, this);
    }

    private void initAllPlayerPosition() {
        // 获取远程玩家位置缓存信息
        String mPlayerListStr = shareData.getString("mRemotePlayerList", "");
        if (!StringUtil.isEmpty(mPlayerListStr)) {
            mPlayerList = gson.fromJson(mPlayerListStr, new TypeToken<List<RemotePlayerPosition>>() {}.getType());
        }
    }

    /**
     * 新增玩家
     */
    private void onAddPlayer() {
        PlayPositionDialog playPositionDialog =
            new PlayPositionDialog(this, "请输入其他玩家openId", null, Constant.PlayerPositionDialogType.AddPlayer);
        playPositionDialog.showAtLocation(btnAddPlayer, Gravity.CENTER, 0, 0);
        playPositionDialog.setOnDataListener((param, dialogType) -> {
            if (StringUtil.isEmpty(param.trim())) {
                Toast.makeText(getApplicationContext(), "玩家不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (param.trim().equals(mLocalOpenId)) {
                Toast.makeText(getApplicationContext(), "本人无需添加", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dialogType == Constant.PlayerPositionDialogType.AddPlayer) {
                for (RemotePlayerPosition player : mPlayerList) {
                    if (player.getOpenId().equals(param)) {
                        Toast.makeText(getApplicationContext(), "该玩家已经添加", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                RemotePlayerPosition remotePlayerPosition = new RemotePlayerPosition();
                remotePlayerPosition.setOpenId(param);
                remotePlayerPosition.setPosition(new PlayerPosition(0, 0, 0));
                mPlayerList.add(remotePlayerPosition);
                int result = mHwRtcEngine.updateRemotePosition(mPlayerList);
                if (result != 0) {
                    Toast.makeText(this, "更新远程玩家失败,code:" + result, Toast.LENGTH_SHORT).show();
                }
                shareData.edit().putString("mRemotePlayerList", gson.toJson(mPlayerList)).apply();
                refreshPlayerListWithThread(recyclerView, this);
            }
        });
    }

    /**
     * 清空所有玩家
     */
    private void onClearAllPlayer() {
        int result = mHwRtcEngine.clearAllRemotePositions();
        if (result == 0) {
            mPlayerList.clear();
            shareData.edit().remove("mRemotePlayerList").apply();
            // 刷新页面
            refreshPlayerListWithThread(recyclerView, this);
        } else {
            Toast.makeText(this, "清空所有玩家失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_player:
                onAddPlayer();
                break;
            case R.id.clear_all_player:
                onClearAllPlayer();
                break;
        }
    }

    /**
     * 刷新与会列表初始化
     *
     * @param playerListView 用户列表父布局
     */
    protected void refreshPlayerListWithThread(RecyclerView playerListView, Context context) {
        // 更新UI，定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(PlayerPositionActivity.this);

        // 设置布局管理器
        playerListView.setLayoutManager(manager);

        mHwRtcEngine = ((GmmeApplication) getApplication()).getEngine();

        // 设置adapter
        scopePlayerListsAdapter = new ScopePlayerListsAdapter(context, mHwRtcEngine, mPlayerList);
        playerListView.setAdapter(scopePlayerListsAdapter);

        // 增加分割线
        if (playerListView.getItemDecorationCount() != RecyclerView.VERTICAL) {
            playerListView
                .addItemDecoration(new DividerItemDecoration(PlayerPositionActivity.this, RecyclerView.VERTICAL));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
