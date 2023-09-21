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
 * IM日志常数
 *
 * @since 2023-04-10
 */
public interface IMLogConstant {
    /**
     * 返回消息的错误码
     */
    interface Common {
        String CODE = "code";
    }

    /**
     * 聊天类型，1:1V1, 2:群聊；
     */
    String CHAT_TYPE = "chatType";

    /**
     * 错误描述信息
     */
    String ERR_MSG = "errMsg";

    /**
     * 发送者ID
     */
    String SENDER_ID = "senderId";

    /**
     * 接受者ID (openId or channelId）
     */
    String RECV_ID = "recvId";

    /**
     * 发送或者接收到的文本内容
     */
    String CONTENT = "content";

    /**
     * 频道id
     */
    String CHANNEL_ID = "channelId";

    /**
     * 消息内容
     */
    String MSG = "msg";

}
