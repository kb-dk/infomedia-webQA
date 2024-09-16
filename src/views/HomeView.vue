<template>
  <p>Click on newspaper</p>
  <b-button :href="linkToUserGuide" target="_blank" variant="primary">User Guide</b-button>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import webQAconfig from "@/assets/configFiles/webQAconfig";
export default defineComponent({
  name: 'HomeView',
  data(){
    return{
      linkToUserGuide: webQAconfig.linkToUserGuide
    }
  },
  mounted() {
    sessionStorage.setItem("usePreloadedNewspaper", "false");
  }

});

</script>