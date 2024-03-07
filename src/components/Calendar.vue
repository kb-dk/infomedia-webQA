<template>
  <div class="calendar">
    <YearPicker v-if="isYear" :inputStyle="yearPickerInputStyle" ref="yearPicker" inputId="yearPickerInput" @date-select="yearSelect" id v-model="date" view="year" dateFormat="yy" class="yearPicker">
    </YearPicker>
    <!--  <DatePicker :masks="format"/>-->
    <Calendar locale="da" :initialPage="{day:1,month:1,year:currentYear}"
              :masks="monthMask" :rows="rows" ref="yearCalendar" class="yearCalendar"
              :columns="columns" :attributes="testData" :first-day-of-week="2" :nav-visibility="monthNav"
              expanded
              @did-move="next($event)"
              @dayclick="calendarDayClicked"
             />
    <!--  <Calendar v-model="date"></Calendar>-->
  </div>

</template>

<script>
import {defineComponent, ref, onMounted, onBeforeUnmount} from "vue";
import {Calendar} from 'v-calendar';
import YearPicker from 'primevue/calendar'
// import 'v-calendar/style.css';
import "primevue/resources/themes/lara-light-indigo/theme.css";
import'v-calendar/style.css'
import BatchMetadata from "@/components/BatchMetadata";
// import "src/style/stylesheet.scss";

export default defineComponent({
  name: "year-calendar",
  components: {
    Calendar,
    YearPicker,
  },
  props:{
    isYear: [Boolean],
    rows : [Number],
    columns : [Number],
    monthNav : [String],
    monthMask : [Object]
  },
  data() {
    return {
      currentYear: 2023,
      date: ref().value = new Date(2023,0,1),
      yearPickerInputStyle: {'text-align':'center','font-size':'larger','font-weight':'bold'},

    }
  },
  computed: {
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
      this.$refs.yearPicker.updateModel(createdDate)
      this.$refs.yearCalendar.focusDate(createdDate)

    },
    next(event){
      this.date.value = new Date(event[0].year,0,1)
      if(this.isYear){
        this.$refs.yearPicker.updateModel(this.date.value)
      }

    },
    calendarDayClicked(calendarData, event){
      if(this.isYear){
        this.$router.push({name:"newspaper-view",params:{batchid:"dl_20210101_rt1",newspaperid:"Aarhusstiftidende",year:2021,month:1,day:calendarData.day}})
      }else{
        event.stopPropagation()
        this.$parent.showBatchInfo(calendarData)
      }

    },
  },




})

</script>
<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>
