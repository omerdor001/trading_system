<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h2>Purchase History</h2>
      <div v-for="store in stores" :key="store.storeName" class="store">
        <h3>Store: {{ store.storeName }}</h3>
        <p>{{ store.description }}</p>
        <PrimeButton label="View Products" @click="openModal(store)" class="view-products-button"/>
      </div>
    </div>

    <Dialog header="Purchased Products" v-model:visible="isModalVisible" :modal="true" :style="{ width: '50vw' }" :closable="true">
      <PrimeDataTable :value="selectedStoreProducts" class="products-table">
        <PrimeColumn field="productId" header="Product ID" />
        <PrimeColumn field="price" header="Price" />
        <PrimeColumn field="quantity" header="Quantity" />
        <PrimeColumn field="category" header="Category" />
      </PrimeDataTable>
    </Dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import PrimeButton from 'primevue/button';
import PrimeDataTable from 'primevue/datatable';
import PrimeColumn from 'primevue/column';
import Dialog from 'primevue/dialog';
import SiteHeader from '@/components/SiteHeader.vue';

export default {
  name: 'PurchaseHistory',
  components: {
    PrimeButton,
    PrimeDataTable,
    PrimeColumn,
    Dialog,
    SiteHeader,
  },
  setup() {
    const isModalVisible = ref(false);
    const selectedStoreProducts = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const stores = ref([]);

    const fetchPurchaseHistory = async () => {
      try {
        const response = await axios.get('/api/purchase-history'); 
        const purchaseData = response.data;
        const storeMap = new Map();
        purchaseData.forEach(purchase => {
          if (!storeMap.has(purchase.storeName)) {
            storeMap.set(purchase.storeName, {
              storeName: purchase.storeName,
              description: '', // Add store description if available
              productInSaleList: []
            });
          }
          const store = storeMap.get(purchase.storeName);
          store.productInSaleList.push(...JSON.parse(purchase.productInSaleList));
        });

        stores.value = Array.from(storeMap.values());
      } catch (error) {
        console.error('Error fetching purchase history:', error);
      }
    };

    const openModal = (store) => {
      selectedStoreProducts.value = store.productInSaleList;
      isModalVisible.value = true;
    };

    onMounted(() => {
      fetchPurchaseHistory();
    });

    return {
      stores,
      isModalVisible,
      selectedStoreProducts,
      openModal,
      username,
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