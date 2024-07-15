<template>
  <header>
    <div class="header-content">
      <div class="left-buttons">
        <img src="@/assets/logo.png" alt="LSMAONY" class="logo">
        <PrimeButton label="Home" @click="goHome" class="p-button-primary" />
        <PrimeButton label="Search Product" @click="goToSearchProduct" class="p-button-primary" />
        <PrimeButton label="Search Store" @click="goToSearchStore" class="p-button-primary" />
      </div>
      <div class="right-buttons">
        <template v-if="isLoggedIn">
          <span class="username">{{ userName }}</span>
          <PrimeButton label="Logout" @click="logout" class="p-button-danger" />
        </template>
        <template v-else>
          <PrimeButton label="Login" @click="$router.push('/login')" />
        </template>
        <PrimeButton label="Notifications" @click="loadNotifications()" icon="pi pi-bell" />
        <PrimeButton label="Cart" @click="viewCart" icon="pi pi-shopping-cart" />
      </div>
    </div>
    <PrimeDialog v-model="notificationsVisible" :visible="notificationsVisible" header="Notifications"
      :draggable="false" :closable="false" modal>
      <div v-if="notifications.length > 0">
        <p>{{ notifications.length }} Notifications</p>
        <div class="notification-grid">
          <div class="notification-item" v-for="(notification, index) in paginatedNotifications" :key="index">
            <div class="notification-content">
              <p>{{ notification }}</p>
              <span class="remove-notification" @click="removeNotification(index)">x</span>
            </div>
          </div>
        </div>
        <div class="pagination">
          <PrimePaginator v-model="currentPage" :totalRecords="notifications.length" :rows="notificationsPerPage"
            @onPageChange="onPageChange" />
        </div>
      </div>
      <PrimeButton @click="notificationsVisible = false">Close</PrimeButton>
    </PrimeDialog>
  </header>
</template>

<script>
import { defineComponent, ref, computed } from 'vue';
import PrimeButton from 'primevue/button';
import PrimeDialog from 'primevue/dialog';
import PrimePaginator from 'primevue/paginator';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import axios from 'axios';
import WebSocketService from '@/WebSocketService';

export default defineComponent({
  name: 'SiteHeader',
  components: {
    PrimeButton,
    PrimeDialog,
    PrimePaginator,
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
    const currentPage = ref(1);
    const notificationsPerPage = 5;
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

    const loadNotifications = () => {
      const storedNotifications = JSON.parse(localStorage.getItem('websocketMessages')) || [];
      notifications.value = storedNotifications;
      notificationsVisible.value = true;
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

      WebSocketService.unsubscribe();

      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('isAdmin');
      localStorage.removeItem('websocketMessages');

      router.push('/login');
    };

    const onPageChange = (event) => {
      currentPage.value = event.page + 1;
    };

    const paginatedNotifications = computed(() => {
      const startIndex = (currentPage.value - 1) * notificationsPerPage;
      const endIndex = startIndex + notificationsPerPage;
      return notifications.value.slice(startIndex, endIndex);
    });

    const removeNotification = (index) => {
      notifications.value.splice(index, 1);
      localStorage.setItem('websocketMessages', JSON.stringify(notifications.value));
    };

    return {
      notificationsVisible,
      notifications,
      currentPage,
      notificationsPerPage,
      paginatedNotifications,
      userName,
      goHome,
      goToSearchProduct,
      goToSearchStore,
      viewCart,
      loadNotifications,
      logout,
      onPageChange,
      removeNotification,
    };
  },
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

.p-button-danger {
  background-color: #dc3545 !important;
}

.p-button:hover {
  background-color: #d35400 !important;
}

.notification-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.notification-item {
  padding: 10px;
  border: 1px solid #ccc;
  background-color: #f9f9f9;
  border-radius: 5px;
}

.notification-content {
  display: flex;
  align-items: center;
}

.notification-text {
  flex: 1;
  margin-right: 20px;
  overflow-wrap: break-word;
  word-wrap: break-word;
  word-break: break-word;
}

.remove-notification {
  cursor: pointer;
  color: #dc3545;
  font-size: 1.5rem;
  margin-left: 10px;
}

.remove-notification:hover {
  color: #a71d2a;
}

.pagination {
  margin-top: 10px;
}
</style>