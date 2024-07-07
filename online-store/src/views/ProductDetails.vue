<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <Toast ref="toast" />
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
              <PrimeButton label="Add To Cart" @click="addToCart" class="action-button"/>
              <PrimeButton label="Buy It Now" @click="buyNow" class="action-button"/>
              <PrimeButton label="Place Bid" @click="priceDialogVisible=true" class="action-button"/>
              <Dialog header="Place Bid" v-model="priceDialogVisible">
              <div class="p-field">
                <label for="bidPrice">Enter Price</label>
                  <InputNumber v-model="bidPrice" :min="0" mode="currency" currency="USD" locale="en-US" />
              </div>
              <div class="p-field">
                  <PrimeButton label="Submit" @click="submitBid"/>
              </div>
              </Dialog>
            </div>
          </div>
          <div class="product-ids">
            <p><strong>Product ID:</strong> {{ product.productId }}</p>
            <p><strong>Order ID:</strong> {{ product.orderId }}</p>
            <p><strong>Store ID:</strong> {{ product.storeId }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
import { Button as PrimeButton } from 'primevue/button';
import Toast from 'primevue/toast';
import { useToast } from 'primevue/usetoast';
import  Dialog  from 'primevue/dialog';
import  InputNumber from 'primevue/inputnumber';
import axios from 'axios';




export default defineComponent({
  name: 'ProductDetails',
  components: {
    SiteHeader,
    PrimeButton,
    Dialog,
    InputNumber,
    Toast
  },
  props: {
    productId: {
      type: String,
      required: true
    }
  },
  setup() {
    const router = useRouter();
    const toast = useToast();
    const username = ref(localStorage.getItem('username') || '');
    var priceDialogVisible = ref(false);
    const bidPrice = ref(null); // Ensure price is defined
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

    const backToStore = () => {
      router.push({name: 'StoreDetails', params: {storeId: product.value.storeId}});
    };


    const addToCart = () => {
      // Simulate adding to cart with a success message
      if (quantity.value > 0) {
        toast.add({severity: 'success', summary: 'Success', detail: 'Product added to cart', life: 3000});
      } else {
        toast.add({severity: 'error', summary: 'Error', detail: 'Failed to add product to cart', life: 3000});
      }
    };

    const buyNow = () => {
      console.log('Buying now:', product.value.name, quantity.value);
    };

    const submitBid = async () => {
  try {
    const product_id = product.value.productId
    const token = localStorage.getItem('token')
    await axios.post(`/store/${product.value.storeId}/place-bid`, null, {
      params: {
        username,
        token,
        product_id,
        bidPrice : bidPrice.value
      }
    });
    priceDialogVisible.value = false;
    alert('Bid placed successfully!');
  } catch (error) {
    console.error('Error placing bid:', error);
    alert('Failed to place bid. Please try again.');
  }
};

    const logout = () => {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('username');
      router.push('/login');
    };

    return {
      username,
      priceDialogVisible,
      product,
      quantity,
      backToStore,
      addToCart,
      buyNow,
      logout,
      submitBid
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
