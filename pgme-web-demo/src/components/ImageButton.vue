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
  <button class="g-img-button" :disabled="disabled" :class="classes">
    <img class="btn-img" :src="imgSrc" />
  </button>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'g-img-button',
  props: {
    type: {
      type: String,
      default: '',
    },
    imgOn: {
      type: String,
      default: '',
    },
    imgGif: {
      type: String,
      default: '',
    },
    imgOff: {
      type: String,
      default: '',
    },
    imgDisabled: {
      type: String,
      default: '',
    },
    status: {
      type: String,
      default: '',
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    size: {
      type: String,
      default: 'normal',
    },
  },
  computed: {
    classes() {
      return {
        'btn-disabled': !!this.disabled,
        'btn-small': this.size === 'small',
      };
    },
    imgSrc() {
      if (this.disabled) return this.imgDisabled;
      if (this.status === 'on') return this.imgOn;
      else if (this.status === 'off') return this.imgOff;
      else return this.imgGif;
    },
  },
});
</script>
<style lang="scss">
.g-img-button {
  outline: none;
  border: none;
  height: 30px;
  width: 32px;
  display: inline-block;
  background-color: transparent;
  color: #222;
  border-radius: 3px;
  text-align: center;
  transition: opacity 0.2s;
  &.btn-small {
    height: 24px;
  }
  img {
    height: 100%;
    vertical-align: middle;
  }
  &:hover {
    cursor: pointer;
  }
  &.btn-disabled {
    opacity: 0.8;
    cursor: not-allowed;
  }
}
</style>
