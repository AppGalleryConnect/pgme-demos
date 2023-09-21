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

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.Message;
import com.huawei.gmmesdk.demo.GmmeApplication;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.activity.GmmeRoomActivity;
import com.huawei.gmmesdk.demo.constant.Constant;
import com.huawei.gmmesdk.demo.util.JsonUtil;
import com.huawei.gmmesdk.demo.util.RandomUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 聊天界面适配器
 *
 * @since 2023-04-10
 */
public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder> {
    private static final int SEND_TEXT_MSG_SUCCEEDED = 0;

    private static final String TAG = ChatMsgAdapter.class.getSimpleName();

    /**
     * HRTCEngine
     */
    protected GameMediaEngine mHwRtcEngine;

    private List<Message> mChatMsgList;

    private Context mContext;

    private String mUserId;

    public ChatMsgAdapter(Context context, String userId, List<Message> msgList) {
        mContext = context;
        mUserId = userId;
        mChatMsgList = msgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msg_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = mChatMsgList.get(position);
        if (msg == null) {
            return;
        }
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(msg.getTime()));
        try {
            String content = msg.getContent();
            JSONObject jobj = JsonUtil.stringToJsonObject(content);
            if (jobj != null && jobj.getInt("msgType") == Constant.MsgType.MSG_TYPE_AUDIO) {
                // 适配语音类型的视图
                audioViewAdapter(holder, msg, time, jobj);
            } else {
                // 适配文本类型的视图
                textViewAdapter(holder, msg, time);
            }
        } catch (IOException | JSONException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    private void audioViewAdapter(ViewHolder holder, Message msg, String time, JSONObject jsonObj)
        throws JSONException, IOException {
        mHwRtcEngine = ((GmmeApplication) ((GmmeRoomActivity) mContext).getApplication()).getEngine();
        int audioSeconds = jsonObj.getInt("audioMilliSeconds") / 1000;
        boolean isOwner = TextUtils.isEmpty(msg.getSenderId()) || msg.getSenderId().equals(mUserId);
        holder.receiveAudioLayout.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        holder.sendAudioLayout.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        holder.receiveMsgLayout.setVisibility(View.GONE);
        holder.sendMsgLayout.setVisibility(View.GONE);
        if (isOwner) {

            // 发送者页面渲染和点击事件监听
            sendAndReceiveAdapter(holder.sendAudioContent, holder.sendAudioUserId, msg, time,
                jsonObj.getString("filePath"), audioSeconds);
            holder.sendAudioTip.setVisibility(View.VISIBLE);
            if (msg.getCode() == SEND_TEXT_MSG_SUCCEEDED) {
                holder.sendAudioTip.setVisibility(View.GONE);
            }
            return;
        }
        // 下载音频
        String fileId = jsonObj.getString("fileId");
        String filePath =
            mContext.getExternalCacheDir().getCanonicalPath() + "/" + RandomUtil.getRandomNum() + Constant.AUDIO_TYPE;
        mHwRtcEngine.downloadAudioMsgFile(fileId, filePath, 5000);

        // 接收者页面渲染和点击事件监听
        sendAndReceiveAdapter(holder.receiveAudioContent, holder.receiveAudioUserId, msg, time, filePath, audioSeconds);

    }

    /**
     * 适配发送者和接收者
     *
     * @param contentTv 内容文本组件
     * @param userTv 用户和时间文本组件
     * @param msg 用户信息
     * @param time 时间
     * @param filePath 文件路径
     * @param audioSeconds 音频时间
     */
    private void sendAndReceiveAdapter(@NonNull TextView contentTv, @NonNull TextView userTv, Message msg, String time,
        String filePath, int audioSeconds) {
        contentTv.setText(String.format("(%s秒) 点击播放/停止语音", audioSeconds));
        userTv.setText(String.format("%s %s", msg.getSenderId(), time));
        contentTv.setOnClickListener(v -> {
            playAudioMsg(filePath);
        });
    }

    /**
     * 播放音频
     *
     * @param filePath 文件路径
     */
    private void playAudioMsg(String filePath) {
        // 播放音频
        mHwRtcEngine.playAudioMsg(filePath);
    }

    private void textViewAdapter(ViewHolder holder, Message msg, String time) {
        String content = msg.getContent();
        holder.receiveAudioLayout.setVisibility(View.GONE);
        holder.sendAudioLayout.setVisibility(View.GONE);
        if (TextUtils.isEmpty(msg.getSenderId()) || msg.getSenderId().equals(mUserId)) {
            holder.receiveMsgLayout.setVisibility(View.GONE);
            holder.sendMsgLayout.setVisibility(View.VISIBLE);
            holder.sendMsgContent.setText(content);
            holder.sendMsgUserId.setText(String.format("%s %s", msg.getSenderId(), time));
            if (msg.getCode() == SEND_TEXT_MSG_SUCCEEDED) {
                holder.sendMsgTip.setVisibility(View.GONE);
            } else {
                holder.sendMsgTip.setVisibility(View.VISIBLE);
            }
        } else {
            holder.sendMsgLayout.setVisibility(View.GONE);
            holder.receiveMsgLayout.setVisibility(View.VISIBLE);
            holder.receiveMsgContent.setText(content);
            holder.receiveMsgUserId.setText(String.format("%s %s", msg.getSenderId(), time));
        }
    }

    @Override
    public int getItemCount() {
        return mChatMsgList != null ? mChatMsgList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout receiveMsgLayout;

        private final LinearLayout sendMsgLayout;

        private final TextView receiveMsgContent;

        private final TextView sendMsgContent;

        private final TextView receiveMsgUserId;

        private final TextView sendMsgUserId;

        private final TextView sendMsgTip;

        private final LinearLayout receiveAudioLayout;

        private final LinearLayout sendAudioLayout;

        private final TextView receiveAudioContent;

        private final TextView sendAudioContent;

        private final TextView receiveAudioUserId;

        private final TextView sendAudioUserId;

        private final TextView sendAudioTip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            receiveMsgLayout = itemView.findViewById(R.id.receive_msg_layout);
            receiveMsgContent = itemView.findViewById(R.id.receive_msg_content);
            receiveMsgUserId = itemView.findViewById(R.id.receive_msg_user_id);

            sendMsgLayout = itemView.findViewById(R.id.send_msg_layout);
            sendMsgContent = itemView.findViewById(R.id.send_msg_content);
            sendMsgUserId = itemView.findViewById(R.id.send_msg_user_id);
            sendMsgTip = itemView.findViewById(R.id.send_msg_tip);

            receiveAudioLayout = itemView.findViewById(R.id.receive_audio_layout);
            receiveAudioContent = itemView.findViewById(R.id.receive_audio_content);
            receiveAudioUserId = itemView.findViewById(R.id.receive_audio_user_id);

            sendAudioLayout = itemView.findViewById(R.id.send_audio_layout);
            sendAudioContent = itemView.findViewById(R.id.send_audio_content);
            sendAudioUserId = itemView.findViewById(R.id.send_audio_user_id);
            sendAudioTip = itemView.findViewById(R.id.send_audio_tip);
        }
    }
}