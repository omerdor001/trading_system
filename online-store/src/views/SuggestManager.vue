<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="suggest-manage">
      <h2>Suggest Manager</h2>
      <form @submit.prevent="suggestManage">
        <div class="p-field">
          <label for="newManager">New Manager: </label>
          <InputText id="newManager" v-model="newManager" required />
        </div>
        <div class="stores-list">
          <h3>Store</h3>
          <select v-model="selectedStore" @change="selectStore(selectedStore)">
            <option v-for="store in stores" :key="store.id" :value="store">{{ store }}</option>
          </select>
        </div>
        <div class="p-field">
          <label>Options:</label>
          <div class="toggle-buttons-container">
            <ToggleButton id="watch" v-model="watch" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Watch" offLabel="Watch" class="small-toggle" />
            <ToggleButton id="editSupply" v-model="editSupply" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Supply" offLabel="Edit Supply" class="small-toggle" />
            <ToggleButton id="editBuyPolicy" v-model="editBuyPolicy" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Buy Policy" offLabel="Edit Buy Policy" class="small-toggle" />
            <ToggleButton id="editDiscountPolicy" v-model="editDiscountPolicy" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Edit Discount Policy" offLabel="Edit Discount Policy" class="small-toggle" />
            <ToggleButton id="acceptBids" v-model="acceptBids" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Accept Bids" offLabel="Accept Bids" class="small-toggle" />
            <ToggleButton id="createLottery" v-model="createLottery" onIcon="pi pi-check" offIcon="pi pi-times" onLabel="Create Lottery" offLabel="Create Lottery" class="small-toggle" />
          </div>
        </div>
        <div class="button-group">
          <PrimeButton label="Submit" type="submit" class="p-mt-2 submit-button" />
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
import { ToggleButton } from 'primevue/togglebutton'; // Import ToggleButton from PrimeVue
import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';


export default defineComponent({
  name: 'SuggestManage',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
    PrimeToast,
    ToggleButton // Use ToggleButton instead of Checkbox
  },
  setup() {
    const router = useRouter();
    const newManager = ref('');
    const watch = ref(false);
    const editSupply = ref(false);
    const editBuyPolicy = ref(false);
    const editDiscountPolicy = ref(false);
    const acceptBids = ref(false);
    const createLottery = ref(false);
    const error = ref(null);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const selectedStore = ref(null);
    const isLoggedIn = ref(!!username.value);
    const stores = ref([]);
    const toast = useToast();



    const suggestManage = async () => {
      try {
        error.value = null;
        const response = await axios.post('http://localhost:8082/api/trading/suggestManage', null, {
          params : {
          appoint : username,
          token : token,
          newManager: newManager.value,
          store_name_id: selectedStore.value,
          watch: watch.value,
          editSupply: editSupply.value,
          editBuyPolicy: editBuyPolicy.value,
          editDiscountPolicy: editDiscountPolicy.value,
          acceptBids: acceptBids.value,
          createLottery : createLottery.value
          // Add other options here
        }})
      
        // Display success toast
        showSuccessToast(response.data.message);
        
        // Reset form fields
        newManager.value = '';
        selectedStore.value = '';
        watch.value = false;
        editSupply.value = false;
        editBuyPolicy.value = false;
        editDiscountPolicy.value = false;
        acceptBids.value = false;
        createLottery.value = false;
        // Reset other options here
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
        showErrorToast(error.value);
      }
    };

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

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    const showSuccessToast = (message) => {
      // Display success toast
      showCustomToast('success', 'Success', message);
    };

    const showErrorToast = (message) => {
      // Display error toast
      showCustomToast('error', 'Error', message);
    };

    const selectStore = (store) => {
      selectedStore.value = store;
      console.log('Selected Store:', store);
      // You can perform additional actions here based on the selected store
    };

    const showCustomToast = (severity, summary, detail) => {
      // Display custom toast
      toast.add({
        severity: severity,
        summary: summary,
        detail: detail,
        life: 3000
      });
    };

    return {
      stores,
      username,
      isLoggedIn,
      newManager,
      selectedStore,
      selectStore,
      watch,
      editSupply,
      editBuyPolicy,
      editDiscountPolicy,
      acceptBids,
      createLottery,
      error,
      suggestManage,
      logout,
    };
  },
});
</script>

<style scoped>
.suggest-manage {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.suggest-manage h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.suggest-manage .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-button {
  background-color: #e67e22 !important;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.submit-button:hover {
  background-color: #d35400 !important;
}

.p-error {
  margin-top: 20px;
  color: red;
}

.small-toggle .p-button {
  padding: 0.5rem 1rem; /* Adjust padding for smaller size */
  font-size: 0.9rem; /* Adjust font size for smaller size */
}
</style>
