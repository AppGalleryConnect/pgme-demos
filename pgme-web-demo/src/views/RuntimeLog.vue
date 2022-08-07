/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

<template>
  <div class="g-runtime-log">
    <div class="log-head">
      <GButton small type="cancel" @click="onClearLog">清 屏</GButton>
    </div>
    <div class="log-body">
      <div
        class="log-item"
        :class="{ error: item.type === enumLogType.ERROR }"
        v-for="(item, index) in logs"
        :key="item.id || index"
      >
        <div class="time">{{ item.time }}</div>
        <div class="message">{{ item.message }}</div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import GButton from '@/components/Button.vue';
import { logsReact, clearLog, LogType } from '@/api/engine';

export default defineComponent({
  name: 'g-runtime-log',
  components: {
    GButton,
  },
  data() {
    return {
      enumLogType: { ...LogType },
    };
  },
  computed: {
    logs() {
      return logsReact.logs;
    },
  },
  methods: {
    onClearLog() {
      clearLog();
    },
  },
});
</script>
<style lang="scss">
.g-runtime-log {
  background-color: rgba($color: #2d2852, $alpha: 0.7);
  border-radius: 4px;
  overflow: hidden;
  height: 100%;
  .log-head {
    display: flex;
    align-items: center;
    background-color: #3a2d7e;
    padding: 0px 20px;
    height: 44px;
    line-height: 50px;
  }
  .log-body {
    color: #a497df;
    height: calc(100% - 50px);
    overflow-y: scroll;
    &::-webkit-scrollbar {
      width: 4px;
      background-color: #1c1d32;
    }
    &::-webkit-scrollbar-thumb {
      background-color: #2a284f;
    }
    .log-item {
      padding: 8px 20px;
      transition: all 0.2s;
      line-height: 20px;
      &.error .message {
        color: #dd579e;
      }
      &:hover {
        background-color: darken($color: #2a244c, $amount: 4);
      }
      .time {
        color: darken($color: #a497df, $amount: 15);
      }
    }
  }
}
</style>
