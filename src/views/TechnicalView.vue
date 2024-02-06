<template>
  <div class="home" id="technicalDiv">
<!--    <h1 v-text="newspaper"></h1>-->
    <Calendar :isYear="false" :rows="1" :columns="1" :monthNav="'click'" :monthMask="{title:'MMMM YYYY'}"
              :expanded="true" ref="calendarRef">
    </Calendar>
    <BatchMetadata ref="batchMetadataRef">

    </BatchMetadata>
  </div>
</template>

<script lang="ts">
import { defineComponent,ref,onBeforeUnmount, onMounted } from 'vue';
import Calendar from '@/components/Calendar.vue'// @ is an alias to /src
import BatchMetadata from "@/components/BatchMetadata.vue";
// import {onBeforeUnmount, onMounted} from "vue";

export default defineComponent({
  name: 'CalendarView',
  watch:{
    '$route'(to,from){
      this.newspaper = to.params.newspaperid
    }
  },
  components: {
    Calendar,
    BatchMetadata
  },
  data(){
    return{
      newspaper: this.$route.params.newspaperid
    }

  },
  methods:{
    showBatchInfo(event: any){
      // this.batchMetadata.active.value = true;
      (this.$refs.batchMetadataRef as typeof BatchMetadata).showBatchData(event)
    },
    closeBatchInfo(){
      (this.$refs.batchMetadataRef as typeof BatchMetadata).close()
    }

  },
  // mounted() {
  //   document.addEventListener('click',(this.$refs.batchMetadataRef as typeof BatchMetadata).close())
  // },
  // unmounted() {
  //   document.removeEventListener('click',(this.$refs.batchMetadataRef as typeof BatchMetadata).close())
  // }


});



// const attributes = ref(result)
// import "src/style/stylesheet.scss";
</script>