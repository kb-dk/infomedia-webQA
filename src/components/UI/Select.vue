<template>
  <select v-model="localModelValue" @change="changeOption">
    <option disabled value="">Select notes</option>
    <option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
    >
      {{ option.name }}
    </option>
  </select>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'im-select',
  computed: {
    localModelValue: {
      get() {
        return this.modelValue
      },
      set(newValue: any) {
        this.$emit('update:modelValue', newValue)
      },
    },
  },
  props: {
    modelValue: {
      type: String
    },
    options: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    changeOption(event: { target: { value: any; }; }) {
      this.$emit('update:modelValue', event.target.value);
    }
  }
})
</script>

<style scoped>

</style>