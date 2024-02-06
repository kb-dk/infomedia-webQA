<template>
  <b-button :variant="changeVariant" @click="approveState" v-text="approveButton.btnText">
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
  data(){
    const approveButton={
      state:ref(this.$attrs.stateChange),
      btnText: this.$attrs.btnText,
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