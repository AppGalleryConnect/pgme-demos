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
  <div class="page">
    <div class="page-body engine-init">
      <div class="common-margin demo-title">华为多媒体引擎演示</div>
      <GInput class="openid-input" block v-model:value="userId" placeholder="请输入用户Id，开始引擎初始化"></GInput>
      <GButton class="common-margin" type="primary" round long @click="initWidthSecurity">引擎初始化</GButton>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import GButton from '@/components/Button.vue';
import GInput from '@/components/Input.vue';
import { useRouter } from 'vue-router';
import { initEngine, EngineParamsConfig, pushLog, LogType } from '@/api/engine';

export default defineComponent({
  components: {
    GButton,
    GInput,
  },
  setup() {
    const userId = ref<string>('');
    const router = useRouter();
    const gameSecretEnabled = !!EngineParamsConfig.gameSecret;

    const initWidthSecurity = () => {
      initEngine(userId.value, { security: gameSecretEnabled })
        .then(() => {
          router.push({
            path: '/room',
            query: { openId: userId.value },
          });
          pushLog('The multimedia engine is created successfully');
        })
        .catch((error) => pushLog(`Failed to create the engine: ${error?.message}`, LogType.ERROR));
    };

    return {
      userId,
      gameSecretEnabled,
      initWidthSecurity,
    };
  },
});
</script>

<style lang="scss">
.demo-title {
  font-size: 28px;
  text-align: center;
  color: white;
}
.engine-init {
  text-align: center;
  margin: 100px auto 0 auto;
}
.openid-input {
  width: 100%;
  max-width: 220px;
  margin: 40px auto 0 auto;
  input {
    text-align: center;
  }
}
</style>
