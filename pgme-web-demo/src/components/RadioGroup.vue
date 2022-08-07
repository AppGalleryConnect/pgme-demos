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
  <div class="g-radio-group">
    <GRadio
      class="radio-item"
      :class="classes"
      v-for="(item, index) in options"
      :key="item.id || index"
      :name="name"
      :label="item.label"
      :disabled="item.disabled"
      :checked-value="item.value"
      v-model:value="selectedOptionValue"
    ></GRadio>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import GRadio from './Radio.vue';

export default defineComponent({
  name: 'g-radio-group',
  components: {
    GRadio,
  },
  props: {
    name: {
      type: String,
      default: '',
      required: true,
    },
    options: {
      type: Array,
      default: () => [],
    },
    value: {
      type: String,
      default: '',
    },
    block: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      selectedOptionValue: this.value,
    };
  },
  computed: {
    classes() {
      return {
        block: !!this.block,
      };
    },
  },
  watch: {
    selectedOptionValue(newVal) {
      this.$emit('update:value', newVal);
    },
  },
});
</script>
<style lang="scss">
.g-radio-group {
  .radio-item {
    display: inline-block;
    padding: 0 10px;
    &.block {
      display: block;
    }
  }
}
</style>
