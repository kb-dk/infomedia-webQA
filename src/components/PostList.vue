<template>
  <div v-if="posts.length > 0">
    <div></div>
    <post-item v-for="(post,index) in posts" :key="post" :post="post.note" @remove="$emit('remove', post,index)">
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