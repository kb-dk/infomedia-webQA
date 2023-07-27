<template>
  <div class="dialog" v-if="show" @click.stop="hideDialog">
    <div @click.stop class="dialog__content">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
 name: 'im-dialog',
  props: {
    show: {
      type: Boolean,
      default: false
    }
  },
   methods: {
     hideDialog() {
       this.$emit('update:show', false)
     }
   }
})
</script>

<style scoped>
.dialog {
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  background: rgba(0,0,0,0.5);
  position: fixed;
  display: flex;
}
.dialog__content {
  margin: auto;
  background: white;
  border-radius: 12px;
  min-height: 58px;
  min-width: 388px;
  padding: 20px;
}

</style>