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
    <label class="g-checkbox">
      <div class="g-checkbox__input-cnt">
        <input
          class="g-checkbox__input"
          type="checkbox"
          :checked="isChecked"
          :disabled="disabled"
          :name="name"
          :value="value"
          @click="onClick"
        />
        <div class="g-checkbox__box" :class="classes">
          <div class="g-checkbox__inner-box"></div>
        </div>
      </div>
      <div class="g-checkbox__label-text">
        <slot></slot>
      </div>
    </label>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'g-checkbox',
  props: {
    disabled: {
      type: Boolean,
      default: false,
    },
    checked: {
      type: Boolean,
      default: false,
    },
    value: {
      type: [String, Number, Boolean],
      default: '',
    },
    name: {
      type: String,
      default: '',
      required: true,
    },
  },
  computed: {
    classes() {
      return {
        'box-checked': this.isChecked,
      };
    },
    isChecked() {
      return !!this.value;
    },
  },
  methods: {
    onClick(e: PointerEvent) {
      const checked = (e.target as HTMLInputElement).checked;
      this.$emit('update:value', checked);
      if (checked !== this.value) {
        this.$emit('status-change', checked);
      }
    },
  },
});
</script>
<style lang="scss">
.g-checkbox {
  align-items: center;
  display: flex;
  margin: 10px 0;
  cursor: pointer;
  .g-checkbox__input-cnt {
    position: relative;
    .g-checkbox__box {
      position: absolute;
      top: -1px;
      left: 0px;
      height: 18px;
      width: 18px;
      border-radius: 5px;
      border-width: 2px;
      border-style: solid;
      border-color: #5b99ff;
      transition: all 0.2s;
      &.box-checked {
        border-color: #50d6dc;
        .g-checkbox__inner-box {
          opacity: 1;
          transform: scale(1.1);
        }
      }
      .g-checkbox__inner-box {
        position: absolute;
        top: 0px;
        left: 0px;
        height: 18px;
        width: 18px;
        transition: all 0.2s;
        transform: scale(0.8);
        opacity: 0;
        background-size: cover;
        background-position: center;
        background-image: url('~@/assets/ic_yxdmtyq_002.png');
      }
    }
  }
  input[type='checkbox'] {
    margin-right: 20px;
    vertical-align: middle;
    visibility: hidden;
  }
}
</style>
