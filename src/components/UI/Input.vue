<template>
  <input :value="modelValue" @input="updateInput" class="input" type="text">
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'im-input',
  props: {
    modelValue: [String, Number]
  },
  methods: {
    updateInput(event: { target: { value: any } }) {
      this.$emit('update:modelValue', event.target.value)
    }
  }
})
</script>

<style scoped>
.input {
  width: 100%;
  border: 1px solid teal;
  padding: 10px 15px;
  margin-top: 15px;
}
</style>