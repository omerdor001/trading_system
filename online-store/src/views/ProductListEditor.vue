<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="container">
      <h2>{{ storeName }} Products</h2>
      <div class="button-wrapper">
        <PrimeButton  v-if="isEditSupply" label="Add Product" icon="pi pi-plus" class="custom-button" @click="openAddProductModal" />
        <PrimeButton  v-if="isEditSupply" label="Remove Product" icon="pi pi-minus" class="custom-button" @click="openRemoveProductModal" />
      </div>
      <div class="product-list">
        <div v-for="product in products" :key="product.id" class="product-item" @click="showProductDetails(product)">
          <div class="product-name">{{ product.name }}</div>
        </div>
      </div>
    </div>

    <div v-if="selectedProduct" class="custom-modal">
     <div class="custom-modal-content">
    <button @click="closeModal" class="custom-modal-close">&times;</button>
    <h3>Product Details</h3>
    <div class="product-info">
          <p><span class="product-label">ID: </span> {{ selectedProduct.id }}</p>
          <p><span class="product-label">Name: </span> {{ selectedProduct.name }}</p>
          <p><span class="product-label">Description: </span> {{ selectedProduct.description }}</p>
          <p><span class="product-label">Price: </span> {{ selectedProduct.price }}$</p>
          <p><span class="product-label">Quantity: </span> {{ selectedProduct.quantity }}</p>
          <p><span class="product-label">Rating: </span> {{ selectedProduct.rating }}</p>
          <p><span class="product-label">Category: </span> {{ selectedProduct.category }}</p>
          <p><span class="product-label">Keywords: </span> {{ selectedProduct.keyWords }}</p>
        </div>
    <div class="modal-button-wrapper">
      <PrimeButton v-if="isEditSupply" label="Edit Product" class="custom-button" @click="editProduct(selectedProduct.id)" />
    </div>
  </div>
</div>

    <div v-if="showAddProduct" class="custom-modal">
      <div class="custom-modal-content">
        <button @click="closeModal" class="custom-modal-close">&times;</button>
        <h3>Add Product</h3>
        <form @submit.prevent="addProduct">
          <div class="p-field">
            <label for="id">ID </label>
            <InputNumber v-model="newProduct.id" required />
          </div>
          <div class="p-field">
            <label for="name">Name </label>
            <InputText v-model="newProduct.name" required />
          </div>
          <div class="p-field">
            <label for="description">Description </label>
            <InputText v-model="newProduct.description" required />
          </div>
          <div class="p-field">
            <label for="price">Price </label>
            <InputNumber v-model="newProduct.price" required mode="currency" currency="USD" locale="en-US" />
          </div>
          <div class="p-field">
            <label for="quantity">Quantity </label>
            <InputNumber v-model="newProduct.quantity" required />
          </div>
          <div class="p-field">
            <label for="rating">Rating </label>
            <InputNumber v-model="newProduct.rating" required />
          </div>
          <div class="p-field">
            <label for="category">Category </label>
            <InputText v-model="newProduct.category" required />
          </div>
          <div class="p-field">
            <label for="keyWords">Keywords </label>
            <InputText v-model="newProduct.keyWords" required class="last-input" />
          </div>
          <div class="button-wrapper">
            <PrimeButton type="submit" label="Add Product" class="custom-button" />
          </div>
        </form>
      </div>
    </div>

    <div v-if="showRemoveProduct" class="custom-modal">
      <div class="custom-modal-content">
        <button @click="closeModal" class="custom-modal-close">&times;</button>
        <h3>Remove Product</h3>
        <form @submit.prevent="removeProduct">
          <div class="p-field">
            <label for="productId">Product ID </label>
            <InputText v-model="productIdToRemove" required />
          </div>
          <div class="button-wrapper">
            <PrimeButton type="submit" label="Remove Product" class="custom-button" />
          </div>
        </form>
      </div>
    </div>
    <PrimeToast />
  </div>
</template>

<script>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';
import { useToast } from 'primevue/usetoast';
import InputText from 'primevue/inputtext';
import InputNumber from 'primevue/inputnumber';
import PrimeButton from 'primevue/button';

