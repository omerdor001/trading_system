

<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <!-- Sidebar buttons remain unchanged -->
      </div>
      <div class="content">
        <!-- AboutSection removed -->
        <div v-if="notifications.length" class="notifications-grid">
          <h2>Notifications</h2>
          <div class="p-grid">
            <div v-for="notification in notifications" :key="notification.id" class="p-col">
              <div class="notification-item" @click="openDialog(notification)">
                <div class="notification-from">{{ notification.from }}</div>
                <div class="notification-sent">{{ notification.sentAt }}</div>
              </div>
            </div>
          </div>
        </div>
        <div v-else>
          <p>No notifications available.</p>
        </div>
      </div>
      <div class="sidebar2">
        <!-- Sidebar2 buttons remain unchanged -->
      </div>
    </div>
    <footer>
      <div class="external-links">
        <!-- External links remain unchanged -->
      </div>
    </footer>

    <!-- Modal dialog for notification content -->
    <div v-if="showDialog">
      <div class="modal">
        <div class="modal-content">
          <h3>{{ selectedNotification.from }}</h3>
          <p>{{ selectedNotification.sentAt }}</p>
          <p>{{ selectedNotification.content }}</p>
          <button @click="closeDialog">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ShowNotifications',
  components: {
    SiteHeader,
  },
  setup() {
    const router = useRouter();
    const isLoggedIn = ref(localStorage.getItem('isLoggedIn') === 'true');
    const username = ref(localStorage.getItem('username') || '');
    const notifications = ref([
      { id: 1, from: 'Admin', sentAt: '2024-06-19 10:00', content: 'Notification content 1' },
      { id: 2, from: 'System', sentAt: '2024-06-19 11:00', content: 'Notification content 2' },
      { id: 3, from: 'Manager', sentAt: '2024-06-19 12:00', content: 'Notification content 3' }
    ]);

    const showDialog = ref(false);
    const selectedNotification = ref(null);

    const logout = () => {
      isLoggedIn.value = false;
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('roles');
      localStorage.removeItem('username');
      router.push('/');
    };

    const openDialog = (notification) => {
      selectedNotification.value = notification;
      showDialog.value = true;
    };

    const closeDialog = () => {
      showDialog.value = false;
    };

    return {
      isLoggedIn,
      username,
      notifications,
      logout,
      showDialog,
      selectedNotification,
      openDialog,
      closeDialog,
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

.notifications-grid {
  margin-top: 20px;
}

.p-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.notification-item {
  padding: 10px;
  border: 1px solid #ccc;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.notification-item:hover {
  background-color: #f0f0f0;
}

.notification-from {
  font-weight: bold;
}

.notification-sent {
  font-size: 0.9em;
  color: #888;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  max-width: 400px;
  width: 100%;
}

.modal-content h3 {
  margin-top: 0;
}

.modal-content button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-content button:hover {
  background-color: #0056b3;
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
