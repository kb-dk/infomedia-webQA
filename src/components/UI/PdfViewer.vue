<template>
  <div class="pdf-header">
    <template v-if="isLoading"> Loading...</template>

    <template v-else>
      <div v-for="(item, index) in pdfVal" :key="index">
    <div class="vue-pdf-embed">
      <vue-pdf-embed class="pdfEmbed" :source=getImage(item) @rendered="handleDocumentRender" ref="pdfRef" :page="page"></vue-pdf-embed>
    </div>
    </div>
    </template>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import VuePdfEmbed from "vue-pdf-embed"
import axios from "axios";

export default defineComponent({
  name: 'im-pdf-viewer',
  data() {
    return {
      isLoading: true,
      page: 0,
      pageCount: 1,
      showAllPages: true,
      disabled: false,
      imageUrls: { }
    }
  },
  components: {
    VuePdfEmbed
  },
  props: {
    pdfVal: {
      type: Array,
      default: () => [],
    },
    pdf: [String],
    checkboxText: [String]
  },
  watch: {
    showAllPages() {
      this.page = this.showAllPages ? 0 : 1
    },
    pdfVal: {
      immediate: true,
      handler() {
        this.loadImages();
      }
    }
  },
  methods: {
    addOnePage() {
      this.page = this.page + 1;
    },
    removeOnePage() {
      this.page = this.page - 1;
    },
    handleDocumentRender(args: any) {
      console.log(args)
      this.isLoading = false
      this.pageCount = (this.$refs.pdfRef as typeof VuePdfEmbed).pageCount
    },
    updateCheckbox() {
      this.showAllPages = true
    },
    async loadImages() {
      try {
        const apiClient = axios.create({
          baseURL: '/kuana-ndb-api',
        })
        for (const item of this.pdfVal) {
          console.log(encodeURIComponent(item as string)); // Log the URL for the API request
          const encoded_item = encodeURIComponent(item as string);
          try {
            const response = await apiClient.get(`/file/${encoded_item}`, {
              responseType: 'blob'
            });
            const blob = new Blob([response.data], {type: 'application/pdf'});
            const url = URL.createObjectURL(blob);
            this.imageUrls = { ...this.imageUrls, [item as string]: url }; // Use spread operator and index signature
          } catch (error) {
            console.error(error); // Log any errors that occur during the API request
          }
        }
      }catch (error) {
        console.error(error);
      }
      this.isLoading = false
    },
    getImage(item: string) {
      console.log(item);
      return (this.imageUrls as { [key: string]: string })[item];
    }
  },
})
</script>

<style scoped>
.pdf-header {
  padding: 20px;
  box-shadow: 0 2px 8px 4px rgba(0, 0, 0, 0.1);
  background-color: #555;
  color: #ddd;
  margin:10px;
}

.vue-pdf-embed {
  padding: 24px 16px;
}
.pdfEmbed{
  height:100%;
  width:100%;
}

</style>