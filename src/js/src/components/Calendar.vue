<template>
  <div class="calendar">
    <YearPicker :inputStyle="yearPickerInputStyle" ref="yearPicker" inputId="yearPickerInput" @date-select="yearSelect" id v-model="date" view="year" dateFormat="yy" class="yearPicker">
    </YearPicker>
    <!--  <DatePicker :masks="format"/>-->
    <Calendar locale="da" :initialPage="{day:1,month:1,year:currentYear}"
              :masks="{title:'MMMM'}" :rows="3" ref="yearCalendar" class="yearCalendar"
              :columns="4" :attributes="testData" :first-day-of-week="2" :nav-visibility="null"
              @did-move="next($event)"
              @dayclick="routeToNewspaper($event)"
              @load="getBatches"
             />
    <!--  <Calendar v-model="date"></Calendar>-->
  </div>

</template>

<script>
import {defineComponent, ref} from "vue";
import {Calendar} from 'v-calendar';
import YearPicker from 'primevue/calendar'
// import 'v-calendar/style.css';
import "primevue/resources/themes/lara-light-indigo/theme.css";
import'v-calendar/style.css'
// import "src/style/stylesheet.scss";

export default defineComponent({
  name: "year-calendar",
  components: {
    Calendar,
    YearPicker
  },
  data() {
    return {
      currentYear: 2023,
      date: ref().value = new Date(2023,0,1),
      yearPickerInputStyle: {'text-align':'center','font-size':'larger','font-weight':'bold'}


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
    },

  },
  methods: {
    yearSelect() {
      let createdDate = this.date;
      this.$refs.yearPicker.updateModel(createdDate)
      this.$refs.yearCalendar.focusDate(createdDate)

    },
    next(event){
      this.date.value = new Date(event[0].year,0,1)
      this.$refs.yearPicker.updateModel(this.date.value)
    },
    routeToNewspaper(event){
      this.$router.push({name:"newspaper-view",params:{batchid:"dl_10102023_rt1",newspaperid:"Aarhusstiftidende",year:event.year,month:event.month,day:event.day}})
    },
    async getBatches(){
      const res = await fetch("/api/batch")
      const data = await res.json()
      console.log(data);
    }
  },




})
// this.getBatches();
</script>
<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>
