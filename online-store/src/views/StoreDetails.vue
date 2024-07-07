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
                <PrimeButton label="View Options" @click="viewOptions(product.id)" class="view-options-button"/>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <PrimeToast ref="toast" />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import PrimeButton from 'primevue/button';
import PrimeToast from 'primevue/toast';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';

export default defineComponent({
  name: 'StoreDetails',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast
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
      products: []
    });
    const toast = useToast();

    onMounted(() => {
      fetchStoreDetails(props.storeId);
    });

    const fetchStoreDetails = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invalid token was supplied', life: 3000 });
        return;
      }
      try {
        const response = await axios.get(`http://localhost:8082/api/trading/stores/products_of_store`, {
          params: {
            userName: username.value,
            token: token
          }
        });
        store.value = response.data;
      } catch (error) {
        if (error.response && error.response.data) {
          toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
        } else {
          toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to load store details', life: 3000 });
        }
      }
    };

    const viewOptions = (productId) => {
      router.push({ name: 'ProductDetails', params: { productId } });
    };

    const backToStores = () => {
      router.push({ name: 'HomePage' });
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('token');
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
