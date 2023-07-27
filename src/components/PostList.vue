<template>
  <div v-if="posts.length > 0">
  <div></div>
  <post-item v-for="post in posts" :key="post.id" :post="post" @remove="$emit('remove', post)">
  </post-item>
  </div>
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
  },
  emits: ['remove'],
})
</script>

<style scoped>

</style>