<template>
<div>
  <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
  <div class="close-store-container">
    <div class="close-store-form">
      <h2>Select Store to Close</h2>
      <div v-if="loading">Loading...</div>
      <div v-else-if="stores.length === 0">No stores found.</div>
      <div v-else>
        <div class="store-list">
          <div v-for="store in stores" :key="store">
            <label class="store-item">
              <input type="radio" name="selectedStore" :value="store" @change="selectStore(store)">
              {{ store }}
            </label>
          </div>
        </div>
        <div class="button-group">
          <button v-if="markedStore" @click="markStoreForClosure">Mark Store for Closure</button>
          <button class="close-button" @click="closeStore">Close Store</button>
          <button class="back-button" @click="goBack">Go Back</button>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';

const api_base_url = '/api';

export default {
  name: 'CloseStore',
  components: {
    SiteHeader
  },
  data() {
    return {
      stores: [],
      loading: true,
      selectedStoreId: '',
      markedStore: null,
      username: localStorage.getItem('username'),
      token: localStorage.getItem('token'),
    };
  },
  created() {
    this.fetchStores();
  },
  methods: {
    async fetchStores() {
      try {
        const response = await axios.get(`${api_base_url}/stores-I-created`, {
          params: {
            userName: this.username,
            token: this.token,
          }
        });
        this.stores = response.data; 
      } catch (error) {
        console.error('Failed to fetch stores:', error);
        alert('Failed to load stores.');
      } finally {
        this.loading = false;
      }
    },
    logout() {
      this.$router.push('/login');
    },
    async selectStore(storeName) {
      this.markedStore = storeName;
    },
    async markStoreForClosure() {
      if (!this.markedStore) {
        alert('Please select a store.');
        return;
      }
      // Mark store for closure logic here, if needed
    },
    async closeStore() {
      try {
        const response = await axios.post(`${api_base_url}/store/close`, null, {
          params: {
            username: this.username,
            token: this.token,
            storeName: this.markedStore
          }
        });
        console.log('Store Closed:', response.data);
        alert('Store closed successfully.');
        this.$router.push('/');
      } catch (error) {
        console.error('Failed to close store:', error);
        alert('Failed to close store.');
      }
    },
    goBack() {
      this.$router.go(-1);
    }
  }
};
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
  text-align: left;
}

.close-store-form h2 {
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
  justify-content: space-between;
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

.close-button {
  background-color: #3498db;
}

.close-button.disabled {
  background-color: #ccc;
}

.close-button:hover {
  background-color: #2980b9;
}
</style>
