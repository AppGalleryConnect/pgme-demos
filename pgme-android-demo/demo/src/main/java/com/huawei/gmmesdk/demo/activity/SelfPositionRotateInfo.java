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
import android.widget.Toast;

import java.util.List;

import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.Axis;
import com.huawei.game.gmme.model.PlayerPosition;
import com.huawei.game.gmme.model.SelfPosition;

/**
 * 自身坐标信息
 *
 * @since 2023-07-18
 */
public class SelfPositionRotateInfo {
    protected GameMediaEngine hwRtcEngine;

    private String localOpenId;

    private List<String> openIds;

    private int posX;

    private int posY;

    private int posZ;

    private int rotX;

    private int rotY;

    private int rotZ;

    private SelfPositionRotateInfo() {
    }

    public static SelfPositionRotateInfo getInstance() {
        return Singleton.INSTANCE;
    }

    public void clear() {
        this.posX = 0;
        this.posY = 0;
        this.posZ = 0;
        this.rotX = 0;
        this.rotY = 0;
        this.rotZ = 0;
    }

    /**
     * 单例模式静态内部类
     *
     * @since 2023-03-07
     */
    private static class Singleton {
        /**
         * 单例初始化
         */
        private static final SelfPositionRotateInfo INSTANCE = new SelfPositionRotateInfo();
    }

    public List<String> getOpenIds() {
        return openIds;
    }

    public void setOpenIds(List<String> openIds) {
        this.openIds = openIds;
    }

    public String getLocalOpenId() {
        return localOpenId;
    }

    public void setLocalOpenId(String localOpenId) {
        this.localOpenId = localOpenId;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getPosZ() {
        return posZ;
    }

    public void setPosZ(int posZ) {
        this.posZ = posZ;
    }

    public int getRotX() {
        return rotX;
    }

    public void setRotX(int rotX) {
        this.rotX = rotX;
    }

    public int getRotY() {
        return rotY;
    }

    public void setRotY(int rotY) {
        this.rotY = rotY;
    }

    public int getRotZ() {
        return rotZ;
    }

    public void setRotZ(int rotZ) {
        this.rotZ = rotZ;
    }

    public GameMediaEngine getHwRtcEngine() {
        return hwRtcEngine;
    }

    public void setHwRtcEngine(GameMediaEngine mHwRtcEngine) {
        this.hwRtcEngine = mHwRtcEngine;
    }

    public void updateSelfPosition(Context context) {
        float[] axisForward = new float[3];
        axisForward[0] = (float) (Math.cos(Math.toRadians(rotZ)) * Math.cos(Math.toRadians(rotY)));
        axisForward[1] = (float) (Math.sin(Math.toRadians(rotZ)) * Math.cos(Math.toRadians(rotY)));
        axisForward[2] = -(float) Math.sin(Math.toRadians(rotY));
        float[] axisRight = new float[3];
        axisRight[0] =
            (float) (Math.cos(Math.toRadians(rotZ)) * Math.sin(Math.toRadians(rotY)) * Math.sin(Math.toRadians(rotX))
                - Math.sin(Math.toRadians(rotZ)) * Math.cos(Math.toRadians(rotX)));
        axisRight[1] = (float) (Math.cos(Math.toRadians(rotZ)) * Math.cos(Math.toRadians(rotX))
            + Math.sin(Math.toRadians(rotZ)) * Math.sin(Math.toRadians(rotY)) * Math.sin(Math.toRadians(rotX)));
        axisRight[2] = (float) (Math.cos(Math.toRadians(rotY)) * Math.sin(Math.toRadians(rotX)));
        float[] axisUp = new float[3];
        axisUp[0] = (float) (Math.sin(Math.toRadians(rotZ)) * Math.sin(Math.toRadians(rotX))
            + Math.cos(Math.toRadians(rotZ)) * Math.sin(Math.toRadians(rotY)) * Math.cos(Math.toRadians(rotX)));
        axisUp[1] =
            (float) (Math.sin(Math.toRadians(rotZ)) * Math.sin(Math.toRadians(rotY)) * Math.cos(Math.toRadians(rotX))
                - Math.cos(Math.toRadians(rotZ)) * Math.sin(Math.toRadians(rotX)));
        axisUp[2] = (float) (Math.cos(Math.toRadians(rotY)) * Math.cos(Math.toRadians(rotX)));

        SelfPosition selfPosition = new SelfPosition();
        selfPosition.setPosition(new PlayerPosition(posX, posY, posZ));
        selfPosition.setAxis(new Axis(axisForward, axisRight, axisUp));
        int result = hwRtcEngine.updateSelfPosition(selfPosition);
        if (result != 0) {
            Toast.makeText(context, "更新本地玩家失败,code:" + result, Toast.LENGTH_SHORT).show();
        }
    }

}