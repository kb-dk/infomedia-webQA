<template>
  <div style="padding:20px;">
    <b-row>
      <b-col sm="2">
        <nav>
          <router-link to="/">Home</router-link>
          |
          <router-link to="/technical">Technical QA</router-link>
        </nav>
        <div class="newspaperTables">
          <p v-if="errorMessage.length > 0" style="color: red">{{ errorMessage }}</p>
          <NewspaperTable :newspapers="getNewspaperNames('dagsaviser')" headerName="Newspapers" :show="true"/>
          <br/>
          <b-button @click="showWeekly=!showWeekly">{{ showWeekly ? "Hide" : "Show Weekly" }}</b-button>
          <NewspaperTable :newspapers="getNewspaperNames('ugeaviser')" headerName="Weekly" :show="showWeekly"/>
          <br/>
          <b-button @click="showMagazine=!showMagazine">{{ showMagazine ? "Hide" : "Show Magazine" }}</b-button>
          <NewspaperTable :newspapers="getNewspaperNames('magasiner')" headerName="Magazines" :show="showMagazine"/>
        </div>
      </b-col>
      <b-col sm="10">
        <router-view :key="$route.fullPath"/>
      </b-col>
    </b-row>
  </div>
</template>
<script>
import NewspaperTable from "@/components/NewspaperTable";
import axios from 'axios';
import {ref} from "vue";
  export default {
    components:{
      NewspaperTable
    },
    data(){
      return{
        showWeekly:ref(false),
        showMagazine:ref(false),
        errorMessage:ref("")
      }
    },
    methods:{
      async getNewspaperNames(newspaperType){
        try{
          const {data} = await axios.get(`/kuana-ndb-api/newspapers/${newspaperType}`);
          return data;
        }catch (error){
          console.log(error);
          this.errorMessage = "Unable to load newspaper names";
        }
      }
    },
  }
</script>
<style lang="scss">

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
.newspaperTables{
  min-height: 40vh;
  max-height: 80vh;
  overflow: scroll;
  overflow-scrolling: auto;

}
.newspaperTables::-webkit-scrollbar{
  width:5px
}
.newspaperTables::-webkit-scrollbar-thumb{
  background-color:rgba(108, 107, 107, 0.57)
}
</style>
