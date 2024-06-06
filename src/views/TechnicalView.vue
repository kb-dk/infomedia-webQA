<template>
  <ApproveButton stateSource="BatchInspected" stateDest="TechnicalInspectionComplete" @click="sendApprovedBatches()" btn-text="Send approved batches"></ApproveButton>
  <b-form-group label="Newspaper types"  v-slot="{ ariaDescribedby }">
    <b-form-radio-group
        v-model="batchType"
        :options="radioBtnOptions"
        :aria-describedby="ariaDescribedby"
        name="radios-stacked"
        stacked
    ></b-form-radio-group>
  </b-form-group>
  <div class="home" id="technicalDiv">
<!--    <h1 v-text="newspaper"></h1>-->

    <Calendar :isYear="false" :rows="1" :columns="1" :monthNav="'click'" :monthMask="{title:'MMMM YYYY'}"
              :batchType="batchType"
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
import ApproveButton from "@/components/ApproveButton.vue";
import axios from "axios";
export default defineComponent({
  name: 'CalendarView',
  watch:{
    '$route'(to,from){
      this.newspaper = to.params.newspaperid;
    }
  },
  components: {
    ApproveButton,
    Calendar,
    BatchMetadata
  },
  data(){
    return{
      newspaper: this.$route.params.newspaperid,
      batchType: ref("dagsaviser"),
      radioBtnOptions:[
          {text:'Dagsaviser',value:'dagsaviser'},
          {text:'Ugeaviser',value:'ugeaviser'},
          {text:'Magasiner',value:'magasiner'}
      ]
    }

  },
  methods:{
    showBatchInfo(event: any){
      // this.batchMetadata.active.value = true;
      (this.$refs.batchMetadataRef as typeof BatchMetadata).showBatchData(event);
    },
    reloadCalendar(){
        this.$forceUpdate();
        (this.$refs.calendarRef as typeof Calendar).batchesForMonth();
    },
    async sendApprovedBatches(){
      const currentDate = (this.$refs.calendarRef as typeof Calendar).date;
      
      const {data} = await axios.get(`/api/batches?month=${currentDate.getMonth()+1}&year=${currentDate.getFullYear()}&state=BatchInspected`);
      for(let i = 0; i < data.length; i++){
        data[i].state = 'TechnicalInspectionComplete'
        await axios({
          method: "PUT",
          url: `/api/batches/`,
          data: data[i],
          headers: {'Content-Type': 'application/json'}
        })
        this.reloadCalendar();
      }
    },
    closeBatchInfo(){
      (this.$refs.batchMetadataRef as typeof BatchMetadata).close();
    }

  },

});
</script>