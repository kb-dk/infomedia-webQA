<template>

  <div v-if="batchMetadata.showBatch.value" class="batchMetadata" :class="{'is-active': batchMetadata.showBatch}" @click.stop>
    <h2 v-text="batchMetadata.currentDay.value" @click.stop></h2>
    <ApproveButton :stateChange="'technicalQAapproved'" :btnText="'Approve Batch'" @click.stop="changeState"></ApproveButton>
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
    <NotesForm @click.stop :batch="batchMetadata.batch" :notesType="batchMetadata.notesType"></NotesForm>
  </div>
</template>

<script>
import {defineComponent, ref,defineExpose, onMounted,onBeforeUnmount} from "vue";
import ApproveButton from "@/components/ApproveButton";
import ErrorList from "@/components/ErrorList"
import NotesForm from "@/components/NotesForm";
import axios from "axios";
import {NotesType} from "@/enums/NotesType"

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
      problemsLoading:ref(true),
    }
  },
  setup(){
    const batchMetadata={
      currentDay: ref("null"),
      showBatch: ref(false),
      batch:ref({}),
      notesType:NotesType.BATCHNOTE,
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
    changeState(){
      this.batchMetadata.batch.state = "TechnicalInspectionComplete"
      axios.put("/api/batches",this.batchMetadata.batch)
    }
  }


});

</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>