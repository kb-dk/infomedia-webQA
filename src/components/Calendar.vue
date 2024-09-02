<template>
  <div class="calendar">
    <p v-if="errorMessage.length > 0" style="color:red">{{ errorMessage }}</p>
    <YearPicker v-if="isYear" :inputStyle="yearPickerInputStyle" ref="yearPicker" inputId="yearPickerInput"
                @date-select="yearSelect" v-model="date" view="year" dateFormat="yy" class="yearPicker">
    </YearPicker>
    <Calendar locale="da"
              :initialPage="{day: isYear ? 1: date.getDay(),month: isYear ? 1 : date.getMonth()+1,year:selectedYear}"
              :masks="monthMask" :rows="rows" ref="yearCalendar" class="yearCalendar"
              :columns="columns" :attributes="calendarAttr" :first-day-of-week="2" :nav-visibility="monthNav"
              expanded
              @did-move="next($event)"
              @dayclick="calendarDayClicked"
              @update:pages.once="isYear ? batchesForYear() : batchesForMonth()"
    />
  </div>

</template>

<script>
import {defineComponent, ref} from "vue";
import {Calendar} from 'v-calendar';
import YearPicker from 'primevue/calendar'
import axios from "axios";
import "primevue/resources/themes/lara-light-indigo/theme.css";
import 'v-calendar/style.css'

export default defineComponent({
  name: "year-calendar",
  components: {
    Calendar,
    YearPicker,
  },
  props: {
    isYear: [Boolean],
    rows: [Number],
    columns: [Number],
    monthNav: [String],
    monthMask: [Object],
    batchType: [String],
    newspaperName: [String]
  },
  data() {
    return {
      date: ref(new Date()),
      selectedYear: ref(new Date().getFullYear()), // Step 1: Add selectedYear property
      selectedMonth: ref(new Date().getMonth()),
      yearPickerInputStyle: {'text-align': 'center', 'font-size': 'larger', 'font-weight': 'bold'},
      calendarAttr: ref([{}]),
      errorMessage: ref("")
    }
  },
  created() {
    this.selectedYear = sessionStorage.getItem("selectedYear") ?? this.selectedYear;
    this.selectedMonth = sessionStorage.getItem("selectedMonth") ?? this.selectedMonth;
    this.date = new Date(this.selectedYear, this.selectedMonth, 1);
  },

  mounted() {
    if (this.isYear) {
      this.$nextTick(() => {
        this.$refs.yearPicker.updateModel(this.date);
      });
    }

  },
  watch: {
    batchType() {
      this.calendarAttr = [{}]
      this.batchesForMonth();
    },
    newspaperName() {
      this.calendarAttr[{}];
      this.batchesForYear();
    },
  },

  methods: {
    yearSelect() {
      let createdDate = this.date;
      this.$refs.yearPicker.updateModel(createdDate)
      this.$refs.yearCalendar.focusDate(createdDate)
      this.selectedYear = this.date.getFullYear();
      sessionStorage.setItem("selectedYear", this.date.getFullYear()); // Store selectedYear in local storage
    },

    next(event) {
      this.loading = true
      if (this.isYear) {
        this.date = new Date(event[0].year, 0, 1);
        this.$refs.yearPicker.updateModel(this.date);
        this.batchesForYear();
      } else {
        this.date = new Date(event[0].year, event[0].month - 1, 1)
        this.$forceUpdate();
        this.batchesForMonth()
        this.selectedMonth = this.date.getMonth();
        this.selectedYear = this.date.getFullYear();
        sessionStorage.setItem("selectedMonth",this.date.getMonth());
        sessionStorage.setItem("selectedYear",this.date.getFullYear());
      }
    },
    calendarDayClicked(calendarData, event) {
      if (calendarData.attributes.length > 0 && calendarData.attributes[0].batch.state === 'TechnicalInspectionComplete') {
        if (this.isYear) {
          this.$router.push({
            name: "newspaper-view",
            params: {
              batchid: calendarData.attributes[0].batch.id,
              newspaperid: calendarData.attributes[0].newspaper.id,
              year: calendarData.year,
              month: calendarData.month,
              day: calendarData.day
            }
          })
        } else {
          event.stopPropagation();
          this.$parent.showBatchInfo(calendarData)
        }
      }
    },
    async batchesForMonth() {
      try {
        const apiClient = axios.create({
          baseURL: '/kuana-ndb-api',
        })
        const {data} = await apiClient.get(`/batches?month=${this.date.getMonth() + 1}&year=${this.date.getFullYear()}&batch_type=${this.batchType}&get_latest=true`)
        let res = await Promise.all(data.map(async (batch) => {
          const {data} = await apiClient.get(`/batches/${batch.id}/has-problems`);
          return {
            highlight: {
              color: data ? 'red' : 'teal',
              fillMode: this.pickFillMode(batch.state)
            },
            dates: new Date(batch.date),
            popover: null,
            datePicker: null,
            batch
          };
        }));
        this.calendarAttr = res
      } catch (error) {
        console.log(error)
        this.errorMessage = "Unable to load batches";
      }
    },
    updateBatchState(batchName, newState) {
      for (let i = 0; i < this.calendarAttr.length; i++) {
        if (this.calendarAttr[i].batch.batch_name === batchName) {
          this.calendarAttr[i].batch.state = newState
          this.calendarAttr[i].highlight.fillMode = this.pickFillMode(newState)
        }
      }
    },
    pickFillMode(state) {
      return state === 'QAChecked' ? 'solid' : state === 'BatchInspected' ? 'light' : 'outline'
    },
    async batchesForYear() {
      try {
        const {data} = (await axios.get(`/kuana-ndb-api/batches?year=${this.date.getFullYear()}&newspaper_name=${this.newspaperName}&get_latest=true`));
        // data = data.filter(batch =>{batch.state === 'TechnicalInspectionComplete' || batch.state === 'AllDone' || batch.state === 'ProcessingToOpex' || batch.state === 'ReadyToBeProcessed'})
        const filteredData = data.filter(batch => {
          return batch.state === 'TechnicalInspectionComplete' || batch.state === 'AllDone' || batch.state === 'ProcessingToOpex' || batch.state === 'ReadyToBeProcessed'
        })
        let res = await Promise.all(filteredData.map(async (batch) => {
          const newspapers = (await axios.get(`/kuana-ndb-api/batches/${batch.id}/newspapers?newspaper_name=${this.newspaperName}`)).data;
          let newspaperBatches = await Promise.all(newspapers.map(async (newspaper) => {
            let hasPage = (await axios.get(`/kuana-ndb-api/batches/${batch.id}/newspapers/${newspaper.id}/has-page`)).data;
            return {
              highlight: {
                color: batch.state !== 'TechnicalInspectionComplete' ? 'gray': hasPage ? 'teal' : 'orange',
                fillMode: newspaper.checked ? 'light' : 'solid'
              },
              dates: new Date(batch.date),
              popover: null,
              datePicker: null,
              batch,
              newspaper
            };
          }));
          return newspaperBatches;
        }));
        this.calendarAttr = res.flat();
        this.selectedYear = this.date.getFullYear();
        sessionStorage.setItem("selectedYear", this.selectedYear);
      } catch (error) {
        console.log(error);
        this.errorMessage = "Unable to load batches";
      }
    }
  },
})

</script>
<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>
