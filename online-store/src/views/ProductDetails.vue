<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Back to Store" @click="backToStore" class="sidebar-button" />
      </div>
      <div class="content">
        <div class="product-details">
          <h2>Product Details - {{ product.name }}</h2>
          <p>Last updated on {{ product.lastUpdated }}</p>
          <div class="product-overview">
            <img :src="product.image" alt="Product Image" class="product-image">
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p>{{ product.description }}</p>
              <p><strong>Store:</strong> {{ product.storeLocation }}</p>
              <p><strong>Quantity:</strong> <input type="number" v-model="quantity" /></p>
              <PrimeButton label="Add To Cart" @click="addToCart" class="action-button" />
              <!-- <PrimeButton label="Buy It Now" @click="buyNow" class="action-button" /> -->
            </div>
          </div>
          <div class="product-ids">
            <p><strong>Product ID:</strong> {{ product.productId }}</p>
            <p><strong>Order ID:</strong> {{ product.orderId }}</p>
            <p><strong>Store ID:</strong> {{ product.storeId }}</p>
          </div>
        </div>
      </div>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';
import { Toast as PrimeToast } from 'primevue/toast';
import axios from 'axios';

export default defineComponent({
  name: 'ProductDetails',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast,
  },
  props: {
    productId: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const router = useRouter();
    const toast = ref(null);
    const product = ref({
      name: 'White Unisex Tee',
      description: 'Sizes: XS, S, M, L, XL, XXL\nType: T-shirt\nFor: Men, Women',
      image: 'https://via.placeholder.com/150',
      storeLocation: 'Manchester, UK',
      lastUpdated: 'Jan 29, 2023, at 2:39 PM',
      productId: '119-12',
      orderId: 'SK19-111',
      storeId: '119'
    });
    const quantity = ref(1);

    const fetchProductInfo = async () => {
      try {
        const storeName = 'Example Store'; // Replace with actual store name
        const response = await axios.get(`http://localhost:8082/api/trading/store/${encodeURIComponent(storeName)}/product/info`, {
          params: {
            userName: localStorage.getItem('username') || '',
            token: localStorage.getItem('token') || '',
            product_Id: props.productId
          }
        });
        return response.data;
      } catch (error) {
        console.error('Error fetching product info:', error);
        return null;
      }
    };
    const updateProductInfo = async () => {
      try {
        const info = await fetchProductInfo();
        if (info) {
          product.value = info; // Update product details with fetched data
        } else {
          // Handle fallback data if API call fails
          product.value = {
            name: 'White Unisex Tee',
            description: 'Sizes: XS, S, M, L, XL, XXL\nType: T-shirt\nFor: Men, Women',
            image: 'https://via.placeholder.com/150',
            storeLocation: 'Manchester, UK',
            lastUpdated: 'Jan 29, 2023, at 2:39 PM',
            productId: '119-12',
            orderId: 'SK19-111',
            storeId: '119'
          };
        }
      } catch (error) {
        console.error('Error updating product info:', error);
        product.value = null;
      }
    };

    const backToStore = () => {
      router.push({ name: 'StoreDetails', params: { storeId: product.value.storeId } });
    };

    const addToCart = async () => {
      const storeName = 'Example Store'; // Replace with actual store name

      try {
        const response = await axios.post('http://localhost:8082/api/trading/cart/add', null, {
          params: {
            username: localStorage.getItem('username') || '',
            token: localStorage.getItem('token') || '',
            productId: props.productId,
            storeName: storeName,
            quantity: quantity.value
          }
        });

        if (response.status === 200) {
          showSuccessToast('Product added to cart');
        } else {
          showErrorToast(`Failed to add product to cart: ${response.message}`);
        }
      } catch (error) {
        console.error('Error adding product to cart:', error);
        showErrorToast(`Failed to add product to cart: ${error.message}`);
      }
    };

    // const buyNow = () => {
    //   console.log('Buying now:', product.value.name, quantity.value);
    // };

    const logout = async () => {
      try {
        await axios.post('http://localhost:8082/api/trading/logout', null, {
          params: {
            username: localStorage.getItem('username') || '',
            token: localStorage.getItem('token') || '',
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

    onMounted(() => {
      updateProductInfo();
    });

    return {
      product,
      quantity,
      backToStore,
      addToCart,
      // buyNow,
      logout,
      toast // Export toast reference for PrimeToast
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
