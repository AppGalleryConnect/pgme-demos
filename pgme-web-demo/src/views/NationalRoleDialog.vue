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
    <GModal title="请选择加入国战房间的身份" v-model:visible="showModal" @submit="onSubmit" @close="onClose">
      <div class="role-chooser">
        <GRadioGroup name="role-chooser" block :options="roleOptions" v-model:value="roleSelected"></GRadioGroup>
      </div>
    </GModal>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import GModal from '../components/Modal.vue';
import GRadioGroup from '../components/RadioGroup.vue';
import { NationalRole } from '@/sdk/GMME';
import { createCheckboxId } from '../components/helper/utils';

export default defineComponent({
  name: 'g-national-role-dialog',
  components: {
    GModal,
    GRadioGroup,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      showModal: this.visible,
      roleSelected: NationalRole.COMMANDER.toString(),
    };
  },
  watch: {
    visible(newVal) {
      this.showModal = newVal;
    },
  },
  setup() {
    const roleOptions = [
      {
        id: createCheckboxId(),
        label: '指挥家',
        value: NationalRole.COMMANDER.toString(),
      },
      {
        id: createCheckboxId(),
        label: '群众',
        value: NationalRole.MASSES.toString(),
      },
    ];
    return {
      roleOptions,
    };
  },
  methods: {
    onSubmit() {
      this.$emit('ok', Number(this.roleSelected));
    },
    onClose() {
      this.$emit('update:visible', false);
    },
  },
});
</script>
<style lang="scss" scoped>
.role-chooser {
  background-color: #332f61;
  padding: 15px calc(100% - 70%);
  text-align: center;
}
</style>
