<template>
  <p v-if="loading">LOADING PROBLEMS</p>
  <p v-if="errorMessage.length !== 0" style="color:red">{{errorMessage}}</p>
  <template v-if="errorMessage.length === 0 && asyncErrors.newspaperPageProblems !== undefined">
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
              {{ err.batchProblem }}
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
          <b-list-group v-for="(e) in err" :key="e" class="batchInfo"
                        :class="{'batchInfo-is-active': currentIndex===index}">
            <b-list-group-item class="errorMessageList">
              <b-list-group v-for="problem in e.newspaperProblems" :key="problem">
                <b-list-group-item>
                  {{ problem.problem }}
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
      loading: true,
      errorMessage: ref("")
    }
  },
  created() {
    this.asyncErrors = {}
    if (this.batch.id !== undefined) {
      this.getNewspapers().then((res) => {
        this.asyncErrors = res;
        this.loading = false;
      }).catch((err)=>{
        console.log(err)
      })
    }

  },
  watch: {
    batch(newVal) {
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
      const eve = event.target;
      setTimeout(()=>{
        eve.scrollIntoView({behavior: 'smooth'})
      },50);

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
      try {
        const newspapersGet = (await axios.get(`/api/batches/${this.batch.id}/newspapers`).catch((err) => {
          this.errorMessage  = "Unable to load newspapers";
          console.log(err)
        }))
        const newspapers = newspapersGet.data;
        for (let i = 0; i < newspapers.length; i++) {
          const {data} = await axios.get(`/api/batches/${this.batch.id}/newspapers/${newspapers[i].id}/problem-count`).catch((err) => {
            this.errorMessage  = "Unable to load problem count";
            console.log(err);
          });
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

          const newspaperProblemsGet = (await axios.get(`/api/batches/${this.batch.id}/newspapers/${newspapers[i].id}/newspaper-problems`).catch(err=>{
            this.errorMessage  = "Unable to load newspaper problems";
            console.log(err);
          }));

          const newspaperProblems = newspaperProblemsGet.data;
          for (let j = 0; j < newspaperProblems.length; j++) {
            let category = newspaperProblems[j].category
            errorMap.newspaperProblems = {"newspaperProblemCategory": {}};
            errorMap.newspaperProblems["newspaperProblemCategory"][category] = {newspaperProblems: []};
            errorMap.newspaperProblems["newspaperProblemCategory"][category]["newspaperProblems"].push({"problem": `${newspaperProblems[j].problem} for ${newspapers[j].newspaper_name}`});
          }


        }
        const {data} = await axios.get(`/api/batches/${this.batch.id}/problems-batch`).catch((err) => {
          this.errorMessage  = "Unable to load problem for batch";
          console.log(err);
        });
        for (let i = 0; i < data.length; i++) {
          errorMap.batchProblems.push({"batchProblem": data[i].problem, problemCategory: "batchError"});
        }
        return errorMap;
      } catch (error) {
        console.log(error);
        if(this.errorMessage.length === 0){
          this.errorMessage = "Unable to load problems"
        }

      }
    }


  }

})
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>