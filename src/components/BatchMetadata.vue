<template>

  <div v-if="batchMetadata.showBatch.value" class="batchMetadata" :class="{'is-active': batchMetadata.showBatch}" @click.stop>
    <h2 v-text="batchMetadata.currentDay.value" @click.stop></h2>
    <ApproveButton :stateChange="'technicalQAapproved'" :btnText="'Approve Batch'" @click.stop></ApproveButton>
    <div id="errorListDiv">
    <ErrorList :problemsLoading="problemsLoading" :date="batchMetadata.currentDay.value" :batch="batchMetadata.batch"></ErrorList>
    </div>
    <div id="roundtripDropdown">
      <b-dropdown :text="dropdownText" dropleft >
        <b-dropdown-item  v-for="roundtrip in getRoundtrips()" :key="roundtrip" @click="dropdownText = roundtrip.name" :class=" roundtrip.name === dropdownText ? 'roundtripDropdownActive' : '' ">
          {{roundtrip.name}}
        </b-dropdown-item>
      </b-dropdown>
    </div>

  </div>
  <div @click.stop v-if="batchMetadata.showBatch.value" class="batchMetadata batchMetadataNotes">
    <NotesForm @click.stop></NotesForm>
  </div>
</template>

<script>
import {defineComponent, ref,defineExpose, onMounted,onBeforeUnmount} from "vue";
import ApproveButton from "@/components/ApproveButton";
import ErrorList from "@/components/ErrorList"
import NotesForm from "@/components/NotesForm";
import axios from "axios";

export default defineComponent({
  name: "BatchMetadata",
  components:{
    ApproveButton,
    ErrorList,
    NotesForm
  },
  data(){
    return{
      dropdownText:ref("rt4"),
      problemsLoading:ref(true)
    }
  },
  setup(){
    const batchMetadata={
      currentDay: ref("null"),
      showBatch: ref(false),
      batch:ref({}),

      close:()=>{
        batchMetadata.showBatch.value=false;
      }
    }
    onBeforeUnmount(()=>{
      document.removeEventListener('click',batchMetadata.close)
    })
    onMounted(()=>{
      document.addEventListener('click',batchMetadata.close)
    })
    return {batchMetadata}

  },
  methods:{
    showBatchData(event){
      this.batchMetadata.showBatch.value = true
      this.batchMetadata.currentDay.value = event.id
      if(event.attributes.length > 0){
        this.batchMetadata.batch = event.attributes[0].batch
      }else{
        this.batchMetadata.batch = {}
      }

    },
    getRoundtrips(date =""){
      return[{name:"rt1"},{name:"rt2"},{name:"rt3"},{name:"rt4"}]
    },
    // async getNewspapers(){
    //   const errorList = {newspaperError:[],batchError:[]}
    //   const newspapers = (await axios.get(`/api/batches/${this.batchMetadata.batch.id}/newspapers`)).data
    //   for(let i = 0; i < newspapers.length;i++){
    //     const {data} = await axios.get(`/api/batches/${this.batchMetadata.batch.id}/newspapers/${newspapers[i].id}/problem_count`);
    //     data.newspaperName = newspapers[i].newspaper_name
    //     errorList.newspaperError.push(data);
    //   }
    //   const {data} = await axios.get(`/api/batches/${this.batchMetadata.batch.id}/problems-batch`);
    //   for(let batchError in data){
    //     errorList.batchError.push(batchError)
    //   }
    //   this.problemsLoading=false;
    //   return errorList;
    // },
  }


});

</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>