<template>
  <div class="app">
    <post-form @create="createPost">
    </post-form>
    <post-list :posts="posts" @remove="removePost"></post-list>
  </div>
</template>
<script>

import {defineComponent} from "vue";
import PostForm from "@/components/PostForm.vue";
import PostList from "@/components/PostList.vue";

export default defineComponent({
  data() {
    return {
      posts: [
        {id:1, body: 'test'},
      ]
    }
  },
  components: {
    PostForm,
    PostList
  },
  methods:{
    createPost(post) {
     this.posts.push(post)
    },
    removePost(post){
      this.posts = this.posts.filter(p => p.id !== post.id)
    }
  }
})
</script>
<style lang="scss">

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
