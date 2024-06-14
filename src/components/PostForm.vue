<template>
  <div class="post">
    <div class="post__select">
      <im-select v-model:model-value="selectedPost"
                 :options="postOptions">

      </im-select>
    </div>
    <div class="post__input">
      <div style="width: 100%; margin-right: 15px;">
        <im-input
            v-model="post.body"
            type="text"
            placeholder="Notes"
        />
      </div>
      <div>
        <im-button
            style="align-self: flex-end; margin-top: 3px"
            @click="createOnePost"
        >
          Gem
        </im-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  data() {
    return {
      post: {
        id: 0,
        body: '',
      },
      selectedPost: '',
      postOptions: [
        {name: "Test note 1"},
        {name: "Test note 2"},
        {name: "Test note 3"}
      ]
    }
  },
  emits: ['create'],
  methods: {
    createOnePost() {
      this.post.id = Date.now();
      this.$emit('create', this.post)
      this.post = {
        id: 0,
        body: '',
      }
    },
  },
  watch: {
    selectedPost(newValue) {
      this.post.id = Date.now();
      this.post.body = newValue;
    }
  },
})
</script>

<style scoped>
.post {

}

.post__input {
  padding: 2px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.post__select {
  padding: 2px;
  display: flex;
}


</style>