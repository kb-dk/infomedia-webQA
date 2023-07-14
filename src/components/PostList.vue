<template>
  <div></div>
  <post-item v-for="post in posts" :key="post.id" :post="post" @remove="$emit('remove', post)">
  </post-item>
</template>

<script lang="ts">

import {defineComponent} from 'vue'
import PostItem from "@/components/PostItem.vue";

export default defineComponent({
  components: {
    PostItem

  },
  props: {
    posts: {
      type: Array,
      required: true
    }
  }
})
</script>

<style scoped>
.post {
  padding: 15px;
  border: 2px solid teal;
  margin-top: 15px;
}
</style>