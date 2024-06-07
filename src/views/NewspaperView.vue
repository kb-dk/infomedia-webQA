<template>
  <div class="app">
    <b-row>
      <!--<im-dialog v-model:show="dialogVisible"></im-dialog>-->
    <b-col><notes-form :postsTitel="dayNotes"></notes-form></b-col>
      <b-col><notes-form :postsTitel="editionNotes"></notes-form></b-col>
        <b-col><notes-form :postsTitel="sectionNotes"></notes-form></b-col>
          <b-col><notes-form :postsTitel="pageNotes"></notes-form></b-col>
  </b-row>
  <b-row>
    <b-col sm="8">
<!--      <im-carousel :carouselVal="frontPages"></im-carousel>-->
      <im-pdf-viewer :pdf="frontPages" :checkbox-text="checkboxText"></im-pdf-viewer>
    </b-col>
    <b-col sm="2">
      <PageTable :rowClick="switchPage"></PageTable>
    </b-col>
  </b-row>
  <!--    <NotesForm></NotesForm>-->
  </div>
</template>

<script>

import {defineComponent, ref} from "vue";
import NotesForm from "@/components/NotesForm.vue";
import PageTable from "@/components/PageTable";
import {useRoute} from "vue-router";
import axios from "axios";

export default defineComponent({
  name: "NewspaperView",
  expose: ["pdf"],
  setup() {
    // const urlParams = useRoute().params;
    // axios.get(`/api/batches?day=${urlParams.day}&month=${urlParams.month}&year=${urlParams.year}`).then((res) =>{
    //   console.log(res.data[0])
    //   this.batch = res.data[0];
    // })
    //   axios.get(`/api/batches/${this.batch.id}/newspapers`).then((res) => {
    //     console.log(res.data)
    //     this.newspapers = res.data;
    //   })
    //   axios.get(`/api/batches/${this.batch.id}/newspapers/${urlParams.newspaperid}/newspaper-pages`).then((res) =>{
    //         console.log(res.data)
    //         this.frontPages = res.data[0]
    // })
    // return {
    //   allBatches : async ()=>{return await axios.get(`/api/batches?day=${urlParams.day}&month=${urlParams.month}&year=${urlParams.year}`)},
    //   urlParams};
  },
  data() {
    return {
      dialogVisible: false,
      dayNotes: "Day notes",
      editionNotes: "Edition notes",
      sectionNotes: "Section notes",
      pageNotes: "Page notes",
      checkboxText: "Show all pages",
      pdfCurrentIndex: ref(0),
      frontPages: [],
      newspapersId: '',
      batchId: '',
      errorMessage: '',
      imageUrls: {}
    }
  },
  components: {
    NotesForm,
    PageTable
  },
  created() {
    this.fetchCarouselData()
  },
  methods: {
    async fetchCarouselData() {
      try {
        const apiClient = axios.create({
          baseURL: '/api',
        })
        const urlParams = useRoute().params;

        const response = await apiClient.get(`/batches/${urlParams.batchid}/newspapers/${urlParams.newspaperid}/newspaper-pages`);
        const frontPagePaths = response.data
            .filter(d => d.page_number === 1);
        this.frontPages = frontPagePaths
            .map(d => {
              const filePathParts = d.filepath.split('/');
              return filePathParts[filePathParts.length - 1];
            });
        console.log(this.frontPages );
      } catch (error) {
        console.error(error);
        this.frontPages = []; // Return an empty array in case of error
      }
    },
    async loadImages() {
      try {
        const apiClient = axios.create({
          baseURL: '/api',
        })
        console.log(this.carouselVal)
        for (const item of this.carouselVal) {
          const response = await apiClient.get(`/file/${item}`, {
            responseType: 'blob'
          });
          const blob = new Blob([response.data], {type: 'application/pdf'});
          const url = URL.createObjectURL(blob);
          this.$set(this.imageUrls, item, url);
        }
      }catch (error) {
        console.error(error);
      }
    },
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
    getPage(index = 0) {
      this.pdfCurrent = index;
      //return this.pdfAllPages[0]
      return ""
    },
    getFrontPages(){
      return this.frontPages
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