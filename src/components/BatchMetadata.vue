<template>
  <div v-if="batchMetadata.showBatch.value" class="batchMetadata" :class="{'is-active': batchMetadata.showBatch}" @click.stop>
    <h2 v-text="batchMetadata.currentDay.value" @click.stop></h2>
    <ApproveButton :stateChange="'technicalQAapproved'" :btnText="'Approve Batch'" @click.stop></ApproveButton>
    <PdfErrorList :date="batchMetadata.currentDay.value"></PdfErrorList>
  </div>
  <div @click.stop v-if="batchMetadata.showBatch.value" class="batchMetadata batchMetadataNotes">
    <NotesForm @click.stop></NotesForm>
  </div>
</template>

<script>
import {defineComponent, ref,defineExpose, onMounted,onBeforeUnmount} from "vue";
import ApproveButton from "@/components/ApproveButton";
import PdfErrorList from "@/components/PdfErrorList"
import NotesForm from "@/components/NotesForm";

export default defineComponent({
  name: "BatchMetadata",
  components:{
    ApproveButton,
    PdfErrorList,
    NotesForm
  },

  setup(){
    const batchMetadata={
      currentDay: ref("null"),
      showBatch: ref(false),

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
    },
  }


});

</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>