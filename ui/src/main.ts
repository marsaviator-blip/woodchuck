import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { vueKeycloak } from '@josempgon/vue-keycloak'
import axios from 'axios'
import App from './App.vue'
import router from './router'
import { getToken } from '@josempgon/vue-keycloak'

// Create an instance of axios with the base URL read from the environment
const baseURL = import.meta.env.VITE_API_URL
const instance = axios.create({ baseURL })

// Request interceptor for API calls
// instance.interceptors.request.use(
//   async config => {
//     const token = await getToken()
//     config.headers['Authorization'] = `Bearer ${token}`
//     return config
//   },
//   error => {
//     Promise.reject(error)
//   },
// )

const app = createApp(App)

app.use(createPinia())
app.use(router)
// app.use(vueKeycloak, {
//   config: {
//     url: 'http://localhost:8080/admin',
//     realm: 'wood-chuck',
//     clientId: 'woodChuckClient',
//   }
// })

app.mount('#app')
