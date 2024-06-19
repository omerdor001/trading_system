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
import InputText from 'primevue/inputtext';
import Textarea from 'primevue/textarea';
import CreateButton from 'primevue/button';
import { useRouter } from 'vue-router';
import UserViewModel from "@/ViewModel/UserViewModel";

export default defineComponent({
  name: 'OpenStore',
  components: {
    SiteHeader,
    InputText,
    Textarea,
    CreateButton,
  },
  setup() {
    const router = useRouter();
    const name = ref('');
    const description = ref('');
    const username = ref('');  //TODO fix

    const handleCreateStore = async () => {
      try {
        console.log('Store created with name:', name.value, 'and description:', description.value);
      } catch (error) {
        console.error('Failed to create store:', error.message);
      }
    };

    const logout = () => {
      UserViewModel.actions.logout();
      router.push('/login');};

    return {
      name,
      description,
      username,
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
