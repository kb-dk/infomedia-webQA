<template>
  <div class="app">
    <b-row>
      <b-col sm="4">
        <im-dialog v-model:show="dialogVisible"></im-dialog>
        <notes-form :postsTitel="dayNotes"></notes-form>
        <notes-form :postsTitel="editionNotes"></notes-form>
        <notes-form :postsTitel="sectionNotes"></notes-form>
        <notes-form :postsTitel="pageNotes"></notes-form>
      </b-col>
      <b-col sm="6">
        <im-pdf-viewer :pdfVal="encodeURIComponent(pdfVal)" :checkbox-text="checkboxText"></im-pdf-viewer>
      </b-col>
      <b-col sm="2">
        <PageTable :rowClick="switchPage"></PageTable>
      </b-col>
    </b-row>
<!--    <NotesForm></NotesForm>-->
  </div>
</template>

<script>

import {defineComponent,ref} from "vue";
import NotesForm from "@/components/NotesForm.vue";
import PageTable from "@/components/PageTable";

export default defineComponent({
  name:"NewspaperView",
  expose:["pdf"],
  data() {
    return {
      dialogVisible: false,
      dayNotes: "Day notes",
      editionNotes: "Edition notes",
      sectionNotes: "Section notes",
      pageNotes: "Page notes",
      checkboxText: "Show all pages",
      pdfVal:ref("20210101_aarhusstiftstidende_section01_page001_ast20210101x11#0001.pdf")
    }
  },
  components: {
    NotesForm,
    PageTable
  },
  methods:{
    hideDialog(){
      this.dialogVisible = true;
    },
    switchPage(source){
      // console.log(this)
      this.pdfVal = source
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