<template>
  <button class="im-button" :disabled="disabled">
    <slot></slot>
  </button>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'im-button',
  props: {
    disabled: [Boolean]
  }
})
</script>

<style scoped>
.im-button {
  padding: 10px 15px;
  background: none;
  color: teal;
  border: 1px solid teal;
}
</style>