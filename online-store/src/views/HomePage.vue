<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" :isAdmin="isAdmin" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="View All Stores" @click="viewAllStores" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn" label="Stores Manager" @click="stores" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn" label="Open Store" @click="openStore" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn" label="Close Store" @click="closeStore" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn" label="Approve Authorities" @click="approves" class="sidebar-button" />
      </div>
      <div class="content">
        <AboutSection />
      </div>
      <div class="sidebar2">
        <PrimeButton v-if="isLoggedIn && isAdmin" label="Create Suspension" @click="createSuspension" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn && isAdmin" label="End Suspension" @click="endSuspension" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn && isAdmin" label="Watch Suspensions" @click="watchSuspensions" class="sidebar-button"/>
        <PrimeButton v-if="isLoggedIn && isAdmin" label="Purchases History" @click="purchasesHistoryAsSystemManager" class="sidebar-button"/>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import AboutSection from '@/components/AboutSection.vue';
import { Button as PrimeButton } from 'primevue/button';
import { useRouter } from 'vue-router';
import axios from 'axios';
import webSocketService from '../webSocketService';

export default defineComponent({
  name: 'HomePage',
  components: {
    SiteHeader,
    AboutSection,
    PrimeButton,
  },
  setup() {
    const router = useRouter();
    const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true');
    const isAdmin = ref(localStorage.getItem('isAdmin') === 'true');
    const username = ref(localStorage.getItem('username') || '');
    const activeStores = ref([
      { id: 1, name: 'Colors Store', image: 'https://via.placeholder.com/150', rating: 8.5 },
      { id: 2, name: 'SmartWatch Store', image: 'https://via.placeholder.com/150', rating: 8.5 },
      { id: 3, name: 'Laptops Store', image: 'https://via.placeholder.com/150', rating: 8.5 }
    ]);

    const handleWebSocketMessage = (message) => {
      console.log('WebSocket message received:', message);
      // Handle the WebSocket message as needed
    };

    const enter = async () => {
      try {
        const res = await axios.get('http://localhost:8082/api/trading/enter');
        localStorage.setItem('username', res.data.username);
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('isLoggedIn', false);
      } catch (error) {
        if (error.response.status === 403) {
          router.push('/register');
        }
      }
    };

    onMounted(() => {
      if (!isLoggedIn.value) {
        enter();
      }
      webSocketService.connect('ws://localhost:8082/websocket');
      webSocketService.subscribe(handleWebSocketMessage);
    });

    onUnmounted(() => {
      webSocketService.unsubscribe(handleWebSocketMessage);
      webSocketService.disconnect();
    });

    const viewProducts = (storeId) => {
      router.push({ name: 'StoreDetails', params: { storeId } });
    };

    const openStore = () => {
      router.push('/open-store');
    };

    const approves = () => {
      router.push('/approve-roles');
    };

    const stores = () => {
      router.push('/stores-page');
    };

    const closeStore = () => {
      router.push('/close-store');
    };

    const createSuspension = () => {
      if(isAdmin.value){
        router.push('/create-suspension');
      }
    };

    const endSuspension = () => {
      if(isAdmin.value){
        router.push('/end-suspension');
      }
    };

    const watchSuspensions = () => {
      if(isAdmin.value){
        router.push('/watch-suspensions');
      }
    };

    const purchasesHistoryAsSystemManager = () => {
      router.push({ name: 'PurchaseHistory' });
    };

    const viewAllStores = () => {
      router.push({ name: 'AllStoresPage' });
    };

    return {
      isLoggedIn,
      isAdmin,
      username,
      activeStores,
      viewProducts,
      openStore,
      approves,
      stores,
      closeStore,
      createSuspension,
      endSuspension,
      watchSuspensions,
      purchasesHistoryAsSystemManager,
      viewAllStores,
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
  max-width: 200px;
  height: 40px;
  margin-bottom: 10px;
  padding: 0;
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
