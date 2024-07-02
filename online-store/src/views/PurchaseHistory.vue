<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h2>Purchase History</h2>
      <div v-for="store in stores" :key="store.id" class="store">
        <h3>Store: {{ store.name }}</h3>
        <p>{{ store.description }}</p>
        <Button label="View Products" @click="toggleProducts(store.id)" class="view-products-button"/>
        <DataTable v-if="store.id === selectedStoreId" :value="store.purchasedProducts" class="products-table">
          <Column field="name" header="Product Name" />
          <Column field="price" header="Price" />
          <Column field="quantity" header="Quantity" />
          <Column field="total" header="Total" />
          <Column field="address" header="Address" />
          <Column field="purchaseTime" header="Purchase Time" />
          <Column field="paymentInfo" header="Payment Info" />
        </DataTable>
      </div>
    </div>
    <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import PrimeToast from 'primevue/toast';
import SiteHeader from '@/components/SiteHeader.vue';
import axios from 'axios';

export default {
  name: 'PurchaseHistory',
  components: {
    Button,
    DataTable,
    Column,
    PrimeToast,
    SiteHeader,
  },
  setup() {
    const stores = ref([]);
    const selectedStoreId = ref(null);
    const username = ref(localStorage.getItem('username') || '');
    const token = ref(localStorage.getItem('token') || '');
    const toast = ref(null);
    const router = useRouter();

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

    const fetchPurchaseHistory = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/purchase/history', {
          params: {
            username: username.value,
            token: token.value,
            storeName: '', // Pass the store name if needed or remove it from the API
          },
        });

        if (response.status === 200) {
          // Assuming response.data contains the purchase history data
          stores.value = response.data;
          showSuccessToast('Purchase history fetched successfully');
        } else {
          showErrorToast(`Failed to fetch purchase history: ${response.statusText}`);
        }
      } catch (error) {
        showErrorToast(`Error fetching purchase history: ${error.message}`);
      }
    };

    const toggleProducts = (storeId) => {
      if (selectedStoreId.value === storeId) {
        selectedStoreId.value = null;
      } else {
        selectedStoreId.value = storeId;
      }
    };

    const logout = async () => {
      try {
        await axios.post('http://localhost:8082/api/trading/logout', null, {
          params: {
            username: username.value,
            token: token.value
          }
        });
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('username');
        localStorage.removeItem('token');
        router.push('/login');
      } catch (error) {
        showErrorToast(`Error during logout: ${error.message}`);
      }
    };

    onMounted(fetchPurchaseHistory);

    return {
      stores,
      selectedStoreId,
      toggleProducts,
      username,
      logout,
      toast,
    };
  },
};
</script>

<style scoped>
.main-content {
  padding: 20px;
}

.store {
  margin-bottom: 40px;
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 8px;
}

.view-products-button {
  margin-top: 10px;
}

.products-table {
  margin-top: 20px;
}
</style>
