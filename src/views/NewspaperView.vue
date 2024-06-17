<template>
  <div class="app">
    <p v-if="errorMessage.length !== 0" style="color: red;">{{ errorMessage }}</p>
    <b-row>
      <b-col>
        <notes-form :postsTitel="dayNotes" :batch="this.batchid" :notes-type="NotesType.BATCHNOTE"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="editionNotes" :batch="this.batchid" :notes-type="NotesType.EDITIONNOTE"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="sectionNotes" :batch="this.batchid" :notes-type="NotesType.SECTIONNOTE"></notes-form>
      </b-col>
      <b-col>
        <notes-form :postsTitel="pageNotes" :batch="this.batchid" :notes-type="NotesType.PAGENOTE"></notes-form>
      </b-col>
    </b-row>
    <b-row>
      <b-col sm="10">
        <im-carousel :carouselVal="frontPages"></im-carousel>
        <!--      <im-pdf-viewer :pdf-val="frontPages" :checkbox-text="checkboxText"></im-pdf-viewer>-->
      </b-col>
      <b-col sm="2">
        <br>
        <b-button :variant="newspaper.checked ? 'success':'primary'" class="approveNewspaperBtn"
                  @click="approveNewspaper()">Approve newspaper
        </b-button>
        <br>
        <br>
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
    }
  },
  expose: ["pdf"],

  setup() {
    const urlParams = useRoute().params;
    return {urlParams};
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
      batchid: this.$route.params.batchid,
      newspaper: {},
      errorMessage: ""
    }
  },
  components: {
    NotesForm,
    PageTable,
  },
  created() {
    this.fetchCarouselData();
    this.fetchNewspaper();
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
    async fetchNewspaper() {
      const urlParams = useRoute().params;
      const {data} = await axios.get(`/api/batches/${urlParams.batchid}/newspapers/${urlParams.newspaperid}`).catch((err) => {
        console.error(err);
        this.errorMessage = "Unable to load newspaper data";
      });
      this.newspaper = data;
    },
    hideDialog() {
      this.dialogVisible = true;
    },
    getFrontPages() {
      return this.frontPages
    },
    async approveNewspaper() {
      if (!this.newspaper.checked && confirm("Do you want to approve newspaper?")) {
        this.newspaper.checked = true;
        axios.put(`/api/batches/${this.newspaper.batch_id}/newspapers/${this.newspaper.id}`).catch(err => {
          this.errorMessage = "Error approving newspaper";
          console.log(err)
        });
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