export default {
  name: 'ProductList',
  components: {
    SiteHeader,
    InputText,
    InputNumber,
    PrimeButton,
  },
  setup() {
    const products = ref([]);
    const selectedProduct = ref(null);
    const route = useRoute();
    const router = useRouter();
    const storeName = ref(route.params.storeName);
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();
    const isEditSupply = ref(route.params.isEditSupply);
    const showAddProduct = ref(false);
    const showRemoveProduct = ref(false);
    const newProduct = ref({
      id: '',
      name: '',
      description: '',
      price: 0,
      quantity: 0,
      rating: 0,
      category: '',
      keyWords: '',
    });
    const productIdToRemove = ref('');

    onMounted(() => {
      fetchProducts();
    });
    
    const fetchProducts = async () => {
      try {
        const response = await axios.get(`http://localhost:8082/api/trading/products_of_store?storeName=${storeName.value}&username=${username}&token=${token}`);
        products.value = response.data.map(product => ({
          id: product.id,
          name: product.name,
          description: product.description,
          price: product.price,
          quantity: product.quantity,
          rating: product.rating,
          category: product.category,
          keyWords: Array.isArray(product.keyWords) ? product.keyWords.join(', ') : product.keyWords.toString(),
        }));
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    };

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
        const keyWordsArray = newProduct.value.keyWords.split(',').map(word => word.trim());
        const keyWordsString = keyWordsArray.join(',');
        const response = await axios.post(`http://localhost:8082/api/trading/product/add`, null, {
          params: {
            username: username,
            token: token,
            storeName: storeName.value,
            productId: newProduct.value.id,
            productName: newProduct.value.name,
            productDescription: newProduct.value.description,
            productPrice: newProduct.value.price,
            productQuantity: newProduct.value.quantity,
            rating: newProduct.value.rating,
            category: newProduct.value.category,
            keyWords: keyWordsString,
            photoUrl: newProduct.value.photoUrl,
          }
        });
        products.value.push(response.data);
        toast.add({ severity: 'success', summary: 'Success', detail: 'Product added successfully', life: 3000 });
        closeModal();
        fetchProducts();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    };

    const removeProduct = async () => {
      try {
        await axios.delete(`http://localhost:8082/api/trading/product/remove`, {
          params: {
            username: username,
            token: token,
            storeName: storeName.value,
            productId: productIdToRemove.value,
          }
        });
        products.value = products.value.filter(product => product.id !== productIdToRemove.value);
        toast.add({ severity: 'success', summary: 'Success', detail: 'Product removed successfully', life: 3000 });
        closeModal();
        fetchProducts();
      } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.message, life: 3000 });
      }
    };

    const editProduct = (productId) => {
      router.push({ name: 'ProductManagement', params: { storeName: storeName.value, productId } });
    };

    return {
      isEditSupply,
      storeName,
      products,
      selectedProduct,
      showAddProduct,
      showRemoveProduct,
      newProduct,
      productIdToRemove,
      showProductDetails,
      closeModal,
      openAddProductModal,
      openRemoveProductModal,
      addProduct,
      removeProduct,
      editProduct,
    };
  },
};
</script>

<style scoped>
.container {
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
}

.product-list {
  display: flex;
  flex-wrap: wrap;
}

.product-item {
  width: 150px;
  height: 150px;
  margin: 10px;
  border: 1px solid black;
  background-color: #f0f0f0;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  justify-content: center;
  font-size: 30px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
}

.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
  text-align: left;
}

.custom-modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 400px;
  position: relative; 
}

.custom-modal-close {
  position: absolute;
  top: 10px;
  right: 20px;
  font-size: 24px;
  border: none;
  background: none;
  cursor: pointer;
}

.custom-button {
  margin: 5px;
}

.button-wrapper {
  display: flex;
  justify-content: center;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
}

.p-field {
  margin-bottom: 10px;
}

.last-input {
  margin-bottom: 0;
}

.custom-modal-content .custom-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  cursor: pointer;
  border-radius: 5px;
}

.custom-modal-content .custom-button:hover {
  background-color: #6EB5FF;
}
</style>

