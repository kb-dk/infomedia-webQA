<template>
  <p v-if="errorMessage.length > 0" style="color: red">{{errorMessage}}</p>
  <form @submit.prevent>
    <div class="form">
      <div class="header">
        <h6>{{ postsTitle }}</h6>
      </div>
      <post-form @create="createPost"></post-form>
      <post-list :posts="posts" @remove="removePost"></post-list>
    </div>
  </form>
</template>

<script lang="ts">
import { defineComponent, PropType} from 'vue';
import PostForm from "@/components/PostForm.vue";
import PostList from "@/components/PostList.vue";
import { NotesType } from "@/enums/NotesType";
import axios from "axios";

export default defineComponent({
  name: "NotesForm",
  props: {
    batch: {
      type: Object as PropType<any>,
      required: true
    },
    postsTitle: {
      type: String,
      required: true
    },
    notesType: {
      type: Number as PropType<NotesType>,
      required: true
    },
    newspaper: {
      type: Object as PropType<any>,
      default: null
    },
    sectiontitle: {
      type: String,
      default: null
    },
    pagenumber: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      posts: [],
      errorMessage: ""
    };
  },
  async created() {
    if (this.batch?.id !== undefined) {
      try {
        const res = await this.getNotes();
        this.posts = res;
      } catch (error) {
        console.error('Error fetching notes:', error);
      }
    }
  },
  methods: {
    async getNotes() {
      try {
        const { batch, notesType, newspaper, sectiontitle, pagenumber } = this;
        let url = `/api/batches/${batch.id}`;

        switch (notesType) {
          case NotesType.BATCHNOTE:
            url += '/notes-to-batch';
            break;
          case NotesType.EDITIONNOTE:
            if (newspaper) {
              url += `/newspapers/${newspaper.id}/notes-to-edition`;
            }
            break;
          case NotesType.SECTIONNOTE:
            if (newspaper && sectiontitle) {
              url += `/newspapers/${newspaper.id}/notes-to-section?section_title=${sectiontitle}`;
            }
            break;
          case NotesType.PAGENOTE:
            if (newspaper && sectiontitle && pagenumber) {
              url += `/newspapers/${newspaper.id}/notes-to-pages?section_title=${sectiontitle}&page_number=${pagenumber}`;
            }
            break;
          default:
            throw new Error("Incorrect notestype");
        }

        const response = await axios.get(url);
        console.log("Get Notes From: ", response?.data);
        return response?.data ?? [];
      } catch (error) {
        console.error("Unable to get notes:", error);
        throw new Error("Unable to get notes");
      }
    },
    async createPost(post: { note: any; }) {
      const { batch, notesType, newspaper, sectiontitle, pagenumber } = this;
      if (batch) {
        try {
          let url = `/api/batches/${batch.id}`;
          switch (notesType) {
            case NotesType.BATCHNOTE:
              url += "/notes-to-batch?username=gui";
              break;
            case NotesType.EDITIONNOTE:
              if (newspaper) {
                url += `/newspapers/${newspaper.id}/notes-to-edition?username=gui`;
              }
              break;
            case NotesType.SECTIONNOTE:
              if (newspaper && sectiontitle) {
                url += `/newspapers/${newspaper.id}/notes-to-section?section_title=${sectiontitle}&username=gui`;
              }
              break;
            case NotesType.PAGENOTE:
              if (newspaper && sectiontitle && pagenumber) {
                url += `/newspapers/${newspaper.id}/notes-to-pages?username=gui&section_title=${sectiontitle}&page_number=${pagenumber}&username=gui`;
              }
              break;
            default:
              console.log("Incorrect notestype");
              return;
          }
          const response = await axios.post(url, post.note, {
            headers: { 'Content-Type': 'application/json' }
          });

          this.posts.push({ note: post.note, id: response.data.note_id_created, batch_id: batch.id } as never);
        } catch (error) {
          console.log(error);
          this.errorMessage = "Unable to create a note";
        }
      }
    },
    async removePost(post: { batch_id: any; id: any; }, index: number) {
      try {
        await axios.delete(`/api/batches/${post.batch_id}/notes-to-batch/${post.id}`);
        this.posts.splice(index, 1);
      } catch (error) {
        console.log(error);
        this.errorMessage = "Unable to remove the note";
      }
    }
  },
  components: {
    PostForm,
    PostList
  }
});
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
