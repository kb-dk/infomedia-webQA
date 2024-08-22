<template>
  <div class="home">
    <h1 v-text="newspaper"></h1>
    <Calendar :isYear="true" :rows="3" :columns="4" :monthNav="null" :monthMask="{title:'MMMM'}" :newspaperName="newspaper" :expanded="false">
    </Calendar>

  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import Calendar from '@/components/Calendar.vue'
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
      newspaper: this.$route.params.newspaperName
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
