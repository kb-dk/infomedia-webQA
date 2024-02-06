<template>
  <div>
    <b-table striped hover :items="items" @row-clicked="rowClicked($event)"></b-table>
  </div>
</template>

<script>
import {defineComponent, ref} from "vue";

export default defineComponent({
  name: "NewspaperTable",
  data() {
    return {
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
    }
  }
})
</script>

<style scoped>

</style>