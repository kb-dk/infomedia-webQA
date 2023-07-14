<template>
  <div>
    <div class="post">
      <div>
        <div><strong>Beskrivelse:</strong> {{ post.body }}</div>
      </div>
      <div class="post__btns">
        <im-button @click="$emit('remove', post)">
          Slet
        </im-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  props: {
    post: {
      type: Object,
      required: true,
    }
  }
})
</script>

<style scoped>
.post {
  padding: 15px;
  border: 2px solid teal;
  margin-top: 15px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.post__btns {
  display: flex;
}
</style>