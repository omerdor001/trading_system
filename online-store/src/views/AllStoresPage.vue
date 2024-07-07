<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>All Stores</h1>
      <div v-if="stores.length === 0">
        <p>No stores found.</p>
      </div>
      <div v-else>
        <ul>
          <li v-for="store in stores" :key="store.id" class="store-item">
            <img :src="store.image" alt="Store Image" class="store-image">
            <div class="store-details">
              <h3>{{ store.name }}</h3>
              <p>{{ store.description }}</p>
              <p>Rating: {{ store.rating }}</p>
              <PrimeButton label="View Products" @click="viewProducts(store.id)" class="view-products-button"/>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <PrimeToast ref="toast" />
  </div>
</template>

<script>
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import PrimeToast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';

export default {
  name: 'AllStoresPage',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast
  },
  data() {
    return {
      stores: [],
      username: localStorage.getItem('username') || ''
    };
  },
  created() {
    this.fetchStores();
  },
  methods: {
    async fetchStores() {
      const toast = useToast();
      const token = localStorage.getItem('token');
      const username = localStorage.getItem('username');
      if (!token) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invalid token was supplied', life: 3000 });
        return;
      }

      try {
        const response = await axios.get('http://localhost:8082/api/trading/stores', {
          params: {
            userName: username,
            token: token
          }
        });
        this.stores = response.data;
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
      }
    },
    viewProducts(storeId) {
      this.$router.push({ name: 'StoreDetails', params: { storeId } });
    },
    logout() {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
.main-content {
  padding: 20px;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  display: flex;
  align-items: center;
}

.store-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.store-image {
  width: 150px;
  height: 150px;
  margin-right: 10px;
}

.store-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.view-products-button {
  margin-top: 10px;
}
</style>
