<template>
  <Carousel v-model="currentSlide" :items-to-show="itemsToShow" :wrap-around="frontPageView" class="custom__carousel">
    <Slide  v-for="(item, index) in carouselValHandled" :key="index" class="carousel__slide" ref="slide">
      <div class="carousel__item">
        <div>
          <template v-if="isLoading"> Loading...</template>
          <template v-else>
            test
            <vue-pdf-embed :source=getImage(item) @rendered="handleDocumentRender" ref="pdfRef"
                           :page="page" width="600"></vue-pdf-embed>
          </template>
        </div>
      </div>
    </Slide>

    <template #addons>
      <Navigation v-if="frontPageView"/>
    </template>
  </Carousel>
</template>

<script>
import {defineComponent, defineEmits, ref} from 'vue'
import VuePdfEmbed from "vue-pdf-embed"
import {Carousel, Navigation, Slide} from 'vue3-carousel'

import 'vue3-carousel/dist/carousel.css'
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
      carouselValHandled:ref([]),
      sectionPages: ref([]),
    }
  },
  watch: {
    carouselVal: {
      immediate: true,
      handler() {
        console.log(this.carouselVal)
        if (this.carouselValHandled !== this.carouselValue && this.carouselVal.length > 0) {
          this.carouselValHandled = this.carouselVal;
          // console.log("carousel value: " + this.carouselValHandled.value);
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
      this.isLoading = false
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
    switchPage(fileName){
      this.carouselValHandled = [fileName];
      this.imageUrls.clear();
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
  height: 83vh;
  width: 100%;
  background-color: #474747;
  color: var(--vc-clr-white);
  font-size: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel__slide {
  padding: 5px;
}

.vue-pdf-embed canvas {
  height: calc(100% - 20px) !important; /* adjust the 20px value to match the carousel's padding or margin */
  flex: 1;
}
</style>