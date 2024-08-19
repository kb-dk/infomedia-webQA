<template>
  <div class="newspaper-table-container">
    <div v-if="show">
    <b-form-input v-model="filter" type="search" placeholder="Search"></b-form-input>
    <b-table v-model:sort-by="sortBy"
             v-model:sort-desc="sortDesc"
             :sort-compare="sortCompare"
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
      <template #cell(newspaper_name)="row">
        <div :class="{ 'selected-newspaper': row.item.newspaper_name === selectedNewspaper }">
          {{ row.value }}
        </div>
      </template>
    </b-table>
    </div>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";

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
      handledNewspapers: [],
      fields: [
        {
          key: "newspaper_name",
          sortable: false,
          label: "Newspaper",
          filterable: true,
          stickyColumn: true,
        }
      ],
      selectedNewspaper: null,
    }
  },
  created() {
    this.handleNewspapers();
    const storedSelectedNewspaper = localStorage.getItem("selectedNewspaper");
    if (storedSelectedNewspaper) {
      this.selectedNewspaper = storedSelectedNewspaper;
    }
  },
  mounted() {
    const storedSelectedNewspaper = localStorage.getItem("selectedNewspaper");
    if (storedSelectedNewspaper) {
      this.selectedNewspaper = storedSelectedNewspaper;
    }
  },
  watch: {
    filter(newValue) {
      this.filterNewspapers(newValue);
     },
  },
  methods: {
    async handleNewspapers() {
      try {
        const response = await this.newspapers;
        this.handledNewspapers = response;
      } catch (error) {
        console.error(error);
      } finally {
        this.isBusy = false;
        this.fields[0].label = this.headerName;
        this.fields[0].sortable = true;
      }
    },
    rowClicked(event) {
      // console.log(event)
      this.selectedNewspaper = event.newspaper_name;
      localStorage.setItem("selectedNewspaper", event.newspaper_name);
      this.$router.push({
        name: "newspaper-calendar",
        params: {
          newspaperName: event.newspaper_name,
          newspaperid: event.id
        }
      })

    },
    filterF(row, filter) {
      return row.newspaper_name.toLowerCase().includes(filter.toLowerCase());
    },
    filterNewspapers(filter) {
      if (filter) {
        this.filteredNewspapers = this.handledNewspapers.filter((row) =>
            this.filterF(row, filter)
        );
      } else {
        this.filteredNewspapers = this.handledNewspapers;
      }
    },
    sortCompare:function(aRow,bRow,key){
      const a = aRow[key].toLowerCase();
      const b = bRow[key].toLowerCase();
      return a<b?-1:a>b?1:0
    }
  },
})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
.selected-newspaper {
  font-weight: bold;
  color: blue;
}
</style>