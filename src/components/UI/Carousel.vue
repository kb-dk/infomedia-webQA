<template>
  <Carousel ref="carousel" v-model="currentSlide" snapAlign="start">
    <Slide v-for="(item, index) in carouselVal" :key="index" class="carousel__slide">
      <div class="carousel__item">
        <iframe :src=getImage(item) alt="carousel-image"/>
      </div>
    </Slide>

    <template #addons>
      <Navigation />
    </template>
  </Carousel>

  <div>
    <button @click="prev">Prev</button>
      <input type="number" min="0" max="9" v-model="currentSlide" />
    <button @click="next">Next</button>
  </div>
</template>

<script>
import {defineComponent, ref} from 'vue'
import { Carousel, Navigation, Slide } from 'vue3-carousel'

import 'vue3-carousel/dist/carousel.css'
import axios from "axios";

export default defineComponent({
  name: 'im-carousel',
  components: {
    Carousel,
    Slide,
    Navigation,
  },
  props: {
    carouselVal: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      currentSlide: 0,
      imageUrls: {} // Object to store image URLs
  }
  },
  mounted() {
    console.log(this.carouselVal)
    this.loadImages();
  },
  watch: {
  carouselVal: {
    immediate: true,
        handler() {
      this.loadImages();
    }
  }
},
  methods: {
    async loadImages() {
      try {
        const apiClient = axios.create({
          baseURL: '/api',
        })
        for (const item of this.carouselVal) {
          const api_url = apiClient.defaults.baseURL + `/file/${item}`;
          console.log(encodeURIComponent(item)); // Logg URL-en til API-forespørselen
          const encoded_item = encodeURIComponent(item);
          const response = await apiClient.get(`/file/${encoded_item}`, {
            responseType: 'blob'
          });
          const blob = new Blob([response.data], {type: 'application/pdf'});
          const url = URL.createObjectURL(blob);
          this.$set(this.imageUrls, item, url);
        }
      }catch (error) {
          console.error(error);
        }
      },
    getImage(item) {
      return this.imageUrls[item];
    },
    next() {
      this.$refs.carousel.next()
    },
    prev() {
      this.$refs.carousel.prev()
    },
  },
})
</script>

<style>
.carousel__item {
  height: 900px;
  width: 100%;
  background-color:  #42b983;
  color: var(--vc-clr-white);
  font-size: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.carousel__slide {
  padding: 10px;
}
.carousel__pagination {
  width: fit-content
}
.carousel__prev,
.carousel__next {
  box-sizing: content-box;
  border: 5px solid white;
}
</style>