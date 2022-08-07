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
  <button class="g-button" :disabled="disabled" :class="classes">
    <slot></slot>
  </button>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'g-button',
  props: {
    type: {
      type: String,
      default: '',
    },
    small: {
      type: Boolean,
      default: false,
    },
    block: {
      type: Boolean,
      default: false,
    },
    round: {
      type: Boolean,
      default: false,
    },
    long: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    classes() {
      return {
        btn__block: !!this.block,
        btn__small: !!this.small,
        btn__round: !!this.round,
        btn__long: !!this.long,
        'btn-disabled': !!this.disabled,
        'btn-primary': this.type === 'primary',
        'btn-cancel': this.type === 'cancel',
      };
    },
  },
});
</script>

<style lang="scss">
@import './style/definitions.scss';
.g-button {
  outline: none;
  border: none;
  height: 32px;
  padding: 0 24px;
  display: inline-block;
  line-height: 32px;
  background-color: #ddd;
  color: #222;
  font-size: 13px;
  border-radius: 3px;
  text-align: center;
  box-shadow: 0 2px 6px 0 #222;
  transition: opacity 0.2s;
  &:hover {
    cursor: pointer;
    opacity: 0.8;
  }
  &.btn__block {
    display: block;
    padding: 0;
  }
  &.btn__small {
    height: 30px;
    line-height: 30px;
    font-size: 13px;
  }
  &.btn__round {
    border-radius: 100px;
  }
  &.btn__long {
    padding: 0 50px;
  }
  &.btn-disabled {
    &:hover {
      cursor: not-allowed;
    }
    &.btn-primary {
      background-image: linear-gradient(30deg, #aaa, gray);
      color: white;
    }
    &.btn-cancel {
      background-image: linear-gradient(30deg, #aaa, gray);
      color: white;
    }
  }
  &.btn-primary {
    color: white;
    background-image: linear-gradient(-30deg, $p-lb-color, $p-m1-color, $p-m2-color, $p-rt-color);
  }
  &.btn-cancel {
    color: white;
    background-image: linear-gradient(-30deg, $c-lb-color, $c-m1-color, $c-m2-color, $c-rt-color);
  }
}
</style>
