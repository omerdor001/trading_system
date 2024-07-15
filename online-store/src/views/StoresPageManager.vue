<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Stores</h2>
        <hr />
        <!-- Stores list -->
        <div class="store-container">
          <div v-for="store in stores" :key="store.name" class="store-item" @click="showStoreDetails(store)">
            <div class="store-content">
              <h3 class="store-name">Store: {{ store.name }}</h3>
              <h5 class="status">Status: {{ getStatusText(store.status) }}</h5>
              <h5 class="role-at-store">I am a: {{ store.role }}</h5>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="selectedStore" class="modal-overlay" @click.self="closeModal">
      <!-- Modal Content -->
      <div class="modal-content">
        <span class="close-button" @click="closeModal">&times;</span>
        <h3>Store Details</h3>
        <div class="store-details">
          <!-- Left column for description and products -->
          <div class="details-column">
            <p><strong>Description:</strong> {{ selectedStore.description }}</p>
            <p><strong>Products:</strong></p>
            <PrimeButton class="option-button primary" @click="navigateToProductsInStore(selectedStore.name,selectedStore.permissions.isEditSupply)">View Products</PrimeButton>
            <p><strong>Workers:</strong></p>
            <PrimeButton class="option-button primary" @click="navigateToWorkersInStore(selectedStore.name)">View Workers</PrimeButton>
          </div>
          <!-- Right column for founder, isActive, rating, isOpen -->
          <div class="details-column">
            <p><strong>Founder:</strong> {{ selectedStore.founder }}</p>
            <label><input type="checkbox" v-model="selectedStore.status"> Active</label><br />
            <label><input type="checkbox" v-model="selectedStore.isOpen"> Is Open</label><br />
            <p><strong>Rating:</strong> {{ selectedStore.rating }}</p>
          </div>
        </div>
        <div class="options-container">
          <h3>Options</h3>
          <PrimeButton v-if="!selectedStore.isOpen  && selectedStore.founder === username.substring(1)" class="option-button primary" @click="openStoreExist(selectedStore.name)">Re-open Store</PrimeButton>
          <PrimeButton v-if="selectedStore.permissions.isOwner" class="option-button primary" @click="suggestOwner(selectedStore.name)">Suggest Owner</PrimeButton>
          <PrimeButton v-if="selectedStore.permissions.isOwner" class="option-button primary" @click="suggestManager(selectedStore.name)">Suggest Manager</PrimeButton>
          <PrimeButton v-if="selectedStore.permissions.isOwner" class="option-button primary" @click="yieldOwnership()">Yield Ownership</PrimeButton>
          <PrimeButton v-if="selectedStore.permissions.isEditPurchasePolicy" class="option-button primary" @click="editPurchasePolicy(selectedStore.name)">Edit Purchase Policy</PrimeButton>
          <PrimeButton v-if="selectedStore.permissions.isEditDiscountPolicy" class="option-button primary" @click="editDiscountPolicy(selectedStore.name)">Edit Discount Policy</PrimeButton>
          <PrimeButton class="option-button primary" @click="purchasesHistory(selectedStore.name)">Purchases History</PrimeButton>
          <PrimeButton v-if="selectedStore.permissions.acceptBids" class="option-button primary" @click="acceptBids(selectedStore.name)">Accept Bids</PrimeButton>
        </div>
      </div>
    </div>
    <p-toast></p-toast>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';
import PrimeButton from 'primevue/button';
import axios from 'axios';

