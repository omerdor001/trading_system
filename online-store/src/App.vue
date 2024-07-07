<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { createApp } from 'vue';
import PrimeVue from 'primevue/config';
import { testService } from "@/testExternalSystem";
import 'primevue/resources/themes/saga-blue/theme.css'; // or other theme
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';

export default {
  name: 'App',
  created() {
    const app = createApp();
    app.use(PrimeVue);
    this.$root.$primevue = PrimeVue;
    testService();
  }
}
</script>

<style scoped>
@import 'primevue/resources/themes/saga-blue/theme.css';
@import 'primevue/resources/primevue.min.css';
@import 'primeicons/primeicons.css';

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
