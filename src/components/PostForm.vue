<template>
  <form @submit.prevent>
    <h4>Create note</h4>
    <im-input
        v-model="post.body"
        type="text"
        placeholder="Notes"
    />
    <im-button
        style="align-self: flex-end; margin-top: 15px"
        @click="createPost"
    >
      Gem
    </im-button>
  </form>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  data() {
    return {
      post: {
        id: 0,
        body: '',
      }
    }
  },
  methods: {
    createPost() {
      this.post.id = Date.now();
      this.$emit('create', this.post)
      this.post = {
        id: 0,
        body: '',
      }

    }
  },
})
</script>

<style scoped>
form {
  display: flex;
  flex-direction: column;
}
</style>