<template>
  <p>Click on newspaper</p>
  <b-button :href="linkToUserGuide" target="_blank" variant="primary">User Guide</b-button>
</template>

<script>
import { defineComponent } from 'vue';
import webQAconfig from "@/assets/configFiles/webQAconfig";
import { newspaperPagesStore } from '@/store/newspaperPages';

export default defineComponent({
  name: 'HomeView',
  data(){
    return{
      linkToUserGuide: webQAconfig.linkToUserGuide,
      newspaperPagesStore: newspaperPagesStore()
    }
  },
  mounted() {
    sessionStorage.setItem("usePreloadedNewspaper", "false");
    this.newspaperPagesStore.clearAll();
  }

});

</script>