import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import PrimeVue from 'primevue/config'
import {setupCalendar} from 'v-calendar'
import components from './components/UI';
import BootstrapVue3 from 'bootstrap-vue-3';
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import {createPinia} from 'pinia'
const app = createApp(App)
components.forEach((component: any) => {
    app.component(component.name, component)
})
const pinia = createPinia();
app.use(router).use(setupCalendar,{transition:"fade"}).use(PrimeVue)
app.use(BootstrapVue3);
app.use(pinia);
app.mount('#app')
