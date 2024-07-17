<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>Search Results for "{{ searchQuery }}"</h1>
      <div v-if="filteredStores.length === 0">
        <p>No stores found with the name "{{ searchQuery }}".</p>
      </div>
      <div v-else>
        <ul class="stores-list">
          <li v-for="store in filteredStores" :key="store.name" class="store-item">
            <div class="store-details">
              <h3>Store Name: {{ store.name }}</h3>
              <p>Description: {{ store.description }}</p>
              <p>Rating: {{ store.rating }}</p>
              <PrimeButton label="View Products" @click="viewProducts(store.name)" class="view-products-button" />
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
import {ref, onMounted, computed} from 'vue';
import { useRouter, useRoute } from 'vue-router';

export default {
  name: 'SearchResultPage',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast
  },
  setup() {
    const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true');
    const stores = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const toast = useToast();
    const router = useRouter();
    const route = useRoute();
    const searchQuery = ref(route.query.name || '');

    const fetchStores = async () => {
      const token = localStorage.getItem('token');

      if (!token) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invalid token was supplied', life: 3000 });
        return;
      }

      try {
        const response = await axios.get('http://localhost:8082/api/trading/stores-detailed-info', {
          params: {
            username: username.value,
            token: token
          }
        });
        stores.value = response.data.map(store => ({
          name: store.name,
          description: store.description,
          rating: store.rating
        }));
      } catch (error) {
        if (error.response && error.response.data) {
          toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
        } else {
          toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to load stores', life: 3000 });
        }
      }
    };

    const filteredStores = computed(() => {
      return stores.value.filter(store => store.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
    });

    const viewProducts = (storeId) => {
      router.push({ name: 'StoreDetails', params: { storeId } });
    };

    onMounted(() => {
      fetchStores();
    });

    return {
      isLoggedIn,
      stores,
      username,
      fetchStores,
      filteredStores,
      viewProducts,
      searchQuery
    };
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

.store-details {
  display: flex;
  flex-direction: column;
}

.view-products-button {
  margin-top: 10px;
}
</style>
