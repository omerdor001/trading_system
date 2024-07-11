<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <Toast ref="toast" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Back to Store" @click="backToStore" class="sidebar-button" />
      </div>
      <div class="content">
        <div class="product-details" v-if="product">
          <h2>Product Details - {{ product.product_name }}</h2>
          <div class="product-overview">
            <img :src="product.image" alt="Product Image" class="product-image">
            <div class="product-info">
              <h3>{{ product.product_name }}</h3>
              <p>{{ product.product_description }}</p>
              <p><strong>Store:</strong> {{ product.store_name }}</p>
              <p><strong>Quantity:</strong> <input type="number" v-model="quantity" min="1" /></p>
              <PrimeButton label="Add To Cart" @click="addToCart" class="action-button"/>
            </div>
          </div>
          <div class="product-ids">
            <p><strong>Product ID:</strong> {{ product.product_id }}</p>
            <p><strong>Store ID:</strong> {{ product.store_name }}</p>
          </div>
        </div>
        <div v-else>
          <p>Loading product details...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter, useRoute } from 'vue-router';
import PrimeButton from 'primevue/button';
import Toast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import axios from 'axios';

export default defineComponent({
  name: 'ProductDetails',
  components: {
    SiteHeader,
    PrimeButton,
    Toast
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const toast = useToast();
    const username = ref(localStorage.getItem('username') || '');
    const product = ref(null);
    const quantity = ref(1);

    onMounted(() => {
      const { productId, storeId } = route.params;
      fetchProductDetails(productId, storeId);
    });

    const fetchProductDetails = async (productId, storeId) => {
      const token = localStorage.getItem('token');
      if (!token) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invalid token was supplied', life: 3000 });
        return;
      }
      try {
        const response = await axios.get('http://localhost:8082/api/trading/product/info', {
          params: {
            userName: username.value,
            token: token,
            storeName: storeId,
            product_Id: productId
          }
        });
        if (typeof response.data === 'string') {
          product.value = JSON.parse(response.data);
        } else {
          product.value = response.data;
        }
      } catch (error) {
        console.error("Error fetching product details:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load product details', life: 3000 });
      }
    };

    const backToStore = () => {
      router.push({ name: 'StoreDetails', params: { storeId: route.params.storeId } });
    };

    const addToCart = async () => {
      if (quantity.value <= 0) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Quantity must be greater than zero', life: 3000 });
        return;
      }
      const token = localStorage.getItem('token');
      try {
        await axios.post('http://localhost:8082/api/trading/cart/add', null, {
          params: {
            username: username.value,
            token: token,
            productId: product.value.product_id,
            storeName: product.value.store_name || route.params.storeId, // Ensure store name is correctly set
            quantity: quantity.value,
            price: product.value.product_price
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: 'Product added to cart', life: 3000 });
      } catch (error) {
        console.error("Error adding to cart:", error);
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to add product to cart', life: 3000 });
      }
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      localStorage.removeItem('token');
      router.push('/login');
    };

    return {
      username,
      product,
      quantity,
      backToStore,
      addToCart,
      logout
    };
  }
});
</script>

<style scoped>
.main-content {
  display: flex;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
}

.content {
  flex: 2;
  padding: 20px;
}

.product-details {
  padding: 20px;
}

.product-overview {
  display: flex;
  align-items: center;
}

.product-image {
  width: 150px;
  height: 150px;
  margin-right: 20px;
}

.product-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.product-ids {
  margin-top: 20px;
}

.action-button {
  margin-top: 10px;
}

.sidebar-button {
  width: 100%;
  max-width: 150px;
}
</style>
