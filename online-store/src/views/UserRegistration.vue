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
            <PrimeCalendar v-model="birthdate" id="birthdate"/>
          </div>
          <!-- Button group -->
          <div class="button-group">
            <PrimeButton label="Register" icon="pi pi-check" type="submit" class="register-button" />
            <PrimeButton label="Close" icon="pi pi-times" type="button" @click="closeModal" class="close-button" />
          </div>
        </form>
      </PrimeCard>
    </div>
    <p-toast></p-toast>
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
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';
import { PrimeCalendar } from 'primevue/calendar';

export default defineComponent({
  name: 'UserRegistration',
  components: {
    PrimeButton,
    InputText,
    PasswordText,
    PrimeCard,
    PrimeCalendar,
    'p-toast': PrimeToast,
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const username = ref('');
    const password = ref('');
    const birthdate = ref('');

    const validateInputs = () => {
      if (!username.value) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Username cannot be empty', life: 3000 });
        return false;
      }
      if (username.value.length < 3) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Username must be at least 3 characters long', life: 3000 });
        return false;
      }
      if (!/^[a-zA-Z0-9]+$/.test(username.value)) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Username can only contain letters and numbers', life: 3000 });
        return false;
      }
      if (!password.value) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Password cannot be empty', life: 3000 });
        return false;
      }
      if (password.value.length < 6) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Password must be at least 6 characters long', life: 3000 });
        return false;
      }
      if (!/[A-Z]/.test(password.value) || !/[a-z]/.test(password.value) || !/[0-9]/.test(password.value)) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Password must contain at least one uppercase letter, one lowercase letter, and one number', life: 3000 });
        return false;
      }
      if (!birthdate.value) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Birthdate cannot be empty', life: 3000 });
        return false;
      }
      if (new Date(birthdate.value) > new Date()) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Birthdate cannot be in the future', life: 3000 });
        return false;
      }
      return true;
    };

    const register = async () => {
      if (validateInputs()) {
        const formattedBirthdate = new Date(birthdate.value).toISOString().split('T')[0]; // format the date as yyyy-MM-dd

        const params = {
          username: username.value,
          password: password.value,
          birthday: formattedBirthdate,
        };

        try {
          const response = await axios.get("http://localhost:8082/api/trading/register", { params });
          toast.add({ severity: 'success', summary: 'Success', detail: 'Registration successful', life: 3000 });
          console.log(response);
          router.push('/');
        } catch (error) {
          toast.add({ severity: 'error', summary: 'Error', detail: error.response || 'Failed to register', life: 3000 });
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
    text-align: left;
  }

  .form-group .p-inputtext,
  .form-group .p-password,
  .form-group .p-calendar {
    /* Assuming these classes are defined elsewhere */
    /* flex-direction: column; / / Not necessary */
    /* align-items: center; / / Not necessary */
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

  .register-button .p-button,
  .close-button .p-button,
  .goback-button .p-button {
    width: 100%;
    }
</style>