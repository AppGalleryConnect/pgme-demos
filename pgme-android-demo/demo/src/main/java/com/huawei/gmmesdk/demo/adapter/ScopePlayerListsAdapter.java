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

package com.huawei.gmmesdk.demo.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.RemotePlayerPosition;
import com.huawei.gmmesdk.demo.R;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 玩家位置列表adapter
 *
 * @since 2023-04-10
 */
public class ScopePlayerListsAdapter extends RecyclerView.Adapter {
    private static final String REG = "^-?[1-9]\\d*$";

    private static final int POSITION_MIN = -100;

    private static final int POSITION_MAX = 100;

    private Context context;

    private GameMediaEngine mHwRtcEngine;

    private List<RemotePlayerPosition> mPlayerList;

    private SharedPreferences shareData = null;

    private Gson gson = new Gson();

    /**
     * 构造方法
     *
     * @param mHwRtcEngine
     */
    public ScopePlayerListsAdapter(Context context, GameMediaEngine mHwRtcEngine,
        List<RemotePlayerPosition> mPlayerList) {
        this.mHwRtcEngine = mHwRtcEngine;
        this.context = context;
        this.mPlayerList = mPlayerList;
        shareData = this.context.getSharedPreferences("playerPositions", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view =
            LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_player_position, viewGroup, false);
        return new PlayerListRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RemotePlayerPosition entity = mPlayerList.get(position);
        PlayerListRecyclerHolder viewHolder = ((PlayerListRecyclerHolder) holder);
        String openId = entity.getOpenId();
        viewHolder.userName.setText(openId);

        setItemPlayerPosition(viewHolder.distanceXAdd, viewHolder.distanceXSub, viewHolder.xPosition,
            context.getString(R.string.x_axis), openId, (int) entity.getPosition().getRight());
        setItemPlayerPosition(viewHolder.distanceYAdd, viewHolder.distanceYSub, viewHolder.yPosition,
            context.getString(R.string.y_axis), openId, (int) entity.getPosition().getUp());
        setItemPlayerPosition(viewHolder.distanceZAdd, viewHolder.distanceZSub, viewHolder.zPosition,
            context.getString(R.string.z_axis), openId, (int) entity.getPosition().getForward());

        editTextListener(viewHolder.xPosition, context.getString(R.string.x_axis), openId);
        editTextListener(viewHolder.yPosition, context.getString(R.string.y_axis), openId);
        editTextListener(viewHolder.zPosition, context.getString(R.string.z_axis), openId);

        viewHolder.btnDelete.setOnClickListener(v -> {
            int result = mHwRtcEngine.clearRemotePlayerPosition(openId);
            if (result == 0) {
                Iterator<RemotePlayerPosition> iterator = mPlayerList.iterator();
                while (iterator.hasNext()) {
                    RemotePlayerPosition item = iterator.next();
                    if (item.getOpenId().equals(openId)) {
                        iterator.remove();
                        break;
                    }
                }
                this.notifyItemRemoved(position);
                shareData.edit().putString("mRemotePlayerList", gson.toJson(mPlayerList)).apply();
            } else {
                Toast.makeText(context, "删除玩家失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editTextListener(EditText editText, String coordinates, String openId) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            String text = editText.getText().toString().trim();
            String number = getNumber(text);
            int value = Math.max(Integer.parseInt(number), POSITION_MIN);
            value = Math.min(value, POSITION_MAX);
            editText.setText(String.valueOf(value));
            // 调用设置坐标
            calRemotePosition(coordinates, openId, value);
            return false;
        });
    }

    @NonNull
    private String getNumber(String text) {
        Pattern pattern = Pattern.compile(REG);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches() ? text : "0";
    }

    private void setItemPlayerPosition(Button distanceAdd, Button distanceSub, EditText editText, String coordinates,
        String openId, int distance) {

        editText.setText(String.valueOf(distance));

        distanceAdd.setOnClickListener(v -> {
            String number = getNumber(editText.getText().toString().trim());
            int dis = Integer.parseInt(number) + 5;
            int result = Math.min(dis, POSITION_MAX);
            editText.setText(String.valueOf(result));
            // 调用设置坐标
            calRemotePosition(coordinates, openId, result);
        });

        distanceSub.setOnClickListener(v -> {
            String number = getNumber(editText.getText().toString().trim());
            int dis = Integer.parseInt(number) - 5;
            int result = Math.max(dis, POSITION_MIN);
            editText.setText(String.valueOf(result));
            // 调用设置坐标
            calRemotePosition(coordinates, openId, result);
        });
    }

    /**
     * 计算远程玩家坐标
     * 
     * @param coordinates 坐标
     * @param openId openId
     * @param currentDistance 当前距离
     */
    private void calRemotePosition(String coordinates, String openId, int currentDistance) {
        // 调用设置坐标
        RemotePlayerPosition mPlayerPosition = null;
        for (RemotePlayerPosition remotePlayerPosition : mPlayerList) {
            if (remotePlayerPosition != null && remotePlayerPosition.getOpenId().equals(openId)) {
                mPlayerPosition = remotePlayerPosition;
                break;
            }
        }
        if (mPlayerPosition != null) {
            if (context.getString(R.string.x_axis).equals(coordinates)) {
                mPlayerPosition.getPosition().setRight(currentDistance);
            }
            if (context.getString(R.string.y_axis).equals(coordinates)) {
                mPlayerPosition.getPosition().setUp(currentDistance);
            }
            if (context.getString(R.string.z_axis).equals(coordinates)) {
                mPlayerPosition.getPosition().setForward(currentDistance);
            }
        }
        shareData.edit().putString("mRemotePlayerList", gson.toJson(mPlayerList)).apply();
        int result = mHwRtcEngine.updateRemotePosition(mPlayerList);
        if (result != 0) {
            Toast.makeText(context, "更新远程玩家失败,code:" + result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mPlayerList.size();
    }

    private static class PlayerListRecyclerHolder extends RecyclerView.ViewHolder {
        private TextView userName;

        private Button distanceXSub;

        private Button distanceYSub;

        private Button distanceZSub;

        private Button distanceXAdd;

        private Button distanceYAdd;

        private Button distanceZAdd;

        private EditText xPosition;

        private EditText yPosition;

        private EditText zPosition;

        private Button btnDelete;

        public PlayerListRecyclerHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.item_user_id);
            distanceXSub = itemView.findViewById(R.id.distance_x_sub);
            distanceYSub = itemView.findViewById(R.id.distance_y_sub);
            distanceZSub = itemView.findViewById(R.id.distance_z_sub);

            distanceXAdd = itemView.findViewById(R.id.distance_x_add);
            distanceYAdd = itemView.findViewById(R.id.distance_y_add);
            distanceZAdd = itemView.findViewById(R.id.distance_z_add);

            xPosition = itemView.findViewById(R.id.distance_x);
            yPosition = itemView.findViewById(R.id.distance_y);
            zPosition = itemView.findViewById(R.id.distance_z);

            btnDelete = itemView.findViewById(R.id.item_delete);
        }
    }
}