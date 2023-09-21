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

import configs from "../../Config";

const jsrsasign = require('jsrsasign');

export const getSign = (appId: string, openId: string) => {
    const gameSecret = configs.gameSecret;
    const timestamp = Date.now().toString();
    const nonce = '1234567890';
    const sig = new jsrsasign.KJUR.crypto.Signature({
        alg: 'SHA256withRSAandMGF1',
        prvkeypem: gameSecret,
    });
    const newStr = `appId=${appId}&nonce=${nonce}&openId=${openId}&timestamp=${timestamp}`;
    sig.updateString(newStr);
    return {sign: jsrsasign.hextob64(sig.sign()), nonce, timestamp};
};

export default class Utils {

    static random() {
        let str = Date.now().toString(36);
        for (let i = 0; i < 7; i++) {
            str += Math.ceil(Math.random() * (10 ** 4)).toString(36);
        }
        return str;
    }

    /**
     * 获取当前时间
     */
    static getCurrentDateTime() {
        let date = new Date();
        let hours = date.getHours(); //获取当前小时数(0-23)
        let minutes = date.getMinutes(); //获取当前分钟数(0-59)
        let seconds = date.getSeconds();
        return hours + ":" + minutes + ":" + seconds;
    }

}