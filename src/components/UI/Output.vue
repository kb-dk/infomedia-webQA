<template>
  <div>
    <output :value="postsTitle" class="output"></output>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'im-output',
  props: {
    postsTitle: [String, Number]
  }
})
</script>

<style scoped>
.output {

}
</style>