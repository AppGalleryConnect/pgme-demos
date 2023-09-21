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

package com.huawei.gmmesdk.demo.constant;

/**
 * Request code
 *
 * @since 2023-04-10
 */
public interface Constant {
    /**
     * 记录日志标识
     */
    int IS_LOG = 1;

    /**
     * 点击时间间隔
     */
    int TIME_INTERVAL = 10000;

    /**
     * 空串
     */
    String EMPTY_STRING = "";

    /**
     * 默认用户ID
     */
    String KEY_USER_ID = "user_id";

    /**
     * 默认用户名
     */
    String KEY_USER_NAME = "user_name";

    /**
     * 话筒按钮关闭标签
     */
    String UN_SELECT = "unSelect";

    /**
     * 话筒按钮打开标签
     */
    String SELECT = "select";

    /**
     * 玩家房间角色 1-joiner 2-player
     */
    int JOINER = 1;

    int PLAYER = 2;

    /**
     * 房间类型 1-TEAMROOM 2-NATIONALROOM 3-RANGEROOM
     */
    int TEAMROOM = 1;

    int NATIONALROOM = 2;

    int RANGEROOM = 3;

    /**
     * 玩家操作房间类型 1-LEAVEROOM 2-JOINROOM 3-SWITCHROOM
     */
    int LEAVEROOM = 1;

    int SWITCHROOM = 3;

    /**
     * 间隔周期
     */
    int INTERVAL_PERIOD = 1000;

    /**
     * 小喇叭闪烁刷新周期
     */
    long REFRESH_MEMBER_LIST_PERIOD = 2000L;

    /**
     * 音频格式
     */
    String AUDIO_TYPE = ".m4a";

    String[] roomTitle = {"房间", "小队", "国战", "范围"};

    /**
     * 记录日志相关常量
     */
    interface LogConstants {

        /**
         * 消息
         */
        String MESSAGE = "message";

        /**
         * 响应code
         */
        String CODE = "status";

        /**
         * 用户ID
         */
        String USER_ID = "userId";

        /**
         * 用户ID集合
         */
        String USER_IDS = "userIds";

        /**
         * 房间ID
         */
        String ROOMID = "roomId";

        /**
         * 房间类型
         */
        String TYPE = "type";

        /**
         * 是否屏蔽
         */
        String IS_MUTED = "isMuted";

        /**
         * 是否禁言
         */
        String IS_FORBIDDEN = "isForbidden";
    }

    /**
     * 消息类型（1文本，2音频）
     */
    interface MsgType {
        /**
         * 文本
         */
        int MSG_TYPE_TEXT = 1;

        /**
         * 音频
         */
        int MSG_TYPE_AUDIO = 2;
    }

    /**
     * 麦克风相关的操作
     */
    enum MicOperateTypeEnum {
        MicOperate,
        VoiceToText,
        VoiceMsg
    }

    /**
     * 音效类型
     */
    interface AudioClipType {
        /**
         * 全部音效
         */
        int AllAudioClip = 1;

        /**
         * 音效1
         */
        int AudioClipOne = 2;

        /**
         * 音效2
         */
        int AudioClipTwo = 3;
    }

    /**
     * 资源存放目录
     */
    interface ResourcesSaveDir {
        /**
         * 目录
         */
        String MUSIC = "music";
    }

    /**
     * 玩家位置弹框类型
     */
    interface PlayerPositionDialogType {
        /**
         * 新增玩家
         */
        int AddPlayer = 1;

        /**
         * 设置范围
         */
        int Scope = 2;
    }

    /**
     * 玩家位置设置类型
     */
    interface PlayerPositionSetType {
        /**
         * 位置
         */
        int Position = 1;

        /**
         * 朝向
         */
        int Axis = 2;

    }

}
