<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="close-store-container">
      <div class="close-store-form">
        <h2>Close Store</h2>
        <form @submit.prevent="handleCloseStorePer">
          <div class="form-group">
            <label for="store">Select Store</label>
            <PrimeDropdown v-model="selectedStore" :options="storeOptions" optionLabel="label" optionValue="value" id="store" placeholder="Select a Store" required />
          </div>
          <div class="button-group">
            <Button type="submit" label="Close Permanently" class="close-button" />
          </div>
        </form>
      </div>
    </div>
    <PrimeToast ref="toast" />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import Button from 'primevue/button';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';
import PrimeDropdown from 'primevue/dropdown';

export default defineComponent({
  name: 'CloseStore',
  components: {
    SiteHeader,
    Button,
    PrimeDropdown,
    PrimeToast,
  },
  setup() {
    const router = useRouter();
    const stores = ref([]);
    const storeOptions = ref([]);
    const selectedStore = ref('');
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();
    const loading = ref(true);

    onMounted(async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/stores-I-created', {
          params: {
            userName: username,
            token: token,
          },
        });
        const storeArray = response.data.split(',');
        stores.value = storeArray;
        storeOptions.value = storeArray.map(store => ({ label: store, value: store }));
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
      } finally {
        loading.value = false;
      }
    });

    const handleCloseStorePer = async () => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/store/close', null, {
          params: { username, token, storeName: selectedStore.value },
        });
        console.log(response.data);
        toast.add({ severity: 'success', summary: 'Success', detail: 'Store was closed successfully', life: 3000 });
        stores.value = stores.value.filter(store => store !== selectedStore.value);
        storeOptions.value = storeOptions.value.filter(option => option.value !== selectedStore.value);
        selectedStore.value = '';
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to close store', life: 3000 });
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push('/login');
    };

    return {
      stores,
      storeOptions,
      selectedStore,
      username,
      token,
      handleCloseStorePer,
      logout,
      loading,
    };
  },
});
</script>

<style scoped>
.close-store-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: #f9f9f9;
}

.close-store-form {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  text-align: center;
}

.close-store-form h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  display: block;
  color: #333;
  margin-bottom: 5px;
}

.button-group {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.close-button {
  background-color: #e67e22;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.close-button:hover {
  background-color: #d35400;
}
</style>
