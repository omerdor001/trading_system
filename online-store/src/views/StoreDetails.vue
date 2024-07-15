<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <div class="sidebar">
        <PrimeButton label="Back to Stores" @click="backToStores" class="sidebar-button" />
      </div>
      <div class="content">
        <div class="store-details">
          <h2>Store Name: {{ store.name }}</h2>
          <p>Description: {{ store.description }}</p>

          <h3>Products</h3>
          <table>
            <thead>
            <tr>
              <th>Product Name</th>
              <th>Description</th>
              <th>Price</th>
              <th>Rating</th>
              <th>Quantity</th>
              <th>Add to Cart</th>
              <th>Place Bid</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(product, index) in store.products" :key="product.id">
              <td>{{ product.name }}</td>
              <td>{{ product.description }}</td>
              <td>{{ product.price }}</td>
              <td>{{ product.rating }}</td>
              <td>
                <input type="number" v-model="productQuantities[index]" min="1" />
              </td>
              <td>
                <button @click="addToCart(product.id, productQuantities[index], product.price)">Add to Cart</button>
              </td>
              <td>
                <input type="number" v-model="bidPrices[index]" placeholder="Enter bid" />
                <button @click="placeBid(product.id, bidPrices[index])">Place Bid</button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <PrimeToast ref="toast" />
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter, useRoute } from 'vue-router';
import PrimeButton from 'primevue/button';
import PrimeToast from 'primevue/toast';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';

export default defineComponent({
  name: 'StoreDetails',
  components: {
    SiteHeader,
    PrimeButton,
    PrimeToast
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const username = ref(localStorage.getItem('username') || '');
    const store = ref({
      name: '',
      description: '',
      products: []
    });
    const productQuantities = ref([]);
    const bidPrices = ref([]);
    const toast = useToast();

    onMounted(() => {
      fetchStoreDetails(route.params.storeId);
    });

    const fetchStoreDetails = async (storeId) => {
      const token = localStorage.getItem('token');
      if (!token) {
        toast.add({severity: 'error', summary: 'Error', detail: 'Invalid token was supplied', life: 3000});
        return;
      }
      try {
        const response = await axios.get('http://localhost:8082/api/trading/products_of_store', {
          params: {
            storeName: storeId,
            username: username.value,
            token: token
          }
        });
        const storeProducts = response.data.map(product => ({
          id: product.id,
          name: product.name,
          description: product.description,
          price: product.price,
          rating: product.rating,  // Assuming rating is available in the response
          quantity: product.quantity  // Assuming quantity is available in the response
        }));
        store.value = {
          name: storeId,
          description: 'Store description here...', // Replace with actual description if available
          products: storeProducts
        };
        productQuantities.value = storeProducts.map(() => 1);
        bidPrices.value = storeProducts.map(() => 0);
      } catch (error) {
        if (error.response && error.response.data) {
          toast.add({severity: 'error', summary: 'Error', detail: error.response.data, life: 3000});
        } else {
          toast.add({severity: 'error', summary: 'Error', detail: 'Failed to load store details', life: 3000});
        }
      }
    };

    const addToCart = async (productID, quantity, price) => {
      const token = localStorage.getItem('token');
      try {
        const response = await axios.post('http://localhost:8082/api/trading/cart/add', null, {
          params: {
            username: username.value,
            token: token,
            productId: productID,
            storeName: store.value.name,
            quantity: quantity,
            price: price
          }
        });
        toast.add({severity: 'success', summary: 'Success', detail: response.data, life: 3000});
      } catch (err) {
        toast.add({severity: 'error', summary: 'Error', detail: err.message, life: 3000});
      }
    };

    const placeBid = async (productID, price) => {
      const token = localStorage.getItem('token');
      try {
        const response = await axios.post('http://localhost:8082/api/trading/store/place-bid', null, {
          params: {
            username: username.value,
            token: token,
            storeName: store.value.name,
            productID: productID,
            price: price
          }
        });
        toast.add({severity: 'success', summary: 'Success', detail: response.data, life: 3000});
      } catch (err) {
        toast.add({severity: 'error', summary: 'Error', detail: err.message, life: 3000});
      }
    };

    const backToStores = () => {
      router.push({name: 'HomePage'});
    };

    return {
      username,
      store,
      productQuantities,
      bidPrices,
      addToCart,
      placeBid,
      backToStores,
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

.store-details {
  padding: 20px;
}

.products-list {
  list-style: none;
  padding: 0;
}

.product-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background-color: #f9f9f9;
}

.product-details {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.price {
  font-weight: bold;
  margin-top: 5px;
}

.sidebar-button {
  width: 100%;
  max-width: 150px;
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
