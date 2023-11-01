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

import configs from '../../Config';

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
  return { sign: jsrsasign.hextob64(sig.sign()), nonce, timestamp };
};

export default class Utils {
  static random() {
    let str = Date.now().toString(36);
    for (let i = 0; i < 7; i++) {
      str += Math.ceil(Math.random() * 10 ** 4).toString(36);
    }
    return str;
  }

  /**
   * 获取当前时间
   */
  static getCurrentDateTime() {
    const date = new Date();
    const hours = date.getHours(); //获取当前小时数(0-23)
    const minutes = date.getMinutes(); //获取当前分钟数(0-59)
    const seconds = date.getSeconds();
    return hours + ':' + minutes + ':' + seconds;
  }

  /**
   * 部分隐藏处理
   *
   * content 需要处理的字符串
   * frontLen  保留的前几位
   * endLen  保留的后几位
   * replaceStr  替换的字符串
   */
  static strReplace(content: string, frontLen: number, endLen: number, replaceStr: string) {
    if (content.length > 20) {
      return content.substring(0, frontLen) + replaceStr + content.substring(content.length - endLen);
    } else {
      return content;
    }
  }

  /**
   * 文本转二进制
   *
   * message 字符串
   */
  static textToUint8Array(message: string) {
    let pos = 0;
    const len = message.length;

    let at = 0;
    let tlen = Math.max(32, len + (len >> 1) + 7);
    let target = new Uint8Array((tlen >> 3) << 3);

    while (pos < len) {
      let value = message.charCodeAt(pos++);
      if (value >= 0xd800 && value <= 0xdbff) {
        if (pos < len) {
          const extra = message.charCodeAt(pos);
          if ((extra & 0xfc00) === 0xdc00) {
            ++pos;
            value = ((value & 0x3ff) << 10) + (extra & 0x3ff) + 0x10000;
          }
        }
        if (value >= 0xd800 && value <= 0xdbff) {
          continue;
        }
      }

      if (at + 4 > target.length) {
        tlen += 8;
        tlen *= 1.0 + (pos / message.length) * 2;
        tlen = (tlen >> 3) << 3;

        const update = new Uint8Array(tlen);
        update.set(target);
        target = update;
      }

      if ((value & 0xffffff80) === 0) {
        target[at++] = value;
        continue;
      } else if ((value & 0xfffff800) === 0) {
        target[at++] = ((value >> 6) & 0x1f) | 0xc0;
      } else if ((value & 0xffff0000) === 0) {
        target[at++] = ((value >> 12) & 0x0f) | 0xe0;
        target[at++] = ((value >> 6) & 0x3f) | 0x80;
      } else if ((value & 0xffe00000) === 0) {
        target[at++] = ((value >> 18) & 0x07) | 0xf0;
        target[at++] = ((value >> 12) & 0x3f) | 0x80;
        target[at++] = ((value >> 6) & 0x3f) | 0x80;
      } else {
        continue;
      }

      target[at++] = (value & 0x3f) | 0x80;
    }

    return target.slice(0, at);
  }

  /**
   * 二进制转文本
   *
   * array 二进制
   */
  static Uint8ArrayToText(array: Uint8Array) {
    let out: string, i, len, c;
    let char2, char3;

    out = '';
    len = array.length;
    i = 0;
    while (i < len) {
      c = array[i++];
      switch (c >> 4) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
          out += String.fromCharCode(c);
          break;
        case 12:
        case 13:
          char2 = array[i++];
          out += String.fromCharCode(((c & 0x1f) << 6) | (char2 & 0x3f));
          break;
        case 14:
          char2 = array[i++];
          char3 = array[i++];
          out += String.fromCharCode(((c & 0x0f) << 12) | ((char2 & 0x3f) << 6) | ((char3 & 0x3f) << 0));
          break;
      }
    }

    return out;
  }
}
