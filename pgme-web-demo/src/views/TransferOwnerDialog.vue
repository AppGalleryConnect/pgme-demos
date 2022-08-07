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
  <div class="role-select">
    <GModal title="离开房间指定下一任房主" v-model:visible="showModal" @submit="onSubmit" @close="onClose">
      <div class="role-chooser">
        <GRadioGroup name="role-chooser" block :options="playerOptions" v-model:value="playerSelected"></GRadioGroup>
      </div>
    </GModal>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import GModal from '../components/Modal.vue';
import GRadioGroup from '../components/RadioGroup.vue';

interface SelectOptions {
  label: string;
  value: string | number;
}

export default defineComponent({
  name: 'g-transfer-owner-dialog',
  components: {
    GModal,
    GRadioGroup,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    playerList: {
      type: Array,
      default: () => [] as string[],
    },
  },
  data() {
    return {
      showModal: this.visible,
      playerSelected: '',
      playerOptions: [] as SelectOptions[],
    };
  },
  methods: {
    updateRadioOptions() {
      const options: SelectOptions[] = [];
      this.playerList.forEach((el) => {
        options.push({ label: el as string, value: el as string });
      });
      this.playerSelected = '';
      this.playerOptions = options;
    },
    onSubmit() {
      this.$emit('ok', this.playerSelected);
    },
    onClose() {
      this.$emit('update:visible', false);
    },
  },
  watch: {
    visible(newVal) {
      if (newVal) {
        this.updateRadioOptions();
      }
      this.showModal = newVal;
    },
  },
});
</script>
<style lang="scss" scoped>
.role-chooser {
  background-color: #332f61;
  padding: 15px;
  text-align: center;
}
</style>
