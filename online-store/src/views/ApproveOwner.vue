<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="approve-owner">
      <h2>Approve Owner</h2>
      <div v-if="!loading && !error && !ownersLoading">
        <div class="p-field">
          <label for="newOwner">New Owner: </label>
          <p>{{ newOwnerLabel }}</p>
        </div>
        <div class="p-field">
          <label for="storeName">Store Name: </label>
          <p>{{ storeNameLabel }}</p>
        </div>
        <div class="p-field">
          <label for="appoint">Appointer: </label>
          <p>{{ appointLabel }}</p>
        </div>
        <div class="button-group">
          <PrimeButton label="Approve" type="button" class="submit-button" @click="approveOwner" />
        </div>
      </div>
      <div v-if="loading || ownersLoading">
        <p>Loading...</p>
      </div>
      <div v-if="error">
        <p class="p-error">{{ error }}</p>
      </div>
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
  name: 'ApproveOwner',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast,
  },
  props: {
    newOwnerLabel: String,
    storeNameLabel: String,
    appointLabel: String,
  },
  setup(props) {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const isLoggedIn = ref(!!username.value);
    const loading = ref(false);
    //const ownersLoading = ref(false);
    const error = ref(null);

    const approveOwner = async () => {
      try {
        loading.value = true;
        const response = await axios.post('/api/approve-owner', {
          newOwner: props.newOwnerLabel,
          storeName: props.storeNameLabel,
          appoint: props.appointLabel,
        });
        showSuccessToast(response.data.message);
        loading.value = false;
      } catch (err) {
        loading.value = false;
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
      loading,
      error,
      approveOwner,
      logout,
    };
  },
});
</script>

<style scoped>
.approve-owner {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.approve-owner h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.approve-owner .p-field {
  margin-bottom: 15px;
}

.approve-owner label {
  font-weight: bold;
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
