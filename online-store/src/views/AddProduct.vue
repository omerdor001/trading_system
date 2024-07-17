<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="add-product-container">
      <div class="add-product-form">
        <h2>Add Product</h2>
        <form @submit.prevent="handleAddProduct">
          <div class="form-group">
            <label for="productId">Product ID</label>
            <InputText v-model="productId" id="productId" required />
          </div>
          <div class="form-group">
            <label for="storeName">Store Name</label>
            <InputText v-model="storeName" id="storeName" required />
          </div>
          <div class="form-group">
            <label for="productName">Product Name</label>
            <InputText v-model="productName" id="productName" required />
          </div>
          <div class="form-group">
            <label for="productDescription">Product Description</label>
            <InputText v-model="productDescription" id="productDescription" required autoResize />
          </div>
          <div class="form-group">
            <label for="productPrice">Product Price</label>
            <InputNumber v-model="productPrice" id="productPrice" required mode="currency" currency="USD" locale="en-US" />
          </div>
          <div class="form-group">
            <label for="productQuantity">Product Quantity</label>
            <InputNumber v-model="productQuantity" id="productQuantity" required />
          </div>
          <div class="form-group">
            <label for="rating">Rating</label>
            <InputNumber v-model="rating" id="rating" required />
          </div>
          <div class="form-group">
            <label for="category">Category</label>
            <InputText v-model="category" id="category" required />
          </div>
          <div class="form-group">
            <label for="keyWords">Keywords</label>
            <Chips v-model="keyWords" id="keyWords" />
          </div>
          <div class="button-group">
            <Button type="submit" label="Add Product" class="add-button" />
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
import Chips from 'primevue/chips';
import Button from 'primevue/button';
import PrimeToast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'AddProduct',
  components: {
    SiteHeader,
    InputText,
    InputNumber,
    Chips,
    Button,
    'p-toast': PrimeToast,
  },
  setup() {
    const router = useRouter();
    const productId = ref('');
    const storeName = ref('');
    const productName = ref('');
    const productDescription = ref('');
    const productPrice = ref(null);
    const productQuantity = ref(null);
    const rating = ref(null);
    const category = ref('');
    const keyWords = ref([]);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();

    const handleAddProduct = async () => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/product/add', {
          username,
          token,
          product_id: productId.value,
          store_name: storeName.value,
          product_name: productName.value,
          product_description: productDescription.value,
          product_price: productPrice.value,
          product_quantity: productQuantity.value,
          rating: rating.value,
          category: category.value,
          keyWords: keyWords.value,
        });
        console.log(response.data);
        toast.add({ severity: 'success', summary: 'Success', detail: 'Product was added successfully', life: 3000 });
        resetForm();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to add product', life: 3000 });
      }
    };

    const resetForm = () => {
      productId.value = '';
      storeName.value = '';
      productName.value = '';
      productDescription.value = '';
      productPrice.value = null;
      productQuantity.value = null;
      rating.value = null;
      category.value = '';
      keyWords.value = [];
    };

    return {
      productId,
      storeName,
      productName,
      productDescription,
      productPrice,
      productQuantity,
      rating,
      category,
      keyWords,
      handleAddProduct,
      username,
      token,
    };
  },
});
</script>

<style scoped>
.add-product-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 80vh;
  background-color: #f9f9f9;
  padding-top: 50px; /* Add padding to move the form down */
}

.add-product-form {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  width: 400px;
  text-align: center;
}

.add-product-form h2 {
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

.add-button {
  background-color: #e67e22;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.add-button:hover {
  background-color: #d35400;
}
</style>
