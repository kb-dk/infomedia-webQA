<template>
  <Carousel v-model="currentSlide" :items-to-show="itemsToShow" :wrap-around="frontPageView" class="custom__carousel">
    <Slide v-for="(item, index) in carouselValHandled" :key="index" class="carousel__slide" ref="slide">
      <div class="carousel__item">

          <template v-if="isLoading">
            <div class="pdf-container">Loading...</div>
          </template>
          <template v-else>
            <b-row>
              <b-col>
                <div class="pdf-container">
                <vue-pdf-embed :source="item ? getImage(item) : null"  @rendered="handleDocumentRender" :page="page"
                               width="550" ></vue-pdf-embed>
                </div>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <div data-bs-toggle="modal" data-bs-target="#pdfModal"
                     @click="openPdfModal(item)">
                  <span class="icon" @click="openPdfModal(item)">
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
  <div class="modal fade" id="pdfModal" tabindex="-1" aria-labelledby="pdfModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title text-dark" id="pdfModalLabel">PDF - {{ selectedPdf }}</h5>
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
// Importer Font Awesome
import '@fortawesome/fontawesome-free/css/all.css';
import axios from "axios";

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
      default: () => [],
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
        if (this.carouselValHandled !== this.carouselValue && this.carouselVal.length > 0) {
          this.carouselValHandled = this.carouselVal;
          console.log("carousel value: " + this.carouselValHandled.value);
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
    handleDocumentRender(args) {
      this.isLoading = false;
    },
    async loadImages() {
      try {
        const apiClient = axios.create({
          baseURL: '/kuana-ndb-api',
        })
        for (const item of this.carouselValHandled) {
          if (!this.imageUrls.has(item)) {
            const encoded_item = encodeURIComponent(item);
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
  },
  mounted() {
    defineEmits(this, ['currentFilenameEvent']);
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
  height: 85vh;
  width: 100%;
  background-color: #6c757d;
  color: var(--vc-clr-white);
  font-size: 20px;
  border-radius: 8px;
  justify-content: center;
  align-items: center;
}

.carousel__slide {
  padding: 5px;
}

.vue-pdf-embed canvas {
  height: calc(100% - 20px) !important; /* adjust the 20px value to match the carousel's padding or margin */
  flex: 1;
  width: 28em;
}

.modal-content {
  height: 80%;
  width: 80%;
}

.pdf-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-top: 20px; /* Adjust the value as needed */
}

.pdf-container vue-pdf-embed {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.icon {
  cursor: pointer;
  /* Tilpas størrelsen og farven efter behov */
  font-size: 20px;
  color: #fff;
}
</style>