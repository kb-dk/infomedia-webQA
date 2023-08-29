<template>
  <div class="app">
    <b-row>
      <b-col sm="5">
        <im-dialog v-model:show="dialogVisible"></im-dialog>
        <notes-form :postsTitel="dayNotes"></notes-form>
        <notes-form :postsTitel="editionNotes"></notes-form>
        <notes-form :postsTitel="sectionNotes"></notes-form>
        <notes-form :postsTitel="pageNotes"></notes-form>
      </b-col>
      <b-col sm="5">
        <im-pdf-viewer :pdf="pdf" :checkbox-text="checkboxText"></im-pdf-viewer>
      </b-col>
    </b-row>
<!--    <NotesForm></NotesForm>-->
  </div>
</template>

<script>

import {defineComponent} from "vue";
import NotesForm from "@/components/NotesForm.vue";

export default defineComponent({
  data() {
    return {
      dialogVisible: false,
      dayNotes: "Day notes",
      editionNotes: "Edition notes",
      sectionNotes: "Section notes",
      pageNotes: "Page notes",
      pdf: "test_pdf.pdf",
      checkboxText: "Show all pages"
    }
  },
  components: {
    NotesForm
  },
  methods:{
    hideDialog(){
      this.dialogVisible = true;
    },
  },
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