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
  <div>
    <label class="g-radio" @click="onClick">
      <div class="g-radio__input-cnt">
        <input
          class="g-radio__input"
          type="radio"
          :checked="isChecked"
          :disabled="disabled"
          :name="name"
          :value="checkedValue"
        />
        <div class="g-radio__ring" :class="classes">
          <div class="g-radio__inner-circle"></div>
        </div>
      </div>
      <div class="g-radio__label-text">{{ label }}</div>
    </label>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
export default defineComponent({
  name: 'g-radio',
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    checked: {
      type: Boolean,
      default: false,
    },
    checkedValue: {
      type: [String, Number],
      default: '',
    },
    value: {
      type: [String, Number],
      default: '',
    },
    name: {
      type: String,
      default: '',
      required: true,
    },
    label: {
      type: String,
      default: '',
    },
  },
  created() {
    this.checked && this.$emit('update:value', this.checkedValue);
  },
  computed: {
    classes() {
      return {
        'radio-checked': this.isChecked,
      };
    },
    isChecked() {
      return this.value === this.checkedValue;
    },
  },
  methods: {
    onClick() {
      this.$emit('update:value', this.checkedValue);
    },
  },
});
</script>
<style lang="scss">
@import './style/definitions.scss';

.g-radio {
  align-items: center;
  display: flex;
  margin: 10px 0;
  cursor: pointer;
  .g-radio__input-cnt {
    position: relative;
    .g-radio__ring {
      position: absolute;
      top: -1px;
      left: 0px;
      height: 18px;
      width: 18px;
      border-radius: 100%;
      border-width: 2px;
      border-style: solid;
      border-color: #759af5;
      transition: all 0.2s;
      &.radio-checked {
        border-color: #50d6dc;
        .g-radio__inner-circle {
          background-image: linear-gradient(-30deg, #57ceee, #6efe89);
          opacity: 1;
          transform: scale(1);
        }
      }
      .g-radio__inner-circle {
        position: absolute;
        top: 3px;
        left: 3px;
        border-radius: 100%;
        height: 12px;
        width: 12px;
        transition: all 0.2s;
        transform: scale(0.3);
        opacity: 0;
      }
    }
  }
  input[type='radio'] {
    margin-right: 20px;
    vertical-align: middle;
    visibility: hidden;
  }
}
</style>
