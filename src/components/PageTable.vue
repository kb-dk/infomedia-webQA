<template>
  <div>
    <b-table :items="pages" :fields="fields" @row-clicked="rowClicked($event)">

    </b-table>
  </div>
</template>

<script>
import {defineComponent} from 'vue'
import NewspaperView from "@/views/NewspaperView";

export default defineComponent({
  name: "PageTable",
  props:{
    rowClick:[Function]
  },
  data(){
    return{
      fields:["Page","Section"],
      pages:[
        {"Page":"1","Section":"1","src":"20210101_aarhusstiftstidende_section01_page001_ast20210101x11#0001.pdf"},
        {"Page":"2","Section":"1","src":"20210101_aarhusstiftstidende_section01_page002_ast20210101x11#0002.pdf"},
        {"Page":"3","Section":"1","src":"20210101_aarhusstiftstidende_section01_page003_ast20210101x11#0003.pdf"},
        {"Page":"1","Section":"4","src":"20210101_aarhusstiftstidende_section04_page001_ast20210101x14#0001.pdf"},
        {"Page":"2","Section":"4","src":"20210101_aarhusstiftstidende_section04_page002_ast20210101x14#0002.pdf"}
      ]
    }
  },
  methods:{
    rowClicked(e){
      this.rowClick(e.src)
      // .switchPage(e.src)
      // NewspaperView.methods.switchPage(e.src)
    }
  }
})
</script>

<style scoped>

</style>