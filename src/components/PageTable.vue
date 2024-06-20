<template>
  <div>
    <div style="display: inline-block; width: 50%">
      <p style=" display: inline-block; font-weight: bold;">{{fields[0]}}</p>
    </div>
    <div style="display: inline-block; width: 50%">
      <p style="  font-weight: bold;">{{fields[1]}}</p>
    </div>

<!--  <p>{{fields[0]}}</p><p>{{fields[1]}}</p>-->
  </div>
  <div class="pageTable">
<!--  <div>-->
    <b-table-simple responsive="md">
<!--      <b-thead sticky-header>-->
<!--        <b-tr>-->
<!--          <b-th sticky-column>{{fields[0]}}</b-th>-->
<!--          <b-th sticky-column>{{fields[1]}}</b-th>-->
<!--        </b-tr>-->
<!--      </b-thead>-->
      <b-tbody >
        <b-tr :key="page" v-for="page in pages"  @click="rowClicked(page)" :class="focusedPage === page.index ? 'focusedPageRow' : '' ">
          <b-td>{{page.Page}} af {{amountOfPagesInSection(pages,page.Section)}}</b-td>
          <b-td>{{ page.Section }}</b-td>
        </b-tr>
      </b-tbody>
<!--      <b-tfoot  sticky-header>-->
<!--        <b-td sticky-column="30em"></b-td>-->
<!--      </b-tfoot>-->
    </b-table-simple>

<!--    <b-table-->
<!--    :items="pages"-->
<!--    :fields="fields"-->
<!--    :sort-by="sortBy"-->
<!--    :sort-desc="sortDesc">-->

<!--    </b-table>-->
  </div>
  <p style="margin-top: 10px;font-weight: bold">Total Pages: {{pages.length}}</p>
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
      sortBy:'Section',
      sortDesc:false,
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
        for (let i = 0; i < this.pagesFileName.length; i++) {
          let section= this.getSectionName(this.pagesFileName[i])
          let page= this.getPageNumber(this.pagesFileName[i])
          let fileObject = {src:this.pagesFileName[i],Section:section[1],Page:page[1],sectionName:section[0],pageName:page[0]}
          this.pages.push(fileObject);
        }
        this.pages = this.sortBySectionAndPage(this.pages);

      }
    }
  },
  methods: {
    rowClicked(e) {
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
    sortBySectionAndPage(arr){
      for(let i=0; i<arr.length; i++){
        for (let j=0; j<arr.length -i - 1; j++){
          if(arr[j].Section > arr[j+1].Section){
            const less = arr[j+1];
            arr[j+1] = arr[j];
            arr[j] = less
          }
          else if(arr[j].Section === arr[j+1].Section && arr[j].Page > arr[j+1].Page){
            const less = arr[j+1];
            arr[j+1] = arr[j];
            arr[j] = less;

          }
          arr[j].index = j;
        }

      }
      return arr;
    }
  }
})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>