import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import {setupCalendar} from 'v-calendar'
import components from './components/UI';

const app = createApp(App)
app.use(router).use(setupCalendar,{transition:"fade"}).use(PrimeVue).mount('#app')
