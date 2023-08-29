<template>
  <div class="pdf-header">
    <template v-if="isLoading"> Loading...</template>

    <template v-else>
      <span v-if="showAllPages"> {{ pageCount }} page(s) </span>

      <span v-else>
        <im-button :disabled="page <= 1" @click="removeOnePage" style="padding: 0px 6px">❮</im-button>
          {{ page }} / {{ pageCount }}
        <im-button :disabled="page >= pageCount" @click="addOnePage" style="padding: 0px 6px">❯</im-button>
      </span>
      <im-checkbox :text="checkboxText" v-model="showAllPages" style="float: right"/>
    </template>
  </div>
  <div class="pdf-content">
    <vue-pdf-embed :source="pdf" @rendered="handleDocumentRender" ref="pdfRef" :page="page"></vue-pdf-embed>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import VuePdfEmbed from "vue-pdf-embed"

export default defineComponent({
  name: 'im-pdf-viewer',
  data() {
    return {
      isLoading: true,
      page: 0,
      pageCount: 1,
      showAllPages: true,
      disabled: false
    }
  },
  components: {
    VuePdfEmbed
  },
  props: {
    pdf: [String],
    checkboxText: [String]
  },
  watch: {
    showAllPages() {
      this.page = this.showAllPages ? 0 : 1
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
    }
  },
})
</script>

<style scoped>
.vue-pdf-embed {
  margin-bottom: 8px;
  box-shadow: 0 2px 8px 4px rgba(0, 0, 0, 0.1);
}

.pdf-header {
  padding: 20px;
  box-shadow: 0 2px 8px 4px rgba(0, 0, 0, 0.1);
  background-color: #555;
  color: #ddd;
  margin:10px;
}

.pdf-content {
  padding: 24px 16px;
}

</style>