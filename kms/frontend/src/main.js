import { createApp } from 'vue';
//import KmsWorkspace from './views/KmsWorkspace.vue';
import './tailwind.css'; // Verify this file path matches your folder exactly
import App from './App.vue'
import router from './router' // <-- 1. Import your router file
// import './style.css'

const app = createApp(App)

app.use(router) // <-- 2. Tell Vue to use the router

app.mount('#app')
