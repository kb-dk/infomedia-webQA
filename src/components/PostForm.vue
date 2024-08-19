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
            v-model="post.note"
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
import {defineComponent, PropType} from 'vue'
import webConfig from '@/assets/configFiles/webQAconfig';
import {NotesType} from "@/enums/NotesType";

export default defineComponent({
  props:{
    notesType: Number as PropType<NotesType>,
  },
  data() {
    return {
      post: {
        note: '',
        id: 0,
        batch_id: '',
      },
      selectedPost: '',
      postOptions: [{}]
    }
  },
  mounted() {
      this.loadPostOptions();
  },

  emits: ['create'],
  methods: {
    async loadPostOptions() {
      try {
        switch (this.notesType){
          case NotesType.BATCHNOTE:
            this.postOptions = webConfig.postOptions.dayNotes;
            break;
          case NotesType.EDITIONNOTE:
            this.postOptions = webConfig.postOptions.editionNotes;
            break;
          case NotesType.SECTIONNOTE:
            this.postOptions = webConfig.postOptions.sectionNotes;
            break;
          case NotesType.PAGENOTE:
            this.postOptions = webConfig.postOptions.pageNotes;
            break;
        }

      } catch (error) {
        console.error('Failed to load postOptions:', error);
      }
    },
    createOnePost() {
      this.post.id = Date.now();
      this.$emit('create', this.post)
      this.post = {
        note: '',
        id: 0,
        batch_id: '',
      }
    }
  },
  watch: {
    selectedPost(newValue) {
      this.post.id = Date.now();
      this.post.note = newValue;
    }
  },
})
</script>

<style scoped>
.post {

}

.post__input {
  padding: 5px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.post__select {
  padding: 5px;
  display: flex;
}


</style>