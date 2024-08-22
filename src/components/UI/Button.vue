<template>
  <button class="im-button" :class="disabled?'disabled':''" :disabled="disabled">
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
  padding: 2px 3px;
  background: none;
  color: black;
  border: 1px solid #6c757d;
}
.disabled{
  color:#6c6b6b;
  border: 1px solid #6c6b6b;
}
</style>