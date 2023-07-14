<template>
  <YearPicker @date-select="yearSelect" v-model="date" view="year" dateFormat="yy" class="yearPicker"></YearPicker>
  <!--  <DatePicker :masks="format"/>-->
  <Calendar :initialPage="{day:1,month:1,year:currentYear}"
            :masks="{title:'MMMM'}" :rows="3" ref="yearCalendar" class="yearCalendar"
            :columns="4" :attributes="testData" :first-day-of-week="2" :nav-visibility="null"/>
  <!--  <Calendar v-model="date"></Calendar>-->
</template>

<script>
import {defineComponent, ref} from "vue";
import {Calendar} from 'v-calendar';
import YearPicker from 'primevue/calendar'
// import 'v-calendar/style.css';
import "primevue/resources/themes/lara-light-indigo/theme.css";
import'v-calendar/style.css'

export default defineComponent({
  name: "year-calendar",
  emits:["yearChange","onYearChange"],
  components: {
    // DatePicker,
    Calendar,
    YearPicker
    // DatePicker,
    //
  },
  data() {
    return {
      currentYear: 2023,
      date: ref(),

    }
  },
  computed: {
    createDateFromYear() {
      console.log(this.date)
      return new Date(this.date.value);
    },
    testData() {
      let result = [];
      for (let i = 0; i < 12; i++) {
        result[i] = {
          highlight: {
            color: i % 2 == 0 ? 'red' : 'green',
            fillMode: 'solid'
          },
          dates: new Date(2023, 5, i + 1),
          masks: {
            title: 'MMMM'
          },
          popover: null,
          datePicker: null,
        }
      }
      return result;
    }
  },
  methods: {
    yearSelect() {
      let createdDate = this.date;
      // console.log("hello")
      console.log(createdDate);
      this.$refs.yearCalendar.focusDate(createdDate)

    }
  },



})

</script>
