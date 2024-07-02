<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Back to Stores" @click="backToStores" class="sidebar-button" />
      </div>
      <div class="content">
        <div class="store-details">
          <h2>{{ store.name }}</h2>
          <p>{{ store.description }}</p>

          <h3>Products</h3>
          <ul>
            <li v-for="product in store.products" :key="product.id" class="product-item">
              <img :src="product.image" alt="Product Image" class="product-image">
              <div class="product-details">
                <h4>{{ product.name }}</h4>
                <p>{{ product.description }}</p>
                <p class="price">{{ product.price }}</p>
                <PrimeButton label="View Options" @click="viewOptions(product.id, store.name)"
                  class="view-options-button" />
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';

export default defineComponent({
  name: 'StoreDetails',
  components: {
    SiteHeader,
    PrimeButton
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
      fetchStoreDetails(props.storeId);
    });

    const fetchStoreDetails = (storeId) => {
      store.value = {
        name: `Store ${storeId}`,
        description: 'This is an example store description.',
        products: [
          { id: 1, name: 'Smartwatch Pro', description: 'Water resistant, Built-in GPS', price: '$90', image: 'https://via.placeholder.com/150' },
          { id: 2, name: 'TechZone Tablet', description: '10-inch display, Wi-Fi enabled', price: '$200', image: 'https://via.placeholder.com/150' }
        ],
        purchaseHistory: [
          { id: 1, date: '2024-01-01', amount: '$90', buyer: 'Buyer 1' },
          { id: 2, date: '2024-02-01', amount: '$200', buyer: 'Buyer 2' }
        ]
      };
    };

    const viewOptions = (productId, storeName) => {
      router.push({ name: 'ProductDetails', params: { storeName, productId } });
    };

    const backToStores = () => {
      router.push({ name: 'HomePage' });
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      store,
      viewOptions,
      backToStores,
      logout
    };
  }
});
</script>

<style scoped>
.main-content {
  display: flex;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
}

.content {
  flex: 2;
  padding: 20px;
}

.store-details {
  padding: 20px;
}

.product-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.product-image {
  width: 150px;
  height: 150px;
  margin-right: 10px;
}

.product-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.price {
  font-weight: bold;
  margin-top: 5px;
}

.sidebar-button {
  width: 100%;
  max-width: 150px;
}
</style>
