<template>
  <div class="home">
    <h1 @click="info" v-text="newspaper" content="{newspaper}"></h1>
    <Calendar/>

  </div>
</template>

<script lang="ts">
import { defineComponent,ref } from 'vue';
import HelloWorld from '@/components/HelloWorld.vue';
import Calendar from '@/components/Calendar.vue'// @ is an alias to /src
export default defineComponent({
  name: 'CalendarView',
  watch:{
    '$route'(to,from){
      this.newspaper = to.params.newspaperid
    }
  },
  components: {
    Calendar,
  },
  data(){
    return{
      newspaper: this.$route.params.newspaperid
    }

  },
  methods:{
    info(){
      console.log(this.$route.params.newspaperid);
    }
  }



});



// const attributes = ref(result)
// import "src/style/stylesheet.scss";
</script>