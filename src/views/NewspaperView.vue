<template>
  <div class="app">
    <b-row>
      <b-col>
        <notes-form :postsTitel="dayNotes"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="editionNotes"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="sectionNotes"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="pageNotes"></notes-form>
      </b-col>
    </b-row>
    <b-row>
      <b-col sm="8" style="width:82%">
        <im-carousel :carouselVal="frontPages"></im-carousel>
        <!--      <im-pdf-viewer :pdf-val="frontPages" :checkbox-text="checkboxText"></im-pdf-viewer>-->
      </b-col>
      <b-col sm="2">
        <PageTable :rowClick="switchPage"></PageTable>
      </b-col>
    </b-row>
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
    const urlParams = useRoute().params;
    const allBatches = async () => {
      const res = await axios.get(
          `/api/batches?day=${urlParams.day}&month=${urlParams.month}&year=${urlParams.year}`
      );
      console.log(res.data[0]);
      const newspapers = await axios.get(
          `/api/batches/${res.data[0].id}/newspapers`
      );
      console.log(newspapers.data);
      return newspapers.data;
    };

    return {allBatches, urlParams};
  },
  data() {
    return {
      dialogVisible: false,
      dayNotes: "Day notes",
      editionNotes: "Edition notes",
      sectionNotes: "Section notes",
      pageNotes: "Page notes",
      checkboxText: "Show all pages",
      frontPages: [],
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
          baseURL: "/api",
        });
        const urlParams = useRoute().params;

        const response = await apiClient.get(
            `/batches/${urlParams.batchid}/newspapers/${urlParams.newspaperid}/newspaper-pages`
        );
        const frontPagePaths = response.data.filter((d) => d.page_number === 1);
        this.frontPages = frontPagePaths.map((d) => {
          const filePathParts = d.filepath.split("/");
          return filePathParts[filePathParts.length - 1];
        });
        console.log(this.frontPages);
      } catch (error) {
        console.error(error);
        this.frontPages = []; // Return an empty array in case of error
      }
    },
    hideDialog() {
      this.dialogVisible = true;
    },
    getFrontPages() {
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