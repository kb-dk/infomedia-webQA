<template>
  <div class="home">
    <h1 v-text="newspaper"></h1>
    <Calendar :isYear="true" :rows="3" :columns="4" :monthNav="null" :monthMask="{title:'MMMM'}" :expanded="false">
    </Calendar>

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

  }

});



// const attributes = ref(result)
// import "src/style/stylesheet.scss";
</script>