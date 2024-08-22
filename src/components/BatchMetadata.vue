<template>

  <div v-if="batchMetadata.showBatch.value" class="batchMetadata" :class="{'is-active': batchMetadata.showBatch}" @click.stop>
    <h2 v-text="batchMetadata.currentDay.toLocaleDateString()" @click.stop></h2>
    <ApproveButton stateSource="QAChecked" stateDest="BatchInspected" :batch="batchMetadata.batch" :stateChange="'technicalQAapproved'" :btnText="'Approve Batch'"></ApproveButton>
    <div id="errorListDiv">
    <ErrorList :problemsLoading="problemsLoading" :date="batchMetadata.currentDay" :batch="batchMetadata.batch" ref="errorList"></ErrorList>
    </div>
    <RoundtripDropdown @changeBatch="changeBatch" :date="batchMetadata.currentDay">

    </RoundtripDropdown>

  </div>
  <div @click.stop v-if="batchMetadata.showBatch.value" class="batchMetadata batchMetadataNotes">
    <NotesForm @click.stop :batch="batchMetadata.batch" :notesType="batchMetadata.notesType"></NotesForm>
  </div>
</template>

<script>
import {defineComponent, ref, onMounted,onBeforeUnmount} from "vue";
import ApproveButton from "@/components/ApproveButton";
import ErrorList from "@/components/ErrorList"
import NotesForm from "@/components/NotesForm";
import {NotesType} from "@/enums/NotesType"
import RoundtripDropdown from "@/components/RoundtripDropdown";

export default defineComponent({
  name: "BatchMetadata",
  components:{
    ApproveButton,
    ErrorList,
    NotesForm,
    RoundtripDropdown
  },
  data(){
    return{
      dropdownText:ref("rt4"),
      problemsLoading:ref(true),
    }
  },
  setup(){
    const batchMetadata={
      currentDay: new Date(),
      showBatch: ref(false),
      batch:ref({}),
      notesType:NotesType.BATCHNOTE,
      close:()=>{
        batchMetadata.showBatch.value=false;
      }
    }
    onBeforeUnmount(()=>{
      document.removeEventListener('click',batchMetadata.close);
    })
    onMounted(()=>{
      document.addEventListener('click',batchMetadata.close);
    })
    return {batchMetadata};

  },
  methods:{
    showBatchData(event){
      this.batchMetadata.showBatch.value = true;
      this.batchMetadata.currentDay = new Date(event.id);
      if(event.attributes.length > 0){
        this.batchMetadata.batch = event.attributes[0].batch;
      }else{
        this.batchMetadata.batch = {};
      }

    },
    changeState(batchName,newState){
      this.$forceUpdate();
      (this.$parent).reloadBatch(batchName,newState);
    },
    changeBatch(newBatch){
      this.batchMetadata.batch = newBatch;
      this.$forceUpdate();
    }
  }


});

</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>