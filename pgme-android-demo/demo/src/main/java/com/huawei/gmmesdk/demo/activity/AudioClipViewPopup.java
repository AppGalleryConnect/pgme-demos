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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.huawei.game.common.utils.LogUtil;
import com.huawei.game.gmme.GameMediaEngine;
import com.huawei.game.gmme.model.LocalAudioClipStateInfo;
import com.huawei.game.gmme.model.LocalAudioInfo;
import com.huawei.gmmesdk.demo.R;
import com.huawei.gmmesdk.demo.constant.Constant;
import com.huawei.gmmesdk.demo.util.RandomUtil;

import java.io.IOException;

/**
 * 音效界面
 *
 * @since 2023-07-13
 */
public class AudioClipViewPopup extends PopupWindow implements View.OnClickListener {
    /**
     * 日志标签
     */
    private static final String AUDIO_CLIP_TAG = GmmeRoomActivity.class.getSimpleName();

    private static final int MAX_VOLUME = 100;

    private static final int SOUND_ID_ONE = RandomUtil.getRandomNum();

    private static final int SOUND_ID_TWO = RandomUtil.getRandomNum();

    private static final String DEFAULT_NET_AUDIO_CLIP = "http://music.163.com/song/media/outer/url?id=25906124.mp3";

    private static int audioClipOneVolume = MAX_VOLUME;

    private static int audioClipTwoVolume = MAX_VOLUME;

    private String audioClipOneName;

    private String audioClipTwoName;

    private boolean audioClipOneIsNetAudio = true;

    private boolean audioClipTwoIsNetAudio = true;

    private SeekBar allVolumeSeekBar;

    private SeekBar audioClipOneVolumeSeekBar;

    private SeekBar audioClipTwoVolumeSeekBar;

    private TextView allVolumeText;

    private TextView audioClipOneText;

    private TextView audioClipTwoText;

    private Button stopAllAudioClipBtn;

    private Button pauseAllAudioClipBtn;

    private Button restoreAllAudioClipBtn;

    private Button playAudioClipOne;

    private Button stopAudioClipOne;

    private Button pauseAudioClipOne;

    private Button restoreAudioClipOne;

    private Button playAudioClipTwo;

    private Button stopAudioClipTwo;

    private Button pauseAudioClipTwo;

    private Button restoreAudioClipTwo;

    private EditText editAudioClipOne;

    private EditText editAudioClipTwo;

    private Spinner spinnerOne;

    private Spinner spinnerTwo;

    private ArrayAdapter<String> spinnerAdapter;

    private GameMediaEngine mHwRtcEngine;

    private Context context;

