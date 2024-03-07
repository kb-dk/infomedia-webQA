<template>
  <b-list-group v-for="(err,index) in errors" :key="err">
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

export default defineComponent({
  name: "PdfErrorList",
  expose:["handleErrors"],
  setup() {
    return {
      errors: ref([
            {
              "6.2.4.3": [
                {"ErrorMessage": "DeviceGray shall only be used if a device independent DefaultGray colour space has been set when the DeviceGray colour space is used, or if a PDF/A OutputIntent is present."}
                , {
                  "Newspapers": [
                    "lemvigfolkeblad",
                    "jydskevestkystensoenderborg",
                    "nordjyskestiftstidendeaalborg"
                  ]
                }],
            },
            {
              "6.2.4.3.1": [
                {
                  "ErrorMessage":
                      "All properties specified in XMP form shall use either the predefined schemas defined in the XMP Specification, ISO 19005-1 or this part of ISO 19005, or any extension schemas that comply with 6.6.2.3.2."
                },
                {
                  "Newspapers":
                      [
                        "lemvigfolkeblad",
                        "jydskevestkystensoenderborg",
                        "nordjyskestiftstidendeaalborg"
                      ]
                }]
            }
            ,
            {
              "File name error": [
                {"ErrorMessage": "File name errors"},
                {
                  "Newspapers": [
                    "fynsamtsavissvendborg",
                    "dagbladetstruer",
                    "ekstrabladet"
                  ]
                }
              ]
        }]),
      currentIndex:ref(-1)
    }
  },
  data(){
    return{
      date:this.$attrs.date
    }
  },
  methods:{
    handleErrorsKey(errors){
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
    }
  }

})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>