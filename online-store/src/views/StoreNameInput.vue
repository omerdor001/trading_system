<template>
  <div>
    <SiteHeader :isLoggedIn="true" />
    <div class="store-name-input-container">
      <PrimeCard class="store-name-input-form">
        <template #title>
          <h2>Enter Store Name</h2>
        </template>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="storeName">Store Name</label>
            <InputText v-model="storeName" id="storeName" required />
          </div>
          <div class="button-group">
            <PrimeButton label="Submit" icon="pi pi-check" type="submit" class="submit-button" />
          </div>
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>
        </form>
      </PrimeCard>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import { Button as PrimeButton } from 'primevue/button';
import { InputText } from 'primevue/inputtext';
import { PrimeCard } from 'primevue/card';
import StoreViewModel from '@/ViewModel/StoreViewModel';

export default {
  name: 'StoreNameInput',
  components: {
    SiteHeader,
    PrimeButton,
    InputText,
    PrimeCard
  },
  setup() {
    const storeName = ref('');
    const errorMessage = ref('');
    const router = useRouter();

    const handleSubmit = async () => {
      const userName = localStorage.getItem('username');
      const token = localStorage.getItem('token');
      try {
        const response = await StoreViewModel.actions.checkStoreExistence(userName, token, storeName.value);
        if (response.exists) {
          router.push({ name: 'ProductList', params: { storeName: storeName.value } });
        } else {
          errorMessage.value = 'Store does not exist. Please enter a valid store name.';
        }
      } catch (error) {
        errorMessage.value = 'Failed to fetch stores. Please try again later.';
      }
    };

    return {
      storeName,
      errorMessage,
      handleSubmit
    };
  }
};
</script>

<style scoped>
.store-name-input-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: rgba(0, 0, 0, 0.5);
}

.store-name-input-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  text-align: center;
}

.store-name-input-form h2 {
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
  justify-content: center;
}

.submit-button .p-button {
  width: 100%;
}

.error-message {
  color: red;
  margin-top: 10px;
}
</style>