    public AudioClipViewPopup(Context context, GameMediaEngine mHwRtcEngine) {
        super(context);
        this.mHwRtcEngine = mHwRtcEngine;
        this.context = context;
        View contentView = LayoutInflater.from(context).inflate(R.layout.audio_clip_view, null, false);
        setFocusable(true);
        setOutsideTouchable(true);

        // 音量控件
        allVolumeSeekBar = contentView.findViewById(R.id.all_volume);
        audioClipOneVolumeSeekBar = contentView.findViewById(R.id.audio_clip_one_volume);
        audioClipTwoVolumeSeekBar = contentView.findViewById(R.id.audio_clip_two_volume);

        // 音量文本
        allVolumeText = contentView.findViewById(R.id.text_all_volume);
        audioClipOneText = contentView.findViewById(R.id.text_audio_clip_one);
        audioClipTwoText = contentView.findViewById(R.id.text_audio_clip_two);

        // 全部音效设置
        stopAllAudioClipBtn = contentView.findViewById(R.id.stop_all_audio_clip);
        pauseAllAudioClipBtn = contentView.findViewById(R.id.pause_all_audio_clip);
        restoreAllAudioClipBtn = contentView.findViewById(R.id.restore_all_audio_clip);

        // 音效1设置
        playAudioClipOne = contentView.findViewById(R.id.play_audio_clip_one);
        stopAudioClipOne = contentView.findViewById(R.id.stop_audio_clip_one);
        pauseAudioClipOne = contentView.findViewById(R.id.pause_audio_clip_one);
        restoreAudioClipOne = contentView.findViewById(R.id.restore_audio_clip_one);

        // 音效2设置
        playAudioClipTwo = contentView.findViewById(R.id.play_audio_clip_two);
        stopAudioClipTwo = contentView.findViewById(R.id.stop_audio_clip_two);
        pauseAudioClipTwo = contentView.findViewById(R.id.pause_audio_clip_two);
        restoreAudioClipTwo = contentView.findViewById(R.id.restore_audio_clip_two);

        // 设置按钮监听事件
        stopAllAudioClipBtn.setOnClickListener(this);
        pauseAllAudioClipBtn.setOnClickListener(this);
        restoreAllAudioClipBtn.setOnClickListener(this);
        playAudioClipOne.setOnClickListener(this);
        stopAudioClipOne.setOnClickListener(this);
        pauseAudioClipOne.setOnClickListener(this);
        restoreAudioClipOne.setOnClickListener(this);
        playAudioClipTwo.setOnClickListener(this);
        stopAudioClipTwo.setOnClickListener(this);
        pauseAudioClipTwo.setOnClickListener(this);
        restoreAudioClipTwo.setOnClickListener(this);

        // 播放次数
        editAudioClipOne = contentView.findViewById(R.id.edit_audio_clip_one);
        editAudioClipTwo = contentView.findViewById(R.id.edit_audio_clip_two);

        // 下拉框
        spinnerOne = contentView.findViewById(R.id.spinner_one);
        spinnerTwo = contentView.findViewById(R.id.spinner_two);

        // 设置下拉框适配器
        handleSpinnerAdapter(context, spinnerOne, Constant.AudioClipType.AudioClipOne);
        handleSpinnerAdapter(context, spinnerTwo, Constant.AudioClipType.AudioClipTwo);

        // 设置全部音效音量
        handleVolume(allVolumeSeekBar, allVolumeText, Constant.AudioClipType.AllAudioClip);

        // 设置音效1音量
        handleVolume(audioClipOneVolumeSeekBar, audioClipOneText, Constant.AudioClipType.AudioClipOne);

        // 设置音效2音量
        handleVolume(audioClipTwoVolumeSeekBar, audioClipTwoText, Constant.AudioClipType.AudioClipTwo);

        // 显示视图文件
        setContentView(contentView);
    }

