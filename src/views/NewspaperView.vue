<template>
  <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  <div class="app">
    <b-row style="height: 50px;">
      <b-col sm="1">
        <b-button class="batch-button  previous" @click="previousBatch()">Prev</b-button>
      </b-col>
      <b-col sm="10">
        <div class="notes-container" ref="notesContainer" @mouseover="showNotes = true" name="expandNotes">
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
    <b-row>
      <b-col sm="10">
        <im-carousel ref="carousel" :carouselVal="currentPagesNames" :items-to-show="itemToShow"
                     :additionalCarouselVal="pagesNames"
                     :front-page-view="toWrapAround()" @current-filename-event="handleCurrentFilename">
        </im-carousel>
      </b-col>
      <b-col sm="2">
        <PageTable ref="pagetable" :pagesFileName="pagesNames" :rowClick="switchPage" :currentPage="currentFileName"></PageTable>
      </b-col>
    </b-row>
    <div class="button-container">
      <b-button v-if="randomPagesView && !oneRandomPageView" class="changeCarouselView"
                @click="changeToRandomSectionPagesView()">Show Random Section Pages
      </b-button>
      <b-button v-if="!frontPageView && !(randomPagesView && !oneRandomPageView)" class="changeCarouselView"
                :variant="isRandomPageButtonClicked ? 'success' : 'secondary'"
                @click="changeToRandomSectionPageView()">
        Show Random Page From {{currentSectionTitle}}
      </b-button>
      <b-button v-if="!frontPageView || !randomPagesView" class="changeCarouselView"
                @click="changeToFrontPageView()">Show Front Pages
      </b-button>
      <b-button :variant="newspaperData.checked ? 'success':'primary'" class="approveNewspaperBtn"
                @click="approveNewspaper()">Approve newspaper
      </b-button>
    </div>
  </div>
</template>

<script>

