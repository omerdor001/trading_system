<template>
  <header>
    <div class="header-content">
      <img src="@/assets/logo.png" alt="LASMONY" class="logo">
      <div class="right-buttons">
        <template v-if="isLoggedIn">
          <button @click="logout">Logout</button>
        </template>
        <template v-else>
          <button @click="$router.push('/register')">Register</button>
          <button @click="$router.push('/login')">Login</button>
        </template>
        <button @click="notifications">Notifications</button>
        <button @click="viewCart">Cart</button>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  name: 'SiteHeader',
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
}
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

.right-buttons button {
  background-color: #e67e22;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  color: white;
  border-radius: 5px;
  font-weight: bold;
  transition: background-color 0.3s;
}

.right-buttons button:hover {
  background-color: #d35400;
}
</style>
