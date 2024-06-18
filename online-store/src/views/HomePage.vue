<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Enter to All Stores" @click="enterStores" class="sidebar-button"/>
        <PrimeButton label="Search Store" @click="navigateToSearchStore" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn" label="Open Store" @click="openStore" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Approve Appointment" @click="approveAppointment" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="My Stores" @click="myStoresIOwn" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Manage Products" @click="manageProductsAsOwner" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Appoint Owner" @click="appointOwner" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Appoint Manager" @click="appointManager" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Permissions to Manager" @click="permissionsToManager" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Yield Ownership" @click="yieldOwnership" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Purchases History" @click="navigateToPurchaseHistory" class="sidebar-button"/>
        <PrimeButton v-if="isStoreOwner && isLoggedIn" label="Close Store" @click="closeStore" class="sidebar-button"/>
      </div>
      <div class="content">
        <AboutSection />
        <div v-if="activeStores.length" class="active-stores">
          <h2>Active Stores</h2>
          <ul>
            <li v-for="store in activeStores" :key="store.id" class="store-item">
              <img :src="store.image" alt="Store Image" class="store-image">
              <div class="store-details">
                <h3>{{ store.name }}</h3>
                <PrimeButton label="View Products" @click="viewProducts(store.id)" class="view-products-button"/>
              </div>
            </li>
          </ul>
        </div>
        <div v-else>
          <p>No active stores available.</p>
        </div>
      </div>
      <div class="sidebar2">
        <PrimeButton v-if="isStoreManager && isLoggedIn" label="My Stores I Manage" @click="myStoresIManage" class="sidebar-button"/>
        <PrimeButton v-if="isStoreManager && isLoggedIn" label="Manage Products" @click="manageProductsAsManager" class="sidebar-button"/>
        <PrimeButton v-if="isStoreManager && isLoggedIn" label="Add Policy" @click="addPolicy" class="sidebar-button"/>
        <PrimeButton v-if="isStoreManager && isLoggedIn" label="Edit Policy" @click="editPolicy" class="sidebar-button"/>
        <PrimeButton v-if="isSystemManager && isLoggedIn" label="Suspension" @click="suspension" class="sidebar-button"/>
        <PrimeButton v-if="isSystemManager && isLoggedIn" label="Create Suspension" @click="createSuspension" class="sidebar-button"/>
        <PrimeButton v-if="isSystemManager && isLoggedIn" label="End Suspension" @click="endSuspension" class="sidebar-button"/>
        <PrimeButton v-if="isSystemManager && isLoggedIn" label="Watch Suspensions" @click="watchSuspensions" class="sidebar-button"/>
        <PrimeButton v-if="isSystemManager && isLoggedIn" label="Purchases History" @click="purchasesHistoryAsSystemManager" class="sidebar-button"/>
        <PrimeButton v-if="isCommercialManager && isLoggedIn" label="All Purchases" @click="allPurchases" class="sidebar-button"/>
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
import AboutSection from '@/components/AboutSection.vue';
import { Button as PrimeButton } from 'primevue/button';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default defineComponent({
  name: 'HomePage',
  components: {
    SiteHeader,
    AboutSection,
    PrimeButton
  },
  setup() {
    const router = useRouter();
    const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true');
    const roles = JSON.parse(localStorage.getItem('roles') || '[]');
    const isStoreOwner = ref(roles.includes('storeOwner'));
    const isStoreManager = ref(roles.includes('storeManager'));
    const isSystemManager = ref(roles.includes('systemManager'));
    const isCommercialManager = ref(roles.includes('commercialManager'));
    const username = ref(localStorage.getItem('username') || '');
    const activeStores = ref([
      { id: 1, name: 'Colors Store', image: 'https://via.placeholder.com/150', rating: 8.5 },
      { id: 2, name: 'SmartWatch Store', image: 'https://via.placeholder.com/150', rating: 8.5 },
      { id: 3, name: 'Laptops Store', image: 'https://via.placeholder.com/150', rating: 8.5 }
    ]);

    const fetchActiveStores = async () => {
      try {
        const response = await axios.get('/api/active-stores');
        activeStores.value = response.data;
      } catch (error) {
        console.error('Error fetching active stores:', error);
      }
    };

    onMounted(() => {
      fetchActiveStores();
    });

    const viewProducts = (storeId) => {
      router.push({ name: 'StoreDetails', params: { storeId } });
    };

    const enterStores = () => {
      console.log('Entering Stores');
    };
    const openStore = () => {
      console.log('Opening Store');
      router.push('/open-store');
    };
    const navigateToSearchStore = () => {
      router.push('/search-store');
    };
    const approveAppointment = () => {
      console.log('Approving Appointment');
    };
    const myStoresIOwn = () => {
      console.log('My Stores I Own');
      router.push('/my-stores-i-own');
    };
    const manageProductsAsOwner = () => {
      console.log('Managing Products as Owner');
      router.push('/store-name-input');
    };
    const appointOwner = () => {
      console.log('Appointing Owner');
    };
    const appointManager = () => {
      console.log('Appointing Manager');
    };
    const permissionsToManager = () => {
      console.log('Setting Permissions to Manager');
    };
    const yieldOwnership = () => {
      console.log('Yielding Ownership');
    };
    const purchasesHistoryAsOwner = () => {
      router.push({name: 'PurchaseHistory'}); // Navigate to PurchaseHistory
    };
    const closeStore = () => {
      console.log('Closing Store');
    };
    const myStoresIManage = () => {
      router.push('/stores-i-manage');
    };
    const manageProductsAsManager = () => {
      console.log('Managing Products as Manager');
      router.push('/store-name-input');
    };
    const addPolicy = () => {
      console.log('Adding Policy');
    };
    const editPolicy = () => {
      console.log('Editing Policy');
    };
    const suspension = () => {
      console.log('Suspension');
    };
    const createSuspension = () => {
      console.log('Creating Suspension');
      if (isSystemManager.value) {
        router.push('/create-suspension');
      } else {
        console.error('Unauthorized');
      }
    };
    const endSuspension = () => {
      console.log('Ending Suspension');
      if (isSystemManager.value) {
        router.push('/end-suspension');
      } else {
        console.error('Unauthorized');
      }
    };
    const watchSuspensions = () => {
      if (isSystemManager.value) {
        router.push('/watch-suspensions');
      } else {
        console.error('Unauthorized');
      }
    };
    const purchasesHistoryAsSystemManager = () => {
      console.log('Viewing Purchases History as System Manager');
      router.push({name: 'PurchaseHistory'});
    };
    const allPurchases = () => {
      console.log('Viewing All Purchases');
    };
    const logout = () => {
      isLoggedIn.value = false;
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('roles');
      localStorage.removeItem('username');
      router.push('/');
    };
    const navigateToPurchaseHistory = () => {
      router.push({name: 'PurchaseHistory'});
    };
    return {
      isLoggedIn,
      isStoreOwner,
      isStoreManager,
      isSystemManager,
      isCommercialManager,
      username,
      activeStores,
      viewProducts,
      enterStores,
      openStore,
      navigateToSearchStore,
      approveAppointment,
      myStoresIOwn,
      manageProductsAsOwner,
      appointOwner,
      appointManager,
      permissionsToManager,
      yieldOwnership,
      purchasesHistoryAsOwner,
      closeStore,
      myStoresIManage,
      manageProductsAsManager,
      addPolicy,
      editPolicy,
      suspension,
      createSuspension,
      endSuspension,
      watchSuspensions,
      purchasesHistoryAsSystemManager,
      allPurchases,
      logout,
      navigateToPurchaseHistory
    };
  }
});
</script>

<style scoped>
.main-content {
  display: flex;
  justify-content: space-between;
  padding: 20px;
}

.sidebar, .sidebar2 {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.sidebar-button {
  width: 100%;
  max-width: 150px;
}

.content {
  flex: 2;
  padding: 0 20px;
}

.active-stores {
  margin-top: 20px;
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
