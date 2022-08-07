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
  <div class="g-select" :class="{ 'optins-active': showOptions }" tabindex="0" @blur="showOptions = false">
    <div class="g-select-value" @click="showOptions = true">
      <div class="value-text" v-if="!!selectedValue">{{ selectedValue }}</div>
      <div class="placeholder" v-else>{{ placeholder }}</div>
    </div>
    <transition name="fade-up">
      <div class="g-select-options" v-if="showOptions">
        <div
          class="option-item"
          v-for="(item, index) in options"
          :key="item.value || index"
          @click.self="onSelect(item.value)"
        >
          {{ item.label }}
        </div>
      </div>
    </transition>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'g-select',
  props: {
    placeholder: {
      type: String,
      default: '',
    },
    options: {
      type: Array,
      default: () => [],
    },
    value: {
      type: [Number, String],
      default: '',
    },
  },
  data() {
    return {
      selectedValue: this.value,
      showOptions: false,
    };
  },
  methods: {
    onSelect(value: string | number) {
      this.showOptions = false;
      if (this.selectedValue !== value) {
        this.selectedValue = value;
        this.$emit('value-change', value);
      }
    },
  },
  watch: {
    value(newVal) {
      this.selectedValue = newVal;
    },
  },
});
</script>
<style lang="scss">
.fade-up-enter-from,
.fade-up-leave-to {
  opacity: 0;
  margin-top: 3px;
}
.fade-up-enter-to,
.fade-up-leave-from {
  opacity: 1;
}
.fade-up-enter-active,
.fade-up-leave-active,
.g-select-options {
  transition: all 0.2s ease;
}
.g-select {
  position: relative;
  outline: none;
  height: 32px;
  line-height: 32px;
  width: 100%;
  font-size: 14px;
  border-radius: 3px;
  background-color: #2f2957;
  box-shadow: 0 2px 6px 0 #222;
  transition: opacity 0.3s;
  border-width: 1px;
  border-style: solid;
  border-color: #325da7;
  color: white;
  padding: 0 10px;
  display: inline-block;
  text-align: left;
  &:hover {
    cursor: pointer;
  }
  &::after {
    content: '';
    display: block;
    position: absolute;
    height: 0px;
    width: 0px;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-top: 10px solid #5e4fac;
    border-bottom: 10px solid transparent;
    right: 10px;
    top: 12px;
  }
  .g-select-value {
    .placeholder {
      color: #55518d;
    }
  }
  .g-select-options {
    background-color: #2f2957;
    border: 1px solid #28254d;
    position: relative;
    z-index: 1;
    top: 3px;
    left: -10px;
    width: 100%;
    box-shadow: 0 2px 6px 0 #222;
    .option-item {
      padding: 0 10px;
      transition: all 0.2s;
      &:hover {
        cursor: pointer;
        background-color: lighten($color: #2f2957, $amount: 10);
      }
    }
  }
}
</style>
