<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <button @click="enterStores">Enter to Stores</button>
        <button v-if="isLoggedIn" @click="openStore">Open Store</button>
        <button @click="searchProduct">Search Product</button>
        <button v-if="isStoreOwner" @click="approveAppointment">Approve Appointment</button>
        <button v-if="isStoreOwner" @click="myStoresIOwn">My Stores I Own</button>
        <button v-if="isStoreOwner" @click="manageProductsAsOwner">Manage Products</button>
        <button v-if="isStoreOwner" @click="appointOwner">Appoint Owner</button>
        <button v-if="isStoreOwner" @click="appointManager">Appoint Manager</button>
        <button v-if="isStoreOwner" @click="permissionsToManager">Permissions to Manager</button>
        <button v-if="isStoreOwner" @click="yieldOwnership">Yield Ownership</button>
        <button v-if="isStoreOwner" @click="purchasesHistoryAsOwner">Purchases History</button>
        <button v-if="isStoreOwner" @click="closeStore">Close Store</button>
      </div>
      <div class="content">
        <AboutSection />
      </div>
      <div class="sidebar2">
        <button v-if="isStoreManager" @click="myStoresIManage">My Stores I Manage</button>
        <button v-if="isStoreManager" @click="manageProductsAsManager">Manage Products</button>
        <button v-if="isStoreManager" @click="addPolicy">Add Policy</button>
        <button v-if="isStoreManager" @click="editPolicy">Edit Policy</button>
        <button v-if="isSystemManager" @click="suspension">Suspension</button>
        <button v-if="isSystemManager" @click="createSuspension">Create Suspension</button>
        <button v-if="isSystemManager" @click="endSuspension">End Suspension</button>
        <button v-if="isSystemManager" @click="purchasesHistoryAsSystemManager">Purchases History</button>
        <button v-if="isCommercialManager" @click="allPurchases">All Purchases</button>
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
import SiteHeader from '@/components/SiteHeader.vue';
import AboutSection from '@/components/AboutSection.vue';

export default {
  name: 'HomePage',
  components: {
    SiteHeader,
    AboutSection
  },
  data() {
    return {
      isLoggedIn: localStorage.getItem('isLoggedIn') === 'true',
      roles: JSON.parse(localStorage.getItem('roles') || '[]'),
      username: localStorage.getItem('username') || ''
    };
  },
  computed: {
    isStoreOwner() {
      return this.roles.includes('storeOwner');
    },
    isStoreManager() {
      return this.roles.includes('storeManager');
    },
    isSystemManager() {
      return this.roles.includes('systemManager');
    },
    isCommercialManager() {
      return this.roles.includes('commercialManager');
    }
  },
  methods: {
    enterStores() {
      console.log('Entering Stores');
    },
    openStore() {
      console.log('Opening Store');
      this.$router.push({ path: '/open-store' });
    },
    searchProduct() {
      console.log('Searching Product');
    },
    approveAppointment() {
      console.log('Approving Appointment');
    },
    myStoresIOwn() {
      console.log('My Stores I Own');
    },
    manageProductsAsOwner() {
      console.log('Managing Products as Owner');
    },
    appointOwner() {
      console.log('Appointing Owner');
    },
    appointManager() {
      console.log('Appointing Manager');
    },
    permissionsToManager() {
      console.log('Setting Permissions to Manager');
    },
    yieldOwnership() {
      console.log('Yielding Ownership');
    },
    purchasesHistoryAsOwner() {
      console.log('Viewing Purchases History as Owner');
    },
    closeStore() {
      console.log('Closing Store');
    },
    myStoresIManage() {
      console.log('My Stores I Manage');
    },
    manageProductsAsManager() {
      console.log('Managing Products as Manager');
    },
    addPolicy() {
      console.log('Adding Policy');
    },
    editPolicy() {
      console.log('Editing Policy');
    },
    suspension() {
      console.log('Suspension');
    },
    createSuspension() {
      console.log('Creating Suspension');
    },
    endSuspension() {
      console.log('Ending Suspension');
    },
    purchasesHistoryAsSystemManager() {
      console.log('Viewing Purchases History as System Manager');
    },
    allPurchases() {
      console.log('Viewing All Purchases');
    },
    logout() {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('roles');
      localStorage.removeItem('username');
      this.isLoggedIn = false;
      if (this.$router.currentRoute.path !== '/login') {
        this.$router.push('/login');
      }
    }
  }
}
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
.sidebar button, .sidebar2 button {
  background-color: #e67e22;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  color: white;
  border-radius: 5px;
  font-weight: bold;
  transition: background-color 0.3s;
}
.sidebar button:hover, .sidebar2 button:hover {
  background-color: #d35400;
}
.content {
  flex: 2;
  padding: 0 20px;
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
