<template>
  <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  <div class="app">
    <b-button class="otherBatchButton" @click="previousBatch()">PREV</b-button>
    <div class="showNotesDiv" @mouseenter="showNotes = true" name="expandNotes">
      DISPLAY NOTES
      <b-row v-if="showNotes">
        <b-col >
          <notes-form :postsTitel="dayNotes" :batch="batch" :notes-type="NotesType.BATCHNOTE"></notes-form>
        </b-col>
        <b-col >
          <notes-form :postsTitel="editionNotes" :batch="batch" :notes-type="NotesType.EDITIONNOTE"
                      :newspaper="newspaper"></notes-form>
        </b-col>
        <b-col >
          <notes-form :postsTitel="sectionNotes" :batch="batch" :notes-type="NotesType.SECTIONNOTE"
                      :newspaper="newspaper" :sectiontitle="currentSectionTitle"></notes-form>
        </b-col>
        <b-col>
          <notes-form :postsTitel="pageNotes" :batch="batch" :notes-type="NotesType.PAGENOTE"
                      :newspaper="newspaper" :sectiontitle="currentSectionTitle"
                      :pagenumber="currentPageNumber"></notes-form>
        </b-col>
      </b-row>
    </div>
    <b-button class="otherBatchButton" @click="nextBatch()">NEXT</b-button>
    <b-row @mouseenter="showNotes = false">
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
      itemToShow: 2,
      showNotes: false
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
      } catch (error) {
        this.errorMessage = "Unable to get a frontpages";
        console.error(this.errorMessage + ": " + error);
        this.pagesNames = []; // Return an empty array in case of error
      }
    },

    async fetchNewspaper() {
      try {
        const { batchid, newspaperid } = this.$route.params;
        const { data } = await axios.get(`/api/batches/${batchid}/newspapers/${newspaperid}`);
        this.newspaperData = data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Unable to load newspaper data";
      }
    },

    handleCurrentFilename(filename) {
      this.currentFileName = filename;
      this.initCurrentSectionTitle();
      this.initCurrentPageNumber();
    },

    initCurrentSectionTitle() {
      const regex = /section(\d+)/;
      const match = this.currentFileName && this.currentFileName.match(regex);
      if (match) {
        this.currentSectionTitle = match[0];
      }
      // console.log("current section title: " + this.currentSectionTitle)
    },

    initCurrentPageNumber() {
      const regex = /page(\d+)/;
      const match = this.currentFileName && this.currentFileName.match(regex);
      if (match) {
        this.currentPageNumber = parseInt(match[1], 10);
      }
      // console.log("current page number: " + this.currentPageNumber)
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
    changeToFrontPageView() {
      this.currentPagesNames = this.frontPagesNames
      this.frontPageView = true
      this.itemToShow = 2;
    },
    async previousBatch() {
      const {year, month, day} = this.$route.params;
      let currentDay = new Date(`${year}/${month}/${day}`);
      currentDay.setDate(currentDay.getDate() - 1);
      this.getOtherBatch(currentDay);
    },
    async nextBatch() {
      const {year, month, day} = this.$route.params;
      let currentDay = new Date(`${year}/${month}/${day}`);
      currentDay.setDate(currentDay.getDate() + 1);
      this.getOtherBatch(currentDay);
    },
    async getOtherBatch(newDate) {
      const newBatch = await axios.get(`/api/batches?year=${newDate.getFullYear()}&month=${newDate.getMonth() + 1}&day=${newDate.getDate()}&latest=true&state=TechnicalInspectionComplete`);
      const batchData = newBatch.data;
      if (batchData.length > 0) {
        const newNewspaper = await axios.get(`/api/batches/${batchData[0].id}/newspapers?newspaper_name=${this.newspaperData.newspaper_name}`);
        const newspaperData = newNewspaper.data;
        if (newspaperData.length > 0) {
          this.$router.push({
            name: "newspaper-view",
            replace: true,
            params: {
              batchid: batchData[0].id,
              newspaperid: newspaperData[0].id,
              year: newDate.getFullYear(),
              month: newDate.getMonth() + 1,
              day: newDate.getDate()
            }
          });
          // this.fetchNewspaper();
        }
      }

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

.otherBatchButton {
  height: 3em;
  border-radius: 3px;
  box-shadow: 1px 1px 3px black;
  padding: 10px;
  //display: inline-block;
  margin: 3px;
  vertical-align: baseline;

}

.showNotesDiv {
  width: 90%;
  height: 3em;
  background-color: #dbc23eb2;
  border-radius: 3px;
  box-shadow: 1px 1px 3px black;
  //max-height: 30em;
  cursor: pointer;
  text-align: center;
  padding: 10px;
  color: white;
  font-weight: bold;
  text-shadow: 1px 1px 3px black;
  display: inline-block;
}

.showNotesDiv > * {
  position: absolute;
  z-index: 1;
  background-color: white;
  width: 70%;
  border-radius: 3px;
  border: 1px solid rgba(90, 89, 89, 0.71);
  text-shadow: 1px 1px 1px white;
  color: black;
  font-weight: normal;
}

</style>