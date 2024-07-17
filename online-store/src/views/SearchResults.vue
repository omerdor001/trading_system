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
                <button @click="openCounterModdal(product.product_id, product.store_name, bidPrices[index])">Place Bid</button>
            </td>
          </tr>
        </tbody>
      </table>
      
      </div>
      </div>
      <Dialog class="dialog-form" header="Enter Address and Credit Card" v-model="counterOfferModalVisible" :visible="counterOfferModalVisible" :draggable="false" :modal="true" :closable="false">
        <div class="form-group">
            <label for="address">Address</label>
            <InputText v-model="address" id="address" required />
          </div>
          <div class="form-group">
            <label for="cardNumber">Card Number</label>
            <InputText v-model="cardNumber" id="cardNumber" required />
          </div>
          <div class="form-group">
            <label for="month">Month</label>
            <InputText v-model="month" id="month" required />
          </div>
          <div class="form-group">
            <label for="year">Year</label>
            <InputText v-model="year" id="year" required />
          </div>
          <div class="form-group">
            <label for="holder">Holder</label>
            <InputText v-model="holder" id="holder" required />
          </div>
          <div class="form-group">
            <label for="ccv">CCV</label>
            <InputText v-model="ccv" id="ccv" required />
          </div>
          <div class="form-group">
            <label for="id">ID</label>
            <InputText v-model="id" id="id" required />
          </div>
      <div class="button-group">
        <Button label="Submit" class="add-button" icon="pi pi-check" @click="placeBid" />
        <Button label="Cancel" class="add-button" icon="pi pi-times" @click="closeCounterOfferModal" />
      </div>
    </Dialog>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import SiteHeader from '@/components/SiteHeader.vue';
import { useRouter } from 'vue-router';
// import PrimeButton from 'primevue/button';
import axios from 'axios';
import { useToast } from 'primevue/usetoast';
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';


export default defineComponent({
  name: 'SearchResults',
  components: {
    SiteHeader,
    Dialog,
    Button
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
    const counterOfferModalVisible = ref(false);
    const address = ref('');
    const cardNumber = ref('');
    const month = ref('');
    const year = ref('');
    const holder = ref('');
    const ccv = ref('');
    const id = ref('');
    const product_ID = ref(0);
    const product_price = ref(0);
    const product_store = ref('');


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
        toast.add({ severity: 'error', summary: 'error', detail: error.message , life: 3000 });

      }
    };

    const viewOptions = (productId) => {
      router.push({ name: 'ProductDetails', params: { productId } });
    };

    const closeCounterOfferModal = () => {
      counterOfferModalVisible.value = false;
    };

    const addToCart = async (productID, storeName, quantity, price) => {
      try {
        await axios.post('http://localhost:8082/api/trading/cart/add',null, {
          params: {
            username: username,
            token: token,
            productId: productID,
            storeName: storeName,
            quantity : quantity,
            price: price
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: "Add to cart successfully", life: 3000 });
      } catch (err) {
        err.value = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }

    };

    const openCounterModdal = async(productId, storeName, price) => {

      product_ID.value = productId;
      product_store.value = storeName;
      product_price.value = price;
      counterOfferModalVisible.value = true;
    };

    const placeBid = async () => {
      try {
       await axios.post('http://localhost:8082/api/trading/store/place-bid',null, {
          params: {
            userName: username,
            token: token,
            storeName: product_store.value,
            productID: product_ID.value,
            price: product_price.value,
            address: address.value,
            amount: product_price.value,
            currency: "USD",
            cardNumber: cardNumber.value,
            month: month.value,
            year: year.value,
            holder: holder.value,
            ccv: ccv.value,
            id: id.value
          }
        });
        toast.add({ severity: 'success', summary: 'Success', detail: "Place bid successfully", life: 3000 });
        counterOfferModalVisible.value = false;

      } catch (err) {
        err.value = err.response?.data?.message || 'An error occurred';
        toast.add({ severity: 'error', summary: 'Error', detail: err.message, life: 3000 });
      }

    };



    return {
      username,
      token,
      product_ID,
      product_price,
      product_store,
      products,
      address,
      cardNumber,
      month,
      year,
      holder,
      ccv,
      id,
      viewOptions,
      openCounterModdal,
      addToCart,
      placeBid,
      bidPrices,
      quantity,
      closeCounterOfferModal,
      counterOfferModalVisible
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

.form-group {
  margin-bottom: 15px;
  text-align: left;
}
.add-button {
  background-color: #e67e22;
  border: none;
  padding: 10px 20px;
  color: white;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
}

.add-button:hover {
  background-color: #d35400;
}

.form-group label {
  display: block;
  color: #333;
  margin-bottom: 5px;
}

.dialog-form {
  background: white;
  border-radius: 12px;
  width: 90%;
  height: 100%;
  max-width: 500px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  animation: fadeIn 0.3s ease;
}

.button-group {
  display: flex; /* Use flexbox for the button group */
  gap: 10px; /* Space between buttons */
}



</style>