    private void handleSpinnerAdapter(Context context, Spinner spinner, int audioClipType) {
        // 获取音频文件名称
        Resources res = context.getResources();
        String[] data = res.getStringArray(R.array.music);

        // 设置下拉框样式
        spinnerAdapter = new ArrayAdapter<>(context, R.layout.custom_spiner_text_item, data);

        // 设置下拉弹框样式
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // 设置选择监听器
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String audioClipName = data[0].equals(selectedItem) ? DEFAULT_NET_AUDIO_CLIP
                    : "/" + Constant.ResourcesSaveDir.MUSIC + "/" + selectedItem;
                if (audioClipType == Constant.AudioClipType.AudioClipOne) {
                    audioClipOneIsNetAudio = data[0].equals(selectedItem);
                    audioClipOneName = audioClipName;
                }
                if (audioClipType == Constant.AudioClipType.AudioClipTwo) {
                    audioClipTwoIsNetAudio = data[0].equals(selectedItem);
                    audioClipTwoName = audioClipName;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onRestoreAudioClipTwo() {
        this.mHwRtcEngine.resumeLocalAudioClip(SOUND_ID_TWO);
    }

    private void onPauseAudioClipTwo() {
        this.mHwRtcEngine.pauseLocalAudioClip(SOUND_ID_TWO);
    }

    private void onStopAudioClipTwo() {
        this.mHwRtcEngine.stopLocalAudioClip(SOUND_ID_TWO);
    }

    private void onPlayAudioClipTwo() {
        // 播放音效2
        LocalAudioInfo localAudioInfo = new LocalAudioInfo();
        localAudioInfo.setSoundId(SOUND_ID_TWO);
        localAudioInfo.setVolume(audioClipTwoVolume);
        String filePath = "";
        try {
            filePath = audioClipTwoIsNetAudio ? audioClipOneName
                : this.context.getExternalCacheDir().getCanonicalPath() + audioClipTwoName;
        } catch (IOException e) {
            LogUtil.e(AUDIO_CLIP_TAG, e.getMessage());
        }
        localAudioInfo.setFilePath(filePath);
        localAudioInfo.setLoop(Integer.parseInt(editAudioClipTwo.getText().toString()));
        this.mHwRtcEngine.playLocalAudioClip(localAudioInfo);
    }

    private void onRestoreAudioClipOne() {
        this.mHwRtcEngine.resumeLocalAudioClip(SOUND_ID_ONE);
    }

    private void onPauseAudioClipOne() {
        this.mHwRtcEngine.pauseLocalAudioClip(SOUND_ID_ONE);
    }

    private void onStopAudioClipOne() {
        this.mHwRtcEngine.stopLocalAudioClip(SOUND_ID_ONE);
    }

    private void onPlayAudioClipOne() {
        // 播放音效1
        LocalAudioInfo localAudioInfo = new LocalAudioInfo();
        localAudioInfo.setSoundId(SOUND_ID_ONE);
        localAudioInfo.setVolume(audioClipOneVolume);
        String filePath = "";
        try {
            filePath = audioClipOneIsNetAudio ? audioClipOneName
                : this.context.getExternalCacheDir().getCanonicalPath() + audioClipOneName;
        } catch (IOException e) {
            LogUtil.e(AUDIO_CLIP_TAG, e.getMessage());
        }
        localAudioInfo.setFilePath(filePath);
        localAudioInfo.setLoop(Integer.parseInt(editAudioClipOne.getText().toString()));
        this.mHwRtcEngine.playLocalAudioClip(localAudioInfo);
    }

    private void onRestoreAllAudioClip() {
        this.mHwRtcEngine.resumeAllLocalAudioClips();
    }

    private void onStopAllAudioClip() {
        this.mHwRtcEngine.stopAllLocalAudioClips();
    }

    private void onPauseAllAudioClip() {
        this.mHwRtcEngine.pauseAllLocalAudioClips();
    }

    private void handleVolume(SeekBar volumeSeekBar, TextView volumeTextView, int audioClipType) {
        volumeSeekBar.setProgress(MAX_VOLUME);
        String volumeText =
            context.getString(R.string.audio_volume) + "(" + this.mHwRtcEngine.getLocalAudioClipsVolume() + ")";
        volumeTextView.setText(volumeText);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentProgress = MAX_VOLUME;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (audioClipType) {
                    case Constant.AudioClipType.AllAudioClip:
                        mHwRtcEngine.setLocalAudioClipsVolume(currentProgress);
                        String allVolumeText = context.getString(R.string.audio_volume) + "("
                            + mHwRtcEngine.getLocalAudioClipsVolume() + ")";
                        volumeTextView.setText(allVolumeText);
                        break;
                    case Constant.AudioClipType.AudioClipOne:
                        audioClipOneVolume = currentProgress;
                        mHwRtcEngine.setVolumeOfLocalAudioClip(SOUND_ID_ONE, currentProgress);
                        int volumeOne = Math.min(mHwRtcEngine.getVolumeOfLocalAudioClip(SOUND_ID_ONE), MAX_VOLUME);
                        String volumeOneText = context.getString(R.string.audio_volume) + "(" + volumeOne + ")";
                        audioClipOneVolumeSeekBar.setProgress(volumeOne);
                        volumeTextView.setText(volumeOneText);
                        break;
                    case Constant.AudioClipType.AudioClipTwo:
                        audioClipTwoVolume = currentProgress;
                        mHwRtcEngine.setVolumeOfLocalAudioClip(SOUND_ID_TWO, currentProgress);
                        int volumeTwo = Math.min(mHwRtcEngine.getVolumeOfLocalAudioClip(SOUND_ID_TWO), MAX_VOLUME);
                        String volumeTwoText = context.getString(R.string.audio_volume) + "(" + volumeTwo + ")";
                        audioClipTwoVolumeSeekBar.setProgress(volumeTwo);
                        volumeTextView.setText(volumeTwoText);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_all_audio_clip:
                onStopAllAudioClip();
                break;
            case R.id.pause_all_audio_clip:
                onPauseAllAudioClip();
                break;
            case R.id.restore_all_audio_clip:
                onRestoreAllAudioClip();
                break;
            case R.id.play_audio_clip_one:
                onPlayAudioClipOne();
                break;
            case R.id.stop_audio_clip_one:
                onStopAudioClipOne();
                break;
            case R.id.pause_audio_clip_one:
                onPauseAudioClipOne();
                break;
            case R.id.restore_audio_clip_one:
                onRestoreAudioClipOne();
                break;
            case R.id.play_audio_clip_two:
                onPlayAudioClipTwo();
                break;
            case R.id.stop_audio_clip_two:
                onStopAudioClipTwo();
                break;
            case R.id.pause_audio_clip_two:
                onPauseAudioClipTwo();
                break;
            case R.id.restore_audio_clip_two:
                onRestoreAudioClipTwo();
                break;
            default:
                break;
        }
    }

    /**
     * 接收语音播放回调
     */
    public void onAudioClipStateChangedNotify(LocalAudioClipStateInfo localAudioClipStateInfo) {
        String state = localAudioClipStateInfo.getState().toString();
        Button stopAudioClipBtn =
            localAudioClipStateInfo.getSoundId() == SOUND_ID_ONE ? this.stopAudioClipOne : this.stopAudioClipTwo;
        Button playAudioClipBtn =
            localAudioClipStateInfo.getSoundId() == SOUND_ID_ONE ? this.playAudioClipOne : this.playAudioClipTwo;
        Button pauseAudioClipBtn =
            localAudioClipStateInfo.getSoundId() == SOUND_ID_ONE ? this.pauseAudioClipOne : this.pauseAudioClipTwo;
        Button restoreAudioClipBtn =
            localAudioClipStateInfo.getSoundId() == SOUND_ID_ONE ? this.restoreAudioClipOne : this.restoreAudioClipTwo;
        switch (state) {
            case "HW_AUDIO_CLIP_PLAYING":
                playingAudioBtnChanged(stopAudioClipBtn, playAudioClipBtn, pauseAudioClipBtn, restoreAudioClipBtn);
                break;
            case "HW_AUDIO_CLIP_PLAY_COMPLETED":
                playCompletedAudioBtnChanged(stopAudioClipBtn, playAudioClipBtn, pauseAudioClipBtn);
                break;
            case "HW_AUDIO_CLIP_PLAY_PAUSED":
                pauseAudioBtnChanged(pauseAudioClipBtn, restoreAudioClipBtn);
                break;
            case "HW_AUDIO_CLIP_PLAY_STOPPED":
                stopAudioBtnChanged(stopAudioClipBtn, playAudioClipBtn, pauseAudioClipBtn, restoreAudioClipBtn);
                break;
            case "HW_AUDIO_CLIP_PLAY_FAILED":
                break;
            default:
                break;
        }
    }

    private void playingAudioBtnChanged(Button stopAudioClipBtn, Button playAudioClipBtn, Button pauseAudioClipBtn,
        Button restoreAudioClipBtn) {
        stopAudioClipBtn.setVisibility(View.VISIBLE);
        playAudioClipBtn.setVisibility(View.GONE);
        pauseAudioClipBtn.setEnabled(true);
        pauseAudioClipBtn.setVisibility(View.VISIBLE);
        restoreAudioClipBtn.setVisibility(View.GONE);
    }

    private void playCompletedAudioBtnChanged(Button stopAudioClipBtn, Button playAudioClipBtn,
        Button pauseAudioClipBtn) {
        stopAudioClipBtn.setVisibility(View.GONE);
        playAudioClipBtn.setVisibility(View.VISIBLE);
        pauseAudioClipBtn.setEnabled(false);
    }

    private void pauseAudioBtnChanged(Button pauseAudioClipBtn, Button restoreAudioClipBtn) {
        pauseAudioClipBtn.setVisibility(View.GONE);
        restoreAudioClipBtn.setVisibility(View.VISIBLE);
        restoreAudioClipBtn.setEnabled(true);
    }

    private void stopAudioBtnChanged(Button stopAudioClipBtn, Button playAudioClipBtn, Button pauseAudioClipBtn,
        Button restoreAudioClipBtn) {
        stopAudioClipBtn.setVisibility(View.GONE);
        playAudioClipBtn.setVisibility(View.VISIBLE);
        pauseAudioClipBtn.setEnabled(false);
        restoreAudioClipBtn.setVisibility(View.GONE);
        pauseAudioClipBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        audioClipOneVolume = MAX_VOLUME;
        audioClipTwoVolume = MAX_VOLUME;
    }

}
