<template>
<div>
  <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
  <div class="close-store-container">
    <div class="close-store-form">
      <h2>Select Store to Close</h2>
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
        <div class="button-group">
          <button v-if="selectedStore" @click="markStoreForClosure">Mark Store for Closure</button>
        </div>
        <div class="button-group">
          <button class="back-button" @click="goBack">Go Back</button>
          <button class="close-button" @click="closeStore">Close Store</button>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import StoreViewModel from '@/ViewModel/StoreViewModel';
import UserViewModel from '@/ViewModel/UserViewModel';

const api_base_url = '/api';
console.log(api_base_url)
export default {
   name: 'CloseStore',
  components: {
    SiteHeader
  },
  data() {
    return {
      stores: [],
      selectedStoreId: '',
      markedStore: null,
      username: UserViewModel.getters.getUsername() || ''

    };
  },
  async mounted() {
    this.stores = ['nirStore','nirStore2']
   // try {
    //  const response = await axios.get(`$api_base_url/stores/owned`);
     // this.stores = response.data;
   // } catch (error) {
     // console.error('Error fetching stores:', error);
   // }
  },

  
  methods: {
        logout() {
      UserViewModel.actions.logout();
      this.$router.push('/login');
    },
    async selectStore(storeName) {
      this.markedStore = storeName;
    },
    goBack() {
      this.$router.push('/');
    },
    async markStoreForClosure() {
      if (!this.markedStore) {
        alert('Please select a store.');
        return;
      }

      try {
        await axios.put(`${api_base_url}/stores/${this.selectedStoreId}/close`);
        this.markedStore = this.stores.find(store => store.id === this.selectedStoreId);
      } catch (error) {
        console.error('Error marking store for closure:', error);
        alert('Failed to mark store for closure.');
      }
    },
    async closeStore() {
      if (!this.markedStore) {
        alert('No store is marked for closure.');
        return;
      }
      try {
        await StoreViewModel.actions.closeStoreExist(this.username, '', this.markedStore);
        console.log('Store Closed with name:', this.markedStore, 'and description:', this.description);
      } catch (error) {
        console.error('Failed to Closed store:', error.message);
      }
      this.$router.push('/');
    }
  },
  watch: {
    selectedStoreId(newValue, oldValue) {
      if (newValue !== oldValue) {
        // Reset markedStore when a new store is selected
        this.markedStore = null;
      }
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
  text-align: left; /* Align form content to the left */
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