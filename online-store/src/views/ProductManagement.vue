<template>
  <div>
    <SiteHeader :isLoggedIn="isLoggedIn" :username="username" @logout="logout" />
    <div class="container">
      <h2>Choose What To Change</h2>
      <div class="fields">
        <PrimeButton v-for="field in fields" :key="field" :label="field" @click="selectField(field)" />
      </div>
      <div v-if="selectedField">
        <h3>{{ selectedField }}</h3>
        <InputText v-model="newDetail" placeholder="New Detail" />
        <PrimeButton label="Change" @click="updateField" />
      </div>
      <div class="button-group">
        <PrimeButton label="Finish" @click="finish" class="finish-button" />
      </div>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { InputText } from 'primevue/inputtext';
import { Button as PrimeButton } from 'primevue/button';
import { Toast as PrimeToast } from 'primevue/toast';
import SiteHeader from '@/components/SiteHeader.vue';
import axios from 'axios';

export default {
  name: 'ProductManagement',
  components: {
    InputText,
    PrimeButton,
    PrimeToast,
    SiteHeader
  },
  setup() {
    const fields = ref(['Name', 'Description', 'Price', 'Quantity']);
    const selectedField = ref('');
    const newDetail = ref('');
    const route = useRoute();
    const router = useRouter();
    const toast = ref(null);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');

    const selectField = (field) => {
      selectedField.value = field;
    };

    const showSuccessToast = (message) => {
      toast.value.add({
        severity: 'success',
        summary: 'Success',
        detail: message,
        life: 3000,
      });
    };

    const showErrorToast = (message) => {
      toast.value.add({
        severity: 'error',
        summary: 'Error',
        detail: message,
        life: 5000,
      });
    };

    const updateField = async () => {
      const storeName = route.params.storeName;
      const productId = route.params.productId;

      try {
        let response;
        switch (selectedField.value) {
          case 'Name':
            response = await setProductName(username, token, storeName, productId, newDetail.value);
            break;
          case 'Description':
            response = await setProductDescription(username, token, storeName, productId, newDetail.value);
            break;
          case 'Price':
            response = await setProductPrice(username, token, storeName, productId, parseFloat(newDetail.value));
            break;
          case 'Quantity':
            response = await setProductQuantity(username, token, storeName, productId, parseInt(newDetail.value, 10));
            break;
          default:
            showErrorToast('Invalid field selected');
            return;
        }
        if (response.status === 200) {
          showSuccessToast(`Product ${selectedField.value.toLowerCase()} updated successfully`);
        } else {
          showErrorToast(`Failed to update product ${selectedField.value.toLowerCase()}: ${response.statusText}`);
        }
      } catch (error) {
        showErrorToast(`Failed to update product field: ${error.message}`);
      }
    };

    const finish = () => {
      const storeName = route.params.storeName;
      router.push({ name: 'ProductList', params: { storeName } });
    };

    const setProductName = (username, token, storeName, productId, productName) => {
      return axios.post('http://localhost:8082/api/trading/setProductName', null, {
        params: {
          username,
          token,
          storeName,
          productId,
          productName
        }
      });
    };

    const setProductDescription = (username, token, storeName, productId, productDescription) => {
      return axios.post('http://localhost:8082/api/trading/setProductDescription', null, {
        params: {
          username,
          token,
          storeName,
          productId,
          productDescription
        }
      });
    };

    const setProductPrice = (username, token, storeName, productId, productPrice) => {
      return axios.post('http://localhost:8082/api/trading/setProductPrice', null, {
        params: {
          username,
          token,
          storeName,
          productId,
          productPrice
        }
      });
    };

    const setProductQuantity = (username, token, storeName, productId, productQuantity) => {
      return axios.post('http://localhost:8082/api/trading/setProductQuantity', null, {
        params: {
          username,
          token,
          storeName,
          productId,
          productQuantity
        }
      });
    };

    const logout = async () => {
      try {
        await axios.post('http://localhost:8082/api/trading/logout', null, {
          params: {
            username: username.value,
            token: token.value
          }
        });
        localStorage.removeItem('isLoggedIn');
        localStorage.removeItem('username');
        localStorage.removeItem('token');
        router.push('/login');
      } catch (error) {
        showErrorToast(`Error during logout: ${error.message}`);
      }
    };

    return {
      fields,
      selectedField,
      newDetail,
      selectField,
      updateField,
      finish,
      logout,
      toast
    };
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
}

.fields {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.button-group {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.finish-button {
  width: 100px;
}
</style>
