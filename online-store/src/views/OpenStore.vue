<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="create-store-container">
      <div class="create-store-form">
        <h2>Create Store</h2>
        <form @submit.prevent="handleCreateStore">
          <div class="form-group">
            <label for="name">Name</label>
            <InputText v-model="name" id="name" required />
          </div>
          <div class="form-group">
            <label for="description">Description</label>
            <Textarea v-model="description" id="description" required />
          </div>
          <div class="button-group">
            <CreateButton type="submit" label="Create" class="create-button" />
          </div>
        </form>
      </div>
    </div>
    <p-toast></p-toast>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import axios from 'axios';
import SiteHeader from '@/components/SiteHeader.vue';
import InputText from 'primevue/inputtext';
import Textarea from 'primevue/textarea';
import CreateButton from 'primevue/button';
import UserViewModel from "@/ViewModel/UserViewModel";
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';

export default defineComponent({
  name: 'OpenStore',
  components: {
    SiteHeader,
    InputText,
    Textarea,
    CreateButton,
    'p-toast': PrimeToast,
  },
  setup() {
    const router = useRouter();
    const name = ref('');
    const description = ref('');
    const username = localStorage.getItem('username'); 
    const token = localStorage.getItem('token'); 
    const toast = useToast();

    const handleCreateStore = async () => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/create-store',null, {
            params : { 
            username: username,
            token: token,
            storeName: name.value,
            description: description.value
            }
        });
        console.log(response.data);
        toast.add({ severity: 'success', summary: 'Success', detail: 'Store was opened Successfully', life: 3000 });
        name.value = '';
        description.value = '';
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response.data || 'Failed to open store', life: 3000 });
        console.error('Failed to create store:', error.message);
      }
    };

    const logout = () => {
      UserViewModel.actions.logout();
      router.push('/login');
    };

    return {
      name,
      description,
      username,
      token,
      handleCreateStore,
      logout,
    };
  }
});
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
