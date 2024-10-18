<template>
  <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  <div class="app">
    <b-row style="height: 30px;">
      <b-col sm="12">
        <im-output :postsTitle="title" class="block-text"/>
      </b-col>
    </b-row>
    <b-row style="height: 50px;">
      <b-col sm="1">
        <b-button class="batch-button  previous" @click="previousBatch()">Prev</b-button>
      </b-col>
      <b-col sm="10">
        <div class="notes-container" ref="notesContainer" @mousedown="showNotes = true" name="expandNotes">
          Display Notes
          <b-row v-if="showNotes">
            <b-col>
              <notes-form :postsTitle="editionNotes" :batch="batch" :notes-type="NotesType.EDITIONNOTE"
                          :newspaper="newspaper"></notes-form>
            </b-col>
            <b-col v-if="currentPagesNames.length > 0">
              <notes-form :postsTitle="sectionNotes" :batch="batch" :notes-type="NotesType.SECTIONNOTE"
                          :newspaper="newspaper" :sectiontitle="currentSectionTitle"></notes-form>
            </b-col>
            <b-col v-if="currentPagesNames.length > 0">
              <notes-form :postsTitle="pageNotes" :batch="batch" :notes-type="NotesType.PAGENOTE"
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
        <im-carousel ref="carousel"  :items-to-show="itemToShow"
                     :additionalCarouselVal="pagesNames" :nextDay="nextDayFrontPagesNames"
                     :front-page-view="toWrapAround()" @current-filename-event="handleCurrentFilename">
        </im-carousel>
      </b-col>
      <b-col sm="2">
        <PageTable ref="pagetable" :pagesFileName="pagesNames" :rowClick="switchPage"
                   :currentPage="currentFileName"></PageTable>
      </b-col>
    </b-row>
    <div class="button-container">
      <b-button v-if="randomPagesView && !oneRandomPageView" class="changeCarouselView"
                @click="changeToRandomSectionPagesView()">Show Random Section Pages
      </b-button>
      <b-button v-if="!frontPageView && !(randomPagesView && !oneRandomPageView)" class="changeCarouselView"
                :variant="isRandomPageButtonClicked ? 'success' : 'secondary'"
                @click="changeToRandomSectionPageView()">
        Show Random Page From {{ currentSectionTitle }}
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

import {defineComponent, getCurrentInstance, onMounted, ref} from "vue";
import NotesForm from "@/components/NotesForm.vue";
import PageTable from "@/components/PageTable";
import axios, {options} from "axios";
import {NotesType} from "@/enums/NotesType";
import ImOutput from "@/components/UI/Output.vue";
import {newspaperPagesStore} from "@/store/newspaperPages";

