<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Register" @click="goToRegister" />
        <PrimeButton label="Enter to Stores" @click="enterStores" />
        <PrimeButton v-if="isLoggedIn" label="Open Store" @click="openStore" />
        <PrimeButton label="Search Product" @click="searchProduct" />
        <PrimeButton v-if="isStoreOwner" label="Approve Appointment" @click="approveAppointment" />
        <PrimeButton v-if="isStoreOwner" label="My Stores I Own" @click="myStoresIOwn" />
        <PrimeButton v-if="isStoreOwner" label="Manage Products" @click="manageProductsAsOwner" />
        <PrimeButton v-if="isStoreOwner" label="Appoint Owner" @click="appointOwner" />
        <PrimeButton v-if="isStoreOwner" label="Appoint Manager" @click="appointManager" />
        <PrimeButton v-if="isStoreOwner" label="Permissions to Manager" @click="permissionsToManager" />
        <PrimeButton v-if="isStoreOwner" label="Yield Ownership" @click="yieldOwnership" />
        <PrimeButton v-if="isStoreOwner" label="Purchases History" @click="purchasesHistoryAsOwner" />
        <PrimeButton v-if="isStoreOwner" label="Close Store" @click="closeStore" />
      </div>
      <div class="content">
        <AboutSection />
      </div>
      <div class="sidebar2">
        <PrimeButton v-if="isStoreManager" label="My Stores I Manage" @click="myStoresIManage" />
        <PrimeButton v-if="isStoreManager" label="Manage Products" @click="manageProductsAsManager" />
        <PrimeButton v-if="isStoreManager" label="Add Policy" @click="addPolicy" />
        <PrimeButton v-if="isStoreManager" label="Edit Policy" @click="editPolicy" />
        <PrimeButton v-if="isSystemManager" label="Suspension" @click="suspension" />
        <PrimeButton v-if="isSystemManager" label="Create Suspension" @click="createSuspension" />
        <PrimeButton v-if="isSystemManager" label="End Suspension" @click="endSuspension" />
        <PrimeButton v-if="isSystemManager" label="Purchases History" @click="purchasesHistoryAsSystemManager" />
        <PrimeButton v-if="isCommercialManager" label="All Purchases" @click="allPurchases" />
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
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import AboutSection from '@/components/AboutSection.vue';
import { Button as PrimeButton } from 'primevue/button';
import { useRouter } from 'vue-router';

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

    const goToRegister = () => {
      router.push('/register');
    };

    const enterStores = () => {
      console.log('Entering Stores');
    };

    const openStore = () => {
      console.log('Opening Store');
      router.push('/open-store');
    };

    const searchProduct = () => {
      console.log('Searching Product');
    };

    const approveAppointment = () => {
      console.log('Approving Appointment');
    };

    const myStoresIOwn = () => {
      console.log('My Stores I Own');
    };

    const manageProductsAsOwner = () => {
      console.log('Managing Products as Owner');
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
      console.log('Viewing Purchases History as Owner');
    };

    const closeStore = () => {
      console.log('Closing Store');
    };

    const myStoresIManage = () => {
      console.log('My Stores I Manage');
    };

    const manageProductsAsManager = () => {
      console.log('Managing Products as Manager');
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
    };

    const endSuspension = () => {
      console.log('Ending Suspension');
    };

    const purchasesHistoryAsSystemManager = () => {
      console.log('Viewing Purchases History as System Manager');
    };

    const allPurchases = () => {
      console.log('Viewing All Purchases');
    };

    const logout = () => {
      isLoggedIn.value = false;
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('roles');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      isLoggedIn,
      isStoreOwner,
      isStoreManager,
      isSystemManager,
      isCommercialManager,
      username,
      goToRegister,
      enterStores,
      openStore,
      searchProduct,
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
      purchasesHistoryAsSystemManager,
      allPurchases,
      logout
    };
  }
});
</script>
