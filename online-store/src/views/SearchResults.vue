<template>
  <div>
    <SiteHeader :isLoggedIn="true" :username="username" @logout="logout" />
    <div class="main-content">
      <h1>Search Results</h1>
      <div v-if="products.length === 0">
        <p>No products found.</p>
      </div>
      <div v-else>
        <table>
        <thead>
          <tr>
            <th>Product Name</th>
            <th>Description</th>
            <th>Store Name</th>
            <th>Rating</th>
            <th>Category</th>
            <th>KeyWords</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Add To Cart</th>
            <th>Place Bid</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.product_id">
            <td>{{ product.product_name }}</td>
            <td>{{ product.product_description }}</td>
            <td>{{ product.store_name}}</td>
            <td>{{ product.rating}}</td>
            <td>{{ product.category}}</td>
            <td>{{ product.keyWords}}</td>
            <td>{{ product.product_price}}</td>
            <td>{{ product.product_quantity }}</td>
            <td>
              <input type="number" v-model="quantity[index]" placeholder="Enter Quantity" />
              <button @click="addToCart(product.product_id, product.store_name, quantity[index], product.product_price)">Add To Cart</button>
            </td>
            <td>
              <input type="number" v-model="bidPrices[index]" placeholder="Enter bid" />
                <button @click="placeBid(product.product_id, product.store_name, bidPrices[index])">Place Bid</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      </div>
      </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
// import PrimeButton from 'primevue/button';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';


export default defineComponent({
  name: 'SearchResults',
  components: {
    SiteHeader,
    // PrimeButton
  },
  setup() {
    const router = useRouter();
    const username = localStorage.getItem('username');
    const token = localStorage.getItem('token');
    const toast = useToast();
    const bidPrices = ref([]);
    const quantity = ref([]);
    const products = ref([]);

    onMounted(() => {
      const filters = router.currentRoute.value.query;
      fetchProducts(filters);
    });

    const fetchProducts = async (filters) => {

      try {

        const categoriesString = filters.categories.length > 0 ? filters.categories.join(',') : '';


        const response = await axios.get('http://localhost:8082/api/trading/store/searchProducts', { 
          params: {
            userName : username,
            token : token,
            keyWord : filters.query,
            minPrice : filters.priceMin,
            maxPrice : filters.priceMax,
            categories : categoriesString,
            rating : filters.rating
          } });
          products.value = response.data
        bidPrices.value = products.value.map(() => 0);
        quantity.value = products.value.map(() => 0);

      } catch (error) {
        console.error('Error fetching products:', error);
        toast.add({ severity: 'error', summary: 'error', detail: filters.query, life: 3000 });

      }
    };

    const viewOptions = (productId) => {
      router.push({ name: 'ProductDetails', params: { productId } });
    };

    const addToCart = async (productID, storeName, quantity, price) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/cart/add',null, {
          params: {
            username: username,
            token: token,
            productId: productID,
            storeName: storeName,
            quantity : quantity,
            price: price
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: response.data, life: 3000 });
      } catch (err) {
        err.value = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }

    };

    const placeBid = async (productID, storeName, price) => {
      try {
        const response = await axios.post('http://localhost:8082/api/trading/store/place-bid',null, {
          params: {
            userName: username,
            token: token,
            storeName: storeName,
            productID: productID,
            price: price,
            address: "1234 Main Street, Springfield, IL, 62704-1234",
            amount: price,
            currency: "USD",
            cardNumber: "4111111111111111",
            month: "12",
            year: "2025",
            holder: "John Doe",
            ccv: "123",
            id:"209507664"
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: response.data, life: 3000 });
      } catch (err) {
        err.value = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }

    };

    return {
      username,
      token,
      products,
      viewOptions,
      addToCart,
      placeBid,
      bidPrices,
      quantity
    };
  }
});
</script>

<style scoped>
.main-content {
  padding: 20px;
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

.product-image {
  width: 150px;
  height: 150px;
  margin-right: 10px;
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