export default defineComponent({
  name: 'StoresPageManager',
  components: {
    SiteHeader,
    'p-toast': PrimeToast,
    PrimeButton,
  },
  setup() {
    const router = useRouter();
    const stores = ref([]);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const selectedStore = ref(null);
    const toast = useToast();



    
    const fetchStoreDetails = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/stores-detailed-info', {
          params: { 
            token: token,
            username: username 
          }
        });
        stores.value = response.data.map(store => ({
          name: store.name,
          role: store.role,
          status: store.status,
          description: store.description,
          founder: store.founder,
          isOpen: store.isOpen,
          rating: store.rating,
          permissions: {
            isWatch: false,
            isEditSupply: false,
            isEditPurchasePolicy: false,
            isEditDiscountPolicy: false,
            isOwner: false,
          }
        }));
        toast.add({ severity: 'success', summary: 'success', detail: username , life: 3000 });
        toast.add({ severity: 'success', summary: 'success', detail: response.data , life: 3000 });
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    };
    
    onMounted(() => {
      fetchStoreDetails();
    });

    
    const showStoreDetails = async (store) => {
      selectedStore.value = store;
      try {
         const response = await axios.get('http://localhost:8082/api/trading/permissions-for-user', {
           params: {
             username: username,
             storeName: store.name,
             token: token,
          }
       });
       console.log(response.data);
       const permissions = response.data; 
       store.permissions.isOwner = store.role === 'Owner';
       store.permissions.isWatch = permissions.watch;
       store.permissions.isEditSupply = permissions.editSupply;
       store.permissions.isEditPurchasePolicy = permissions.editBuyPolicy;
       store.permissions.isEditDiscountPolicy = permissions.editDiscountPolicy;
       store.permissions.acceptBids = permissions.acceptBids;
       toast.add({ severity: 'success', summary: 'success', detail: username , life: 3000 });
       toast.add({ severity: 'success', summary: 'success', detail: response.data , life: 3000 });
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch permissions', life: 3000 });
        console.error('Error fetching permissions:', error);
      }
    };

    const closeModal = () => {
      selectedStore.value = null;
    };

    const navigateToProductsInStore = (storeName,isEditSupply) => {
      router.push({ name: 'ProductList', params: { storeName,isEditSupply } });
    };

    const navigateToWorkersInStore = (storeName) => {
      router.push({ name: 'WorkersInStore', params: { storeName } });
    };

    const getStatusText = (status) => {
      return status ? 'Active' : 'Inactive';
    };

    const suggestOwner = (storeName) => {
      const store = selectedStore.value;
      if (store && store.permissions.isOwner) {
        router.push({ name: 'SuggestOwner', params: { storeName } });
      } 
    };

    const openStoreExist =  async (store) => {
      const tempStore = store;
      try {
         const response = await axios.post('http://localhost:8082/api/trading/store/open', null, {
           params: {
             username: username,
             storeName: tempStore,
             token: token,
          }
       });
       console.log(response.data);
       closeModal();
       fetchStoreDetails();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to Re-open store', life: 3000 });
        console.error('Error fetching permissions:', error);
      }
    };


    const suggestManager = (storeName) => {
      const store = selectedStore.value;
      if (store && store.permissions.isOwner) {
        router.push({ name: 'SuggestManager', params: { storeName } });
      }
    };

    const editPurchasePolicy = (storeName) => {
      const store = selectedStore.value;
      if (store && store.permissions.isEditPurchasePolicy) {
        router.push({ name: 'EditPurchasePolicy', params: { storeName } });
      }
    };

    const editDiscountPolicy = (storeName) => {
      const store = selectedStore.value;
      if (store && store.permissions.isEditDiscountPolicy) {
        router.push({ name: 'EditDiscountPolicy', params: { storeName } });
      }
    };

    const acceptBids = (storeName) => {
      const store = selectedStore.value;
      if (store) {
        router.push({ name: 'WatchStoreBids', params: { storeName } });
      }
    };

    const yieldOwnership = async () => {
      const store = selectedStore.value;
      if (store && store.permissions.isOwner) {
        try {
          const response = await axios.post('http://localhost:8082/api/trading/waiverOnOwnership', null, {
            params: { 
              username: username,
              token: token,
              storeName: store.name,
            }
          });
          console.log(response.data);
          toast.add({ severity: 'success', summary: 'Success', detail: response.data, life: 3000 });
        } catch (error) {
          toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to yield ownership', life: 3000 });
          console.error('Failed to yield ownership:', error.message);
        }
      }
    };

    const purchasesHistory = (storeName) => {
      router.push({ name: 'PurchaseHistoryForStore', params: { storeName } });
    };

    return {
      stores,
      username,
      token,
      showStoreDetails,
      selectedStore,
      navigateToProductsInStore,
      navigateToWorkersInStore,
      getStatusText,
      closeModal,
      suggestOwner,
      acceptBids,
      suggestManager,
      editPurchasePolicy,
      editDiscountPolicy,
      yieldOwnership,
      openStoreExist,
      purchasesHistory,
      fetchStoreDetails
    };
  }
});
</script>

<style scoped>
.container {
  padding: 20px;
}

.content {
  width: 80%;
  padding: 15px;
  margin: auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.store-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.store-item {
  flex: 0 0 calc(33.33% - 20px);
  background-color: #f9f9f9;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
}

.store-content {
  display: flex;
  flex-direction: column;
}

.status, .role-at-store {
  margin-bottom: 5px;
  font-size: 14px;
  color: #666;
}

/* Modal Overlay */
.modal-overlay {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fefefe;
  padding: 30px;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  max-width: 80%;
  max-height: 80%;
  overflow-y: auto;
  position: relative;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.store-details {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-family: inherit;
}

.details-column {
  flex: 1;
  margin-right: 20px;
  font-family: inherit;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 15px;
  color: #aaa;
  cursor: pointer;
  font-size: 24px;
  font-family: inherit;
}

close-button:hover {
  color: #555;
}

.manage-products-link {
  margin-top: 10px;
  margin-left: 5px;
  font-family: inherit;
}

.manage-products-link a {
  margin-bottom: 0;
  text-decoration: none;
  color: #007bff;
  font-family: inherit;
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
  font-family: inherit;
}

.option-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-family: inherit;
}

.option-button:hover {
  background-color: #0056b3;
}

.primary {
  background-color: #cce5ff;
  color: black;
  font-family: inherit;
}

.primary:hover {
  background-color: #b3d7ff;
}
</style>
