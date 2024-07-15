
<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="approve-manage">
      <h2>Approve Manager Requests</h2>
      <div v-if="requests.length === 0 && !loading && !error && !managerLoading">
        <p>No requests found.</p>
      </div>
      <div v-if="requests.length > 0">
        <table class="manager-requests">
          <thead>
            <tr>
              <th>Store Name</th>
              <th>Appointer</th>
              <th>Watch</th>
              <th>Edit Supply</th>
              <th>Edit Buy Policy</th>
              <th>Edit Discount Policy</th>
              <th>Accept Bids</th>
              <th>Approve</th>
              <th>Reject</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(request, index) in requests" :key="index">
              <td>{{ request.storeName }}</td>
              <td>{{ request.appointee }}</td>
              <td>{{ request.watch ? '✔' : '✘' }}</td>
              <td>{{ request.editSupply ? '✔' : '✘' }}</td>
              <td>{{ request.editBuyPolicy ? '✔' : '✘' }}</td>
              <td>{{ request.editDiscountPolicy ? '✔' : '✘' }}</td>
              <td>{{ request.acceptBids ? '✔' : '✘' }}</td>
              <td>
                <PrimeButton label="Approve" type="button" @click="approveManager(request)" />
              </td>
              <td>
                <PrimeButton label="Reject" type="button" @click="rejectManager(request)" />
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

export default defineComponent({
  name: 'ApproveManager',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast,
  },
  setup() {
    const loading = ref(false);
    const error = ref(null);
    const requests = ref([]);
    const toast = ref(null);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const isLoggedIn = !!username;

    const fetchRequests = async () => {
      try {
        loading.value = true;
        const response = await axios.get('http://localhost:8082/api/trading/requests-for-management', {
          params: {
            username: username,
            token: token,
          },
        });
        requests.value = response.data.map(request => ({
          storeName: request.storeName,
          appointee: request.appointee,
          watch: request.watch,
          editSupply: request.editSupply,
          editBuyPolicy: request.editBuyPolicy,
          editDiscountPolicy: request.editDiscountPolicy,
          acceptBids: request.acceptBids
        }));
        console.log(requests.value);
        loading.value = false;
      } catch (err) {
        loading.value = false;
        error.value = err.response?.data?.message || 'An error occurred';
      }
    };

    const approveManager = async (request) => {
      try {
        loading.value = true;
        const response = await axios.post('http://localhost:8082/api/trading/approveManage', null, {
          params: {
            newManager: username,
            token: token,
            store_name_id: request.storeName,
            appoint: request.appointee,
            watch: request.watch,
            editSupply: request.editSupply,
            editBuyPolicy: request.editBuyPolicy,
            editDiscountPolicy: request.editDiscountPolicy,
            acceptBids: request.acceptBids
          }
        });
        showSuccessToast(response.data.message);
        requests.value = requests.value.filter(req => req !== request);
        loading.value = false;
      } catch (err) {
        loading.value = false;
        showErrorToast(err.response?.data?.message || 'An error occurred');
      }
    };

    const rejectManager = async (request) => {
      try {
        loading.value = true;
        const response = await axios.post('http://localhost:8082/api/trading/rejectToManageStore', null, {
          params: {
            username: username,
            token: token,
            store_name_id: request.storeName,
            appoint: request.appointee,
          }
        });
        showSuccessToast(response.data.message);
        requests.value = requests.value.filter(req => req !== request);
        loading.value = false;
      } catch (err) {
        loading.value = false;
        showErrorToast(err.response?.data?.message || 'An error occurred');
      }
    };

    const showSuccessToast = (message) => {
      toast.value.add({
        severity: 'success',
        summary: 'Success',
        detail: message,
        life: 3000,
      });
    };

    const showErrorToast = (message) => {
      toast.value.add({
        severity: 'error',
        summary: 'Error',
        detail: message,
        life: 5000,
      });
    };

    onMounted(fetchRequests);

    return {
      isLoggedIn,
      username,
      loading,
      error,
      requests,
      approveManager,
      rejectManager,
      toast,
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
  width: 800px; /* Adjust width as needed */
  margin: 0 auto;
  text-align: center;
}

.approve-manage h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.manager-requests {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.manager-requests th,
.manager-requests td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.manager-requests th {
  background-color: #f2f2f2;
}

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
