<template>

  <div  class="newspaperTableDiv">
    <div v-if="show">
    <b-form-input v-model="filter" type="search" placeholder="Search"></b-form-input>
    <b-table v-model:sort-by="sortBy"
             v-model:sort-desc="sortDesc"
             :filterable="true"
             :responsive = "true"
             striped hover
             :items="handledNewspapers"
             :fields="fields"
             @row-clicked="rowClicked($event)"
             :filter="filter"
             class="newspaperTable"
             :busy="isBusy"
             :sticky-header="true"
             >
    </b-table>
    </div>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";
import axios from "axios";

export default defineComponent({
  name: "NewspaperTable",
  props: {
    newspapers: [Promise],
    headerName:[String],
    show:[Boolean]
  },
  data() {
    return {
      filter: null,
      sortBy: 'newspaper_name',
      sortDesc: false,
      isBusy: true,
      fields: [
        {
          key: 'newspaper_name',
          sortable: false,
          label: 'Newspaper',
          filterable: true,
          stickyColumn: true
        },
      ],
      handledNewspapers: []
    }
  },
  created() {
    if (this.newspapers) {
      this.newspapers.then((response) => {
        this.handledNewspapers = response;
      }).finally(() => {
            this.isBusy = false;
            this.fields[0].label = this.headerName;
            this.fields[0].sortable = true;
          }
      )
    }
  },
  methods: {
    rowClicked(event) {
      // console.log(event)
      this.$router.push({name: "newspaper-calendar", params: {newspapername: event.newspaper_name}})
    },
    filterF(row, filter) {
      console.log(row)
      console.log(filter)
      return true
    },
    filterTest(items) {
      console.log(items)
    },
    async getNewspaperNames() {
      const {data} = await axios.get(`/api/newspapers/${'dagsaviser'}`);
      if (data) {
        console.log(data)
        this.isBusy = false;
      }
      return data;
    }
  }
})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>