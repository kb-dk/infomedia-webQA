<template>
  <Carousel v-model="currentSlide" :items-to-show="itemsToShow" :wrap-around="frontPageView" class="custom__carousel">

    <Slide v-for="(item, index) in carouselValHandled" :key="index" class="carousel__slide" ref="slide">

      <div class="carousel__item">

          <template v-if="isLoading">
            <div class="pdf-container" id="loadingDiv">Loading...</div>
          </template>
          <template v-else>
            <b-row >
              <b-col>
                <div class="pdf-container">
                <vue-pdf-embed :source="item ? getImage(item) : null"  @rendered="handleDocumentRender" :page="page"></vue-pdf-embed>
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

    <template #addons>
      <Navigation v-if="frontPageView"/>
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
          <vue-pdf-embed :source="selectedPdf ? getImage(selectedPdf) : ''"></vue-pdf-embed>
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
import {Carousel, Navigation, Slide} from 'vue3-carousel'

import 'vue3-carousel/dist/carousel.css'
//importing bootstrap 5
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.min.js';
import {Modal} from 'bootstrap'
// Importer Font Awesome
import '@fortawesome/fontawesome-free/css/all.css';
import axios from "axios";
import BootstrapVue3 from "bootstrap-vue-3";

export default defineComponent({
  name: 'im-carousel',
  emits: ['currentFilenameEvent'],
  components: {
    Carousel,
    Slide,
    Navigation,
    VuePdfEmbed
  },
  props: {
    carouselVal: {
      type: Array,
      default: () => [{}],
    },
    frontPageView: Boolean,
    itemsToShow: Number,
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
    }
  },
  watch: {
    carouselVal: {
      immediate: true,
      handler() {
        if (this.carouselValHandled !== this.carouselVal && this.carouselVal.length > 0) {
          this.carouselValHandled = this.carouselVal;
          this.loadImages().then(() => {
            this.isLoading = false;
          }).catch((error) => {
            console.error(error);
            this.isLoading = false;
          });
        }
      }
    },
  },
  methods: {
    handleDocumentRender() {
      this.isLoading = false;
    },
    async loadImages() {
      try {
        const apiClient = axios.create({
          baseURL: '/kuana-ndb-api',
        })
        for (const item of this.carouselValHandled) {
          if (!this.imageUrls.has(item.name)) {
            const encoded_item = encodeURIComponent(item.name);
            try {
              const response = await apiClient.get(`/file/${encoded_item}`, {
                responseType: 'blob'
              });
              const blob = new Blob([response.data], {type: 'application/pdf'});
              const url = URL.createObjectURL(blob);
              this.imageUrls.set(item, url); // Use spread operator and index signature
            } catch (error) {
              console.error(error); // Log any errors that occur during the API request
            }
          }
        }
      } catch (error) {
        console.error(error);
      }
    },
    getImage(item) {
      return this.imageUrls.get(item);
    },
    switchPage(fileName) {
      this.carouselValHandled = [fileName];
      this.imageUrls.clear();
    },
    openPdfModal(fileName) {
      this.selectedPdf = fileName;
    },
    handleKeyPress(event){
      if(event.altKey && event.ctrlKey && event.key === "f"){
        const modal = new Modal(this.$refs.pdfModal)
        modal.show();
        this.openPdfModal(this.carouselValHandled[this.currentSlide])
      }
    },
  },
  mounted() {
    defineEmits(this, ['currentFilenameEvent']);
    document.addEventListener("keyup",this.handleKeyPress)
  },
  unmounted() {
    document.removeEventListener("keyup",this.handleKeyPress)
  },
  updated() {
    const currentFilename = this.carouselVal[this.currentSlide];
    this.$emit('currentFilenameEvent', currentFilename);
  }
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
#loadingDiv{
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

.pdf-container .vue-pdf-embed__page canvas{
  max-width:65vh !important;
  height:74vh !important;
  margin:auto;
  padding-top:5px;
  object-fit:contain;
}

.icon {
  cursor: pointer;
  /* Tilpas størrelsen og farven efter behov */
  font-size: 20px;
  color: #fff;
}
.modal-body{
  padding:0 !important;
}
.modal-header{
  border-bottom: 1px solid black !important;
}
.modal-footer{
  border-top: 1px solid black !important;
}
</style>