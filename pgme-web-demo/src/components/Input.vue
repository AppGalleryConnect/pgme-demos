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
  <div class="g-input" :class="classes">
    <input :placeholder="placeholder" :disabled="disabled" v-model="inputValue" />
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
export default defineComponent({
  name: 'g-input',
  props: {
    small: {
      type: Boolean,
      default: false,
    },
    block: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    value: {
      type: String,
      default: '',
    },
    placeholder: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      inputValue: this.value,
    };
  },
  watch: {
    inputValue(newVal) {
      this.$emit('update:value', newVal);
    },
    value(newVal) {
      this.inputValue = newVal;
    },
  },
  computed: {
    classes() {
      return {
        input__block: !!this.block,
        input__small: !!this.small,
        input__disabled: !!this.disabled,
      };
    },
  },
});
</script>

<style lang="scss">
@import './style/definitions.scss';

.g-input {
  display: inline-block;
  input {
    outline: 0;
    box-sizing: border-box;
    height: 32px;
    width: 100%;
    padding: 0 10px;
    background-color: #2f2957;
    color: #222;
    font-size: 14px;
    border-radius: 3px;
    box-shadow: 0 2px 6px 0 #222;
    transition: opacity 0.3s;
    border-width: 1px;
    border-style: solid;
    border-color: #325da7;
    transition: border-color 0.3s;
    color: white;
    &:focus {
      border-color: #5346a1;
    }
    &:hover {
      border-color: #5346a1;
    }
    &::placeholder {
      color: #575098;
    }
  }
  &.input__small input {
    height: 30px;
    font-size: 13px;
  }
  &.input__disabled input {
    cursor: not-allowed;
    background-color: lighten($color: #2f2957, $amount: 10);
    color: #bbb;
    border-color: #5346a1;
  }
  &.input__block {
    display: block;
  }
}
</style>
