<template>
    <div>
      <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
      <div class="create-store-container">
        <div class="create-store-form">
          <h2>Create Store</h2>
          <form @submit.prevent="handleCreateStore">
            <div class="form-group">
              <label for="name">Name</label>
              <input type="text" v-model="name" id="name" required />
            </div>
            <div class="form-group">
              <label for="description">Description</label>
              <textarea v-model="description" id="description" required></textarea>
            </div>
            <div class="button-group">
              <button type="submit" class="create-button">Create</button>
              <button type="button" @click="goBack" class="back-button">Back</button>
            </div>
          </form>
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
  
  export default {
    name: 'OpenStore',
    components: {
      SiteHeader,
    },
    data() {
      return {
        name: '',
        description: '',
        username: localStorage.getItem('username') || '',
      };
    },
    methods: {
      handleCreateStore() {
        // Handle store creation logic here
        console.log('Store created with name:', this.name, 'and description:', this.description);
      },
      goBack() {
        this.$router.push('/');
      },
      logout() {
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('roles');
        localStorage.removeItem('username');
        this.$router.push('/login');
      }
    }
  }
  </script>
  
  <style scoped>
  .create-store-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 80vh;
    background-color: #f9f9f9;
  }
  
  .create-store-form {
    background: #fff;
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
    width: 400px;
    text-align: center;
  }
  
  .create-store-form h2 {
    color: #e67e22;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 15px;
    text-align: left;
  }
  
  .form-group label {
    display: block;
    color: #333;
    margin-bottom: 5px;
  }
  
  .form-group input,
  .form-group textarea {
    width: calc(100% - 20px);
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    background-color: #f9f9f9;
  }
  
  .button-group {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
  }
  
  .create-button {
    background-color: #e67e22;
    border: none;
    padding: 10px 20px;
    color: white;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
  }
  
  .create-button:hover {
    background-color: #d35400;
  }
  
  .back-button {
    background-color: #95a5a6;
    border: none;
    padding: 10px 20px;
    color: white;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
  }
  
  .back-button:hover {
    background-color: #7f8c8d;
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
  