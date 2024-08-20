<template>
  <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  <div class="app">
    <b-row style="height: 50px;">
      <b-col sm="1">
        <b-button class="batch-button  previous" @click="previousBatch()">Prev</b-button>
      </b-col>
      <b-col sm="10">
        <div class="notes-container" @mouseenter="showNotes = true" name="expandNotes">
          Display Notes
          <b-row v-if="showNotes">
            <b-col>
              <notes-form :postsTitel="editionNotes" :batch="batch" :notes-type="NotesType.EDITIONNOTE"
                          :newspaper="newspaper"></notes-form>
            </b-col>
            <b-col v-if="currentPagesNames.length > 0">
              <notes-form :postsTitel="sectionNotes" :batch="batch" :notes-type="NotesType.SECTIONNOTE"
                          :newspaper="newspaper" :sectiontitle="currentSectionTitle"></notes-form>
            </b-col>
            <b-col v-if="currentPagesNames.length > 0">
              <notes-form :postsTitel="pageNotes" :batch="batch" :notes-type="NotesType.PAGENOTE"
                          :newspaper="newspaper" :sectiontitle="currentSectionTitle"
                          :pagenumber="currentPageNumber"></notes-form>
            </b-col>
          </b-row>
        </div>
      </b-col>
      <b-col sm="1">
        <b-button class="batch-button next" @click="nextBatch">Next</b-button>
      </b-col>
    </b-row>
    <b-row @mouseenter="showNotes = true">
      <b-col sm="10">
        <im-carousel ref="carousel" :carouselVal="currentPagesNames" :items-to-show="itemToShow"
                     :front-page-view="toWrapAround()" @current-filename-event="handleCurrentFilename"></im-carousel>
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
      itemToShow: 2,
      showNotes: false
    }
  },
  components: {
    NotesForm,
    PageTable
  },
  created() {
    this.fetchNewspaper();
    this.fetchBatchData();
  },
  methods: {
    async fetchCarouselData() {
      try {
        const apiClient = axios.create({
          baseURL: "/kuana-ndb-api",
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
        const {batchid, newspaperid} = this.$route.params;
        const {data} = await axios.get(`/kuana-ndb-api/batches/${batchid}/newspapers/${newspaperid}`);
        this.newspaperData = data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Unable to load newspaper data";
      }
    },
    async fetchBatchData(){
      try{
        const {batchid} = this.$route.params;
        const {data} = await axios.get(`/kuana-ndb-api/batches/${batchid}`);
        this.batch = data;
      }catch (error){
        console.error(error);
        this.errorMessage = "Unable to load batch data"
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
          const {id} = this.newspaper;
          await axios.put(`/kuana-ndb-api/batches/${this.batch.id}/newspapers/${id}`);
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
      this.itemToShow = 1;
    },
    changeToFrontPageView() {
      this.currentPagesNames = this.frontPagesNames
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
      try {
      const newBatch = await axios.get(`/kuana-ndb-api/batches?year=${newDate.getFullYear()}&month=${newDate.getMonth() + 1}&day=${newDate.getDate()}&latest=true&state=TechnicalInspectionComplete`);
      const batchData = newBatch.data;
      if (batchData.length > 0) {
        const newNewspaper = await axios.get(`/kuana-ndb-api/batches/${batchData[0].id}/newspapers?newspaper_name=${this.newspaperData.newspaper_name}`);
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
        }
      }
      } catch (error) {
        this.errorMessage = "An error occurred while fetching data. Please try again later.";
        console.log(this.errorMessage + ": " + error);
      }
    },
    toWrapAround(){
      return this.currentPagesNames.length > 1
    }
  }
})
</script>

<style lang="scss">
.row {
  margin: 10px;
}
.batch-button {
  height: 3em;
  border-radius: 3px;
  box-shadow: 1px 1px 3px black;
  vertical-align: baseline;
  width: 6em;
}
.notes-container {
  width: 100%;
  height: 3em;
  background-color: #dbc23eb2;
  border-radius: 3px;
  box-shadow: 1px 1px 3px black;
  cursor: pointer;
  text-align: center;
  color: white;
  font-weight: bold;
  text-shadow: 1px 1px 3px black;
  display: inline-block;
  padding-top: 10px;
}
.notes-container > * {
  display: inline-block;
  position: relative; /* or absolute */
  z-index: 1000;
  flex-grow: 1;
  border-radius: 3px;
  //border: 1px solid rgba(90, 89, 89, 0.71);
  text-shadow: 1px 1px 1px white;
  color: black;
  font-weight: normal;
}
</style>