<template>
  <div>
    <output :value="postsTitel" class="output"></output>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'im-output',
  props: {
    postsTitel: [String, Number]
  }
})
</script>

<style scoped>
.output {

}
</style>