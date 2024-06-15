<template>
  <div>
    <header>
      <div class="header-content">
        <img src="@/assets/logo.png" alt="LASMONY" class="logo">
        <div class="right-buttons">
          <button @click="notifications">Notifications</button>
          <button @click="viewCart">Cart</button>
        </div>
      </div>
    </header>
    <div class="login-container">
      <div class="login-form">
        <h2>Login</h2>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">Username</label>
            <input type="text" v-model="username" id="username" required>
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input type="password" v-model="password" id="password" required>
          </div>
          <div class="button-group">
            <button type="submit" class="approve-button">Approve</button>
            <button type="button" @click="closeModal" class="close-button">Back</button>
          </div>
        </form>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import UserViewModel from '@/ViewModel/UserViewModel';

export default {
  name: 'LoginModal',
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    async handleLogin() {
      let roles = [];
      if (this.username === 'admin' && this.password === 'admin') {
        roles.push('storeManager', 'commercialManager', 'storeOwner', 'systemManager');
      }
      if (this.username === 'manager' && this.password === 'manager') {
        roles.push('storeManager');
      }
      if (this.username === 'system' && this.password === 'system') {
        roles.push('systemManager');
      }
      if (this.username === 'commercial' && this.password === 'commercial') {
        roles.push('commercialManager');
      }
      if (this.username === 'lana' && this.password === 'lana') {
        roles.push('commercialManager', 'systemManager', 'storeOwner');
      }
      if (this.username === 'user' && this.password === 'user') {
        roles = [];
      }

      try {
        await UserViewModel.actions.login('', this.username, this.username, this.password);
        localStorage.setItem('roles', JSON.stringify(roles));
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('username', this.username);
        this.errorMessage = '';
        this.$router.push('/');
      } catch (error) {
        this.errorMessage = error.message;
      }
    },
    closeModal() {
      this.$router.push('/');
    },
    notifications() {
      // handle notifications
    },
    viewCart() {
      // handle viewing cart
    },
    setTestUser(user) {
      this.username = user.username;
      this.password = user.password;
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
  height: 100px;
}

.logo {
  height: 80px;
  width: auto;
}

.right-buttons {
  display: flex;
  gap: 10px;
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

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: rgba(0, 0, 0, 0.5);
}

.login-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-form h2 {
  margin-bottom: 20px;
  color: #425965;
}

.form-group {
  width: 100%;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
}

.form-group input {
  width: calc(100% - 16px);
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.button-group {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.button-group button {
  width: calc(100% - 16px);
  padding: 10px;
  margin: 5px 0;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.approve-button {
  background-color: #e67e22;
  color: white;
}

.approve-button:hover {
  background-color: #d35400;
}

.close-button {
  background-color: #d35400;
  color: white;
}

.close-button:hover {
  background-color: #e67e22;
}

.error {
  color: red;
  margin-top: 10px;
}

.predefined-users {
  margin-top: 20px;
  text-align: left;
}

.predefined-users h3 {
  margin-bottom: 10px;
}

.predefined-users ul {
  list-style: none;
  padding: 0;
}

.predefined-users li {
  cursor: pointer;
  margin-bottom: 5px;
  color: #333;
}

.predefined-users li:hover {
  text-decoration: underline;
}
</style>