export default defineComponent({
  name: "NewspaperView",
  computed: {
    NotesType() {
      return NotesType
    },
    title() {
      let day = this.newspaper.day.toString();
      if (day.length < 2) {
        day = '0' + day;
      }

      let month = this.newspaper.month.toString();
      if (month.length < 2) {
        month = '0' + month;
      }

      return `${this.newspaperData.newspaper_name}, ${day}.${month}.${this.newspaper.year}`;
    }
  },
  setup() {
    const instance = getCurrentInstance();
    onMounted(async function () {
      try {
        await instance.proxy.fetchCarouselData();
        await instance.proxy.initCurrentFrontPage();
        // await instance.proxy.fetchSectionPages();
        await instance.proxy.preLoadNextDay();
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
        value: '',
        year: this.$route.params.year,
        month: this.$route.params.month,
        day: this.$route.params.day
      },
      currentFileName: {},
      currentPageNumber: 0,
      currentSectionTitle: ref(""),
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
      nextDayFrontPagesNames: [],
      newspaperPagesStore:newspaperPagesStore()
    }
  },
  components: {
    ImOutput,
    NotesForm,
    PageTable
  },
  created() {
    this.fetchNewspaper();
    this.fetchBatchData();
  },
  mounted() {
    document.addEventListener("click", this.handleClickOutside);
    document.addEventListener('keyup', this.handleKeyPress);
  },
  beforeUnmount() {
    document.removeEventListener("click", this.handleClickOutside);
    document.removeEventListener('keyup', this.handleKeyPress);
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
          baseURL: "/kuana-ndb-api/v1",
        });
        const {batchid, newspaperid} = this.$route.params;

        const response = (await apiClient.get(
            `/batches/${batchid}/newspapers/${newspaperid}/newspaper-pages`
        ).then((res)=>{
          const response = res.data;
          const frontPagesNames = this.filterFrontPages(response.filter((d) => d.page_number === 1));
          for (let i = 0; i < response.length; i++) {

            const filePathParts = response[i].filepath.split("/");
            this.pagesNames[i] = {
              "name": filePathParts[filePathParts.length - 1],
              "section": response[i].section_title,
              "pageNumber": response[i].page_number,
            }
          }
          for (const frontPagesName of frontPagesNames) {
            frontPagesName.loading = true;
          }
          this.frontPagesNames = frontPagesNames;
          this.currentPagesNames = this.frontPagesNames;
          this.newspaperPagesStore.newspaperPages = this.currentPagesNames;
          this.fetchSectionPages();
        }).catch((err)=>{
          console.log("failed doing fetch");
          console.log(err)
        });

      } catch (error) {
        this.errorMessage = "Unable to get a frontpages";
        console.error(this.errorMessage + ": " + error);
        this.pagesNames = []; // Return an empty array in case of error
      }
    },
    filterFrontPages(frontPagePaths) {
      const frontPagesNames = []
      // Sort front pages by section title
      frontPagePaths.sort((a, b) => {
        const sectionA = a.section_title.toLowerCase();
        const sectionB = b.section_title.toLowerCase();
        if (sectionA < sectionB) {
          return -1;
        }
        if (sectionA > sectionB) {
          return 1;
        }
        return 0;
      });

      for (let i = 0; i < frontPagePaths.length; i++) {
        const filePathParts = frontPagePaths[i].filepath.split("/");
        frontPagesNames[i] = {
          name: filePathParts[filePathParts.length - 1],
          section: frontPagePaths[i].section_title,
          pageNumber: frontPagePaths[i].page_number,
        };
      }
      return frontPagesNames;
    },
    async fetchNewspaper() {
      try {
        const {batchid, newspaperid} = this.$route.params;
        const {data} = await axios.get(`/kuana-ndb-api/v1/batches/${batchid}/newspapers/${newspaperid}`);
        this.newspaperData = data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Unable to load newspaper data";
      }
    },
    async fetchBatchData() {
      try {
        const {batchid} = this.$route.params;
        const {data} = await axios.get(`/kuana-ndb-api/v1/batches/${batchid}`);
        this.batch = data;
      } catch (error) {
        console.error(error);
        this.errorMessage = "Unable to load batch data"
      }
    },
    handleCurrentFilename(filename) {
      if (filename) {
        console.log("handleCurrentFilename")
        console.log(filename)
        this.currentFileName = filename;
        this.currentSectionTitle = filename.section
        this.currentPageNumber = filename.pageNumber;
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
          await axios.put(`/kuana-ndb-api/v1/batches/${this.batch.id}/newspapers/${id}`);
          this.nextBatch();
        } catch (error) {
          this.errorMessage = "Error approving the newspaper";
          console.log(this.errorMessage + ": " + error);
        }
      }
    },

    switchPage(fileName) {
      console.log("switchPage")
      console.log(fileName)
      // this.$refs.carousel.switchPage(fileName);
      fileName.loading = true;
      this.newspaperPagesStore.newspaperPage = [fileName];
      this.handleCurrentFilename(fileName);
      this.currentPagesNames = [fileName];
      this.frontPageView = false;
      this.randomPagesView = false;
      this.oneRandomPageView = true;
      this.itemToShow = 1;
      this.currentSectionTitle = fileName.section;
      this.isRandomPageButtonClicked = false;
    },

    changeToFrontPageView() {
      // this.newspaperPagesStore.newspaperFrontPages = [];
      this.newspaperPagesStore.newspaperFrontPages = this.frontPagesNames;
      this.currentPagesNames = this.frontPagesNames
      this.frontPageView = true
      this.randomPagesView = true;
      this.oneRandomPageView = false;
      this.itemToShow = 2;
      this.isRandomPageButtonClicked = false;
    },
    getPreviousDay() {
      const {year, month, day} = this.$route.params;
      let currentDay = new Date(`${year}/${month}/${day}`);
      currentDay.setDate(currentDay.getDate() - 1);
      return currentDay;
    },
    async previousBatch() {
      const newDate = this.getPreviousDay();
      const newData = await this.getOtherBatch(newDate)
      if(newData){
        this.newspaperPagesStore.clearAll();
        this.routeBatch(newData.newspaperData, newData.batchData, newDate, false)
      }
    },
    getNextDay() {
      const {year, month, day} = this.$route.params;
      let currentDay = new Date(`${year}/${month}/${day}`);
      currentDay.setDate(currentDay.getDate() + 1);
      return currentDay;
    },
    async nextBatch() {
      const newDate = this.getNextDay()
      const newData = await this.getOtherBatch(newDate);
      if(newData){
        this.newspaperPagesStore.setUseCached(true);
        this.routeBatch(newData.newspaperData, newData.batchData, newDate, true)
      }

    },

    async getOtherBatch(newDate) {
      try {
        const newBatch = await axios.get(`/kuana-ndb-api/v1/batches?year=${newDate.getFullYear()}&month=${newDate.getMonth() + 1}&day=${newDate.getDate()}&latest=true&state=TechnicalInspectionComplete`);
        const batchData = newBatch.data;
        if (batchData.length > 0) {
          const newNewspaper = await axios.get(`/kuana-ndb-api/v1/batches/${batchData[0].id}/newspapers?newspaper_name=${this.newspaperData.newspaper_name}`);
          const newspaperData = newNewspaper.data;
          return {batchData: batchData[0], newspaperData: newspaperData[0]}

        }
      } catch (error) {
        this.errorMessage = "An error occurred while fetching data. Please try again later.";
        console.log(this.errorMessage + ": " + error);
      }
    },
    routeBatch(newspaperData, batchData, newDate,useCached) {
      if (newspaperData) {
        this.$router.push({
          name: "newspaper-view",
          replace: true,
          params: {
            batchid: batchData.id,
            newspaperid: newspaperData.id,
            year: newDate.getFullYear(),
            month: newDate.getMonth() + 1,
            day: newDate.getDate()
          }
        });
      }
    },
    toWrapAround() {
      if (this.currentPagesNames.length > 1) {
        this.itemToShow = 2;
        return true;
      } else {
        this.itemToShow = 1;
        return false
      }
    },

    changeToRandomSectionPagesView() {
      const randomPagesNames = this.sectionPages.map(({sectionNumber, pageCount}) => {
        const randomPageNumber = this.generateRandomPageNumber(pageCount);
        return this.findFileName(sectionNumber, randomPageNumber);
      });
      console.log("random")
      console.log(randomPagesNames);
      this.randomPagesView = false;
      this.oneRandomPageView = true;
      this.newspaperPagesStore.randomNewspaperPages = randomPagesNames;
      this.currentPagesNames = randomPagesNames;
      this.currentFileName = randomPagesNames[0];
    },

    changeToRandomSectionPageView() {
      let sectionNumber = this.currentSectionTitle;
      let sectionIndex = this.sectionPages.findIndex(page => page.sectionNumber === sectionNumber);
      if (sectionIndex !== -1) {
        let pageCount = this.sectionPages[sectionIndex].pageCount;
        let randomPageNumber = this.generateRandomPageNumber(pageCount);
        let randomPageName = this.findFileName(sectionNumber, randomPageNumber);
        this.isRandomPageButtonClicked = true;
        this.newspaperPagesStore.randomNewspaperPages = [randomPageName];
        this.currentPagesNames = [randomPageName];
        this.currentFileName = randomPageName;
      }
    },

    getPageNumber(currentFileName) {
      if (currentFileName) {
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
      console.log("fetchSectionPages")
      console.log(this.pagesNames)
      for (let i = 0; i < this.pagesNames.length; i++) {
        let sectionNumber = this.pagesNames[i].section;

        // Check if the section number already exists in the sectionPages array
        let sectionIndex = this.sectionPages.findIndex((section) => section.sectionNumber === sectionNumber);

        console.log(sectionIndex)
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
    },
    handleKeyPress(event) {
      if (event.ctrlKey) {
        switch (event.key) {
          case "Enter":
            this.approveNewspaper();
            break;
          case "ArrowLeft":
            this.previousBatch();
            break;
          case "ArrowRight":
            this.nextBatch();
            break;
        }
        if (event.altKey) {
          switch (event.key) {
            case "n":
              this.showNotes = !this.showNotes;
              break;
          }
        }
      }

    },
    async preLoadNextDay() {
      const apiClient = axios.create({
        baseURL: "/kuana-ndb-api/v1",
      });
      const nextDay = this.getNextDay();
      this.getOtherBatch(nextDay).then(async (newData) => {
        if(newData){
          if (newData.batchData && newData.newspaperData) {
            await apiClient.get(
                `/batches/${newData.batchData.id}/newspapers/${newData.newspaperData.id}/newspaper-pages`
            ).then((response) => {
              const filtered = this.filterFrontPages(response.data.filter((d) => d.page_number === 1));
              this.nextDayFrontPagesNames = filtered;
            }).catch((error)=>{
              console.log("nextDayFrontPagesNames failed");
              console.log(error)
            })
          }
        }

      }).catch((error)=>{
        console.log("failed getOtherBatch");
        console.log(error)
      })


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

.block-text {
  text-transform: uppercase;
  font-weight: bold;
}

</style>