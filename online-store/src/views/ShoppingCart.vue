<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="content">
        <h2>Shopping Cart</h2>
        <div v-if="cartItems.length">
          <ul class="cart-list">
            <li v-for="item in cartItems" :key="item.productId" class="cart-item">
              <img :src="item.image" alt="Product Image" class="product-image">
              <div class="item-details">
                <h3>{{ item.name }}</h3>
                <p>{{ item.description }}</p>
                <p><strong>Quantity:</strong> {{ item.quantity }}</p>
                <p><strong>Price:</strong> {{ item.price }}</p>
                <p><strong>Store Number:</strong> {{ item.storeNumber }}</p>
              </div>
            </li>
          </ul>
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
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, computed } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import PrimeButton from 'primevue/button';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ShoppingCart',
  components: {
    SiteHeader,
    PrimeButton
  },
  setup() {
    const router = useRouter();
    const username = ref(localStorage.getItem('username') || '');
    const cartItems = ref([
      // Example data, replace with actual data fetching
      {
        productId: '119-12',
        name: 'White Unisex Tee',
        description: 'Sizes: XS, S, M, L, XL, XXL\nType: T-shirt\nFor: Men, Women',
        image: 'https://via.placeholder.com/150',
        quantity: 1,
        price: 20,
        storeNumber: 'Store 1'
      },
      {
        productId: '119-13',
        name: 'Black Unisex Tee',
        description: 'Sizes: XS, S, M, L, XL, XXL\nType: T-shirt\nFor: Men, Women',
        image: 'https://via.placeholder.com/150',
        quantity: 2,
        price: 15,
        storeNumber: 'Store 2'
      }
    ]);

    const totalPrice = computed(() => {
      return cartItems.value.reduce((total, item) => total + item.price * item.quantity, 0);
    });

    const buyCart = () => {
      router.push('/checkout');
    };

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
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
