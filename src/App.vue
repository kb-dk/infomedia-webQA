<template>
  <div style="padding:20px;">
  <b-row>
    <b-col sm="2">
      <p v-if="errorMessage.length > 0" style="color: red">{{errorMessage}}</p>
      <NewspaperTable :newspapers="getNewspaperNames('dagsaviser')" headerName="Newspapers" :show="true"/>
      <br/>
      <b-button @click="showWeekly=!showWeekly">{{showWeekly ? "Hide":"Show Weekly"}}</b-button>
      <NewspaperTable :newspapers="getNewspaperNames('ugeaviser')" headerName="Weekly" :show="showWeekly"/>
      <br/>
      <b-button @click="showMagazine=!showMagazine">{{showMagazine ? "Hide":"Show Magazine"}}</b-button>
      <NewspaperTable :newspapers="getNewspaperNames('magasiner')" headerName="Magazines" :show="showMagazine"/>
    </b-col>
    <b-col sm="10">
      <nav>
        <router-link to="/">Home</router-link> |
        <router-link to="/technical">Technical QA</router-link>
      </nav>
      <router-view/>
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
          const {data} = await axios.get(`/api/newspapers/${newspaperType}`);
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
</style>
