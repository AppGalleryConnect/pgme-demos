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

export default class AudioMsgScrollItem {
    // 文件id
    public fileId: number = null
    // 文件名称
    public fileName: string = null
    // 文件路径
    public filePath: string = null
    // 上传状态
    public uploadState: boolean = false;
    // 本地是否播放
    public isLocalPlay: boolean = true; // true播放  false停止
    // 远程是否播放
    public isRemotePlay: boolean = true; // true播放  false停止

    init(fileId: number, fileName: string, filePath: string, uploadState: boolean) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadState = uploadState;
    }

    setUploadState(uploadState: boolean) {
        this.uploadState = uploadState;
    }

    setFileId(fileId: number) {
        this.fileId = fileId;
    }

    setIsLocalPlay(isLocalPlay: boolean) {
        this.isLocalPlay = isLocalPlay;
    }

    setIsRemotePlay(isRemotePlay: boolean) {
        this.isRemotePlay = isRemotePlay;
    }

}
