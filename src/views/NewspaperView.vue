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
        <im-pdf-viewer :pdfSource="getPage"></im-pdf-viewer>
        <div v-if="displayFrontpages">

        </div>
      </b-col>
      <b-col sm="2">
        <PageTable :rowClick="switchPage"></PageTable>
      </b-col>
    </b-row>
    {{console.log(test)}}
    <!--    <NotesForm></NotesForm>-->
  </div>
</template>

<script>

import {defineComponent, onMounted, ref} from "vue";
import NotesForm from "@/components/NotesForm.vue";
import PageTable from "@/components/PageTable";
import {useRoute} from "vue-router";
import axios from "axios";

export default defineComponent({
  name: "NewspaperView",
  expose: ["pdf"],
  setup(){
    const urlParams = useRoute().params;
    const test = axios.get(`/api/batches?day=${urlParams.day}&month=${urlParams.month}&year=${urlParams.year}`).then((res) =>{
      console.log(res.data[0])
      const newspapers = axios.get(`/api/batches/${res.data[0].id}/newspapers`).then((res) =>{
        console.log(res.data)
        return res.data
      })
      return newspapers
    })
    return {
      allBatches : async ()=>{return await axios.get(`/api/batches?day=${urlParams.day}&month=${urlParams.month}&year=${urlParams.year}`)},
      test,
      urlParams};
  },
  data() {
    return {
      dialogVisible: false,
      dayNotes: "Day notes",
      editionNotes: "Edition notes",
      sectionNotes: "Section notes",
      pageNotes: "Page notes",
      checkboxText: "Show all pages",
      displayFrontpages: false,
      pdfCurrentIndex: ref(0),
    }
  },
  components: {
    NotesForm,
    PageTable
  },
  methods: {
    hideDialog() {
      this.dialogVisible = true;
    },
    switchPage(source) {
      // console.log(this)
      this.pdfCurrent = source
    },
    // async allBatches() {
    //   return await axios.get(`/api/batches?day=${this.urlParams.day}&month=${this.urlParams.month}&year=${this.urlParams.year}`)
    //   // return axios.get(url);
    // },
    // async allPages(){
    //   const latestBatch = this.allBatches()
    //
    // },
    getPage(index=0){
      this.pdfCurrent = index;
      // return this.pdfAllPages[0]
      return ""
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