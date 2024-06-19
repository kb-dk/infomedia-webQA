<template>
  <Carousel v-model="currentSlide" :items-to-show="2" :wrap-around="true">
    <Slide v-for="(item, index) in carouselVal" :key="index" class="carousel__slide">
      <div class="carousel__item">
        <div>
          <template v-if="isLoading"> Loading...</template>
          <template v-else>
            <vue-pdf-embed :source=getImage(item) @rendered="handleDocumentRender" ref="pdfRef"
                           :page="page" :height="780" :width="580"></vue-pdf-embed>
          </template>
        </div>
      </div>
    </Slide>

    <template #addons>
      <Navigation/>
    </template>
  </Carousel>
</template>

<script>
import {defineComponent, defineEmits} from 'vue'
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
      default: () => [],
    },
  },
  data() {
    return {
      isLoading: true,
      currentSlide: 0,
      page: 1,
      imageUrls: {} // Object to store image URLs
    }
  },
  watch: {
    carouselVal: {
      immediate: true,
      handler() {
        this.loadImages().then(() => {
          this.isLoading = false;
        }).catch((error) => {
              this.isLoading = false;
            });
      }
    }
  },
  methods: {
    handleDocumentRender(args) {
      this.isLoading = false
    },
    async loadImages() {
      try {
        const apiClient = axios.create({
          baseURL: '/api',
        })
        for (const item of this.carouselVal) {
          console.log(encodeURIComponent(item)); // Log the URL for the API request
          const encoded_item = encodeURIComponent(item);
          try {
            const response = await apiClient.get(`/file/${encoded_item}`, {
              responseType: 'blob'
            });
            const blob = new Blob([response.data], {type: 'application/pdf'});
            const url = URL.createObjectURL(blob);
            this.imageUrls = {...this.imageUrls, [item]: url}; // Use spread operator and index signature
          } catch (error) {
            console.error(error); // Log any errors that occur during the API request
          }
        }
      } catch (error) {
        console.error(error);
      }
    },
    getImage(item) {
      //console.log(item);
      return this.imageUrls[item];
    },
  },
  mounted() {
    defineEmits(this, ['currentFilenameEvent']);
  },
  updated() {
    const currentFilename = this.carouselVal[this.currentSlide];
    this.$emit('currentFilenameEvent', currentFilename);
  },
})
</script>s

<style>
.carousel__item {
  height: 800px;
  width:630px;
  background-color: #42b983;
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

.pdf-content {
  padding: 20px 15px;
  width: 100%;
  height: 100%;
}

</style>