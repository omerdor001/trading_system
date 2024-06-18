<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="suspend-user">
      <h2>End Suspension</h2>
      <form @submit.prevent="submitForm">
        <div class="p-field">
          <label for="toSuspend">Username: </label>
          <InputText v-model="toSuspend" id="toSuspend" required />
        </div>
        <div class="button-group">
          <PrimeButton label="End Suspension" type="submit" class="p-mt-2" />
          <PrimeButton type="button" label="Back" class="back-button" @click="goBack" />
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
import { PrimeButton } from 'primevue/button';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'EndSuspension',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
  },
  setup() {
    const router = useRouter();
    const toSuspend = ref('');
    const error = ref(null);
    const username = ref(localStorage.getItem('username') || '');

    const submitForm = async () => {
      try {
        error.value = null;
        await axios.post('/api/endSuspendUser', {
          admin: username,
          toSuspend: toSuspend.value,
        });
        alert('User suspension ended successfully');
        router.push('/'); // Redirect to home or appropriate page
      } catch (err) {
        error.value = err.response.data.message || 'An error occurred';
      }
    };

    const logout = () => {
      router.push('/login');
    };

    const goBack = () => {
      router.push('/');
    };

    return {
      username,
      toSuspend,
      error,
      submitForm,
      logout,
      goBack,
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

.back-button {
  background-color: #95a5a6;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.back-button:hover {
  background-color: #7f8c8d;
}

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
