<template>
  <header>
    <div class="header-content">
      <img src="@/assets/logo.png" alt="LASMONY" class="logo">
      <div class="right-buttons">
        <template v-if="isLoggedIn">
          <PrimeButton label="Logout" @click="logout" class="p-button-danger" />
        </template>
        <template v-else>
          <PrimeButton label="Register" @click="$router.push('/register')" />
          <PrimeButton label="Login" @click="$router.push('/login')" />
        </template>
        <PrimeButton label="Notifications" @click="notifications" icon="pi pi-bell" />
        <PrimeButton label="Cart" @click="viewCart" icon="pi pi-shopping-cart" />
      </div>
    </div>
  </header>
</template>

<script>
import { defineComponent } from 'vue';
import PrimeButton from 'primevue/button';

export default defineComponent({
  name: 'SiteHeader',
  components: {
    PrimeButton
  },
  props: {
    isLoggedIn: {
      type: Boolean,
      default: false
    },
    username: {
      type: String,
      default: ''
    }
  },
  methods: {
    notifications() {
      // handle notifications
    },
    viewCart() {
      // handle viewing cart
    },
    logout() {
      this.$emit('logout');
    }
  }
});
</script>

<style scoped>
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: #425965;
  color: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  height: 100px; /* Increased height for larger logo */
}

.logo {
  height: 80px; /* Increased height for larger logo */
  width: auto;
}

.right-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.p-button {
  background-color: #e67e22 !important;
  border: none !important;
  padding: 10px 15px !important;
  cursor: pointer !important;
  color: white !important;
  border-radius: 5px !important;
  font-weight: bold !important;
  transition: background-color 0.3s !important;
}

.p-button:hover {
  background-color: #d35400 !important;
}
</style>
