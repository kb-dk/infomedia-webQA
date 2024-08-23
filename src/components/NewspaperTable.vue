<template>
  <div class="newspaper-table-container">
    <div v-if="show">
      <b-form-input v-model="filter" type="search" placeholder="Search"></b-form-input>
      <b-table v-model:sort-by="sortBy"
               v-model:sort-desc="sortDesc"
               :filterable="true"
               :responsive="true"
               striped hover
               :items="filteredNewspapers"
               :fields="fields"
               @row-clicked="rowClicked($event)"
               :filter="filter"
               class="newspaperTable block-text"
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
import { defineComponent, ref } from "vue";

export default defineComponent({
  name: "NewspaperTable",
  props: {
    newspapers: {
      type: Promise,
      required: true,
    },
    headerName: {
      type: String,
      required: true,
    },
    show: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      filter: null,
      sortBy: "newspaper_name",
      sortDesc: false,
      isBusy: true,
      handledNewspapers: [],
      filteredNewspapers: [],
      fields: [
        {
          key: "newspaper_name",
          sortable: true,
          label: "Newspaper",
          filterable: true,
        },
      ],
      selectedNewspaper: null,
    };
  },
  created() {
    this.handleNewspapers();
    this.selectedNewspaper = sessionStorage.getItem("selectedNewspaper") ?? this.selectedNewspaper;
  },

  mounted() {
    this.selectedNewspaper = sessionStorage.getItem("selectedNewspaper") ?? this.selectedNewspaper;
  },

  watch: {
    filter(newValue) {
      this.filterNewspapers(newValue);
    },
  },
  methods: {
    clearSelectedNewspaper() {
      this.selectedNewspaper = null;
      sessionStorage.removeItem("selectedNewspaper");
    },

    async handleNewspapers() {
      try {
        const response = await this.newspapers;
        this.handledNewspapers = response;
        this.filteredNewspapers = response;
      } catch (error) {
        console.error(error);
      } finally {
        this.isBusy = false;
        this.fields[0].label = this.headerName;
      }
    },

    rowClicked(event) {
      this.selectedNewspaper = event.newspaper_name;
      sessionStorage.setItem("selectedNewspaper", event.newspaper_name);
      this.$router.push({
        name: "newspaper-calendar",
        params: {
          newspaperName: event.newspaper_name,
          newspaperid: event.id,
        },
      });
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
});
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
.selected-newspaper {
  font-weight: bold;
  color: blue;
}
.block-text {
  text-transform: uppercase;
  font-size: small;
  font-weight:normal;
}
</style>