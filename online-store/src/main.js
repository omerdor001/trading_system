import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import PrimeVue from 'primevue/config';
import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';
import PrimeVueButton from 'primevue/button'; 

const app = createApp(App);
app.use(PrimeVue);
app.component('PrimeButton', PrimeVueButton);
app.use(router);
app.mount('#app');
