<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>Search Results for "{{ $route.query.name }}"</h1>
      <div v-if="stores.length">
        <ul>
          <li v-for="store in stores" :key="store.id" class="store-item">
            <img :src="store.image" alt="Store Image" class="store-image">
            <div class="store-details">
              <h3>{{ store.name }}</h3>
              <p>{{ store.description }}</p>
              <PrimeButton label="View Products" @click="viewProducts(store.id)" class="view-products-button"/>
            </div>
          </li>
        </ul>
      </div>
      <div v-else>
        <p>No stores found with the name "{{ $route.query.name }}".</p>
      </div>
    </div>
    <PrimeToast ref="toast" />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import PrimeToast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'StoreSearchResults',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const username = ref(localStorage.getItem('username') || '');
    const stores = ref([]);

    onMounted(() => {
      const storeName = router.currentRoute.value.query.name;
      fetchStores(storeName);
    });

    const fetchStores = async (storeName) => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/stores/search/name', {
          params: {
            name: storeName,
            userName: username.value,
            token: localStorage.getItem('token')
          }
        });
        toast.add({ severity: 'error', summary: 'Error', detail: response.data || 'Failed to load stores', life: 3000 });
        stores.value = response.data;
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
        console.error('Error fetching stores:', error);
      }
    };

    const viewProducts = (storeId) => {
      router.push({ name: 'StoreDetails', params: { storeId } });
    };

    return {
      username,
      stores,
      viewProducts,
    };
  }
});
</script>

<style scoped>
.main-content {
  padding: 20px;
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
</style>
