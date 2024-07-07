<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>All Stores</h1>
      <div v-if="stores.length === 0">
        <p>No stores found.</p>
      </div>
      <div v-else>
        <ul class="stores-list">
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
        console.log(response.data);  // Add this line to check the returned data
        this.stores = response.data;
      } catch (error) {
        if (error.response && error.response.data) {
          toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
        } else {
          toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to load stores', life: 3000 });
        }
      }
    },
    viewProducts() {
/*      if (!storeId) {
        console.error('Missing storeId');
        return;
      }*/
      this.$router.push({ name: 'StoreDetails', params: { storeId: 'humberger' } });
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

.stores-list {
  list-style: none;
  padding: 0;
}

.store-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background-color: #f9f9f9;
}

.store-image {
  width: 150px;
  height: 150px;
  margin-right: 20px;
  border-radius: 10px;
}

.store-details {
  display: flex;
  flex-direction: column;
}

.view-products-button {
  margin-top: 10px;
}
</style>
