<template>
  <div class="home">
    <h1 v-text="newspaper"></h1>
    <Calendar :isYear="true" :rows="3" :columns="4" :monthNav="null" :monthMask="{title:'MMMM'}" :newspaperName="newspaper"
              :expanded="false" :batch-type="batchtype">
    </Calendar>

  </div>
</template>

<script lang="ts">
import { defineComponent,ref } from 'vue';
import Calendar from '@/components/Calendar.vue'
import {useRoute} from "vue-router";
import axios from "axios";
// @ is an alias to /src
export default defineComponent({
  name: 'CalendarView',
  watch:{
    '$route'(to,from){
      this.newspaper = to.params.newspaperName
    }
  },
  components: {
    Calendar,
  },
    data(){
    return{
      newspaper: this.$route.params.newspaperName,
      batchtype: "dagsaviser"
    }
  },
  methods:{
  }
});

</script>
<style>
.home {
  align-items: center;
}
</style>
