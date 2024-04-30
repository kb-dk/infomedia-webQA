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
import {defineComponent, PropType, Ref, ref} from 'vue'
import PostForm from "@/components/PostForm.vue";
import PostList from "@/components/PostList.vue";
import {NotesType} from "@/enums/NotesType";
import axios from "axios";
import {data} from "v-calendar/dist/types/tests/unit/util/dayData";

export default defineComponent({
  name:"NotesForm",
  props: {
    batch: [Object],
    postsTitel: [String, Number],
    notesType: Number as PropType<NotesType>,

    newspaper: [Object]
  },
  data() {
    return {
      posts: ref([]) as Ref<Array<object>>,
      dialogVisible: false,
    }
  },
  created() {
    if (this.batch?.id !== undefined) {
      this.getNotes().then((res) => {

        this.posts = res;
      })
    }
  },
  components: {
    PostForm,
    PostList
  },
  methods: {
    createPost(post: { id: any; body: any }) {
      console.log(this.batch)
      if (this.batch) {
        switch (this.notesType) {
          case NotesType.BATCHNOTE:
            axios({
              method: "POST",
              url: `/api/batches/${this.batch.id}/notes-to-batch?username=gui`,
              data: post.body,
              headers: {'Content-Type': 'Text'}
            })
            break;
          case NotesType.EDITIONNOTE:
            if (this.newspaper) {
              axios({
                method: "POST",
                url: `/api/batches/${this.batch.id}/newspapers/${this.newspaper.id}/notes-to-batch?username=gui`,
                data: post.body,
                headers: {'Content-Type': 'Text'}
              })
            }
            break;
          case NotesType.SECTIONNOTE:
            if (this.newspaper) {
              //TODO add section to url
              axios({
                method: "POST",
                url: `/api/batches/${this.batch.id}/newspapers/${this.newspaper.id}/notes-to-section?username=gui`,
                data: post.body,
                headers: {'Content-Type': 'Text'}
              })
            }
            break;
          case NotesType.PAGENOTE:
            if (this.newspaper) {
              //TODO add section and page_number to url
              axios({
                method: "POST",
                url: `/api/batches/${this.batch.id}/newspapers/${this.newspaper.id}/notes-to-pages?username=gui`,
                data: post.body,
                headers: {'Content-Type': 'Text'}
              })
            }
            break;
          default:
            console.log("Incorrect notestype")
        }

        this.posts.push({note:post.body})
      }

    },
    removePost(post: { id: any; }) {
      // this.posts = this.posts.filter((p: { id: any; }) => p.id !== post.id)
    },
    hideDialog() {
      this.dialogVisible = true;
    },
    async getNotes() {
      const {data} = await axios.get(`/api/batches/${this.batch?.id}/notes-to-batch`);
      return  data;
    }
  }
})
</script>

<style scoped>
.form {
  padding: 10px;
  border: 2px solid teal;
  margin-top: 10px;
}

.header {
  text-align: left;
  padding: 10px;
}

</style>