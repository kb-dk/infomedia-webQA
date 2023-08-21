<template>
  <form @submit.prevent>
    <div class="form">
      <div class="header">
        <h4>{{ postsTitel }}</h4>
      </div>
      <post-form @create="createPost">
      </post-form>
      <post-list :posts="posts" @remove="removePost"></post-list>
    </div>
  </form>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import PostForm from "@/components/PostForm.vue";
import PostList from "@/components/PostList.vue";

export default defineComponent({
  data() {
    return {
      posts: [] as any [],
      dialogVisible: false,
    }
  },
  props: {
    postsTitel: [String, Number]
  },
  components: {
    PostForm,
    PostList
  },
  methods: {
    createPost(post: any) {
      this.posts.push(post)
    },
    removePost(post: { id: any; }) {
      this.posts = this.posts.filter((p: { id: any; }) => p.id !== post.id)
    },
    hideDialog() {
      this.dialogVisible = true;
    }
  }
})
</script>

<style scoped>
.form {
  padding: 15px;
  border: 2px solid teal;
  margin-top: 15px;
}

.header {
  text-align: left;
  padding: 15px;
}

</style>