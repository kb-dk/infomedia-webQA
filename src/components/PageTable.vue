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
    rowClick: [Function]
  },
  data() {
    return {
      fields: ["Page", "Section"],
      pages: [
        {"Page": 1, "Section": 1, "src": "20210101_aarhusstiftstidende_section01_page001_ast20210101x11#0001.pdf","index":0},
        {"Page": 2, "Section": 1, "src": "20210101_aarhusstiftstidende_section01_page002_ast20210101x11#0002.pdf","index":1},
        {"Page": 3, "Section": 1, "src": "20210101_aarhusstiftstidende_section01_page003_ast20210101x11#0003.pdf","index":2},
        {"Page": 1, "Section": 4, "src": "20210101_aarhusstiftstidende_section04_page001_ast20210101x14#0001.pdf","index":3},
        {"Page": 2, "Section": 4, "src": "20210101_aarhusstiftstidende_section04_page002_ast20210101x14#0002.pdf","index":4}
      ],
      focusedPage:ref(0)
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
    }
  }
})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>