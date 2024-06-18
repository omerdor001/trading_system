<template>
  <header>
    <div class="header-content">
      <div class="left-buttons">
        <img src="@/assets/logo.png" alt="LASMONY" class="logo">
        <PrimeButton label="Home" @click="goHome" class="p-button-primary" />
      </div>
      <div class="right-buttons">
        <template v-if="isLoggedIn">
          <span class="username">{{ username }}</span>
          <PrimeButton label="Logout" @click="logout" class="p-button-danger" />
        </template>
        <template v-else>
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
import { useRouter } from 'vue-router';

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
  setup() {
    const router = useRouter();

    const goHome = () => {
      router.push({ name: 'HomePage' });
    };

    const viewCart = () => {
      router.push({ name: 'ShoppingCart' });
    };

    return {
      goHome,
      viewCart
    };
  },
  methods: {
    notifications() {
      // handle notifications
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
  height: 100px;
}

.logo {
  height: 80px;
  width: auto;
}

.left-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.right-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.username {
  color: white;
  margin-right: 10px;
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

.p-button-primary {
  background-color: #007bff !important;
}

.p-button:hover {
  background-color: #d35400 !important;
}
</style>
