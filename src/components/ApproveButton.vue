<template>
  <b-button :variant="changeVariant" @click="approveState" v-text="btnText">
  </b-button>
</template>

<script>
import {defineComponent, ref} from "vue";

export default defineComponent({
  name: "ApproveButton",
  setup(){
    return{
      clicked:ref(false)
    }
  },
  props:{
    stateChange: [Boolean],
    btnText: [String]
  },
  data(){
    const approveButton={
      state:ref(this.stateChange),
    }
    return {approveButton}
  },
  methods:{
    approveState(){
      this.clicked = true;
    }
  },
  computed:{
    changeVariant(){
      return this.clicked ? "success":"secondary"
    }
  }
})
</script>

<style scoped>

</style>