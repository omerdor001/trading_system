<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="displayName" />
    <div class="main-content">
      <h2>Purchase History for Store: {{ storeName }}</h2>
      <PrimeDataTable :value="processedProducts" class="products-table">
        <PrimeColumn field="productId" header="Product ID" />
        <PrimeColumn field="price" header="Price" />
        <PrimeColumn field="quantity" header="Quantity" />
        <PrimeColumn field="category" header="Category" />
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
import { useRouter } from 'vue-router';

export default {
  name: 'PurchaseHistoryForStore',
  components: {
    PrimeDataTable,
    PrimeColumn,
    SiteHeader,
  },
  setup() {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const token = ref(localStorage.getItem('token') || '');
    const storeName = ref(router.currentRoute.value.params.storeName);
    const allProducts = ref([]);
    const toast = useToast();

    const fetchPurchaseHistory = async () => {
      try {
        const response = await axios.get(`http://localhost:8082/api/trading/store/purchaseHistory`, {
          params: {
            username: username.value,
            token: token.value,
            storeName: storeName.value
          },
        });
        const purchaseData = response.data;
        const allProductsList = [];

        purchaseData.forEach(purchase => {
          const productList = JSON.parse(purchase.productInSaleList);
          productList.forEach(product => {
            allProductsList.push({
              ...product,
              customUsername: purchase.customUsername,
              totalPrice: purchase.totalPrice
            });
          });
        });

        allProducts.value = allProductsList;
      } catch (error) {
        toast.add({severity: 'error', summary: 'Error', detail: 'Failed to fetch purchase history'});
      }
    };

    // Computed property to process product usernames
    const processedProducts = computed(() => {
      return allProducts.value.map(product => {
        let customUsername = product.customUsername;
        if (customUsername.startsWith('r')) {
          customUsername = customUsername.substring(1);  // Remove the first character 'r'
        } else if (customUsername.startsWith('v')) {
          customUsername = 'Guest';  // Change any username starting with 'v' to 'Guest'
        }
        return {...product, customUsername};
      });
    });

    const displayName = computed(() => {
      if (username.value.startsWith('r')) {
        return username.value.substring(1);  // Removes the first character 'r'
      } else if (username.value.startsWith('v')) {
        return 'Guest';  // Changes any username starting with 'v' to 'Guest'
      }
      return username.value;  // Default case if no specific rules are matched
    });

    onMounted(() => {
      fetchPurchaseHistory();
    });

    return {
      allProducts,
      processedProducts,
      displayName,
      username,
      token,
      storeName,
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
