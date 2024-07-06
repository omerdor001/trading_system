<template>
  <div>
    <SiteHeader />
    <div class="container">
      <h2>Products Of Store</h2>
      <div class="product-list">
        <div v-for="product in products" :key="product.id" class="product-item" @click="showProductDetails(product)">
          <div class="product-image">150 x 150</div>
          <div class="product-name">{{ product.name }}</div>
        </div>
      </div>
      <div class="button-wrapper">
        <PrimeButton label="Add Product" icon="pi pi-plus" class="custom-button" @click="openAddProductModal" />
        <PrimeButton label="Remove Product" icon="pi pi-minus" class="custom-button" @click="openRemoveProductModal" />
      </div>
    </div>

    <!-- Product Details Modal -->
    <PrimeDialog header="Product Details" :visible="selectedProduct !== null" modal @hide="closeModal">
      <p><strong>Name:</strong> {{ selectedProduct?.name }}</p>
      <p><strong>Description:</strong> {{ selectedProduct?.description }}</p>
      <p><strong>Price:</strong> {{ selectedProduct?.price }}</p>
      <p><strong>Quantity:</strong> {{ selectedProduct?.quantity }}</p>
      <p><strong>Rating:</strong> {{ selectedProduct?.rating }}</p>
      <p><strong>Category:</strong> {{ selectedProduct?.category }}</p>
      <p><strong>Keywords:</strong> {{ selectedProduct?.keyWords }}</p>
    </PrimeDialog>

    <!-- Add Product Modal -->
    <PrimeDialog header="Add Product" :visible="showAddProduct" modal @check.self="closeModal">
      <form @submit.prevent="addProduct">
        <div class="p-fluid">
          <div class="p-field">
            <label for="name">Name</label>
            <InputText v-model="newProduct.name" required />
          </div>
          <div class="p-field">
            <label for="description">Description</label>
            <InputText v-model="newProduct.description" required />
          </div>
          <div class="p-field">
            <label for="price">Price</label>
            <InputNumber v-model="newProduct.price" required mode="currency" currency="USD" locale="en-US" />
          </div>
          <div class="p-field">
            <label for="quantity">Quantity</label>
            <InputNumber v-model="newProduct.quantity" required />
          </div>
          <div class="p-field">
            <label for="rating">Rating</label>
            <InputNumber v-model="newProduct.rating" required />
          </div>
          <div class="p-field">
            <label for="category">Category</label>
            <InputText v-model="newProduct.category" required />
          </div>
          <div class="p-field">
            <label for="keyWords">Keywords</label>
            <InputText v-model="newProduct.keyWords" required class="last-input" />
          </div>
        </div>
        <div class="button-wrapper">
          <PrimeButton type="submit" label="Add Product" class="custom-button" />
        </div>
      </form>
    </PrimeDialog>

    <!-- Remove Product Modal -->
    <PrimeDialog header="Remove Product" :visible="showRemoveProduct" modal @hide="closeModal">
      <form @submit.prevent="removeProduct">
        <div class="p-field">
          <label for="productId">Product ID</label>
          <InputText v-model="productIdToRemove" required />
        </div>
        <div class="button-wrapper">
          <PrimeButton type="submit" label="Remove Product" class="custom-button" />
        </div>
      </form>
    </PrimeDialog>

    <PrimeToast />
  </div>
</template>

<script>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';
import PrimeToast from 'primevue/toast';
import PrimeDialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber';
import PrimeButton from 'primevue/button';

export default {
  name: 'ProductList',
  components: {
    SiteHeader,
    PrimeToast,
    PrimeDialog,
    InputText,
    InputNumber,
    PrimeButton,
  },
  setup() {
    const products = ref([]);
    const selectedProduct = ref(null);
    const route = useRoute();
    const storeName = ref(route.params.storeName);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();

    const showAddProduct = ref(false);
    const showRemoveProduct = ref(false);
    const newProduct = ref({
      name: '',
      description: '',
      price: 0,
      quantity: 0,
      rating: 0,
      category: '',
      keyWords: '',
    });
    const productIdToRemove = ref('');

    onMounted(async () => {
      try {
        const response = await axios.get('http://localhost:8082/api/trading/products_of_store', {
          params: { 
            storeName: storeName.value,
            username: username, 
            token: token
          }
        });
        products.value = response.data.map(product => ({
          id: product.id,
          name: product.name,
          description: product.description,
          price: product.price,
          quantity: product.quantity,
          rating: product.rating,
          category: product.category,
          keyWords: product.keyWords,
        }));
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    });

    const showProductDetails = (product) => {
      selectedProduct.value = product;
    };

    const closeModal = () => {
      selectedProduct.value = null;
      showAddProduct.value = false;
      showRemoveProduct.value = false;
    };

    const openAddProductModal = () => {
      showAddProduct.value = true;
    };

    const openRemoveProductModal = () => {
      showRemoveProduct.value = true;
    };

    const addProduct = async () => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/add_product', {
          storeName: storeName.value,
          product: newProduct.value,
          username: username, 
          token: token
        });
        products.value.push(response.data);
        toast.add({ severity: 'success', summary: 'Success', detail: 'Product added successfully', life: 3000 });
        closeModal();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    };

    const removeProduct = async () => {
      try {
        await axios.delete('http://localhost:8082/api/trading/remove_product', {
          data: {
            storeName: storeName.value,
            productId: productIdToRemove.value,
            username: username,
            token: token
          }
        });
        products.value = products.value.filter(product => product.id !== productIdToRemove.value);
        toast.add({ severity: 'success', summary: 'Success', detail: 'Product removed successfully', life: 3000 });
        closeModal();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    };

    return {
      products,
      showProductDetails,
      selectedProduct,
      closeModal,
      showAddProduct,
      showRemoveProduct,
      openAddProductModal,
      openRemoveProductModal,
      newProduct,
      addProduct,
      productIdToRemove,
      removeProduct
    };
  }
};
</script>

<style scoped>
.container {
  padding: 40px;
}
.product-list {
  display: flex;
  gap: 40px;
}
.product-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.product-image {
  width: 200px;
  height: 200px;
  background-color: #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}
.product-name {
  margin-top: 20px;
}
.button-wrapper {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 20px;
}
.custom-button {
  padding: 10px 20px;
  background-color: #007bff;
  border: none;
  color: white;
  font-size: 16px;
  border-radius: 5px;
  cursor: pointer;
}
.custom-button:hover {
  background-color: #0056b3;
}
.last-input {
  margin-bottom: 20px;
}
</style>
