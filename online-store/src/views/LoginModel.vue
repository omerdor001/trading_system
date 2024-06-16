<template>
  <div>
    <!-- Header and logo section (unchanged) -->
    <header>
      <div class="header-content">
        <img src="@/assets/logo.png" alt="LASMONY" class="logo">
        <div class="right-buttons">
          <PrimeButton label="Notifications" icon="pi pi-bell" @click="notifications" />
          <PrimeButton label="Cart" icon="pi pi-shopping-cart" @click="viewCart" />
        </div>
      </div>
    </header>

    <!-- Login form container -->
    <div class="login-container">
      <Card class="login-form">
        <template #title>
          <h2>Login</h2>
        </template>
        <form @submit.prevent="handleLogin">
          <!-- Username input -->
          <div class="form-group">
            <label for="username">Username</label>
            <InputText v-model="username" id="username" required />
          </div>
          <!-- Password input -->
          <div class="form-group">
            <label for="password">Password</label>
            <Password v-model="password" id="password" toggleMask required />
          </div>
          <!-- Button group -->
          <div class="button-group">
            <PrimeButton label="Login" icon="pi pi-check" type="submit" class="login-button" />
            <PrimeButton label="Close" icon="pi pi-times" type="button" @click="closeModal" class="close-button" />
          </div>
        </form>
        <!-- Error message -->
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </Card>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';
import { InputText } from 'primevue/inputtext';
import { Password } from 'primevue/password';
import { Card } from 'primevue/card';

export default defineComponent({
  name: 'LoginModel',
  components: {
    PrimeButton,
    InputText,
    Password,
    Card
  },
  setup() {
    const router = useRouter();
    const username = ref('');
    const password = ref('');
    const errorMessage = ref('');

    const handleLogin = async () => {
      try {
        // Perform authentication logic here
        const loggedIn = await authenticate(username.value, password.value);
        if (loggedIn) {
          // Successful login
          router.push('/'); // Redirect to home page
          localStorage.setItem('isLoggedIn', 'true');
          localStorage.setItem('username', username.value);
        } else {
          // Authentication failed
          errorMessage.value = 'Invalid username or password';
        }
      } catch (error) {
        errorMessage.value = error.message;
      }
    };

    const closeModal = () => {
      router.push('/');
    };

    const notifications = () => {
      // Handle notifications
    };

    const viewCart = () => {
      // Handle viewing cart
    };

    // Simulated authentication function (replace with actual logic)
    const authenticate = async (username, password) => {
      let roles = [];
      switch (username) {
        case 'admin':
          if (password === 'admin') {
            roles.push('storeManager', 'commercialManager', 'storeOwner', 'systemManager');
          }
          break;
        case 'manager':
          if (password === 'manager') {
            roles.push('storeManager');
          }
          break;
        case 'system':
          if (password === 'system') {
            roles.push('systemManager');
          }
          break;
        case 'commercial':
          if (password === 'commercial') {
            roles.push('commercialManager');
          }
          break;
        case 'lana':
          if (password === 'lana') {
            roles.push('commercialManager', 'systemManager', 'storeOwner');
          }
          break;
        case 'user':
          if (password === 'user') {
            roles = [];
          }
          break;
        default:
          return false;
      }
      
      // Simulate async delay
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Simulate successful login
      return true;
    };

    return {
      username,
      password,
      errorMessage,
      handleLogin,
      closeModal,
      notifications,
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

.right-buttons {
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

.form-group .p-inputtext {
  width: 100%;
  max-width: 300px; 
}

.form-group .p-password {
  width: 100%;
  max-width: 300px; 
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

.close-button .p-button {
  width: 100%;
}

.error {
  color: red;
  margin-top: 10px;
  font-size: 14px;
}
</style>

