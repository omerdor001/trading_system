<template>
  <div>
    <!-- Header and logo section (unchanged) -->
    <header>
      <div class="header-content">
        <div class="left-buttons">
          <PrimeButton icon="pi pi-arrow-left" @click="goBack" />
        </div>
        <img src="@/assets/logo.png" alt="LASMONY" class="logo">
        <div class="right-buttons">
          <PrimeButton label="Notifications" icon="pi pi-bell" @click="notifications" />
          <PrimeButton label="Cart" icon="pi pi-shopping-cart" @click="viewCart" />
        </div>
      </div>
    </header>

    <!-- Login form container -->
    <div class="login-container">
      <PrimeCard class="login-form">
        <template #title>
          <h2>Login</h2>
        </template>
        <form @submit.prevent="handleLogin">
          <!-- Username input -->
          <div class="form-group">
            <label for="username">Username</label>
            <InputText v-model="username" id="username" />
          </div>
          <!-- Password input -->
          <div class="form-group">
            <label for="password">Password</label>
            <PasswordText v-model="password" id="password" />
          </div>
          <!-- Button group -->
          <div class="button-group">
            <PrimeButton label="Login" icon="pi pi-check" type="submit" class="login-button" />
            <PrimeButton label="Register" icon="pi pi-user-plus" type="button" @click="goToRegister" class="register-button" />
          </div>
        </form>
        <!-- Error message -->
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </PrimeCard>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';
import { InputText } from 'primevue/inputtext';
import { PasswordText } from 'primevue/password';
import { PrimeCard } from 'primevue/card';

export default defineComponent({
  name: 'LoginModel',
  components: {
    PrimeButton,
    InputText,
    PasswordText,
    PrimeCard
  },
  setup() {
    const router = useRouter();
    const username = ref('');
    const password = ref('');
    const errorMessage = ref('');

    const authenticate = async (username, password) => {
      let roles = [];
      let isAuthenticated = false;

      switch (username) {
        case 'admin':
          if (password === 'admin') {
            roles = ['storeManager', 'commercialManager', 'storeOwner', 'systemManager'];
            isAuthenticated = true;
          }
          break;
        case 'manager':
          if (password === 'manager') {
            roles = ['storeManager'];
            isAuthenticated = true;
          }
          break;
        case 'system':
          if (password === 'system') {
            roles = ['systemManager'];
            isAuthenticated = true;
          }
          break;
        case 'commercial':
          if (password === 'commercial') {
            roles = ['commercialManager'];
            isAuthenticated = true;
          }
          break;
        case 'lana':
          if (password === 'lana') {
            roles = ['commercialManager', 'systemManager', 'storeOwner'];
            isAuthenticated = true;
          }
          break;
        case 'user':
          if (password === 'user') {
            roles = [];  // No roles for regular user
            isAuthenticated = true;
          }
          break;
        default:
          isAuthenticated = false;
      }

      await new Promise(resolve => setTimeout(resolve, 1000));

      if (isAuthenticated) {
        localStorage.setItem('roles', JSON.stringify(roles));
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('username', username);
        return true;
      } else {
        localStorage.removeItem('roles');
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('username');
        return false;
      }
    };

    const handleLogin = async () => {
      try {
        const loggedIn = await authenticate(username.value, password.value);
        if (loggedIn) {
          router.push('/');
        } else {
          errorMessage.value = 'Invalid username or password';
        }
      } catch (error) {
        errorMessage.value = error.message;
      }
    };

    const goBack = () => {
      router.go(-1);
    };

    const goToRegister = () => {
      router.push('/register');
    };

    const notifications = () => {
      // Handle notifications
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

.custom-card {
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 1rem;
  display: block;
}

.card-footer {
  background-color: #f0f0f0;
  padding: 0.5rem;
  text-align: center;
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
