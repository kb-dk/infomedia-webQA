<template>
  <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
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

export default defineComponent({
  name: "NotesForm",
  props: {
    batch: Object,
    postsTitel: [String, Number],
    notesType: Number as PropType<NotesType>,
    newspaper: Object,
    sectiontitle: String,
    pagenumber: Number,
  },
  data() {
    return {
      posts: ref([]) as Ref<Array<object>>,
      errorMessage: "",
    }
  },
  mounted() {
    if (this.batch && this.batch.id !== undefined) {
      this.getPosts()
          .then((res) => {
            this.posts = res;
          })
          .catch((error) => {
            console.error('Error fetching notes:', error);
          });
    }
  },
  watch: {
    sectiontitle: {
      immediate: true,
      handler() {
        this.getPosts().then((fetchedPosts) => { // Step 3: Update the posts data property
          this.posts = fetchedPosts;
          // console.log("Section title changed: " + this.sectiontitle);
        }).catch((error) => {
          console.error('Error fetching sections notes:', error);
        });
      }
    },
    pagenumber: {
      immediate: true,
      handler() {
        this.getPosts().then((fetchedPosts) => { // Step 3: Update the posts data property
          this.posts = fetchedPosts;
          // console.log("Page number changed: " + this.pagenumber);
        }).catch((error) => {
          console.error('Error fetching page notes:', error);
        });
      }
    },
  },
  components: {
    PostForm,
    PostList
  },
  methods: {
    async createPost(post: { id: any; note: any; batch_id: any }) {
      if (!this.batch) {
        return;
      }
      try {
        let url = `/api/batches/${this.batch.id}`;
        switch (this.notesType) {
          case NotesType.BATCHNOTE:
            url += "/notes-to-batch?username=gui";
            break;
          case NotesType.EDITIONNOTE:
            if (this.newspaper) {
              url += `/newspapers/${this.newspaper.id}/notes-to-edition?username=gui`;
            }
            break;
          case NotesType.SECTIONNOTE:
            if (this.newspaper && this.sectiontitle) {
              url += `/newspapers/${this.newspaper.id}/notes-to-section?section_title=${this.sectiontitle}&username=gui`;
            }
            break;
          case NotesType.PAGENOTE:
            if (this.newspaper && this.sectiontitle && this.pagenumber) {
              url += `/newspapers/${this.newspaper.id}/notes-to-pages?username=gui&section_title=${this.sectiontitle}&page_number=${this.pagenumber}&username=gui`;
            }
            break;
          default:
            console.log("Incorrect notestype");
            return;
        }
        const response = await axios.post(url, post.note, {
          headers: {'Content-Type': 'text/plain'},
        });

        if (!Array.isArray(this.posts)) {
          this.posts = [];
        }
        this.posts.push({note: post.note, id: response.data.note_id_created, batch_id: this.batch.id})
      } catch (error) {
        console.log(error)
        this.errorMessage = "Unable to create a note";
      }
    },
    async removePost(post: { id: any; batch_id: any; }, index: number) {
      if (!this.batch) {
        return;
      }
      try {
        let url = `/api/batches/${this.batch.id}`;
        switch (this.notesType) {
          case NotesType.BATCHNOTE:
            url += `/notes-to-batch/${post.id}`;
            break;
          case NotesType.EDITIONNOTE:
            if (this.newspaper) {
              url += `/newspapers/${this.newspaper.id}/notes-to-edition?note_id=${post.id}`;
            }
            break;
          case NotesType.SECTIONNOTE:
            if (this.newspaper && this.sectiontitle) {
              url += `/newspapers/${this.newspaper.id}/notes-to-section?note_id=${post.id}`;
            }
            break;
          case NotesType.PAGENOTE:
            if (this.newspaper && this.sectiontitle && this.pagenumber) {
              url += `/newspapers/${this.newspaper.id}/notes-to-pages?note_id=${post.id}`;
            }
            break;
          default:
            console.log("Incorrect notestype");
            return;
        }
        await axios.delete(url);
        this.posts.splice(index, 1)
      } catch (error) {
        console.log("Unable to remove a note: " + error)
        this.errorMessage = "Unable to remove a note";
      }
    },
    async getPosts() {
      if (!this.batch) {
        return;
      }
      try {
        let result;
        switch (this.notesType) {
          case NotesType.BATCHNOTE:
            result = await axios.get(`/api/batches/${this.batch?.id}/notes-to-batch`);
            // console.log("BATCHNOTE: " + result?.data)
            break;
          case NotesType.EDITIONNOTE:
            if (this.newspaper) {
              result = await axios.get(`/api/batches/${this.batch?.id}/newspapers/${this.newspaper.id}/notes-to-edition`);
              // console.log("EDITIONNOTE: " + result?.data)
            }
            break;
          case NotesType.SECTIONNOTE:
            if (this.newspaper && this.sectiontitle) {
              result = await axios.get(`/api/batches/${this.batch?.id}/newspapers/${this.newspaper.id}/notes-to-section?section_title=${this.sectiontitle}`);
              // console.log("SECTIONNOTE: " + result?.data)
            }
            break;
          case NotesType.PAGENOTE:
            if (this.newspaper && this.sectiontitle && this.pagenumber) {
              result = await axios.get(`/api/batches/${this.batch?.id}/newspapers/${this.newspaper.id}/notes-to-pages?section_title=${this.sectiontitle}&page_number=${this.pagenumber}`);
              // console.log("PAGENOTE: " + result?.data)
            }
            break;
          default:
            this.errorMessage = "Incorrect notestype";
            return;
        }
        return result?.data ?? '';
      } catch (error) {
        this.errorMessage = "Unable to get notes";
        console.log("Unable to get notes:" + error);
      }
    }
  }
})
</script>

<style scoped>
.form {
  padding: 10px;
  border: 2px solid #6c757d;
  margin-top: 2px;
  border-radius: 10px;
  background-color: white;
}
.header {
  text-align: left;
  padding: 5px;
}

</style>