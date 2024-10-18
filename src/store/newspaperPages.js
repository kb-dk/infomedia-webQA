
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
  const pdfRequests = ref();
  function setUseCached(toCache){
    useCached.value = toCache;

    if(useCached.value){
      cachedNewspaperPages.value = nextDayNewspaperPages.value;
      cachedNewspaperPagesBlob.value = new Map(nextDayNewspaperPagesBlob.value);

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
  function clearAll(){
    newspaperPages.value = [];
    newspaperPage.value = [];
    newspaperFrontPages.value= []
    randomNewspaperPages.value = []
    cachedNewspaperPagesBlob.value = new Map;
    nextDayNewspaperPagesBlob.value = new Map;
    cachedNewspaperPages.value = [];
    nextDayNewspaperPages.value = [];
    useCached.value = false;
    allLoaded.value = false;
  }
  function cancelRequests(){
    if(pdfRequests.value){
      console.log(pdfRequests.value)
      pdfRequests.value.cancel();
    }

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
    pdfRequests,
    setUseCached,
    getFrontPages,
    getRandomPages,
    getPage,
    addNextDayBlob,
    allLoaded,
    clearAll,
    cancelRequests
  }
});
