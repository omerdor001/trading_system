<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Manage Products" @click="manageProducts" />
        <PrimeButton label="Suggest Owner" @click="suggestOwner" />
        <PrimeButton label="Suggest Manager" @click="suggestManager" />
        <PrimeButton label="Manage Purchase Policy" @click="managePurchasePolicy" />
        <PrimeButton label="Manage Discount Policy" @click="manageDiscountPolicy" />
        <PrimeButton label="Yield Ownership" @click="yieldOwnership" />
        <PrimeButton label="Purchase History" @click="purchaseHistory" />
      </div>
      <div class="content">
        <h2>Stores I Own</h2>
        <div class="stores-list">
          <h3>Stores List</h3>
          <select v-model="selectedStore" @change="selectStore(selectedStore)">
            <option v-for="store in stores" :key="store.id" :value="store">{{ store }}</option>
          </select>
        </div>

        <!-- Edit Workers Section -->
        <div v-if="editingWorkers === selectedStore">
          <h4>Edit Workers</h4>
          <table>
            <thead>
              <tr>
                <th>Select</th>
                <th>Username</th>
                <th>Role</th>
                <th>Birthdate</th>
                <th>Address</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="worker in editableWorkers" :key="worker.username">
                <td><input type="checkbox" v-model="worker.selected" /></td>
                <td><input v-model="worker.username" /></td>
                <td><input v-model="worker.role" /></td>
                <td><input v-model="worker.birthdate" /></td>
                <td><input v-model="worker.address" /></td>
              </tr>
            </tbody>
          </table>
          <PrimeButton label="Add Worker" @click="addWorker" />
          <PrimeButton label="Remove Worker" @click="removeWorker" />
          <PrimeButton label="Save Workers" @click="saveWorkers" />
        </div>

        <!-- Display Workers Section -->
        <div v-else>
          <h4>Workers</h4>
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Role</th>
                <th>Birthdate</th>
                <th>Address</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="worker in selectedStoreWorkers" :key="worker.username">
                <td>{{ worker.username }}</td>
                <td>{{ worker.role }}</td>
                <td>{{ worker.birthdate }}</td>
                <td>{{ worker.address }}</td>
              </tr>
            </tbody>
          </table>
          <PrimeButton label="Edit Workers" @click="editWorkers(selectedStore)" />
        </div>

        <!-- Edit Permissions Section -->
        <div v-if="editingPermissions === selectedStore">
          <h4>Edit Permissions</h4>
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Watch</th>
                <th>Edit Supply</th>
                <th>Edit Store Policy</th>
                <th>Edit Discount Policy</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="worker in editableWorkers" :key="worker.username">
                <td>{{ worker.username }}</td>
                <td><input type="checkbox" v-model="worker.permissions.watch" /></td>
                <td><input type="checkbox" v-model="worker.permissions.editSupply" /></td>
                <td><input type="checkbox" v-model="worker.permissions.editStorePolicy" /></td>
                <td><input type="checkbox" v-model="worker.permissions.editDiscountPolicy" /></td>
              </tr>
            </tbody>
          </table>
          <PrimeButton label="Save Permissions" @click="savePermissions" />
        </div>

        <!-- Display Permissions Section -->
        <div v-else>
          <h4>Permissions</h4>
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Watch</th>
                <th>Edit Supply</th>
                <th>Edit Store Policy</th>
                <th>Edit Discount Policy</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="worker in selectedStoreWorkers" :key="worker.username">
                <td>{{ worker.username }}</td>
                <td><input type="checkbox" :checked="worker.permissions.watch" disabled /></td>
                <td><input type="checkbox" :checked="worker.permissions.editSupply" disabled /></td>
                <td><input type="checkbox" :checked="worker.permissions.editStorePolicy" disabled /></td>
                <td><input type="checkbox" :checked="worker.permissions.editDiscountPolicy" disabled /></td>
              </tr>
            </tbody>
          </table>
          <PrimeButton label="Edit Permissions" @click="editPermissions(selectedStore)" />
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
import { Button as PrimeButton } from 'primevue/button';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';
import axios from 'axios';

export default defineComponent({
  name: 'MyStoresIOwn',
  components: {
    SiteHeader,
    PrimeButton,
    'p-toast': PrimeToast,
  },
  setup() {
    const router = useRouter();
    const stores = ref([]);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const editingWorkers = ref(null);
    const editingPermissions = ref(null);
    const selectedStore = ref(null);
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

    const enterStore = (storeId) => {
      router.push({ name: 'StoreDetailsEditor', params: { storeId } });
    };

    const manageProducts = () => {
      router.push('/product-management/:storeName/:productId');
    };

    const suggestOwner = () => {
      router.push('/suggest-owner');
    };

    const suggestManager = () => {
      router.push('/suggest-manager');
    };

    const managePurchasePolicy = () => {
      // Change to router
    };

    const manageDiscountPolicy = () => {
      // Change to router
    };

    const yieldOwnership = () => {
      router.push('/yield-ownership');
    };

    const purchaseHistory = () => {
      router.push('/purchase-history');
    };

    const editWorkers = (storeId) => {
      const store = stores.value.find(store => store.id === storeId);
      store.editableWorkers = JSON.parse(JSON.stringify(store.workers)); // Deep copy
      editingWorkers.value = storeId;
    };

    const saveWorkers = (storeId) => {
      const store = stores.value.find(store => store.id === storeId);
      store.workers = JSON.parse(JSON.stringify(store.editableWorkers)); // Deep copy
      editingWorkers.value = null;
    };

    const editPermissions = (storeId) => {
      const store = stores.value.find(store => store.id === storeId);
      store.editableWorkers = JSON.parse(JSON.stringify(store.workers)); // Deep copy
      editingPermissions.value = storeId;
    };

    const savePermissions = (storeId) => {
      const store = stores.value.find(store => store.id === storeId);
      store.workers = JSON.parse(JSON.stringify(store.editableWorkers)); // Deep copy
      editingPermissions.value = null;
    };

    const addWorker = (storeId) => {
      const store = stores.value.find(store => store.id === storeId);
      store.editableWorkers.push({ username: '', role: '', birthdate: '', address: '', permissions: { watch: false, editSupply: false, editStorePolicy: false, editDiscountPolicy: false }, selected: false });
    };

    const removeWorker = (storeId) => {
      const store = stores.value.find(store => store.id === storeId);
      store.editableWorkers = store.editableWorkers.filter(worker => !worker.selected);
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      stores,
      username,
      selectStore,
      enterStore,
      manageProducts,
      suggestOwner,
      suggestManager,
      managePurchasePolicy,
      manageDiscountPolicy,
      yieldOwnership,
      purchaseHistory,
      editWorkers,
      saveWorkers,
      editPermissions,
      savePermissions,
      addWorker,
      removeWorker,
      editingWorkers,
      editingPermissions,
      logout
    };
  }
});
</script>

<style scoped>
.main-content {
  display: flex;
}
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
}
.content {
  flex: 2;
  padding: 20px;
}
.stores-list {
  margin-bottom: 20px;
}
.stores-list select {
  width: 100%;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: none;
  background-color: #fff;
  cursor: pointer;
}
.stores-list select:focus {
  outline: none;
  border-color: #86b7fe;
  box-shadow: 0 0 0 2px rgba(38, 132, 255, 0.2);
}
.stores-list h3 {
  margin-bottom: 10px;
}
</style>

