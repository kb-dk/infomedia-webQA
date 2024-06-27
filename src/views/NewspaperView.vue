<template>
  <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  <div class="app">
    <b-row class="row">
      <b-col>
        <notes-form :postsTitel="dayNotes" :batch="batch" :notes-type="NotesType.BATCHNOTE"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="editionNotes" :batch="batch" :notes-type="NotesType.EDITIONNOTE"
                    :newspaper="newspaper"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="sectionNotes" :batch="batch" :notes-type="NotesType.SECTIONNOTE"
                    :newspaper="newspaper" :sectiontitle="currentSectionTitle"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="pageNotes" :batch="batch" :notes-type="NotesType.PAGENOTE"
                    :newspaper="newspaper" :sectiontitle="currentSectionTitle"
                    :pagenumber="currentPageNumber"></notes-form>
      </b-col>
    </b-row>
    <b-row>
      <b-col sm="10">
        <im-carousel ref="carousel" :carouselVal="currentPagesNames" :items-to-show="itemToShow"
                     :front-page-view="frontPageView" @current-filename-event="handleCurrentFilename"></im-carousel>
      </b-col>
      <b-col sm="2">
        <br>
        <b-button :variant="newspaperData.checked ? 'success':'primary'" class="approveNewspaperBtn"
                  @click="approveNewspaper()">Approve newspaper
        </b-button>
        <br>
        <br>
        <PageTable :pagesFileName="pagesNames" :rowClick="switchPage"></PageTable>
        <b-button v-if="!frontPageView" class="changeCarouselView"
                  @click="changeToFrontPageView()">Show Front Pages
        </b-button>
      </b-col>
    </b-row>
  </div>
</template>

<script>

import {defineComponent, getCurrentInstance, onMounted} from "vue";
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
    const instance = getCurrentInstance();

    onMounted(async function () {
      try {
        await instance.proxy.fetchCarouselData();
        await instance.proxy.initCurrentFrontPage();
      } catch (error) {
        console.error(error);
      }
    });
  },
  data() {
    return {
      dayNotes: "Day notes",
      editionNotes: "Edition notes",
      sectionNotes: "Section notes",
      pageNotes: "Page notes",
      checkboxText: "Show all pages",
      batch: {
        id: this.$route.params.batchid,
        value: ''
      },
      newspaper: {
        id: this.$route.params.newspaperid,
        value: ''
      },
      currentFileName: "",
      currentPageNumber: 0,
      currentSectionTitle: "",
      errorMessage: "",
      pagesNames: [],
      frontPagesNames: [],
      currentPagesNames: [],
      newspaperData: {},
      frontPageView: true,
      itemToShow: 2
    }
  },
  components: {
    NotesForm,
    PageTable
  },
  created(){
    this.fetchNewspaper();
  },
  methods: {
    async fetchCarouselData() {
      try {
        const apiClient = axios.create({
          baseURL: "/api",
        });
        const {batchid, newspaperid} = this.$route.params;

        const response = await apiClient.get(
            `/batches/${batchid}/newspapers/${newspaperid}/newspaper-pages`
        );
        const frontPagePaths = response.data.filter((d) => d.page_number === 1);
        this.pagesNames = response.data.map((d) => {
          const filePathParts = d.filepath.split("/");
          return filePathParts[filePathParts.length - 1];
        });
        this.frontPagesNames = frontPagePaths.map((d) => {
          const filePathParts = d.filepath.split("/");
          return filePathParts[filePathParts.length - 1];
        });
        this.currentPagesNames = this.frontPagesNames;
        console.log("fetch carousel data: " + this.currentPagesNames)
      } catch (error) {
        this.errorMessage = "Unable to get a frontpages";
        console.error(this.errorMessage + ": " + error);
        this.pagesNames = []; // Return an empty array in case of error
      }
    },

    async fetchNewspaper() {
      try {
        const { batchid, newspaperid } = useRoute().params;
        const { data } = await axios.get(`/api/batches/${batchid}/newspapers/${newspaperid}`);
        this.newspaperData = data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Unable to load newspaper data";
      }
    },

    handleCurrentFilename(filename) {
      this.currentFileName = filename;
      console.log("current file name: " + this.currentFileName)
      this.initCurrentSectionTitle();
      this.initCurrentPageNumber();
    },

    initCurrentSectionTitle() {
      const regex = /section(\d+)/;
      const match = this.currentFileName && this.currentFileName.match(regex);
      if (match) {
        this.currentSectionTitle = match[0];
      }
      console.log("current section title: " + this.currentSectionTitle)
    },

    initCurrentPageNumber() {
      const regex = /page(\d+)/;
      const match = this.currentFileName && this.currentFileName.match(regex);
      if (match) {
        this.currentPageNumber = parseInt(match[1], 10);
      }
      console.log("current page number: " + this.currentPageNumber)
    },

    initCurrentFrontPage() {
      if (this.frontPagesNames.length > 0) {
        this.handleCurrentFilename(this.frontPagesNames[0])
      }
    },
    async approveNewspaper() {
      if (!this.newspaper.checked && confirm("Do you want to approve the newspaper?")) {
        this.newspaper.checked = true;
        try {
          const { id } = this.newspaper;
          await axios.put(`/api/batches/${this.batch.id}/newspapers/${id}`);
        } catch (error) {
          this.errorMessage = "Error approving the newspaper";
          console.log(this.errorMessage + ": " + error);
        }
      }
    },
    switchPage(fileName) {
      this.$refs.carousel.switchPage(fileName);
      this.handleCurrentFilename(fileName);
      this.currentPagesNames = [fileName];
      this.frontPageView = false;
      this.itemToShow = 1;
    },
    changeToFrontPageView(){
      this.currentPagesNames = this.frontPagesNames
      this.frontPageView = true
      this.itemToShow = 2;
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

.row {
  margin: 10px;
}
</style>