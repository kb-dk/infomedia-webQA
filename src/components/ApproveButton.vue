<template>
  <b-button :variant="changeVariant" @click="approveState" v-text="btnText">
  </b-button>
</template>

<script>
import {defineComponent, ref} from "vue";
import axios from "axios";

export default defineComponent({
  name: "ApproveButton",
  props: {
    stateChange: [String],
    btnText: [String],
    batch: [Object],
    stateSource: [String],
    stateDest: [String]
  },

  methods: {

    async approveState() {
      if (this.batch) {
        if(this.batch.state !== this.stateDest || this.batch.state !== this.stateSource) {
          const changedBatch = this.changeBatchState();
          await axios({
            method: "PUT",
            url: `/api/batches/`,
            data: changedBatch,
            headers: {'Content-Type': 'application/json'}
          })
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
      return this.clicked ? "success" : "secondary"
    },
    clicked() {
      if (this.batch) {
        return this.batch.state === this.stateDest;
      } else {
        return false;
      }

    }
  }
})
</script>

<style scoped>

</style>