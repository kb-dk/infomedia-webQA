<template>

  <div>
    <b-form-input v-model="filter" placeholder="Search"></b-form-input>
    <b-table v-model:sort-by="sortBy"
             v-model:sort-desc="sortDesc"
             striped hover
             :items="items"
             :fields="fields"
             @row-clicked="rowClicked($event)"
             :filter="filter"
             filter-included-fields="newspaperId"
    filter-debounce="1000">
    </b-table>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";

export default defineComponent({
  name: "NewspaperTable",
  data() {
    return {
      filter:'',
      sortBy:'newspaperId',
      sortDesc:true,

      fields: [{
        key: 'newspaperId',
        sortable:true,
        label:'Newspaper',
        // sortDesc:true,
      }

      ],
      items: [
        { newspaperId:"Aarhusstiftidende"},
        { newspaperId:"BT"},
        { newspaperId:"Berlingsketidende"},
        { newspaperId:"Arbejderen"},
        { newspaperId:"Ekstrabladet"},
        { newspaperId:"Information"},
        { newspaperId:"Politiken"},

      ]
    }
  },
  methods:{
    rowClicked(event){
      // console.log(event)
      this.$router.push({name:"newspaper-calendar",params:{newspaperid:event.newspaperId}})
    },
    filterF(row,filter){
      console.log(row)
      console.log(filter)
      return true
    }
  }
})
</script>

<style scoped>

</style>