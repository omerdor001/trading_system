<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="remove-product-container">
      <div class="remove-product-form">
        <h2>Remove Product</h2>
        <form @submit.prevent="handleRemoveProduct">
          <div class="form-group">
            <label for="storeName">Store Name</label>
            <InputText v-model="storeName" id="storeName" required />
          </div>
          <div class="form-group">
            <label for="productId">Product ID</label>
            <InputNumber v-model="productId" id="productId" required />
          </div>
          <div class="button-group">
            <Button type="submit" label="Remove Product" class="remove-button" />
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
import InputNumber from 'primevue/inputnumber';
import Button from 'primevue/button';
import PrimeToast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'RemoveProduct',
  components: {
    SiteHeader,
    InputText,
    InputNumber,
    Button,
    'p-toast': PrimeToast,
  },
  setup() {
    const router = useRouter();
    const storeName = ref('');
    const productId = ref(null);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();

    const handleRemoveProduct = async () => {
      try {
        const response = await axios.delete('http://localhost:8082/api/trading/product/remove', {
          params: {
            username,
            token,
            storeName: storeName.value,
            productId: productId.value,
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: 'Product was removed successfully', life: 3000 });
        resetForm();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to remove product', life: 3000 });
      }
    };

    const resetForm = () => {
      storeName.value = '';
      productId.value = null;
    };

    return {
      storeName,
      productId,
      handleRemoveProduct,
      username,
      token,
    };
  },
});
</script>

<style scoped>
.remove-product-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 80vh;
  background-color: #f9f9f9;
  padding-top: 50px; /* Add padding to move the form down */
}

.remove-product-form {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  text-align: center;
}

.remove-product-form h2 {
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

.button-group {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.remove-button {
  background-color: #e67e22;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.remove-button:hover {
  background-color: #d35400;
}
</style>
