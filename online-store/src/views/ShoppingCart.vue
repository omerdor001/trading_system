<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Shopping Cart</h2>
        <div v-if="cartItems.length">
          <DataTable :value="cartItems">
            <Column field="name" header="Name"></Column>
            <Column field="description" header="Description"></Column>
            <Column field="quantity" header="Quantity"></Column>
            <Column field="price" header="Price"></Column>
            <Column field="storeNumber" header="Store Number"></Column>
          </DataTable>
          <div class="total-price">
            <h3>Total Price: {{ totalPrice }}</h3>
          </div>
          <div class="buy-cart">
            <PrimeButton label="Buy Cart" @click="buyCart" class="buy-cart-button" />
          </div>
        </div>
        <div v-else>
          <p>Your cart is empty.</p>
        </div>
      </div>
      <PrimeToast ref="toast" position="top-right" :life="3000"></PrimeToast>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue'; // Import onMounted for lifecycle hook
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { Toast as PrimeToast } from 'primevue/toast';

export default defineComponent({
  name: 'ShoppingCart',
  components: {
    SiteHeader,
    PrimeButton,
    DataTable,
    Column,
    PrimeToast,
  },
  setup() {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const token = ref(localStorage.getItem('token') || '');
    const cartItems = ref([]);
    const totalPrice = ref(null);
    const toast = ref(null);

    const fetchCartItems = async () => {
      try {
        const response = await axios.get("http://localhost:8082/api/trading/cart/view", {
          params: {
            username: username.value,
            token: token.value
          }
        });
        return response.data;
      } catch (error) {
        console.error('Error fetching cart items:', error);
        return null;
      }
    };

    // Method to update cart items
    const updateCartItems = async () => {
      try {
        const items = await fetchCartItems();
        if (items) {
          cartItems.value = items; // Update reactive cartItems with fetched data
        } else {
          // Handle fallback data if API call fails
          cartItems.value = [
            {
              productId: '119-12',
              name: 'White Unisex Tee',
              description: 'Sizes: XS, S, M, L, XL, XXL\nType: T-shirt\nFor: Men, Women',
              quantity: 1,
              price: 20,
              storeNumber: 'Store 1'
            },
            {
              productId: '119-13',
              name: 'Black Unisex Tee',
              description: 'Sizes: XS, S, M, L, XL, XXL\nType: T-shirt\nFor: Men, Women',
              quantity: 2,
              price: 15,
              storeNumber: 'Store 2'
            }
          ];
        }
      } catch (error) {
        console.error('Error updating cart items:', error);
        cartItems.value = []; // Set cartItems to empty array or handle error state
      }
    };

    // Fetch cart items on component mount
    onMounted(() => {
      updateCartItems();
    });

    // Function to fetch total price asynchronously
    const fetchTotalPrice = async () => {
      try {
        const response = await axios.get("http://localhost:8082/api/trading/calculatePrice", {
          params: {
            username: username.value,
            token: token.value
          }
        });
        return response.data;
      } catch (error) {
        console.error('Error fetching total price:', error);
        throw error;
      }
    };

    // Method to update total price
    const updateTotalPrice = async () => {
      try {
        const price = await fetchTotalPrice();
        totalPrice.value = price;
      } catch (error) {
        console.error('Error updating total price:', error);
        totalPrice.value = null;
      }
    };

    // Fetch total price on component mount
    onMounted(() => {
      updateTotalPrice();
    });

    const buyCart = () => {
      router.push('/checkout');
    };

    const logout = async () => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/logout', null, {
          params: {
            username: localStorage.getItem('username') || '',
            token: localStorage.getItem('token') || ''
          }
        });

        if (response.status === 200) {
          showSuccessToast('Successfully logged out');
          localStorage.removeItem('isLoggedIn');
          localStorage.removeItem('username');
          router.push('/login');
        } else {
          showErrorToast(`Failed to log out: ${response.statusText}`);
        }
      } catch (error) {
        console.error('Error logging out:', error);
        showErrorToast(`Failed to log out: ${error.message}`);
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

    return {
      username,
      cartItems,
      totalPrice,
      buyCart,
      logout
    };
  }
});
</script>

<style scoped>
.main-content {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.content {
  flex: 2;
  padding: 20px;
}

.cart-list {
  list-style-type: none;
  padding: 0;
}

.cart-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.product-image {
  width: 150px;
  height: 150px;
  margin-right: 20px;
  border-radius: 8px;
  border: 1px solid #ccc;
}

.item-details {
  display: flex;
  flex-direction: column;
}

.total-price {
  text-align: right;
  margin-top: 20px;
  font-size: 1.5em;
}

.buy-cart {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.buy-cart-button {
  background-color: #e67e22 !important;
  border: none !important;
  padding: 10px 20px !important;
  cursor: pointer !important;
  color: white !important;
  border-radius: 5px !important;
  font-weight: bold !important;
  transition: background-color 0.3s !important;
}

.buy-cart-button:hover {
  background-color: #d35400 !important;
}
</style>
