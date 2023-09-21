/*
 * Copyright 2023. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import AudioMsgScrollItem from "../Function/AudioMsgScrollItem";
import LogUtil from "../Function/LogUtils";
import {LogType} from "../Function/Enum";
import GlobalData from "../../GlobalData";
import {GameMediaEngine} from "../../GMME/GMMEForMiniGames";

const {ccclass, property} = cc._decorator;

@ccclass
export default class AudioMsgItem extends cc.Component {
    @property(cc.Label)
    fileName: cc.Label = null;

    @property(cc.Label)
    uploadLog: cc.Label = null;

    @property(cc.Button)
    upload: cc.Button = null;

    @property(cc.Button)
    localPlayOrStop: cc.Button = null;

    @property(cc.Button)
    remotePlayOrStop: cc.Button = null;

    @property(cc.Label)
    localPlayOrStopLabel: cc.Label = null;

    @property(cc.Label)
    remotePlayOrStopLabel: cc.Label = null;

    private filePath: string = "";

    private fileId: number;

    // 判断数字的正则表达式
    private number_reg: RegExp = new RegExp('^[0-9]*$');

    start() {
        this.initListener();
    }

    private initListener() {
        this.upload.node.on(cc.Node.EventType.TOUCH_START, () => this.uploadAudio());
        this.localPlayOrStop.node.on(cc.Node.EventType.TOUCH_START, () => this.localPlayOrStopAudio());
        GameMediaEngine.on("onPlayAudioMsg", (filePath: string, code: number, msg: string) =>
            this.onPlayAudioMsg(filePath, code, msg));
        GameMediaEngine.on("onUploadAudioMsgFile", (filePath: string, fileId: number, code: number, msg: string) =>
            this.onUploadAudioMsgFile(filePath, fileId, code, msg));
    }

    // ========== 回调方法 ==========
    private onPlayAudioMsg(filePath: string, code: number, msg: string) {
        LogUtil.printLog("播放语音消息回调，code:" + code + ",msg:" + msg + ",filePath:" + filePath, LogType.AUDIO_MSG_TYPE, this.node);
        let audioMsgItem = this.isFileId(filePath) ? GlobalData.audioMsgItems.find(audio => audio.fileId.toString() === filePath) :
            GlobalData.audioMsgItems.find(audioMsgItem => audioMsgItem.filePath === filePath);
        this.isFileId(filePath) ? audioMsgItem.setIsRemotePlay(true) : audioMsgItem.setIsLocalPlay(true);
        GlobalData.isRefreshAudioMsg = true;
    }

    private onUploadAudioMsgFile(filePath: string, fileId: number, code: number, msg: string) {
        LogUtil.printLog("上传语音消息，code:" + code + ",msg:" + msg + ",fileId:" + fileId, LogType.AUDIO_MSG_TYPE, this.node);
        if (code === 0) {
            // 上传成功，组件文案，上传按钮消失,文件id 本地存储
            let audioMsgItem = GlobalData.audioMsgItems.find(audioMsgItem => audioMsgItem.filePath === filePath);
            audioMsgItem.setUploadState(true);
            audioMsgItem.setFileId(fileId);
            // 通知事件
            this.node.dispatchEvent(new cc.Event.EventCustom("audioMsgEvent", true));
        }
    }

    private uploadAudio() {
        // 上传按钮事件
        GlobalData.gameMediaEngine.uploadAudioMsgFile(this.filePath, 5000);
    }

    private localPlayOrStopAudio() {
        // 本地播放和停止语音按钮事件
        let audioMsgItem = GlobalData.audioMsgItems.find(audioMsgItem => audioMsgItem.filePath === this.filePath);
        audioMsgItem.isLocalPlay ? GlobalData.gameMediaEngine.playAudioMsg(this.filePath)
            : GlobalData.gameMediaEngine.stopPlayAudioMsg();
        this.localPlayOrStopLabel.string = audioMsgItem.isLocalPlay ? "本地停止" : "本地播放";
        audioMsgItem.setIsLocalPlay(!audioMsgItem.isLocalPlay);
    }

    private remotePlayOrStopAudio() {
        // 远程播放和停止语音按钮事件
        LogUtil.printLog("远程播放语音消息，fileId:" + this.fileId, LogType.AUDIO_MSG_TYPE, this.node);
        let audioMsgItem = GlobalData.audioMsgItems.find(audioMsgItem => audioMsgItem.filePath === this.filePath);
        audioMsgItem.isRemotePlay ? GlobalData.gameMediaEngine.playAudioMsg(this.fileId.toString())
            : GlobalData.gameMediaEngine.stopPlayAudioMsg();
        this.remotePlayOrStopLabel.string = audioMsgItem.isRemotePlay ? "远程停止" : "远程播放";
        audioMsgItem.setIsRemotePlay(!audioMsgItem.isRemotePlay);
    }

    /**
     * 判断当前是否为fileId
     * @param filePath 用户输入路径
     * @return true表示是fileId
     */
    private isFileId(filePath: string) {
        return this.number_reg.test(filePath);
    }

    init(item: AudioMsgScrollItem) {
        // 设置界面按钮和文本的变化
        this.filePath = item.filePath;
        this.fileId = item.fileId;
        this.fileName.string = item.fileName;
        if (item.uploadState) {
            this.upload.node.active = false;
            this.remotePlayOrStop.node.on(cc.Node.EventType.TOUCH_START, () => this.remotePlayOrStopAudio());
        }
        this.localPlayOrStopLabel.string = item.isLocalPlay ? "本地播放" : "本地停止";
        this.remotePlayOrStopLabel.string = item.isRemotePlay ? "远程播放" : "远程停止";
    }
}
