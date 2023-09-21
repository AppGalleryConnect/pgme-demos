/**
 * Copyright 2023. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.huawei.gmmesdk.demo.activity;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.constant.Constant;

/**
 * 音效界面
 *
 * @since 2023-07-13
 */
public class AudioScopePopup extends PopupWindow {
    private static final String SUB = "sub";

    private static final String ADD = "add";

    private static final int POSITION_MIN = -100;

    private static final int POSITION_MAX = 100;

    private static final int AXIS_MIN = -180;

    private static final int AXIS_MAX = 180;

    private static final String REG = "^-?[1-9]\\d*$";

    private GameMediaEngine mHwRtcEngine;

    private Context context;

    private List<String> players;

    private EditText distanceX;

    private EditText distanceY;

    private EditText distanceZ;

    private EditText rotateX;

    private EditText rotateY;

    private EditText rotateZ;

    private Button distanceXAddBtn;

    private Button distanceXSubBtn;

    private Button distanceYAddBtn;

    private Button distanceYSubBtn;

    private Button distanceZAddBtn;

    private Button distanceZSubBtn;

    private Button rotateXAddBtn;

    private Button rotateXSubBtn;

    private Button rotateYAddBtn;

    private Button rotateYSubBtn;

    private Button rotateZAddBtn;

    private Button rotateZSubBtn;

    public AudioScopePopup(Context context, GameMediaEngine mHwRtcEngine, List<String> mEntityList,
        String localPlayerId) {
        super(context);
        this.mHwRtcEngine = mHwRtcEngine;
        this.context = context;
        this.players = mEntityList;
        View contentView = LayoutInflater.from(context).inflate(R.layout.audio_scope, null, false);
        setFocusable(true);
        setOutsideTouchable(true);

        SelfPositionRotateInfo selfPositionRotateInfo = SelfPositionRotateInfo.getInstance();
        selfPositionRotateInfo.setOpenIds(players);
        selfPositionRotateInfo.setLocalOpenId(localPlayerId);
        distanceX = contentView.findViewById(R.id.distance_x);
        distanceX.setText(String.valueOf(selfPositionRotateInfo.getPosX()));
        distanceY = contentView.findViewById(R.id.distance_y);
        distanceY.setText(String.valueOf(selfPositionRotateInfo.getPosY()));
        distanceZ = contentView.findViewById(R.id.distance_z);
        distanceZ.setText(String.valueOf(selfPositionRotateInfo.getPosZ()));

        rotateX = contentView.findViewById(R.id.rotate_x);
        rotateX.setText(String.valueOf(selfPositionRotateInfo.getRotX()));
        rotateY = contentView.findViewById(R.id.rotate_y);
        rotateY.setText(String.valueOf(selfPositionRotateInfo.getRotY()));
        rotateZ = contentView.findViewById(R.id.rotate_z);
        rotateZ.setText(String.valueOf(selfPositionRotateInfo.getRotZ()));

        distanceXAddBtn = contentView.findViewById(R.id.distance_x_add);
        distanceXSubBtn = contentView.findViewById(R.id.distance_x_sub);
        distanceYAddBtn = contentView.findViewById(R.id.distance_y_add);
        distanceYSubBtn = contentView.findViewById(R.id.distance_y_sub);
        distanceZAddBtn = contentView.findViewById(R.id.distance_z_add);
        distanceZSubBtn = contentView.findViewById(R.id.distance_z_sub);

        rotateXAddBtn = contentView.findViewById(R.id.rotate_x_add);
        rotateXSubBtn = contentView.findViewById(R.id.rotate_x_sub);
        rotateYAddBtn = contentView.findViewById(R.id.rotate_y_add);
        rotateYSubBtn = contentView.findViewById(R.id.rotate_y_sub);
        rotateZAddBtn = contentView.findViewById(R.id.rotate_z_add);
        rotateZSubBtn = contentView.findViewById(R.id.rotate_z_sub);

        buttonListener(selfPositionRotateInfo, distanceXAddBtn, distanceX, context.getString(R.string.x_axis),
            Constant.PlayerPositionSetType.Position, ADD);
        buttonListener(selfPositionRotateInfo, distanceXSubBtn, distanceX, context.getString(R.string.x_axis),
            Constant.PlayerPositionSetType.Position, SUB);

        buttonListener(selfPositionRotateInfo, distanceYAddBtn, distanceY, context.getString(R.string.y_axis),
            Constant.PlayerPositionSetType.Position, ADD);
        buttonListener(selfPositionRotateInfo, distanceYSubBtn, distanceY, context.getString(R.string.y_axis),
            Constant.PlayerPositionSetType.Position, SUB);

        buttonListener(selfPositionRotateInfo, distanceZAddBtn, distanceZ, context.getString(R.string.z_axis),
            Constant.PlayerPositionSetType.Position, ADD);
        buttonListener(selfPositionRotateInfo, distanceZSubBtn, distanceZ, context.getString(R.string.z_axis),
            Constant.PlayerPositionSetType.Position, SUB);

        // ======================rotate======================
        buttonListener(selfPositionRotateInfo, rotateXAddBtn, rotateX, context.getString(R.string.x_axis),
            Constant.PlayerPositionSetType.Axis, ADD);
        buttonListener(selfPositionRotateInfo, rotateXSubBtn, rotateX, context.getString(R.string.x_axis),
            Constant.PlayerPositionSetType.Axis, SUB);

        buttonListener(selfPositionRotateInfo, rotateYAddBtn, rotateY, context.getString(R.string.y_axis),
            Constant.PlayerPositionSetType.Axis, ADD);
        buttonListener(selfPositionRotateInfo, rotateYSubBtn, rotateY, context.getString(R.string.y_axis),
            Constant.PlayerPositionSetType.Axis, SUB);

        buttonListener(selfPositionRotateInfo, rotateZAddBtn, rotateZ, context.getString(R.string.z_axis),
            Constant.PlayerPositionSetType.Axis, ADD);
        buttonListener(selfPositionRotateInfo, rotateZSubBtn, rotateZ, context.getString(R.string.z_axis),
            Constant.PlayerPositionSetType.Axis, SUB);

        editTextListener(selfPositionRotateInfo, distanceX, context.getString(R.string.x_axis),
            Constant.PlayerPositionSetType.Position);
        editTextListener(selfPositionRotateInfo, distanceY, context.getString(R.string.y_axis),
            Constant.PlayerPositionSetType.Position);
        editTextListener(selfPositionRotateInfo, distanceZ, context.getString(R.string.z_axis),
            Constant.PlayerPositionSetType.Position);
        editTextListener(selfPositionRotateInfo, rotateX, context.getString(R.string.x_axis),
            Constant.PlayerPositionSetType.Axis);
        editTextListener(selfPositionRotateInfo, rotateY, context.getString(R.string.y_axis),
            Constant.PlayerPositionSetType.Axis);
        editTextListener(selfPositionRotateInfo, rotateZ, context.getString(R.string.z_axis),
            Constant.PlayerPositionSetType.Axis);

        // 显示视图文件
        setContentView(contentView);
    }

