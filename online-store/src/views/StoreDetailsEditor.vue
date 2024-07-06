<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="store-details">
      <h2>{{ store.name }}</h2>
      <p>{{ store.description }}</p>

      <h3>Products</h3>
      <DataTable :value="store.products">
        <Column field="name" header="Product Name" />
        <Column field="price" header="Price" />
      </DataTable>

      <h3>Purchase History</h3>
      <DataTable :value="store.purchaseHistory">
        <Column field="date" header="Date" />
        <Column field="amount" header="Amount" />
        <Column field="buyer" header="Buyer" />
      </DataTable>
    </div>
  </div>
</template>

<script>
import {defineComponent, ref, onMounted} from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import {useRouter} from 'vue-router';

export default defineComponent({
  name: 'StoreDetailsEditor',
  components: {
    SiteHeader,
    DataTable,
    Column
  },
  props: {
    storeId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const store = ref({
      name: '',
      description: '',
      products: [],
      purchaseHistory: []
    });

    onMounted(() => {
      // Fetch store details based on the store ID from the props
      fetchStoreDetails(props.storeId);
    });

    const fetchStoreDetails = (storeId) => {
      // Example data fetching (replace with real API call)
      store.value = {
        name: `Store ${storeId}`,
        description: 'This is an example store description.',
        products: [
          {id: 1, name: 'Product 1', price: '$10'},
          {id: 2, name: 'Product 2', price: '$20'}
        ],
        purchaseHistory: [
          {id: 1, date: '2024-01-01', amount: '$10', buyer: 'Buyer 1'},
          {id: 2, date: '2024-02-01', amount: '$20', buyer: 'Buyer 2'}
        ]
      };
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      store,
      logout
    };
  }
});
</script>

<style scoped>
.store-details {
  padding: 20px;
}
</style>
