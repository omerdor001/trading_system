<template>
  <header>
    <div class="header-content">
      <div class="left-buttons">
        <img src="@/assets/logo.png" alt="LASMONY" class="logo">
        <PrimeButton label="Home" @click="goHome" class="p-button-primary" />
        <PrimeButton label="Search Product" @click="goToSearchProduct" class="p-button-primary" />
        <PrimeButton label="Search Store" @click="goToSearchStore" class="p-button-primary" /> <!-- New button -->
      </div>
      <div class="right-buttons">
        <template v-if="isLoggedIn">
          <span class="username">{{userName}}</span>
          <PrimeButton label="Logout" @click="logout" class="p-button-danger" />
        </template>
        <template v-else>
          <PrimeButton label="Login" @click="$router.push('/login')" />
        </template>
        <PrimeButton label="Notifications" @click="notifications" icon="pi pi-bell" />
        <PrimeButton label="Cart" @click="viewCart" icon="pi pi-shopping-cart" />
      </div>
      <p-toast></p-toast>
    </div>
    <PrimeDialog v-model="notificationsVisible" header="Notifications">
      <ul>
        <li v-for="(notification, index) in notifications" :key="index">
          {{ notification.message }}
        </li>
      </ul>
      <Button @click="notificationsVisible = false">Close</Button>
    </PrimeDialog>
  </header>
</template>

<script>
import { defineComponent, ref } from 'vue';
import PrimeButton from 'primevue/button';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';
import axios from 'axios';

export default defineComponent({
  name: 'SiteHeader',
  components: {
    PrimeButton,
    'p-toast': PrimeToast,
  },
  props: {
    isLoggedIn: {
      type: Boolean,
      default: false
    },
  },
  setup() {
    const router = useRouter();
    const toast = useToast();

    const notificationsVisible = ref(false);
    const notifications = ref([]);
    const userName = localStorage.getItem('userName');

    const goHome = () => {
      router.push({ name: 'HomePage' });
    };

    const goToSearchProduct = () => {
      router.push({ name: 'SearchPage' });
    };

    const goToSearchStore = () => {
      router.push({ name: 'StoreSearchPage' });
    };

    const viewCart = () => {
      router.push({ name: 'ShoppingCart' });
    };

    const showNotifications = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/notifications', {
          params: {
            username: localStorage.getItem('username'),
            token: localStorage.getItem('token')
          }
        });
        notifications.value = response.data.notifications;
        notificationsVisible.value = true;
      } catch (error) {
        console.error('Error fetching notifications:', error);
        toast.add({ severity: 'error', summary: 'Error', detail: 'Failed to fetch notifications.', life: 3000 });
      }
    };

    const logout = async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/logout', {
          params: {
            token: localStorage.getItem('token'),
            username: localStorage.getItem('username'),
          }
        });
        localStorage.setItem('username', response.data.username);
        localStorage.setItem('token', response.data.token);
      } catch (error) {
        console.log(error.response.data);
        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data, life: 3000 });
      }
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('isAdmin');
      router.push('/login');
    };

    return {
      userName,
      goHome,
      goToSearchProduct,
      goToSearchStore,
      viewCart,
      showNotifications,
      logout,
      notificationsVisible,
      notifications
    };
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

.p-dialog .p-dialog-content ul {
  list-style-type: none;
  padding: 0;
}

.p-dialog .p-dialog-content li {
  margin-bottom: 10px;
}
</style>
