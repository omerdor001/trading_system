<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="displayName" />
    <div class="main-content">
      <h2>Purchase History for {{ displayName }}</h2>
      <PrimeDataTable :value="processedProducts" class="products-table">
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
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';

export default {
  name: 'PurchaseHistoryForCustomer',
  components: {
    PrimeDataTable,
    PrimeColumn,
    SiteHeader,
  },
  setup() {
    const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true');
    const userName = ref(localStorage.getItem('username') || 'Guest');
    const token = ref(localStorage.getItem('token') || '');
    const allProducts = ref([]);
    const toast = useToast();

    const storeName = ref(''); // Add this line

    const fetchPurchaseHistory = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/purchase/history/customer', {
          params: {
            userName: userName.value,
            token: token.value,
            customerUserName: userName.value,
            storeName: storeName.value, // Add this line
          },
        });
        allProducts.value = response.data;
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch purchases' });
      }
    };

    const processedProducts = computed(() => {
      return allProducts.value.map(product => {
        let customUsername = product.customUsername;
        if (customUsername.startsWith('r')) {
          customUsername = customUsername.substring(1);  // Remove the first character 'r'
        } else if (customUsername.startsWith('v')) {
          customUsername = 'Guest';  // Change any username starting with 'v' to 'Guest'
        }
        return { ...product, customUsername };
      });
    });

    const displayName = computed(() => {
      if (userName.value.startsWith('r')) {
        return userName.value.substring(1);  // Removes the first character 'r'
      } else if (userName.value.startsWith('v')) {
        return 'Guest';  // Changes any username starting with 'v' to 'Guest'
      }
      return userName.value;  // Default case if no specific rules are matched
    });

    onMounted(() => {
      fetchPurchaseHistory();
    });

    return {
      allProducts,
      processedProducts,
      displayName,
      token,
      isLoggedIn,
      userName,
      storeName, // Add this line
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
