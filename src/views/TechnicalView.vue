<template>
  <p v-if="errorMessage" style="color: red">{{ errorMessage }}</p>
  <div class="app">
    <div class="custom-card">
      <b-card bg-variant="light">
        <b-form-group label="Newspaper types" v-slot="{ ariaDescribedby }" class="custom-form-group"
                      label-cols-sm="12"
                      label-align-sm="center" label-class="bold-label" label-size="lg">
          <b-form-radio-group
              v-model="batchType"
              :options="radioBtnOptions"
              :aria-describedby="ariaDescribedby"
              name="radios-stacked"
          ></b-form-radio-group>
        </b-form-group>
      </b-card>
    </div>
    <div id="technicalDiv">
      <Calendar :isYear="false" :rows="1" :columns="1" :monthNav="'click'" :monthMask="{title:'MMMM YYYY'}"
                :batchType="batchType"
                :expanded="true" ref="calendarRef">
      </Calendar>
      <BatchMetadata ref="batchMetadataRef"></BatchMetadata>
    </div>
    <div class="button-container">
      <ApproveButton
          stateSource="BatchInspected"
          stateDest="TechnicalInspectionComplete"
          @click="sendApprovedBatches('TechnicalInspectionComplete')"
          btn-text="Send approved batches"
          title="Change batch status from Batch Inspected to Technical Inspection Complete"
      ></ApproveButton>
      <ApproveButton
          stateSource="BatchInspected"
          stateDest="ReadyToBeProcessed"
          @click="sendApprovedBatches('ReadyToBeProcessed')"
          btn-text="Process approved batches"
          :disabled="isProcessButtonDisabled"
          title="Change batch status from Batch Inspected to Ready To Be Processed"
      ></ApproveButton>
      <b-button class="changeCarouselView"
                @click="exportNotes">Export Notes
      </b-button>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, ref, unref} from 'vue';
import Calendar from '@/components/Calendar.vue'// @ is an alias to /src
import BatchMetadata from "@/components/BatchMetadata.vue";
import ApproveButton from "@/components/ApproveButton.vue";
import axios from "axios";

export default defineComponent({
  name: 'CalendarView',
  watch: {
    '$route'(to, from) {
      this.newspaper = to.params.newspaperid;
    }
  },
  components: {
    ApproveButton,
    Calendar,
    BatchMetadata
  },
  data() {
    return {
      errorMessage: '',
      newspaper: this.$route.params.newspaperid,
      batchType: ref("dagsaviser"),
      radioBtnOptions: [
        {text: 'Dagsaviser', value: 'dagsaviser'},
        {text: 'Ugeaviser', value: 'ugeaviser'},
        {text: 'Magasiner', value: 'magasiner'}
      ]
    }
  },
  computed: {
    isProcessButtonDisabled(): boolean {
      return unref(this.batchType) === 'dagsaviser';
    }
  },
  methods: {
    showBatchInfo(event: any) {
      // this.batchMetadata.active.value = true;
      (this.$refs.batchMetadataRef as typeof BatchMetadata).showBatchData(event);
    },
    reloadBatch(batchName: string, newState: string) {
      this.$forceUpdate();
      console.log("reload");
      (this.$refs.calendarRef as typeof Calendar).updateBatchState(batchName, newState);
    },
    reloadCalendar() {
      this.$forceUpdate();
      (this.$refs.calendarRef as typeof Calendar).batchesForMonth();
    },

    async sendApprovedBatches(stateParam: any) {
      try {
        const { calendarRef } = this.$refs;
        const currentDate = (calendarRef as typeof Calendar).date;

        const { data } = await axios.get(`/kuana-ndb-api/batches?month=${currentDate.getMonth() + 1}&year=${currentDate.getFullYear()}&state=BatchInspected`);

        for (const batch of data) {
          batch.state = stateParam;
          await axios.put(`/kuana-ndb-api/batches/`, batch, { headers: { 'Content-Type': 'application/json' } });
        }

        this.reloadCalendar();
      } catch (error) {
        console.error(error);
        // Display the error message to the user
        this.errorMessage = 'An error occurred while sending approved batches. Please try again later.';
      }
    },
    async exportNotes() {
      try {
        const { calendarRef } = this.$refs;
        const currentDate = (calendarRef as typeof Calendar).date;

        const approvedBatches = await this.fetchBatch(currentDate);
        const batchInfos = this.extractBatchIds(approvedBatches);
        const batchNotes = await this.fetchAllNotesForBatches(batchInfos);

        const notes = batchNotes.flatMap(noteArray => noteArray);
        this.exportToCSV(notes, currentDate);
        alert('Notes exported to ' + this.getCSVFilename(currentDate));
      } catch (error) {
        console.error(error);
      }
    },
    async fetchBatch(currentDate: Date) {
      try {
        const response = await axios.get(`/api/batches?month=${currentDate.getMonth() + 1}&year=${currentDate.getFullYear()}&batch_type=dagsaviser&get_latest=false`);
        return response.data;
      } catch (error) {
        console.error(error);
      }
    },
    extractBatchIds(approvedBatches: any[]) {
      return approvedBatches.map(batch => ({ id: batch.id, batch_name: batch.batch_name }));
    },
    async fetchAllNotesForBatches(batchInfos: any[]) {
      const notes = [];

      for (const batchInfo of batchInfos) {
        const response = await axios.get(`/api/batches/${batchInfo.id}/all-notes-to-batch`);
        const noteArray = response.data;

        if (noteArray.length != 0) {
          for (const note of noteArray) {
            note.batchName = batchInfo.batch_name;

            if (note.newspaper_id) {
              const newspaperResponse = await axios.get(`/api/batches/${batchInfo.id}/newspapers/${note.newspaper_id}`);
              note.newspaperName = newspaperResponse.data.newspaper_name;
            }else {
              note.newspaperName = null;
            }
            notes.push(note);
          }
        }
      }
      return notes;
    },
    exportToCSV(notes: any[], currentDate: Date) {
      let csvContent = "Note Text,Batch Name,Newspaper Title,Section Title,Page Number\n";

      notes.forEach(note => {
        csvContent += `${note.note},${note.batchName},${note.newspaperName},${note.section_title},${note.page_number}\n`;
      });

      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
      const link = document.createElement('a');
      link.setAttribute('href', URL.createObjectURL(blob));
      link.setAttribute('download', this.getCSVFilename(currentDate));
      document.body.appendChild(link);
      link.click();
    },

    getCSVFilename(date: Date): string {
      const month = date.getMonth() + 1;
      const year = date.getFullYear();
      return `notes_${month}_${year}.csv`;
    },

    closeBatchInfo() {
      (this.$refs.batchMetadataRef as typeof BatchMetadata).close();
    }
  },
});
</script>

<style lang="scss" scoped>
@import "../style/stylesheet.scss";
</style>