<template>
  <label>
    <input class="checkBox" type="checkbox" :id="id" :checked="modelValue" @change="handleClick" :disabled="disabled"/>
    {{ text }}
  </label>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'im-checkbox',
  emits: ['update:modelValue'],
  props: {
    text: [String],
    modelValue: [Boolean],
    disabled: [Boolean]
  },
  methods: {
    handleClick(event: { target: { checked: any } }){
      this.$emit('update:modelValue', event.target.checked)
    }
  },
})
</script>

<style scoped>
.checkBox {
  border: 1px solid #6c757d;
}

</style>