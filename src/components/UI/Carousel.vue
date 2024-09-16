<template>
  <Carousel v-model="currentSlide" :items-to-show="1" :wrap-around="false" class="custom__carousel" ref="carousel"
  @keyup.exact.left="currentSlide>0?currentSlide--:currentSlide"
  @keyup.exact.right="currentSlide<carouselValHandled.length-1?currentSlide++:currentSlide"
  @click="this.$refs.carousel.$el.focus()">

    <Slide v-for="(item, index) in carouselValHandled" :key="index" class="carousel__slide" ref="slide">

      <div class="carousel__item">

        <template v-if="item.loading">
          <div class="pdf-container" id="loadingDiv">Loading...</div>
        </template>
        <template v-else>
          <b-row>
            <b-col>
              <div class="pdf-container">
                <vue-pdf-embed :source="item ? getImage(item.name) : null" @rendered="handleDocumentRender"
                               :page="page"></vue-pdf-embed>
              </div>
            </b-col>
          </b-row>
          <b-row class="zoomImage">
            <b-col>
              <div data-bs-toggle="modal" data-bs-target="#pdfModal"
                   ref="zoomButton"
                   @click="openPdfModal(item)">
                  <span class="icon">
                    <!-- Brug et ikon fra Font Awesome -->
                    <i class="fa-solid fa-magnifying-glass"></i>
                  </span>
              </div>
            </b-col>
          </b-row>
        </template>
      </div>
    </Slide>
<!--    <Navigation></Navigation>-->
<!--    <Pagination />-->
    <template #addons>
      <Navigation v-if="frontPageView"/>
      <Pagination v-if="frontPageView"/>
    </template>
  </Carousel>
  <!-- Modal -->
  <div class="modal fade" id="pdfModal" tabindex="-1" aria-labelledby="pdfModalLabel" aria-hidden="true" ref="pdfModal">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title text-dark" id="pdfModalLabel">PDF - {{ selectedPdf.name }}</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <vue-pdf-embed :source="selectedPdf ? getImage(selectedPdf.name) : ''"></vue-pdf-embed>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-warning" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {defineComponent, defineEmits, ref} from 'vue'
import VuePdfEmbed from 'vue-pdf-embed'
import {Carousel, Navigation, Slide,Pagination} from 'vue3-carousel'

import 'vue3-carousel/dist/carousel.css'
//importing bootstrap 5
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.min.js';
import {Modal} from 'bootstrap'
// Importer Font Awesome
import '@fortawesome/fontawesome-free/css/all.css';
import axios from "axios";
import BootstrapVue3 from "bootstrap-vue-3";
import {newspaperPagesStore} from "@/store/newspaperPages";
import carousel from "bootstrap/js/src/carousel";