import {defineComponent, getCurrentInstance, onMounted} from "vue";
import NotesForm from "@/components/NotesForm.vue";
import PageTable from "@/components/PageTable";
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
        await instance.proxy.fetchSectionPages();
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
      currentSectionNumber: 0,
      errorMessage: "",
      pagesNames: [],
      frontPagesNames: [],
      currentPagesNames: [],
      sectionPages: [],
      newspaperData: {},
      frontPageView: true,
      randomPagesView: true,
      oneRandomPageView: false,
      itemToShow: 2,
      showNotes: false,
      isRandomPageButtonClicked: false,
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
  mounted() {
    document.addEventListener("click", this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener("click", this.handleClickOutside);
  },
  methods: {
    handleClickOutside(event) {
      const notesContainer = this.$refs.notesContainer;
      if (!notesContainer.contains(event.target)) {
        this.showNotes = false;
      }
    },
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
        for (let i = 0; i < frontPagePaths.length; i++) {
          const filePathParts = frontPagePaths[i].filepath.split("/");
          this.frontPagesNames[i] = {
            "name": filePathParts[filePathParts.length - 1],
            "section": frontPagePaths[i].section_title,
            "pageNumber": frontPagePaths[i].page_number
          };
        }
        for (let i = 0; i < response.data.length; i++) {
          const filePathParts = response.data[i].filepath.split("/");
          this.pagesNames[i] = {
            "name": filePathParts[filePathParts.length - 1],
            "section": response.data[i].section_title,
            "pageNumber": response.data[i].page_number
          }
        }

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
    async fetchBatchData() {
      try {
        const {batchid} = this.$route.params;
        const {data} = await axios.get(`/kuana-ndb-api/batches/${batchid}`);
        this.batch = data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Unable to load batch data"
      }
    },
    handleCurrentFilename(filename) {
      if(filename){
        this.currentFileName = filename;
        this.currentSectionTitle = filename.section
        this.initCurrentPageNumber();
      }

    },

    initCurrentSectionTitle() {
      if(this.currentFileName instanceof String){
        const regex = /section(\d+)/;
        console.log(this.currentFileName.match(regex))
        const match = this.currentFileName && this.currentFileName.match(regex);
        if (match) {
          this.currentSectionTitle = match[0];
        }
      }
    },

    initCurrentPageNumber() {
      if(this.currentFileName instanceof String){
        const regex = /page(\d+)/;
        const match = this.currentFileName && this.currentFileName.match(regex);
        if (match) {
          this.currentPageNumber = parseInt(match[1], 10);
        }
      }
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
      this.handleCurrentFilename(fileName.name);
      this.currentPagesNames = [fileName];
      this.frontPageView = false;
      this.randomPagesView = false;
      this.oneRandomPageView = true;
      this.itemToShow = 1;
      this.currentSectionTitle = fileName.section_title;
      this.isRandomPageButtonClicked = false;
    },

    changeToFrontPageView() {
      this.currentPagesNames = this.frontPagesNames
      this.frontPageView = true
      this.randomPagesView = true;
      this.oneRandomPageView = false;
      this.itemToShow = 2;
      this.isRandomPageButtonClicked = false;
    },

    async previousBatch() {
      const {year, month, day} = this.$route.params;
      let currentDay = new Date(`${year}/${month}/${day}`);
      currentDay.setDate(currentDay.getDate() - 1);
      await this.getOtherBatch(currentDay);
    },

    async nextBatch() {
      const {year, month, day} = this.$route.params;
      let currentDay = new Date(`${year}/${month}/${day}`);
      currentDay.setDate(currentDay.getDate() + 1);
      await this.getOtherBatch(currentDay);
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

    toWrapAround() {
      if (this.currentPagesNames.length > 1)
      {
        this.itemToShow = 2;
        return true;
      }   else {
        this.itemToShow = 1;
        return false
      }
    },

    changeToRandomSectionPagesView() {
      const randomPagesNames = this.sectionPages.map(({sectionNumber, pageCount}) => {
        const randomPageNumber = this.generateRandomPageNumber(pageCount);
        return this.findFileName(sectionNumber, randomPageNumber);
      });

      this.randomPagesView = false;
      this.oneRandomPageView = true;
      this.currentPagesNames = randomPagesNames;
    },

    changeToRandomSectionPageView() {
      let sectionNumber = this.currentSectionTitle;
      let sectionIndex = this.sectionPages.findIndex(page => page.sectionNumber === sectionNumber);
      if (sectionIndex !== -1) {
        let pageCount = this.sectionPages[sectionIndex].pageCount;
        let randomPageNumber = this.generateRandomPageNumber(pageCount);
        let randomPageName = this.findFileName(sectionNumber, randomPageNumber);
        this.isRandomPageButtonClicked = true;
        this.currentPagesNames = [randomPageName];
      }
    },

    getSectionNumber(currentFileName) {
      if(currentFileName){
        const sectionMatch = currentFileName.match(/section(\d+)/)[1];
        if (sectionMatch) {
          return parseInt(sectionMatch[1]);
        }
      }

      return 0;
    },

    getPageNumber(currentFileName) {
      if(currentFileName){
        return currentFileName.match(/page(\d+)/)[1];
      }
     return 0;
    },

    generateRandomPageNumber(pageCount) {
      return Math.floor(Math.random() * (pageCount - 2 + 1)) + 2;
    },

    findFileName(sectionName, randomPageNumber) {
      let fileName = null;
      for (let i = 0; i < this.pagesNames.length; i++) {
        let sectionNumber = this.pagesNames[i].section;
        let pageNumber = this.getPageNumber(this.pagesNames[i].name);
        if (sectionNumber === sectionName && pageNumber === String(randomPageNumber).padStart(3, '0')) {
          fileName = this.pagesNames[i];
          break;
        }
      }
      return fileName;
    },

    async fetchSectionPages() {
      for (let i = 0; i < this.pagesNames.length; i++) {
        let pageName = this.pagesNames[i].name;
        //Extract the section number using a regular expression
        // let sectionNumber = this.getSectionNumber(pageName);
        let sectionNumber = this.pagesNames[i].section;

        // Check if the section number already exists in the sectionPages array
        let sectionIndex = this.sectionPages.findIndex((section) => section.sectionNumber === sectionNumber);

        if (sectionIndex !== -1) {
          // If the section number exists, increment the page count for that section
          this.sectionPages[sectionIndex].pageCount++;
        } else {
          // If the section number does not exist, add a new entry to the sectionPages array
          this.sectionPages.push({
            sectionNumber: sectionNumber,
            pageCount: 1
          });
        }
      }
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
  position: relative; /* or absolute */
  z-index: 1000;
  flex-grow: 1;
  border-radius: 3px;
  //border: 1px solid rgba(90, 89, 89, 0.71);
  text-shadow: 1px 1px 1px white;
  color: black;
  font-weight: normal;
}

.button-container {
  bottom: 0;
  width: 80%;
  display: flex;
  padding-bottom: 20px;
  padding-top: 5px;
  justify-content: center;
  position: fixed;
}

.button-container .btn {
  margin-right: 10px; /* Add margin to the right */
  margin-left: 10px;
}

</style>