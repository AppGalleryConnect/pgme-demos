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
  <transition name="fade-scale">
    <div class="g-modal mask" v-if="visible">
      <div class="modal">
        <div class="modal-title">{{ title }}</div>
        <div class="modal-body">
          <slot></slot>
        </div>
        <div class="btn-line">
          <g-button small type="cancel" @click="onCancel">取 消</g-button>
          <g-button small type="primary" @click="onSubmit">确 定</g-button>
        </div>
      </div>
    </div>
  </transition>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import GButton from './Button.vue';

export default defineComponent({
  name: 'g-modal',
  components: {
    GButton,
  },
  props: {
    title: {
      type: String,
      default: '',
    },
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {};
  },
  computed: {
    toggleTransition() {
      return `g-modal__transition-fade-enter`;
    },
  },
  methods: {
    onCancel() {
      this.$emit('update:visible', false);
      this.$emit('close');
    },
    onSubmit() {
      this.$emit('update:visible', false);
      this.$emit('submit');
      this.$emit('close');
    },
    onEnter() {
      this.$emit('reveal');
    },
    onLeave() {
      this.$emit('hide');
    },
  },
});
</script>
<style lang="scss">
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  .modal {
    transform: scale(1.1);
  }
}
.fade-scale-enter-to,
.fade-scale-leave-from {
  opacity: 1;
  .modal {
    transform: scale(1);
  }
}
.fade-scale-enter-active,
.fade-scale-leave-active,
.modal {
  transition: all 0.26s ease;
}
.g-modal {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  display: flex;
  align-items: center;
  background-color: rgba($color: #090617, $alpha: 0.7);
  z-index: 999;
  .modal {
    margin: 0 auto;
    width: 360px;
    max-width: 86%;
    background-image: linear-gradient(30deg, #2f2a5a, #1f1d3a);
    border: 1px solid #3f6ebe;
    box-shadow: 0 0 10px 0px #090617;
  }
  .modal-title {
    text-align: center;
    line-height: 36px;
    color: white;
    margin-top: 10px;
    padding: 0 20px;
    font-size: 16px;
  }
  .modal-body {
    padding: 10px 20px;
  }
  .btn-line {
    display: flex;
    justify-content: space-between;
    margin: 20px;
  }
}
</style>
