<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="approve-manage">
      <h2>Approve Manager Requests</h2>
      <div v-if="requests.length === 0 && !loading && !error && !managersLoading">
        <p>No requests found.</p>
      </div>
      <div v-if="requests.length > 0">
        <table class="manager-requests">
          <thead>
            <tr>
              <th>New Manager</th>
              <th>Store Name</th>
              <th>Appointer</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(request, index) in requests" :key="index">
              <td>{{ request.newManager }}</td>
              <td>{{ request.storeName }}</td>
              <td>{{ request.appointer }}</td> <!-- Assuming the backend returns 'appointer' instead of 'appoint' -->
              <td>
                <PrimeButton label="Approve" type="button" @click="approveManager(request)" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div v-if="loading || managerLoading">
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
import { defineComponent, ref, onMounted } from 'vue';
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
    const loading = ref(false);
    const error = ref(null);
    const requests = ref([]);
    const username = localStorage.getItem('username');  
    const token = localStorage.getItem('token'); 

    const fetchRequests = async () => {
      try {
        loading.value = true;
        const response = await axios.get(`http://localhost:8082/api/trading/requests-for-management`,{
             params : { 
              username: username,
              token: token,
            }
          });
        requests.value = response.data; 
        loading.value = false;
      } catch (err) {
        loading.value = false;
        error.value = err.response?.data?.message || 'An error occurred';
      }
    };

    const approveManager = async (request) => {
      try {
        loading.value = true;
        const response = await axios.post('http://localhost:8082/api/trading/approveManager', request);
        showSuccessToast(response.data.message);
        // Remove the approved request from the list
        requests.value = requests.value.filter(req => req !== request);
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

    // Fetch requests on component mount
    onMounted(fetchRequests);

    return {
      username,
      loading,
      error,
      requests,
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
