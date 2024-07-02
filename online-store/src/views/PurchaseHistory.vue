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
  </div>
</template>

<script>
import { ref } from 'vue';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import SiteHeader from '@/components/SiteHeader.vue';

export default {
  name: 'PurchaseHistory',
  components: {
    Button,
    DataTable,
    Column,
    SiteHeader,
  },
  setup() {
    const stores = ref([
      {
        id: 1,
        name: 'Store 1',
        description: 'Description for Store 1',
        purchasedProducts: [
          { name: 'Product 1', price: '$10', quantity: 1, total: '$10', address: '123 Main St', purchaseTime: '2024-01-01 10:00', paymentInfo: 'Paid with Visa' },
          { name: 'Product 2', price: '$20', quantity: 2, total: '$40', address: '123 Main St', purchaseTime: '2024-02-01 11:00', paymentInfo: 'Paid with MasterCard' }
        ]
      },
      {
        id: 2,
        name: 'Store 2',
        description: 'Description for Store 2',
        purchasedProducts: [
          { name: 'Product 3', price: '$30', quantity: 1, total: '$30', address: '456 Elm St', purchaseTime: '2024-03-01 12:00', paymentInfo: 'Paid with PayPal' },
          { name: 'Product 4', price: '$40', quantity: 3, total: '$120', address: '456 Elm St', purchaseTime: '2024-04-01 13:00', paymentInfo: 'Paid with American Express' }
        ]
      }
    ]);

    const selectedStoreId = ref(null);
    const username = ref(localStorage.getItem('username') || '');

    const toggleProducts = (storeId) => {
      if (selectedStoreId.value === storeId) {
        selectedStoreId.value = null;
      } else {
        selectedStoreId.value = storeId;
      }
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      this.$router.push('/login');
    };

    return {
      stores,
      selectedStoreId,
      toggleProducts,
      username,
      logout
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
