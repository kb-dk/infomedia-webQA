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
<!--    <vue-pdf-embed :scale="1" :id="'page'" :key="'page'" v-if="!showAllPages" :source="encodeURIComponent(pdfSource)" @progress="(e)=>{console.log(e)}" @rendered="handleDocumentRender" ref="pdfRef" :page="page"></vue-pdf-embed>-->
<!--    <vue-pdf-embed :id="frontPage.src" v-for="frontPage in filterFrontPages()" :page="frontPage.Page" :key="frontPage.src" :source="encodeURIComponent(frontPage.src)" @rendered="handleDocumentRender" ref="pdfRef"></vue-pdf-embed>-->
<!--      <div  v-for="frontPage in filterFrontPages()" :key="frontPage">-->
<!--        <vue-pdf-embed :page="frontPage.Page" :source="encodeURIComponent(frontPage.src)" @rendered="handleDocumentRender" ref="pdfRef"></vue-pdf-embed>-->
<!--      </div>-->

<!--    <vue-pdf-embed v-if="showAllPages" >-->
<!--      <vue-pdf-embed :source="frontPage.src" v-for="frontPage in filterFrontPages" :key="frontPage.src">-->
<!--      </vue-pdf-embed>-->
<!--    </vue-pdf-embed>-->
    <VuePDF :pdf="pdfSource"></VuePDF>
  </div>
</template>

<script lang="ts">
import {defineComponent, ref, ShallowRef} from 'vue'
// import VuePdfEmbed from "vue-pdf-embed"
import {VuePDF,usePDF} from "@tato30/vue-pdf";

export default defineComponent({
  name: 'im-pdf-viewer',
  data() {
    return {
      isLoading: false,
      page: ref(1),
      pageCount: 2,
      showAllPages: false,
      disabled: false,
      // pdfSource:ref(usePDF(this.pdf).pdf),
      allPages:[
        {"Page":1,"Section":"1","src":"20210101_aarhusstiftstidende_section01_page001_ast20210101x11#0001.pdf"},
        {"Page":2,"Section":"1","src":"20210101_aarhusstiftstidende_section01_page002_ast20210101x11#0002.pdf"},
        {"Page":3,"Section":"1","src":"20210101_aarhusstiftstidende_section01_page003_ast20210101x11#0003.pdf"},
        {"Page":1,"Section":"4","src":"20210101_aarhusstiftstidende_section04_page001_ast20210101x14#0001.pdf"},
        {"Page":2,"Section":"4","src":"20210101_aarhusstiftstidende_section04_page002_ast20210101x14#0002.pdf"}
      ]
    }
  },
  components: {
    VuePDF
  },
  props: {
    pdf: [String],
    checkboxText: [String]
  },
  watch: {
    showAllPages() {
      this.page = this.showAllPages ? 1 : 1
    },
    pdf:function(newVal,oldVal){
      this.showAllPages = false;
    },
  },
  methods: {
    addOnePage() {
      this.page = this.page + 1;
    },
    removeOnePage() {
      this.page = this.page - 1;
    },
    handleDocumentRender(args: any) {
      console.log("rendered")
      this.isLoading = false
      // this.page=1
      // this.pageCount = this.showAllPages ? 1 : 2
    },
    updateCheckbox() {
      this.showAllPages = true
    },
    filterFrontPages(){
      if(this.showAllPages){
        return this.allPages.filter((page) =>{
          return page.Page === 1
        })
      }

    }
  },
  computed:{
    pdfSource():any{
      console.log(usePDF(this.pdf))
      return usePDF(this.pdf).pages
    }
  }
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