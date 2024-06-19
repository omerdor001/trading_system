<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="appoint-owner">
      <h2>Appoint Owner</h2>
      <form @submit.prevent="suggestManage">
        <div class="p-field">
          <label for="newOwner">New Owner: </label>
          <InputText id="newOwner" v-model="newOwner" required />
        </div>
        <div class="p-field">
          <label for="storeName">Store Name: </label>
          <InputText id="storeName" v-model="storeName" required />
        </div>
        <div class="button-group">
          <PrimeButton label="Appoint Owner" type="submit" class="submit-button" />
        </div>
      </form>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'AppointOwner',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
    PrimeToast,
  },
  setup() {
    const router = useRouter();
    const newOwner = ref('');
    const storeName = ref('');
    const username = ref(localStorage.getItem('username') || '');
    const isLoggedIn = ref(!!username.value);

    const suggestManage = async () => {
      try {
        const response = await axios.post('/api/appoint-owner', {
          newOwner: newOwner.value,
          storeName: storeName.value,
        });
        showSuccessToast(response.data.message);
        newOwner.value = '';
        storeName.value = '';

      } catch (err) {
        showErrorToast(err.response?.data?.message || 'An error occurred');
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    const showSuccessToast = (message) => {
      const toast = ref.$refs.toast;
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: message,
        life: 3000,
      });
    };

    const showErrorToast = (message) => {
      const toast = ref.$refs.toast;
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: message,
        life: 5000,
      });
    };

    return {
      username,
      isLoggedIn,
      newOwner,
      storeName,
      suggestManage,
      logout,
    };
  },
});
</script>

<style scoped>
.appoint-owner {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.appoint-owner h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.appoint-owner .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
  justify-content: center;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-button {
  background-color: #e67e22 !important;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.submit-button:hover {
  background-color: #d35400 !important;
}

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
