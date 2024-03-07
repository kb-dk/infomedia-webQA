<template>
  <b-row>
    <nav>
      <router-link to="/">Home</router-link> |
      <router-link to="/technical">Technical QA</router-link>
    </nav>
  </b-row>
  <b-row>
    <b-col sm="2">
      <NewspaperTable/>
    </b-col>
    <b-col sm="8">
      <router-view/>
    </b-col>

  </b-row>


</template>
<script>
import NewspaperTable from "@/components/NewspaperTable";
  export default {
    components:{
      NewspaperTable
    }
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
