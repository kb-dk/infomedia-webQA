<template>
  <div class="home">
    <h1>{{newspaper}}</h1>
    <Calendar/>

  </div>
</template>

<script lang="ts">
import { defineComponent,ref } from 'vue';
import HelloWorld from '@/components/HelloWorld.vue';
import Calendar from '@/components/Calendar.vue'// @ is an alias to /src

export default defineComponent({
  name: 'HomeView',
  components: {
    Calendar,
  },
  data(){
    return{
      newspaper:"Aarhusstiftidende"
    }

  }



});



// const attributes = ref(result)
// import "src/style/stylesheet.scss";
</script>