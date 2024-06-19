<template>
  <div>
    <b-table-simple >
      <b-thead>
        <b-tr>
          <b-th>{{fields[0]}}</b-th>
          <b-th>{{fields[1]}}</b-th>
        </b-tr>
      </b-thead>
      <b-tbody>
        <b-tr :key="page" v-for="page in pages"  @click="rowClicked(page)" :class="focusedPage === page.index ? 'focusedPageRow' : '' ">
          <b-td>{{page.Page}} af {{amountOfPagesInSection(pages,page.Section)}}</b-td>
          <b-td>{{ page.Section }}</b-td>
        </b-tr>
      </b-tbody>
      <b-tfoot>
        <b-td>Total Pages: {{pages.length}}</b-td>
      </b-tfoot>
    </b-table-simple>
  </div>
</template>

<script>
import {defineComponent,ref} from 'vue'
import NewspaperView from "@/views/NewspaperView";

export default defineComponent({
  name: "PageTable",
  props: {
    rowClick: [Function],
    pagesFileName: [Array]
  },
  data() {
    return {
      fields: ["Page", "Section"],
      pages: [
      ],
      focusedPage:ref(0)
    }
  },
  watch:{
    pagesFileName: {
      immediate: true,
      handler(){
        console.log(this.pagesFileName)
        for (let i = 0; i < this.pagesFileName.length; i++) {
          let fileObject = {src:this.pagesFileName[i],index:i,Section:this.getSectionName(this.pagesFileName[i]),Page:this.getPageNumber(this.pagesFileName[i])}
          console.log(fileObject);
          this.pages.push(fileObject);
        }
      }
    }
  },
  methods: {
    rowClicked(e) {
      console.log(event)
      this.focusedPage = e.index
      this.rowClick(e.src)
    },
    amountOfPagesInSection(pages,section){
      const filtered = pages.filter((p) =>{
        return p.Section === section
      })
      return filtered.length
    },
    getSectionName(filename){
      let regex = /.*section(\d{2}).*/g;

      let result = regex.exec(filename);
      console.log(result)
      return Number(result[1]);
    },
    getPageNumber(filename){
      let regex = /.*page(\d{3}).*/g;
      let result = regex.exec(filename);
      return Number(result[1]);
    }
  }
})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>