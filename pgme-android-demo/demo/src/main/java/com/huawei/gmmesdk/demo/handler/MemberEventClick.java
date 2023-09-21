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

package com.huawei.gmmesdk.demo.handler;

/**
 * 与会者列表用户订阅开关处理
 *
 * @since 2023-04-10
 */
public interface MemberEventClick {
    /**
     * 用户列表开启远端订阅
     *
     * @param openId 用户ID
     * @param state 屏蔽状态
     * @return 开启订阅是否成功
     */
    boolean muteRemote(String openId, boolean state);

    /**
     * 房主禁言指定玩家远端订阅
     *
     * @param openId 禁言用户ID
     * @param state 禁言状态
     * @return 开启订阅是否成功
     */
    boolean mutePlayer(String openId, boolean state);
}
