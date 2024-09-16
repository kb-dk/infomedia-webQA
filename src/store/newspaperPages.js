
import {ref} from "vue";
import {defineStore} from "pinia";
import axios from "axios";

export const newspaperPagesStore =  defineStore('newspaperPages',()=>{
  const newspaperPages = ref([]);
  const newspaperPage = ref([]);
  const newspaperFrontPages= ref([])
  const randomNewspaperPages = ref([])
  const cachedNewspaperPagesBlob = ref(new Map);
  const nextDayNewspaperPagesBlob = ref(new Map);
  const cachedNewspaperPages = ref([]);
  const nextDayNewspaperPages = ref([]);
  const useCached = ref(false);
  const allLoaded = ref(false);
  function setUseCached(toCache){
    useCached.value = toCache;
    if(useCached.value){
      cachedNewspaperPages.value = nextDayNewspaperPages.value;
      cachedNewspaperPagesBlob.value = nextDayNewspaperPagesBlob.value;
    }
  }
  async function addNextDayBlob(key,blob) {
    nextDayNewspaperPagesBlob.value.set(key,await blob)
  }
  function getFrontPages(){
    randomNewspaperPages.value = [];
    newspaperPage. value = [];
    return newspaperFrontPages.value;
  }
  function getRandomPages(){
    newspaperFrontPages.value = [];
    newspaperPage. value = [];
    return randomNewspaperPages.value;
  }
  function getPage(){
    randomNewspaperPages.value = [];
    newspaperFrontPages. value = [];
    return newspaperPage.value;
  }
  return {newspaperPages,
    newspaperPage,
    newspaperFrontPages,
    randomNewspaperPages,
    cachedNewspaperPages,
    cachedNewspaperPagesBlob,
    nextDayNewspaperPages,
    nextDayNewspaperPagesBlob,
    useCached,
    setUseCached,
    getFrontPages,
    getRandomPages,
    getPage,
    addNextDayBlob,
    allLoaded
  }
});
