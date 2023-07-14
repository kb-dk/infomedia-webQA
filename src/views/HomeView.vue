<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
    <Calendar/>

  </div>
</template>

<script lang="ts">
import { defineComponent,ref } from 'vue';
import HelloWorld from '@/components/HelloWorld.vue';
import Calendar from '@/components/Calendar.vue'// @ is an alias to /src

export default defineComponent({
  name: 'HomeView',
  emits:["yearChange"],
  components: {
    Calendar,
  },


});



// const attributes = ref(result)

</script>
