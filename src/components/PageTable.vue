<template>
  <div>
    <div style="display: inline-block; width: 50%">
      <p style=" display: inline-block; font-weight: bold;">{{fields[0]}}</p>
    </div>
    <div style="display: inline-block; width: 50%">
      <p style="  font-weight: bold;">{{fields[1]}}</p>
    </div>

  </div>
  <div class="pageTable">
    <b-table-simple responsive="md">
      <b-tbody >
        <b-tr :key="page.index" v-for="(page, index) in pages" @click="rowClicked(page)" :class="focusedPage === index ? 'focusedPageRow' : ''">
          <b-td>{{ page.Page }} af {{ amountOfPagesInSection(pages, page.Section) }}</b-td>
          <b-td>{{ page.Section }}</b-td>
        </b-tr>
      </b-tbody>
    </b-table-simple>
  </div>
  <p style="margin-top: 10px;font-weight: bold">Total Pages: {{pages.length}}</p>
  <p style="font-weight: bold">Total Sections: {{sections.size}}</p>
</template>

<script>
import {defineComponent,ref} from 'vue'

export default defineComponent({
  name: "PageTable",
  props: {
    rowClick: Function,
    pagesFileName: Array
  },
  data() {
    return {
      fields: ["Page", "Section"],
      pages: [],
      sections:new Set(),
      focusedPage:ref(0)
    }
  },
  watch:{
    pagesFileName: {
      immediate: true,
      handler(){
        this.pages = [];
        this.sections = new Set();
        for (let i = 0; i < this.pagesFileName.length; i++) {
          let section= this.getSectionName(this.pagesFileName[i])
          let page= this.getPageNumber(this.pagesFileName[i])
          const fileObject = {
            src: this.pagesFileName[i],
            Section: section[1],
            Page: page[1],
            sectionName: section[0],
            pageName: page[0],
            index: i,
          };
          this.pages.push(fileObject);
          this.sections.add(section[1]);
        }
        this.pages = this.sortBySectionAndPage(this.pages);
      }
    }
  },
  methods: {
    rowClicked(e) {
      const clickedIndex = this.pages.findIndex(page => page.src === e.src);
      if (clickedIndex !== -1) {
        this.focusedPage = clickedIndex;
        this.rowClick(e.src);
      }
    },
    amountOfPagesInSection(pages,section){
      return this.pages.filter((p) => p.Section === section).length;
    },
    getSectionName(filename){
      let regex = /.*(section(\d{2})).*/g;
      let result = regex.exec(filename);
      // console.log(result)
      return [result[1],Number(result[2])];
    },
    getPageNumber(filename){
      let regex = /.*(page(\d{3})).*/g;
      let result = regex.exec(filename);
      return [result[1],Number(result[2])];
    },
    sortBySectionAndPage(arr) {
      for (let i = 0; i < arr.length; i++) {
        for (let j = 0; j < arr.length - i - 1; j++) {
          if (arr[j].Section > arr[j + 1].Section || (arr[j].Section === arr[j + 1].Section && arr[j].Page > arr[j + 1].Page)) {
            const temp = arr[j];
            arr[j] = arr[j + 1];
            arr[j + 1] = temp;
          }
        }
      }
      return arr;
    },
  }
})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>