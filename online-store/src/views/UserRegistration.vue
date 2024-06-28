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

    <!-- Registration form container -->
    <div class="registration-container">
      <PrimeCard class="registration-form">
        <template #title>
          <h2>Register</h2>
        </template>
        <form @submit.prevent="register">
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
          <!-- Birthdate input -->
          <div class="form-group">
            <label for="birthdate">Birthdate</label>
            <input type="date" id="birthdate" v-model="birthdate" />
          </div>
          <!-- Button group -->
          <div class="button-group">
            <PrimeButton label="Register" icon="pi pi-check" type="submit" class="register-button" />
            <PrimeButton label="Close" icon="pi pi-times" type="button" @click="closeModal" class="close-button" />
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
import axios from "axios";

export default defineComponent({
  name: 'UserRegistration',
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
    const birthdate = ref('');
    const errorMessage = ref('');

    const validateInputs = () => {
      if (!username.value) {
        errorMessage.value = 'Username cannot be empty';
        return false;
      }
      if (username.value.length < 3) {
        errorMessage.value = 'Username must be at least 3 characters long';
        return false;
      }
      if (!/^[a-zA-Z0-9]+$/.test(username.value)) {
        errorMessage.value = 'Username can only contain letters and numbers';
        return false;
      }
      if (!password.value) {
        errorMessage.value = 'Password cannot be empty';
        return false;
      }
      if (password.value.length < 6) {
        errorMessage.value = 'Password must be at least 6 characters long';
        return false;
      }
      if (!/[A-Z]/.test(password.value) || !/[a-z]/.test(password.value) || !/[0-9]/.test(password.value)) {
        errorMessage.value = 'Password must contain at least one uppercase letter, one lowercase letter, and one number';
        return false;
      }
      if (!birthdate.value) {
        errorMessage.value = 'Birthdate cannot be empty';
        return false;
      }
      if (new Date(birthdate.value) > new Date()) {
        errorMessage.value = 'Birthdate cannot be in the future';
        return false;
      }
      return true;
    };

    const register = async () => {
      if (validateInputs()) {
        const params = {
          username: username.value,
          password: password.value,
          birthday: birthdate.value,
        };

        try {
          const response = await axios.get("http://localhost:8082/api/trading/register",{ params });
          console.log(response.data); 
          //TODO add success message
        } catch (error) {
          errorMessage.value = error.response.data || 'Failed to register'; // Display server error message if available
          console.error('Registration error:', error);
        }
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

    return {
      username,
      password,
      birthdate,
      errorMessage,
      register,
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

.registration-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: rgba(0, 0, 0, 0.5);
}

.registration-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  text-align: center;
}

.registration-form h2 {
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
}

.form-group .p-password {
  width: 100%;
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

.register-button .p-button {
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