    private void buttonListener(SelfPositionRotateInfo selfPositionRotateInfo, Button button, EditText editText,
        String coordinates, int setType, String calType) {
        button.setOnClickListener(v -> {
            int min = setType == Constant.PlayerPositionSetType.Position ? POSITION_MIN : AXIS_MIN;
            int max = setType == Constant.PlayerPositionSetType.Position ? POSITION_MAX : AXIS_MAX;
            String text = editText.getText().toString().trim();
            String number = getNumber(text);
            int value = Objects.equals(SUB, calType) ? Math.max(Integer.parseInt(number) - 5, min)
                : Math.min(Integer.parseInt(number) + 5, max);
            editText.setText(String.valueOf(value));
            reportSelfPositionAndAxis(selfPositionRotateInfo, coordinates, setType, value);
        });
    }

    private void editTextListener(SelfPositionRotateInfo selfPositionRotateInfo, EditText editText, String coordinates,
        int setType) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            int min = setType == Constant.PlayerPositionSetType.Position ? POSITION_MIN : AXIS_MIN;
            int max = setType == Constant.PlayerPositionSetType.Position ? POSITION_MAX : AXIS_MAX;
            String text = editText.getText().toString().trim();
            String number = getNumber(text);
            int value = Math.max(Integer.parseInt(number), min);
            value = Math.min(value, max);
            editText.setText(String.valueOf(value));
            reportSelfPositionAndAxis(selfPositionRotateInfo, coordinates, setType, value);
            return false;
        });
    }

    @NonNull
    private String getNumber(String text) {
        Pattern pattern = Pattern.compile(REG);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches() ? text : "0";
    }

    private void reportSelfPositionAndAxis(SelfPositionRotateInfo selfPositionRotateInfo, String coordinates,
        int setType, int value) {
        if (coordinates.equals(context.getString(R.string.x_axis))
            && setType == Constant.PlayerPositionSetType.Position) {
            selfPositionRotateInfo.setPosX(value);
        }
        if (coordinates.equals(context.getString(R.string.y_axis))
            && setType == Constant.PlayerPositionSetType.Position) {
            selfPositionRotateInfo.setPosY(value);
        }
        if (coordinates.equals(context.getString(R.string.z_axis))
            && setType == Constant.PlayerPositionSetType.Position) {
            selfPositionRotateInfo.setPosZ(value);
        }
        if (coordinates.equals(context.getString(R.string.x_axis)) && setType == Constant.PlayerPositionSetType.Axis) {
            selfPositionRotateInfo.setRotX(value);
        }
        if (coordinates.equals(context.getString(R.string.y_axis)) && setType == Constant.PlayerPositionSetType.Axis) {
            selfPositionRotateInfo.setRotY(value);
        }
        if (coordinates.equals(context.getString(R.string.z_axis)) && setType == Constant.PlayerPositionSetType.Axis) {
            selfPositionRotateInfo.setRotZ(value);
        }
        selfPositionRotateInfo.updateSelfPosition(this.context);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}