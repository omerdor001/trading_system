<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Shopping Cart</h2>
        <div v-if="Object.keys(cart.shoppingBags).length != 0">
          <div v-for="(shoppingBag, storeId) in cart.shoppingBags" :key="storeId">
          <h2 style="text-align: left;">{{ shoppingBag.storeId }}</h2>
          <table>
            <thead>
              <tr>
                <th>Product ID</th>
                <th>Category</th>
                <th>Quantity</th>
                <th>Price</th>
                <th>Remove</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(product, productId) in shoppingBag.products_list" :key="productId">
                <td>{{ product.id }}</td>
                <td>{{ product.category }}</td>
                <td>{{ product.quantity }}</td>
                <td>{{ product.price }}</td>
                <td>
                  <button @click="removeFromCart(storeId, productId,product.quantity)">Remove</button>
                </td>
              </tr>
            </tbody>
          </table>
      </div>
      <button @click="buyCart">Buy Cart</button>

        </div>
        <div v-else>
          <p>Your cart is empty.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
// import PrimeButton from 'primevue/button';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';



export default defineComponent({
  name: 'ShoppingCart',
  components: {
    SiteHeader,
    // PrimeButton
  },
  setup() {
    const router = useRouter();
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const cart = ref({ shoppingBags: {} });
    const toast = useToast();


    const totalPrice = computed(() => {
      return cart.value.reduce((total, item) => total + item.price * item.quantity, 0);
    });

    const buyCart = () => {
      router.push('/checkout');
    };

    const removeFromCart = async (storeID, productID, quantity) => {
      try {
        const response = await axios.put('http://localhost:8082/api/trading/cart/remove',null, {
          params: {
            username: username,
            token: token,
            productId : productID,
            storeName: storeID,
            quantity: quantity,
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: response.data, life: 3000 });
        fetchUserCart();
      } catch (err) {
        err.value = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    const fetchUserCart = async () => {
      try {
      const response = await axios.get('http://localhost:8082/api/trading/cart/view', {
      params : {
        username : username,
        token : token,
      },
    });
      toast.add({ severity: 'success', summary: 'success', detail: response.data , life: 5000 });
      toast.add({ severity: 'success', summary: 'success', detail: typeof(response.data), life: 5000 });
      cart.value = response.data;
      toast.add({ severity: 'success', summary: 'success', detail: cart.value.shoppingBags, life: 5000 });

    } catch (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.response?.data || 'Failed to load stores', life: 3000 });
      }
    };


    onMounted(fetchUserCart);

    return {
      username,
      token,
      cart,
      fetchUserCart,
      removeFromCart,
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

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

thead th, tbody td {
  padding: 10px;
  text-align: center;
  border: 1px solid #ddd;
}
thead th {
  background-color: #f2f2f2;
}
</style>
