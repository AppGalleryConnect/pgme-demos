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

export default class CustomPropertiesScrollItem {
  public id: string | null = null;
  // 自定义属性 key 值
  public key: string | null = null;
  // 自定义属性 value 值
  public value: string | null = null;

  init(id:string, key: string, value: string) {
    this.id = id;
    this.key = key;
    this.value = value;
  }
}
