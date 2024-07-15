<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" />
    <div class="main-content">
      <h2>Purchase History</h2>
      <PrimeDataTable :value="allProducts" class="products-table">
        <PrimeColumn field="productId" header="Product ID" />
        <PrimeColumn field="price" header="Price" />
        <PrimeColumn field="quantity" header="Quantity" />
        <PrimeColumn field="category" header="Category" />
        <PrimeColumn field="storeName" header="Store Name" />
        <PrimeColumn field="customUsername" header="Customer" />
        <PrimeColumn field="totalPrice" header="Total Price" />
      </PrimeDataTable>
    </div>
    <PrimeToast />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';

export default {
  name: 'PurchaseHistory',
  components: {
    PrimeDataTable,
    PrimeColumn,
    SiteHeader,
  },
  setup() {
    const username = ref(localStorage.getItem('username') || '');
    const token = ref(localStorage.getItem('token') || '');
    const allProducts = ref([]);
    const toast = useToast();

    const fetchPurchaseHistory = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/purchaseHistory', {
          params: {
            username: username.value,
            token: token.value,
          },
        });
        allProducts.value = response.data;
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch purchases' });
      }
    };

    onMounted(() => {
      fetchPurchaseHistory();
    });

    return {
      allProducts,
      username,
      token,
    };
  },
};
</script>

<style scoped>
.main-content {
  padding: 20px;
}

.products-table {
  margin-top: 20px;
}
</style>
