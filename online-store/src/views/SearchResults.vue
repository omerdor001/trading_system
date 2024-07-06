<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>Search Results</h1>
      <div v-if="products.length">
        <ul>
          <li v-for="product in products" :key="product.id" class="product-item">
            <img :src="product.image" alt="Product Image" class="product-image">
            <div class="product-details">
              <h4>{{ product.name }}</h4>
              <p>{{ product.description }}</p>
              <p class="price">{{ product.price }}</p>
              <PrimeButton label="View Options" @click="viewOptions(product.id)" class="view-options-button"/>
            </div>
          </li>
        </ul>
      </div>
      <div v-else>
        <p>No products found.</p>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import PrimeButton from 'primevue/button';
import axios from 'axios';

export default defineComponent({
  name: 'SearchResults',
  components: {
    SiteHeader,
    PrimeButton
  },
  setup() {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const products = ref([]);

    onMounted(() => {
      const filters = router.currentRoute.value.query;
      fetchProducts(filters);
    });

    const fetchProducts = async (filters) => {
      try {
        const response = await axios.get('/api/products', { params: filters });
        products.value = response.data;
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    };

    const viewOptions = (productId) => {
      router.push({ name: 'ProductDetails', params: { productId } });
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      products,
      viewOptions,
      logout
    };
  }
});
</script>

<style scoped>
.main-content {
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
</style>
