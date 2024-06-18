<template>
  <p v-if="errorMessage.toString().length > 0" style="color: red">{{errorMessage}}</p>
  <div class="app">
    <b-row>
      <b-col>
        <notes-form :postsTitel="dayNotes" :batch="this.batch" :notes-type="NotesType.BATCHNOTE"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="editionNotes" :batch="this.batch" :notes-type="NotesType.EDITIONNOTE"
                    :newspaper="this.newspaper"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="sectionNotes" :batch="this.batch" :notes-type="NotesType.SECTIONNOTE"
                    :newspaper="this.newspaper" :sectiontitle="currentSectionTitle"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="pageNotes" :batch="this.batch" :notes-type="NotesType.PAGENOTE"
                    :newspaper="this.newspaper" :sectiontitle="currentSectionTitle"
                    :pagenumber="currentPageNumber"></notes-form>
      </b-col>
    </b-row>
    <b-row>
      <b-col sm="10">
        <im-carousel :carouselVal="carouselVal" @current-filename-event="handleCurrentFilename"></im-carousel>
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
import {NotesType} from "@/enums/NotesType";

export default defineComponent({
  name: "NewspaperView",
  computed: {
    NotesType() {
      return NotesType
    },
  },
  setup() {
    const urlParams = useRoute().params;

    const fetchFrontPages = async () => {
      const response = await axios.get(
          `/api/batches/${urlParams.batchid}/newspapers/${urlParams.newspaperid}/newspaper-pages`
      );
      const frontPages = response.data
          .filter((page) => page.page_number === 1)
          .map((page) => {
            const filePathParts = page.filepath.split("/");
            return filePathParts[filePathParts.length - 1];
          });
      return frontPages;
    };

    const getCurrentSectionTitle = (frontPage) => {
      const regex = /section(\d+)/;
      const match = frontPage.match(regex);
      return match ? match[0] : null;
    };

    const getCurrentPageNumber = (frontPage) => {
      const regex = /page(\d+)/;
      const match = frontPage.match(regex);
      return match ? parseInt(match[1], 10) : 0;
    };

    const carouselVal = ref([]);
    const currentFrontPage = ref("");
    const currentSectionTitle = ref("");
    const currentPageNumber = ref(0);

    const initializeCarousel = async () => {
      carouselVal.value = await fetchFrontPages();
      currentFrontPage.value = carouselVal.value[0];
      currentSectionTitle.value = getCurrentSectionTitle(currentFrontPage.value);
      console.log(currentSectionTitle.value)
      currentPageNumber.value = getCurrentPageNumber(currentFrontPage.value);
      console.log(currentPageNumber.value)
    };

    initializeCarousel();

    return {
      urlParams,
      carouselVal,
      currentFrontPage,
      currentSectionTitle,
      currentPageNumber,
    };
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
      batch: {
        id: useRoute().params.batchid,
        value: ''
      },
      newspaper:{
        id: useRoute().params.newspaperid,
        value: ''
      },
      currentFileName: '',
      errorMessage: ref("")
    }
  },
  components: {
    NotesForm,
    PageTable
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
        this.errorMessage = "Unable to get a frontpages";
      }
    },
    hideDialog() {
      this.dialogVisible = true;
    },
    handleCurrentFilename(filename) {
      console.log("filename from carousel: " + filename);
      this.currentFileName = filename;
      this.initCurrentSectionTitle();
      this.initCurrentPageNumber();
    },

    initCurrentSectionTitle() {
      const regex = /section(\d+)/;
      const match = this.currentFileName.match(regex);
      if (match) {
        this.currentSectionTitle = match[0];
      }
    },
    initCurrentPageNumber() {
      const regex = /page(\d+)/;
      const match = this.currentFileName.match(regex);
      if (match) {
        const pageNumber = parseInt(match[1], 10);
        this.currentPageNumber = pageNumber;
      }
    },
    initCurrentFrontPage() {
      if (this.frontPages.length > 0) {
        this.currentFileName = this.frontPages[0];
        this.initCurrentSectionTitle();
        this.initCurrentPageNumber();
      }
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