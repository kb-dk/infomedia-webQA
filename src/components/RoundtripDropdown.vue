<template>
  <div id="roundtripDropdown">
    <b-dropdown :text="'RT'+dropdownText" dropleft >
      <b-dropdown-item  v-for="({roundtrip},index) in roundtrips" :key="roundtrip" @click="changeBatch(roundtrip,index)" :class=" roundtrip === dropdownText ? 'roundtripDropdownActive' : '' ">
        RT{{roundtrip}}
      </b-dropdown-item>
    </b-dropdown>
  </div>
</template>

<script>
import {defineComponent,ref} from "vue";
import axios from "axios";

export default defineComponent({
  name: "RoundtripDropdown",
  props:{
    date:[Date]
  },
  data(){
    return{
      dropdownText:ref(-1),
      roundtrips:[]
    }
  },
  created(){
    this.getRoundtrips();
  },
  methods:{
    async getRoundtrips(){
      if(this.date){
        const {data} = await axios.get(`/kuana-ndb-api/v1/batches?day=${this.date.getDate()}&month=${this.date.getMonth()+1}&year=${this.date.getFullYear()}`);
        this.roundtrips = data;
        this.dropdownText = data[0].roundtrip;
        return data;
      }
    },
    changeBatch(roundtrip,index){
      this.dropdownText = roundtrip;
      this.$emit("changeBatch",this.roundtrips[index]);

    }
  }
})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>