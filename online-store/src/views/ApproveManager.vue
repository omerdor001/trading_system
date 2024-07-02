<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="approve-manage">
      <h2>Approve Manager</h2>
      <form @submit.prevent="approveManager">
        <div class="p-field">
          <label for="newManager">New Manager: </label>
          <span>{{ newManager }}</span>
        </div>
        <div class="p-field">
          <label for="storeNameId">Store Name: </label>
          <span>{{ storeNameId }}</span>
        </div>
        <div class="p-field">
          <label for="appointUser">Appointed By: </label>
          <span>{{ appoint }}</span>
        </div>
        <div class="button-group">
          <PrimeButton label="Approve" type="submit" class="submit-button" />
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
import { Button as PrimeButton } from 'primevue/button';
import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ApproveManager',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast,
  },
  setup() {
    const router = useRouter();
    const newManager = ref('');
    const storeNameId = ref('');
    const appoint = ref('');
    const username = ref(localStorage.getItem('username') || '');
    const isLoggedIn = ref(!!username.value);

    const approveManager = async () => {
      try {
        const response = await axios.post('/api/approve-manage', {
          newManager: newManager.value,
          storeNameId: storeNameId.value,
          appoint: appoint.value,
        });
        showSuccessToast(response.data.message);
        resetForm();

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

    const resetForm = () => {
      newManager.value = '';
      storeNameId.value = '';
      appoint.value = '';
    };

    return {
      username,
      isLoggedIn,
      newManager,
      storeNameId,
      appoint,
      approveManager,
      logout,
    };
  },
});
</script>

<style scoped>
.approve-manage {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.approve-manage h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.approve-manage .p-field {
  margin-bottom: 15px;
}

.approve-manage label {
  font-weight: bold;
}

.button-group {
  display: flex;
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
