<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { createApp } from 'vue';
import PrimeVue from 'primevue/config';
import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';
import webSocketService from './webSocketService';

export default {
  name: 'App',
  created() {
    const app = createApp();
    app.use(PrimeVue);
    this.$root.$primevue = PrimeVue;
    const webSocketUrl = 'ws://localhost:8080/ws';
    webSocketService.connect(webSocketUrl);
    
    webSocketService.subscribe(this.handleWebSocketMessage);
  },
  beforeDestroy() {
    webSocketService.unsubscribe(this.handleWebSocketMessage);
    webSocketService.disconnect();
  },
  methods: {
    handleWebSocketMessage(message) {
      // Handle the incoming WebSocket message
      console.log('Received WebSocket message:', message);
      //notifications.value.push(message);
      // Add your custom handling logic here
    }
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