export default defineComponent({
  name: 'im-carousel',
  emits: ['currentFilenameEvent'],
  components: {
    Carousel,
    Slide,
    Navigation,
    Pagination,
    VuePdfEmbed
  },
  props: {
    frontPageView: Boolean,
    itemsToShow: Number,
    nextDay: {
      type: Array,
      default: () => [{}],
    },
  },
  data() {
    return {
      isLoading: true,
      currentSlide: 0,
      page: 1,
      imageUrls: new Map(), // Object to store image URLs
      carouselValHandled: ref([]),
      sectionPages: ref([]),
      selectedPdf: '',
      nextDayHandled: ref([]),
      loadedVal: false,
      newspaperStore: newspaperPagesStore(),
    }
  },
  watch: {
    'newspaperStore.newspaperPages': {
      immediate: true,
      handler() {
        if (!this.loadedVal) {
          if (this.newspaperStore.useCached && this.newspaperStore.allLoaded) {
            this.handleCached();
            this.loadedVal = true;
            this.newspaperStore.allLoaded = false;
          } else {
            if (this.newspaperStore.newspaperPages.length > 0) {
              this.handleLoadingValues(this.newspaperStore.newspaperPages);
              this.loadedVal = true;
            }
          }
        }
      }
    },
    'newspaperStore.newspaperPage': {
      handler() {
        if (this.newspaperStore.newspaperPage.length > 0 && this.carouselValHandled !== this.newspaperStore.newspaperPage) {
          this.carouselValHandled = this.newspaperStore.getPage();
          this.loadImages(this.carouselValHandled);
        }
      }
    },
    'newspaperStore.newspaperFrontPages': {
      handler() {
        if (this.newspaperStore.newspaperPages.length > 0) {
          const frontPages = this.newspaperStore.getFrontPages();
          this.handleLoadingValues(frontPages);
          // this.loadImages(this.carouselValHandled);
        }
      }
    },
    'newspaperStore.randomNewspaperPages': {
      handler() {
        if (this.newspaperStore.randomNewspaperPages.length > 0) {
          this.handleLoadingValues(this.newspaperStore.getRandomPages());
        }
      }
    },
    nextDay: {
      handler() {
        if (this.nextDay.length > 0 && this.loadedVal) {
          this.nextDayHandled = this.nextDay;
          this.preLoadNextDay();
        }

      }
    }
  },
  methods: {
    handleDocumentRender() {
      this.isLoading = false;
    },
    async loadImages(imageData, preload = false) {
      const imageUrls = new Map();
      let config = preload ? {responseType: 'blob',fetchOptions:{priority:'high'}}:{responseType: 'blob',fetchOptions:{priority:'low'}}
      try {

        for (const item of imageData) {
          if (item && !this.imageUrls.has(item.name)) {
            const encoded_item = encodeURIComponent(item.name);
            try {
              const apiClient = axios.create({
                baseURL: '/kuana-ndb-api',
              })
              await apiClient.get(`/file/${encoded_item}`, config).then((response) => {

                const blob = new Blob([response.data], {type: 'application/pdf'});
                const url = URL.createObjectURL(blob);
                if (preload) {
                  imageUrls.set(item.name, url);
                } else {
                  item.loading = false;
                  this.imageUrls.set(item.name, url);
                }
              }).catch((error) => {
                console.log(error)
                item.loading = false;
              });
            } catch (error) {
              console.error(error); // Log any errors that occur during the API request
            }

          } else {
            item.loading = false;
          }
        }
        if (preload) {
          return imageUrls;
        }
      } catch (error) {
        console.error(error);
      }
    },
    getImage(item) {
      return this.imageUrls.get(item);
    },
    openPdfModal(fileName) {
      this.selectedPdf = fileName;
    },
    handleKeyPress(event) {
      if (event.altKey && event.ctrlKey && event.key === "f") {
        const modal = new Modal(this.$refs.pdfModal)
        modal.show();
        this.openPdfModal(this.carouselValHandled[this.currentSlide])
      }
    },
    async preLoadNextDay() {
      this.newspaperStore.nextDayNewspaperPages = [];
      this.newspaperStore.nextDayNewspaperPagesBlob.clear();
      for (let i = 0; i < this.nextDayHandled.length; i++) {
        this.nextDayHandled[i].loading = true;
        this.newspaperStore.nextDayNewspaperPages.push(this.nextDayHandled[i])
      }
      this.loadImages(this.nextDayHandled, true).then((res) => {
        for (const [key, value] of res) {
          this.newspaperStore.addNextDayBlob(key, value);
        }
      }).finally(() => {
            this.newspaperStore.allLoaded = true;
          }
      )

    },
    async handleCached() {
      for (const [key, value] of this.newspaperStore.cachedNewspaperPagesBlob) {
        this.imageUrls.set(key, await value)
      }
      for (const page of this.newspaperStore.cachedNewspaperPages) {
        page.loading = false;
        this.carouselValHandled.push(page);
      }
    },
    handleLoadingValues(val) {
      for (let i = 0; i < val.length; i++) {
        this.carouselValHandled[i] = val[i];
        this.carouselValHandled[i].loading = true;
      }
      this.loadImages(this.carouselValHandled)
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
  },
  mounted() {
    defineEmits(this, ['currentFilenameEvent']);
    document.addEventListener("keyup", this.handleKeyPress)
    this.$refs.carousel.$el.focus();
  },
  unmounted() {
    document.removeEventListener("keyup", this.handleKeyPress)
  },
  updated() {
    const currentFilename = this.carouselValHandled[this.currentSlide];
    console.log("currentFilename")
    console.log(currentFilename);
    this.$emit('currentFilenameEvent', currentFilename);
  },
})
</script>

<style>
.custom__carousel {
  margin-top: 5px;
}

.carousel__item {
  height: 79vh;
  width: 100%;
  background-color: #6c757d;
  color: var(--vc-clr-white);
  font-size: 20px;
  border-radius: 8px;
  justify-content: center;
  align-items: center;
}

#loadingDiv {
  position: relative;
  top: 50%;
  transform: translateY(-50%);
}

.carousel__slide {
  padding: 5px;
}

.modal-content {
  height: 80%;
  width: 80%;
}

.pdf-container .vue-pdf-embed__page canvas {
  max-width: 65vh !important;
  height: 74vh !important;
  margin: auto;
  padding-top: 5px;
  object-fit: contain;
}

.icon {
  cursor: pointer;
  /* Tilpas størrelsen og farven efter behov */
  font-size: 20px;
  color: #fff;
}

.modal-body {
  padding: 0 !important;
}

.modal-header {
  border-bottom: 1px solid black !important;
}

.modal-footer {
  border-top: 1px solid black !important;
}
.custom__carousel:focus{
  outline: none ;
}
.carousel__pagination{
  position:absolute;
  top:1%;
  right: 4%;
}

.carousel__pagination-button::after{
  width:2em;
  height: 0.4em;
  background-color: rgba(202, 197, 197, 0.78);
}
.carousel__pagination-button--active::after{
  width:2em;
  height: 0.4em;
  background-color: rgba(46, 46, 46, 0.92);
}
</style>