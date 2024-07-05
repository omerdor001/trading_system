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
    </div>

    <!-- Product Details Modal -->
    <div v-if="selectedProduct" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h3>Product Details</h3>
        <hr>
        <p><strong>Name:</strong> {{ selectedProduct.name }}</p>
        <p><strong>Description:</strong> {{ selectedProduct.description }}</p>
        <!-- Add more details as needed -->
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue';
import {  useRoute } from 'vue-router';
import SiteHeader from '@/components/SiteHeader.vue';

export default {
  name: 'ProductList',
  components: {
    SiteHeader,
  },
  setup() {
    const products = ref([]);
    const route = useRoute();
    const selectedProduct = ref(null);

    onMounted(async () => {
      const storeName = route.params.storeName;
      // Fetch products data (replace with your actual data fetching logic)
      try {
        products.value = await fetchProducts(storeName); 
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    });

    const showProductDetails = (product) => {
      selectedProduct.value = product;
    };

    const closeModal = () => {
      selectedProduct.value = null;
    };



    // Mock function to fetch products (replace with actual API call)
    const fetchProducts = async () => {
      // Replace with actual API call to fetch products
      return [
        { id: 1, name: 'Product A', description: 'Description for Product A' },
        { id: 2, name: 'Product B', description: 'Description for Product B' },
        { id: 3, name: 'Product C', description: 'Description for Product C' }
      ];
    };

    return {
      products,
      showProductDetails,
      selectedProduct,
      closeModal
    };
  }
};
</script>

<style scoped>
.container {
  padding: 20px;
}
.product-list {
  display: flex;
  gap: 20px;
}
.product-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer; 
}
.product-image {
  width: 150px;
  height: 150px;
  background-color: #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
}
.product-name {
  margin-top: 10px;
}

/* Modal Styles */
.modal {
  display: block;
  position: fixed;
  z-index: 1000;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); 
  overflow: auto;
}

.modal-content {
  background-color: #fefefe;
  margin: 15% auto; 
  padding: 20px;
  border: 1px solid #888;
  width: 80%;
  max-width: 600px; /* Max width of modal */
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
  cursor: pointer;
}

.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
</style>
