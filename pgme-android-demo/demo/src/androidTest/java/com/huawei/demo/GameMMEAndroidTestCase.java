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

package com.huawei.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.ChannelInfo;
import com.huawei.game.gmme.model.Room;
import com.huawei.game.gmme.model.VoiceParam;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * 游戏多媒体测试用例
 * AIP:
 * https://developer.huawei.com/consumer/cn/doc/development/AppGallery-connect-References/gamemme-gmme-gamemediaengine-android-0000001238323625#section168115185491
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.JVM)
public class GameMMEAndroidTestCase {
    protected static GameMediaEngine mHwRtcEngine;

    protected static String callbackMethod = null;

    protected static String mvalue = null;

    public final static String TAG = "GameMMETestCasetag";

    public static String roomid = "special_room";

    public static int rcode = -1;

    public static String openid = GameMMETestUtil.openid;

    protected static GameMMETestUtil util = new GameMMETestUtil();

    public static GameMMETestUtil.IMyHandler MyHandler = (method, value) -> {
        callbackMethod = method;
        mvalue = value;
        switch (method) {
            case "onJoinTeamRoom":
                try {
                    JSONObject job = new JSONObject(value);
                    if (roomid == null) {
                        roomid = job.getString("roomId");
                    }
                    rcode = job.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    JSONObject job = new JSONObject(value);
                    rcode = job.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
        LogUtil.d(TAG, "onCallback=" + method + "; value=" + value);
    };

    @BeforeClass
    public static void setUp() {
        LogUtil.d(TAG, "setUp");
    }

    @AfterClass
    public static void tearDown() {
        LogUtil.d(TAG, "tearDown");
    }

    @Before
    public void begintest() {
        mHwRtcEngine = util.init(InstrumentationRegistry.getInstrumentation().getTargetContext(), MyHandler, null);
        mvalue = null;
        rcode = -1;
    }

    @After
    public void endtest() {
        rcode = -1;
        if (roomid != null && mHwRtcEngine != null) {
            mHwRtcEngine.leaveRoom(roomid, null);
            sleep(1500);
        }
        if (mHwRtcEngine != null) {
            GameMediaEngine.destroy();
            mHwRtcEngine = null;
        }
    }

    private void sleep(long s) {
        try {
            Thread.sleep(s);
        } catch (Exception e) {

        }
    }

    /***
     * 测试创建实例，初始化SDK
     */
    @Test
    public void testCreate() {
        sleep(2000);
        assertEquals("onCreate", callbackMethod);
        assertEquals(0, rcode);
    }

    /***
     * 测试创建或加入小队房间
     */
    @Test
    public void testJoinTeamRoom() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(3000);
        assertEquals("onJoinTeamRoom", callbackMethod);
        assertEquals(0, rcode);
    }

    /***
     * 测试创建或加入小队房间
     */
    @Test
    public void testJoinTeamRoomWithErrorid() {
        mHwRtcEngine.joinTeamRoom("roomid");
        sleep(3000);
        assertEquals("onJoinTeamRoom", callbackMethod);
        assertEquals(0, rcode);
    }

    /***
     * 获取房间信息
     */
    @Test
    public void testGetRoom() {
        sleep(1000);
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(3000);
        LogUtil.d(TAG, "testgetRoom");
        Room room = mHwRtcEngine.getRoom(roomid);
        sleep(1000);
        assertEquals(roomid, room.getRoomId());
        assertEquals(openid, room.getOwnerId());
    }

    /***
     * 未加入房间，获取房间信息
     */
    @Test
    public void testGetRoomWithnojoin() {
        sleep(1000);
        Room room = mHwRtcEngine.getRoom("roomid");
        sleep(1000);
        assertEquals("roomid", room.getRoomId());
        assertEquals(null, room.getOwnerId());
    }

    /***
     * 测试加入小队房间
     */
    @Test
    public void testJoinTeamRoomWithid() {
        sleep(2000);
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(3000);
        assertEquals("onJoinTeamRoom", callbackMethod);
        assertEquals(0, rcode);
    }

    /***
     * 离开房间
     * 玩家离开房间，或房主离开房间并指定新房主
     */
    @Test
    public void testLeaveRoom() {
        sleep(3000);
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(3000);
        mHwRtcEngine.leaveRoom(roomid, null);
        sleep(3000);
        assertEquals("onLeaveRoom", callbackMethod);
        assertEquals(0, rcode);
    }

    /***
     * 切换房间
     */
    @Test
    public void testSwitchRoom() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(3000);
        mHwRtcEngine.switchRoom(roomid);
        sleep(3000);
        assertEquals("onSwitchRoom", callbackMethod);
        assertEquals(0, rcode);
    }

    /***
     * 打开麦克风
     */
    @Test
    public void testEnableMic() {
        int r = mHwRtcEngine.enableMic(true);
        sleep(3000);
        assertEquals(0, r);
    }

    /***
     * 测试创建或加入小队房间
     */
    @Test
    public void testJoinNationalRoom() {
        sleep(2000);
        mHwRtcEngine.joinNationalRoom(roomid, 1);
        sleep(4000);
        assertEquals("onJoinNationalRoom", callbackMethod);
        assertEquals(0, rcode);
    }

    /***
     * 转让房主。新房主需在线
     */
    @Test
    public void testTransferOwner() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(3000);
        mHwRtcEngine.transferOwner(roomid, openid);
        sleep(3000);
        assertEquals(6002, rcode);
        assertEquals("onTransferOwner", callbackMethod);

