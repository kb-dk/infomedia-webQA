<template>
  <div class="pdf-header">
    <template v-if="isLoading"> Loading...</template>

    <template v-else>
<!--      <span v-if="showAllPages"> {{ pageCount }} page(s) </span>-->

<!--&lt;!&ndash;      <span v-else>&ndash;&gt;-->
<!--&lt;!&ndash;        <im-button :disabled="page <= 1" @click="removeOnePage" style="padding: 0px 6px">❮</im-button>&ndash;&gt;-->
<!--&lt;!&ndash;          {{ page }} / {{ pageCount }}&ndash;&gt;-->
<!--&lt;!&ndash;        <im-button :disabled="page >= pageCount" @click="addOnePage" style="padding: 0px 6px">❯</im-button>&ndash;&gt;-->
<!--&lt;!&ndash;      </span>&ndash;&gt;-->
      <im-checkbox :text="checkboxText" v-model="showAllPages" style="float: right"/>
    </template>
  </div>
  <div class="pdf-content">
    <VuePdfEmbed :source="pdfSource"></VuePdfEmbed>
<!--    <div v-if="!showAllPages">-->
<!--      <VuePdfEmbed :source="pdfSource"></VuePdfEmbed>-->
<!--    </div>-->
<!--    <div v-if="showAllPages">-->
<!--      <VuePdfEmbed :key="frontpage" v-for="frontpage in filterFrontPages()" :source="encodeURIComponent(frontpage.src)"  :height="500" class="pdfFrontpage"></VuePdfEmbed>-->
<!--    </div>-->
  </div>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue'
import VuePdfEmbed from "vue-pdf-embed"

export default defineComponent({
  name: 'im-pdf-viewer',
  data() {
    return {
      isLoading: false,
      page: ref(1),
      pageCount: 2,
      showAllPages: false,
      disabled: false,
    }
  },
  components: {
    VuePdfEmbed
  },
  props: {
    pdfSource: [String],
  },
  // watch: {
  //   showAllPages() {
  //     this.page = this.showAllPages ? 1 : 1
  //   },
  //   pdfVal:function(newVal,oldVal){
  //     this.showAllPages = false;
  //     console.log(this.pdfSource)
  //     this.pdfSource = newVal
  //
  //   },
  // },
  // methods: {
  //   addOnePage() {
  //     this.page = this.page + 1;
  //   },
  //   removeOnePage() {
  //     this.page = this.page - 1;
  //   },
  //   handleDocumentRender(args: any) {
  //     this.isLoading = false
  //
  //   },
  //   updateCheckbox() {
  //     this.showAllPages = true
  //   },
  //   filterFrontPages(){
  //     console.log(this.allPages)
  //     if(this.allPages){
  //       return this.allPages.filter((page: { Page: number }) => {
  //         return page.Page === 1
  //       })
  //
  //     }
  //     return []
  //
  //   }
  // }
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