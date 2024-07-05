<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="suggest-owner">
      <h2>Suggest Owner</h2>
      <form @submit.prevent="suggestOwner">
        <div class="p-field">
          <label for="newOwner">New Owner: </label>
          <InputText id="newOwner" v-model="newOwner" required />
        </div>
        <div class="button-group">
          <PrimeButton label="Submit" type="submit" class="p-mt-2 p-button-primary" />
        </div>
      </form>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
    <p-toast></p-toast>
  </div>
</template>

<script>
import axios from 'axios';
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import { useToast } from 'primevue/usetoast';
import { Toast as PrimeToast } from 'primevue/toast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'SuggestOwner',
  components: {
    SiteHeader,
    InputText,
    PrimeButton,
    'p-toast': PrimeToast,
  },
  setup() {
    const router = useRouter();
    const appoint = ref('');
    const newOwner = ref('');
    const storeName = ref(router.currentRoute.value.params.storeName);
    const error = ref(null);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const isLoggedIn = ref(!!username.value);
    const toast = useToast();

    const suggestOwner = async () => {
      try {
        error.value = null;
        const response = await axios.post('http://localhost:8082/api/trading/suggestOwner',null, {
          params: {
            appoint: username,
            token: token,
            newOwner: newOwner.value,
            storeName: storeName.value,
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: response.data, life: 3000 });
        newOwner.value = '';
      } catch (err) {
        err.value = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }
    };

    const logout = () => {
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      isLoggedIn,
      appoint,
      newOwner,
      storeName,
      error,
      suggestOwner,
      logout,
    };
  },
});
</script>

<style scoped>
.suggest-owner {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  margin: 0 auto;
  text-align: center;
}

.suggest-owner h2 {
  color: #e67e22;
  margin-bottom: 20px;
}

.suggest-owner .p-field {
  margin-bottom: 15px;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-button {
  background-color: #e67e22;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.submit-button:hover {
  background-color: #d35400;
}

.p-error {
  margin-top: 20px;
  color: red;
}
</style>
