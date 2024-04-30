<template>
  <div class="post__item">
      <div class="output">
        <div>{{ post}}</div>
      </div>

    <div>
      <im-button @click="$emit('remove', post)">
        Slet
      </im-button>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  emits: ['remove'],
  props: {
    post: {
      type: Object,
      required: true,
    }
  }
})
</script>

<style scoped>
.post__item {
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.output {
  width: 100%;
  border: 1px solid teal;
  padding: 10px 15px;
  margin-right: 15px;
  text-align: left;
}
</style>