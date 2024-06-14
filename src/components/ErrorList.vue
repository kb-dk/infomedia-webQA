<template>
  <p v-if="loading">LOADING PROBLEMS</p>
  <template v-if="asyncErrors.newspaperPageProblems !== undefined">
    <b-list-group ref="errorList" id="errorList" v-for="(err,index) in asyncErrors.newspaperPageProblems" :key="err">
      <b-list-group>
        <b-list-group-item class="errorTypeList" @click="this.switch(index)">
          {{ err.problemCategory }}
          <b-list-group class="batchInfo"
                        :class="{'batchInfo-is-active': currentIndex===index}">
            <b-list-group-item class="errorMessageList">
              {{ index }}
            </b-list-group-item>
            <b-list-group-item class="newspaperList" v-for="n in err.newspapers" :key="n"
                               @click="goToNewspaper(n)">
              {{ n.newspaperName }} <br/> Contains this problem {{ n.count }} times
            </b-list-group-item>
          </b-list-group>
        </b-list-group-item>
      </b-list-group>

    </b-list-group>
    <hr/>
    <b-list-group v-for="(err,index) in asyncErrors.batchProblems" :key="err">
      <b-list-group>
        <b-list-group-item class="errorTypeList" @click="this.switch(index)">
          {{ err }}
          <b-list-group class="batchInfo"
                        :class="{'batchInfo-is-active': currentIndex===index}">
            <b-list-group-item class="errorMessageList">
              {{err.batchProblem}}
            </b-list-group-item>
          </b-list-group>
        </b-list-group-item>
      </b-list-group>
    <hr/>
    </b-list-group>
    <b-list-group v-for="(err,index) in asyncErrors.newspaperProblems" :key="err">
      <b-list-group>
        <b-list-group-item class="errorTypeList" @click="this.switch(index)">
          {{ Object.keys(err)[0] }}
          <b-list-group v-for="(e) in err" :key="e"  class="batchInfo"
                        :class="{'batchInfo-is-active': currentIndex===index}">
            <b-list-group-item class="errorMessageList">
              <b-list-group v-for="problem in e.newspaperProblems" :key="problem">
                <b-list-group-item>
                  {{problem.problem}}
                </b-list-group-item>
              </b-list-group>
            </b-list-group-item>
          </b-list-group>
        </b-list-group-item>
      </b-list-group>

    </b-list-group>
  </template>

</template>

<script>
import {defineComponent, ref, watch} from "vue";
import axios from "axios";

export default defineComponent({
  name: "ErrorList",
  expose: ["handleErrors"],

  props: {
    date: [Date],
    batch: [Object],
    newspapers: [Array],
    errors: [Promise],
    problemsLoading: [Boolean],
  },
  setup(props) {
    return {
      currentIndex: ref(-1),
      asyncErrors: ref({}),
      loading: true
    }
  },
  created() {
    this.asyncErrors = {}
    if (this.batch.id !== undefined) {
      this.getNewspapers().then((res) => {
        this.asyncErrors = res;
        this.loading = false;
      })
    }

  },
  watch:{
    batch(newVal){
      this.getNewspapers().then((res) => {
        this.asyncErrors = res;
        this.loading = false;
      })
    }
  },
  methods: {
    switch(index) {
      if (this.currentIndex !== index) {
        this.currentIndex = index;
      } else {
        this.currentIndex = -1;
      }
      event.target.scrollIntoView({behavior: 'smooth'});
    },
    goToNewspaper(newspaper) {
      this.$router.push({
        name: "newspaper-view",
        params: {
          batchid: this.batch.id,
          newspaperid: newspaper.id,
          year: this.date.getFullYear(),
          month: this.date.getMonth(),
          day: this.date.getDay()
        }
      })
    },
    async getNewspapers() {
      let errorMap = {};
      errorMap["newspaperPageProblems"] = {};
      errorMap["batchProblems"] = [];
      errorMap["newspaperProblems"] = {};

      const errorList = {newspaperError: [], batchError: []};
      const newspapers = (await axios.get(`/api/batches/${this.batch.id}/newspapers`)).data;
      for (let i = 0; i < newspapers.length; i++) {
        const {data} = await axios.get(`/api/batches/${this.batch.id}/newspapers/${newspapers[i].id}/problem_count`);
        for (let j = 0; j < data.length; j++) {
          let problemSplitted = data[j].problem.split(/=(.*)}/)[1];
          if (errorMap["newspaperPageProblems"][problemSplitted]) {
            errorMap["newspaperPageProblems"][problemSplitted]["newspapers"].push({
              "newspaperName": newspapers[i].newspaper_name,
              "count": data[j].count
            });
          } else {
            errorMap["newspaperPageProblems"][problemSplitted] = {}
            errorMap["newspaperPageProblems"][problemSplitted]["problemCategory"] = data[j].problemCategory;
            errorMap["newspaperPageProblems"][problemSplitted]["newspapers"] = [{
              "newspaperName": newspapers[i].newspaper_name,
              "count": data[j].count
            }];

          }
        }
        data.newspaperName = newspapers[i].newspaper_name;
        errorList.newspaperError.push(data);

        const newspaperProblems = (await axios.get(`/api/batches/${this.batch.id}/newspapers/${newspapers[i].id}/newspaper_problems`)).data;
        for(let j = 0; j < newspaperProblems.length; j++) {
          let category = newspaperProblems[j].category
          errorMap.newspaperProblems = {"newspaperProblemCategory":{}};
          errorMap.newspaperProblems["newspaperProblemCategory"][category] = {newspaperProblems:[]};
          errorMap.newspaperProblems["newspaperProblemCategory"][category]["newspaperProblems"].push({"problem": `${newspaperProblems[j].problem} for ${newspapers[j].newspaper_name}`});
        }


      }
      const {data} = await axios.get(`/api/batches/${this.batch.id}/problems-batch`);
      for (let i = 0;i <data.length;i++) {
        errorMap.batchProblems.push({"batchProblem": data[i].problem, problemCategory: "batchError"});
      }
      return errorMap;
    },

  }

})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>