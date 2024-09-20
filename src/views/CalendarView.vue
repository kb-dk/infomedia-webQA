<template>
  <div class="home">
    <h1 v-text="newspaper" class="block-text"></h1>
    <Calendar :isYear="true" :rows="3" :columns="4" :monthNav="null" :monthMask="{title:'MMMM'}" :newspaperName="newspaper" :expanded="false">
    </Calendar>

  </div>
</template>

<script>
import { defineComponent } from 'vue';
import Calendar from '@/components/Calendar.vue'
import {newspaperPagesStore} from "@/store/newspaperPages";

export default defineComponent({
  name: 'CalendarView',
  watch: {
    '$route'(to) {
      this.newspaper = to.params.newspaperName
     }
  },
  components: {
    Calendar,
  },
    data(){
    return{
      newspaper: this.$route.params.newspaperName,
      newspaperPagesStore: newspaperPagesStore()
    }
  },
  methods:{
  },
  mounted(){
    sessionStorage.setItem("usePreloadedNewspaper", "false");
    this.newspaperPagesStore.clearAll();
  },
});

</script>
<style>
.home {
  align-items: center;
}
.block-text {
  text-transform: uppercase;
  font-weight: bold;
}
</style>
