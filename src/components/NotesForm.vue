<template>
  <form @submit.prevent>
    <div class="form">
      <div class="header">
        <h6>{{ postsTitel }}</h6>
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
    async createPost(post: { id: any; body: any }) {
      if (this.batch) {
        let response = {data:{note_id_created:""}};
        switch (this.notesType) {
          case NotesType.BATCHNOTE:
            response = await axios({
              method: "POST",
              url: `/api/batches/${this.batch.id}/notes-to-batch?username=gui`,
              data: post.body,
              headers: {'Content-Type': 'Text'}
            })
            break;
          case NotesType.EDITIONNOTE:
            if (this.newspaper) {
              response = await axios({
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
              response = await axios({
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
              response = await axios({
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

        this.posts.push({note:post.body,id:response.data.note_id_created,batch_id:this.batch.id})
      }

    },
    removePost(post: { id: any;batch_id:any;},index:number) {
      axios.delete(`/api/batches/${post.batch_id}/notes-to-batch/${post.id}`);
      this.posts.splice(index,1)
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
  padding: 2px;
  border: 2px solid teal;
  margin-top: 2px;
}

.header {
  text-align: left;
  padding: 2px;
}

</style>