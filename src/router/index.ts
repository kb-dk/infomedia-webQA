import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import NewspaperView from '../views/NewspaperView.vue'
import CalendarView from "@/views/CalendarView.vue";
import TechnicalView from "@/views/TechnicalView.vue";
const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path:'/:newspaperName',
    name:"newspaper-calendar",
    component:CalendarView
  },
  {
    path: '/technical',
    name: 'technical',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: TechnicalView
  },
  {
    path: '/:batchid/:newspaperid/:year/:month/:day',
    name: 'newspaper-view',
    component: NewspaperView
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
