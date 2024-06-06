<template>

  <div>
    <b-form-input v-model="filter" type="search" placeholder="Search"></b-form-input>
    <b-table v-model:sort-by="sortBy"
             v-model:sort-desc="sortDesc"
             :filterable="true"
             striped hover
             :items="items"
             :fields="fields"
             @row-clicked="rowClicked($event)"
              :filter="filter"

    @filtered="filterTest">
    </b-table>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";
export default defineComponent({
  name: "NewspaperTable",
  data() {
    return {
      filter:null,
      sortBy:'newspaperName',
      sortDesc:false,

      fields: [{
        key: 'newspaperName',
        sortable:true,
        label:'Newspaper',
        filterable:true
        // sortDesc:true,
      }

      ],
      // axios.get("/api/")
      items: [
        { newspaperName:"Aarhusstiftstidende"},
        { newspaperName:"BT"},
        { newspaperName:"Berlingsketidende"},
        { newspaperName:"Arbejderen"},
        { newspaperName:"Ekstrabladet"},
        { newspaperName:"Information"},
        { newspaperName:"Politiken"},

      ]
    }
  },
  methods:{
    rowClicked(event){
      // console.log(event)
      this.$router.push({name:"newspaper-calendar",params:{newspapername:event.newspaperName}})
    },
    filterF(row,filter){
      console.log(row)
      console.log(filter)
      return true
    },
    filterTest(items){
      console.log(items)
    }
  }
})
</script>

<style scoped>

</style>