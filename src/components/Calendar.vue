<template>
  <div class="calendar">
    <YearPicker v-if="isYear" :inputStyle="yearPickerInputStyle" ref="yearPicker" inputId="yearPickerInput"
                @date-select="yearSelect" id v-model="date" view="year" dateFormat="yy" class="yearPicker">
    </YearPicker>
    <!--  <DatePicker :masks="format"/>-->
    <Calendar locale="da"
              :initialPage="{day: isYear ? 1: date.getDay(),month: isYear ? 1 : date.getMonth()+1,year:date.getFullYear()}"
              :masks="monthMask" :rows="rows" ref="yearCalendar" class="yearCalendar"
              :columns="columns" :attributes="calendarAttr" :first-day-of-week="2" :nav-visibility="monthNav"
              expanded
              @did-move="next($event)"
              @dayclick="calendarDayClicked"
              @update:pages.once="isYear ? batchesForYear() : batchesForMonth()"
    />
    <!--    {{batchesForMonth()}}-->
    <!--  <Calendar v-model="date"></Calendar>-->
  </div>

</template>

<script>
import {defineComponent, ref} from "vue";
import {Calendar} from 'v-calendar';
import YearPicker from 'primevue/calendar'
import axios from "axios";
// import 'v-calendar/style.css';
import "primevue/resources/themes/lara-light-indigo/theme.css";
import 'v-calendar/style.css'
import {useRoute} from "vue-router";
// import "src/style/stylesheet.scss";

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

          currentYear: 2023,
          date: ref(new Date()),
          yearPickerInputStyle: {'text-align': 'center', 'font-size': 'larger', 'font-weight': 'bold'},
          calendarAttr: ref([{}])
        }
      },
      watch: {
        batchType(newVal) {
          this.calendarAttr = [{}]
          this.batchesForMonth();
        },
        newspaperName(newVal){
          this.calendarAttr[{}];
          this.batchesForYear();
        }
      },

      methods: {
        yearSelect() {
          let createdDate = this.date;
          this.$refs.yearPicker.updateModel(createdDate)
          this.$refs.yearCalendar.focusDate(createdDate)

        },
        next(event) {
          this.loading = true

          // this.date.setMonth(event[0].month - 1);
          // this.date.setYear(event[0].year)
          if (this.isYear) {
            this.date = new Date(event[0].year, 0, 1);
            this.$refs.yearPicker.updateModel(this.date);
            this.batchesForYear();
          } else {
            this.date = new Date(event[0].year, event[0].month - 1, 1)
            this.$forceUpdate();
            this.batchesForMonth()
          }

        },
        calendarDayClicked(calendarData, event) {
          if (this.isYear) {
            console.log(calendarData)
            console.log(event)
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
            event.stopPropagation()
            // console.log(calendarData)
            this.$parent.showBatchInfo(calendarData)
          }

        },
        async batchesForMonth() {
          let res = [];
          const apiClient = axios.create({
            baseURL: '/api',
          })
          const {data} = await apiClient.get(`/batches?month=${this.date.getMonth() + 1}&year=${this.date.getFullYear()}&batch_type=${this.batchType}&get_latest=true`)
          res = data

          if (res.length > 0) {
            for (let i = 0; i < res.length; i++) {
              const {data} = await apiClient.get(`/batches/${res[i].id}/has_problems`);
              res[i] = {
                highlight: {
                  color: data ? 'red' : 'teal',
                  fillMode: this.pickFillMode(res[i].state)
                },
                dates: new Date(res[i].date),
                popover: null,
                datePicker: null,
                batch: res[i]

              }
            }
            this.calendarAttr = res
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
          let res = [];
          const {data} = await axios.get(`/api/batches?state=TechnicalInspectionComplete&year=${this.date.getFullYear()}&newspaper_name=${this.newspaperName}&get_latest=true`);
          res = data;
          for (let i = 0; i < res.length; i++) {
            const newspapers = (await axios.get(`/api/batches/${res[i].id}/newspapers?newspaper_name=${this.newspaperName}`)).data
            for(let j = 0; j < newspapers.length; j++) {
              res[i] = {
                highlight: {
                  color: 'teal',
                  fillMode: newspapers[j].checked?'light':'solid'
                },
                dates: new Date(res[i].date),
                popover: null,
                datePicker: null,
                batch: res[i],
                newspaper: newspapers[j]

              }
            }
            }

          this.calendarAttr = res;
        }

      }


    }
)

</script>
<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>
