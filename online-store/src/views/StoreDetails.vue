<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="store-details">
      <h2>{{ store.name }}</h2>
      <p>{{ store.description }}</p>

      <h3>Products</h3>
      <ul>
        <li v-for="product in store.products" :key="product.id">
          {{ product.name }} - {{ product.price }}
        </li>
      </ul>

      <h3>Purchase History</h3>
      <ul>
        <li v-for="purchase in store.purchaseHistory" :key="purchase.id">
          {{ purchase.date }} - {{ purchase.amount }} - {{ purchase.buyer }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';

export default defineComponent({
  name: 'StoreDetails',
  components: {
    SiteHeader
  },
  props: {
    storeId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const username = ref(localStorage.getItem('username') || '');
    const store = ref({
      name: '',
      description: '',
      products: [],
      purchaseHistory: []
    });

    onMounted(() => {
      // Fetch store details based on the store ID from the props
      // Use the storeId from props directly
      fetchStoreDetails(props.storeId);
    });

    const fetchStoreDetails = (storeId) => {
      // Example data fetching (replace with real API call)
      // This is where you would make an API call to fetch the store details based on the storeId
      store.value = {
        name: `Store ${storeId}`,
        description: 'This is an example store description.',
        products: [
          { id: 1, name: 'Product 1', price: '$10' },
          { id: 2, name: 'Product 2', price: '$20' }
        ],
        purchaseHistory: [
          { id: 1, date: '2024-01-01', amount: '$10', buyer: 'Buyer 1' },
          { id: 2, date: '2024-02-01', amount: '$20', buyer: 'Buyer 2' }
        ]
      };
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      this.$router.push('/login');
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