        mHwRtcEngine.transferOwner(roomid, "xxx");
        sleep(3000);
        assertEquals(1002, rcode);
    }

    /***
     * 屏蔽指定玩家语音。需玩家提前加入该房间
     */
    @Test
    public void testMutePlayer() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(2000);
        String playid = "xxx";
        mHwRtcEngine.mutePlayer(roomid, playid, false);
        sleep(3000);
        assertEquals(1002, rcode);// 玩家不在线
        assertEquals("onMutePlayer", callbackMethod);// 玩家不在线
    }

    /***
     * 屏蔽其他所有玩家语音。
     */
    @Test
    public void testMutePlayers() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(2000);
        mHwRtcEngine.muteAllPlayers(roomid, false);
        sleep(3000);
        assertEquals(0, rcode);
        assertEquals("onMuteAllPlayers", callbackMethod);
    }

    /***
     * 房主禁言指定玩家。。需房主身份
     */
    @Test
    public void testForbidPlayer() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(2000);
        String playid = "xxx";
        mHwRtcEngine.forbidPlayer(roomid, playid, false);
        sleep(3000);
        assertEquals(1002, rcode);// 玩家不在线
        assertEquals("onForbidPlayer", callbackMethod);
    }

    /***
     * 房主禁言其他所有玩家。。。需房主身份
     */
    @Test
    public void testForbidAllPlayers() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(2000);
        mHwRtcEngine.forbidAllPlayers(roomid, false);
        sleep(3000);
        assertEquals(0, rcode);// 玩家不在线
        assertEquals("onForbidAllPlayers", callbackMethod);
    }

    /***
     * 开始录音转文字。
     */
    @Test
    public void testStartRecordAudioToText() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(2000);
        VoiceParam voiceParam = new VoiceParam();
        voiceParam.languageCodeSet("CN");
        mHwRtcEngine.startRecordAudioToText(voiceParam);
        sleep(3000);
        assertEquals(3015, rcode);// VOICE_TO_TEXT_THIRD_INVOKE_ERROR
    }

    /***
     * 停止录音转文字。
     */
    @Test
    public void testStopRecordAudioToText() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(2000);
        mHwRtcEngine.stopRecordAudioToText();
        sleep(3000);
        assertEquals(3013, rcode);
    }

    /***
     * 开启音量回调，用于获取当前房间发言玩家，默认为关闭状态。
     */
    @Test
    public void testEnableSpeakersDetection() {
        mHwRtcEngine.joinTeamRoom(roomid);
        sleep(2000);
        // 当前发言玩家列表回调的时间间隔，有效值范围为[100, 10000]，单位: 毫秒。当传入0时，即关闭音量回调
        mHwRtcEngine.enableSpeakersDetection(roomid, 1000);// 会不断的回调，需停止
        sleep(3000);
        assertEquals(0, rcode);
        mHwRtcEngine.enableSpeakersDetection(roomid, 0);
        sleep(1000);
    }

    /***
     * 加入聊天群组频道。
     * 群组ID，由数字（0~9）、大小写字母(A~Z, a~z)或下划线（_）组成的最大长度为32的字符串。
     */
    @Test
    public void testjoingroup() {
        mHwRtcEngine.joinGroupChannel("iloveyou");
        sleep(2000);
        assertEquals("onJoinChannel", callbackMethod);
        assertEquals(0, rcode);
        String channelId = null;
        try {
            JSONObject job = new JSONObject(mvalue);
            channelId = job.getString("channelId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals("iloveyou", channelId);
    }

    /***
     * 离开聊天群组频道。
     */
    @Test
    public void testleavegroup() {
        mHwRtcEngine.leaveChannel("iloveyou");
        sleep(2000);
        assertEquals("onLeaveChannel", callbackMethod);
        assertEquals(0, rcode);
        String channelId = null;
        try {
            JSONObject job = new JSONObject(mvalue);
            channelId = job.getString("channelId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals("iloveyou", channelId);
    }

    /***
     * 调用GameMediaEngine.getChannelInfo方法，获取指定的消息群组信息。
     */
    @Test
    public void testgetChannelInfo() {
        mHwRtcEngine.joinGroupChannel("iloveyou");
        sleep(2000);
        assertEquals(0, rcode);
        ChannelInfo channelInfo = mHwRtcEngine.getChannelInfo("iloveyou");
        sleep(2000);
        assertEquals("iloveyou", channelInfo.getChannelId());
        assertEquals(1, channelInfo.getChannelType());
        assertEquals(1, channelInfo.getPlayerCount());
        assertNotNull(channelInfo.getOwnerId());
    }

    /***
     * 发送消息
     * recvid 接受者ID。当聊天类型为单聊时，则填OpenId。当聊天类型为群聊时，则填ChannelId。
     * type
     * 聊天类型。
     * 1：单聊
     * 2：群聊
     * textMsg
     * 文本消息。
     */
    @Test
    public void testsendTextMsg() {
        String recid = "otherP";
        mHwRtcEngine.sendTextMsg(recid, 1, "goodluck");
        sleep(2000);
        assertEquals("onSendMsg", callbackMethod);
        String msg = null;
        try {
            JSONObject job = new JSONObject(mvalue);
            msg = job.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals("goodluck", msg);
    }

}