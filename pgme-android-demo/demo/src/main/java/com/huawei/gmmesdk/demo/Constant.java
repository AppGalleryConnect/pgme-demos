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

package com.huawei.gmmesdk.demo;

/**
 * Request code
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
     * 房间ID
     */
    String KEY_ROOM_ID = "room_id";

    /**
     * 默认用户ID
     */
    String KEY_USER_ID = "user_id";

    /**
     * 默认用户名
     */
    String KEY_USER_NAME = "user_name";

    /**
     * 登录华为账号
     */
    int REQUEST_SIGN_IN_LOGIN = 1001;

    /**
     * 初始化引擎
     */
    int GMME_INIT_ENGINE = 1002;

    /**
     * 引擎销毁
     */
    int GMME_DESTROY_ENGINE = 1003;

    /**
     * 日志大小
     */
    int LOG_SIZE = 1024 * 10;

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
     * 房间类型 1-TEAMROOM 2-NATIONALROOM
     */
    int TEAMROOM = 1;

    int NATIONALROOM = 2;

    /**
     * 玩家操作房间类型 1-LEAVEROOM 2-JOINROOM 3-SWITCHROOM
     */
    int LEAVEROOM = 1;

    int JOINROOM = 2;

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
     * 房主一键禁言按钮刷新周期
     */
    long REFRESH_FORBID_IMAGE = 500L;


    String[] roomTitle = {"房间成员", "小队成员", "国战成员"};

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
         * 调用结果
         */
        String RESULT = "result";

        /**
         * 是否屏蔽
         */
        String IS_MUTED = "isMuted";

        /**
         * 是否禁言
         */
        String IS_FORBIDDEN = "isForbidden";
    }
}
