<template>
  <b-button id="button" :variant="changeVariant" @click="approveState" v-text="btnText"
            v-b-tooltip.hover :title="title">
  </b-button>

</template>

<script>
import {defineComponent} from "vue";
import axios from "axios";

export default defineComponent({
  name: "ApproveButton",
  props: {
    stateChange: [String],
    btnText: [String],
    batch: [Object],
    stateSource: [String],
    stateDest: [String],
    title: [String]
  },

  methods: {

    async approveState() {
      if (this.batch) {
        if(this.batch.state !== this.stateDest || this.batch.state !== this.stateSource) {
          const changedBatch = this.changeBatchState();
          await axios({
            method: "PUT",
            url: `/kuana-ndb-api/batches/`,
            data: changedBatch,
            headers: {'Content-Type': 'application/json'}
          });
          (this.$parent).changeState(this.batch.batch_name,changedBatch.state)
        }
      }
    },
    changeBatchState() {
      const changedBatch = this.batch;
      if (changedBatch.state === this.stateDest) {
        changedBatch.state = this.stateSource;
      } else {
        changedBatch.state = this.stateDest;
      }
      return changedBatch;
    }
  },
  computed: {
    changeVariant() {
      return this.clicked;
    },
    clicked() {
      if (this.batch) {
        return this.batch.state === 'TechnicalInspectionComplete' ? "success" : this.batch.state === 'BatchInspected' ? "warning" : "secondary"
      }else{
        return "secondary"
      }

    }
  }
})
</script>

<style scoped>
</style>