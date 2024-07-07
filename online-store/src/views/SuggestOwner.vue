<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="suggest-owner">
      <h2>Suggest Owner</h2>
      <form @submit.prevent="suggestOwner">
        <div class="p-field">
          <label for="newOwner">New Owner: </label>
          <InputText id="newOwner" v-model="newOwner" required />
        </div>
        <div class="stores-list">
          <h3>Store</h3>
          <select v-model="selectedStore" @change="selectStore(selectedStore)">
            <option v-for="store in stores" :key="store.id" :value="store">{{ store }}</option>
          </select>
        </div>
        <div class="button-group">
          <PrimeButton label="Submit" type="submit" class="p-mt-2 p-button-primary" />
        </div>
      </form>
      <p v-if="error" class="error">{{ error }}</p>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';


export default defineComponent({
  name: 'SuggestOwner',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
    PrimeToast
  },
  setup() {
    const router = useRouter();
    const appoint = ref('');
    const newOwner = ref('');
    const storeName = ref('');
    const error = ref(null);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const isLoggedIn = ref(!!username.value);
    var selectedStore = ref('');
    const stores = ref([]);
    const toast = useToast();



    onMounted(
      async () => {
      try {

        const response = await axios.get('http://localhost:8082/api/trading/stores-I-own', {
           params: {
            userName: username,
            token: token,
          }
        });
        console.log(response.data);
        stores.value = response.data.substring(1,response.data.length-1).split(',');
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
      }  
    });

    const selectStore = (store) => {
      selectedStore.value = store;
      console.log('Selected Store:', store);
      // You can perform additional actions here based on the selected store
    };

    const suggestOwner = async () => {
      try {
        error.value = null;
        const response = await axios.post('http://localhost:8082/api/trading/suggestOwner', null, {
          params : {
          appoint: username,
          token: token,
          newOwner: newOwner.value,
          storeName: selectedStore.value
          }
        });
        // Display success toast
       toast.add({
          severity: 'success',
          summary: 'Success',
          detail: response.data.message,
          life: 3000
        });
        // Clear form fields after successful submission
        appoint.value = '';
        newOwner.value = '';
        storeName.value = '';
      } catch (err) {
        if (err.response) {
            error.value = err.response.data || 'Failed to Login';
            console.error('Server responded with status:', err.response.status);
          } else if (err.request) {
            error.value = 'No response from server';
            console.error('No response received:', err.request);
          } else {
            error.value = 'Request failed to reach the server';
            console.error('Error setting up the request:', err.message);
          }

        // Display error toast
        toast.add({
          severity: 'error',
          summary: 'Error',
          detail: error.value,
          life: 5000
        });
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      token,
      selectStore,
      selectedStore,
      isLoggedIn,
      appoint,
      newOwner,
      storeName,
      error,
      suggestOwner,
      logout,
      stores
    };
  },
});
</script>

<style scoped>
.suggest-owner {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.suggest-owner h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.suggest-owner .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-button {
  background-color: #e67e22;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.submit-button:hover {
  background-color: #d35400;
}

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
