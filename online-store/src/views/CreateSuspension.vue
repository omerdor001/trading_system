<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="suspend-user">
      <h2>Suspend User</h2>
      <form @submit.prevent="submitForm">
        <div class="p-field">
          <label for="toSuspend">Username: </label>
          <InputText v-model="toSuspend" id="toSuspend" required />
        </div>
        <div class="p-field">
          <label for="endSuspention">End-Date: </label>
          <PrimeCalendar v-model="endSuspention" id="endSuspention" showTime showSeconds required />
        </div>
        <div class="button-group">
            <PrimeButton label="Suspend" type="submit" class="p-mt-2" />
          </div>
      </form>
      <div v-if="error" class="p-error">{{ error }}</div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { InputText } from 'primevue/inputtext';
import { PrimeCalendar } from 'primevue/calendar';
import { PrimeButton } from 'primevue/button';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'CreateSuspension',
  components: {
    SiteHeader,
    InputText,
    PrimeCalendar,
    PrimeButton,
  },
  setup() {
    const router = useRouter();
    const admin = ref('');
    const toSuspend = ref('');
    const endSuspention = ref(null);
    const error = ref(null);
    const username = ref(localStorage.getItem('username') || '');

    const submitForm = async () => {
      try {
        error.value = null;
        await axios.post('/api/suspendUser', {
          admin: username,
          toSuspend: toSuspend.value,
          endSuspention: endSuspention.value,
        });
        alert('User suspended successfully');
      } catch (err) {
        error.value = err.response.data.message || 'An error occurred';
      }
    };

    const logout = () => {
      router.push('/login');
    };

    return {
      admin,
      toSuspend,
      endSuspention,
      error,
      username,
      submitForm,
      logout,
    };
  },
});
</script>

<style scoped>
.suspend-user {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.suspend-user h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.suspend-user .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  display: block;
  color: #333;
  margin-bottom: 5px;
}

.form-group input,
.form-group textarea {
  width: calc(100% - 20px);
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
