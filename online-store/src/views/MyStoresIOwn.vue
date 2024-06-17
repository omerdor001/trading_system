<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Add Store" @click="addStore" />
        <PrimeButton label="Yield Ownership" @click="yieldOwnership" />
        <PrimeButton label="Purchase History" @click="purchaseHistory" />
      </div>
      <div class="content">
        <h2>Stores I Own</h2>
        <div v-for="store in stores" :key="store.id" class="store">
          <h3 @click="enterStore(store.id)">Store: {{ store.name }}</h3>
          <PrimeButton label="Enter Store" @click="enterStore(store.id)" />

          <div v-if="editingWorkers === store.id">
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
              <tr v-for="worker in store.editableWorkers" :key="worker.username">
                <td><input type="checkbox" v-model="worker.selected" /></td>
                <td><input v-model="worker.username" /></td>
                <td><input v-model="worker.role" /></td>
                <td><input v-model="worker.birthdate" /></td>
                <td><input v-model="worker.address" /></td>
              </tr>
              </tbody>
            </table>
            <PrimeButton label="Add Worker" @click="addWorker(store.id)" />
            <PrimeButton label="Remove Worker" @click="removeWorker(store.id)" />
            <PrimeButton label="Save" @click="saveWorkers(store.id)" />
          </div>
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
              <tr v-for="worker in store.workers" :key="worker.username">
                <td>{{ worker.username }}</td>
                <td>{{ worker.role }}</td>
                <td>{{ worker.birthdate }}</td>
                <td>{{ worker.address }}</td>
              </tr>
              </tbody>
            </table>
            <PrimeButton label="Edit Workers" @click="editWorkers(store.id)" />
          </div>

          <div v-if="editingPermissions === store.id">
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
              <tr v-for="worker in store.editableWorkers" :key="worker.username">
                <td>{{ worker.username }}</td>
                <td><input type="checkbox" v-model="worker.permissions.watch" /></td>
                <td><input type="checkbox" v-model="worker.permissions.editSupply" /></td>
                <td><input type="checkbox" v-model="worker.permissions.editStorePolicy" /></td>
                <td><input type="checkbox" v-model="worker.permissions.editDiscountPolicy" /></td>
              </tr>
              </tbody>
            </table>
            <PrimeButton label="Save" @click="savePermissions(store.id)" />
          </div>
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
              <tr v-for="worker in store.workers" :key="worker.username">
                <td>{{ worker.username }}</td>
                <td><input type="checkbox" :checked="worker.permissions.watch" disabled /></td>
                <td><input type="checkbox" :checked="worker.permissions.editSupply" disabled /></td>
                <td><input type="checkbox" :checked="worker.permissions.editStorePolicy" disabled /></td>
                <td><input type="checkbox" :checked="worker.permissions.editDiscountPolicy" disabled /></td>
              </tr>
              </tbody>
            </table>
            <PrimeButton label="Edit Permissions" @click="editPermissions(store.id)" />
          </div>
        </div>
      </div>
    </div>
    <footer>
      <div class="external-links">
        <h3>External Links</h3>
        <ul>
          <li><a href="#">Link 1</a></li>
          <li><a href="#">Link 2</a></li>
          <li><a href="#">Link 3</a></li>
        </ul>
      </div>
    </footer>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';

export default defineComponent({
  name: 'MyStoresIOwn',
  components: {
    SiteHeader,
    PrimeButton
  },
  setup() {
    const router = useRouter();
    const stores = ref([]);
    const username = ref(localStorage.getItem('username') || '');
    const editingWorkers = ref(null);
    const editingPermissions = ref(null);

    onMounted(() => {
      // Fetch stores data here
      stores.value = [
        {
          id: 1,
          name: 'Store 1',
          description: 'Description for Store 1',
          products: [],
          purchaseHistory: [],
          workers: [
            { username: 'user1', role: 'Manager', birthdate: '01-01-1990', address: 'Address 1', permissions: { watch: true, editSupply: true, editStorePolicy: false, editDiscountPolicy: false } },
            { username: 'user2', role: 'Worker', birthdate: '02-02-1991', address: 'Address 2', permissions: { watch: true, editSupply: false, editStorePolicy: true, editDiscountPolicy: false } }
          ],
          editableWorkers: []
        },
        {
          id: 2,
          name: 'Store 2',
          description: 'Description for Store 2',
          products: [],
          purchaseHistory: [],
          workers: [
            { username: 'user3', role: 'Manager', birthdate: '03-03-1992', address: 'Address 3', permissions: { watch: true, editSupply: true, editStorePolicy: true, editDiscountPolicy: true } },
            { username: 'user4', role: 'Worker', birthdate: '04-04-1993', address: 'Address 4', permissions: { watch: false, editSupply: true, editStorePolicy: false, editDiscountPolicy: true } }
          ],
          editableWorkers: []
        }
      ];
    });

    const enterStore = (storeId) => {
      router.push({ name: 'StoreDetails', params: { storeId } });
    };

    const addStore = () => {
      router.push('/open-store');
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
      enterStore,
      addStore,
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
.store {
  margin-bottom: 40px;
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 8px;
}
.workers, .permissions {
  margin-bottom: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}
th, td {
  padding: 10px;
  border: 1px solid #ddd;
}
footer {
  background-color: #425965;
  color: white;
  padding: 20px;
  text-align: center;
}
.external-links h3 {
  margin-bottom: 10px;
}
.external-links ul {
  list-style: none;
  padding: 0;
}
.external-links li {
  margin-bottom: 5px;
}
.external-links a {
  color: white;
  text-decoration: none;
}
.external-links a:hover {
  text-decoration: underline;
}
</style>
