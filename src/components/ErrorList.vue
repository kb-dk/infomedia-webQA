<template>
  <p v-if="loading">LOADING PROBLEMS</p>
  <template v-if="asyncErrors.newspaperProblems !== undefined">
    <b-list-group ref="errorList" id="errorList" v-for="(err,index) in asyncErrors.newspaperProblems" :key="err">
      <b-list-group>
        <b-list-group-item class="errorTypeList" @click="this.switch(index)">
          {{err.problemCategory}}
          <b-list-group class="batchInfo"
                        :class="{'batchInfo-is-active': currentIndex===index}">
            <b-list-group-item class="errorMessageList">
              {{ index }}
            </b-list-group-item>
            <b-list-group-item class="newspaperList" v-for="n in err.newspapers" :key="n"
                               @click="goToNewspaper(n)">
              {{ n.newspaperName }} <br/> Contains this problem {{n.count}} times
            </b-list-group-item>
          </b-list-group>
        </b-list-group-item>
      </b-list-group>

    </b-list-group>
    <b-list-group v-for="(err,index) in asyncErrors.batchProblems" :key="err">
      <b-list-group>
        <b-list-group-item class="errorTypeList" @click="this.switch(index)">
          {{err.problemCategory}}
          <b-list-group class="batchInfo"
                        :class="{'batchInfo-is-active': currentIndex===index}">
            <b-list-group-item class="errorMessageList">
              {{ index }}
            </b-list-group-item>
          </b-list-group>
        </b-list-group-item>
      </b-list-group>

    </b-list-group>
  </template>

</template>

<script>
import {defineComponent, ref} from "vue";
import axios from "axios";

export default defineComponent({
  name: "ErrorList",
  expose: ["handleErrors"],
  props: {
    date: [String],
    batch: [Object],
    newspapers: [Array],
    errors: [Promise],
    problemsLoading: [Boolean],
  },
  setup() {
    return {
      currentIndex: ref(-1),
      asyncErrors: ref({}),
      loading: true
    }
  },
  created() {
    this.asyncErrors = {}
    if(this.batch.id !== undefined){
      this.getNewspapers().then((res) => {
        this.asyncErrors = res;
        this.loading = false;
      })
    }

  },
  methods: {
    switch(index) {
      if (this.currentIndex !== index) {
        this.currentIndex = index
      } else {
        this.currentIndex = -1
      }
      console.log(this.$el)
      // this.$refs.errorList.scrollIntoView({behavior:'smooth'})
      // window.scrollTo(event.target)
      console.log(this.$refs.errorList)
      event.target.scrollIntoView({behavior:'smooth'})
    },
    goToNewspaper(newspaper) {
      const year = this.date.slice(0, 4)
      const month = this.date.slice(6, 7)
      const day = this.date[7, 9]
      this.$router.push({
        name: "newspaper-view",
        params: {batchid: "dl_20210101_rt1", newspaperid: "Aarhusstiftidende", year: 2021, month: 1, day: day}
      })
    },
    async getNewspapers() {
      // const errorMap = {"newspaperProblems":{"problemDescription":"","ruleId":"","newspapers":[{}]}}
      let errorMap = {}
      errorMap["newspaperProblems"] = {}
      errorMap["batchProblems"] = []

      const errorList = {newspaperError: [], batchError: []}
      const newspapers = (await axios.get(`/api/batches/${this.batch.id}/newspapers`)).data
      for (let i = 0; i < newspapers.length; i++) {
        const {data} = await axios.get(`/api/batches/${this.batch.id}/newspapers/${newspapers[i].id}/problem_count`);
        for (let j = 0; j < data.length; j++) {
          let problemSplitted = data[j].problem.split(/=(.*)}/)[1]
          if (errorMap["newspaperProblems"][problemSplitted]) {
            errorMap["newspaperProblems"][problemSplitted]["newspapers"].push({
              "newspaperName": newspapers[i].newspaper_name,
              "count": data[j].count
            })
          } else {
            errorMap["newspaperProblems"][problemSplitted] = {}
            errorMap["newspaperProblems"][problemSplitted]["problemCategory"] = data[j].problemCategory;
            errorMap["newspaperProblems"][problemSplitted]["newspapers"] = [{
              "newspaperName": newspapers[i].newspaper_name,
              "count": data[j].count
            }]

          }
        }
        data.newspaperName = newspapers[i].newspaper_name
        errorList.newspaperError.push(data);
      }
      const {data} = await axios.get(`/api/batches/${this.batch.id}/problems-batch`);
      for (let batchError in data) {
        errorMap.batchProblems.push({"batchProblem":batchError,problemCategory:"batchError"})
      }
      return errorMap;
    },

  }

})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>