<template>
<div>
  <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
  <div class="yield-ownership-container">
    <div class="yield-ownership-form">
      <h2>Select Store to Yield Ownership</h2>
      <div v-if="stores.length === 0">Loading...</div>
      <div v-else>
        <div class="store-list">
          <div v-for="store in stores" :key="store">
            <label class="store-item">
              <input type="radio" name="selectedStore" :value="store" @change="selectStore(store)">
              {{ store }}
            </label>
          </div>
        </div>
        <!-- <div class="button-group"> -->
          <!-- <button v-if="selectedStore" @click="markStoreForClosure">Mark Store for Closure</button> -->
        <!-- </div> -->
        <div class="button-group">
          <button class="back-button" @click="goBack">Go Back</button>
          <button class="yield-ownership-button" @click="yieldOwnership">Yield Ownership</button>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import UserViewModel from '@/ViewModel/UserViewModel';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';
import { defineComponent, ref, onMounted } from 'vue';


const api_base_url = '/api';
console.log(api_base_url)
export default defineComponent({
   name: 'yieldOwnership',
  components: {
    SiteHeader
  },
  setup() {
    const router = useRouter();
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    var selectedStore = ref(null);
    var stores = ref([]); // Use ref for reactivity

    const toast = useToast();

    const loadStores = async () => {
    try {
        console.log(username)
        console.log(token)
        const response =  await axios.get('http://localhost:8082/api/trading/stores-I-own', {
           params: {
            userName: username,
            token: token,
          }
        });
        console.log(response.data);
        stores = response.data.substring(1,response.data.length-1).split(',');
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
      }  
    };    

    onMounted(() => {
      loadStores(); // Load stores when component is mounted
    });

  const logout = () => {
      UserViewModel.actions.logout();
      router.push('/login');
    };
    const selectStore = (storeName) => {
      selectedStore = storeName;
    };
    const goBack = () => {
      router.push('/');
    };

    const yieldOwnership = () => {
      if (!selectedStore) {
        alert('Please select a store.');
        return;
      }

      try {
        axios.put(`${api_base_url}/stores/${selectedStore}/waiverOnOwnership`);
        alert('Store marked for closure successfully!');
        this.markedStore = this.stores.find(store => store.id === selectedStore);
      } catch (error) {
        console.error('Error marking store for closure:', error);
        alert('Failed to mark store for closure.');
      }

    };

    return {
      stores,
      selectedStore,
      username,
      token,
      logout,
      selectStore,
      goBack,
      yieldOwnership,
      loadStores
    };
  }
});
</script>

<style scoped>
.yield-ownership-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: #f9f9f9;
}

.yield-ownership-form {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  text-align: left; /* Align form content to the left */
}

.yield-ownership-form h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.store-list {
  display: grid;
  gap: 10px;
  grid-template-columns: 1fr;
}

.store-item {
  display: flex;
  align-items: center;
}

.store-item input[type="radio"] {
  margin-right: 10px;
}

.button-group {
  display: flex;
  justify-content: space-between; /* Adjust as needed */
  margin-top: 20px;
}

.button-group button {
  background-color: #e67e22;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.button-group button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.button-group button:hover {
  background-color: #d35400;
}

.back-button {
  background-color: #95a5a6;
}

.back-button:hover {
  background-color: #7f8c8d;
}

.yield-ownership-button {
  background-color: #3498db;
}

.yield-ownership-button.disabled {
  background-color: #ccc;
}

.yield-ownership-button:hover {
  background-color: #2980b9;
}
</style>