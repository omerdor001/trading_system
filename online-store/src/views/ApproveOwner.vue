<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div>
    <h2>Approve Owner Requests</h2>
    <div v-if="approvals.length === 0">No approvals needed.</div>
    <div v-else>
      <table>
        <thead>
          <tr>
            <th>Store</th>
            <th>Appointer</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(approval, index) in approvals" :key="index">
            <td>{{ approval.store }}</td>
            <td>{{ approval.appointer }}</td>
            <td>
              <button @click="approveOwner(approval.store, approval.appointer)">Approve</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
// import { Button as PrimeButton } from 'primevue/button';
// import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';

export default defineComponent({
  name: 'ApproveOwner',
  components: {
    SiteHeader,
    // PrimeButton,
    // PrimeToast,
  },
  setup() {
    const router = useRouter();
    const loading = ref(false);
    const error = ref(null);
    const requests = ref([]);
    const username=localStorage.getItem('username'); 
    const token = localStorage.getItem('token'); 
    const approvals = ref([]);
    const toast = useToast();


    const fetchRequests = async () => {
      try {
        const response = await axios.get(`http://localhost:8082/api/trading/requests-for-ownership`,{
             params : { 
              username: username,
              token: token,
            }
          });
          approvals.value = Object.entries(response.data).map(([key, value]) => {
          return { store: key, appointer: value };
        });
        } catch (err) {
        error.value = err.response?.data?.message || 'An error occurred';
      }
    };

    const approveOwner = async (store, appointer) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/approveOwner', null, {
          params: {
            newOwner: username,
            token: token,
            storeName : store,
            appoint: appointer
          }});
        showSuccessToast(response.data.message);
        // Remove the approved request from the list
      } catch (err) {
        showErrorToast(err.response?.data?.message || 'An error occurred');
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    const showSuccessToast = (message) => {
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: message,
        life: 3000,
      });
    };

    const showErrorToast = (message) => {
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
      loading,
      error,
      requests,
      approvals,
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
  width: 800px; /* Adjust width as needed */
  margin: 0 auto;
  text-align: center;
}

.approve-owner h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.owner-requests {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.owner-requests th,
.owner-requests td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.owner-requests th {
  background-color: #f2f2f2;
}

.p-error {
  margin-top: 20px;
  color: red;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

thead th, tbody td {
  padding: 10px;
  text-align: center;
  border: 1px solid #ddd;
}
thead th {
  background-color: #f2f2f2;
}

button {
  padding: 5px 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
  font-size: 14px;
  border-radius: 5px;
}
button:hover {
  background-color: #45a049;
}
</style>
