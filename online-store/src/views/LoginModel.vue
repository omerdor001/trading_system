<template>
  <div>
    <header>
      <div class="header-content">
        <div class="left-buttons">
          <PrimeButton icon="pi pi-arrow-left" @click="goBack" />
        </div>
        <img src="@/assets/logo.png" alt="LASMONY" class="logo">
        <div class="right-buttons">
          <PrimeButton label="Notifications" icon="pi pi-bell" @click="toggleNotifications" />
          <PrimeButton label="Cart" icon="pi pi-shopping-cart" @click="viewCart" />
        </div>
      </div>
    </header>

    <div class="login-container">
      <PrimeCard class="login-form">
        <template #title>
          <h2>Login</h2>
        </template>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">Username</label>
            <InputText v-model="username" id="username" />
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <InputText type="password" v-model="password" id="password" />
          </div>
          <div class="button-group">
            <PrimeButton label="Login" icon="pi pi-check" type="submit" class="login-button" />
            <PrimeButton label="Register" icon="pi pi-user-plus" type="button" @click="goToRegister" class="register-button" />
          </div>
        </form>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </PrimeCard>
    </div>

    <!-- Notifications Toast -->
    <p-toast v-if="notificationsVisible" :baseZIndex="10000">
      <div v-for="(notification, index) in notifications" :key="index">
        {{ notification }}
      </div>
    </p-toast>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';
import { InputText } from 'primevue/inputtext';
import { Card as PrimeCard } from 'primevue/card';
import axios from 'axios';
import webSocketService from '../webSocketService';

export default defineComponent({
  name: 'LoginModel',
  components: {
    PrimeButton,
    InputText,
    PrimeCard
  },
  setup() {
    const router = useRouter();
    const username = ref('');
    const password = ref('');
    const errorMessage = ref('');
    const notifications = ref([]);
    const notificationsVisible = ref(false);

    // Function to handle login form submission
    const handleLogin = async () => {
      try {
            console.log(localStorage.getItem("token"));
            console.log(localStorage.getItem("username"));
            console.log(username.value);
            console.log(password.value);
        const response = await axios.get('http://localhost:8082/api/trading/login', {
          params: {
            token: localStorage.getItem("token"),
            usernameV: localStorage.getItem("username"),
            username: username.value,
            password: password.value,
          }
        });
        console.log('Login successful:', response.data);
        
        // Store user session information
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('username', response.data.username);
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('isAdmin', response.data.isAdmin);
        localStorage.setItem('userName', username.value)

        // Subscribe to notifications
        subscribeToNotifications(username.value);

        // Redirect to home page
        router.push('/');
      } catch (error) {
        if (error.response) {
          errorMessage.value = error.response.data || 'Failed to Login';
          console.error('Server responded with status:', error.response.status);
        } else if (error.request) {
          errorMessage.value = 'No response from server';
          console.error('No response received:', error.request);
        } else {
          errorMessage.value = 'Request failed to reach the server';
          console.error('Error setting up the request:', error.message);
        }
      }
    };

    // Function to handle WebSocket notifications
    const subscribeToNotifications = (/*username*/) => {
      const webSocketUrl = 'ws://localhost:8080/ws';
      webSocketService.connect(webSocketUrl);
      webSocketService.subscribe(handleWebSocketMessage);

      // Send the username to the backend after WebSocket connection is established
      // webSocketService.socket.onopen = () => {
      //   webSocketService.socket.send(JSON.stringify({ type: 'subscribe', username }));
      // };
    };

    // Function to handle incoming WebSocket messages (notifications)
    const handleWebSocketMessage = (message) => {
      console.log('Received WebSocket message:', message);
      notifications.value.push(message);
      notificationsVisible.value = true; // Show notifications toast
    };

    const goBack = () => {
      router.go(-1); // Go back to previous page using router
    };

    const goToRegister = () => {
      router.push('/register'); // Navigate to register page using router
    };

    const toggleNotifications = () => {
      notificationsVisible.value = !notificationsVisible.value;
    };

    const viewCart = () => {
      // Handle viewing cart
    };

    return {
      username,
      password,
      errorMessage,
      handleLogin,
      goBack,
      goToRegister,
      notifications,
      notificationsVisible,
      toggleNotifications,
      viewCart
    };
  }
});
</script>

<style scoped>
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: #425965;
  color: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  height: 100px;
}

.logo {
  height: 80px;
  width: auto;
}

.right-buttons, .left-buttons {
  display: flex;
  gap: 10px;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: rgba(0, 0, 0, 0.5);
}

.login-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  text-align: center;
}

.login-form h2 {
  margin-bottom: 20px;
  color: #425965;
}

.form-group {
  width: 100%;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
}

.button-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.login-button .p-button {
  width: 100%;
}

.register-button .p-button {
  width: 100%;
}

.error {
  color: red;
  margin-top: 10px;
  font-size: 14px;
}
</style>
