<template>
  <select class="select" v-model="localModelValue" @change="changeOption">
    <option disabled value="">Select notes</option>
    <option
        v-for="option in options"
        :key="option.value"
        :value="option.value">
      {{ option }}
    </option>
  </select>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'im-select',
  computed: {
    localModelValue: {
      get():string|undefined {
        return this.modelValue;
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
.select {
  width: 100%;
  border: 1px solid teal;
  padding: 2px 3px;
  margin-top: 3px;
}
</style>