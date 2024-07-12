<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="p-grid p-justify-center">
      <div class="p-col-8">
        <div class="p-field">
          <label for="requestType">Select Request Type:</label>
          <PrimeDropdown v-model="selectedRequestType" :options="requestTypes" optionLabel="label" @change="fetchRequests" />
        </div>
      </div>
    </div>
    <div class="p-grid p-justify-center">
      <div class="p-col-8">
        <PrimeCard v-if="requests.length === 0 && !loading && !error && !loadingRequests">
          <div class="p-text-center">
            <p>No requests found.</p>
          </div>
        </PrimeCard>
        <PrimeCard v-else>
          <PrimeDataTable :value="requests" :loading="loadingRequests" loadingIcon="pi pi-spin pi-spinner">
            <PrimeColumn field="storeName" header="Store Name"></PrimeColumn>
            <PrimeColumn field="appointee" header="Appointer"></PrimeColumn>
            <PrimeColumn v-if="selectedRequestType === 'manager'" field="watch" header="Watch"></PrimeColumn>
            <PrimeColumn v-if="selectedRequestType === 'manager'" field="editSupply" header="Edit Supply"></PrimeColumn>
            <PrimeColumn v-if="selectedRequestType === 'manager'" field="editBuyPolicy" header="Edit Buy Policy"></PrimeColumn>
            <PrimeColumn v-if="selectedRequestType === 'manager'" field="editDiscountPolicy" header="Edit Discount Policy"></PrimeColumn>
            <PrimeColumn>
              <template #body="slotProps">
                <PrimeButton label="Approve" @click="approveRequest(slotProps.rowData)" class="p-button-success" />
              </template>
            </PrimeColumn>
            <PrimeColumn>
              <template #body="slotProps">
                <PrimeButton label="Reject" @click="rejectRequest(slotProps.rowData)" class="p-button-danger" />
              </template>
            </PrimeColumn>
          </PrimeDataTable>
        </PrimeCard>
        <PrimeToast ref="toast" position="top-right"></PrimeToast>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeDropdown from 'primevue/dropdown';
import PrimeCard from 'primevue/card';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';
import PrimeButton from 'primevue/button';
import PrimeToast from 'primevue/toast';

export default defineComponent({
  name: 'ApproveRoles',
  components: {
    SiteHeader,
    PrimeDropdown,
    PrimeCard,
    PrimeDataTable,
    PrimeColumn,
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
    const selectedRequestType = ref('owner');
    const loadingRequests = ref(false);

    const requestTypes = [
      { label: 'Owner Requests', value: 'owner' },
      { label: 'Manager Requests', value: 'manager' },
    ];

    const fetchRequests = async () => {
      try {
        loadingRequests.value = true;
        let endpoint = '';
        if (selectedRequestType.value === 'owner') {
          endpoint = 'http://localhost:8082/api/trading/requests-for-ownership';
        } else if (selectedRequestType.value === 'manager') {
          endpoint = 'http://localhost:8082/api/trading/requests-for-management';
        }
        const response = await axios.get(endpoint, {
          params: {
            username: username,
            token: token,
          }
        });
        requests.value = response.data.map(request => ({
          storeName: request.storeName,
          appointee: request.appointee,
          watch: request.watch,
          editSupply: request.editSupply,
          editBuyPolicy: request.editBuyPolicy,
          editDiscountPolicy: request.editDiscountPolicy,
        }));
        loadingRequests.value = false;
      } catch (err) {
        loadingRequests.value = false;
        error.value = err.response?.data?.message || 'No requests yet';
      }
    };

    const approveRequest = async (request) => {
      try {
        loading.value = true;
        let endpoint = '';
        if (selectedRequestType.value === 'owner') {
          endpoint = 'http://localhost:8082/api/trading/approveOwner';
        } else if (selectedRequestType.value === 'manager') {
          endpoint = 'http://localhost:8082/api/trading/approveManage';
        }
        const response = await axios.post(endpoint, null, {
          params: {
            newOwner: username,
            token: token,
            storeName: request.storeName,
            appoint: request.appointee,
            watch: request.watch,
            editSupply: request.editSupply,
            editBuyPolicy: request.editBuyPolicy,
            editDiscountPolicy: request.editDiscountPolicy,
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

    const rejectRequest = async (request) => {
      try {
        loading.value = true;
        let endpoint = '';
        if (selectedRequestType.value === 'owner') {
          endpoint = 'http://localhost:8082/api/trading/rejectToOwnStore';
        } else if (selectedRequestType.value === 'manager') {
          endpoint = 'http://localhost:8082/api/trading/rejectToManageStore';
        }
        const response = await axios.post(endpoint, null, {
          params: {
            username: username,
            token: token,
            storeName: request.storeName,
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

    onMounted(() => {
      fetchRequests();
    });

    return {
      isLoggedIn,
      username,
      loading,
      error,
      requests,
      selectedRequestType,
      requestTypes,
      fetchRequests,
      approveRequest,
      rejectRequest,
      toast,
      loadingRequests,
    };
  },
});
</script>

<style scoped>
.p-grid {
  max-width: 1024px;
  margin: 0 auto;
  padding: 20px;
}

.p-col-8 {
  margin-bottom: 20px;
}

.p-field {
  margin-bottom: 20px;
}

.p-text-center {
  text-align: center;
}

.p-button {
  margin-right: 10px;
}

.p-button-success {
  background-color: #4caf50;
  color: #fff;
}

.p-button-danger {
  background-color: #f44336;
  color: #fff;
}

.p-card {
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  margin-bottom: 20px;
}

.p-datatable {
  width: 100%;
}

.p-datatable-header {
  background-color: #f2f2f2;
}

.p-datatable th,
.p-datatable td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}
</style>
