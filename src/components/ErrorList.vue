<template>
  {{console.log(this.errors)}}}
  <b-list-group v-for="(err,index) in this.errors" :key="err">
    <b-list-group>
      <b-list-group-item class="errorTypeList" @click="this.switch(index)" >
          {{handleErrorsKey(err)}}
        <b-list-group v-for="e in err" :key="e" class="batchInfo"  :class="{'batchInfo-is-active': currentIndex===index}">
          <b-list-group-item class="errorMessageList">
            {{handleErrorsVal(err)[0].ErrorMessage}}
          </b-list-group-item>
          <b-list-group-item class="newspaperList" v-for="n in handleErrorsVal(err)[1].Newspapers" :key="n" @click="goToNewspaper(n)">
              {{n}}
          </b-list-group-item>
        </b-list-group>
      </b-list-group-item>
    </b-list-group>
  </b-list-group>

</template>

<script>
import {defineComponent, ref} from "vue";
import axios from "axios";

export default defineComponent({
  name: "ErrorList",
  expose:["handleErrors"],
  props:{
    date:[String],
    batch:[Object],
    newspapers:[Array],
    errors:[Object]
  },
  setup() {
    return {
      currentIndex:ref(-1)
    }
  },
  methods:{
    handleErrorsKey(errors){
      console.log(errors)
      let key;
      let val;
      for(let i in errors){
        key = i
        val = errors[i]
      }
      return key
    },
    handleErrorsVal(errors){
      let key;
      let val;
      for(let i in errors){
        key = i
        val = errors[i]
      }
      return val
    },
    switch(index){
      if(this.currentIndex !== index) {
        this.currentIndex = index
      }else{
        this.currentIndex = -1
      }
    },
    goToNewspaper(newspaper){
      const year = this.date.slice(0,4)
      const month = this.date.slice(6,7)
      const day = this.date[7,9]
      this.$router.push({name:"newspaper-view",params:{batchid:"dl_20210101_rt1",newspaperid:"Aarhusstiftidende",year:2021,month:1,day:day}})
    },

  }

})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>