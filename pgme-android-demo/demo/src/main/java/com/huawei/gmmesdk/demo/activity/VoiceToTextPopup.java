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

package com.huawei.gmmesdk.demo.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.VoiceParam;
import com.huawei.gmmesdk.demo.R;

/**
 * 语音转文字弹框
 */
public class VoiceToTextPopup extends PopupWindow {
    private static final String TAG = VoiceToTextPopup.class.getSimpleName();

    private GameMediaEngine mHwRtcEngine;

    private Button mEnd;

    private Button mCancels;

    private TextView mTextView;

    private CheckBox mEnableMicView;

    private CountDownTimer timer;

    private VoiceParam voiceParam;

    private Boolean isChecked;

    private static int currVolume;

    private AudioManager audioManager;

    public VoiceToTextPopup(Context context, GameMediaEngine mHwRtcEngine, CheckBox mEnableMicView, Boolean isChecked,
        AudioManager audioManager) {
        super(context);
        this.isChecked = isChecked;
        this.mHwRtcEngine = mHwRtcEngine;
        this.mEnableMicView = mEnableMicView;
        this.audioManager = audioManager;
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(false);
        View contentView = LayoutInflater.from(context).inflate(R.layout.voice_to_text, null, false);
        mEnd = contentView.findViewById(R.id.end);
        mCancels = contentView.findViewById(R.id.cancels);
        mTextView = contentView.findViewById(R.id.voice_to_text_view);
        voiceParam = new VoiceParam();
        voiceParam.languageCodeSet("zh");
        mEnd.setOnClickListener(v -> {
            clearTimerAndOnSpeaker();
            mTextView.setText("正在转换中");
            mHwRtcEngine.stopRecordAudioToText();
        });
        mCancels.setOnClickListener(v -> {
            dismiss();
        });
        setContentView(contentView);
    }

    @Override
    public void dismiss() {
        clearTimerAndOnSpeaker();
        super.dismiss();
        if (isChecked && mHwRtcEngine != null) {
            mHwRtcEngine.enableMic(true);
        }
    }

    public void init() {
        closeSpeaker();
        timer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                // 计时倒计时显示
                mTextView.setText("剩余：" + (l / 1000 + 1) + "s");
            }

            @Override
            public void onFinish() {
                // 计时完毕时触发
                clearTimerAndOnSpeaker();
                mTextView.setText("正在转换中");
            }
        }.start();
        mHwRtcEngine.startRecordAudioToText(voiceParam);
    }

    // 销毁timer并打开扬声器
    protected void clearTimerAndOnSpeaker() {
        if (timer != null) {
            timer.cancel();
        }
        openSpeaker();
        mEnd.setEnabled(false);
    }

    public void setVoiceToText(String text) {
        clearTimerAndOnSpeaker();
        mTextView.setText(text);
        mEnd.setEnabled(false);
        mCancels.setEnabled(true);
    }

    public void openSpeaker() {
        // 打开扬声器
        try {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                    AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    public void closeSpeaker() {
        // 关闭扬声器
        currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }
}
