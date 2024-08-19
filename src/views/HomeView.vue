<template>
  <p>Click on newspaper</p>
  <b-button :href="linkToUserGuide" target="_blank" variant="primary">User Guide</b-button>
</template>

<script lang="ts">
import { defineComponent,ref } from 'vue';
import webQAconfig from "@/assets/configFiles/webQAconfig";
export default defineComponent({
  name: 'HomeView',
  data(){
    return{
      linkToUserGuide: webQAconfig.linkToUserGuide
    }
  }

});



// const attributes = ref(result)
// import "src/style/stylesheet.scss";
</script>